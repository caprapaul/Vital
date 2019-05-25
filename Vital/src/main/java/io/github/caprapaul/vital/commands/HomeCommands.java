package io.github.caprapaul.vital.commands;

import io.github.caprapaul.bettercommandexecutor.BetterCommand;
import io.github.caprapaul.bettercommandexecutor.BetterCommandExecutor;
import io.github.caprapaul.bettercommandexecutor.BetterExecutor;
import io.github.caprapaul.vital.*;
import io.github.caprapaul.vital.data.Warp;
import io.github.caprapaul.vitalcore.VitalCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("unused")
@BetterExecutor
public class HomeCommands extends BetterCommandExecutor implements Listener
{
    private File homesFile;
    private FileConfiguration homes;

    // { HashMap | Key: UUID, Value: { HashMap | Key: Home name, Value: Warp class } }
    private HashMap<String, HashMap<String, Warp>> playerHomes;

    public HomeCommands(VitalCore plugin)
    {
        super(plugin);
    }

    @Override
    public void onEnable()
    {
        this.playerHomes = new HashMap<String, HashMap<String, Warp>>();
        loadCommands(this, this.plugin);

        this.homesFile = new File(this.plugin.getDataFolder(), "homes.yml");
        this.homes = YamlConfiguration.loadConfiguration(this.homesFile);

        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        pluginManager.registerEvents(this, this.plugin);

        /*
         When reloading plugins we want to make sure that all the
         player homes for players currently on the server are loaded
        */
        @SuppressWarnings("unchecked")
        Iterable<Player> onlinePlayers = (Iterable<Player>) Bukkit.getServer().getOnlinePlayers();
        this.loadMultiplePlayersHomes(onlinePlayers);
    }

