package yancey.commandfallingblock.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;
import yancey.commandfallingblock.network.NetworkHandler;

@Environment(EnvType.CLIENT)
public class CommandFallingBlockClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        NetworkHandler.initClient();
        EntityRendererRegistry.INSTANCE.register(EntityBetterFallingBlock.BETTER_FALLING_BLOCK, (dispatcher, context) -> new RenderBetterFallingBlock(dispatcher));
    }
}
