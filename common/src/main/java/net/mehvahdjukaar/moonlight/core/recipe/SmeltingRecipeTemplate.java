package net.mehvahdjukaar.moonlight.core.recipe;

import com.google.gson.JsonObject;
import net.mehvahdjukaar.moonlight.api.resources.recipe.IRecipeTemplate;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SmeltingRecipeTemplate implements IRecipeTemplate<SimpleCookingRecipeBuilder.Result>  {

    private final List<Object> conditions = new ArrayList<>();

    private final CookingBookCategory category;
    private final Ingredient ingredient;
    private final ItemStack result;
    private final float experience;
    private final int cookingTime;
    private final SimpleCookingSerializer<?> serializer;

    public SmeltingRecipeTemplate(JsonObject json, SimpleCookingSerializer<?> serializer) {
        this.serializer = serializer;
        AtomicReference<CookingBookCategory> cat = new AtomicReference<>();
        AtomicReference<Ingredient> ing = new AtomicReference<>();
        AtomicReference<ItemStack> res = new AtomicReference<>();
        AtomicReference<Float> exp = new AtomicReference<>();
        AtomicReference<Integer> time = new AtomicReference<>();
        var dummy = new SimpleCookingSerializer<>((resourceLocation, string, cookingBookCategory, ingredient1, itemStack, f, i) -> {
            cat.set(cookingBookCategory);
            ing.set(ingredient1);
            res.set(itemStack);
            exp.set(f);
            time.set(i);

            return null;
        }, serializer.defaultCookingTime);

        dummy.fromJson(new ResourceLocation("dummy"), json);
        category = cat.get();
        ingredient = ing.get();
        result = res.get();
        experience = exp.get();
        cookingTime = time.get();

    }

    @Override
    public <T extends BlockType> SimpleCookingRecipeBuilder.Result createSimilar(T originalMat, T destinationMat, Item unlockItem, @Nullable String id) {
        var newIngredient = IRecipeTemplate.convertIngredients(originalMat, destinationMat, this.ingredient);
        ItemStack newResult =
                BlockType.changeItemType(this.result.getItem(), originalMat, destinationMat)
                        .getDefaultInstance(); //

        if (newResult == null) {
            throw new UnsupportedOperationException(String.format("Could not convert output item %s from type %s to %s",
                    this.result, originalMat, destinationMat));
        }
        if (newIngredient == null) {
            throw new UnsupportedOperationException(String.format("Could not convert output item %s from type %s to %s",
                    this.ingredient, originalMat, destinationMat));
        }

        SimpleCookingRecipeBuilder builder = new SimpleCookingRecipeBuilder(
                RecipeCategory.BUILDING_BLOCKS,
                category, newResult.getItem(),
                newIngredient, this.experience, this.cookingTime,
                serializer);

        builder.unlockedBy("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(unlockItem));

        AtomicReference<SimpleCookingRecipeBuilder.Result> newRecipe = new AtomicReference<>();

        if (id == null) {
            builder.save(r -> newRecipe.set((SimpleCookingRecipeBuilder.Result) r));
        } else {
            builder.save(r -> newRecipe.set((SimpleCookingRecipeBuilder.Result) r), id);
        }

        return newRecipe.get();
    }

    @Override
    public void addCondition(Object condition) {
        this.conditions.add(condition);
    }

    @Override
    public List<Object> getConditions() {
        return conditions;
    }
}
