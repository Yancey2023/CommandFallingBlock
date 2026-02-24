package yancey.commandfallingblock.util;

import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

//#if MC<11802||MC>=12005
import net.minecraft.world.level.Level;
//#endif

//#if MC>=11802
import net.minecraft.world.level.block.LevelEvent;
//#else
//$$ import yancey.commandfallingblock.mixin.BlockEntityAccessor;
//#endif

//#if MC>=12000&&MC<12106
//$$ import net.minecraft.core.registries.BuiltInRegistries;
//#endif

//#if MC>=12000
import net.minecraft.core.HolderLookup;
//#endif

//#if MC>=12005
import net.minecraft.network.RegistryFriendlyByteBuf;
//#endif

//#if MC<12104
//$$ import net.minecraft.world.level.block.RenderShape;
//#endif

//#if MC==12105
//$$ import net.minecraft.world.level.block.Blocks;
//#endif

//#if MC>=12106
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.util.ProblemReporter;

import java.util.Optional;
//#else
//$$ import net.minecraft.nbt.NbtUtils;
//#endif

//#if MC>=11802
public record DataBlock(BlockState blockState, CompoundTag compoundTag) {
//#else
//$$ public class DataBlock {
//#endif

    //#if MC>=12106
    public static final Codec<DataBlock> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BlockState.CODEC.fieldOf("BlockState").forGetter(datablock -> datablock.blockState),
                    CompoundTag.CODEC.optionalFieldOf("Compound").forGetter(datablock -> Optional.ofNullable(datablock.compoundTag))
            ).apply(instance, (blockState, compoundTag) -> new DataBlock(blockState, compoundTag.orElse(null)))
    );
    //#endif

    //#if MC>=12000&&MC<12102
    //$$ public static final HolderLookup.RegistryLookup<Block> registryWrapper = BuiltInRegistries.BLOCK.asLookup();
    //#endif

    //#if MC<11802
    //$$ private final BlockState blockState;
    //$$ private final CompoundTag compoundTag;
    //$$
    //$$ public DataBlock(BlockState blockState, CompoundTag compoundTag) {
    //$$     this.blockState = blockState;
    //$$     this.compoundTag = compoundTag;
    //$$ }
    //$$
    //$$ public BlockState blockState() {
    //$$     return blockState;
    //$$ }
    //$$
    //$$ public CompoundTag compoundTag() {
    //$$     return compoundTag;
    //$$ }
    //#endif

    //#if MC<12106
    //$$ public DataBlock(CompoundTag compoundTag) {
    //$$     this(
    //$$             //#if MC>=12105
    //$$             compoundTag.getCompound("BlockState")
    //$$                     .map(compoundTag1 -> NbtUtils.readBlockState(BuiltInRegistries.BLOCK, compoundTag1))
    //$$                     .orElse(Blocks.AIR.defaultBlockState()),
    //$$             //#elseif MC>=12102
    //$$             //$$ NbtUtils.readBlockState(BuiltInRegistries.BLOCK, compoundTag.getCompound("BlockState")),
    //$$             //#elseif MC>=12000
    //$$             //$$ NbtUtils.readBlockState(registryWrapper, compoundTag.getCompound("BlockState")),
    //$$             //#else
    //$$             //$$ NbtUtils.readBlockState(compoundTag.getCompound("BlockState")),
    //$$             //#endif
    //$$             //#if MC>=12105
    //$$             compoundTag.getCompound("Compound").orElse(null)
    //$$             //#else
    //$$             //$$ compoundTag.contains("Compound") ? compoundTag.getCompound("Compound") : null
    //$$             //#endif
    //$$     );
    //$$ }
    //#endif

    public static DataBlock createByClientRenderData(FriendlyByteBuf buf) {
        BlockState blockState = Block.stateById(buf.readInt());
        CompoundTag compoundTag = null;
        //#if MC>=12104
        boolean hasNbt = buf.readBoolean();
        //#else
        //$$ boolean hasNbt = blockState.getRenderShape() != RenderShape.MODEL && buf.readBoolean();
        //#endif
        if (hasNbt) {
            compoundTag = buf.readNbt();
        }
        return new DataBlock(blockState, compoundTag);
    }


    //#if MC<12106
    //$$ public CompoundTag writeToNBT() {
    //$$     CompoundTag compoundTag = new CompoundTag();
    //$$     compoundTag.put("BlockState", NbtUtils.writeBlockState(blockState));
    //$$     if (this.compoundTag != null) {
    //$$         compoundTag.put("Compound", this.compoundTag);
    //$$     }
    //$$     return compoundTag;
    //$$ }
    //#endif

    /**
     * write data used for render in client
     *
     * @param buf      byte buffer
     * @param blockPos block position
     */
    public void writeClientRenderData(
            Logger logger,
            //#if MC>=12005
            RegistryFriendlyByteBuf buf,
            //#else
            //$$ FriendlyByteBuf buf,
            //#endif
            BlockPos blockPos

    ) {
        buf.writeInt(Block.getId(blockState));
        //#if MC<12104
        //$$ if (blockState.getRenderShape() == RenderShape.MODEL) {
        //$$     return;
        //$$ }
        //#endif
        Block block = blockState.getBlock();
        if (block instanceof EntityBlock) {
            //#if MC>=11802
            BlockEntity blockEntity = ((EntityBlock) block).newBlockEntity(blockPos, blockState);
            //#else
            //$$ BlockEntity blockEntity = ((EntityBlock) block).newBlockEntity(null);
            //#endif
            if (blockEntity == null) {
                buf.writeBoolean(false);
                return;
            }
            //#if MC>=12005
            writeNbtToBlockEntity(logger, buf.registryAccess(), blockEntity);
            //#elseif MC>=11802
            //$$ writeNbtToBlockEntity(logger, blockEntity);
            //#else
            //$$ writeNbtToBlockEntity(logger, blockPos, blockEntity);
            //#endif
            //#if MC>=12005
            CompoundTag updateTag = blockEntity.getUpdateTag(buf.registryAccess());
            //#else
            //$$ CompoundTag updateTag = blockEntity.getUpdateTag();
            //#endif
            buf.writeBoolean(true);
            buf.writeNbt(updateTag);
            return;
        }
        buf.writeBoolean(false);
    }

    public void run(Logger logger, ServerLevel level, BlockPos blockPos, boolean isDestroy, boolean isDropItem) {
        if (level == null || blockPos == null || blockState == null) {
            return;
        }
        BlockState blockStatePre = level.getBlockState(blockPos);
        if (!blockStatePre.isAir()) {
            if (isDestroy && !(blockState.getBlock() instanceof BaseFireBlock)) {
                //#if MC>=11802
                level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, blockPos, Block.getId(blockStatePre));
                //#else
                //$$ level.levelEvent(2001, blockPos, Block.getId(blockStatePre));
                //#endif
            }
            if (isDropItem) {
                Block.dropResources(blockStatePre, level, blockPos, level.getBlockEntity(blockPos));
            }
        }
        if (!level.setBlockAndUpdate(blockPos, blockState) || compoundTag == null) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity == null) {
            return;
        }
        //#if MC>=12005
        writeNbtToBlockEntity(logger, level.registryAccess(), blockEntity);
        //#elseif MC>=11802
        //$$ writeNbtToBlockEntity(logger, blockEntity);
        //#else
        //$$ writeNbtToBlockEntity(logger, blockPos, blockEntity);
        //#endif
        blockEntity.setChanged();
    }

    public BlockEntity newBlockEntity(
            Logger logger,
            //#if MC<11802||MC>=12005
            Level level,
            //#endif
            BlockPos blockPos
    ) {
        if (blockState.getBlock() instanceof EntityBlock) {
            //#if MC>=11802
            BlockEntity blockEntity = ((EntityBlock) blockState.getBlock()).newBlockEntity(blockPos, blockState);
            //#else
            //$$ BlockEntity blockEntity = ((EntityBlock) blockState.getBlock()).newBlockEntity(level);
            //#endif

            if (blockEntity != null) {
                //#if MC>=12005
                writeNbtToBlockEntity(logger, level.registryAccess(), blockEntity);
                //#elseif MC>=11802
                //$$ writeNbtToBlockEntity(logger, blockEntity);
                //#else
                //$$ writeNbtToBlockEntity(logger, blockPos, blockEntity);
                //#endif
            }

            return blockEntity;
        } else {
            return null;
        }
    }

    public void writeNbtToBlockEntity(
            Logger logger,
            //#if MC>=12005
            HolderLookup.Provider wrapperLookup,
            //#endif
            //#if MC<11802
            //$$ BlockPos blockPos,
            //#endif
            @NotNull BlockEntity blockEntity
    ) {
        if (compoundTag == null) {
            return;
        }
        //#if MC>=12106
        blockEntity.loadWithComponents(TagValueInput.create(new ProblemReporter.ScopedCollector(logger), wrapperLookup, compoundTag));
        //#else
        //$$ try {
        //$$     //#if MC>=12005
        //$$     blockEntity.loadWithComponents(compoundTag, wrapperLookup);
        //$$     //#elseif MC>=11802
        //$$     //$$ blockEntity.load(compoundTag);
        //$$     //#else
        //$$     //$$ ((BlockEntityAccessor) blockEntity).setBlockState(blockState);
        //$$     //$$ blockEntity.setLevelAndPosition(null, blockPos);
        //$$     //$$ blockEntity.load(blockState, compoundTag);
        //$$     //#endif
        //$$ } catch (Exception e) {
        //$$     logger.warn("Failed to load block entity", e);
        //$$ }
        //#endif
    }
}
