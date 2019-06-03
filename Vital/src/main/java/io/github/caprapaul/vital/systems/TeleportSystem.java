package io.github.caprapaul.vital.systems;

import io.github.caprapaul.vitalcore.VitalCore;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Vehicle;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class TeleportSystem
{
    private final static int DEFAULT_CHUNK_LOAD_DELAY = 10;
    private final static String CONFIG_KEY_VEHICLES = "teleport-vehicles";
    private final static String CONFIG_KEY_LEASHED = "teleport-leashed";
    private final static double LEASH_CHECK_RANGE = 7;

    private static void teleportOnChunkLoaded(final VitalCore plugin, final Entity entity, final Location destination, final Chunk chunk)
    {
        new BukkitRunnable() {
            @Override
            public void run() {
                boolean result = chunk.isLoaded();
                //player.sendMessage(plugin.prefix + "Warp loaded: " + result);

                if (result)
                {
                    destination.setY(destination.getY() + 0.5);
                    teleportImmediate(plugin, entity, destination);
                }
                else
                {
                    teleportOnChunkLoaded(plugin, entity, destination, chunk);
                }
            }
        }.runTaskLater(plugin, DEFAULT_CHUNK_LOAD_DELAY);
    }

    private static void teleportImmediate(VitalCore plugin, Entity entity, Location destination)
    {
        if (!plugin.getConfig().contains(CONFIG_KEY_VEHICLES))
        {
            plugin.getConfig().addDefault(CONFIG_KEY_VEHICLES, true);
            plugin.getConfig().addDefault(CONFIG_KEY_LEASHED, true);
            plugin.getConfig().options().copyDefaults(true);
            plugin.saveConfig();
        }

        if (plugin.getConfig().getBoolean(CONFIG_KEY_LEASHED))
        {
            ArrayList<Entity> nearbyEntities = new ArrayList<>(entity.getNearbyEntities(LEASH_CHECK_RANGE, LEASH_CHECK_RANGE, LEASH_CHECK_RANGE));

            for (Entity nearbyEntity : nearbyEntities)
            {
                if (nearbyEntity instanceof LivingEntity)
                {
                    LivingEntity livingEntity = (LivingEntity) nearbyEntity;

                    if (livingEntity.isLeashed())
                    {
                        if (livingEntity.getLeashHolder().equals(entity))
                        {
                            livingEntity.teleport(destination);
                        }
                    }
                }
            }
        }

        if (plugin.getConfig().getBoolean(CONFIG_KEY_VEHICLES))
        {
            if (entity.isInsideVehicle())
            {
                Vehicle vehicle = (Vehicle) entity.getVehicle();
                vehicle.eject();
                vehicle.teleport(destination);
                entity.teleport(destination);
                vehicle.addPassenger(entity);

                return;
            }
        }

        entity.teleport(destination);
    }

    private static Chunk getChunk(Location destination)
    {
        World world = destination.getWorld();

        return world.getChunkAt(destination);
    }

    public static void teleport(VitalCore plugin, Entity entity, Location destination)
    {
        Chunk chunkToLoad = TeleportSystem.getChunk(destination);

        chunkToLoad.load(true);
        TeleportSystem.teleportOnChunkLoaded(plugin, entity, destination, chunkToLoad);
    }

    public static void teleport(VitalCore plugin, Entity entity, Entity destination)
    {
        Chunk chunkToLoad = TeleportSystem.getChunk(destination.getLocation());

        chunkToLoad.load(true);
        TeleportSystem.teleportOnChunkLoaded(plugin, entity, destination.getLocation(), chunkToLoad);
    }
}
