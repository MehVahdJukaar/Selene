package net.mehvahdjukaar.moonlight.api.misc;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.function.Supplier;

/**
 * Basically a registry object wrapper and simple supplier with id and holder functionality for fabric
 */
public interface RegSupplier<T> extends Supplier<T> {

    @Override
    T get();

    ResourceLocation getId();

    ResourceKey<T> getKey();

    Holder<T> getHolder();

    default boolean is(TagKey<T> tag) {
        return this.getHolder().is(tag);
    }


    class Direct<T> implements RegSupplier<T> {

        private final T obj;
        private final Registry<T> reg;

        public Direct(T obj, Registry<T> reg) {
            this.obj = obj;
            this.reg = reg;
        }

        @Override
        public T get() {
            return this.obj;
        }

        @Override
        public ResourceLocation getId() {
            return this.reg.getKey(this.obj);
        }

        @Override
        public ResourceKey<T> getKey() {
            return this.reg.getResourceKey(this.obj).get();
        }

        @Override
        public Holder<T> getHolder() {
            return this.reg.wrapAsHolder(this.obj);
        }

    }
}
