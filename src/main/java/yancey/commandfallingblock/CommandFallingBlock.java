package yancey.commandfallingblock;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.SharedConstants;
import yancey.commandfallingblock.command.FallingBlockCommand;

public class CommandFallingBlock implements ModInitializer {

    public static final String MOD_ID = "commandfallingblock";

    @Override
    public void onInitialize() {
//        SharedConstants.isDevelopment = true;//打开开发者模式
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> FallingBlockCommand.register(dispatcher, registryAccess));
    }
}
