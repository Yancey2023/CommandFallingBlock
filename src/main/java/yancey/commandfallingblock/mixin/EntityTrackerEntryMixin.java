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

//#if MC<12005
//$$ import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
//$$ import net.minecraft.network.PacketByteBuf;
//#endif

@Mixin(EntityTrackerEntry.class)
public abstract class EntityTrackerEntryMixin {

    @Final
    @Shadow
    private Entity entity;

    @Inject(method = "startTracking", at = @At("HEAD"), cancellable = true)
    private void modifyStartTracking(ServerPlayerEntity player, CallbackInfo ci) {
        if (entity instanceof EntityBetterFallingBlock) {
            //#if MC>=12005
            ServerPlayNetworking.send(player, new SummonFallingBlockPayloadS2C((EntityBetterFallingBlock) entity));
            //#else
            //$$ PacketByteBuf packetByteBuf = PacketByteBufs.create();
            //$$ new SummonFallingBlockPayloadS2C((EntityBetterFallingBlock) entity).encode(packetByteBuf);
            //$$ ServerPlayNetworking.send(player, SummonFallingBlockPayloadS2C.ID, packetByteBuf);
            //#endif
            ci.cancel();
        }
    }

}
