package com.titan1um.adaptivemusic.client.modmenu;

import com.titan1um.adaptivemusic.client.gui.AdaptiveMusicConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public final class AdaptiveMusicModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return AdaptiveMusicConfigScreen::create;
    }
}
