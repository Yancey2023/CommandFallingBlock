package yancey.commandfallingblock.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import yancey.commandfallingblock.data.DataBlock;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;

import java.util.UUID;

public class PacketSummonFallingBlock {

    public final int id;
    public final UUID uuid;
    public final double x;
    public final double y;
    public final double z;
    public final double velocityX;
    public final double velocityY;
    public final double velocityZ;
    public final float pitch;
    public final float yaw;
    public final DataBlock dataBlock;
    public final boolean hasNoGravity;
    public final int tickMove;
    public final int age;

    public PacketSummonFallingBlock(EntityBetterFallingBlock entity) {
        this(entity.getId(), entity.getUuid(), entity.getX(), entity.getY(), entity.getZ(), entity.getPitch(), entity.getYaw(), entity.getVelocity(), entity.dataBlock,entity.hasNoGravity(), entity.tickMove,entity.age);
    }

    public PacketSummonFallingBlock(int id, UUID uuid, double x, double y, double z, float pitch, float yaw, Vec3d velocity, DataBlock dataBlock, boolean hasNoGravity, int tickMove, int age) {
        this.id = id;
        this.uuid = uuid;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        velocityX = velocity.x;
        velocityY = velocity.y;
        velocityZ = velocity.z;
        this.dataBlock = dataBlock;
        this.hasNoGravity = hasNoGravity;
        this.tickMove = tickMove;
        this.age = age;
    }

    public PacketSummonFallingBlock(PacketByteBuf buf) {
        id = buf.readInt();
        uuid = buf.readUuid();
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        pitch = buf.readFloat();
        yaw = buf.readFloat();
        velocityX = buf.readDouble();
        velocityY = buf.readDouble();
        velocityZ = buf.readDouble();
        dataBlock = new DataBlock(buf);
        hasNoGravity = buf.readBoolean();
        tickMove = buf.readInt();
        age = buf.readInt();
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(id);
        buf.writeUuid(uuid);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeFloat(pitch);
        buf.writeFloat(yaw);
        buf.writeDouble(velocityX);
        buf.writeDouble(velocityY);
        buf.writeDouble(velocityZ);
        dataBlock.writeToBuf(buf);
        buf.writeBoolean(hasNoGravity);
        buf.writeInt(tickMove);
        buf.writeInt(age);
    }

}
