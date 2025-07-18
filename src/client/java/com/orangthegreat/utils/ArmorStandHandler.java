package com.orangthegreat.utils;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.Box;

import java.util.List;

public class ArmorStandHandler {

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

    public static boolean isUnderFloatingArmorStand(Entity entity, ClientWorld world) {
        if (!(entity instanceof LivingEntity) || entity instanceof ArmorStandEntity) return false;

        Box above = entity.getBoundingBox().expand(0.6, 2.0, 0.6).offset(0, 0.5, 0);  // small box just above entity
        List<Entity> nearArmorStands = world.getOtherEntities(entity, above, e -> e instanceof ArmorStandEntity && e.getY() >= entity.getY());
        return !nearArmorStands.isEmpty();
    }
}
