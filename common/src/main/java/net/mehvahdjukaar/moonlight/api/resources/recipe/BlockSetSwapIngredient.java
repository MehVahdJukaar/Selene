package net.mehvahdjukaar.moonlight.api.resources.recipe;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.api.set.BlockTypeRegistry;
import net.minecraft.world.item.crafting.Ingredient;

public class BlockSetSwapIngredient {

    @ExpectPlatform
    public static <T extends BlockType> Ingredient create(Ingredient original, T from, T to){
        throw new AssertionError();
    }
}
