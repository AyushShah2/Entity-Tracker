package com.orangthegreat;

import com.orangthegreat.menu.ScreenOpener;
import com.orangthegreat.menu.ModMenuScreen;
import com.orangthegreat.utils.Color;
import com.orangthegreat.utils.ETConfigs;
import static com.orangthegreat.utils.Renderer.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.ClientStopping;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

public class EntityTrackerClient implements ClientModInitializer {
	public static final String MOD_ID = "Entity Tracker";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static MinecraftClient MC;
	public static List<Entity> entitiesToRender = new ArrayList<>();
	private static final ETConfigs configs = ETConfigs.getInstance();

	@Override
	public void onInitializeClient() {
		configs.loadConfig();
		configs.getLoadedEntities().add("Starred Mobs");
		ScreenOpener.register();

		//checking for the command /etr
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(
					literal("etr").executes(context -> {
						if (FabricLoader.getInstance().isModLoaded("modmenu")) {
							ScreenOpener.openNextTick(ModMenuScreen.getMainModScreen(MinecraftClient.getInstance().currentScreen));
						} else {
							LOGGER.info("ModMenu not found");
						}
						return 1;
					})
			);
		});

		//getting all entities being loaded
		ClientEntityEvents.ENTITY_LOAD.register(new ClientEntityEvents.Load() {
			@Override
			public void onLoad(Entity entity, ClientWorld clientWorld) {
				if (!configs.getSettings().isEnabled()) return;
				if(entity instanceof ProjectileEntity) return;
				final String entityName = entity.getName().getString();

				if (configs.getEnabledEntities().contains("Starred Mobs") && (entity instanceof LivingEntity) && (entityName.contains("✯") || entityName.contains("Shadow Assassin") || entityName.contains("King Midas") || entityName.contains("?"))){
					entitiesToRender.add(entity);
					return;
				}

				if(entity.isControlledByPlayer() && (entity instanceof LivingEntity)){
					configs.getPlayersList().add(entityName);
					if(configs.getEnabledEntities().contains(entityName)) entitiesToRender.add(entity);
					return;
				}

				configs.getLoadedEntities().add(entityName);
				if(configs.getEnabledEntities().contains(entityName)) entitiesToRender.add(entity);
			}
		});

		//removing entities from render list if it is not loaded
		ClientEntityEvents.ENTITY_UNLOAD.register(new ClientEntityEvents.Unload() {
			@Override
			public void onUnload(Entity entity, ClientWorld clientWorld) {
				if (!configs.getSettings().isEnabled()) return;
				entitiesToRender.remove(entity);
			}
		});

		//setting minecraft client
		ClientTickEvents.END_CLIENT_TICK.register(new ClientTickEvents.EndTick() {
			//private boolean pendingOpen = false;
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
				if (!configs.getSettings().isEnabled()) return;
				for (Entity entity : entitiesToRender){
					if (MC == null) return;
					entity = (entity instanceof ArmorStandEntity) ? getEntityUnderArmorStand(entity, 1.5) : entity; //useful when servers put nametags on invis armor stands above entities
					if(!entity.isLiving()) return;

					String entityName = entity.getName().getString();

					Color entityColor = Color.convertHextoRGB(configs.getColorOfEnabled().get(configs.getEnabledEntities().indexOf(entityName)));
					String renderMode = configs.getRenderModes().getOrDefault(entityName, "Hitbox");

					switch (renderMode) {
						case "Fill Hitbox":
							drawFill3DBox(context.matrixStack(), context.camera(), entity.getBoundingBox(), entityColor,1.0f, MC);
							break;
						case "Fill Entity":
							drawEntityModel(context.matrixStack(), context.camera(), 1.0f, entity, entityColor, MC);
							break;
						case "Hitbox":
							draw3DBox(context.matrixStack(), context.camera(), entity.getBoundingBox(), entityColor,1.0f, MC);
							break;
						default:
							break;
					}

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

	public static Entity getEntityUnderArmorStand(Entity armorStand, double maxDistance) {
		if (armorStand == null || armorStand.getWorld() == null) return armorStand;

		// Define a bounding box slightly below the armor stand
		return armorStand.getWorld().getOtherEntities(armorStand,
						armorStand.getBoundingBox().expand(0.5, maxDistance, 0.5),
						entity -> entity != armorStand && entity instanceof LivingEntity && !(entity instanceof net.minecraft.entity.decoration.ArmorStandEntity)
				).stream()
				.min((a, b) -> Double.compare(a.getY(), b.getY())) // Pick the one closest beneath
				.orElse(armorStand);
	}
}