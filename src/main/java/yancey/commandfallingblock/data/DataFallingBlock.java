package yancey.commandfallingblock.data;

import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;

public class DataFallingBlock {

    public final BlockPos blockPosEnd;
    public final DataBlock dataBlock;
    public final DataBlock dataBlockBefore;
    public final double x;
    public final double y;
    public final double z;
    public final double motionX;
    public final double motionY;
    public final double motionZ;
    public final int tickLiving;
    private EntityBetterFallingBlock entityBetterFallingBlock = null;

    public DataFallingBlock(BlockPos blockPosEnd, DataBlock dataBlock, DataBlock dataBlockBefore, double x, double y, double z, double motionX, double motionY, double motionZ, int tickLiving) {
        this.blockPosEnd = blockPosEnd;
        this.dataBlock = dataBlock;
        this.dataBlockBefore = dataBlockBefore;
        this.x = x;
        this.y = y;
        this.z = z;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.tickLiving = tickLiving;
    }

    public DataFallingBlock(NbtCompound compound) {
        blockPosEnd = compound.contains("BlockPosEnd") ? NbtHelper.toBlockPos(compound.getCompound("BlockPosEnd")) : null;
        dataBlock = new DataBlock(compound.getCompound("DataBlock"));
        dataBlockBefore = compound.contains("DataBlockBefore") ? new DataBlock(compound.getCompound("DataBlockBefore")) : null;
        x = compound.getDouble("X");
        y = compound.getDouble("Y");
        z = compound.getDouble("Z");
        motionX = compound.getDouble("MotionX");
        motionY = compound.getDouble("MotionY");
        motionZ = compound.getDouble("MotionZ");
        tickLiving = compound.getInt("TickLiving");
    }

    public void run(World world) {
        entityBetterFallingBlock = EntityBetterFallingBlock.fall(world, x, y, z, motionX, motionY, motionZ, dataBlock, tickLiving);
        world.spawnEntity(entityBetterFallingBlock);
    }

    public static DataFallingBlock moveFromPosByTick(DataBlock dataBlock, DataBlock dataBlockBefore, double xStart, double yStart, double zStart, double motionXStart, double motionYStart, double motionZStart, int tickLiving) {
        double x = xStart;
        double y = yStart;
        double z = zStart;
        double motionX = motionXStart;
        double motionY = motionYStart;
        double motionZ = motionZStart;
        for (int i = 0; i < tickLiving; i++) {
            motionY -= 0.04;
            x += motionX;
            y += motionY;
            z += motionZ;
            motionX *= 0.98;
            motionY *= 0.98;
            motionZ *= 0.98;
        }
        return new DataFallingBlock(BlockPos.ofFloored(x, y, z), dataBlock, dataBlockBefore, xStart, yStart, zStart, motionXStart, motionYStart, motionZStart, tickLiving);
    }

    public static DataFallingBlock moveFromBlockPosByTick(DataBlock dataBlock, DataBlock dataBlockBefore, BlockPos blockPosStart, double motionXStart, double motionYStart, double motionZStart, int tickLiving) {
        return moveFromPosByTick(dataBlock, dataBlockBefore, blockPosStart.getX() + 0.5, blockPosStart.getY(), blockPosStart.getZ() + 0.5, motionXStart, motionYStart, motionZStart, tickLiving);
    }

    public static DataFallingBlock moveFromPos(DataBlock dataBlock, DataBlock dataBlockBefore, double xStart, double yStart, double zStart, double motionXStart, double motionYStart, double motionZStart) {
        return new DataFallingBlock(null, dataBlock, dataBlockBefore, xStart, yStart, zStart, motionXStart, motionYStart, motionZStart, -1);
    }

    public static DataFallingBlock moveFromBlockPos(DataBlock dataBlock, DataBlock dataBlockBefore, BlockPos blockPosStart, double motionXStart, double motionYStart, double motionZStart) {
        return moveFromPos(dataBlock, dataBlockBefore, blockPosStart.getX() + 0.5, blockPosStart.getY(), blockPosStart.getZ() + 0.5, motionXStart, motionYStart, motionZStart);
    }

