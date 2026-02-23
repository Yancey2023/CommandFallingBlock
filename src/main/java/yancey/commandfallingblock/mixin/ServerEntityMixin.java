package yancey.commandfallingblock.mixin;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
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
//$$ import net.minecraft.network.FriendlyByteBuf;
//#endif

@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {

    @Final
    @Shadow
    private Entity entity;

    @Inject(method = "addPairing", at = @At("HEAD"), cancellable = true)
    private void modifyAddPairing(ServerPlayer player, CallbackInfo ci) {
        if (entity instanceof EntityBetterFallingBlock) {
            //#if MC>=12005
            ServerPlayNetworking.send(player, new SummonFallingBlockPayloadS2C((EntityBetterFallingBlock) entity));
            //#else
            //$$ FriendlyByteBuf buf = PacketByteBufs.create();
            //$$ new SummonFallingBlockPayloadS2C((EntityBetterFallingBlock) entity).encode(buf);
            //$$ ServerPlayNetworking.send(player, SummonFallingBlockPayloadS2C.ID, buf);
            //#endif
            ci.cancel();
        }
    }

}
