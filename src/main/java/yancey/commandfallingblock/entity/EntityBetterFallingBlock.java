package yancey.commandfallingblock.entity;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.tags.FluidTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.CrashReportCategory;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import yancey.commandfallingblock.CommandFallingBlock;
import yancey.commandfallingblock.util.DataBlock;
import yancey.commandfallingblock.util.DataFallingBlock;
import yancey.commandfallingblock.mixin.FallingBlockEntityAccessor;
import yancey.commandfallingblock.network.SummonFallingBlockPayloadS2C;

//#if MC>=11802
import java.util.Objects;
import net.minecraft.world.level.block.Fallable;
//#else
//$$ import net.minecraft.world.level.block.FallingBlock;
//#endif

//#if MC<12000
//$$ import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
//$$ import net.minecraft.world.entity.EntityDimensions;
//#endif

//#if MC<12000
//$$ import net.minecraft.network.chat.TranslatableComponent;
//$$ import net.minecraft.network.protocol.Packet;
//#endif

//#if MC>=11802
import net.minecraft.world.damagesource.DamageSource;
//#endif

//#if MC>=12102
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
//#endif

//#if MC>=12105
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
//#endif

//#if MC>=12106
//#elseif MC>=12105
//$$ import com.mojang.datafixers.util.Pair;
//$$ import com.mojang.serialization.Dynamic;
//$$ import java.util.Optional;
//$$ import net.minecraft.nbt.NbtOps;
//$$ import net.minecraft.nbt.Tag;
//#else
//$$ import net.minecraft.nbt.NbtUtils;
//#endif

//#if MC<12104
//$$ import net.minecraft.world.level.block.RenderShape;
//#endif

//#if MC>=12106
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
//#else
//$$ import net.minecraft.nbt.CompoundTag;
//#endif

//#if MC>=12111
import org.jspecify.annotations.NonNull;
//#else
//$$ import org.jetbrains.annotations.NotNull;
//#endif

public class EntityBetterFallingBlock extends Entity {

    //#if MC>=12100
    public static final Identifier ID_BETTER_FALLING_BLOCK = Objects.requireNonNull(Identifier.fromNamespaceAndPath(CommandFallingBlock.MOD_ID, "better_falling_block"));
    //#else
    //$$ public static final ResourceLocation ID_BETTER_FALLING_BLOCK = new ResourceLocation(CommandFallingBlock.MOD_ID, "better_falling_block");
    //#endif
    public static final EntityType<EntityBetterFallingBlock> BETTER_FALLING_BLOCK =
            //@formatter:off
            //#if MC>=12000
            EntityType.Builder.of((EntityType.EntityFactory<EntityBetterFallingBlock>) EntityBetterFallingBlock::new, MobCategory.MISC)
            //#else
            //$$ FabricEntityTypeBuilder.create(MobCategory.MISC, (EntityType.EntityFactory<EntityBetterFallingBlock>) EntityBetterFallingBlock::new)
            //#endif
            //@formatter:on

                    //#if MC>=12000
                    .sized(0.98f, 0.98f)
                    //#else
                    //$$ .dimensions(EntityDimensions.fixed(0.98f, 0.98f))
                    //#endif

                    //#if MC>=12000
                    .clientTrackingRange(10)
                    //#else
                    //$$ .trackRangeChunks(10)
                    //#endif

                    //#if MC>=12000
                    .updateInterval(20)
                    //#else
                    //$$ .trackedUpdateRate(20)
                    //#endif

