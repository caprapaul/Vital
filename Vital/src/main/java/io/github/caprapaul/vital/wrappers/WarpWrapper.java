package io.github.caprapaul.vital.wrappers;

import io.github.caprapaul.vital.data.Warp;
import io.github.caprapaul.vitalcore.VitalCore;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class WarpWrapper
{
    public final static int DEFAULT_CHUNK_LOAD_DELAY = 10;

    public static void warpOnChunkLoaded(final VitalCore plugin, final Warp warp, final Player player, final Chunk chunk)
    {
        new BukkitRunnable() {
            @Override
            public void run() {
                boolean result = chunk.isLoaded();
                //player.sendMessage(plugin.prefix + "Warp loaded: " + result);

                if (result)
                {
                    warpImmediate(plugin, warp, player);
                }
                else
                {
                    warpOnChunkLoaded(plugin, warp, player, chunk);
                }
            }
        }.runTaskLater(plugin, DEFAULT_CHUNK_LOAD_DELAY);
    }

    public static void warpOnChunkLoaded(final VitalCore plugin, final Location location, final Player player, final Chunk chunk)
    {
        new BukkitRunnable() {
            @Override
            public void run() {
                boolean result = chunk.isLoaded();
                //player.sendMessage(plugin.prefix + "Warp loaded: " + result);

                if (result)
                {
                    warpImmediate(location, player);
                }
                else
                {
                    warpOnChunkLoaded(plugin, location, player, chunk);
                }
            }
        }.runTaskLater(plugin, DEFAULT_CHUNK_LOAD_DELAY);
    }

    public static void warpOnChunkLoaded(final VitalCore plugin, final Player player, final Player destination, final Chunk chunk)
    {
        new BukkitRunnable() {
            @Override
            public void run() {
                boolean result = chunk.isLoaded();
                //player.sendMessage(plugin.prefix + "Warp loaded: " + result);

                if (result)
                {
                    warpImmediate(player, destination);
                }
                else
                {
                    warpOnChunkLoaded(plugin, player, destination, chunk);
                }
            }
        }.runTaskLater(plugin, DEFAULT_CHUNK_LOAD_DELAY);
    }

    public static void warpImmediate(VitalCore plugin, Warp warp, Player player)
    {
        player.teleport(warp.toLocation(plugin));
    }

    public static void warpImmediate(Location location, Player player)
    {
        player.teleport(location);
    }

    public static void warpImmediate(Player player, Player destination)
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

    public static void warp(VitalCore plugin, Warp warp, Player player)
    {
        Chunk chunkToLoad = WarpWrapper.getChunk(plugin, warp);

        if (chunkToLoad.load(true))
        {
            WarpWrapper.warpImmediate(plugin, warp, player);
            return;
        }

        WarpWrapper.warpOnChunkLoaded(plugin, warp, player, chunkToLoad);
    }

    public static void warp(VitalCore plugin, Location location, Player player)
    {
        Chunk chunkToLoad = WarpWrapper.getChunk(location);

        if (chunkToLoad.load(true))
        {
            WarpWrapper.warpImmediate(location, player);
            return;
        }

        WarpWrapper.warpOnChunkLoaded(plugin, location, player, chunkToLoad);
    }

    public static void warp(VitalCore plugin, Player player, Player destination)
    {
        Chunk chunkToLoad = WarpWrapper.getChunk(destination);

        if (chunkToLoad.load(true))
        {
            WarpWrapper.warpImmediate(player, destination);
            return;
        }

        WarpWrapper.warpOnChunkLoaded(plugin, player, destination, chunkToLoad);
    }
}
