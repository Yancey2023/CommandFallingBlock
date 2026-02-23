package yancey.commandfallingblock.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;

//#if MC >= 11802
public record DataFallingBlock(BlockPos blockPosEnd, DataBlock dataBlock, Vec3 pos, Vec3 motion, boolean hasGravity,
                               int tickMove, int age) {
//#else
//$$ public class DataFallingBlock {
//#endif

    //#if MC < 11802
    //$$ private final BlockPos blockPosEnd;
    //$$ private final DataBlock dataBlock;
    //$$ private final Vec3 pos;
    //$$ private final Vec3 motion;
    //$$ private final boolean hasGravity;
    //$$ private final int tickMove;
    //$$ private final int age;
    //$$
    //$$ public DataFallingBlock(BlockPos blockPosEnd, DataBlock dataBlock, Vec3 pos, Vec3 motion, boolean hasGravity, int tickMove, int age) {
    //$$     this.blockPosEnd = blockPosEnd;
    //$$     this.dataBlock = dataBlock;
    //$$     this.pos = pos;
    //$$     this.motion = motion;
    //$$     this.hasGravity = hasGravity;
    //$$     this.tickMove = tickMove;
    //$$     this.age = age;
    //$$ }
    //#endif

    public static DataFallingBlock moveFromPosByTick(DataBlock dataBlock, Vec3 posStart, Vec3 motionStart, boolean hasGravity, int tickMove, int age) {
        double x = posStart.x;
        double y = posStart.y;
        double z = posStart.z;
        double motionX = motionStart.x;
        double motionY = motionStart.y;
        double motionZ = motionStart.z;
        for (int i = 0; i < tickMove; i++) {
            if (hasGravity) {
                motionY -= 0.04;
            }
            x += motionX;
            y += motionY;
            z += motionZ;
            motionX *= 0.98;
            motionY *= 0.98;
            motionZ *= 0.98;
        }
        return new DataFallingBlock(floorPos(x, y, z), dataBlock, posStart, motionStart, hasGravity, tickMove, age);
    }

    public static DataFallingBlock moveFromPos(DataBlock dataBlock, Vec3 posStart, Vec3 motionStart, boolean hasGravity, int age) {
        return new DataFallingBlock(null, dataBlock, posStart, motionStart, hasGravity, -1, age);
    }

    public static DataFallingBlock moveToPosByTick(DataBlock dataBlock, Vec3 posEnd, Vec3 motionStart, boolean hasGravity, int tickMove, int age) {
        Vec3 posStart;
        if (hasGravity) {
            double y = posEnd.y;
            double motionY = motionStart.y;
            for (int i = 0; i < tickMove; i++) {
                motionY -= 0.04;
                y -= motionY;
                motionY *= 0.98;
            }
            double a = (1 - Math.pow(0.98, tickMove)) * 50;
            posStart = new Vec3(
                    posEnd.x - motionStart.x * a,
                    y,
                    posEnd.z - motionStart.z * a
            );
        } else {
            posStart = new Vec3(
                    posEnd.x - tickMove * motionStart.x,
                    posEnd.y - tickMove * motionStart.y,
                    posEnd.z - tickMove * motionStart.z
            );
        }
        return new DataFallingBlock(floorPos(posEnd), dataBlock, posStart, motionStart, hasGravity, tickMove, age);
    }

    public static DataFallingBlock moveToPosByYMove(DataBlock dataBlock, Vec3 posEnd, Vec3 motionStart, boolean hasGravity, double yMove, int age) {
        Vec3 posStart;
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
            posStart = new Vec3(
                    posEnd.x - motionStart.x * a,
                    posEnd.y - y,
                    posEnd.z - motionStart.z * a
            );
        } else {
            if (motionStart.y != 0) {
                tick = (int) (yMove / motionStart.y);
                if (tick < 0) {
                    return null;
                }
            } else if (yMove == 0) {
                tick = 0;
            } else {
                return null;
            }
            posStart = new Vec3(
                    posEnd.x - tick * motionStart.x,
                    posEnd.y - yMove,
                    posEnd.z - tick * motionStart.z
            );
        }
        return new DataFallingBlock(floorPos(posEnd), dataBlock, posStart, motionStart, hasGravity, tick, age);
    }

    public static DataFallingBlock moveFromPosToPosByMotionY(DataBlock dataBlock, Vec3 posStart, Vec3 posEnd, double motionYStart, int age) {
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
        Vec3 motionStart = a == 0 ? new Vec3(0, motionYStart, 0) : new Vec3((posEnd.x - posStart.x) / a, motionYStart, (posEnd.z - posStart.z) / a);
        return new DataFallingBlock(floorPos(posEnd), dataBlock, new Vec3(posStart.x, posEnd.y - y, posStart.z), motionStart, true, tick, age);
    }

    public static DataFallingBlock moveFromPosToPosByTick(DataBlock dataBlock, Vec3 posStart, Vec3 posEnd, boolean hasGravity, int tickMove, int age) {
        Vec3 motionStart;
        if (hasGravity) {
            double a = 0.02 / (1 - Math.pow(0.98, tickMove));
            motionStart = new Vec3(
                    (posEnd.x - posStart.x) * a,
                    ((posEnd.y - posStart.y) * 0.02 + 0.04 * (tickMove - 1)) / (1 - Math.pow(0.98, tickMove - 1)) - 1.96,
                    (posEnd.z - posStart.z) * a
            );
            double y = posEnd.y;
            double motionY = motionStart.y;
            for (int i = 0; i < tickMove; i++) {
                motionY -= 0.04;
                y -= motionY;
                motionY *= 0.98;
            }
            posStart = new Vec3(posStart.x, y, posStart.z);
        } else {
            motionStart = new Vec3(
                    (posEnd.x - posStart.x) / tickMove,
                    (posEnd.y - posStart.y) / tickMove,
                    (posEnd.z - posStart.z) / tickMove
            );
        }
        return new DataFallingBlock(floorPos(posEnd), dataBlock, posStart, motionStart, hasGravity, tickMove, age);
    }

    public static BlockPos floorPos(Vec3 vec3) {
        return floorPos(vec3.x, vec3.y, vec3.z);
    }

    public static BlockPos floorPos(double x, double y, double z) {
        return new BlockPos(betterFloor(x), betterFloor(y), betterFloor(z));
    }

    public static int betterFloor(double num) {
        return Mth.floor(num + 0.1);
    }

    public void run(ServerLevel level) {
        level.addFreshEntity(new EntityBetterFallingBlock(level, blockPosEnd, pos, motion, dataBlock, !hasGravity, tickMove, age));
    }
}
