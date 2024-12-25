package net.mehvahdjukaar.moonlight.core.misc;

import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class McMetaUtils {

    private static List<Field> FIELDS = null;
    private static final int VANILLA_FIELDS = 5;

    public static void copyAllMixinAddedFields(AnimationMetadataSection from, AnimationMetadataSection to) {
        if (FIELDS == null) {
            FIELDS = new ArrayList<>();
            Field[] f = AnimationMetadataSection.class.getDeclaredFields();
            for (int i = 0; i < f.length; i++) {
                if (i > VANILLA_FIELDS - 1) {
                    Field field = f[i];
                    FIELDS.add(field);
                    field.setAccessible(true);
                }
            }
        }
        for (Field field : FIELDS) {
            try {
                field.set(to, field.get(from));
            } catch (IllegalAccessException e) {
               throw new RuntimeException(e);
            }
        }
    }
}
