
package dev.smtcify.mod;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public final class SmtcifyHud {
    private static final int X = 8;
    private static final int Y = 8;
    private static final int WIDTH = 224;
    private static final int HEIGHT = 78;

    private SmtcifyHud() {
    }

    public static void register() {
        SmtcifyMediaClient.start();

        HudElementRegistry.addLast(
                Identifier.fromNamespaceAndPath(
                        "smtcify",
                        "media_hud"
                ),
                SmtcifyHud::render
        );
    }

    private static void render(
            net.minecraft.client.gui.GuiGraphicsExtractor graphics,
            net.minecraft.client.DeltaTracker deltaTracker
    ) {
        System.out.println("[SMTCify] Renderizando HUD");
        if (!SmtcifySettings.hudEnabled) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.font == null) {
            return;
        }

        SmtcifyMediaClient.MediaInfo media =
                SmtcifyMediaClient.getCurrent();

        int opacity = Math.max(
                0,
                Math.min(100, SmtcifySettings.backgroundOpacity)
        );

        int alpha = Math.round(opacity * 255.0f / 100.0f);
        int background = (alpha << 24);

        // Fondo oscuro y una línea de acento.
        graphics.fill(
                X, Y, X + WIDTH, Y + HEIGHT,
                background
        );

        graphics.fill(
                X, Y, X + 3, Y + HEIGHT,
                0xFF55AAFF
        );

        int textX = X + 11;
        int white = 0xFFFFFFFF;
        int muted = 0xFFB8B8B8;

        graphics.text(
                minecraft.font,
                "SMTCify",
                textX, Y + 7,
                0xFF55AAFF
        );

        if (!media.available) {
            graphics.text(
                    minecraft.font,
                    "Sin reproducción activa",
                    textX, Y + 25,
                    white
            );

            graphics.text(
                    minecraft.font,
                    media.message,
                    textX, Y + 42,
                    muted
            );

            return;
        }

        graphics.text(
                minecraft.font,
                media.title,
                textX, Y + 23,
                white
        );

        graphics.text(
                minecraft.font,
                media.artist,
                textX, Y + 39,
                muted
        );

        String album = media.album.isBlank()
                ? media.source
                : media.album;

        graphics.text(
                minecraft.font,
                album,
                textX, Y + 54,
                muted
        );

        graphics.text(
                minecraft.font,
                media.status,
                X + WIDTH - 60, Y + 7,
                white
        );
    }
}
