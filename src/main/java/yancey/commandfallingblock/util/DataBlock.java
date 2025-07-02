package yancey.commandfallingblock.util;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

//#if MC<11802||MC>=12005
import net.minecraft.world.World;
//#endif

//#if MC>=11802
import net.minecraft.world.WorldEvents;
//#endif

//#if MC>=12000&&MC<12106
//$$ import net.minecraft.registry.Registries;
//#endif

//#if MC>=12000
import net.minecraft.registry.RegistryWrapper;
//#endif

//#if MC>=12005
import net.minecraft.network.RegistryByteBuf;
//#endif

//#if MC>=12106
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.storage.NbtReadView;
import net.minecraft.util.ErrorReporter;
//#else
//$$ import net.minecraft.nbt.NbtHelper;
//#endif

public class DataBlock {

    //#if MC>=12106
    public static final Codec<DataBlock> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BlockState.CODEC.fieldOf("BlockState").forGetter(datablock -> datablock.blockState),
                    NbtCompound.CODEC.fieldOf("Compound").forGetter(datablock -> datablock.nbtCompound)
            ).apply(instance, DataBlock::new)
    );
    //#endif

    //#if MC>=12000&&MC<12102
    //$$ public static final RegistryWrapper.Impl<Block> registryWrapper = Registries.BLOCK.getReadOnlyWrapper();
    //#endif
    public final BlockState blockState;
    public final NbtCompound nbtCompound;

    public DataBlock(BlockState blockState, NbtCompound nbtCompound) {
        this.blockState = blockState;
        this.nbtCompound = nbtCompound;
    }

    //#if MC<12106
    //$$ public DataBlock(NbtCompound nbtCompound) {
    //$$     //#if MC>=12105
    //$$     this.blockState = nbtCompound.getCompound("BlockState")
    //$$             .map(nbtCompound1 -> NbtHelper.toBlockState(Registries.BLOCK, nbtCompound1))
    //$$             .orElse(Blocks.AIR.getDefaultState());
    //$$     //#elseif MC>=12102
    //$$     //$$ blockState = NbtHelper.toBlockState(Registries.BLOCK, nbtCompound.getCompound("BlockState"));
    //$$     //#elseif MC>=12000
    //$$     //$$ blockState = NbtHelper.toBlockState(registryWrapper, nbtCompound.getCompound("BlockState"));
    //$$     //#else
    //$$     //$$ blockState = NbtHelper.toBlockState(nbtCompound.getCompound("BlockState"));
    //$$     //#endif
    //$$     //#if MC>=12105
    //$$     this.nbtCompound = nbtCompound.getCompound("Compound").orElse(null);
    //$$     //#else
    //$$     //$$ if (nbtCompound.contains("Compound")) {
    //$$     //$$     this.nbtCompound = nbtCompound.getCompound("Compound");
    //$$     //$$ } else {
    //$$     //$$     this.nbtCompound = null;
    //$$     //$$ }
    //$$     //#endif
    //$$ }
    //#endif

    public static DataBlock createByClientRenderData(PacketByteBuf packetByteBuf) {
        BlockState blockState = Block.getStateFromRawId(packetByteBuf.readInt());
        NbtCompound nbtCompound = null;
        if (blockState.getRenderType() != BlockRenderType.MODEL && packetByteBuf.readBoolean()) {
            nbtCompound = packetByteBuf.readNbt();
        }
        return new DataBlock(blockState, nbtCompound);
    }


    //#if MC<12106
    //$$ public NbtCompound writeToNBT() {
    //$$     NbtCompound nbtCompound = new NbtCompound();
    //$$     nbtCompound.put("BlockState", NbtHelper.fromBlockState(blockState));
    //$$     if (this.nbtCompound != null) {
    //$$         nbtCompound.put("Compound", this.nbtCompound);
    //$$     }
    //$$     return nbtCompound;
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
            RegistryByteBuf buf,
            //#else
            //$$ PacketByteBuf buf,
            //#endif
            BlockPos blockPos

    ) {
        buf.writeInt(Block.getRawIdFromState(blockState));
        if (blockState.getRenderType() == BlockRenderType.MODEL) {
            return;
        }
        Block block = blockState.getBlock();
        if (nbtCompound != null && block instanceof BlockEntityProvider) {
            //#if MC>=11802
            BlockEntity blockEntity = ((BlockEntityProvider) block).createBlockEntity(blockPos, blockState);
            //#else
            //$$ BlockEntity blockEntity = ((BlockEntityProvider) block).createBlockEntity(null);
            //#endif
            if (blockEntity == null) {
                buf.writeBoolean(false);
                return;
            }
            //#if MC>=12005
            writeNbtToBlockEntity(logger, buf.getRegistryManager(), blockEntity);
            //#elseif MC>=11802
            //$$ writeNbtToBlockEntity(logger, blockEntity);
            //#else
            //$$ writeNbtToBlockEntity(logger, blockPos, blockEntity);
            //#endif
            //#if MC>=12005
            NbtCompound initialChunkDataNbt = blockEntity.toInitialChunkDataNbt(buf.getRegistryManager());
            //#else
            //$$ NbtCompound initialChunkDataNbt = blockEntity.toInitialChunkDataNbt();
            //#endif
            if (initialChunkDataNbt == null) {
                buf.writeBoolean(false);
                return;
            }
            buf.writeBoolean(true);
            buf.writeNbt(initialChunkDataNbt);
            return;
        }
        buf.writeBoolean(false);
    }

    public void run(Logger logger,ServerWorld world, BlockPos blockPos, boolean isDestroy, boolean isDropItem) {
        if (world == null || blockPos == null || blockState == null) {
            return;
        }
        BlockState blockStatePre = world.getBlockState(blockPos);
        if (!blockStatePre.isAir()) {
            if (isDestroy && !(blockState.getBlock() instanceof AbstractFireBlock)) {
                //#if MC>=11802
                world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, blockPos, Block.getRawIdFromState(blockStatePre));
                //#else
                //$$ world.syncWorldEvent(2001, blockPos, Block.getRawIdFromState(blockStatePre));
                //#endif
            }
            if (isDropItem) {
                Block.dropStacks(blockStatePre, world, blockPos, world.getBlockEntity(blockPos));
            }
        }
        if (!world.setBlockState(blockPos, blockState) || nbtCompound == null) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity == null) {
            return;
        }
        //#if MC>=12005
        writeNbtToBlockEntity(logger, world.getRegistryManager(), blockEntity);
        //#elseif MC>=11802
        //$$ writeNbtToBlockEntity(logger, blockEntity);
        //#else
        //$$ writeNbtToBlockEntity(logger, blockPos, blockEntity);
        //#endif
        blockEntity.markDirty();
    }

    public BlockEntity createBlockEntity(
            Logger logger,
            //#if MC<11802||MC>=12005
            World world,
            //#endif
            BlockPos blockPos
    ) {
        if (blockState instanceof BlockEntityProvider) {

            //#if MC>=11802
            BlockEntity blockEntity = ((BlockEntityProvider) blockState).createBlockEntity(blockPos, blockState);
            //#else
            //$$ BlockEntity blockEntity = ((BlockEntityProvider) blockState).createBlockEntity(world);
            //#endif

            if (blockEntity != null) {
                //#if MC>=12005
                writeNbtToBlockEntity(logger, world.getRegistryManager(), blockEntity);
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
            RegistryWrapper.WrapperLookup wrapperLookup,
            //#endif
            //#if MC<11802
            //$$ BlockPos blockPos,
            //#endif
            @NotNull BlockEntity blockEntity
    ) {
        if (nbtCompound == null) {
            return;
        }
        //#if MC>=12106
        blockEntity.read(NbtReadView.create(new ErrorReporter.Logging(logger), wrapperLookup, nbtCompound));
        //#else
        //$$ try {
        //$$     //#if MC>=12005
        //$$     blockEntity.read(nbtCompound, wrapperLookup);
        //$$     //#elseif MC>=11802
        //$$     //$$ blockEntity.readNbt(nbtCompound);
        //$$     //#else
        //$$     //$$ blockEntity.setLocation(null, blockPos);
        //$$     //$$ blockEntity.fromTag(blockState, nbtCompound);
        //$$     //#endif
        //$$ } catch (Exception e) {
        //$$     logger.warn("Failed to load block entity", e);
        //$$ }
        //#endif
    }
}
