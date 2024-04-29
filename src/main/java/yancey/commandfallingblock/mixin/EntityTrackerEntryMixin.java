package yancey.commandfallingblock.mixin;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yancey.commandfallingblock.entity.EntityBetterFallingBlock;
import yancey.commandfallingblock.network.SummonFallingBlockPayloadS2C;

@Mixin(EntityTrackerEntry.class)
public abstract class EntityTrackerEntryMixin {

    @Final
    @Shadow
    private Entity entity;

    @Inject(method = "startTracking", at = @At("HEAD"), cancellable = true)
    private void modifyStartTracking(ServerPlayerEntity player, CallbackInfo ci) {
        if (entity instanceof EntityBetterFallingBlock entityBetterFallingBlock) {
            ServerPlayNetworking.send(player, new SummonFallingBlockPayloadS2C(entityBetterFallingBlock));
            ci.cancel();
        }
    }

}