                    //@formatter:off
                    //#if MC>=12102
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, ID_BETTER_FALLING_BLOCK));
                    //#elseif MC>=12000
                    //$$ .build(ID_BETTER_FALLING_BLOCK.toString());
                    //#else
                    //$$ .build();
                    //#endif
                    //@formatter:on

    private static final Logger LOGGER = LogUtils.getLogger();
    //#if MC>=12105
    private static final BlockState DEFAULT_BLOCK_STATE = Blocks.SAND.defaultBlockState();
    //#endif

    public BlockPos blockPosEnd;
    public DataBlock dataBlock;
    private int timeFalling = 0;
    public int tickMove, age;
    public BlockEntity blockEntity = null;
    private int prepareDied = -1;
    private static final EntityDataAccessor<BlockPos> BLOCK_POS = SynchedEntityData.defineId(EntityBetterFallingBlock.class, EntityDataSerializers.BLOCK_POS);

    public EntityBetterFallingBlock(EntityType<EntityBetterFallingBlock> entityEntityType, Level level) {
        super(entityEntityType, level);
    }

    public EntityBetterFallingBlock(Level level, BlockPos blockPosEnd, Vec3 pos, Vec3 motion, DataBlock dataBlock, boolean hasNoGravity, int tickMove, int age) {
        super(BETTER_FALLING_BLOCK, level);
        this.dataBlock = dataBlock;
        this.blocksBuilding = true;
        setPos(pos.x, pos.y, pos.z);
        setDeltaMovement(motion);
        this.xo = pos.x;
        this.yo = pos.y;
        this.zo = pos.z;
        setFallingBlockPos(blockPosEnd);
        this.tickMove = tickMove;
        this.age = age;
        setNoGravity(hasNoGravity);
        this.noPhysics = tickMove >= 0;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    public void setFallingBlockPos(BlockPos blockPos) {
        blockPosEnd = blockPos;
        //#if MC>=11802
        this.entityData.set(BLOCK_POS, Objects.requireNonNullElseGet(blockPos, this::blockPosition));
        //#else
        //$$ if (blockPos != null) {
        //$$     this.entityData.set(BLOCK_POS, blockPos);
        //$$ } else {
        //$$     this.entityData.set(BLOCK_POS, blockPosition());
        //$$ }
        //#endif
    }

    public BlockPos getFallingBlockPos() {
        return this.entityData.get(BLOCK_POS);
    }

    //#if MC>=11802
    @Override
    protected
    //#if MC>=12111
    @NonNull
    //#else
    //$$ @NotNull
    //#endif
    MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }
    //#else
    //$$ @Override
    //$$ protected boolean isMovementNoisy() {
    //$$     return false;
    //$$ }
    //#endif

    @Override
    protected void defineSynchedData(
            //#if MC>=12005
            net.minecraft.network.syncher.SynchedEntityData.Builder builder
            //#endif
    ) {
        //#if MC>=12005
        builder.define(BLOCK_POS, BlockPos.ZERO);
        //#else
        //$$ this.entityData.define(BLOCK_POS, BlockPos.ZERO);
        //#endif
    }


    @Override
    public boolean isPickable() {
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
        if (dataBlock.blockState().isAir()) {
            discard();
            return;
        }
        //#if MC>=11802
        Level level = level();
        //#endif
        timeFalling++;
        //#if MC>=12109
        boolean isClientSide = level.isClientSide();
        //#else
        //$$ boolean isClientSide = level.isClientSide;
        //#endif
        if (!isClientSide && timeFalling > age && age > 0) {
            discard();
            return;
        }
        if (timeFalling > tickMove && tickMove >= 0) {
            setDeltaMovement(Vec3.ZERO);
            setNoGravity(true);
            if (!isClientSide && age <= 0) {
                dataBlock.run(LOGGER, (ServerLevel) level, blockPosEnd, false, false);
                callOnLandAfterFall(dataBlock.blockState().getBlock(), blockPosEnd);
                prepareDied = 1;
            }
            return;
        }
        if (!isNoGravity()) {
            setDeltaMovement(getDeltaMovement().add(0, -0.04, 0));
        }
        move(MoverType.SELF, getDeltaMovement());
        if (tickMove < 0 && !isClientSide) {
            BlockHitResult blockHitResult;
            BlockPos blockPos = DataFallingBlock.floorPos(position());
            boolean isConcretePowder = dataBlock.blockState().getBlock() instanceof ConcretePowderBlock;
            boolean isConcretePowderInWater = isConcretePowder && level.getFluidState(blockPos).is(FluidTags.WATER);
            if (isConcretePowder && getDeltaMovement().lengthSqr() > 1 && (blockHitResult = level.clip(new ClipContext(new Vec3(this.xo, this.yo, this.zo), position(), ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, this))).getType() != HitResult.Type.MISS && level.getFluidState(blockHitResult.getBlockPos()).is(FluidTags.WATER)) {
                blockPos = blockHitResult.getBlockPos();
                isConcretePowderInWater = true;
            }
            if (onGround() || isConcretePowderInWater) {
                setDeltaMovement(Vec3.ZERO);
                prepareDied = 1;
                dataBlock.run(LOGGER, (ServerLevel) level, blockPos, false, false);
                callOnLandAfterFall(dataBlock.blockState().getBlock(), blockPos);
            }
        }
        if (!isNoGravity()) {
            setDeltaMovement(getDeltaMovement().scale(0.98));
        }
    }

    public void callOnLandAfterFall(Block block, BlockPos pos) {
        //#if MC>=11802
        if (block instanceof Fallable) {
            Level level = level();
            ((Fallable) block).onLand(level, pos, dataBlock.blockState(), level.getBlockState(blockPosition()), getFallingBlockEntity());
        }
        //#else
        //$$ if (block instanceof FallingBlock) {
        //$$     //#if MC>=11802
        //$$     //$$ Level level = getLevel();
        //$$     //#endif
        //$$     ((FallingBlock) block).onLand(level, pos, dataBlock.blockState(), level.getBlockState(blockPosition()), getFallingBlockEntity());
        //$$ }
        //#endif
    }

    public FallingBlockEntity getFallingBlockEntity() {
        //#if MC>=11802
        Level level = level();
        //#endif
        FallingBlockEntity entity = new FallingBlockEntity(EntityType.FALLING_BLOCK, level);
        ((FallingBlockEntityAccessor) entity).setBlockState(dataBlock.blockState());
        //#if MC>=11802
        entity.setPos(position());
        //#else
        //$$ Vec3 pos = position();
        //$$ entity.setPos(pos.x, pos.y, pos.z);
        //#endif
        entity.setSilent(isSilent());
        entity.setStartPos(getFallingBlockPos());
        entity.setDeltaMovement(getDeltaMovement());
        entity.setNoGravity(isNoGravity());
        entity.tickCount = age;
        entity.blockData = dataBlock.compoundTag();
        entity.dropItem = false;
        entity.noPhysics = this.noPhysics;
        return entity;
    }

    @Override
    public boolean causeFallDamage(
            //#if MC>=12105
            double fallDistance,
            //#else
            //$$ float fallDistance,
            //#endif
            float damagePerDistance
            //#if MC>=11802
            ,
            //#if MC>=12111
            @NonNull
            //#endif
            DamageSource damageSource
            //#endif
    ) {
        return false;
    }

    //#if MC>=12102
    @Override
    public boolean hurtServer(
            //#if MC>=12111
            @NonNull
            //#endif
            ServerLevel level,
            //#if MC>=12111
            @NonNull
            //#endif
            DamageSource source,
            float amount
    ) {
        return false;
    }
    //#endif

    private void onDataBlockUpdate() {
        this.noPhysics = tickMove >= 0;
        if (dataBlock.compoundTag() != null &&
                //#if MC<12104
                //$$ dataBlock.blockState().getRenderShape() != RenderShape.MODEL &&
                //#endif
                dataBlock.blockState().getBlock() instanceof EntityBlock
        ) {
            //#if MC>=12005
            blockEntity = dataBlock.newBlockEntity(LOGGER, level(), getFallingBlockPos());
            //#elseif MC>=11802
            //$$ blockEntity = dataBlock.newBlockEntity(LOGGER, getFallingBlockPos());
            //#else
            //$$ blockEntity = dataBlock.newBlockEntity(LOGGER, level, getFallingBlockPos());
            //#endif
        }
    }

    //#if MC>=12105&&MC<12106
    //$$ public static Optional<BlockPos> readBlockPos(CompoundTag compoundTag, String key) {
    //$$     Tag optionalBlockPos = compoundTag.get(key);
    //$$     if (optionalBlockPos == null) {
    //$$         LOGGER.warn("Failed to deserialize block pos");
    //$$         return Optional.empty();
    //$$     }
    //$$     byte optionalBlockPosId = optionalBlockPos.getId();
    //$$     Optional<BlockPos> optionalBlockPos1;
    //$$     if (optionalBlockPosId == Tag.TAG_INT_ARRAY) {
    //$$         optionalBlockPos1 = BlockPos.CODEC.decode(new Dynamic<>(NbtOps.INSTANCE, optionalBlockPos))
    //$$                 .resultOrPartial(error -> LOGGER.warn("Failed to deserialize block pos: {}", error))
    //$$                 .map(Pair::getFirst);
    //$$     } else if (optionalBlockPosId == Tag.TAG_COMPOUND) {
    //$$         CompoundTag compoundTag1 = (CompoundTag) optionalBlockPos;
    //$$         optionalBlockPos1 = Optional.of(new BlockPos(compoundTag1.getIntOr("X", 0), compoundTag1.getIntOr("Y", 0), compoundTag1.getIntOr("Z", 0)));
    //$$     } else {
    //$$         LOGGER.warn("Failed to deserialize block pos");
    //$$         return Optional.empty();
    //$$     }
    //$$     return optionalBlockPos1;
    //$$ }
    //$$
    //$$ public static Tag writeBlockPos(BlockPos blockPos) {
    //$$     return BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, blockPos)
    //$$             .resultOrPartial(error -> LOGGER.warn("Failed to serialize block pos: {}", error))
    //$$             .orElseGet(NbtOps.INSTANCE::emptyList);
    //$$ }
    //#endif

    //#if MC>=12106
    @Override
    protected void addAdditionalSaveData(ValueOutput view) {
        view.store("DataBlock", DataBlock.CODEC, this.dataBlock);
        view.putInt("Time", this.timeFalling);
        view.putInt("TickMove", this.tickMove);
        view.putInt("Age", this.age);
        view.putInt("PrepareDied", this.prepareDied);
        view.store("BlockPosEnd", BlockPos.CODEC, this.blockPosition());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput view) {
        this.dataBlock = view.read("DataBlock", DataBlock.CODEC).orElse(new DataBlock(DEFAULT_BLOCK_STATE, null));
        this.timeFalling = view.getIntOr("Time", 0);
        this.tickMove = view.getIntOr("TickMove", -1);
        this.age = view.getIntOr("Age", -1);
        this.prepareDied = view.getIntOr("PrepareDied", -1);
        setFallingBlockPos(view.read("BlockPosEnd", BlockPos.CODEC).orElse(BlockPos.ZERO));
        onDataBlockUpdate();
    }
    //#else
    //$$ @Override
    //$$ protected void addAdditionalSaveData(CompoundTag compoundTag) {
    //$$     compoundTag.put("DataBlock", dataBlock.writeToNBT());
    //$$     compoundTag.putInt("Time", timeFalling);
    //$$     compoundTag.putInt("TickMove", tickMove);
    //$$     compoundTag.putInt("Age", age);
    //$$     compoundTag.putInt("PrepareDied", prepareDied);
    //$$     //#if MC>=12105
    //$$     compoundTag.put("BlockPosEnd", writeBlockPos(blockPosition()));
    //$$     //#else
    //$$     //$$ compoundTag.put("BlockPosEnd", NbtUtils.writeBlockPos(getFallingBlockPos()));
    //$$     //#endif
    //$$ }
    //$$
    //$$ @Override
    //$$ protected void readAdditionalSaveData(CompoundTag compoundTag) {
    //$$     //#if MC>=12105
    //$$     dataBlock = compoundTag.getCompound("DataBlock").map(DataBlock::new).orElse(new DataBlock(DEFAULT_BLOCK_STATE, null));
    //$$     timeFalling = compoundTag.getIntOr("Time", 0);
    //$$     tickMove = compoundTag.getIntOr("TickMove", -1);
    //$$     age = compoundTag.getIntOr("Age", -1);
    //$$     prepareDied = compoundTag.getIntOr("PrepareDied", -1);
    //$$     //#else
    //$$     //$$ dataBlock = new DataBlock(compoundTag.getCompound("DataBlock"));
    //$$     //$$ timeFalling = compoundTag.getInt("Time");
    //$$     //$$ tickMove = compoundTag.getInt("TickMove");
    //$$     //$$ age = compoundTag.getInt("Age");
    //$$     //$$ prepareDied = compoundTag.getInt("PrepareDied");
    //$$     //#endif
    //$$     //#if MC>=12105
    //$$     setFallingBlockPos(readBlockPos(compoundTag, "BlockPosEnd").orElse(BlockPos.ZERO));
    //$$     //#elseif MC>=12005
    //$$     //$$ setFallingBlockPos(NbtUtils.readBlockPos(compoundTag, "BlockPosEnd").orElse(BlockPos.ZERO));
    //$$     //#else
    //$$     //$$ setFallingBlockPos(NbtUtils.readBlockPos(compoundTag.getCompound("BlockPosEnd")));
    //$$     //#endif
    //$$     onDataBlockUpdate();
    //$$ }
    //#endif

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public void fillCrashReportCategory(
            //#if MC>=12111
            @NonNull
            //#endif
            CrashReportCategory section) {
        super.fillCrashReportCategory(section);
        section.setDetail("Immitating BlockState", dataBlock.blockState().toString());
    }

    @Override
    protected
    //@formatter:off
    //#if MC>=12111
    @NonNull
    //#else
    //$$ @NotNull
    //#endif
    //@formatter:on
    Component getTypeName() {
        //#if MC>=12000
        return Component.translatable("entity.commandfallingblock.better_falling_block_type", dataBlock.blockState().getBlock().getName());
        //#else
        //$$ return new TranslatableComponent("entity.commandfallingblock.better_falling_block_type", String.valueOf(dataBlock.blockState().getBlock().getName()));
        //#endif
    }

    //#if MC<12104
    //$$ @Override
    //$$ public boolean onlyOpCanSetNbt() {
    //$$     return true;
    //$$ }
    //#endif

    //#if MC<12000
    //$$ @Override
    //$$ @SuppressWarnings("DataFlowIssue")
    //$$ public @NotNull Packet<?> getAddEntityPacket() {
    //$$     return null;
    //$$ }
    //#endif

    public void onSpawnPacket(SummonFallingBlockPayloadS2C payload) {
        //#if MC>=12000
        getPositionCodec().setBase(payload.pos());
        //#else
        //$$ setPacketCoordinates(payload.pos());
        //#endif

        snapTo(payload.pos());

        //#if MC>=12000
        setPos(payload.pos());
        //#else
        //$$ setPos(payload.pos().x, payload.pos().y, payload.pos().z);
        //#endif

        setId(payload.id());
        setUUID(payload.uuid());
        setDeltaMovement(payload.velocity());
        dataBlock = payload.dataBlock();
        tickMove = payload.tickMove();
        age = -1;
        this.blocksBuilding = true;
        setFallingBlockPos(payload.blockPosEnd());
        setNoGravity(payload.hasNoGravity());
        onDataBlockUpdate();
    }

}
