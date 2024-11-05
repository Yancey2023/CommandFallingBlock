package yancey.commandfallingblock.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;
import yancey.commandfallingblock.network.SummonFallingBlockPayloadS2C;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

//#if MC>=11802
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
//#else
//$$ import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
//#endif

//#if MC>=12005
import net.minecraft.client.MinecraftClient;
//#else
//$$ import net.minecraft.client.world.ClientWorld;
//#endif

//#if MC>=12102
import net.minecraft.entity.SpawnReason;
//#endif

@Environment(EnvType.CLIENT)
public class CommandFallingBlockClient implements ClientModInitializer {

    private static final Logger LOGGER = LogUtils.getLogger();

    public void onInitializeClient() {
        //#if MC>=12005
        ClientPlayNetworking.registerGlobalReceiver(SummonFallingBlockPayloadS2C.ID, (payload, context) ->
                MinecraftClient.getInstance().execute(() -> {
                    //#if MC>=12102
                    EntityBetterFallingBlock entity = EntityBetterFallingBlock.BETTER_FALLING_BLOCK.create(context.player().clientWorld, SpawnReason.COMMAND);
                    //#else
                    //$$ EntityBetterFallingBlock entity = EntityBetterFallingBlock.BETTER_FALLING_BLOCK.create(context.player().clientWorld);
                    //#endif
                    if (entity != null) {
                        entity.onSpawnPacket(payload);
                        context.player().clientWorld.addEntity(entity);
                    } else {
                        LOGGER.warn("Skipping Entity with id {}", EntityBetterFallingBlock.BETTER_FALLING_BLOCK);
                    }
                }));
        //#else
        //$$ ClientPlayNetworking.registerGlobalReceiver(SummonFallingBlockPayloadS2C.ID, (client, handler, buf, responseSender) -> {
        //$$     ClientWorld world = handler.getWorld();
        //$$     SummonFallingBlockPayloadS2C packet = SummonFallingBlockPayloadS2C.decode(buf);
        //$$     client.execute(() -> {
        //$$         EntityBetterFallingBlock entity = EntityBetterFallingBlock.BETTER_FALLING_BLOCK.create(world);
        //$$         if (entity != null) {
        //$$             entity.onSpawnPacket(packet);
        //$$             //#if MC>=12002
        //$$             world.addEntity(entity);
        //$$             //#else
        //$$             //$$ world.addEntity(packet.id, entity);
        //$$             //#endif
        //$$         } else {
        //$$             LOGGER.warn("Skipping Entity with id {}", EntityBetterFallingBlock.BETTER_FALLING_BLOCK);
        //$$         }
        //$$     });
        //$$ });
        //#endif

        //#if MC>=11802
        EntityRendererRegistry.register(EntityBetterFallingBlock.BETTER_FALLING_BLOCK, RenderBetterFallingBlock::new);
        //#else
        //$$ EntityRendererRegistry.INSTANCE.register(EntityBetterFallingBlock.BETTER_FALLING_BLOCK, (dispatcher, context) -> new RenderBetterFallingBlock(dispatcher));
        //#endif
    }
}
