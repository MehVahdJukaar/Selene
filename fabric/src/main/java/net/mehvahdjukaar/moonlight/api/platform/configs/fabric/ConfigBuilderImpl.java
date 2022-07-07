package net.mehvahdjukaar.moonlight.api.platform.configs.fabric;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.mehvahdjukaar.moonlight.api.client.language.LangBuilder;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;
import net.mehvahdjukaar.moonlight.api.platform.configs.fabric.values.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ConfigBuilderImpl extends ConfigBuilder {

    public static ConfigBuilder create(ResourceLocation name, ConfigType type) {
        return new ConfigBuilderImpl(name, type);
    }

    private final ConfigCategory mainCategory = new ConfigCategory(this.getName());

    private final Stack<ConfigCategory> categoryStack = new Stack<>();

    public ConfigBuilderImpl(ResourceLocation name, ConfigType type) {
        super(name, type);
        categoryStack.push(mainCategory);
    }

    @Override
    public ConfigSpec buildAndRegister() {
        ConfigSpec spec = build();
        ConfigSpec.saveSpec(spec, type);
        return spec;
    }

    @NotNull
    public ConfigSpec build() {
        assert categoryStack.size() == 1;
        ConfigSpec spec = new ConfigSpec(new ResourceLocation(this.getModId(), this.getName()),
                mainCategory, this.getFileName());
        spec.loadConfig();
        spec.saveConfig();
        return spec;
    }

    @Override
    protected String currentCategory() {
        return categoryStack.peek().getName();
    }

    @Override
    public ConfigBuilderImpl push(String translation) {
        var cat = new ConfigCategory(translation);
        categoryStack.peek().addEntry(cat);
        categoryStack.push(cat);
        return this;
    }

    @Override
    public ConfigBuilderImpl pop() {
        assert categoryStack.size()!=1;
        categoryStack.pop();
        return this;
    }

    private void doAddConfig(String name, ConfigValue<?> config) {
        config.setDescriptionKey(this.tooltipKey(name));
        config.setTranslationKey(this.translationKey(name));
        maybeAddComment(name);
        this.categoryStack.peek().addEntry(config);
    }


    @Override
    public Supplier<Boolean> define(String name, boolean defaultValue) {
        var config = new BoolConfigValue(name, defaultValue);
        doAddConfig(name, config);
        return config;
    }


    @Override
    public Supplier<Double> define(String name, double defaultValue, double min, double max) {
        var config = new DoubleConfigValue(name, defaultValue, min, max);
        doAddConfig(name, config);
        return config;
    }

    @Override
    public Supplier<Integer> define(String name, int defaultValue, int min, int max) {
        var config = new IntConfigValue(name, defaultValue, min, max);
        doAddConfig(name, config);
        return config;
    }

    @Override
    public Supplier<String> define(String name, String defaultValue, Predicate<Object> validator) {
        var config = new StringConfigValue(name, defaultValue, validator);
        doAddConfig(name, config);
        return config;
    }

    @Override
    public <T extends String> Supplier<List<String>> define(String name, List<? extends T> defaultValue, Predicate<Object> predicate){
        var config = new ListStringConfigValue<>(name, (List<String>) defaultValue, predicate);
        doAddConfig(name, config);
        return config;
    }

    @Override
    public <V extends Enum<V>> Supplier<V> define(String name, V defaultValue) {
        var config = new EnumConfigValue<>(name, defaultValue);
        doAddConfig(name, config);
        return config;
    }

    @Override
    public <T> Supplier<List<? extends T>> defineForgeList(String path, List<? extends T> defaultValue, Predicate<Object> elementValidator) {
        return ()-> defaultValue;
    }

    @Override
    protected void maybeAddComment(String name) {
        comments.put(this.translationKey(name), LangBuilder.getReadableName(name));
        super.maybeAddComment(name);
    }
}
