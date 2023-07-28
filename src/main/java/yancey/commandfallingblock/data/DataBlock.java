package yancey.commandfallingblock.data;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DataBlock {

    public static final RegistryWrapper.Impl<Block> registryWrapper = Registries.BLOCK.getReadOnlyWrapper();
    public BlockState blockState;
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

    public DataBlock(PacketByteBuf packetByteBuf) {
        blockState = Block.getStateFromRawId(packetByteBuf.readInt());
        nbtCompound = packetByteBuf.readNbt();
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
                world.syncWorldEvent(2001, blockPos, Block.getRawIdFromState(blockStatePre));
            }
            if (isDropItem) {
                Block.dropStacks(blockStatePre, world, blockPos, world.getBlockEntity(blockPos));
            }
        }
        if (!world.setBlockState(blockPos, blockState, 3)) {
            return;
        }
        if (nbtCompound != null) {
            world.addBlockEntity(BlockEntity.createFromNbt(blockPos, blockState, nbtCompound));
        }
    }

    public void writeToBuf(PacketByteBuf packetByteBuf) {
        packetByteBuf.writeInt(Block.getRawIdFromState(blockState));
        packetByteBuf.writeNbt(nbtCompound);
    }
}
