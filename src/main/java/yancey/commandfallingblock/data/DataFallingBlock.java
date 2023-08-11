package yancey.commandfallingblock.data;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;

public record DataFallingBlock(BlockPos blockPosEnd, DataBlock dataBlock, Vec3d pos, Vec3d motion, boolean hasGravity,
                               int tickMove, int age) {

    public static DataFallingBlock moveFromPosByTick(DataBlock dataBlock, Vec3d posStart, Vec3d motionStart, boolean hasGravity, int tickMove, int age) {
        double x = posStart.x;
        double y = posStart.y;
        double z = posStart.z;
        double motionX = motionStart.x;
        double motionY = motionStart.y;
        double motionZ = motionStart.z;
        for (int i = 0; i < tickMove; i++) {
            motionY -= 0.04;
            x += motionX;
            y += motionY;
            z += motionZ;
            motionX *= 0.98;
            motionY *= 0.98;
            motionZ *= 0.98;
        }
        return new DataFallingBlock(floorPos(x, y, z), dataBlock, posStart, motionStart, hasGravity, tickMove, age);
    }

    public static DataFallingBlock moveFromPos(DataBlock dataBlock, Vec3d posStart, Vec3d motionStart, boolean hasGravity, int age) {
        return new DataFallingBlock(null, dataBlock, posStart, motionStart, hasGravity, -1, age);
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
        return new DataFallingBlock(floorPos(posEnd), dataBlock, posStart, motionStart, hasGravity, tickMove, age);
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
            if(motionStart.y != 0){
                tick = (int) (yMove / motionStart.y);
                if(tick < 0){
                    return null;
                }
            }else if(yMove == 0){
                tick = 0;
            }else{
                return null;
            }
            posStart = new Vec3d(
                    posEnd.x - tick * motionStart.x,
                    posEnd.y - yMove,
                    posEnd.z - tick * motionStart.z
            );
        }
        return new DataFallingBlock(floorPos(posEnd), dataBlock, posStart, motionStart, hasGravity, tick, age);
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
        return new DataFallingBlock(floorPos(posEnd), dataBlock, new Vec3d(posStart.x, posEnd.y - y, posStart.z), motionStart, true, tick, age);
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
            double y = posEnd.y;
            double motionY = motionStart.y;
            for (int i = 0; i < tickMove; i++) {
                motionY -= 0.04;
                y -= motionY;
                motionY *= 0.98;
            }
            posStart = new Vec3d(posStart.x, y, posStart.z);
        } else {
            motionStart = new Vec3d(
                    (posEnd.x - posStart.x) / tickMove,
                    (posEnd.y - posStart.y) / tickMove,
                    (posEnd.z - posStart.z) / tickMove
            );
        }
        return new DataFallingBlock(floorPos(posEnd), dataBlock, posStart, motionStart, hasGravity, tickMove, age);
    }

    public static BlockPos floorPos(Vec3d vec3d) {
        return floorPos(vec3d.x, vec3d.y, vec3d.z);
    }

    public static BlockPos floorPos(double x, double y, double z) {
        return new BlockPos(betterFloor(x), betterFloor(y), betterFloor(z));
    }

    public static int betterFloor(double num) {
        return MathHelper.floor(num + 0.1);
    }

    public void run(ServerWorld world) {
        world.spawnEntity(new EntityBetterFallingBlock(world, blockPosEnd, pos, motion, dataBlock, !hasGravity, tickMove, age));
    }
}
