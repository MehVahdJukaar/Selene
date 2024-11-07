package net.mehvahdjukaar.moonlight.core.mixins.neoforge;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//Fixes hard casts
@Mixin(PacketDistributor.class)
public class PacketDistributorHackMixin {

    @Inject(method = "sendToPlayersTrackingEntity",
            cancellable = true,
            remap = false,
            require = 0,
            at = @At(value = "HEAD"))
    private static void ml$fixHardCast1(Entity entity, CustomPacketPayload payload, CustomPacketPayload[] payloads, CallbackInfo ci) {
        Level level = entity.getCommandSenderWorld();
        if (!level.isClientSide && !(level.getChunkSource() instanceof ServerChunkCache)) {
            ci.cancel();
        }
    }

    @Inject(method = "sendToPlayersTrackingEntityAndSelf",
            cancellable = true,
            remap = false,
            require = 0,
            at = @At(value = "HEAD"))
    private static void ml$fixHardCast2(Entity entity, CustomPacketPayload payload, CustomPacketPayload[] payloads, CallbackInfo ci) {
        Level level = entity.getCommandSenderWorld();
        if (!level.isClientSide && !(level.getChunkSource() instanceof ServerChunkCache)) {
            ci.cancel();
        }
    }

}
