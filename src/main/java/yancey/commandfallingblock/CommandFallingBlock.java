package yancey.commandfallingblock;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import yancey.commandfallingblock.command.FallingBlockCommand;

public class CommandFallingBlock implements ModInitializer {

    public static final String MOD_ID = "commandfallingblock";

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> FallingBlockCommand.register(dispatcher, registryAccess));
    }
}
