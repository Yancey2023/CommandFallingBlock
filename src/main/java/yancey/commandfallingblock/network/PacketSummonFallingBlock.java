package yancey.commandfallingblock.network;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;

import java.util.UUID;

public class PacketSummonFallingBlock {

    public final int id;
    public final UUID uuid;
    public final Vec3d pos;
    public final Vec3d velocity;
    public final BlockState blockState;
    public final boolean hasNoGravity;
    public final int tickMove;

    public PacketSummonFallingBlock(EntityBetterFallingBlock entity) {
        this(entity.getId(), entity.getUuid(), entity.getPos(), entity.getVelocity(), entity.dataBlock.blockState, entity.hasNoGravity(), entity.tickMove);
    }

    public PacketSummonFallingBlock(int id, UUID uuid, Vec3d pos, Vec3d velocity, BlockState blockState, boolean hasNoGravity, int tickMove) {
        this.id = id;
        this.uuid = uuid;
        this.pos = pos;
        this.velocity = velocity;
        this.blockState = blockState;
        this.hasNoGravity = hasNoGravity;
        this.tickMove = tickMove;
    }

    public PacketSummonFallingBlock(PacketByteBuf buf) {
        id = buf.readInt();
        uuid = buf.readUuid();
        pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        velocity = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        blockState = Block.getStateFromRawId(buf.readInt());
        hasNoGravity = buf.readBoolean();
        tickMove = buf.readInt();
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
        buf.writeInt(Block.getRawIdFromState(blockState));
        buf.writeBoolean(hasNoGravity);
        buf.writeInt(tickMove);
    }

}