    @Override
    public void onDisable()
    {
        @SuppressWarnings("unchecked")
        Iterable<Player> onlinePlayers = (Iterable<Player>) Bukkit.getServer().getOnlinePlayers();
        this.unloadMultiplePlayersHomes(onlinePlayers);

        try
        {
            this.homes.save(this.homesFile);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void loadMultiplePlayersHomes(Iterable<Player> players)
    {
        for (Player player : players)
        {
            loadPlayerHomes(player);
        }
    }

    private void unloadMultiplePlayersHomes(Iterable<Player> players)
    {
        for (Player player : players)
        {
            unloadPlayerHomes(player);
        }
    }

    private void loadPlayerHomes(Player player)
    {
        // Get player UUID
        String playerUUID = player.getUniqueId().toString();

        /*
         Get a list of all the homes that belong to the player
         where the player UUID is the key in the YAML
        */

        ConfigurationSection configurationSection = homes.getConfigurationSection("homes." + playerUUID);

        if (configurationSection == null)
        {
            return;
        }
        HashMap<String, Object> homeObjects = (HashMap<String, Object>) configurationSection.getValues(false);

        HashMap<String, Warp> homes = new HashMap<String, Warp>();
        /*
         For each warp class we got from the YAML for that player
         Add them to the hash map with their names
         This allows for name key access instead of iterating the warps each time looking for a name.
        */
        for (Object obj : homeObjects.values())
        {
            Warp home = (Warp) obj;
            homes.put(home.getName(), home);
        }

        // Add the loaded; or newly created, HashMap to the player home HashMap
        this.playerHomes.put(playerUUID, homes);
    }

    // Update the loaded YML file for homes and remove the player from the list of loaded homes
    private void unloadPlayerHomes(Player player)
    {
        String playerUUID = player.getUniqueId().toString();

        if (!(playerHomes.containsKey(playerUUID)))
        {
            return;
        }

        ConfigurationSection configurationSection = homes.getConfigurationSection("homes." + playerUUID);

        if (configurationSection == null)
        {
            homes.createSection("homes." + playerUUID, this.playerHomes.get(playerUUID));
            return;
        }

        for (Warp home : playerHomes.get(playerUUID).values())
        {
            configurationSection.set(home.getName(), home);
        }

        this.playerHomes.remove(playerUUID);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        this.loadPlayerHomes(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        this.unloadPlayerHomes(event.getPlayer());
    }

    /*
     Add a home to the player-home HashMap
     Will also overwrite old homes when named
    */
    private void addHome(Player player, Warp home)
    {
        HashMap<String, Warp> homes = new HashMap<String, Warp>();

        if (this.playerHomes.containsKey(player.getUniqueId().toString()))
        {
            homes = this.playerHomes.get(player.getUniqueId().toString());
        } else
        {
            this.playerHomes.put(player.getUniqueId().toString(), homes);
        }

        homes.put(home.getName(), home);

        player.sendMessage(this.plugin.prefix + ChatColor.GRAY + "Set home " + ChatColor.GOLD + home.getName() + ChatColor.GRAY + ".");
    }

    private void removeHome(Player player, String homeName)
    {
        String playerUUID = player.getUniqueId().toString();
        if (!(this.playerHomes.containsKey(playerUUID)))
        {
            player.sendMessage(plugin.prefix + ChatColor.RED + "You don't have any homes!");
            return;
        }

        HashMap<String, Warp> homes = this.playerHomes.get(playerUUID);

        if (!(homes.containsKey(homeName)))
        {
            player.sendMessage(this.plugin.prefix + ChatColor.RED + "Home \"" + homeName + "\" doesn't exist!");
            return;
        }

        homes.remove(homeName);

        ConfigurationSection configurationSection = this.homes.getConfigurationSection("homes." + playerUUID);

        if (configurationSection != null)
        {
            configurationSection.set(homeName, null);
        }

        player.sendMessage(this.plugin.prefix + "Deleted home " + homeName + ".");
    }

    /*
     Takes the player to their default home. Usage:
        /home
     Takes the player to a named home. Usage:
        /home <home name>
    */
    @BetterCommand(name = "home")
    public void home(CommandSender commandSender, String[] args, String commandLabel)
    {
        // Get the player from commandSender
        Player player = (Player) commandSender;

        // Get the UUID of the player who sent the command
        String playerUUID = player.getUniqueId().toString();

        if (!(playerHomes.containsKey(playerUUID)))
        {
            player.sendMessage(this.plugin.prefix + ChatColor.RED + "You don't have any homes!");
            return;
        }

        Warp target;

        switch (args.length)
        {
            // Default home
            case 0:
                target = this.playerHomes.get(playerUUID).get("default");
                break;

            // Named home
            case 1:
            {
                target = this.playerHomes.get(playerUUID).get(args[0]);
                break;
            }

            // Error: too many arguments
            default:
                player.sendMessage(this.plugin.prefix + ChatColor.RED + "Too many arguments! Usage /home <home_name>");
                return;
        }

        // If the home doesn't exist, send an error message to the user.
        // And return from this method
        if (target == null)
        {
            player.sendMessage(this.plugin.prefix + ChatColor.RED + "Home does not exist! Use /homes to see your set homes");
            return;
        }

        // Send a message and then teleport the player to the target.
        player.sendMessage(this.plugin.prefix + ChatColor.GRAY + "Teleporting to home: " + ChatColor.GOLD + target.getName() + ChatColor.GRAY + "...");
        player.teleport(target.toLocation(this.plugin));
    }

    /*
     List the player's homes. Usage:
        /homes
     Show a page from the list of homes. Usage:
        /homes <page number>
    */
    @BetterCommand(name = "homes")
    public void homes(CommandSender commandSender, String[] args, String commandLabel)
    {
        Player player = (Player) commandSender;
        String playerUUID = player.getUniqueId().toString();
        HashMap<String, Warp> playerHomes = this.playerHomes.get(playerUUID);

        String output = "";
        int pageCount = 10;
        int homeCount = playerHomes.size();

        if (homeCount == 0)
        {
            player.sendMessage(this.plugin.prefix + ChatColor.RED + "You have no homes.\nUse " + ChatColor.GOLD + "/sethome" + ChatColor.GRAY + "to set a home!");
        }

        ArrayList<Warp> homeArray = new ArrayList<Warp>(playerHomes.values());

        switch (args.length)
        {
            // Show the first page of the player's homes
            case 0:
                output += this.plugin.prefix + ChatColor.GRAY + "Homes page: 0/" + (int) Math.ceil(homeArray.size() / pageCount);
                for (int i = 0; i < pageCount && i < homeArray.size(); i++)
                {
                    output += "\n" + this.plugin.prefix + ChatColor.GRAY + "Name: " + ChatColor.GOLD + homeArray.get(i).getName();
                }
                player.sendMessage(output);
                break;

            // Show the selected page of the player's homes
            case 1:
                int startInt;
                try
                {
                    startInt = pageCount * Integer.parseInt(args[0]);
                } catch (NumberFormatException e)
                {
                    player.sendMessage(this.plugin.prefix + ChatColor.RED + "Home page must be a number.");
                    return;
                }
                if (startInt < 0)
                {
                    startInt = 0;
                }
                output += this.plugin.prefix + ChatColor.GRAY + "Homes page: " + args[0] + "/" + (int) Math.ceil(homeArray.size() / pageCount);
                for (int i = startInt; i < startInt + pageCount && i < homeArray.size(); i++)
                {
                    System.out.println(i);
                    output += "\n" + this.plugin.prefix + ChatColor.GRAY + "Name: " + ChatColor.GOLD + homeArray.get(i).getName();
                }
                player.sendMessage(output);
                break;

            // Error: Too many arguments
            default:
                player.sendMessage(this.plugin.prefix + ChatColor.RED + "Too many arguments! Usage /homes <page number>");
                break;
        }
    }

    /*
     Sets the player's default home at the player's current location. Usage:
        /sethome
     Sets a named home at the player's current location. Usage:
        /sethome <home name>
    */
    @BetterCommand(name = "sethome")
    public void setHome(CommandSender commandSender, String[] args, String commandLabel)
    {
        Player player = (Player) commandSender;

        switch (args.length)
        {
            // Set the player's default home
            case 0:
                this.addHome(player, new Warp("default", player.getLocation()));
                break;

            // Set a named home
            case 1:
                this.addHome(player, new Warp(args[0], player.getLocation()));
                break;

            // Error: Too many arguments
            default:
                player.sendMessage(this.plugin.prefix + ChatColor.RED + "Too many arguments! Usage /sethome <home name>");
                break;
        }
    }

    /*
     Deletes the default home of the player. Usage:
        /delhome
     Deletes a named home of the player. Usage:
        /delhome <home name>
    */
    @BetterCommand(name = "delhome")
    public void delHome(CommandSender commandSender, String[] args, String commandLabel)
    {
        // Get the player
        Player player = (Player) commandSender;

        switch (args.length)
        {
            // Delete the default home
            case 0:
                this.removeHome(player, "default");
                break;

            // Delete a named home
            case 1:
                this.removeHome(player, args[0]);
                break;

            // Error: Too many arguments
            default:
                player.sendMessage(this.plugin.prefix + ChatColor.RED + "Too many arguments! Usage /homes <page number>");
        }
    }

    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args)
    {
        parseCommand(this, commandSender, command, commandLabel, args);
        return false;
    }
}
