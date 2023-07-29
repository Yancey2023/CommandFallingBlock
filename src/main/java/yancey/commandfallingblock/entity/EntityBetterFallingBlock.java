package yancey.commandfallingblock.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import yancey.commandfallingblock.CommandFallingBlock;
import yancey.commandfallingblock.data.DataBlock;
import yancey.commandfallingblock.network.PacketSummonFallingBlock;

import java.util.function.Predicate;

public class EntityBetterFallingBlock extends Entity {

    public static final EntityType<EntityBetterFallingBlock> BETTER_FALLING_BLOCK = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(CommandFallingBlock.MOD_ID, "better_falling_block"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, (EntityType.EntityFactory<EntityBetterFallingBlock>) EntityBetterFallingBlock::new).dimensions(EntityDimensions.fixed(0.98f, 0.98f)).trackRangeChunks(10).trackedUpdateRate(20).build()
    );

    public DataBlock dataBlock;
    public boolean dropItem = true;
    private boolean cancelDrop, isPrepareDied = false;
    private boolean hurtEntities;
    private int fallHurtMax = 40;
    private float fallHurtAmount = 2.0F;
    public int timeFalling;//死亡倒计时,设置为-1变成正常的掉落方块模式
    protected static final TrackedData<BlockPos> BLOCK_POS = DataTracker.registerData(EntityBetterFallingBlock.class, TrackedDataHandlerRegistry.BLOCK_POS);

    public EntityBetterFallingBlock(EntityType<EntityBetterFallingBlock> entityEntityType, World world) {
        super(entityEntityType, world);
        dataBlock = new DataBlock(Blocks.SAND.getDefaultState(), null);
    }

    public EntityBetterFallingBlock(World world, Vec3d pos, Vec3d motion, DataBlock dataBlock, boolean hasNoGravity, int timeFalling) {
        super(BETTER_FALLING_BLOCK, world);
        this.dataBlock = dataBlock;
        intersectionChecked = true;
        setPos(pos.x, pos.y, pos.z);
        setVelocity(motion);
        prevX = pos.x;
        prevY = pos.y;
        prevZ = pos.z;
        setFallingBlockPos(getBlockPos());
        this.timeFalling = timeFalling;
        setNoGravity(hasNoGravity);
        if (timeFalling >= 0) {
            noClip = true;
            dropItem = false;
        }
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    public void setFallingBlockPos(BlockPos pos) {
        this.dataTracker.set(BLOCK_POS, pos);
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
        if (dataBlock.blockState.isAir()) {
            discard();
            return;
        }
        World world = getWorld();
        if (isPrepareDied) {
            discard();
            return;
        }
        if (timeFalling > 0) {
            timeFalling--;
        } else if (timeFalling == 0) {
            if (world.isClient) {
                return;
            }
            isPrepareDied = true;
            setVelocity(Vec3d.ZERO);
            dataBlock.run(world, getRealBlockPos(), false, false);
            return;
        }
        if (!hasNoGravity()) {
            setVelocity(getVelocity().add(0, -0.04, 0));
        }
        move(MovementType.SELF, getVelocity());
        if (timeFalling < 0) {
            if (world.isClient) {
                return;
            }
            BlockHitResult blockHitResult;
            BlockPos blockPos = getRealBlockPos();
            boolean isConcretePowder = dataBlock.blockState.getBlock() instanceof ConcretePowderBlock;
            boolean isConcretePowderInWater = isConcretePowder && world.getFluidState(blockPos).isIn(FluidTags.WATER);
            if (isConcretePowder && getVelocity().lengthSquared() > 1 && (blockHitResult = world.raycast(new RaycastContext(new Vec3d(prevX, prevY, prevZ), getPos(), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.SOURCE_ONLY, this))).getType() != HitResult.Type.MISS && world.getFluidState(blockHitResult.getBlockPos()).isIn(FluidTags.WATER)) {
                blockPos = blockHitResult.getBlockPos();
                isConcretePowderInWater = true;
            }
            if (isOnGround() || isConcretePowderInWater) {
                setVelocity(getVelocity().multiply(0.7, -0.5, 0.7));
                discard();
                dataBlock.run(world, blockPos, false, false);
            }
        }
        if (!hasNoGravity()) {
            setVelocity(getVelocity().multiply(0.98));
        }
    }

    public BlockPos getRealBlockPos() {
        Vec3d pos = getPos();
        return new BlockPos(betterFloor(pos.x), betterFloor(pos.y), betterFloor(pos.z));
    }

    public static int betterFloor(double num) {
        int a = (int) Math.floor(num);
        return num % 1 > 0.9 ? a + 1 : a;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        DamageSource damageSource2;
        if (!hurtEntities) {
            return false;
        }
        int i = MathHelper.ceil(fallDistance - 1.0f);
        if (i < 0) {
            return false;
        }
        Predicate<Entity> predicate = EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.and(EntityPredicates.VALID_LIVING_ENTITY);
        Block block = dataBlock.blockState.getBlock();
        if (block instanceof LandingBlock landingBlock) {
            damageSource2 = landingBlock.getDamageSource(this);
        } else {
            damageSource2 = getDamageSources().fallingBlock(this);
        }
        DamageSource damageSource22 = damageSource2;
        float f = Math.min(MathHelper.floor((float) i * fallHurtAmount), fallHurtMax);
        getWorld().getOtherEntities(this, getBoundingBox(), predicate).forEach(entity -> entity.damage(damageSource22, f));
        boolean bl = dataBlock.blockState.isIn(BlockTags.ANVIL);
        if (bl && f > 0.0f && random.nextFloat() < 0.05f + (float) i * 0.05f) {
            BlockState blockState = AnvilBlock.getLandingState(dataBlock.blockState);
            if (blockState == null) {
                cancelDrop = true;
            } else {
                dataBlock.blockState = blockState;
            }
        }
        return false;
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbtCompound) {
        nbtCompound.put("DataBlock", dataBlock.writeToNBT());
        nbtCompound.putInt("Time", timeFalling);
        nbtCompound.putBoolean("DropItem", dropItem);
        nbtCompound.putBoolean("HurtEntities", hurtEntities);
        nbtCompound.putFloat("FallHurtAmount", fallHurtAmount);
        nbtCompound.putInt("FallHurtMax", fallHurtMax);
        nbtCompound.putBoolean("CancelDrop", cancelDrop);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbtCompound) {
        dataBlock = new DataBlock(nbtCompound.getCompound("DataBlock"));
        timeFalling = nbtCompound.getInt("Time");
        if (nbtCompound.contains("HurtEntities", 99)) {
            hurtEntities = nbtCompound.getBoolean("HurtEntities");
            fallHurtAmount = nbtCompound.getFloat("FallHurtAmount");
            fallHurtMax = nbtCompound.getInt("FallHurtMax");
        } else if (dataBlock.blockState.isIn(BlockTags.ANVIL)) {
            hurtEntities = true;
        }
        if (nbtCompound.contains("DropItem", 99)) {
            dropItem = nbtCompound.getBoolean("DropItem");
        }
        cancelDrop = nbtCompound.getBoolean("CancelDrop");
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

    public BlockState getBlockState() {
        return dataBlock.blockState;
    }

    @Override
    protected Text getDefaultName() {
        return Text.translatable("entity.setblockplus.better_falling_block_type", dataBlock.blockState.getBlock().getName());
    }

    @Override
    public boolean entityDataRequiresOperator() {
        return true;
    }

    public void onSpawnPacket(PacketSummonFallingBlock packet) {
        updateTrackedPosition(packet.x, packet.y, packet.z);
        refreshPositionAfterTeleport(packet.x, packet.y, packet.z);
        setPosition(packet.x, packet.y, packet.z);
        setPitch(packet.pitch);
        setYaw(packet.yaw);
        setId(packet.id);
        setUuid(packet.uuid);
        setVelocity(packet.velocityX, packet.velocityY, packet.velocityZ);
        dataBlock = packet.dataBlock;
        timeFalling = packet.timeFalling;
        intersectionChecked = true;
        setFallingBlockPos(getBlockPos());
        setNoGravity(packet.hasNoGravity);
        if (timeFalling != -1) {
            noClip = true;
            dropItem = false;
        }
    }
}
