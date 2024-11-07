package net.mehvahdjukaar.moonlight.core.mixins.forge;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

//Fixes hard casts
@Mixin(PacketDistributor.class)
public class PacketDistributorHackMixin {

    @Inject(method = "lambda$trackingEntity$6",
            cancellable = true,
            remap = false,
            require = 0,
            at = @At(value = "INVOKE",
                    remap = true,
                    target = "Lnet/minecraft/world/entity/Entity;getCommandSenderWorld()Lnet/minecraft/world/level/Level;"))
    private static void ml$fixHardCast1(Supplier entitySupplier, Packet p, CallbackInfo ci,
                             @Local Entity entity) {
        if (!(entity.getCommandSenderWorld().getChunkSource() instanceof ServerChunkCache)) {
            ci.cancel();
        }
    }

    @Inject(method = "lambda$trackingEntityAndSelf$7",
            cancellable = true,
            remap = false,
            require = 0,
            at = @At(value = "INVOKE",
                    remap = true,
                    target = "Lnet/minecraft/world/entity/Entity;getCommandSenderWorld()Lnet/minecraft/world/level/Level;"))
    private static void ml$fixHardCast2(Supplier entitySupplier, Packet p, CallbackInfo ci,
                             @Local Entity entity) {
        if (!(entity.getCommandSenderWorld().getChunkSource() instanceof ServerChunkCache)) {
            ci.cancel();
        }
    }

    @Inject(method = "lambda$trackingChunk$9",
            cancellable = true,
            remap = false,
            require = 0,
            at = @At(value = "INVOKE",
                    remap = true,
                    target = "Lnet/minecraft/world/level/chunk/LevelChunk;getLevel()Lnet/minecraft/world/level/Level;"))
    private static void ml$fixHardCast3(Supplier entitySupplier, Packet p, CallbackInfo ci,
                                        @Local LevelChunk chunk) {
        if (!(chunk.getLevel().getChunkSource() instanceof ServerChunkCache)) {
            ci.cancel();
        }
    }
}
