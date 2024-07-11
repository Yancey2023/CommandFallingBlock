package yancey.commandfallingblock.entity;

import com.mojang.logging.LogUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.slf4j.Logger;
import yancey.commandfallingblock.CommandFallingBlock;
import yancey.commandfallingblock.data.DataBlock;
import yancey.commandfallingblock.data.DataFallingBlock;
import yancey.commandfallingblock.mixin.FallingBlockEntityAccessor;
import yancey.commandfallingblock.network.SummonFallingBlockPayloadS2C;

public class EntityBetterFallingBlock extends Entity {

    public static final EntityType<EntityBetterFallingBlock> BETTER_FALLING_BLOCK = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(CommandFallingBlock.MOD_ID, "better_falling_block"),
            EntityType.Builder.create((EntityType.EntityFactory<EntityBetterFallingBlock>) EntityBetterFallingBlock::new, SpawnGroup.MISC).dimensions(0.98f, 0.98f).maxTrackingRange(10).trackingTickInterval(20).build()
    );

    private static final Logger LOGGER = LogUtils.getLogger();

    public BlockPos blockPosEnd;
    public DataBlock dataBlock;
    private int timeFalling = 0;
    public int tickMove, age;
    public BlockEntity blockEntity = null;
    private int prepareDied = -1;
    private static final TrackedData<BlockPos> BLOCK_POS = DataTracker.registerData(EntityBetterFallingBlock.class, TrackedDataHandlerRegistry.BLOCK_POS);

    public EntityBetterFallingBlock(EntityType<EntityBetterFallingBlock> entityEntityType, World world) {
        super(entityEntityType, world);
    }

    public EntityBetterFallingBlock(World world, BlockPos blockPosEnd, Vec3d pos, Vec3d motion, DataBlock dataBlock, boolean hasNoGravity, int tickMove, int age) {
        super(BETTER_FALLING_BLOCK, world);
        this.dataBlock = dataBlock;
        intersectionChecked = true;
        setPos(pos.x, pos.y, pos.z);
        setVelocity(motion);
        prevX = pos.x;
        prevY = pos.y;
        prevZ = pos.z;
        setFallingBlockPos(blockPosEnd);
        this.tickMove = tickMove;
        this.age = age;
        setNoGravity(hasNoGravity);
        if (timeFalling >= 0) {
            noClip = true;
        }
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    public void setFallingBlockPos(BlockPos blockPos) {
        blockPosEnd = blockPos;
        if (blockPos == null) {
            dataTracker.set(BLOCK_POS, getBlockPos());
        } else {
            dataTracker.set(BLOCK_POS, blockPos);
        }
    }

    public BlockPos getFallingBlockPos() {
        return this.dataTracker.get(BLOCK_POS);
    }

    @Override
    protected MoveEffect getMoveEffect() {
        return MoveEffect.NONE;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(BLOCK_POS, BlockPos.ORIGIN);
    }

    @Override
    public boolean canHit() {
        return !this.isRemoved();
    }

    @Override
    public void tick() {
        if (prepareDied == 0) {
            discard();
            return;
        } else if (prepareDied > 0) {
            prepareDied--;
            return;
        }
        if (dataBlock.blockState.isAir()) {
            discard();
            return;
        }
        World world = getWorld();
        timeFalling++;
        if (!world.isClient && timeFalling >= age && age >= 0) {
            discard();
            return;
        }
        if (timeFalling >= tickMove + 1 && tickMove >= 0) {
            setVelocity(Vec3d.ZERO);
            setNoGravity(true);
            if (!world.isClient && age < 0) {
                dataBlock.run((ServerWorld) world, blockPosEnd, false, false);
                onLand(dataBlock.blockState.getBlock(), blockPosEnd);
                prepareDied = 1;
            }
            return;
        }
        if (!hasNoGravity()) {
            setVelocity(getVelocity().add(0, -0.04, 0));
        }
        move(MovementType.SELF, getVelocity());
        if (tickMove < 0 && !world.isClient) {
            BlockHitResult blockHitResult;
            BlockPos blockPos = DataFallingBlock.floorPos(getPos());
            boolean isConcretePowder = dataBlock.blockState.getBlock() instanceof ConcretePowderBlock;
            boolean isConcretePowderInWater = isConcretePowder && world.getFluidState(blockPos).isIn(FluidTags.WATER);
            if (isConcretePowder && getVelocity().lengthSquared() > 1 && (blockHitResult = world.raycast(new RaycastContext(new Vec3d(prevX, prevY, prevZ), getPos(), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.SOURCE_ONLY, this))).getType() != HitResult.Type.MISS && world.getFluidState(blockHitResult.getBlockPos()).isIn(FluidTags.WATER)) {
                blockPos = blockHitResult.getBlockPos();
                isConcretePowderInWater = true;
            }
            if (isOnGround() || isConcretePowderInWater) {
                setVelocity(Vec3d.ZERO);
                prepareDied = 1;
                dataBlock.run((ServerWorld) world, blockPos, false, false);
                onLand(dataBlock.blockState.getBlock(), blockPos);
            }
        }
        if (!hasNoGravity()) {
            setVelocity(getVelocity().multiply(0.98));
        }
    }

    public void onLand(Block block, BlockPos pos) {
        if (block instanceof LandingBlock landingBlock) {
            landingBlock.onLanding(getWorld(), pos, dataBlock.blockState, getWorld().getBlockState(getBlockPos()), getFallingBlockEntity());
        }
    }

    public FallingBlockEntity getFallingBlockEntity() {
        FallingBlockEntity entity = new FallingBlockEntity(EntityType.FALLING_BLOCK, getWorld());
        ((FallingBlockEntityAccessor) entity).setBlock(dataBlock.blockState);
        entity.setPosition(getPos());
        entity.setSilent(isSilent());
        entity.setFallingBlockPos(getFallingBlockPos());
        entity.setVelocity(getVelocity());
        entity.setNoGravity(hasNoGravity());
        entity.age = age;
        entity.blockEntityData = dataBlock.nbtCompound;
        entity.dropItem = false;
        entity.noClip = noClip;
        return entity;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbtCompound) {
        nbtCompound.put("DataBlock", dataBlock.writeToNBT());
        nbtCompound.putInt("Time", timeFalling);
        nbtCompound.putInt("TickMove", tickMove);
        nbtCompound.putInt("Age", age);
        nbtCompound.putInt("PrepareDied", prepareDied);
        nbtCompound.put("BlockPosEnd", NbtHelper.fromBlockPos(getFallingBlockPos()));
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbtCompound) {
        dataBlock = new DataBlock(nbtCompound.getCompound("DataBlock"));
        timeFalling = nbtCompound.getInt("Time");
        tickMove = nbtCompound.getInt("TickMove");
        age = nbtCompound.getInt("Age");
        prepareDied = nbtCompound.getInt("PrepareDied");
        setFallingBlockPos(NbtHelper.toBlockPos(nbtCompound, "BlockPosEnd").orElse(BlockPos.ORIGIN));
        if (tickMove >= 0) {
            noClip = true;
        }
        Block block = dataBlock.blockState.getBlock();
        if (block instanceof BlockEntityProvider) {
            blockEntity = ((BlockEntityProvider) block).createBlockEntity(getFallingBlockPos(), dataBlock.blockState);
            if (dataBlock.nbtCompound != null && blockEntity != null) {
                blockEntity.read(dataBlock.nbtCompound, getRegistryManager());
            }
        }
    }

    @Override
    public boolean doesRenderOnFire() {
        return false;
    }

    @Override
    @SuppressWarnings("SpellCheckingInspection")
    public void populateCrashReport(CrashReportSection section) {
        super.populateCrashReport(section);
        section.add("Immitating BlockState", dataBlock.blockState.toString());
    }

    @Override
    @SuppressWarnings("SpellCheckingInspection")
    protected Text getDefaultName() {
        return Text.translatable("entity.commandfallingblock.better_falling_block_type", dataBlock.blockState.getBlock().getName());
    }

    @Override
    public boolean entityDataRequiresOperator() {
        return true;
    }

    public void onSpawnPacket(SummonFallingBlockPayloadS2C payload) {
        getTrackedPosition().setPos(payload.pos());
        refreshPositionAfterTeleport(payload.pos());
        setPosition(payload.pos());
        setId(payload.id());
        setUuid(payload.uuid());
        setVelocity(payload.velocity());
        dataBlock = payload.dataBlock();
        tickMove = payload.tickMove();
        age = -1;
        intersectionChecked = true;
        setFallingBlockPos(payload.blockPosEnd());
        setNoGravity(payload.hasNoGravity());
        if (tickMove >= 0) {
            noClip = true;
        }
        if (dataBlock.nbtCompound != null &&
                dataBlock.blockState.getRenderType() != BlockRenderType.MODEL &&
                dataBlock.blockState.getBlock() instanceof BlockEntityProvider blockEntityProvider
        ) {
            blockEntity = blockEntityProvider.createBlockEntity(getFallingBlockPos(), dataBlock.blockState);
            if (blockEntity != null) {
                try {
                    blockEntity.read(dataBlock.nbtCompound, getRegistryManager());
                } catch (Exception e) {
                    LOGGER.warn("Failed to load block entity from falling block", e);
                }
            }
        }
    }
}
