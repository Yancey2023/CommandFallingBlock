package yancey.commandfallingblock.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import yancey.commandfallingblock.data.DataBlock;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;

import java.util.UUID;

public class SummonFallingBlockPacket {

    public final int id;
    public final UUID uuid;
    public final Vec3d pos;
    public final Vec3d velocity;
    public final DataBlock dataBlock;
    public final boolean hasNoGravity;
    public final int tickMove;
    public final BlockPos blockPosEnd;

    public SummonFallingBlockPacket(EntityBetterFallingBlock entity) {
        this(entity.getEntityId(), entity.getUuid(), entity.getPos(), entity.getVelocity(), entity.dataBlock, entity.hasNoGravity(), entity.tickMove, entity.blockPosEnd);
    }

    public SummonFallingBlockPacket(int id, UUID uuid, Vec3d pos, Vec3d velocity, DataBlock dataBlock, boolean hasNoGravity, int tickMove, BlockPos blockPosEnd) {
        this.id = id;
        this.uuid = uuid;
        this.pos = pos;
        this.velocity = velocity;
        this.dataBlock = dataBlock;
        this.hasNoGravity = hasNoGravity;
        this.tickMove = tickMove;
        this.blockPosEnd = blockPosEnd;
    }

    public SummonFallingBlockPacket(PacketByteBuf buf) {
        id = buf.readInt();
        uuid = buf.readUuid();
        pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        velocity = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        dataBlock = DataBlock.createByClientRenderData(buf);
        hasNoGravity = buf.readBoolean();
        tickMove = buf.readInt();
        if (tickMove >= 0) {
            blockPosEnd = buf.readBlockPos();
        } else {
            blockPosEnd = null;
        }
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
        dataBlock.writeClientRenderData(buf, blockPosEnd);
        buf.writeBoolean(hasNoGravity);
        buf.writeInt(tickMove);
        if (tickMove >= 0) {
            buf.writeBlockPos(blockPosEnd);
        }
    }

}
