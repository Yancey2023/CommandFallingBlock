package yancey.commandfallingblock;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import yancey.commandfallingblock.command.FallingBlockCommand;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;

//#if MC>=12000
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.core.registries.BuiltInRegistries;
//#else
//$$ import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
//#endif

//#if MC>=12005
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import yancey.commandfallingblock.network.SummonFallingBlockPayloadS2C;
//#endif

public class CommandFallingBlock implements ModInitializer {

    public static final String MOD_ID = "commandfallingblock";

    @Override
    public void onInitialize() {
        Registry.register(
                //#if MC>=12000
                BuiltInRegistries.ENTITY_TYPE,
                //#else
                //$$ Registry.ENTITY_TYPE,
                //#endif
                EntityBetterFallingBlock.ID_BETTER_FALLING_BLOCK,
                EntityBetterFallingBlock.BETTER_FALLING_BLOCK
        );

        //#if MC>=12005
        PayloadTypeRegistry.playS2C().register(SummonFallingBlockPayloadS2C.ID, SummonFallingBlockPayloadS2C.CODEC);
        //#endif

        //#if MC>=12000
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> FallingBlockCommand.register(registryAccess, dispatcher));
        //#else
        //$$ CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> FallingBlockCommand.register(dispatcher));
        //#endif
    }
}
