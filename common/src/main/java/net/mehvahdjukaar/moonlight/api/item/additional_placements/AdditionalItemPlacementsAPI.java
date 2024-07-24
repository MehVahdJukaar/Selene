package net.mehvahdjukaar.moonlight.api.item.additional_placements;

import com.mojang.datafixers.util.Pair;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.misc.IExtendedItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AdditionalItemPlacementsAPI {

    private static boolean isAfterRegistration = false;
    private static WeakReference<Map<Block, Item>> blockToItemsMap = new WeakReference<>(null);

    private static final List<Consumer<Event>> registrationListeners = new ArrayList<>();

    private static final List<Pair<Supplier<? extends AdditionalItemPlacement>, Supplier<? extends Item>>> PLACEMENTS = new ArrayList<>();
    private static final List<Pair<Function<Item, ? extends AdditionalItemPlacement>, Predicate<Item>>> PLACEMENTS_GENERIC = new ArrayList<>();

    /**
     * Adds a behavior to an existing block. can be called at any time but ideally before registration. Less ideally during mod setup
     */
    public static void addRegistration(Consumer<Event> eventConsumer){
        Moonlight.assertInitPhase();
        registrationListeners.add(eventConsumer);
    }

    @Nullable
    public static AdditionalItemPlacement getBehavior(Item item) {
        return ((IExtendedItem) item).moonlight$getAdditionalBehavior();
    }

    public static boolean hasBehavior(Item item) {
        return getBehavior(item) != null;
    }


    //needed as all items have to be registered before we can add them to maps. ALso better to do this asap
    @ApiStatus.Internal
    public static void afterItemReg() {
        if (blockToItemsMap.get() == null) {
            if (PlatHelper.isDev()) {
                throw new AssertionError("Block to items map was null");
            }
        }
        //after all registry objects are created we register our stuff
        attemptRegistering();
    }


    private static void attemptRegistering() {
        Map<Block, Item> map = blockToItemsMap.get();
        if (map != null) {

            for (Item item : BuiltInRegistries.ITEM) {
                for (var v : PLACEMENTS_GENERIC) {
                    var predicate = v.getSecond();
                    if (predicate.test(item)) {
                        PLACEMENTS.add(Pair.of(() -> v.getFirst().apply(item), () -> item));
                    }
                }
            }
            Event ev = (target, instance) -> PLACEMENTS.add(Pair.of(() -> instance, () -> target));
            for (var l : registrationListeners) {
                l.accept(ev);
            }
            PLACEMENTS_GENERIC.clear();
            for (var p : PLACEMENTS) {
                AdditionalItemPlacement placement = p.getFirst().get();
                Item i = p.getSecond().get();
                Block b = placement.getPlacedBlock();

                if (i != null && b != null) {
                    if (i != Items.AIR && b != Blocks.AIR) {
                        ((IExtendedItem) i).moonlight$addAdditionalBehavior(placement);
                        if (!map.containsKey(b)) map.put(b, i);
                    } else {
                        throw new AssertionError("Attempted to register an Additional behavior for block "+  b + " using with item " + i);
                    }
                }
            }
        }
    }

    //called just once when registry callbacks fire for items. once since we just have 1 item that we use to call this.
    static void onRegistryCallback(Map<Block, Item> pBlockToItemMap) {
        blockToItemsMap = new WeakReference<>(pBlockToItemMap);
        if (isAfterRegistration) {
            //if we are here it means we are in sync phase where maps are re constructured
            attemptRegistering();
            blockToItemsMap.clear();
        }
        isAfterRegistration = true;
    }

    public interface Event {

        void register(Item target, AdditionalItemPlacement instance);

        // Registers default instance to make simple block placement behavior
        default void registerSimple(Item target, Block toPlace) {
            register(target, new AdditionalItemPlacement(toPlace));
        }
    }

}
