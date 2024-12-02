package net.mehvahdjukaar.moonlight.api.resources.recipe;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.ApiStatus;

public class BlockTypeSwapIngredient {

    @ExpectPlatform
    public static <T extends BlockType> Ingredient create(Ingredient original, T from, T to) {
        throw new AssertionError();
    }

    @ApiStatus.Internal
    @ExpectPlatform
    public static void init() {
        throw new AssertionError();
    }
}
