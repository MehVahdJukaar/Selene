package net.mehvahdjukaar.moonlight.api.platform.configs;

public enum ConfigType {
    COMMON, COMMON_SYNCED, CLIENT;

    public boolean isSynced() {
        return this == COMMON_SYNCED;
    }

    public String getDefaultName() {
        if (this == CLIENT) return "client";
        else return "common";
    }
}
