package dev.smtcify.mod;

import net.fabricmc.api.ClientModInitializer;

public final class SmtcifyMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        System.out.println("[SMTCify] Mod inicializado.");
    }
}
