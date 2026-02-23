package yancey.commandfallingblock.network;

import com.mojang.logging.LogUtils;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import yancey.commandfallingblock.util.DataBlock;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;

//#if MC>=12005
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//#endif

import static yancey.commandfallingblock.CommandFallingBlock.MOD_ID;

//#if MC>=12111
import org.jspecify.annotations.NonNull;
//#elseif MC>=12005
//$$ import org.jetbrains.annotations.NotNull;
//#endif

//#if MC>=11802
public record SummonFallingBlockPayloadS2C(int id, UUID uuid, Vec3 pos, Vec3 velocity, DataBlock dataBlock,
                                           boolean hasNoGravity, int tickMove, BlockPos blockPosEnd)
//#else
//$$ public class SummonFallingBlockPayloadS2C
//#endif
        //#if MC>=12005
        implements CustomPacketPayload
        //#endif
{

    private static final Logger LOGGER = LogUtils.getLogger();

    //#if MC>=12005
    //#if MC>=12100
    public static final Type<SummonFallingBlockPayloadS2C> ID = new Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "summon_falling_block"));
    //#else
    //$$ public static final Type<SummonFallingBlockPayloadS2C> ID = new Type<>(new ResourceLocation(MOD_ID, "summon_falling_block"));
    //#endif
    public static final StreamCodec<RegistryFriendlyByteBuf, SummonFallingBlockPayloadS2C> CODEC
            = StreamCodec.ofMember(SummonFallingBlockPayloadS2C::encode, SummonFallingBlockPayloadS2C::decode);
    //#else
    //$$ public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "summon_falling_block");
    //#endif

    //#if MC<11802
    //$$ private final int id;
    //$$ private final UUID uuid;
    //$$ private final Vec3 pos, velocity;
    //$$ private final DataBlock dataBlock;
    //$$ private final boolean hasNoGravity;
    //$$ private final int tickMove;
    //$$ private final BlockPos blockPosEnd;
    //$$
    //$$ public SummonFallingBlockPayloadS2C(int id, UUID uuid, Vec3 pos, Vec3 velocity, DataBlock dataBlock, boolean hasNoGravity, int tickMove, BlockPos blockPosEnd) {
    //$$     this.id = id;
    //$$     this.uuid = uuid;
    //$$     this.pos = pos;
    //$$     this.velocity = velocity;
    //$$     this.dataBlock = dataBlock;
    //$$     this.hasNoGravity = hasNoGravity;
    //$$     this.tickMove = tickMove;
    //$$     this.blockPosEnd = blockPosEnd;
    //$$ }
    //$$
    //$$ public int id() {
    //$$     return id;
    //$$ }
    //$$
    //$$ public UUID uuid() {
    //$$     return uuid;
    //$$ }
    //$$
    //$$ public Vec3 pos() {
    //$$     return pos;
    //$$ }
    //$$
    //$$ public Vec3 velocity() {
    //$$     return velocity;
    //$$ }
    //$$
    //$$ public DataBlock dataBlock() {
    //$$     return dataBlock;
    //$$ }
    //$$
    //$$ public boolean hasNoGravity() {
    //$$     return hasNoGravity;
    //$$ }
    //$$
    //$$ public int tickMove() {
    //$$     return tickMove;
    //$$ }
    //$$
    //$$ public BlockPos blockPosEnd() {
    //$$     return blockPosEnd;
    //$$ }
    //#endif

    public SummonFallingBlockPayloadS2C(EntityBetterFallingBlock entity) {
        this(entity.getId(), entity.getUUID(), entity.position(), entity.getDeltaMovement(), entity.dataBlock, entity.isNoGravity(), entity.tickMove, entity.blockPosEnd);
    }

    //#if MC>=12005
    @Override
    public
    //#if MC>=12111
    @NonNull
    //#else
    //$$ @NotNull
    //#endif
    Type<SummonFallingBlockPayloadS2C> type() {
        return ID;
    }
    //#endif

    public void encode(
            //#if MC>=12005
            RegistryFriendlyByteBuf buf
            //#else
            //$$ FriendlyByteBuf buf
            //#endif
    ) {
        buf.writeInt(id);
        buf.writeUUID(uuid);
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

    public static SummonFallingBlockPayloadS2C decode(FriendlyByteBuf buf) {
        int id = buf.readInt();
        UUID uuid = buf.readUUID();
        Vec3 pos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        Vec3 velocity = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
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