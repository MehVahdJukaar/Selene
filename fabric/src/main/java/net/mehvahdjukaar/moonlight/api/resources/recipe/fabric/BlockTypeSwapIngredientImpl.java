package net.mehvahdjukaar.moonlight.api.resources.recipe.fabric;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.api.set.BlockTypeRegistry;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.set.BlockSetInternal;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockTypeSwapIngredientImpl<T extends BlockType> implements CustomIngredient {

    private static final ResourceLocation ID = Moonlight.res("block_type_swap");

    private final Ingredient inner;
    private final T fromType;
    private final T toType;
    private final BlockTypeRegistry<T> registry;

    private List<ItemStack> items;

    public BlockTypeSwapIngredientImpl(Ingredient inner, T fromType, T toType, BlockTypeRegistry<T> reg) {
        super();
        this.inner = inner;
        this.fromType = fromType;
        this.toType = toType;
        this.registry = reg;
    }


    @Override
    public boolean test(ItemStack stack) {
        if (stack != null) {
            for (ItemStack itemStack : this.getMatchingStacks()) {
                if (itemStack.is(stack.getItem())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<ItemStack> getMatchingStacks() {
        if (this.items == null) {
            var original = this.inner.getItems();
            List<ItemStack> newItems = new ArrayList<>();
            boolean success = false;
            for (ItemStack it : this.items) {
                var type = this.registry.getBlockTypeOf(it.getItem());
                if (type != this.fromType) {
                    break;
                } else {
                    var newItem = BlockType.changeItemType(it.getItem(), this.fromType, this.toType);
                    if (newItem != null) {
                        newItems.add(new ItemStack(newItem));
                        success = true;
                    }
                }
            }
            if (!success) {
                newItems.addAll(Arrays.stream(original).toList());
            }
            this.items = newItems;
        }
        return this.items;
    }

    @Override
    public boolean requiresTesting() {
        return inner.requiresTesting();
    }

    @Override
    public CustomIngredientSerializer<BlockTypeSwapIngredientImpl<?>> getSerializer() {
        return SERIALIZER;
    }

    public static CustomIngredientSerializer<BlockTypeSwapIngredientImpl<?>> SERIALIZER =
            new CustomIngredientSerializer<>() {
                @Override
                public ResourceLocation getIdentifier() {
                    return ID;
                }

                @Override
                public BlockTypeSwapIngredientImpl<?> read(JsonObject json) {
                    var reg = BlockSetInternal.REGISTRIES_BY_NAME.getValue(json.get("registry").getAsString());
                    var from = reg.getFromNBT(json.get("from").getAsString());
                    var to = reg.getFromNBT(json.get("to").getAsString());
                    var ing = Ingredient.fromJson(json.get("inner"));
                    return new BlockTypeSwapIngredientImpl<>(ing, from, to, (BlockTypeRegistry) reg);
                }

                @Override
                public void write(JsonObject json, BlockTypeSwapIngredientImpl<?> ingredient) {
                    json.addProperty("registry", ingredient.registry.typeName());
                    json.addProperty("from", ingredient.fromType.getAppendableId());
                    json.addProperty("to", ingredient.toType.getAppendableId());
                    json.add("inner", ingredient.inner.toJson());
                }

                @Override
                public BlockTypeSwapIngredientImpl<?> read(FriendlyByteBuf buf) {
                    var reg = BlockSetInternal.getByName(buf.readUtf());
                    var from = reg.getFromNBT(buf.readUtf());
                    var to = reg.getFromNBT(buf.readUtf());
                    var ing = Ingredient.fromNetwork(buf);
                    return new BlockTypeSwapIngredientImpl<>(ing, from, to, (BlockTypeRegistry) reg);
                }

                @Override
                public void write(FriendlyByteBuf buf, BlockTypeSwapIngredientImpl<?> ing) {
                    buf.writeUtf(ing.registry.typeName());
                    buf.writeUtf(ing.fromType.getAppendableId());
                    buf.writeUtf(ing.toType.getAppendableId());
                    ing.inner.toNetwork(buf);
                }
            };

    public static <T extends BlockType> Ingredient create(Ingredient original, T from, T to) {
        return new BlockTypeSwapIngredientImpl<>(original, from, to, from.getRegistry())
                .toVanilla();
    }

    public static void init() {
        CustomIngredientSerializer.register(SERIALIZER);
    }
}
