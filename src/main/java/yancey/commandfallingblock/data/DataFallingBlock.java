package yancey.commandfallingblock.data;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;

public record DataFallingBlock(DataBlock dataBlock, Vec3d pos, Vec3d motion, boolean hasGravity, int tickMove,
                               int age) {

    public void run(World world) {
        world.spawnEntity(new EntityBetterFallingBlock(world, pos, motion, dataBlock, !hasGravity, tickMove, age));
    }

    public static DataFallingBlock moveFromPosByTick(DataBlock dataBlock, Vec3d posStart, Vec3d motionStart, boolean hasGravity, int tickMove, int age) {
        return new DataFallingBlock(dataBlock, posStart, motionStart, hasGravity, tickMove, age);
    }

    public static DataFallingBlock moveFromBlockPosByTick(DataBlock dataBlock, BlockPos posStart, Vec3d motionStart, boolean hasGravity, int tickMove, int age) {
        return moveFromPosByTick(dataBlock, Vec3d.ofBottomCenter(posStart), motionStart, hasGravity, tickMove, age);
    }

    public static DataFallingBlock moveFromPos(DataBlock dataBlock, Vec3d posStart, Vec3d motionStart, boolean hasGravity, int age) {
        return new DataFallingBlock(dataBlock, posStart, motionStart, hasGravity, -1, age);
    }

    public static DataFallingBlock moveFromBlockPos(DataBlock dataBlock, BlockPos posStart, Vec3d motionStart, boolean hasGravity, int age) {
        return moveFromPos(dataBlock, Vec3d.ofBottomCenter(posStart), motionStart, hasGravity, age);
    }

    public static DataFallingBlock moveToPosByTick(DataBlock dataBlock, Vec3d posEnd, Vec3d motionStart, boolean hasGravity, int tickMove, int age) {
        Vec3d posStart;
        if (hasGravity) {
            double y = posEnd.y;
            double motionY = motionStart.y;
            for (int i = 0; i < tickMove; i++) {
                motionY -= 0.04;
                y -= motionY;
                motionY *= 0.98;
            }
            double a = (1 - Math.pow(0.98, tickMove)) * 50;
            posStart = new Vec3d(
                    posEnd.x - motionStart.x * a,
                    y,
                    posEnd.z - motionStart.z * a
            );
        } else {
            posStart = new Vec3d(
                    posEnd.x - tickMove * motionStart.x,
                    posEnd.y - tickMove * motionStart.y,
                    posEnd.z - tickMove * motionStart.z
            );
        }
        return new DataFallingBlock(dataBlock, posStart, motionStart, hasGravity, tickMove, age);
    }

    public static DataFallingBlock moveToBlockPosByTick(DataBlock dataBlock, BlockPos posEnd, Vec3d motionStart, boolean hasGravity, int tickMove, int age) {
        return moveToPosByTick(dataBlock, Vec3d.ofBottomCenter(posEnd), motionStart, hasGravity, tickMove, age);
    }

    public static DataFallingBlock moveToPosByYMove(DataBlock dataBlock, Vec3d posEnd, Vec3d motionStart, boolean hasGravity, double yMove, int age) {
        Vec3d posStart;
        int tick;
        if (hasGravity) {
            double motionY = motionStart.y;
            double y = 0;
            tick = 0;
            while (true) {
                motionY -= 0.04;
                y += motionY;
                tick++;
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
            }
            double a = (1 - Math.pow(0.98, tick)) * 50;
            posStart = new Vec3d(
                    posEnd.x - motionStart.x * a,
                    posEnd.y - y,
                    posEnd.z - motionStart.z * a
            );
        } else {
            tick = (int) (yMove / motionStart.y);
            posStart = new Vec3d(
                    posEnd.x - tick * motionStart.x,
                    posEnd.y - yMove,
                    posEnd.z - tick * motionStart.z
            );
        }
        return new DataFallingBlock(dataBlock, posStart, motionStart, hasGravity, tick, age);
    }

    public static DataFallingBlock moveToBlockPosByYMove(DataBlock dataBlock, BlockPos posEnd, Vec3d motionStart, boolean hasGravity, double yMove, int age) {
        return moveToPosByYMove(dataBlock, Vec3d.ofBottomCenter(posEnd), motionStart, hasGravity, yMove, age);
    }

    public static DataFallingBlock moveFromPosToPosByMotionY(DataBlock dataBlock, Vec3d posStart, Vec3d posEnd, double motionYStart, int age) {
        int tick = 0;
        double yMove = posEnd.y - posStart.y;
        double motionY = motionYStart;
        double y = 0;
        while (true) {
            motionY -= 0.04;
            y += motionY;
            tick++;
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
        }
        double a = (1 - Math.pow(0.98, tick)) * 50;
        Vec3d motionStart = a == 0 ? new Vec3d(0, motionYStart, 0) : new Vec3d((posEnd.x - posStart.x) / a, motionYStart, (posEnd.z - posStart.z) / a);
        return new DataFallingBlock(dataBlock, new Vec3d(posStart.x, posEnd.y - y, posStart.z), motionStart, true, tick, age);
    }

    public static DataFallingBlock moveFromBlockPosToBlockPosByMotionY(DataBlock dataBlock, BlockPos posStart, BlockPos posEnd, double motionYStart, int age) {
        return moveFromPosToPosByMotionY(dataBlock, Vec3d.ofBottomCenter(posStart), Vec3d.ofBottomCenter(posEnd), motionYStart, age);
    }

    public static DataFallingBlock moveFromPosToPosByTick(DataBlock dataBlock, Vec3d posStart, Vec3d posEnd, boolean hasGravity, int tickMove, int age) {
        Vec3d motionStart;
        if (hasGravity) {
            double a = 0.02 / (1 - Math.pow(0.98, tickMove));
            motionStart = new Vec3d(
                    (posEnd.x - posStart.x) * a,
                    ((posEnd.y - posStart.y) * 0.02 + 0.04 * (tickMove - 1)) / (1 - Math.pow(0.98, tickMove - 1)) - 1.96,
                    (posEnd.z - posStart.z) * a
            );
//            double y = posEnd.y;
//            double motionY = motionStart.y;
//            for (int i = 0; i < tickMove; i++) {
//                motionY -= 0.04;
//                y -= motionY;
//                motionY *= 0.98;
//            }
//            posStart = new Vec3d(posStart.x,y,posStart.z);
        } else {
            motionStart = new Vec3d(
                    (posEnd.x - posStart.x) / tickMove,
                    (posEnd.y - posStart.y) / tickMove,
                    (posEnd.z - posStart.z) / tickMove
            );
        }
        return new DataFallingBlock(dataBlock, posStart, motionStart, hasGravity, tickMove, age);
    }

    public static DataFallingBlock moveFromBlockPosToBlockPosByTick(DataBlock dataBlock, BlockPos posStart, BlockPos posEnd, boolean hasGravity, int tickMove, int age) {
        return moveFromPosToPosByTick(dataBlock, Vec3d.ofBottomCenter(posStart), Vec3d.ofBottomCenter(posEnd), hasGravity, tickMove, age);
    }
}
