package io.github.caprapaul.vital.commands;

import io.github.caprapaul.bettercommandexecutor.BetterCommand;
import io.github.caprapaul.bettercommandexecutor.BetterCommandExecutor;
import io.github.caprapaul.vital.*;
import io.github.caprapaul.vital.data.Warp;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class HomeCommands extends BetterCommandExecutor
{
    private final Vital plugin;

    // { HashMap | Key: UUID, Value: { HashMap | Key: Home name, Value: Warp class } }
    private HashMap<String, HashMap<String, Warp>> playerHomes;

    public HomeCommands(Vital plugin)
    {
        this.plugin = plugin;
        this.playerHomes = new HashMap<String, HashMap<String, Warp>>();
        loadCommands(this, this.plugin);
        loadPlayerHomes();
    }

    private void loadPlayerHomes()
    {
        // When reloading plugins we want to make sure that all the
        // player homes for players currently on the server are loaded
        for (Player player: Bukkit.getServer().getOnlinePlayers())
        {
            // Get player UUID
            String playerUUID = player.getUniqueId().toString();

            // Get a list of all the homes that belong to the player
            // where the player UUID is the key in the YAML
            ArrayList<Warp> homeWarps = (ArrayList<Warp>) plugin
                    .getPlayerHomes()
                    .get("homes." + playerUUID, new ArrayList<Warp>());

            // Initialise a new HashMap for warp classes and their names
            HashMap<String, Warp> namedHomes = new HashMap<String, Warp>();

            // For each warp class we got from the YAML for that player
            // Add them to the hash map with their names
            // This allows for name key access instead of iterating the warps each time looking for a name.
            for (Warp home: homeWarps)
            {
                namedHomes.put(home.getName(), home);
            }
            this.playerHomes.put(playerUUID, namedHomes);
        }
    }

    // Add a home to the player-home HashMap
    private void addHome(Player player, Warp home) {
        HashMap<String, Warp> homes = this.playerHomes.get(player.getUniqueId().toString());
        homes.put(home.getName(), home);
    }

    private void removeHome(Player player, String homeName) {
        HashMap<String, Warp> homes = this.playerHomes.get(player.getUniqueId().toString());
        homes.remove(homeName);
    }

    // Takes the player to their default home. Usage:
    // /home
    // Takes the player to a named home. Usage:
    // /home <home name>
    @BetterCommand(name = "home")
    public void home(CommandSender commandSender, String[] args, String commandLabel)
    {
        // Default home
        if (args.length == 0)
        {

        }
    }

    // List the player's homes. Usage:
    // /homes
    @BetterCommand(name = "homes")
    public void homes()
    {

    }

    // Sets the player's default home at the player's current location. Usage:
    // /sethome
    // Sets a named home at the player's current location. Usage:
    // /sethome <home name>
    @BetterCommand(name = "sethome")
    public void setHome()
    {

    }

    // Deletes the default home of the player. Usage:
    // /delhome
    // Deletes a named home of the player. Usage:
    // /delhome <home name>
    @BetterCommand(name = "delhome")
    public void delHome()
    {

    }

    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args)
    {
        parseCommand(this, commandSender, command, commandLabel, args);
        return false;
    }
}
