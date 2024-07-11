package yancey.commandfallingblock.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import yancey.commandfallingblock.data.DataBlock;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;

import java.util.UUID;

import static yancey.commandfallingblock.CommandFallingBlock.MOD_ID;

public record SummonFallingBlockPayloadS2C(int id, UUID uuid, Vec3d pos, Vec3d velocity, DataBlock dataBlock,
                                           boolean hasNoGravity, int tickMove,
                                           BlockPos blockPosEnd) implements CustomPayload {

    public static final CustomPayload.Id<SummonFallingBlockPayloadS2C> ID = new CustomPayload.Id<>(Identifier.of(MOD_ID, "summon_falling_block"));
    public static final PacketCodec<RegistryByteBuf, SummonFallingBlockPayloadS2C> CODEC = new PacketCodec<>() {
        @Override
        public SummonFallingBlockPayloadS2C decode(RegistryByteBuf buf) {
            int id = buf.readInt();
            UUID uuid = buf.readUuid();
            Vec3d pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
            Vec3d velocity = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
            DataBlock dataBlock = DataBlock.createByClientRenderData(buf);
            boolean hasNoGravity = buf.readBoolean();
            int tickMove = buf.readInt();
            BlockPos blockPosEnd;
            if (tickMove >= 0) {
                blockPosEnd = buf.readBlockPos();
            } else {
                blockPosEnd = null;
            }
            return new SummonFallingBlockPayloadS2C(id, uuid, pos, velocity, dataBlock, hasNoGravity, tickMove, blockPosEnd);
        }

        @Override
        public void encode(RegistryByteBuf buf, SummonFallingBlockPayloadS2C value) {
            buf.writeInt(value.id);
            buf.writeUuid(value.uuid);
            buf.writeDouble(value.pos.x);
            buf.writeDouble(value.pos.y);
            buf.writeDouble(value.pos.z);
            buf.writeDouble(value.velocity.x);
            buf.writeDouble(value.velocity.y);
            buf.writeDouble(value.velocity.z);
            value.dataBlock.writeClientRenderData(buf, value.blockPosEnd, buf.getRegistryManager());
            buf.writeBoolean(value.hasNoGravity);
            buf.writeInt(value.tickMove);
            if (value.tickMove >= 0) {
                buf.writeBlockPos(value.blockPosEnd);
            }
        }
    };

    @Override
    public Id<SummonFallingBlockPayloadS2C> getId() {
        return ID;
    }

    public SummonFallingBlockPayloadS2C(EntityBetterFallingBlock entity) {
        this(entity.getId(), entity.getUuid(), entity.getPos(), entity.getVelocity(), entity.dataBlock, entity.hasNoGravity(), entity.tickMove, entity.blockPosEnd);
    }

}
