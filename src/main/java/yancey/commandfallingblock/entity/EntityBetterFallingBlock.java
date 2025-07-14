package yancey.commandfallingblock.entity;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
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

//#if MC>=12102
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
//#endif

//#if MC>=12106
//#elseif MC>=12105
//$$ import com.mojang.datafixers.util.Pair;
//$$ import com.mojang.serialization.Dynamic;
//$$ import java.util.Optional;
//$$ import net.minecraft.nbt.NbtOps;
//$$ import net.minecraft.nbt.NbtElement;
//#else
//$$ import net.minecraft.nbt.NbtHelper;
//#endif

//#if MC>=12106
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
//#else
//$$ import net.minecraft.nbt.NbtCompound;
//#endif

public class EntityBetterFallingBlock extends Entity {

    //#if MC>=12000
    public static final Identifier ID_BETTER_FALLING_BLOCK = Objects.requireNonNull(Identifier.of(CommandFallingBlock.MOD_ID, "better_falling_block"));
    //#else
    //$$ public static final Identifier ID_BETTER_FALLING_BLOCK = new Identifier(CommandFallingBlock.MOD_ID, "better_falling_block");
    //#endif
    public static final EntityType<EntityBetterFallingBlock> BETTER_FALLING_BLOCK =
            //@formatter:off
            //#if MC>=12000
            EntityType.Builder.create((EntityType.EntityFactory<EntityBetterFallingBlock>) EntityBetterFallingBlock::new, SpawnGroup.MISC)
            //#else
            //$$ FabricEntityTypeBuilder.create(SpawnGroup.MISC, (EntityType.EntityFactory<EntityBetterFallingBlock>) EntityBetterFallingBlock::new)
            //#endif
            //@formatter:on

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

