package yancey.commandfallingblock.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.ConcretePowderBlock;
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
import yancey.commandfallingblock.data.DataBlock;
import yancey.commandfallingblock.data.DataFallingBlock;
import yancey.commandfallingblock.network.SummonFallingBlockPacket;

public class EntityBetterFallingBlock extends Entity {

    public static final EntityType<EntityBetterFallingBlock> BETTER_FALLING_BLOCK = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(CommandFallingBlock.MOD_ID, "better_falling_block"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, (EntityType.EntityFactory<EntityBetterFallingBlock>) EntityBetterFallingBlock::new).dimensions(EntityDimensions.fixed(0.98f, 0.98f)).trackRangeChunks(10).trackedUpdateRate(20).build()
    );

    public BlockPos blockPosEnd;
    public DataBlock dataBlock;
    private int timeFalling = 0;
    public int tickMove, age;
    public BlockEntity blockEntity = null;
    private int prepareDied = -1;
    protected static final TrackedData<BlockPos> BLOCK_POS = DataTracker.registerData(EntityBetterFallingBlock.class, TrackedDataHandlerRegistry.BLOCK_POS);

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
        this.age = age + 1;
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
    protected void initDataTracker() {
        this.dataTracker.startTracking(BLOCK_POS, BlockPos.ORIGIN);
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
        if (timeFalling == tickMove + 1) {
            setVelocity(Vec3d.ZERO);
            setNoGravity(true);
            if (!world.isClient && tickMove >= age) {
                dataBlock.run(world, blockPosEnd, false, false);
                prepareDied = 1;
            }
            return;
        }
        if (!world.isClient && timeFalling == age) {
            discard();
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
                dataBlock.run(world, blockPos, false, false);
            }
        }
        if (!hasNoGravity()) {
            setVelocity(getVelocity().multiply(0.98));
        }
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
        nbtCompound.put("BlockPosEnd",NbtHelper.fromBlockPos(getFallingBlockPos()));
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbtCompound) {
        dataBlock = new DataBlock(nbtCompound.getCompound("DataBlock"));
        timeFalling = nbtCompound.getInt("Time");
        tickMove = nbtCompound.getInt("TickMove");
        age = nbtCompound.getInt("Age");
        prepareDied = nbtCompound.getInt("PrepareDied");
        setFallingBlockPos(NbtHelper.toBlockPos(nbtCompound.getCompound("BlockPosEnd")));
    }

    @Override
    public boolean doesRenderOnFire() {
        return false;
    }

    @Override
    public void populateCrashReport(CrashReportSection section) {
        super.populateCrashReport(section);
        section.add("Immitating BlockState", dataBlock.blockState.toString());
    }

    @Override
    protected Text getDefaultName() {
        return Text.translatable("entity.setblockplus.better_falling_block_type", dataBlock.blockState.getBlock().getName());
    }

    @Override
    public boolean entityDataRequiresOperator() {
        return true;
    }

    public void onSpawnPacket(SummonFallingBlockPacket packet) {
        getTrackedPosition().setPos(packet.pos);
        refreshPositionAfterTeleport(packet.pos);
        setPosition(packet.pos);
        setId(packet.id);
        setUuid(packet.uuid);
        setVelocity(packet.velocity);
        dataBlock = packet.dataBlock;
        tickMove = packet.tickMove;
        age = -1;
        intersectionChecked = true;
        setFallingBlockPos(packet.blockPosEnd);
        setNoGravity(packet.hasNoGravity);
        if (tickMove >= 0) {
            noClip = true;
        }
        Block block = dataBlock.blockState.getBlock();
        if (block instanceof BlockEntityProvider) {
            blockEntity = ((BlockEntityProvider) block).createBlockEntity(getFallingBlockPos(), dataBlock.blockState);
            if (dataBlock.nbtCompound != null && blockEntity != null) {
                blockEntity.readNbt(dataBlock.nbtCompound);
            }
        }
    }
}
