package com.orangthegreat;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.orangthegreat.menu.ModMenuScreen;
import com.orangthegreat.utils.Color;
import com.orangthegreat.utils.ETConfigs;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
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


import java.util.ArrayList;
import java.util.List;

import static com.orangthegreat.utils.Renderer.*;
import static net.minecraft.server.command.CommandManager.literal;

public class EntityTrackerClient implements ClientModInitializer {
	public static final String MOD_ID = "entity-tracker";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static MinecraftClient MC;
	private static List<Entity> entitiesToRender = new ArrayList<>();
	private static final ETConfigs configs = ETConfigs.getInstance();
	public static String currentColor = "#378614";


	@Override
	public void onInitializeClient() {
		configs.loadConfig();

		//checking for the command /etr
		CommandRegistrationCallback.EVENT.register(new CommandRegistrationCallback() {
			@Override
			public void register(CommandDispatcher<ServerCommandSource> commandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
				commandDispatcher.register(literal("etr").executes(new Command<ServerCommandSource>() {
					@Override
					public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
						//function that opens up the mod menu
						if (FabricLoader.getInstance().isModLoaded("modmenu")) {
							MinecraftClient.getInstance().execute(() -> {
								MinecraftClient.getInstance().setScreen(ModMenuScreen.getMainModScreen(MinecraftClient.getInstance().currentScreen));
							});
						} else {
							LOGGER.info("ModMenu not found");
						}
						LOGGER.info("YOU ENTERED THE COMMAND");
						return 0;
					}
				}));
			}
		});


		//getting all entities being loaded
		ClientEntityEvents.ENTITY_LOAD.register(new ClientEntityEvents.Load() {
			@Override
			public void onLoad(Entity entity, ClientWorld clientWorld) {
				configs.getLoadedEntities().add(entity.getName().getString());
				LOGGER.info(entity.getName().getString());
				if(configs.getEnabledEntities().contains(entity.getName().getString())) entitiesToRender.add(entity);
			}
		});

		//removing entities from render list if it is not loaded
		ClientEntityEvents.ENTITY_UNLOAD.register(new ClientEntityEvents.Unload() {
			@Override
			public void onUnload(Entity entity, ClientWorld clientWorld) {
				entitiesToRender.remove(entity);
			}
		});

		//setting minecraft client
		ClientTickEvents.END_CLIENT_TICK.register(new ClientTickEvents.EndTick() {
			@Override
			public void onEndTick(MinecraftClient client) {
				if (client.world == null || client.player == null) return;
				MC = client;
			}
		});

		//rendering command here
		WorldRenderEvents.AFTER_ENTITIES.register(new WorldRenderEvents.AfterEntities() {
			@Override
			public void afterEntities(WorldRenderContext context) {
				for (Entity entity : entitiesToRender){
					if (MC == null) return;
					drawEntityModel(context.matrixStack(), context.camera(), 1.0f, entity, Color.convertHextoRGB(currentColor), MC);
					//draw3DBox(context.matrixStack(), context.camera(), entity.getBoundingBox(), new Color(196, 186, 181),1.0f, MC);
				}
			}
		});

		//when exiting event
		ClientLifecycleEvents.CLIENT_STOPPING.register(new ClientStopping() {
		   @Override
		   public void onClientStopping(MinecraftClient minecraftClient) {
			   configs.saveConfig();
		   }
		});


	}

}