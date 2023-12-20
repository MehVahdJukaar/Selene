package net.mehvahdjukaar.moonlight.forge;


import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.mehvahdjukaar.moonlight.api.client.util.ParticleUtil;
import net.mehvahdjukaar.moonlight.api.entity.IControllableVehicle;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.MoonlightClient;
import net.mehvahdjukaar.moonlight.core.client.MLRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;

@Mod.EventBusSubscriber(modid = MoonlightForge.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MoonlightForgeClient {

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(MoonlightForgeClient::registerShader);
        modEventBus.addListener(EventPriority.LOWEST, MoonlightForgeClient::onTextureStitch);
    }

    private static ShaderInstance translucentParticle;
    public static ShaderInstance textColorShader;

    public static ShaderInstance getTranslucentParticle() {
        return translucentParticle;
    }

    public static ShaderInstance getTextColorShader() {
        return textColorShader;
    }

    public static void registerShader(RegisterShadersEvent event) {
        try {
            ShaderInstance translucentParticleShader = new ShaderInstance(event.getResourceProvider(),
                    Moonlight.res("particle_translucent"), DefaultVertexFormat.POSITION_TEX);

            event.registerShader(translucentParticleShader, s -> translucentParticle = s);

            ParticleUtil.particleShader = MoonlightForgeClient::getTranslucentParticle;
        } catch (Exception e) {
            Moonlight.LOGGER.error("Failed to parse shader: " + e);
        }
        try {
            ShaderInstance shader = new ShaderInstance(event.getResourceProvider(),
                    Moonlight.res("text_alpha_color"), DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
            event.registerShader(shader, s -> textColorShader = s);

            MLRenderTypes.textColorShader = MoonlightForgeClient::getTextColorShader;
        } catch (Exception e) {
            Moonlight.LOGGER.error("Failed to parse shader: " + e);
        }
    }


    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) {

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onInputUpdate(MovementInputUpdateEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            Entity riddenEntity = mc.player.getVehicle();
            if (riddenEntity instanceof IControllableVehicle listener) {
                Input movementInput = event.getInput();
                listener.onInputUpdate(movementInput.left, movementInput.right,
                        movementInput.up, movementInput.down,
                        mc.options.keySprint.isDown(), movementInput.jumping);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onTextureStitch(TextureAtlasStitchedEvent event) {
        MoonlightClient.afterTextureReload();
    }

}