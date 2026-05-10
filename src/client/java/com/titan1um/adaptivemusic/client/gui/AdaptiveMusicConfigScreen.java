package com.titan1um.adaptivemusic.client.gui;

import com.titan1um.adaptivemusic.client.config.AdaptiveMusicConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class AdaptiveMusicConfigScreen extends Screen {
    private final Screen parent;
    private final AdaptiveMusicConfig config;
    private final List<TextFieldWidget> musicFields = new ArrayList<>();

    private AdaptiveMusicConfigScreen(Screen parent) {
        super(Text.translatable("screen.adaptivemusic.config"));
        this.parent = parent;
        this.config = AdaptiveMusicConfig.get();
    }

    public static Screen create(Screen parent) {
        return new AdaptiveMusicConfigScreen(parent);
    }

    @Override
    protected void init() {
        int center = width / 2;
        int y = 42;
        addDrawableChild(ButtonWidget.builder(enabledText(), button -> {
            config.toggleEnabled();
            button.setMessage(enabledText());
        }).dimensions(center - 155, y, 150, 20).build());
        addDrawableChild(ButtonWidget.builder(debugText(), button -> {
            config.toggleDebugToasts();
            button.setMessage(debugText());
        }).dimensions(center + 5, y, 150, 20).build());

        y += 32;
        musicFields.clear();
        for (Map.Entry<String, String> entry : config.music.entrySet()) {
            TextFieldWidget field = new TextFieldWidget(textRenderer, center - 20, y, 175, 20, Text.literal(entry.getKey()));
            field.setText(entry.getValue());
            field.setMaxLength(160);
            field.setChangedListener(value -> config.music.put(entry.getKey(), value));
            musicFields.add(field);
            addDrawableChild(field);
            y += 24;
        }

        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), button -> close()).dimensions(center - 75, height - 28, 150, 20).build());
    }

    @Override
    public void close() {
        config.save();
        MinecraftClient.getInstance().setScreen(parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 14, 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, Text.translatable("screen.adaptivemusic.subtitle"), width / 2, 27, 0xA0A0A0);

        int y = 78;
        for (String mood : config.music.keySet()) {
            context.drawTextWithShadow(textRenderer, mood, width / 2 - 155, y + 6, 0xFFFFFF);
            y += 24;
        }
        super.render(context, mouseX, mouseY, delta);
    }

    private Text enabledText() {
        return Text.translatable(config.enabled ? "option.adaptivemusic.enabled.on" : "option.adaptivemusic.enabled.off");
    }

    private Text debugText() {
        return Text.translatable(config.showDebugToasts ? "option.adaptivemusic.debug.on" : "option.adaptivemusic.debug.off");
    }
}
