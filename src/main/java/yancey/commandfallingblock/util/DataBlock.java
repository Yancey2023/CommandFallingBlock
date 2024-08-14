package yancey.commandfallingblock.util;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

//#if MC>=12001
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
//#endif

//#if MC>=11802
import com.mojang.logging.LogUtils;
import net.minecraft.world.WorldEvents;
import org.slf4j.Logger;
//#else
//$$ import org.apache.logging.log4j.LogManager;
//$$ import org.apache.logging.log4j.Logger;
//#endif

public class DataBlock {

    //#if MC>=11802
    private static final Logger LOGGER = LogUtils.getLogger();
    //#else
    //$$ private static final Logger LOGGER = LogManager.getLogger();
    //#endif

    //#if MC>=12001
    public static final RegistryWrapper.Impl<Block> registryWrapper = Registries.BLOCK.getReadOnlyWrapper();
    //#endif
    public final BlockState blockState;
    public final NbtCompound nbtCompound;

    public DataBlock(BlockState blockState, NbtCompound nbtCompound) {
        this.blockState = blockState;
        this.nbtCompound = nbtCompound;
    }

    public DataBlock(NbtCompound nbtCompound) {
        //#if MC>=12001
        blockState = NbtHelper.toBlockState(registryWrapper, nbtCompound.getCompound("BlockState"));
        //#else
        //$$ blockState = NbtHelper.toBlockState(nbtCompound.getCompound("BlockState"));
        //#endif
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
    public void writeClientRenderData(
            //#if MC>=12005
            RegistryWrapper.WrapperLookup registryManager,
            //#endif
            PacketByteBuf packetByteBuf,
            BlockPos blockPos

    ) {
        packetByteBuf.writeInt(Block.getRawIdFromState(blockState));
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
                packetByteBuf.writeBoolean(false);
                return;
            }
            //#if MC>=12005
            writeNbtToBlockEntity(registryManager, blockEntity);
            //#elseif MC>=11802
            //$$ writeNbtToBlockEntity(blockEntity);
            //#else
            //$$ writeNbtToBlockEntity(blockPos, blockEntity);
            //#endif
            //#if MC>=12005
            NbtCompound initialChunkDataNbt = blockEntity.toInitialChunkDataNbt(registryManager);
            //#else
            //$$ NbtCompound initialChunkDataNbt = blockEntity.toInitialChunkDataNbt();
            //#endif
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
                //#if MC>=12005
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
        writeNbtToBlockEntity(world.getRegistryManager(), blockEntity);
        //#elseif MC>=11802
        //$$ writeNbtToBlockEntity(blockEntity);
        //#else
        //$$ writeNbtToBlockEntity(blockPos, blockEntity);
        //#endif
        blockEntity.markDirty();
    }

    public BlockEntity createBlockEntity(
            World world,
            BlockPos blockPos) {
        if (blockState instanceof BlockEntityProvider) {

            //#if MC>=11802
            BlockEntity blockEntity = ((BlockEntityProvider) blockState).createBlockEntity(blockPos, blockState);
            //#else
            //$$ BlockEntity blockEntity = ((BlockEntityProvider) blockState).createBlockEntity(world);
            //#endif

            if (blockEntity != null) {
                //#if MC>=12005
                writeNbtToBlockEntity(world.getRegistryManager(), blockEntity);
                //#elseif MC>=11802
                //$$ writeNbtToBlockEntity(blockEntity);
                //#else
                //$$ writeNbtToBlockEntity(blockPos, blockEntity);
                //#endif
            }

            return blockEntity;
        } else {
            return null;
        }
    }

    public void writeNbtToBlockEntity(
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
        try {
            //#if MC>=12005
            blockEntity.read(nbtCompound, wrapperLookup);
            //#elseif MC>=11802
            //$$ blockEntity.readNbt(nbtCompound);
            //#else
            //$$ blockEntity.setLocation(null, blockPos);
            //$$ blockEntity.fromTag(blockState, nbtCompound);
            //#endif
        } catch (Exception e) {
            LOGGER.warn("Failed to load block entity", e);
        }
    }
}
