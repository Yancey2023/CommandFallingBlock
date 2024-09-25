package yancey.commandfallingblock.entity;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
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
import yancey.commandfallingblock.CommandFallingBlock;
import yancey.commandfallingblock.util.DataBlock;
import yancey.commandfallingblock.util.DataFallingBlock;
import yancey.commandfallingblock.mixin.FallingBlockEntityAccessor;
import yancey.commandfallingblock.network.SummonFallingBlockPayloadS2C;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

//#if MC<12000
//$$ import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
//#endif

//#if MC>=12000
import java.util.Objects;
//#else
//$$ import net.minecraft.network.Packet;
//$$ import net.minecraft.text.TranslatableText;
//#endif

//#if MC>=11802
import net.minecraft.entity.damage.DamageSource;
//#endif

public class EntityBetterFallingBlock extends Entity {

    //#if MC>=12000
    public static final Identifier ID_BETTER_FALLING_BLOCK = Objects.requireNonNull(Identifier.of(CommandFallingBlock.MOD_ID, "better_falling_block"));
    //#else
    //$$ public static final Identifier ID_BETTER_FALLING_BLOCK = new Identifier(CommandFallingBlock.MOD_ID, "better_falling_block");
    //#endif
    public static final EntityType<EntityBetterFallingBlock> BETTER_FALLING_BLOCK =
            //#if MC>=12000
            EntityType.Builder.create((EntityType.EntityFactory<EntityBetterFallingBlock>) EntityBetterFallingBlock::new, SpawnGroup.MISC)
                    //#else
                    //$$ FabricEntityTypeBuilder.create(SpawnGroup.MISC, (EntityType.EntityFactory<EntityBetterFallingBlock>) EntityBetterFallingBlock::new)
                    //#endif

                    //#if MC>=12005
                    .dimensions(0.98f, 0.98f)
                    //#elseif MC>=12000
                    //$$ .setDimensions(0.98F, 0.98F)
                    //#else
                    //$$ .dimensions(EntityDimensions.fixed(0.98f, 0.98f))
                    //#endif

                    //#if MC>=12000
                    .maxTrackingRange(10)
                    //#else
                    //$$ .trackRangeChunks(10)
                    //#endif

                    //#if MC>=12000
                    .trackingTickInterval(20)
                    //#else
                    //$$ .trackedUpdateRate(20)
                    //#endif

                    //#if MC>=12000
                    .build(ID_BETTER_FALLING_BLOCK.toString());
                    //#else
                    //$$ .build();
                    //#endif

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
        this.intersectionChecked = true;
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

    //#if MC>=11802
    @Override
    protected MoveEffect getMoveEffect() {
        return MoveEffect.NONE;
    }
    //#else
    //$$ @Override
    //$$ protected boolean canClimb() {
    //$$     return false;
    //$$ }
    //#endif

    @Override
    protected void initDataTracker(
            //#if MC>=12005
            DataTracker.Builder builder
            //#endif
    ) {
        //#if MC>=12005
        builder.add(BLOCK_POS, BlockPos.ORIGIN);
        //#else
        //$$ this.dataTracker.startTracking(BLOCK_POS, BlockPos.ORIGIN);
        //#endif
    }


    @Override
    public boolean canHit() {
        //#if MC>=11802
        return !this.isRemoved();
        //#else
        //$$ return !this.removed;
        //#endif
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
        //#if MC>=11802
        World world = getWorld();
        //#endif
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
        //#if MC>=11802
        if (block instanceof LandingBlock) {
            World world = getWorld();
            ((LandingBlock) block).onLanding(world, pos, dataBlock.blockState, world.getBlockState(getBlockPos()), getFallingBlockEntity());
        }
        //#else
        //$$ if (block instanceof FallingBlock) {
        //$$     //#if MC>=11802
        //$$     //$$ World world = getWorld();
        //$$     //#endif
        //$$     ((FallingBlock) block).onLanding(world, pos, dataBlock.blockState, world.getBlockState(getBlockPos()), getFallingBlockEntity());
        //$$ }
        //#endif
    }

    public FallingBlockEntity getFallingBlockEntity() {
        //#if MC>=11802
        World world = getWorld();
        //#endif
        FallingBlockEntity entity = new FallingBlockEntity(EntityType.FALLING_BLOCK, world);
        ((FallingBlockEntityAccessor) entity).setBlock(dataBlock.blockState);
        //#if MC>=11802
        entity.setPosition(getPos());
        //#else
        //$$ Vec3d pos = getPos();
        //$$ entity.setPosition(pos.x, pos.y, pos.z);
        //#endif
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

    //#if MC>=11802
    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }
    //#else
    //$$ @Override
    //$$ public boolean handleFallDamage(float fallDistance, float damageMultiplier) {
    //$$     return false;
    //$$ }
    //#endif

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
        //#if MC>=12005
        setFallingBlockPos(NbtHelper.toBlockPos(nbtCompound, "BlockPosEnd").orElse(BlockPos.ORIGIN));
        //#else
        //$$ setFallingBlockPos(NbtHelper.toBlockPos(nbtCompound.getCompound("BlockPosEnd")));
        //#endif
        if (tickMove >= 0) {
            noClip = true;
        }
        Block block = dataBlock.blockState.getBlock();
        //#if MC>=11802
        blockEntity = dataBlock.createBlockEntity(getWorld(), getFallingBlockPos());
        //#else
        //$$ blockEntity = dataBlock.createBlockEntity(world, getFallingBlockPos());
        //#endif
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
        //#if MC>=12000
        return Text.translatable("entity.commandfallingblock.better_falling_block_type", dataBlock.blockState.getBlock().getName());
        //#else
        //$$ return new TranslatableText("entity.commandfallingblock.better_falling_block_type", String.valueOf(dataBlock.blockState.getBlock().getName()));
        //#endif
    }

    @Override
    public boolean entityDataRequiresOperator() {
        return true;
    }

    //#if MC<12000
    //$$ @Override
    //$$ public Packet<?> createSpawnPacket() {
    //$$     return null;
    //$$ }
    //#endif

    public void onSpawnPacket(SummonFallingBlockPayloadS2C payload) {

        //#if MC>=12000
        getTrackedPosition().setPos(payload.pos);
        //#else
        //$$ updateTrackedPosition(payload.pos);
        //#endif

        refreshPositionAfterTeleport(payload.pos);

        //#if MC>=12000
        setPosition(payload.pos);
        //#else
        //$$ setPosition(payload.pos.x, payload.pos.y, payload.pos.z);
        //#endif

        setId(payload.id);
        setUuid(payload.uuid);
        setVelocity(payload.velocity);
        dataBlock = payload.dataBlock;
        tickMove = payload.tickMove;
        age = -1;
        this.intersectionChecked = true;
        setFallingBlockPos(payload.blockPosEnd);
        setNoGravity(payload.hasNoGravity);
        if (tickMove >= 0) {
            noClip = true;
        }
        Block block = dataBlock.blockState.getBlock();
        if (dataBlock.nbtCompound != null &&
                dataBlock.blockState.getRenderType() != BlockRenderType.MODEL &&
                block instanceof BlockEntityProvider
        ) {
            //#if MC>=11802
            blockEntity = dataBlock.createBlockEntity(getWorld(), getFallingBlockPos());
            //#else
            //$$ blockEntity = dataBlock.createBlockEntity(world, getFallingBlockPos());
            //#endif
        }
    }

}
