package com.orangthegreat.testing;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.orangthegreat.utils.ArmorStandHandler.isUnderFloatingArmorStand;

public class EntityInfo {
    public void getEntityInfo(PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult, MinecraftClient MC){
        List<String> lines = new ArrayList<>();

        try {
            lines.add("§6--- Entity Info ---");
            lines.add("§eName: §f" + safe(() -> entity.getName().getString()));
            lines.add("§eDisplay Name: §f" + safe(() -> entity.getDisplayName().getString()));
            lines.add("§eType: §f" + safe(() -> entity.getType().toString()));
            lines.add("§eUUID: §f" + entity.getUuidAsString());
            lines.add("§eClass: §f" + entity.getClass().getSimpleName());
            lines.add("§eIs Alive: §f" + entity.isAlive());
            lines.add("§eIs Removed: §f" + entity.isRemoved());

            // Position and movement
            lines.add("§ePosition: §f" + entity.getX() + ", " + entity.getY() + ", " + entity.getZ());
            lines.add("§eVelocity: §f" + entity.getVelocity());
            lines.add("§eYaw / Pitch: §f" + entity.getYaw() + " / " + entity.getPitch());

            // Visual state
            lines.add("§eInvisible: §f" + entity.isInvisible());
            lines.add("§eGlowing: §f" + entity.isGlowing());
            lines.add("§eSilent: §f" + entity.isSilent());
            lines.add("§eHas No Gravity: §f" + entity.hasNoGravity());

            // Name and custom name
            lines.add("§eCustom Name Visible: §f" + entity.isCustomNameVisible());
            lines.add("§eHas Custom Name: §f" + (entity.hasCustomName() ? entity.getCustomName().getString() : "false"));

            // Players and latency
            if (MC.getNetworkHandler() != null) {
                PlayerListEntry self = MC.getNetworkHandler().getPlayerListEntry(player.getUuid());
                if (self != null) {
                    lines.add("§eYour Ping: §f" + self.getLatency() + "ms");
                }

                PlayerListEntry targetEntry = MC.getNetworkHandler().getPlayerListEntry(entity.getUuid());
                if (targetEntry != null) {
                    lines.add("§eTarget Ping: §f" + targetEntry.getLatency() + "ms");
                }
            }

            // Passengers
            if (entity.hasVehicle()) {
                lines.add("§eRiding: §f" + entity.getVehicle().getName().getString());
            }
            if (!entity.getPassengerList().isEmpty()) {
                lines.add("§ePassengers:");
                for (Entity passenger : entity.getPassengerList()) {
                    lines.add("§7  - " + passenger.getName().getString() + " (" + passenger.getType() + ")");
                }
            }

            // If it's a living entity
            if (entity instanceof LivingEntity living && world instanceof ClientWorld clientWorld) {
                lines.add("§eHealth: §f" + living.getHealth() + " / " + living.getMaxHealth());
                lines.add("§eIs Dead: §f" + living.isDead());
                lines.add("§eIs Sleeping: §f" + living.isSleeping());
                lines.add("§eIs Blocking: §f" + living.isBlocking());
                lines.add("§eActive Effects: §f" + living.getStatusEffects().size());
                lines.add("§eUnder armor stand: §f" + isUnderFloatingArmorStand(living, clientWorld));
                lines.add("§eIs Attackable: §f" + living.isAttackable());
                lines.add("§eIs Invulnerable: §f" + living.isInvulnerable());
                lines.add("§eIs Pushed by fluids: §f" + living.isPushedByFluids());
            }

            if (entity instanceof net.minecraft.entity.mob.MobEntity) {
                lines.add("Mob Entity");
            }

            // NBT Summary
            NbtCompound nbt = new NbtCompound();
            entity.writeNbt(nbt);
            String nbtStr = nbt.toString();
            lines.add("§eNBT: §7" + (nbtStr.length() > 200 ? nbtStr.substring(0, 200) + "..." : nbtStr));

        } catch (Exception e) {
            lines.add("§c[Error getting entity info: " + e.getMessage() + "]");
        }

        for (String line : lines) {
            player.sendMessage(Text.of(line), false);
        }
    }

    public static <T> String safe(Supplier<T> supplier) {
        try {
            T result = supplier.get();
            return result != null ? result.toString() : "null";
        } catch (Exception e) {
            return "error";
        }
    }
// Place this in EntityTrackerClient.java to test
// Will require these imports
// import com.orangthegreat.testing.EntityInfo;
// import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
// import net.minecraft.util.ActionResult;
//    AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
//        if (!world.isClient || player != MinecraftClient.getInstance().player) return ActionResult.PASS;
//        new EntityInfo().getEntityInfo(player, world, hand, entity, hitResult, MC);
//        return ActionResult.PASS;
//    });
}
