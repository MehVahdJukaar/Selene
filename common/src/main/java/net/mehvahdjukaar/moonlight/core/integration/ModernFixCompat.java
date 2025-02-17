package net.mehvahdjukaar.moonlight.core.integration;

import org.embeddedt.modernfix.core.ModernFixMixinPlugin;

public class ModernFixCompat {

    public static boolean areLazyResourcesOn(){
        return ModernFixMixinPlugin.instance.isOptionEnabled("perf.dynamic_resources.SomeDummyClassNameHere");
    }
}
