package io.github.cottonmc.cotton.gui.impl.client;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class LibGuiConfig {
	public static ForgeConfigSpec.BooleanValue DARK_MODE_CONFIG;

	public static void register() {
		registerClientConfigs();
	}

	private static void registerClientConfigs() {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.comment("Client settings for the power generator").push("powergen");

		DARK_MODE_CONFIG = builder
				.comment("Whether dark mode should be enabled. Will only affect Vanilla-styled GUIs.")
				.define("darkMode", false);

		builder.pop();
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, builder.build());
	}

	public static ForgeConfigSpec.BooleanValue darkMode() {
		return DARK_MODE_CONFIG;
	}
}