    public static DataFallingBlock moveToPosByTick(DataBlock dataBlock, DataBlock dataBlockBefore, double xEnd, double yEnd, double zEnd, double motionXStart, double motionYStart, double motionZStart, int tickLiving) {
        double motionX = motionXStart;
        double motionY = motionYStart;
        double motionZ = motionZStart;
        double x = xEnd;
        double y = yEnd;
        double z = zEnd;
        for (int i = 0; i < tickLiving; i++) {
            motionY -= 0.04;
            x -= motionX;
            y -= motionY;
            z -= motionZ;
            motionX *= 0.98;
            motionY *= 0.98;
            motionZ *= 0.98;
        }
        return new DataFallingBlock(BlockPos.ofFloored(xEnd, yEnd, zEnd), dataBlock, dataBlockBefore, x, y, z, motionXStart, motionYStart, motionZStart, tickLiving);
    }

    public static DataFallingBlock moveToBlockPosByTick(DataBlock dataBlock, DataBlock dataBlockBefore, BlockPos blockPosEnd, double motionXStart, double motionYStart, double motionZStart, int tickLiving) {
        return moveToPosByTick(dataBlock, dataBlockBefore, blockPosEnd.getX() + 0.5, blockPosEnd.getY(), blockPosEnd.getZ() + 0.5, motionXStart, motionYStart, motionZStart, tickLiving);
    }

    public static DataFallingBlock moveToPosByYMove(DataBlock dataBlock, DataBlock dataBlockBefore, double xEnd, double yEnd, double zEnd, double motionXStart, double motionYStart, double motionZStart, double yMove) {
        double motionX = motionXStart;
        double motionY = motionYStart;
        double motionZ = motionZStart;
        double x = xEnd;
        double y = 0;
        double yTemp = y;
        double z = zEnd;
        int tick = 0;
        while (true) {
            motionY -= 0.04;
            x -= motionX;
            yTemp += motionY;
            z -= motionZ;
            if (yMove < 0) {
                if (yTemp < yMove) {
                    break;
                }
            } else {
                if (motionY <= 0) {
                    return null;
                } else if (yTemp > yMove) {
                    break;
                }
            }
            y = yTemp;
            motionX *= 0.98;
            motionY *= 0.98;
            motionZ *= 0.98;
            tick++;
        }
        y = yEnd - y;
        return new DataFallingBlock(BlockPos.ofFloored(xEnd, yEnd, zEnd), dataBlock, dataBlockBefore, x, y, z, motionXStart, motionYStart, motionZStart, tick);
    }

    public static DataFallingBlock moveToBlockPosByYMove(DataBlock dataBlock, DataBlock dataBlockBefore, BlockPos blockPosEnd, double motionXStart, double motionYStart, double motionZStart, double yMove) {
        return moveToPosByYMove(dataBlock, dataBlockBefore, blockPosEnd.getX() + 0.5, blockPosEnd.getY(), blockPosEnd.getZ() + 0.5, motionXStart, motionYStart, motionZStart, yMove);
    }

    public static DataFallingBlock moveFromPosToPos(DataBlock dataBlock, DataBlock dataBlockBefore, double xStart, double yStart, double zStart, double xEnd, double yEnd, double zEnd, double motionYStart) {
        int tick = 0;
        double yMove = yEnd - yStart;
        double motionY = motionYStart;
        double y = 0;
        double a = 0;
        while (true) {
            motionY -= 0.04;
            y += motionY;
            if (yMove < 0) {
                if (y < yMove) {
                    break;
                }
            } else {
                if (motionY <= 0) {
                    return null;
                } else if (y > yMove) {
                    break;
                }
            }
            motionY *= 0.98;
            tick++;
            a += Math.pow(0.98, tick);
        }
        double motionXStart, motionZStart;
        if (a == 0) {
            motionXStart = 0;
            motionZStart = 0;
        } else {
            motionXStart = (xEnd - xStart) / a;
            motionZStart = (zEnd - zStart) / a;
        }
        return new DataFallingBlock(BlockPos.ofFloored(xEnd, yEnd, zEnd), dataBlock, dataBlockBefore, xStart, yStart, zStart, motionXStart, motionYStart, motionZStart, tick);
    }

    public static DataFallingBlock moveFromBlockPosToBlockPos(DataBlock dataBlock, DataBlock dataBlockBefore, BlockPos blockPosStart, BlockPos blockPosEnd, double motionYStart) {
        return moveFromPosToPos(dataBlock, dataBlockBefore, blockPosStart.getX() + 0.5, blockPosStart.getY(), blockPosStart.getZ() + 0.5, blockPosEnd.getX() + 0.5, blockPosEnd.getY(), blockPosEnd.getZ() + 0.5, motionYStart);
    }
}
