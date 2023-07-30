package yancey.commandfallingblock.network;

import net.minecraft.block.Block;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import yancey.commandfallingblock.data.DataBlock;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;

import java.util.UUID;

public class PacketSummonFallingBlock {

    public final int id;
    public final UUID uuid;
    public final Vec3d pos;
    public final Vec3d velocity;
    public final DataBlock dataBlock;
    public final boolean hasNoGravity;
    public final int tickMove;
    public final int age;

    public PacketSummonFallingBlock(EntityBetterFallingBlock entity) {
        this(entity.getId(), entity.getUuid(), entity.getPos(), entity.getVelocity(), entity.dataBlock,entity.hasNoGravity(), entity.tickMove,entity.age);
    }

    public PacketSummonFallingBlock(int id, UUID uuid, Vec3d pos, Vec3d velocity, DataBlock dataBlock, boolean hasNoGravity, int tickMove, int age) {
        this.id = id;
        this.uuid = uuid;
        this.pos = pos;
        this.velocity = velocity;
        this.dataBlock = dataBlock;
        this.hasNoGravity = hasNoGravity;
        this.tickMove = tickMove;
        this.age = age;
    }

    public PacketSummonFallingBlock(PacketByteBuf buf) {
        id = buf.readInt();
        uuid = buf.readUuid();
        pos = new Vec3d(buf.readDouble(),buf.readDouble(),buf.readDouble());
        velocity = new Vec3d(buf.readDouble(),buf.readDouble(),buf.readDouble());
        dataBlock = new DataBlock(Block.getStateFromRawId(buf.readInt()),null);
        hasNoGravity = buf.readBoolean();
        tickMove = buf.readInt();
        age = buf.readInt();
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(id);
        buf.writeUuid(uuid);
        buf.writeDouble(pos.x);
        buf.writeDouble(pos.y);
        buf.writeDouble(pos.z);
        buf.writeDouble(velocity.x);
        buf.writeDouble(velocity.y);
        buf.writeDouble(velocity.z);
        buf.writeInt(Block.getRawIdFromState(dataBlock.blockState));
        buf.writeBoolean(hasNoGravity);
        buf.writeInt(tickMove);
        buf.writeInt(age);
    }

}
