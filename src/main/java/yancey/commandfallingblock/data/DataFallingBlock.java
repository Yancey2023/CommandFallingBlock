package yancey.commandfallingblock.data;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;

public record DataFallingBlock(DataBlock dataBlock, Vec3d pos, Vec3d motion, int tickLiving) {

    public void run(World world) {
        world.spawnEntity(EntityBetterFallingBlock.fall(world, pos, motion, dataBlock, tickLiving));
    }

    public static DataFallingBlock moveFromPosByTick(DataBlock dataBlock, Vec3d posStart, Vec3d motionStart, int tickLiving) {
        return new DataFallingBlock(dataBlock, posStart, motionStart, tickLiving);
    }

    public static DataFallingBlock moveFromBlockPosByTick(DataBlock dataBlock, BlockPos blockPosStart, Vec3d motionStart, int tickLiving) {
        return moveFromPosByTick(dataBlock, Vec3d.ofBottomCenter(blockPosStart), motionStart, tickLiving);
    }

    public static DataFallingBlock moveFromPos(DataBlock dataBlock, Vec3d posStart, Vec3d motionStart) {
        return new DataFallingBlock(dataBlock, posStart, motionStart, -1);
    }

    public static DataFallingBlock moveFromBlockPos(DataBlock dataBlock, BlockPos blockPosStart, Vec3d motionStart) {
        return moveFromPos(dataBlock, Vec3d.ofBottomCenter(blockPosStart), motionStart);
    }

    public static DataFallingBlock moveToPosByTick(DataBlock dataBlock, Vec3d posEnd, Vec3d motionStart, int tickLiving) {
        double x = posEnd.x;
        double y = posEnd.y;
        double z = posEnd.z;
        double motionX = motionStart.x;
        double motionY = motionStart.y;
        double motionZ = motionStart.z;
        for (int i = 0; i < tickLiving; i++) {
            motionY -= 0.04;
            x -= motionX;
            y -= motionY;
            z -= motionZ;
            motionX *= 0.98;
            motionY *= 0.98;
            motionZ *= 0.98;
        }
        return new DataFallingBlock(dataBlock, new Vec3d(x, y, z), motionStart, tickLiving);
    }

    public static DataFallingBlock moveToBlockPosByTick(DataBlock dataBlock, BlockPos blockPosEnd, Vec3d motionStart, int tickLiving) {
        return moveToPosByTick(dataBlock, Vec3d.ofBottomCenter(blockPosEnd), motionStart, tickLiving);
    }

    public static DataFallingBlock moveToPosByYMove(DataBlock dataBlock, Vec3d posEnd, Vec3d motionStart, double yMove) {
        double motionX = motionStart.x;
        double motionY = motionStart.y;
        double motionZ = motionStart.z;
        double x = posEnd.x;
        double y = 0;
        double yTemp = y;
        double z = posEnd.z;
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
        y = posEnd.y - y;
        return new DataFallingBlock(dataBlock, new Vec3d(x, y, z), motionStart, tick);
    }

    public static DataFallingBlock moveToBlockPosByYMove(DataBlock dataBlock, BlockPos blockPosEnd, Vec3d motionStart, double yMove) {
        return moveToPosByYMove(dataBlock, Vec3d.ofBottomCenter(blockPosEnd), motionStart, yMove);
    }

    public static DataFallingBlock moveFromPosToPos(DataBlock dataBlock, Vec3d posStart, Vec3d posEnd, double motionYStart) {
        int tick = 0;
        double yMove = posEnd.y - posStart.y;
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
        Vec3d motionStart = a == 0 ? new Vec3d(0, motionYStart, 0) : new Vec3d((posEnd.x - posStart.x) / a, motionYStart, (posEnd.z - posStart.z) / a);
        return new DataFallingBlock(dataBlock, posStart, motionStart, tick);
    }

    public static DataFallingBlock moveFromBlockPosToBlockPos(DataBlock dataBlock, BlockPos blockPosStart, BlockPos blockPosEnd, double motionYStart) {
        return moveFromPosToPos(dataBlock, Vec3d.ofBottomCenter(blockPosStart), Vec3d.ofBottomCenter(blockPosEnd), motionYStart);
    }
}
