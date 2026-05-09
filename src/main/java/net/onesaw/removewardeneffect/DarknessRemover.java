package net.onesaw.removewardeneffect;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class DarknessRemover implements ClientModInitializer {
	public static final String MOD_ID = "removewardeneffect";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final Path CONFIG_PATH = FabricLoader.getInstance()
			.getConfigDir()
			.resolve(MOD_ID + ".properties");

	private boolean nullifyDarkness = true;
	private boolean nullifyBlindness = true;

	@Override
	public void onInitializeClient() {
		loadConfig();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player != null) {
				if (nullifyDarkness && hasDarkness(client.player.getEffect(MobEffects.DARKNESS))) {
					client.player.removeEffect(MobEffects.DARKNESS);
				}

				if (nullifyBlindness && hasBlindness(client.player.getEffect(MobEffects.BLINDNESS))) {
					client.player.removeEffect(MobEffects.BLINDNESS);
				}
			}
		});
	}

	private void loadConfig() {
		Properties properties = new Properties();
		properties.setProperty("nullifyDarkness", Boolean.toString(nullifyDarkness));
		properties.setProperty("nullifyBlindness", Boolean.toString(nullifyBlindness));

		if (Files.exists(CONFIG_PATH)) {
			try (InputStream input = Files.newInputStream(CONFIG_PATH)) {
				properties.load(input);
			} catch (IOException exception) {
				LOGGER.warn("Failed to read config file, using defaults: {}", CONFIG_PATH, exception);
			}
		}

		nullifyDarkness = Boolean.parseBoolean(properties.getProperty("nullifyDarkness", "true"));
		nullifyBlindness = Boolean.parseBoolean(properties.getProperty("nullifyBlindness", "true"));

		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			try (OutputStream output = Files.newOutputStream(CONFIG_PATH)) {
				properties.setProperty("nullifyDarkness", Boolean.toString(nullifyDarkness));
				properties.setProperty("nullifyBlindness", Boolean.toString(nullifyBlindness));
				properties.store(output, "Remove Warden Effect config");
			}
		} catch (IOException exception) {
			LOGGER.warn("Failed to write config file: {}", CONFIG_PATH, exception);
		}
	}

	private static boolean hasDarkness(MobEffectInstance effect) {
		return effect != null;
	}

	private static boolean hasBlindness(MobEffectInstance effect) {
		return effect != null;
	}
}
