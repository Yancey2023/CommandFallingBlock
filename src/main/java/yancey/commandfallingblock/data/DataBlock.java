package yancey.commandfallingblock.data;

import com.mojang.logging.LogUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEvents;
import org.slf4j.Logger;

public class DataBlock {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final RegistryWrapper.Impl<Block> registryWrapper = Registries.BLOCK.getReadOnlyWrapper();
    public final BlockState blockState;
    public final NbtCompound nbtCompound;

    public DataBlock(BlockState blockState, NbtCompound nbtCompound) {
        this.blockState = blockState;
        this.nbtCompound = nbtCompound;
    }

    public DataBlock(NbtCompound nbtCompound) {
        blockState = NbtHelper.toBlockState(registryWrapper, nbtCompound.getCompound("BlockState"));
        if (nbtCompound.contains("Compound")) {
            this.nbtCompound = nbtCompound.getCompound("Compound");
        } else {
            this.nbtCompound = null;
        }
    }

    public static DataBlock createByClientRenderData(PacketByteBuf packetByteBuf) {
        BlockState blockState = Block.getStateFromRawId(packetByteBuf.readInt());
        NbtCompound nbtCompound = null;
        if (blockState.getRenderType() != BlockRenderType.MODEL && packetByteBuf.readBoolean()) {
            nbtCompound = packetByteBuf.readNbt();
        }
        return new DataBlock(blockState, nbtCompound);
    }


    public NbtCompound writeToNBT() {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.put("BlockState", NbtHelper.fromBlockState(blockState));
        if (this.nbtCompound != null) {
            nbtCompound.put("Compound", this.nbtCompound);
        }
        return nbtCompound;
    }

    /**
     * write data used for render in client
     *
     * @param packetByteBuf byte buffer
     * @param blockPos      block position
     */
    public void writeClientRenderData(PacketByteBuf packetByteBuf, BlockPos blockPos) {
        packetByteBuf.writeInt(Block.getRawIdFromState(blockState));
        if (blockState.getRenderType() == BlockRenderType.MODEL) {
            return;
        }
        if (nbtCompound != null && blockState.getBlock() instanceof BlockEntityProvider blockEntityProvider) {
            BlockEntity blockEntity = blockEntityProvider.createBlockEntity(blockPos, blockState);
            if (blockEntity == null) {
                packetByteBuf.writeBoolean(false);
                return;
            }
            try {
                blockEntity.readNbt(nbtCompound);
            } catch (Exception e) {
                LOGGER.warn("Failed to load block entity from falling block", e);
                packetByteBuf.writeBoolean(false);
                return;
            }
            NbtCompound initialChunkDataNbt = blockEntity.toInitialChunkDataNbt();
            if (initialChunkDataNbt == null) {
                packetByteBuf.writeBoolean(false);
                return;
            }
            packetByteBuf.writeBoolean(true);
            packetByteBuf.writeNbt(initialChunkDataNbt);
            return;
        }
        packetByteBuf.writeBoolean(false);
    }

    public void run(ServerWorld world, BlockPos blockPos, boolean isDestroy, boolean isDropItem) {
        if (world == null || blockPos == null || blockState == null) {
            return;
        }
        BlockState blockStatePre = world.getBlockState(blockPos);
        if (!blockStatePre.isAir()) {
            if (isDestroy && !(blockState.getBlock() instanceof AbstractFireBlock)) {
                world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, blockPos, Block.getRawIdFromState(blockStatePre));
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
        try {
            blockEntity.readNbt(nbtCompound);
        } catch (Exception e) {
            LOGGER.warn("Failed to load block entity from falling block", e);
        }
        blockEntity.markDirty();
    }

}
