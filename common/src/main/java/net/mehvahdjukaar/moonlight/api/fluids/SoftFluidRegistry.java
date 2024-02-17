package net.mehvahdjukaar.moonlight.api.fluids;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.network.ClientBoundFinalizeFluidsMessage;
import net.mehvahdjukaar.moonlight.core.network.ModMessages;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

//TODO: maybe split into api/core?
public class SoftFluidRegistry {

    public static final ResourceKey<Registry<SoftFluid>> KEY = ResourceKey.createRegistryKey(Moonlight.res("soft_fluids"));

    public static final ResourceLocation EMPTY_ID = Moonlight.res("empty");

    private static final Map<Fluid, Holder<SoftFluid>> FLUID_MAP = new IdentityHashMap<>();
    private static final Map<Item, Holder<SoftFluid>> ITEM_MAP = new IdentityHashMap<>();


    public static Holder<SoftFluid> getEmpty() {
        return BuiltInSoftFluids.EMPTY.getHolder();
    }

    @ExpectPlatform
    public static void init() {
    }

    public static Registry<SoftFluid> hackyGetRegistry() {
        return Utils.hackyGetRegistry(KEY);
    }

    public static Registry<SoftFluid> getRegistry(RegistryAccess registryAccess) {
        return registryAccess.registryOrThrow(KEY);
    }

    public static Collection<SoftFluid> getValues() {
        return hackyGetRegistry().stream().toList();
    }

    public static Collection<Holder.Reference<SoftFluid>> getHolders() {
        return hackyGetRegistry().holders().toList();
    }

    public static Set<Map.Entry<ResourceKey<SoftFluid>, SoftFluid>> getEntries() {
        return hackyGetRegistry().entrySet();
    }

    public static Holder<SoftFluid> getHolder(ResourceLocation id) {
        var opt = getOptionalHolder(id);
        if (opt.isPresent()) return opt.get();
        return getEmpty();
    }

    public static Optional<Holder.Reference<SoftFluid>> getOptionalHolder(ResourceLocation id) {
        id = backwardsCompat(id);
        return hackyGetRegistry().getHolder(ResourceKey.create(KEY, id));
    }

    @NotNull
    private static ResourceLocation backwardsCompat(ResourceLocation id) {
        String namespace = id.getNamespace();
        if (namespace.equals("selene") || namespace.equals("minecraft"))
            id = Moonlight.res(id.getPath()); //backwards compat
        return id;
    }

    /**
     * gets a soft fluid provided a forge fluid
     *
     * @param fluid equivalent forge fluid
     * @return soft fluid. null if not found
     */
    @Nullable
    public static Holder<SoftFluid> fromVanillaFluid(Fluid fluid) {
        return FLUID_MAP.get(fluid);
    }

    /**
     * gets a soft fluid provided a bottle like item
     *
     * @param filledContainerItem item containing provided fluid
     * @return soft fluid. null if not found
     */
    @Nullable
    public static Holder<SoftFluid> fromItem(Item filledContainerItem) {
        return ITEM_MAP.get(filledContainerItem);
    }

    //needs to be called on both sides
    private static void populateSlaveMaps() {
        var itemMap = ITEM_MAP;
        itemMap.clear();
        var fluidsMap = FLUID_MAP;
        fluidsMap.clear();
        for (var h : getHolders()) {
            var s = h.value();
            if (PlatHelper.isModLoaded(s.getFromMod())) {
                s.getEquivalentFluids().forEach(f -> fluidsMap.put(f, h));
                s.getContainerList().getPossibleFilled().forEach(i -> {
                    //don't associate water to potion bottle
                    if (i != Items.POTION || s != BuiltInSoftFluids.WATER.get()) {
                        itemMap.put(i, h);
                    }
                });
            }
        }
    }


    //wtf is going on here

    //called by data sync to player
    @ApiStatus.Internal
    public static void postInitClient() {
        populateSlaveMaps();
    }

    @ApiStatus.Internal
    public static void onDataSyncToPlayer(ServerPlayer player, boolean o) {
        ModMessages.CHANNEL.sendToClientPlayer(player, new ClientBoundFinalizeFluidsMessage());
    }

    //on data load
    @ApiStatus.Internal
    public static void onDataLoad() {
        populateSlaveMaps();
        //registers existing fluids. also update the salve maps
        //TODO: why not needed on the client?
        registerExistingVanillaFluids(FLUID_MAP, ITEM_MAP);
    }

    @ExpectPlatform
    private static void registerExistingVanillaFluids(Map<Fluid, Holder<SoftFluid>> fluidMap, Map<Item, Holder<SoftFluid>> itemMap) {
        throw new AssertionError();
    }


}

