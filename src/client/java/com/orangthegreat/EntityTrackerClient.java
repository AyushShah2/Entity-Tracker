package com.orangthegreat;


import com.orangthegreat.utils.BetterArrayList;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.world.ClientWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndWorldTick;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.ClientStopping;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import net.fabricmc.loader.api.FabricLoader;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class EntityTrackerClient implements ClientModInitializer {
	public static final String MOD_ID = "entity-tracker";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static BetterArrayList loadedEntities = new BetterArrayList();
	private static List<String> currentEntities = new ArrayList<>();
	private static BetterArrayList enabledEntities = new BetterArrayList();

	@Override
	public void onInitializeClient() {
		LOGGER.info("loading...");
		Path loadFilePath = FabricLoader.getInstance().getConfigDir().resolve("loadedEntities.txt");
		Path enabledFilePath = FabricLoader.getInstance().getConfigDir().resolve("enabledEntities.txt");

		loadedEntities.loadListFromFile(loadFilePath);
		enabledEntities.loadListFromFile(enabledFilePath);

		//getting all entities being loaded
		ClientEntityEvents.ENTITY_LOAD.register(new ClientEntityEvents.Load() {
			@Override
			public void onLoad(Entity entity, ClientWorld clientWorld) {
				loadedEntities.add(entity.getName().getString());
				LOGGER.info(entity.getName().getString());
			}
		});

		ClientLifecycleEvents.CLIENT_STOPPING.register(new ClientStopping() {
		   @Override
		   public void onClientStopping(MinecraftClient minecraftClient) {
			   LOGGER.info("saving...");
			   loadedEntities.saveListToFile(loadFilePath);
			   enabledEntities.saveListToFile(enabledFilePath);
		   }
		});


	}

}