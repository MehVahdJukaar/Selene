package net.mehvahdjukaar.moonlight.core.recipe;

import com.google.gson.JsonObject;
import net.mehvahdjukaar.moonlight.api.resources.recipe.IRecipeTemplate;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SmeltingRecipeTemplate implements IRecipeTemplate<SimpleCookingRecipeBuilder.Result>  {

    private final List<Object> conditions = new ArrayList<>();

//    private final ResourceLocation id;
    private final CookingBookCategory category;
    private final Ingredient ingredient;
    private final Item result;
    private final float experience;
    private final int cookingTime;

    public SmeltingRecipeTemplate(JsonObject json) {
        JsonObject result = json.getAsJsonObject("result");
        ResourceLocation item = new ResourceLocation(result.get("item").getAsString());
        this.cookingTime = json.get("cookingTime").getAsInt();
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

        SimpleCookingRecipeBuilder builder = SimpleCookingRecipeBuilder.smelting(newIngredient,
                RecipeCategory.BUILDING_BLOCKS, newResult, this.experience, this.cookingTime);

        return null;
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
