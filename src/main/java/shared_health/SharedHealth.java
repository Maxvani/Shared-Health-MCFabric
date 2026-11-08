package shared_health;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resources.Identifier;

import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SharedHealth implements ModInitializer {
	public static final String MOD_ID = "shared_health";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static MinecraftServer serverInstance = null;

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Shared Health");

		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			serverInstance = server;
		});

		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			serverInstance = null; // Prevent memory leaks
		});
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public static MinecraftServer getServerInstance() {
		return serverInstance;
	}
}
