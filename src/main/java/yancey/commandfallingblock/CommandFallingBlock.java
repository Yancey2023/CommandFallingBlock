package yancey.commandfallingblock;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import yancey.commandfallingblock.command.FallingBlockCommand;
import yancey.commandfallingblock.network.SummonFallingBlockPayloadS2C;

public class CommandFallingBlock implements ModInitializer {

    @SuppressWarnings("SpellCheckingInspection")
    public static final String MOD_ID = "commandfallingblock";

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(SummonFallingBlockPayloadS2C.ID, SummonFallingBlockPayloadS2C.CODEC);
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> FallingBlockCommand.register(dispatcher, registryAccess));
    }
}
