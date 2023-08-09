package yancey.commandfallingblock.data;

import com.mojang.logging.LogUtils;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.slf4j.Logger;

public class DataBlock {

    public static Logger LOGGER = LogUtils.getLogger();
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

    public NbtCompound writeToNBT() {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.put("BlockState", NbtHelper.fromBlockState(blockState));
        if (this.nbtCompound != null) {
            nbtCompound.put("Compound", this.nbtCompound);
        }
        return nbtCompound;
    }

    public void run(World world, BlockPos blockPos, boolean isDestroy, boolean isDropItem) {
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
        if (!world.setBlockState(blockPos, blockState)) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity != null && nbtCompound != null) {
            try {
                blockEntity.readNbt(nbtCompound);
            } catch (Exception e) {
                LOGGER.warn("Failed to load block entity from falling block", e);
            }
            blockEntity.markDirty();
        }
    }

}
