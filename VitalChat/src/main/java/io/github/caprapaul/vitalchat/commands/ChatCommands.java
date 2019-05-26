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
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
        loadConfig();

        this.chatInfoFile = new File(this.plugin.getDataFolder(), "chatInfo.yml");
        this.chatInfoConfig = YamlConfiguration.loadConfiguration(this.chatInfoFile);

        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(this, plugin);

        @SuppressWarnings("unchecked")
        Iterable<Player> onlinePlayers = (Iterable<Player>) Bukkit.getServer().getOnlinePlayers();
        loadAllChatInfo(onlinePlayers);
    }

    private void loadConfig()
    {
        plugin.getConfig().addDefault("nick-max-length", 10);
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
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

        if(chatInfoObjects == null)
        {
            chatInfoObjects = new ArrayList<String>();
        }

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

    @BetterCommand(name = "setnick")
    public void setnick(CommandSender commandSender, String[] args, String commandLabel)
    {
        if (!(commandSender instanceof Player))
        {
            commandSender.sendMessage(plugin.prefix + ChatColor.RED + "Error: The console can't change it's nickname");
            return;
        }

        if(args.length == 0)
        {
            commandSender.sendMessage(plugin.prefix + ChatColor.RED + "Error: Need to specify a player and nickname");
        }

        @SuppressWarnings("unchecked")
        Iterable<Player> onlinePlayers = (Iterable<Player>) Bukkit.getServer().getOnlinePlayers();

        Player player = null;
        //Try to find player based on name passed
        String playerName = args[0];
        for (Player p : onlinePlayers)
        {
            if(playerName.equals(p.getDisplayName()))
            {
                player = p;
                break;
            }
        }

        if(player == null)
        {
            commandSender.sendMessage(plugin.prefix + ChatColor.RED + "Error: Could not find player");
            return;
        }

        String newName = CombineStrings(args, 1);
        SetPlayerName(player, newName, commandSender);
    }

    @BetterCommand(name = "nick")
    public void nick(CommandSender commandSender, String[] args, String commandLabel)
    {
        if (!(commandSender instanceof Player))
        {
            commandSender.sendMessage(plugin.prefix + ChatColor.RED + "Error: The console can't change it's nickname");
            return;
        }

        Player player = (Player) commandSender;
        String playerUUID = player.getUniqueId().toString();

        if(args.length == 0)
        {
            commandSender.sendMessage(plugin.prefix + "Nickname reset to default");
            ArrayList<String> array = playerChatInfo.get(playerUUID);
            array.set(0, player.getDisplayName());

            playerChatInfo.put(playerUUID, array);

            return;
        }

        String newName = CombineStrings(args, 0);
        SetPlayerName(player, newName, commandSender);
    }

    private void SetPlayerName(Player player, String newName, CommandSender commandSender)
    {
        String playerUUID = player.getUniqueId().toString();
        int nameLength = getTrueNameLength(newName);

        if(nameLength == 0)
        {
            commandSender.sendMessage(plugin.prefix + ChatColor.RED + "Error: Nickname must be at least one character long!");
            return;
        }

        int duplicates = CheckForDuplicate(newName);
        if(duplicates != 0)
        {
            newName += "(" + (duplicates + 1) + ")";
            commandSender.sendMessage(plugin.prefix + ChatColor.RED + " Someone already has this name! Adding a (" + (duplicates + 1) + ")");
        }

        ArrayList<String> array = playerChatInfo.get(playerUUID);
        array.set(0, newName);

        playerChatInfo.put(playerUUID, array);

        commandSender.sendMessage(plugin.prefix + "Name has been changed to " + EvaluateStringForColors(newName));
        if(((Player)commandSender) != player)
        {
            player.sendMessage(plugin.prefix + "Your name has been changed to " + EvaluateStringForColors(newName) + ChatColor.WHITE + " by " + ((Player)commandSender).getDisplayName());
        }
    }

    private int getTrueNameLength(String finalName)
    {
        int count = 0;
        boolean skipNext = false;
        char[] chars = finalName.toCharArray();
        for (int i = 0; i < chars.length; i++)
        {
            if(skipNext)
            {
                skipNext = false;
                continue;
            }
            else if(chars[i] == '&')
            {
                skipNext = true;
            }
            else
            {
                count++;
            }
        }
        return count;
    }

    private int CheckForDuplicate(String finalName)
    {
        int currentCount = 0;
        for (ArrayList<String> data : playerChatInfo.values())
        {
            if(data.get(0) == finalName)
            {
                currentCount++;
            }
        }
        return currentCount;
    }

    @BetterCommand(name = "chatcolor")
    public void chatcolor(CommandSender commandSender, String[] args, String commandLabel)
    {
        if (!(commandSender instanceof Player))
        {
            commandSender.sendMessage(plugin.prefix + ChatColor.RED + "Error: The console can't change it's chat color");
            return;
        }

        ChatColor[] colors = ChatColor.values();
        if(args.length == 0)
        {
            String finalMessage = "";
            for (ChatColor color : colors)
            {
                finalMessage += ChatColor.translateAlternateColorCodes('&', "&" + Character.toString(color.getChar())) + "&" + color.getChar();
                finalMessage += ChatColor.translateAlternateColorCodes('&',"&r");
            }
            commandSender.sendMessage(finalMessage);
            return;
        }

        Player player = (Player) commandSender;
        String playerUUID = player.getUniqueId().toString();

        boolean inputValid = args.length == 1 && args[0].toCharArray().length == 2 && args[0].toCharArray()[0] == '&' && ChatColor.getByChar(args[0].toCharArray()[1]) != null;

        if(inputValid)
        {
            String newColor = args[0];

            ArrayList<String> array = playerChatInfo.get(playerUUID);
            array.set(1, newColor);

            playerChatInfo.put(playerUUID, array);

            commandSender.sendMessage("Your chat color is now " + ChatColor.getByChar(newColor) + ChatColor.getByChar(newColor).name());
        }
        else {
            commandSender.sendMessage(plugin.prefix + ChatColor.RED + "Error: Invalid input (Example: /chatcolor &7");
        }
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

    @EventHandler(priority= EventPriority.HIGHEST)
    public  void onPlayerChat(AsyncPlayerChatEvent chatEvent)
    {
        Player p = chatEvent.getPlayer();
        String pId = p.getUniqueId().toString();
        String prefix = EvaluateStringForColors(GetPlayerPrefix(p));

        String playerName =  EvaluateStringForColors(playerChatInfo.get(pId).get(0));

        TextComponent mainComponent = new TextComponent(prefix + ChatColor.GRAY );
        TextComponent subComponent = new TextComponent( playerName );
        subComponent.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder( p.getDisplayName() ).create() ) );
        mainComponent.addExtra( subComponent );

        String chatMessage = EvaluateStringForColors(": " + playerChatInfo.get(pId).get(1) + chatEvent.getMessage());
        mainComponent.addExtra( chatMessage );

        for (Player player: chatEvent.getRecipients())
        {
            player.spigot().sendMessage(mainComponent);
        }
        chatEvent.setCancelled(true);
    }

    private String CombineStrings(String[] strings, int startIndex)
    {
        String result = "";
        for (int i = startIndex; i < strings.length; i++) {
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
        return ChatColor.translateAlternateColorCodes('&', text);
        //String finalText = text;
        //if(text == "null")
        //{
        //    return "";
        //}
        //if(text.contains("&"))
        //{
        //    finalText = "";
        //    boolean checkNext = false;
        //    for (int i = 0; i < text.length(); i++)
        //    {
        //        char current = text.toCharArray()[i];
        //        if(!checkNext)
        //        {
        //            if(current == '&')
        //            {
        //                checkNext = true;
        //            }
        //            else
        //            {
        //                finalText += current;
        //            }
        //        }
        //        else
        //        {
        //            finalText += ChatColor.getByChar(current);
        //            checkNext = false;
        //        }
        //    }
        //}
//
        //return finalText;
    }
}
