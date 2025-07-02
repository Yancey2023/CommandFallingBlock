package yancey.commandfallingblock.network;

import com.mojang.logging.LogUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import yancey.commandfallingblock.util.DataBlock;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;

import java.util.UUID;

//#if MC>=12000&&MC<12005
//$$ import java.util.Objects;
//#endif

//#if MC>=12005
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
//#endif

import static yancey.commandfallingblock.CommandFallingBlock.MOD_ID;

public class SummonFallingBlockPayloadS2C
        //#if MC>=12005
        implements CustomPayload
        //#endif
{

    private static final Logger LOGGER = LogUtils.getLogger();

    //#if MC>=12005
    public static final CustomPayload.Id<SummonFallingBlockPayloadS2C> ID = new CustomPayload.Id<>(Identifier.of(MOD_ID, "summon_falling_block"));
    public static final PacketCodec<RegistryByteBuf, SummonFallingBlockPayloadS2C> CODEC
            = PacketCodec.of(SummonFallingBlockPayloadS2C::encode, SummonFallingBlockPayloadS2C::decode);
    //#elseif MC>=12000
    //$$ public static final Identifier ID = Objects.requireNonNull(Identifier.of(MOD_ID, "summon_falling_block"));
    //#else
    //$$ public static final Identifier ID = new Identifier(MOD_ID, "summon_falling_block");
    //#endif

    public final int id;
    public final UUID uuid;
    public final Vec3d pos, velocity;
    public final DataBlock dataBlock;
    public final boolean hasNoGravity;
    public final int tickMove;
    public final BlockPos blockPosEnd;

    public SummonFallingBlockPayloadS2C(int id, UUID uuid, Vec3d pos, Vec3d velocity, DataBlock dataBlock, boolean hasNoGravity, int tickMove, BlockPos blockPosEnd) {
        this.id = id;
        this.uuid = uuid;
        this.pos = pos;
        this.velocity = velocity;
        this.dataBlock = dataBlock;
        this.hasNoGravity = hasNoGravity;
        this.tickMove = tickMove;
        this.blockPosEnd = blockPosEnd;
    }

    public SummonFallingBlockPayloadS2C(EntityBetterFallingBlock entity) {
        this(entity.getId(), entity.getUuid(), entity.getPos(), entity.getVelocity(), entity.dataBlock, entity.hasNoGravity(), entity.tickMove, entity.blockPosEnd);
    }

    //#if MC>=12005
    @Override
    public Id<SummonFallingBlockPayloadS2C> getId() {
        return ID;
    }
    //#endif

    public void encode(
            //#if MC>=12005
            RegistryByteBuf buf
            //#else
            //$$ PacketByteBuf buf
            //#endif
    ) {
        buf.writeInt(id);
        buf.writeUuid(uuid);
        buf.writeDouble(pos.x);
        buf.writeDouble(pos.y);
        buf.writeDouble(pos.z);
        buf.writeDouble(velocity.x);
        buf.writeDouble(velocity.y);
        buf.writeDouble(velocity.z);
        dataBlock.writeClientRenderData(LOGGER, buf, blockPosEnd);
        buf.writeBoolean(hasNoGravity);
        buf.writeInt(tickMove);
        if (tickMove >= 0) {
            buf.writeBlockPos(blockPosEnd);
        }
    }

    public static SummonFallingBlockPayloadS2C decode(PacketByteBuf buf) {
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

}