package io.github.caprapaul.vitalchat.commands;

import io.github.caprapaul.bettercommandexecutor.BetterCommand;
import io.github.caprapaul.bettercommandexecutor.BetterCommandExecutor;
import io.github.caprapaul.bettercommandexecutor.BetterExecutor;
import io.github.caprapaul.vitalcore.VitalCore;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.context.ContextSet;
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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.util.*;

@BetterExecutor
public class ChatCommands extends BetterCommandExecutor implements Listener
{
    private File chatInfoFile;
    private FileConfiguration chatInfoConfig;

    //<String - Player UUID,
    //[ArrayList]
    //#1 String - Chat nickname
    //#2 String - Chat color
    private HashMap<String, ArrayList<String>> playerChatInfo;
    enum ChatInfoSettings { Nick, Color };
    private static final String[] ChatInfoSettings = new String[] { "Nick", "Color" };

    public  ChatCommands(VitalCore plugin)
    {
        super(plugin);
    }

    @Override
    public void onEnable()
    {
        playerChatInfo = new HashMap<String, ArrayList<String>>();
        loadCommands(this, plugin);

        this.chatInfoFile = new File(this.plugin.getDataFolder(), "chatInfo.yml");
        this.chatInfoConfig = YamlConfiguration.loadConfiguration(this.chatInfoFile);

        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(this, plugin);

        @SuppressWarnings("unchecked")
        Iterable<Player> onlinePlayers = (Iterable<Player>) Bukkit.getServer().getOnlinePlayers();
        loadAllChatInfo(onlinePlayers);
    }

    @Override
    public void onDisable()
    {
        @SuppressWarnings("unchecked")
        Iterable<Player> onlinePlayers = (Iterable<Player>) Bukkit.getServer().getOnlinePlayers();
        this.unloadAllChatInfo(onlinePlayers);

        try
        {
            this.chatInfoConfig.save(this.chatInfoFile);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args)
    {
        parseCommand(this, commandSender, command, commandLabel, args);
        return false;
    }

    private void loadAllChatInfo(Iterable<Player> players)
    {
        for (Player player : players)
        {
            loadPlayerChatInfo(player);
        }
    }

    private void unloadAllChatInfo(Iterable<Player> players)
    {
        for (Player player : players)
        {
            unloadPlayerChatInfo(player);
        }
    }
    
    private void loadPlayerChatInfo(Player player)
    {
        // Get player UUID
        String playerUUID = player.getUniqueId().toString();

        ArrayList<String> chatInfoObjects = (ArrayList<String>)chatInfoConfig.get("chatInfo." + playerUUID);

        while (chatInfoObjects.size() < ChatInfoSettings.length)
        {
            String current = ChatInfoSettings[chatInfoObjects.size()];

            if(current == "Nick")
            {
                chatInfoObjects.add(player.getDisplayName());
            }
            else if(current == "Color")
            {
                chatInfoObjects.add("&7");
            }
        }

        // Add the loaded; or newly created, HashMap to the player home HashMap
        playerChatInfo.put(playerUUID, chatInfoObjects);
        System.out.println("Player " + player.getDisplayName() + " now has " + playerChatInfo.get(playerUUID).size() + " loaded");
    }

    // Update the loaded YML file for homes and remove the player from the list of loaded homes
    private void unloadPlayerChatInfo(Player player)
    {
        String playerUUID = player.getUniqueId().toString();

        if (!(playerChatInfo.containsKey(playerUUID)))
        {
            return;
        }

        chatInfoConfig.set("chatInfo." + playerUUID, playerChatInfo.get(playerUUID));

        this.playerChatInfo.remove(playerUUID);
    }


    @BetterCommand(name = "nick")
    public void nick(CommandSender commandSender, String[] args, String commandLabel)
    {
        if (!(commandSender instanceof Player))
        {
            commandSender.sendMessage(plugin.prefix + ChatColor.RED + "Error: The console can't change it's nickname");
            return;
        }

        if(args.length == 0)
        {
            commandSender.sendMessage(plugin.prefix + ChatColor.RED + "Error: You must specify your new nickname!");
            return;
        }

        Player player = (Player) commandSender;
        String playerUUID = player.getUniqueId().toString();

        String newName = CombineStrings(args);

        ArrayList<String> array = playerChatInfo.get(playerUUID);
        array.set(0, newName);

        playerChatInfo.put(playerUUID, array);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        this.loadPlayerChatInfo(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        this.unloadPlayerChatInfo(event.getPlayer());
    }

    @EventHandler
    public  void onPlayerChat(AsyncPlayerChatEvent chatEvent)
    {
        Player p = chatEvent.getPlayer();
        String pId = p.getUniqueId().toString();
        String prefix = EvaluateStringForColors(GetPlayerPrefix(p));

        String playerName = EvaluateStringForColors(playerChatInfo.get(pId).get(0));
        String chatMessage = EvaluateStringForColors(playerChatInfo.get(pId).get(1) + chatEvent.getMessage());
        chatEvent.setFormat(prefix + ChatColor.GRAY + playerName + ChatColor.GRAY + ": " + chatMessage);
    }

    private String CombineStrings(String[] strings)
    {
        String result = "";
        for (int i = 0; i < strings.length; i++) {
            result += strings[i];
        }
        return result;
    }

    private String GetPlayerPrefix(Player player)
    {
        User user = LuckPerms.getApi().getUser(player.getUniqueId());
        Contexts userCtx = LuckPerms.getApi().getContextForUser(user).orElseThrow(() -> new IllegalStateException("Could not get LuckPerms context for player " + player));
        return user.getCachedData().getMetaData(userCtx).getPrefix();
    }

    private String EvaluateStringForColors(String text)
    {
        String finalText = text;
        if(text == "null")
        {
            return "";
        }
        if(text.contains("&"))
        {
            finalText = "";
            boolean checkNext = false;
            for (int i = 0; i < text.length(); i++)
            {
                char current = text.toCharArray()[i];
                if(!checkNext)
                {
                    if(current == '&')
                    {
                        checkNext = true;
                    }
                    else
                    {
                        finalText += current;
                    }
                }
                else
                {
                    finalText += ChatColor.getByChar(current);
                    checkNext = false;
                }
            }
        }

        return finalText;
    }
}
