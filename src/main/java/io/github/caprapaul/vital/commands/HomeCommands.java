package io.github.caprapaul.vital.commands;

import io.github.caprapaul.bettercommandexecutor.BetterCommand;
import io.github.caprapaul.bettercommandexecutor.BetterCommandExecutor;
import io.github.caprapaul.vital.*;
import io.github.caprapaul.vital.data.Warp;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeCommands extends BetterCommandExecutor
{
    private final Vital plugin;
    private HashMap<Player, ArrayList<Warp>> playerHomes;

    public HomeCommands(Vital plugin) {
        this.plugin = plugin;
        this.playerHomes = new HashMap<Player, ArrayList<Warp>>();
        loadCommands(this, this.plugin);
        loadPlayerHomes();
    }

    public void loadPlayerHomes() {
        ArrayList<Player> playersCurrentlyOn = (ArrayList<Player>) Bukkit.getServer().getOnlinePlayers();
    }


    @BetterCommand(name="home")
    public void home() {

    }

    @BetterCommand(name="sethome")
    public void setHome() {

    }

    @BetterCommand(name="delhome")
    public void delHome() {

    }

    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args)
    {
        parseCommand(this, commandSender, command, commandLabel, args);
        return false;
    }
}
