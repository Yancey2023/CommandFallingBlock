package yancey.commandfallingblock.mixin;

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
import yancey.commandfallingblock.network.NetworkHandler;

@Mixin(EntityTrackerEntry.class)
public abstract class EntityTrackerEntryMixin {

    @Final
    @Shadow
    private Entity entity;

    @Inject(method = "startTracking", at = @At("HEAD"), cancellable = true)
    private void modifyStartTracking(ServerPlayerEntity player, CallbackInfo ci) {
        if (entity instanceof EntityBetterFallingBlock) {
            NetworkHandler.summonFallingBlock((EntityBetterFallingBlock) entity, player);
            ci.cancel();
        }
    }

//    @Inject(method = "stopTracking", at = @At("TAIL"))
//    private void onStopTracking(ServerPlayerEntity player, CallbackInfo ci) {
//        EntityTrackingEvents.STOP_TRACKING.invoker().onStopTracking(this.entity, player);
//    }

}
