package net.mehvahdjukaar.moonlight.api.misc;

import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.function.Predicate;

public class HolderReference<T> {

    private final ResourceKey<Registry<T>> registryKey;
    private final ResourceKey<T> key;

    protected HolderReference(ResourceKey<Registry<T>> registryKey, ResourceKey<T> key) {
        this.registryKey = registryKey;
        this.key = key;
    }

    public static <A> HolderReference<A> of(String id, ResourceKey<Registry<A>> registry) {
        return of(ResourceLocation.tryParse(id), registry);
    }

    public static <A> HolderReference<A> of(ResourceLocation location, ResourceKey<Registry<A>> registry) {
        return new HolderReference<>(registry, ResourceKey.create(registry, location));
    }

    public static <A> HolderReference<A> of(ResourceKey<A> key) {
        return new HolderReference<>(ResourceKey.createRegistryKey(key.registry()), key);
    }

    public T getUnsafe() {
        return get(Utils.hackyGetRegistryAccess());
    }

    public T get(Level level) {
        return get(level.registryAccess());
    }

    public T get(HolderLookup.Provider r) {
        return getHolder(r).value();
    }

    public Holder<T> getHolderUnsafe() {
        return getHolder(Utils.hackyGetRegistryAccess());
    }

    public Holder<T> getHolder(Level level) {
        return getHolder(level.registryAccess());
    }

    public Holder<T> getHolder(HolderLookup.Provider r) {
        var lookupReg = r.lookup(registryKey);
        var reg = lookupReg.get();
        try {
            return reg.getOrThrow(key);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get object from registry: " + key +
                    ".\nCalled from " + Thread.currentThread() + ".\n" +
                    "Registry content was: " + reg.listElements().map(b -> b.key().location()).toList(), e);
        }
    }

    public String getRegisteredName() {
        return key.location().toString();
    }

    public ResourceLocation getID() {
        return key.location();
    }

    public ResourceKey<T> getKey() {
        return key;
    }

    public boolean is(ResourceLocation location) {
        return registryKey.location().equals(location);
    }

    public boolean is(ResourceKey<T> resourceKey) {
        return resourceKey == key;
    }

    public boolean is(Predicate<ResourceKey<T>> predicate) {
        return predicate.test(key);
    }

    public boolean is(Holder<T> other) {
        return other.unwrapKey().get() == key;
    }

    @Override
    public String toString() {
        return "DynamicHolder{" + key + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HolderReference<?> that)) return false;
        return Objects.equals(registryKey, that.registryKey) && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registryKey, key);
    }
}

