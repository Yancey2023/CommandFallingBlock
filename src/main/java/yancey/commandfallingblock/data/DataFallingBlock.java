package yancey.commandfallingblock.data;

import com.mojang.logging.LogUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;

public record DataFallingBlock(DataBlock dataBlock, Vec3d pos, Vec3d motion, boolean hasGravity, int timeFalling) {

    private static final Logger logger = LogUtils.getLogger();

    public void run(World world) {
        logger.warn(toString());
        world.spawnEntity(new EntityBetterFallingBlock(world, pos, motion, dataBlock, !hasGravity, timeFalling));
    }

    public static DataFallingBlock moveFromPosByTick(DataBlock dataBlock, Vec3d posStart, Vec3d motionStart, boolean hasGravity, int tickLiving) {
        return new DataFallingBlock(dataBlock, posStart, motionStart, hasGravity, tickLiving);
    }

    public static DataFallingBlock moveFromBlockPosByTick(DataBlock dataBlock, BlockPos posStart, Vec3d motionStart, boolean hasGravity, int tickLiving) {
        return moveFromPosByTick(dataBlock, Vec3d.ofBottomCenter(posStart), motionStart, hasGravity, tickLiving);
    }

    public static DataFallingBlock moveFromPos(DataBlock dataBlock, Vec3d posStart, Vec3d motionStart, boolean hasGravity) {
        return new DataFallingBlock(dataBlock, posStart, motionStart, hasGravity, -1);
    }

    public static DataFallingBlock moveFromBlockPos(DataBlock dataBlock, BlockPos posStart, Vec3d motionStart, boolean hasGravity) {
        return moveFromPos(dataBlock, Vec3d.ofBottomCenter(posStart), motionStart, hasGravity);
    }

    public static DataFallingBlock moveToPosByTick(DataBlock dataBlock, Vec3d posEnd, Vec3d motionStart, boolean hasGravity, int tickLiving) {
        Vec3d posStart;
        if (hasGravity) {
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
            posStart = new Vec3d(x, y, z);
        } else {
            posStart = new Vec3d(
                    posEnd.x + tickLiving * motionStart.x,
                    posEnd.y + tickLiving * motionStart.y,
                    posEnd.z + tickLiving * motionStart.z
            );
        }
        return new DataFallingBlock(dataBlock, posStart, motionStart, false, tickLiving);
    }

    public static DataFallingBlock moveToBlockPosByTick(DataBlock dataBlock, BlockPos posEnd, Vec3d motionStart, boolean hasGravity, int tickLiving) {
        return moveToPosByTick(dataBlock, Vec3d.ofBottomCenter(posEnd), motionStart, hasGravity, tickLiving);
    }

    public static DataFallingBlock moveToPosByYMove(DataBlock dataBlock, Vec3d posEnd, Vec3d motionStart, boolean hasGravity, double yMove) {
        Vec3d posStart;
        int tick;
        if (hasGravity) {
            double motionX = motionStart.x;
            double motionY = motionStart.y;
            double motionZ = motionStart.z;
            double x = posEnd.x;
            double y = 0;
            double yTemp = y;
            double z = posEnd.z;
            tick = 0;
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
            posStart = new Vec3d(x, posEnd.y - y, z);
        } else {
            tick = (int) (yMove / motionStart.y);
            posStart = new Vec3d(
                    posEnd.x + tick * motionStart.x,
                    posEnd.y + tick * motionStart.y,
                    posEnd.z + tick * motionStart.z
            );
        }
        return new DataFallingBlock(dataBlock, posStart, motionStart, false, tick);
    }

    public static DataFallingBlock moveToBlockPosByYMove(DataBlock dataBlock, BlockPos posEnd, Vec3d motionStart, boolean hasGravity, double yMove) {
        return moveToPosByYMove(dataBlock, Vec3d.ofBottomCenter(posEnd), motionStart, hasGravity, yMove);
    }

    public static DataFallingBlock moveFromPosToPosByMotionY(DataBlock dataBlock, Vec3d posStart, Vec3d posEnd, double motionYStart) {
        int tick = 0;
        double yMove = posEnd.y - posStart.y;
        double motionY = motionYStart;
        double y = 0;
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
        }
        double a = (1 - Math.pow(0.98, tick)) * 50;
        Vec3d motionStart = a == 0 ? new Vec3d(0, motionYStart, 0) : new Vec3d((posEnd.x - posStart.x) / a, motionYStart, (posEnd.z - posStart.z) / a);
        return new DataFallingBlock(dataBlock, posStart, motionStart, false, tick);
    }

    public static DataFallingBlock moveFromBlockPosToBlockPosByMotionY(DataBlock dataBlock, BlockPos posStart, BlockPos posEnd, double motionYStart) {
        return moveFromPosToPosByMotionY(dataBlock, Vec3d.ofBottomCenter(posStart), Vec3d.ofBottomCenter(posEnd), motionYStart);
    }

    public static DataFallingBlock moveFromPosToPosByTick(DataBlock dataBlock, Vec3d posStart, Vec3d posEnd, boolean hasGravity, int tick) {
        Vec3d motionStart;
        if (hasGravity) {
            double a = 0.02 / (1 - Math.pow(0.98, tick));
            motionStart = new Vec3d(
                    (posEnd.x - posStart.x) * a,
                    ((posEnd.y - posStart.y) * 0.02 + 0.04 * tick - 0.04) / (1 - Math.pow(0.98, tick - 1)) - 1.96,
                    (posEnd.z - posStart.z) * a
            );
        } else {
            motionStart = new Vec3d(
                    (posEnd.x - posStart.x) / tick,
                    (posEnd.y - posStart.y) / tick,
                    (posEnd.z - posStart.z) / tick
            );
        }
        return new DataFallingBlock(dataBlock, posStart, motionStart, hasGravity, tick);
    }

    public static DataFallingBlock moveFromBlockPosToBlockPosByTick(DataBlock dataBlock, BlockPos posStart, BlockPos posEnd, boolean hasGravity, int tick) {
        return moveFromPosToPosByTick(dataBlock, Vec3d.ofBottomCenter(posStart), Vec3d.ofBottomCenter(posEnd), hasGravity, tick);
    }

    @Override
    public String toString() {
        return "DataFallingBlock{" +
                ", pos=" + pos +
                ", motion=" + motion +
                ", hasGravity=" + hasGravity +
                ", timeFalling=" + timeFalling +
                '}';
    }
}
