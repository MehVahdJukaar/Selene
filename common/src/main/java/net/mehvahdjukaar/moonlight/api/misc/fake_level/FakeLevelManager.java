package net.mehvahdjukaar.moonlight.api.misc.fake_level;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;

public class FakeLevelManager {

    protected static final Map<String, Level> INSTANCES = new Object2ObjectArrayMap<>();

    @ApiStatus.Internal
    @VisibleForTesting
    public static Collection<Level> invalidateAll() {
        var unloaded = new ArrayList<>(INSTANCES.values());
        new ArrayList<>(INSTANCES.keySet()).forEach(FakeLevelManager::invalidate);
        return unloaded;
    }

    @ApiStatus.Internal
    public static void invalidate(String name) {
        Level level = INSTANCES.remove(name);
        try {
            level.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static FakeLevel getDefaultClient(Level original) {
        return getClient("dummy_world", original, FakeLevel::new);
    }

    public static <T extends FakeLevel> T getClient(String id, Level original, BiFunction<String, RegistryAccess, FakeLevel> constructor) {
        id = "client_" + id;
        String finalId = id;
        return (T) INSTANCES.computeIfAbsent(id, k -> constructor.apply(finalId, original.registryAccess()));
    }


    public static FakeServerLevel getDefaultServer(ServerLevel original) {
        return getServer("dummy_world", original, FakeServerLevel::new);
    }

    public static <T extends FakeServerLevel> T getServer(String id, ServerLevel original, BiFunction<String, ServerLevel, FakeServerLevel> constructor) {
        id = "server_" + id;
        String finalId = id;
        return (T) INSTANCES.computeIfAbsent(id, k -> constructor.apply(finalId, original));
    }

    public static Level get(String id, Level original,
                            BiFunction<String, RegistryAccess, FakeLevel> clientConstr,
                            BiFunction<String, ServerLevel, FakeServerLevel> serverConstr) {
        if (original instanceof ServerLevel sl) {
            return getServer(id, sl, serverConstr);
        } else {
            return getClient(id, original, clientConstr);
        }
    }

    public static Level getDefault(Level original) {
        if (original instanceof ServerLevel sl) {
            return getDefaultServer(sl);
        } else {
            return getDefaultClient(original);
        }
    }

    public interface ILevelLike {
        Level cast();
    }
}
