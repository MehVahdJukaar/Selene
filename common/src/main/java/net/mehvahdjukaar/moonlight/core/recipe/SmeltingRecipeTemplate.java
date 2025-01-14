package net.mehvahdjukaar.moonlight.core.recipe;

import com.google.gson.JsonObject;
import net.mehvahdjukaar.moonlight.api.resources.recipe.IRecipeTemplate;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SmeltingRecipeTemplate implements IRecipeTemplate<SimpleCookingRecipeBuilder.Result>  {

    private final List<Object> conditions = new ArrayList<>();

    private final CookingBookCategory category;
    private final Ingredient ingredient;
    private final Item result;
    private final float experience;
    private final int cookingTime;
    private final RecipeSerializer<? extends AbstractCookingRecipe> serializer;

    public SmeltingRecipeTemplate(JsonObject json, RecipeSerializer<? extends AbstractCookingRecipe> serializer) {
        this.serializer = serializer;
        JsonObject result = json.getAsJsonObject("result");
        ResourceLocation item = new ResourceLocation(result.get("item").getAsString());
        this.cookingTime = json.get("cookingtime").getAsInt();
        this.experience = json.get("experience").getAsInt();

        var getIngredient = json.getAsJsonObject("ingredient").get("item");
        this.ingredient = Ingredient.fromJson(getIngredient);

        this.result = BuiltInRegistries.ITEM.get(item);
        this.category = CookingBookCategory.CODEC.byName(GsonHelper.getAsString(json, "category", null), CookingBookCategory.BLOCKS);

    }

    @Override
    public <T extends BlockType> SimpleCookingRecipeBuilder.Result createSimilar(T originalMat, T destinationMat, Item unlockItem, @Nullable String id) {
        var newIngredient = IRecipeTemplate.convertIngredients(originalMat, destinationMat, this.ingredient);
        ItemLike newResult = BlockType.changeItemType(this.result, originalMat, destinationMat);

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
                category, newResult,
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
