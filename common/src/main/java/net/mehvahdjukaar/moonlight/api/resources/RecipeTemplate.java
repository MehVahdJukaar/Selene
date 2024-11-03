package net.mehvahdjukaar.moonlight.api.resources;

import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public class RecipeTemplate {

    private static final Map<Class<? extends Recipe<?>>, BiFunction<Recipe<?>, UnaryOperator<ItemStack>, Recipe<?>>> REMAPPERS = new HashMap<>();

    public static <R extends Recipe<?>> void registerSimple(Class<R> type, RecipeFactory<R> factory) {
        register(type, (r, t) -> createSimple(r, factory, t));
    }

    public static <R extends Recipe<?>> void register(Class<R> type, BiFunction<R, UnaryOperator<ItemStack>, R> factory) {
        REMAPPERS.put(type, (r, t) -> factory.apply((R) r, t));
    }

    public interface RecipeFactory<R extends Recipe<?>> {
        R create(String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients);
    }


    private static <R extends Recipe<?>> R createSimple(R or, RecipeFactory<R> factory, UnaryOperator<ItemStack> typeChanger) {
        List<Ingredient> newList = convertIngredients(or.getIngredients(), typeChanger);
        ItemStack originalResult = or.getResultItem(RegistryAccess.EMPTY);
        ItemStack newResult = typeChanger.apply(originalResult);
        if (newResult == null) throw new UnsupportedOperationException("Failed to convert recipe result");
        NonNullList<Ingredient> ingredients = NonNullList.of(Ingredient.EMPTY, newList.toArray(Ingredient[]::new));

        CraftingBookCategory cat = CraftingBookCategory.MISC;
        if (or instanceof CraftingRecipe cr) {
            cat = cr.category();
        }
        return factory.create(or.getGroup(), cat, newResult, ingredients);
    }

    private static <R extends Recipe<?>> @NotNull List<Ingredient> convertIngredients(NonNullList<Ingredient> or, UnaryOperator<ItemStack> typeChanger) {
        List<Ingredient> newList = new ArrayList<>(or);
        for (var ingredient : or) {
            if (ingredient.getItems().length > 0) {
                ItemStack i = typeChanger.apply(ingredient.getItems()[0]);
                if (i != null) newList.add(Ingredient.of(i));
            }
        }
        return newList;
    }

    private static ShapedRecipe createShaped(ShapedRecipe or, UnaryOperator<ItemStack> typeChanger) {
        List<Ingredient> newList = convertIngredients(or.getIngredients(), typeChanger);
        ItemStack originalResult = or.getResultItem(RegistryAccess.EMPTY);
        ItemStack newResult = typeChanger.apply(originalResult);
        if (newResult == null) throw new UnsupportedOperationException("Failed to convert recipe result");
        NonNullList<Ingredient> ingredients = NonNullList.of(Ingredient.EMPTY, newList.toArray(Ingredient[]::new));

        ShapedRecipePattern pattern = new ShapedRecipePattern(or.getWidth(), or.getHeight(), ingredients, Optional.empty());

        return new ShapedRecipe(or.getGroup(), or.category(), pattern, newResult);
    }

    public static <T extends BlockType, R extends Recipe<?>> RecipeHolder<?> makeSimilarRecipe(R original, T originalMat,
                                                                                               T destinationMat, String baseID) {
        var clazz = original.getClass();
        var remapper = REMAPPERS.get(clazz);
        if (remapper == null) {
            throw new UnsupportedOperationException("Recipe class " + clazz + " not supported. You must register it using RecipeTemplate.register()");
        }
        ResourceLocation newId = ResourceLocation.parse(baseID + "/" + destinationMat.getAppendableId());

        var remapped = remapper.apply(original, (stack) -> convertItemStack(stack, originalMat, destinationMat));

        return new RecipeHolder<>(newId, remapped);
    }

    @Nullable
    public static <T extends BlockType> ItemStack convertItemStack(ItemStack original, T originalMat, T destinationMat) {
        var changed = BlockType.changeItemType(original.getItem(), originalMat, destinationMat);
        if (changed == null) return null;
        return original.transmuteCopy(changed);

    }

    static {
        register(ShapedRecipe.class, RecipeTemplate::createShaped);
        registerSimple(ShapelessRecipe.class, ShapelessRecipe::new);
    }

}
