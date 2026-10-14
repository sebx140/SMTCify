
package dev.smtcify.mod;

import net.fabricmc.api.ClientModInitializer;

public final class SmtcifyMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SmtcifyHud.register();

        System.out.println("[SMTCify] Mod inicializado.");
    }
}
