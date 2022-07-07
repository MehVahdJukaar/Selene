package net.mehvahdjukaar.moonlight.api.platform.configs.fabric;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ConfigSpec {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Map<String, Map<ConfigType, ConfigSpec>> CONFIG_STORAGE = new HashMap<>();

    public static void saveSpec(ConfigSpec spec, ConfigType type) {
        String modId = spec.getName().getNamespace();
        var map = CONFIG_STORAGE.computeIfAbsent(modId, n -> new HashMap<>());
        map.put(type, spec);
    }

    @Nullable
    public static ConfigSpec getSpec(String modId, ConfigType type) {
        var map = CONFIG_STORAGE.get(modId);
        if (map != null) {
            return map.getOrDefault(type, null);
        }
        return null;
    }

    private final ConfigCategory mainEntry;

    private final ResourceLocation name;
    private final File file;

    public ConfigSpec(ResourceLocation name, ConfigCategory mainEntry, String filePath) {
        this.name = name;
        this.mainEntry = mainEntry;
        this.file = new File(FabricLoader.getInstance().getConfigDir().toFile(), filePath);
    }


    public ConfigCategory getMainEntry() {
        return mainEntry;
    }

    public ResourceLocation getName() {
        return name;
    }


    public void loadConfig() {
        JsonElement config = null;


        if (file.exists() && file.isFile()) {
            try (FileInputStream fileInputStream = new FileInputStream(file);
                 InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                 BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

                config = GSON.fromJson(bufferedReader, JsonElement.class);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load config", e);
            }
        }

        if (config instanceof JsonObject jo) {
            //dont call load directly so we skip the main category name
            mainEntry.getEntries().forEach(e -> e.loadFromJson(jo));
        }
    }

    public void saveConfig() {
        try (FileOutputStream stream = new FileOutputStream(this.file);
             Writer writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {

            JsonObject jo = new JsonObject();
            mainEntry.getEntries().forEach(e -> e.saveToJson(jo));

            GSON.toJson(jo, writer);
        } catch (IOException ignored) {
        }
    }

    public String getTitleKey() {
        return "config." + this.getName().toLanguageKey();
    }
}
