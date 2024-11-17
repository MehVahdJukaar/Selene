package net.mehvahdjukaar.moonlight.core.network.fabric;

import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.fabric.DataMapBridge;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class ClientBoundSyncDataMapsPacket implements Message {

    public static final TypeAndCodec<RegistryFriendlyByteBuf, ClientBoundSyncDataMapsPacket> TYPE = Message.makeType(
            Moonlight.res("s2c_sync_data_map"), ClientBoundSyncDataMapsPacket::new);
    private final DataMapBridge<?, ?> dataMapBridge;

    public ClientBoundSyncDataMapsPacket(DataMapBridge<?, ?> dataMapBridge) {
        this.dataMapBridge = dataMapBridge;
    }

    public ClientBoundSyncDataMapsPacket(RegistryFriendlyByteBuf buf) {
        String path = buf.readUtf();
        this.dataMapBridge = DataMapBridge.FACTORIES.get(path).apply(buf.registryAccess());
        this.dataMapBridge.decode(buf);
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(this.dataMapBridge.path);
        this.dataMapBridge.encode(buf);
    }

    @Override
    public void handle(Context context) {
        this.dataMapBridge.applyData();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE.type();
    }
}
