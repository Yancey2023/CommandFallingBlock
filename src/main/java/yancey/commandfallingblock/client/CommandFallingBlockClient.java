package yancey.commandfallingblock.client;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;
import yancey.commandfallingblock.network.SummonFallingBlockPayloadS2C;

@Environment(EnvType.CLIENT)
public class CommandFallingBlockClient implements ClientModInitializer {

    private static final Logger LOGGER = LogUtils.getLogger();

    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(SummonFallingBlockPayloadS2C.ID, (payload, context) ->
                MinecraftClient.getInstance().execute(() -> {
                    EntityBetterFallingBlock entity = EntityBetterFallingBlock.BETTER_FALLING_BLOCK.create(context.player().clientWorld);
                    if (entity != null) {
                        entity.onSpawnPacket(payload);
                        context.player().clientWorld.addEntity(entity);
                    } else {
                        LOGGER.warn("Skipping Entity with id {}", EntityBetterFallingBlock.BETTER_FALLING_BLOCK);
                    }
                }));
        EntityRendererRegistry.register(EntityBetterFallingBlock.BETTER_FALLING_BLOCK, RenderBetterFallingBlock::new);
    }
}
