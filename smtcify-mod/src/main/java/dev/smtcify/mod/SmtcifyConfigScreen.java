
package dev.smtcify.mod;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class SmtcifyConfigScreen extends Screen {
    private final Screen parent;

    public SmtcifyConfigScreen(Screen parent) {
        super(Component.literal("SMTCify Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int buttonWidth = 220;
        int buttonHeight = 20;
        int x = (this.width - buttonWidth) / 2;
        int y = this.height / 4;

        this.addRenderableWidget(Button.builder(
                hudLabel(),
                button -> {
                    SmtcifySettings.hudEnabled =
                            !SmtcifySettings.hudEnabled;
                    button.setMessage(hudLabel());
                })
                .bounds(x, y, buttonWidth, buttonHeight)
                .build());

        this.addRenderableWidget(Button.builder(
                chatLabel(),
                button -> {
                    SmtcifySettings.chatNotifications =
                            !SmtcifySettings.chatNotifications;
                    button.setMessage(chatLabel());
                })
                .bounds(x, y + 26, buttonWidth, buttonHeight)
                .build());

        this.addRenderableWidget(Button.builder(
                opacityLabel(),
                button -> {
                    SmtcifySettings.backgroundOpacity += 10;

                    if (SmtcifySettings.backgroundOpacity > 90) {
                        SmtcifySettings.backgroundOpacity = 30;
                    }

                    button.setMessage(opacityLabel());
                })
                .bounds(x, y + 52, buttonWidth, buttonHeight)
                .build());

        this.addRenderableWidget(Button.builder(
                Component.literal("Done"),
                button -> this.onClose())
                .bounds(x, y + 84, buttonWidth, buttonHeight)
                .build());
    }

    private static Component hudLabel() {
        return Component.literal(
                "HUD: " + (SmtcifySettings.hudEnabled ? "ON" : "OFF"));
    }

    private static Component chatLabel() {
        return Component.literal(
                "Chat notifications: "
                        + (SmtcifySettings.chatNotifications ? "ON" : "OFF"));
    }

    private static Component opacityLabel() {
        return Component.literal(
                "Background opacity: "
                        + SmtcifySettings.backgroundOpacity + "%");
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.parent);
    }
}
