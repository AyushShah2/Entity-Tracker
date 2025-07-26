package com.orangthegreat;

import com.orangthegreat.menu.ScreenOpener;
import com.orangthegreat.menu.ModMenuScreen;
import com.orangthegreat.utils.Color;

import static com.orangthegreat.utils.ArmorStandHandler.*;
import static com.orangthegreat.utils.Renderer.*;
import com.orangthegreat.utils.ETConfigs;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.*;
import net.minecraft.entity.decoration.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class EntityTrackerClient implements ClientModInitializer {
	public static final String MOD_ID = "Entity Tracker";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static MinecraftClient MC;
	public static List<Entity> entitiesToRender = new ArrayList<>();
	private static final ETConfigs modConfigs = ETConfigs.getInstance();

	@Override
	public void onInitializeClient() {
		modConfigs.loadConfig();
		ScreenOpener.register();

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(literal("etr").executes(context -> {
			if (MC.currentScreen instanceof ChatScreen) MC.setScreen(null);
			if (FabricLoader.getInstance().isModLoaded("modmenu")) ScreenOpener.openNextTick(ModMenuScreen.getMainModScreen(MC.currentScreen));
			else {
				MC.player.sendMessage(Text.literal("ModMenu and Cloth Config are required!").formatted(Formatting.DARK_RED, Formatting.BOLD), false);
                try {
					MC.player.sendMessage(Text.literal("➤ ").append(Text.literal("Click here to download ModMenu").setStyle(Style.EMPTY.withColor(Formatting.AQUA).withUnderline(true).withClickEvent(new ClickEvent.OpenUrl(new URI("https://modrinth.com/mod/modmenu/version/14.0.0-rc.2"))))), false);
                    MC.player.sendMessage(Text.literal("➤ ").append(Text.literal("Click here to download Cloth Config").setStyle(Style.EMPTY.withColor(Formatting.AQUA).withUnderline(true).withClickEvent(new ClickEvent.OpenUrl(new URI("https://modrinth.com/mod/cloth-config/version/18.0.145+fabric"))))), false);
                } catch (URISyntaxException e) {
                    LOGGER.error("Error sending message with click event");
                }
				LOGGER.warn("ModMenu Not installed.");
            }
			return 1;
		})));

		ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (!modConfigs.getSettings().isEnabled() || entity instanceof ProjectileEntity || entity instanceof AbstractDecorationEntity || entity instanceof FallingBlockEntity) return;
			String name = entity.getName().getString();
			if (isUnderFloatingArmorStand(entity, world) || !(entity instanceof LivingEntity)) return;
			modConfigs.getOrCreateSettings(name);
			if (entity instanceof PlayerEntity) modConfigs.updateEntityType(name, true);
			if (modConfigs.getEnabledEntityNames().contains(name)) entitiesToRender.add(entity);
		});

		ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
			if (modConfigs.getSettings().isEnabled()) entitiesToRender.remove(entity);
		});

		ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((client, world) -> {
			modConfigs.removeAllPlayers();
			modConfigs.refresh();
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.world == null || client.player == null) return;
			MC = client;
			if (!(modConfigs.getSettings().isEnabled() && modConfigs.getEnabledEntityNames().contains("Starred Mobs"))) return;
			entitiesToRender.removeIf(e -> !e.isAlive());
			for (Entity entity : client.world.getEntities()) {
				String name = entity.getName().getString();
				if (name.contains("✯") || name.contains("Shadow Assassin") || name.contains("King Midas")) {
					if (entity instanceof ArmorStandEntity) {
						entity = getEntityUnderArmorStand(entity, 1.5);
						entity.setCustomName(Text.of("Starred Mobs"));
					}
					entitiesToRender.add(entity);
				}
			}
		});

		WorldRenderEvents.AFTER_ENTITIES.register(context -> {
			if (!modConfigs.getSettings().isEnabled()) return;
			for (Entity entity : entitiesToRender) {
				if (MC == null || !(entity instanceof LivingEntity)) continue;
				String name = entity.getName().getString();
				try {
					if (entity.getCustomName() != null && "Starred Mobs".equals(entity.getCustomName().getString())) name = "Starred Mobs";
				} catch (Exception e) {
					LOGGER.info("Custom Name could not be read", e);
				}
				Color c = Color.convertHextoRGB(modConfigs.getOrCreateSettings(name).color);
				switch (modConfigs.getOrCreateSettings(name).renderMode) {
					case "Fill Hitbox" -> drawFill3DBox(context.matrixStack(), context.camera(), entity.getBoundingBox(), c, 1.0f, MC);
					case "Fill Entity" -> drawEntityModel(context.matrixStack(), context.camera(), 1.0f, entity, c, MC);
					case "Hitbox" -> draw3DBox(context.matrixStack(), context.camera(), entity.getBoundingBox(), c, 1.0f, MC);
				}
			}
		});

		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			modConfigs.removeAllPlayers();
			modConfigs.refresh();
		});
	}
}
