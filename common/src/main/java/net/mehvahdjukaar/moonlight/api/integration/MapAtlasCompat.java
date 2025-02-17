package net.mehvahdjukaar.moonlight.api.integration;


import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;
import pepjebs.mapatlases.MapAtlasesMod;
import pepjebs.mapatlases.client.MapAtlasesClient;
import pepjebs.mapatlases.item.MapAtlasItem;
import pepjebs.mapatlases.map_collection.MapKey;

@Deprecated(forRemoval = true) //use the internal one of make your own
public class MapAtlasCompat {

    public static boolean isAtlas(Item item) {
        return net.mehvahdjukaar.moonlight.core.integration.MapAtlasCompat.isAtlas(item);
    }

    @Nullable
    public static MapItemSavedData getSavedDataFromAtlas(ItemStack atlas, Level level, Player player) {
        return net.mehvahdjukaar.moonlight.core.integration.MapAtlasCompat.getSavedDataFromAtlas(atlas, level, player);
    }

    @Nullable
    public static Integer getMapIdFromAtlas(ItemStack atlas, Level level, Object data) {
        return net.mehvahdjukaar.moonlight.core.integration.MapAtlasCompat.getMapIdFromAtlas(atlas, level, data);
    }

    @Environment(EnvType.CLIENT)
    public static void scaleDecoration(PoseStack poseStack) {
        MapAtlasesClient.modifyDecorationTransform(poseStack);
    }

    @Environment(EnvType.CLIENT)
    public static void scaleDecorationText(PoseStack poseStack, float textWidth, float textScale) {
        MapAtlasesClient.modifyTextDecorationTransform(poseStack, textWidth, textScale);
    }
}
