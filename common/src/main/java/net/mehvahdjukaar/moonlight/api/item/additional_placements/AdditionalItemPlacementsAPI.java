package net.mehvahdjukaar.moonlight.api.item.additional_placements;

import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.misc.IExtendedItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class AdditionalItemPlacementsAPI {

    //no need for sided instance yet
    private static final List<Consumer<Event>> LISTENERS = Collections.synchronizedList(new ArrayList<>());
    private static final Map<Item, AdditionalItemPlacement> PLACEMENTS = new HashMap<>();
    private static final Set<Block> ADDED_BLOCKS = new HashSet<>();

    /**
     * Adds a behavior to an existing block. can be called at any time but ideally before registration. Less ideally during mod setup
     */
    public static void addRegistration(Consumer<Event> eventConsumer) {
        Moonlight.assertInitPhase();
        LISTENERS.add(eventConsumer);
    }

    @Nullable
    public static AdditionalItemPlacement getBehavior(Item item) {
        return ((IExtendedItem) item).moonlight$getAdditionalBehavior();
    }

    public static boolean hasBehavior(Item item) {
        return getBehavior(item) != null;
    }

    @ApiStatus.Internal
    public static void onReload(RegistryAccess registryAccess, Boolean aBoolean) {
        Map<Block, Item> map = Item.BY_BLOCK;

        for (var b : ADDED_BLOCKS) {
            map.remove(b);
            //reset inverse
            b.item = null;
        }
        ADDED_BLOCKS.clear();

        for (var l : LISTENERS) {
            l.accept(PLACEMENTS::put);
        }
        for (var p : PLACEMENTS.entrySet()) {
            AdditionalItemPlacement placement = p.getValue();
            Item i = p.getKey();
            Block b = placement.getPlacedBlock();

            if (i != null && b != null) {
                if (i != Items.AIR && b != Blocks.AIR) {
                    ((IExtendedItem) i).moonlight$addAdditionalBehavior(placement);
                    if (!map.containsKey(b)) {
                        map.put(b, i);
                        b.item = null;
                        ADDED_BLOCKS.add(b);
                    }
                } else {
                    throw new AssertionError("Attempted to register an Additional behavior for block " + b + " using with item " + i);
                }
            }
        }
    }

    public interface Event {

        void register(Item target, AdditionalItemPlacement instance);

        // Registers default instance to make simple block placement behavior
        default void registerSimple(Item target, Block toPlace) {
            register(target, new AdditionalItemPlacement(toPlace));
        }
    }

}
