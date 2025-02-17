package net.mehvahdjukaar.moonlight.api.integration;

import net.irisshaders.iris.Iris;
import net.irisshaders.iris.pipeline.ShaderRenderingPipeline;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;

@Deprecated(forRemoval = true)
public class IrisCompat {

    public static boolean isIrisShaderFuckerActive() {
        return net.mehvahdjukaar.moonlight.core.integration.IrisCompat.isIrisShaderStuffActive();
    }

}
