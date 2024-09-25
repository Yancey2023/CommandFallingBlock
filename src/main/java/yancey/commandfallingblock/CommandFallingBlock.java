package yancey.commandfallingblock;

import net.fabricmc.api.ModInitializer;
import yancey.commandfallingblock.command.FallingBlockCommand;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;

//#if MC>=12000
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
//#else
//$$ import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
//$$ import net.minecraft.util.registry.Registry;
//#endif

//#if MC>=12005
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import yancey.commandfallingblock.network.SummonFallingBlockPayloadS2C;
//#endif

public class CommandFallingBlock implements ModInitializer {

    @SuppressWarnings("SpellCheckingInspection")
    public static final String MOD_ID = "commandfallingblock";

    @Override
    public void onInitialize() {
        Registry.register(
                //#if MC>=12000
                Registries.ENTITY_TYPE,
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
