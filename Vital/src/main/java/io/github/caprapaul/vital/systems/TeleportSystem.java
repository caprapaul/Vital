package io.github.caprapaul.vital.systems;

import io.github.caprapaul.vital.data.Warp;
import io.github.caprapaul.vitalcore.VitalCore;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportSystem
{
    public final static int DEFAULT_CHUNK_LOAD_DELAY = 10;

    public static void teleportOnChunkLoaded(final VitalCore plugin, final Warp warp, final Player player, final Chunk chunk)
    {
        new BukkitRunnable() {
            @Override
            public void run() {
                boolean result = chunk.isLoaded();
                //player.sendMessage(plugin.prefix + "Warp loaded: " + result);

                if (result)
                {
                    teleportImmediate(plugin, warp, player);
                }
                else
                {
                    teleportOnChunkLoaded(plugin, warp, player, chunk);
                }
            }
        }.runTaskLater(plugin, DEFAULT_CHUNK_LOAD_DELAY);
    }

    public static void teleportOnChunkLoaded(final VitalCore plugin, final Location location, final Player player, final Chunk chunk)
    {
        new BukkitRunnable() {
            @Override
            public void run() {
                boolean result = chunk.isLoaded();
                //player.sendMessage(plugin.prefix + "Warp loaded: " + result);

                if (result)
                {
                    teleportImmediate(location, player);
                }
                else
                {
                    teleportOnChunkLoaded(plugin, location, player, chunk);
                }
            }
        }.runTaskLater(plugin, DEFAULT_CHUNK_LOAD_DELAY);
    }

    public static void teleportOnChunkLoaded(final VitalCore plugin, final Player player, final Player destination, final Chunk chunk)
    {
        new BukkitRunnable() {
            @Override
            public void run() {
                boolean result = chunk.isLoaded();
                //player.sendMessage(plugin.prefix + "Warp loaded: " + result);

                if (result)
                {
                    teleportImmediate(player, destination);
                }
                else
                {
                    teleportOnChunkLoaded(plugin, player, destination, chunk);
                }
            }
        }.runTaskLater(plugin, DEFAULT_CHUNK_LOAD_DELAY);
    }

    public static void teleportImmediate(VitalCore plugin, Warp warp, Player player)
    {
        player.teleport(warp.toLocation(plugin));
    }

    public static void teleportImmediate(Location location, Player player)
    {
        player.teleport(location);
    }

    public static void teleportImmediate(Player player, Player destination)
    {
        player.teleport(destination);
    }

    public static Chunk getChunk(VitalCore plugin, Warp warp)
    {
        Location location = warp.toLocation(plugin);
        World world = location.getWorld();

        return world.getChunkAt(location);
    }

    public static Chunk getChunk(Location location)
    {
        World world = location.getWorld();

        return world.getChunkAt(location);
    }

    public static Chunk getChunk(Player destination)
    {
        Location location = destination.getLocation();
        World world = location.getWorld();

        return world.getChunkAt(location);
    }

    public static void teleport(VitalCore plugin, Warp warp, Player player)
    {
        Chunk chunkToLoad = TeleportSystem.getChunk(plugin, warp);

        if (chunkToLoad.load(true))
        {
            TeleportSystem.teleportImmediate(plugin, warp, player);
            return;
        }

        TeleportSystem.teleportOnChunkLoaded(plugin, warp, player, chunkToLoad);
    }

    public static void teleport(VitalCore plugin, Location location, Player player)
    {
        Chunk chunkToLoad = TeleportSystem.getChunk(location);

        if (chunkToLoad.load(true))
        {
            TeleportSystem.teleportImmediate(location, player);
            return;
        }

        TeleportSystem.teleportOnChunkLoaded(plugin, location, player, chunkToLoad);
    }

    public static void teleport(VitalCore plugin, Player player, Player destination)
    {
        Chunk chunkToLoad = TeleportSystem.getChunk(destination);

        if (chunkToLoad.load(true))
        {
            TeleportSystem.teleportImmediate(player, destination);
            return;
        }

        TeleportSystem.teleportOnChunkLoaded(plugin, player, destination, chunkToLoad);
    }
}
