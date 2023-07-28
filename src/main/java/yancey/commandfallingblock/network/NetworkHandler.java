package yancey.commandfallingblock.network;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import yancey.commandfallingblock.CommandFallingBlock;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;

public class NetworkHandler {

    private static final Identifier ID_SUMMON_FALLING_BLOCK = new Identifier(CommandFallingBlock.MOD_ID, "summon_falling_block");
    private static final Logger LOGGER = LogUtils.getLogger();

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        //生成掉落方块
        ClientPlayNetworking.registerGlobalReceiver(ID_SUMMON_FALLING_BLOCK, (client, handler, buf, responseSender) -> {
            ClientWorld world = handler.getWorld();
            PacketSummonFallingBlock packet = new PacketSummonFallingBlock(buf);
            client.execute(() -> {
                EntityBetterFallingBlock entity = EntityBetterFallingBlock.BETTER_FALLING_BLOCK.create(world);
                if (entity != null) {
                    entity.onSpawnPacket(packet);
                    world.addEntity(packet.id, entity);
                } else {
                    LOGGER.warn("Skipping Entity with id {}", EntityBetterFallingBlock.BETTER_FALLING_BLOCK);
                }
            });
        });
    }

    public static void summonFallingBlock(EntityBetterFallingBlock entityBetterFallingBlock, ServerPlayerEntity player){
        PacketByteBuf packetByteBuf = PacketByteBufs.create();
        new PacketSummonFallingBlock(entityBetterFallingBlock).write(packetByteBuf);
        ServerPlayNetworking.send(player, ID_SUMMON_FALLING_BLOCK, packetByteBuf);
    }

}
