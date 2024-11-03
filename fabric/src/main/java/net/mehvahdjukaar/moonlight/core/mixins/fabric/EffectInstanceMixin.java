package net.mehvahdjukaar.moonlight.core.mixins.fabric;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EffectInstance.class)
public class EffectInstanceMixin {

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;withDefaultNamespace(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"))
    private ResourceLocation ml$allowModShaderRes(String string, Operation<ResourceLocation> original) {
        try {
            var res = original.call(string);
            if (Moonlight.isDependant(res.getNamespace())) {
                return res;
            }
        } catch (Exception e) {
            //ignore
        }
        return ResourceLocation.tryParse(string);
    }
}
