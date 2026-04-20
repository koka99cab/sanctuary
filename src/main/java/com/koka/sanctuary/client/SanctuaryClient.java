package com.koka.sanctuary.client;

import com.koka.sanctuary.client.gui.SanctuaryCustomizeScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.world.LevelScreenProvider;

public final class SanctuaryClient implements ClientModInitializer {
	public static final LevelScreenProvider SANCTUARY_LEVEL_SCREEN_PROVIDER = SanctuaryCustomizeScreen::new;

	@Override
	public void onInitializeClient() {
	}
}
