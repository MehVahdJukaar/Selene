package net.mehvahdjukaar.moonlight.core.map.neoforge;

import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapDecorationType;
import net.mehvahdjukaar.moonlight.core.map.MapDataInternal;
import net.mehvahdjukaar.moonlight.neoforge.MoonlightForge;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.registries.DataMapLoader;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;


public class MapDataInternalImpl {

    @SubscribeEvent
    public static void registerDataPackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(MapDataInternal.KEY, MLMapDecorationType.DIRECT_CODEC, MLMapDecorationType.DIRECT_CODEC);
    }

    public static void init() {
        var bus = MoonlightForge.getCurrentBus();
        bus.register(MapDataInternalImpl.class);
    }
}