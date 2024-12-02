package net.mehvahdjukaar.moonlight.api.resources.recipe.forge;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.api.set.BlockTypeRegistry;
import net.mehvahdjukaar.moonlight.core.set.BlockSetInternal;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockSetSwapIngredientImpl<T extends BlockType> extends AbstractIngredient {

    private final Ingredient inner;
    private final T fromType;
    private final T toType;
    private final BlockTypeRegistry<T> registry;

    private ItemStack[] items;

    public BlockSetSwapIngredientImpl(Ingredient inner, T fromType, T toType, BlockTypeRegistry<T> registry) {
        super();
        this.inner = inner;
        this.fromType = fromType;
        this.toType = toType;
        this.registry = registry;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public ItemStack[] getItems() {
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
            this.items = newItems.toArray(new ItemStack[0]);
        }
        return this.items;
    }

    @Override
    protected void invalidate() {
        super.invalidate();
        this.items = null;
    }

    @Override
    public IIngredientSerializer<BlockSetSwapIngredientImpl<?>> getSerializer() {
        return new IIngredientSerializer<>() {
            @Override
            public BlockSetSwapIngredientImpl<?> parse(FriendlyByteBuf arg) {
                var reg = BlockSetInternal.getByName(arg.readUtf());
                var from = reg.getFromNBT(arg.readUtf());
                var to = reg.getFromNBT(arg.readUtf());
                var ing = Ingredient.fromNetwork(arg);
                return new BlockSetSwapIngredientImpl<>(ing, from, to, (BlockTypeRegistry) reg);
            }

            @Override
            public BlockSetSwapIngredientImpl<?> parse(JsonObject jsonObject) {
                var reg = BlockSetInternal.REGISTRIES_BY_NAME.getValue(jsonObject.get("registry").getAsString());
                var from = reg.getFromNBT(jsonObject.get("from").getAsString());
                var to = reg.getFromNBT(jsonObject.get("to").getAsString());
                var ing = Ingredient.fromJson(jsonObject.get("inner"));
                return new BlockSetSwapIngredientImpl<>(ing, from, to, (BlockTypeRegistry) reg);
            }

            @Override
            public void write(FriendlyByteBuf buf, BlockSetSwapIngredientImpl ing) {
                buf.writeUtf(ing.registry.typeName());
                buf.writeUtf(ing.fromType.getAppendableId());
                buf.writeUtf(ing.toType.getAppendableId());
                ing.inner.toNetwork(buf);
            }
        };
    }

    @Override
    public JsonElement toJson() {
        var obj = new JsonObject();
        obj.addProperty("registry", this.registry.typeName());
        obj.addProperty("from", this.fromType.getAppendableId());
        obj.addProperty("to", this.toType.getAppendableId());
        obj.add("inner", this.inner.toJson());
        return obj;
    }

    public static <T extends BlockType> Ingredient create(Ingredient original, T from, T to){
        return new BlockSetSwapIngredientImpl<>(original, from, to, from.getRegistry());
    }

}