                    //@formatter:off
                    //#if MC>=12102
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, ID_BETTER_FALLING_BLOCK));
                    //#elseif MC>=12000
                    //$$ .build(ID_BETTER_FALLING_BLOCK.toString());
                    //#else
                    //$$ .build();
                    //#endif
                    //@formatter:on

    private static final Logger LOGGER = LogUtils.getLogger();
    //#if MC>=12105
    private static final BlockState DEFAULT_BLOCK_STATE = Blocks.SAND.getDefaultState();
    //#endif

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
        this.lastX = pos.x;
        this.lastY = pos.y;
        this.lastZ = pos.z;
        setFallingBlockPos(blockPosEnd);
        this.tickMove = tickMove;
        this.age = age;
        setNoGravity(hasNoGravity);
        noClip = tickMove >= 0;
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
        if (!world.isClient && timeFalling > age && age > 0) {
            discard();
            return;
        }
        if (timeFalling > tickMove && tickMove >= 0) {
            setVelocity(Vec3d.ZERO);
            setNoGravity(true);
            if (!world.isClient && age <= 0) {
                dataBlock.run(LOGGER, (ServerWorld) world, blockPosEnd, false, false);
                onDestroyedOnLanding(dataBlock.blockState.getBlock(), blockPosEnd);
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
            if (isConcretePowder && getVelocity().lengthSquared() > 1 && (blockHitResult = world.raycast(new RaycastContext(new Vec3d(this.lastX, this.lastY, this.lastZ), getPos(), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.SOURCE_ONLY, this))).getType() != HitResult.Type.MISS && world.getFluidState(blockHitResult.getBlockPos()).isIn(FluidTags.WATER)) {
                blockPos = blockHitResult.getBlockPos();
                isConcretePowderInWater = true;
            }
            if (isOnGround() || isConcretePowderInWater) {
                setVelocity(Vec3d.ZERO);
                prepareDied = 1;
                dataBlock.run(LOGGER, (ServerWorld) world, blockPos, false, false);
                onDestroyedOnLanding(dataBlock.blockState.getBlock(), blockPos);
            }
        }
        if (!hasNoGravity()) {
            setVelocity(getVelocity().multiply(0.98));
        }
    }

    public void onDestroyedOnLanding(Block block, BlockPos pos) {
        //#if MC>=11802
        if (block instanceof Falling) {
            World world = getWorld();
            ((Falling) block).onLanding(world, pos, dataBlock.blockState, world.getBlockState(getBlockPos()), getFallingBlockEntity());
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
        //#if MC>=12105
        ((FallingBlockEntityAccessor) entity).setBlockState(dataBlock.blockState);
        //#else
        //$$ ((FallingBlockEntityAccessor) entity).setBlock(dataBlock.blockState);
        //#endif
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

    @Override
    public boolean handleFallDamage(
            //#if MC>=12105
            double fallDistance,
            //#else
            //$$ float fallDistance,
            //#endif
            float damagePerDistance
            //#if MC>=11802
            ,
            DamageSource damageSource
            //#endif
    ) {
        return false;
    }

    //#if MC>=12102
    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }
    //#endif

    private void onDataBlockUpdate() {
        noClip = tickMove >= 0;
        if (dataBlock.nbtCompound != null &&
                dataBlock.blockState.getRenderType() != BlockRenderType.MODEL &&
                dataBlock.blockState.getBlock() instanceof BlockEntityProvider
        ) {
            //#if MC>=12005
            blockEntity = dataBlock.createBlockEntity(LOGGER, getWorld(), getFallingBlockPos());
            //#elseif MC>=11802
            //$$ blockEntity = dataBlock.createBlockEntity(LOGGER, getFallingBlockPos());
            //#else
            //$$ blockEntity = dataBlock.createBlockEntity(LOGGER, world, getFallingBlockPos());
            //#endif
        }
    }

    //#if MC>=12105&&MC<12106
    //$$ public static Optional<BlockPos> toBlockPos(NbtCompound nbtCompound, String key) {
    //$$     NbtElement optionalBlockPos = nbtCompound.get(key);
    //$$     if (optionalBlockPos == null) {
    //$$         LOGGER.warn("Failed to deserialize block pos");
    //$$         return Optional.empty();
    //$$     }
    //$$     byte optionalBlockPosType = optionalBlockPos.getType();
    //$$     Optional<BlockPos> optionalBlockPos1;
    //$$     if (optionalBlockPosType == NbtElement.INT_ARRAY_TYPE) {
    //$$         optionalBlockPos1 = BlockPos.CODEC.decode(new Dynamic<>(NbtOps.INSTANCE, optionalBlockPos))
    //$$                 .resultOrPartial(error -> LOGGER.warn("Failed to deserialize block pos: {}", error))
    //$$                 .map(Pair::getFirst);
    //$$     } else if (optionalBlockPosType == NbtElement.COMPOUND_TYPE) {
    //$$         NbtCompound nbtCompound1 = (NbtCompound) optionalBlockPos;
    //$$         optionalBlockPos1 = Optional.of(new BlockPos(nbtCompound1.getInt("X", 0), nbtCompound1.getInt("Y", 0), nbtCompound1.getInt("Z", 0)));
    //$$     } else {
    //$$         LOGGER.warn("Failed to deserialize block pos");
    //$$         return Optional.empty();
    //$$     }
    //$$     return optionalBlockPos1;
    //$$ }
    //$$
    //$$ public static NbtElement fromBlockPos(BlockPos blockPos) {
    //$$     return BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, blockPos)
    //$$             .resultOrPartial(error -> LOGGER.warn("Failed to serialize block pos: {}", error))
    //$$             .orElseGet(NbtOps.INSTANCE::emptyList);
    //$$ }
    //#endif

    //#if MC>=12106
    @Override
    protected void writeCustomData(WriteView view) {
        view.put("DataBlock", DataBlock.CODEC, this.dataBlock);
        view.putInt("Time", this.timeFalling);
        view.putInt("TickMove", this.tickMove);
        view.putInt("Age", this.age);
        view.putInt("PrepareDied", this.prepareDied);
        view.put("BlockPosEnd", BlockPos.CODEC, this.getBlockPos());
    }

    @Override
    protected void readCustomData(ReadView view) {
        this.dataBlock = view.read("DataBlock", DataBlock.CODEC).orElse(new DataBlock(DEFAULT_BLOCK_STATE, null));
        this.timeFalling = view.getInt("Time", 0);
        this.tickMove = view.getInt("TickMove", -1);
        this.age = view.getInt("Age", -1);
        this.prepareDied = view.getInt("PrepareDied", -1);
        setFallingBlockPos(view.read("BlockPosEnd", BlockPos.CODEC).orElse(BlockPos.ORIGIN));
        onDataBlockUpdate();
    }
    //#else
    //$$ @Override
    //$$ protected void writeCustomDataToNbt(NbtCompound nbtCompound) {
    //$$     nbtCompound.put("DataBlock", dataBlock.writeToNBT());
    //$$     nbtCompound.putInt("Time", timeFalling);
    //$$     nbtCompound.putInt("TickMove", tickMove);
    //$$     nbtCompound.putInt("Age", age);
    //$$     nbtCompound.putInt("PrepareDied", prepareDied);
    //$$     //#if MC>=12105
    //$$     nbtCompound.put("BlockPosEnd", fromBlockPos(getBlockPos()));
    //$$     //#else
    //$$     //$$ nbtCompound.put("BlockPosEnd", NbtHelper.fromBlockPos(getFallingBlockPos()));
    //$$     //#endif
    //$$ }
    //$$
    //$$ @Override
    //$$ protected void readCustomDataFromNbt(NbtCompound nbtCompound) {
    //$$     //#if MC>=12105
    //$$     dataBlock = nbtCompound.getCompound("DataBlock").map(DataBlock::new).orElse(new DataBlock(DEFAULT_BLOCK_STATE, null));
    //$$     timeFalling = nbtCompound.getInt("Time", 0);
    //$$     tickMove = nbtCompound.getInt("TickMove", -1);
    //$$     age = nbtCompound.getInt("Age", -1);
    //$$     prepareDied = nbtCompound.getInt("PrepareDied", -1);
    //$$     //#else
    //$$     //$$ dataBlock = new DataBlock(nbtCompound.getCompound("DataBlock"));
    //$$     //$$ timeFalling = nbtCompound.getInt("Time");
    //$$     //$$ tickMove = nbtCompound.getInt("TickMove");
    //$$     //$$ age = nbtCompound.getInt("Age");
    //$$     //$$ prepareDied = nbtCompound.getInt("PrepareDied");
    //$$     //#endif
    //$$     //#if MC>=12105
    //$$     setFallingBlockPos(toBlockPos(nbtCompound, "BlockPosEnd").orElse(BlockPos.ORIGIN));
    //$$     //#elseif MC>=12005
    //$$     //$$ setFallingBlockPos(NbtHelper.toBlockPos(nbtCompound, "BlockPosEnd").orElse(BlockPos.ORIGIN));
    //$$     //#else
    //$$     //$$ setFallingBlockPos(NbtHelper.toBlockPos(nbtCompound.getCompound("BlockPosEnd")));
    //$$     //#endif
    //$$     onDataBlockUpdate();
    //$$ }
    //#endif

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

    //#if MC<12104
    //$$ @Override
    //$$ public boolean entityDataRequiresOperator() {
    //$$     return true;
    //$$ }
    //#endif

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
        onDataBlockUpdate();
    }

}
