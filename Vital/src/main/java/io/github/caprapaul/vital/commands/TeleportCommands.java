package io.github.caprapaul.vital.commands;

import io.github.caprapaul.bettercommandexecutor.BetterCommand;
import io.github.caprapaul.bettercommandexecutor.BetterCommandExecutor;
import io.github.caprapaul.bettercommandexecutor.BetterExecutor;
import io.github.caprapaul.bettercommandexecutor.CommandTarget;
import io.github.caprapaul.vital.systems.TeleportSystem;
import io.github.caprapaul.vitalcore.VitalCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@BetterExecutor
public class TeleportCommands extends BetterCommandExecutor
{
    private enum RequestType
    {
        HERE,
        THERE
    }

    private class Request
    {
        private Request(String sender, RequestType type)
        {
            this.sender = sender;
            this.type = type;
        }

        private String sender;
        private RequestType type;

        private String getSender()
        {
            return sender;
        }

        public RequestType getType()
        {
            return type;
        }
    }

    private Map<String, Long> tpaCooldowns = new HashMap<String, Long>();
    private Map<String, Request> currentRequests = new HashMap<String, Request>();

    public TeleportCommands(VitalCore plugin)
    {
        super(plugin);
        loadCommands(this, this.plugin);
    }

    @Override
    public void onEnable()
    {
        loadConfig();
    }

    private void loadConfig()
    {
        plugin.getConfig().addDefault("tpa-cooldown", 5);
        plugin.getConfig().addDefault("keep-alive", 30);
        plugin.getConfig().addDefault("override-old-requests", false);
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
    }

    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args)
    {
        parseCommand(this, commandSender, command, commandLabel, args);
        return false;
    }

    private void sendRequest(Player sender, Player recipient, RequestType type)
    {
        sender.sendMessage(plugin.prefix + ChatColor.GRAY + "Sending a teleport request to " + recipient.getName() + ".");

        String sendTpAccept = "";
        String sendTpDeny = "";

        if (recipient.hasPermission("vital.tpaccept"))
        {
            sendTpAccept = ChatColor.GRAY + "\nTo accept the teleport request, type " + ChatColor.GREEN + "/tpaccept" + ChatColor.GRAY + ".";
        }
        else
        {
            sendTpAccept = "";
        }

        if (recipient.hasPermission("vital.tpdeny"))
        {
            sendTpDeny = ChatColor.GRAY + "\nTo deny the teleport request, type " + ChatColor.RED + "/tpdeny" + ChatColor.GRAY + ".";
        }
        else
        {
            sendTpDeny = "";
        }

        switch (type)
        {
            case HERE:
                recipient.sendMessage(plugin.prefix + ChatColor.GOLD + sender.getName() + ChatColor.GRAY + " has sent you a request to teleport to them." + ChatColor.RESET + sendTpAccept + sendTpDeny);
                break;
            case THERE:
                recipient.sendMessage(plugin.prefix + ChatColor.GOLD + sender.getName() + ChatColor.GRAY + " has sent a request to teleport to you." + ChatColor.RESET + sendTpAccept + sendTpDeny);
                break;
        }
        currentRequests.put(recipient.getUniqueId().toString(), new Request(sender.getName(), type));
    }

    private boolean killRequest(String key)
    {
        if (currentRequests.containsKey(key))
        {
            Player sender = plugin.getServer().getPlayer(currentRequests.get(key).getSender());
            if (!(sender == null))
            {
                sender.sendMessage(plugin.prefix + ChatColor.RED + "Your teleport request timed out.");
            }

            currentRequests.remove(key);

            return true;
        }
        else
        {
            return false;
        }
    }

    @BetterCommand(name = "tpa", target = CommandTarget.PLAYER)
    public void tpa(CommandSender commandSender, String[] args, String commandLabel)
    {
        Player player = (Player) commandSender;
        if (!player.hasPermission("vital.tpa.overridecooldown"))
        {
            int cooldown = plugin.getConfig().getInt("tpa-cooldown");
            if (tpaCooldowns.containsKey(player.getUniqueId().toString()))
            {
                long diff = (System.currentTimeMillis() - tpaCooldowns.get(player.getUniqueId().toString())) / 1000;
                if (diff < cooldown)
                {
                    player.sendMessage(plugin.prefix + ChatColor.RED + "Error: You must wait a " + cooldown + " second cooldown in between teleport requests!");
                    return;
                }
            }
        }

        if (args.length == 0)
        {
            player.sendMessage(plugin.prefix + "Send a teleport request to a player.");
            player.sendMessage(ChatColor.GRAY + "Usage: /tpa <player>");
            return;
        }

        if (args.length > 1)
        {
            player.sendMessage(plugin.prefix + ChatColor.RED + "Error: Too many arguments!");
            return;
        }

        final Player target = plugin.getServer().getPlayer(args[0]);
        long keepAlive = plugin.getConfig().getLong("keep-alive");

        if (target == null)
        {
            player.sendMessage(plugin.prefix + ChatColor.RED + "Error: You can only send a teleport request to online players!");
            return;
        }

        if (target == player)
        {
            player.sendMessage(plugin.prefix + ChatColor.RED + "Error: You can't teleport to yourself!");
            return;
        }

        boolean overrideOldRequest = plugin.getConfig().getBoolean("override-old-request");

        if (!(overrideOldRequest) && currentRequests.containsKey(target.getUniqueId().toString()))
        {
            if (currentRequests.get(target.getUniqueId().toString()).getSender().equals(player.getUniqueId().toString()))
            {
                player.sendMessage(plugin.prefix + ChatColor.RED + "Error: You can't send multiple requests to the same player!");
                return;
            }
        }

        sendRequest(player, target, RequestType.THERE);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            public void run()
            {
                killRequest(target.getUniqueId().toString());
            }
        }, keepAlive);

        tpaCooldowns.put(player.getUniqueId().toString(), System.currentTimeMillis());
    }

    @BetterCommand(name = "tpahere", target = CommandTarget.PLAYER)
    public void tpahere(CommandSender commandSender, String[] args, String commandLabel)
    {
        Player player = (Player) commandSender;
        if (!player.hasPermission("vital.tpahere.overridecooldown"))
        {
            int cooldown = plugin.getConfig().getInt("tpa-cooldown");
            if (tpaCooldowns.containsKey(player.getUniqueId().toString()))
            {
                long diff = (System.currentTimeMillis() - tpaCooldowns.get(player.getUniqueId().toString())) / 1000;
                if (diff < cooldown)
                {
                    player.sendMessage(plugin.prefix + ChatColor.RED + "Error: You must wait a " + cooldown + " second cooldown in between teleport requests!");
                    return;
                }
            }
        }

        if (args.length == 0)
        {
            player.sendMessage(plugin.prefix + "Send a teleport request to a player.");
            player.sendMessage(ChatColor.GRAY + "Usage: /tpahere <player>");
            return;
        }

        if (args.length > 1)
        {
            player.sendMessage(plugin.prefix + ChatColor.RED + "Error: Too many arguments!");
            return;
        }

        final Player target = plugin.getServer().getPlayer(args[0]);
        long keepAlive = plugin.getConfig().getLong("keep-alive") * 20;

        if (target == null)
        {
            player.sendMessage(plugin.prefix + ChatColor.RED + "Error: You can only send a teleport request to online players!");
            return;
        }

        if (target == player)
        {
            player.sendMessage(plugin.prefix + ChatColor.RED + "Error: You can't teleport to yourself!");
            return;
        }

        boolean overrideOldRequest = plugin.getConfig().getBoolean("override-old-request");

        if (!(overrideOldRequest) && currentRequests.containsKey(target.getUniqueId().toString()))
        {
            if (currentRequests.get(target.getUniqueId().toString()).getSender().equals(player.getUniqueId().toString()))
            {
                player.sendMessage(plugin.prefix + ChatColor.RED + "Error: You can't send multiple requests to the same player!");
                return;
            }
        }

        sendRequest(player, target, RequestType.HERE);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            public void run()
            {
                killRequest(target.getUniqueId().toString());
            }
        }, keepAlive);

        tpaCooldowns.put(player.getUniqueId().toString(), System.currentTimeMillis());
    }

    @BetterCommand(name = "tpaccept", target = CommandTarget.PLAYER)
    public void tpaccept(CommandSender commandSender, String[] args, String commandLabel)
    {
        Player player = (Player) commandSender;

        if (!(currentRequests.containsKey(player.getUniqueId().toString())))
        {
            player.sendMessage(plugin.prefix + ChatColor.RED + "Error: It appears you don't have any tp requests currently. Maybe it timed out?");
            return;
        }
        Request request = currentRequests.get(player.getUniqueId().toString());

        Player teleportingPlayer = null;
        Player targetPlayer = null;
        switch (request.type)
        {
            case HERE:
                teleportingPlayer = player;
                targetPlayer = plugin.getServer().getPlayer(request.getSender());
                break;
            case THERE:
                teleportingPlayer = plugin.getServer().getPlayer(request.getSender());
                targetPlayer = player;
                break;
        }

        currentRequests.remove(player.getUniqueId().toString());

        if (teleportingPlayer == null)
        {
            targetPlayer.sendMessage(plugin.prefix + ChatColor.RED + "Error: It appears that the person trying to teleport to you doesn't exist anymore.");
            return;
        }

        if (targetPlayer == null)
        {
            teleportingPlayer.sendMessage(plugin.prefix + ChatColor.RED + "Error: It appears that the person you are trying to teleport to doesn't exist anymore.");
            return;
        }

        teleportingPlayer.sendMessage(plugin.prefix + ChatColor.GRAY + "Teleporting...");
        targetPlayer.sendMessage(plugin.prefix + ChatColor.GRAY + "Teleporting...");
        TeleportSystem.teleport(plugin, teleportingPlayer, targetPlayer);
    }

    @BetterCommand(name = "tpdeny", target = CommandTarget.PLAYER)
    public void tpdeny(CommandSender commandSender, String[] args, String commandLabel)
    {
        Player player = (Player) commandSender;

        if (!(currentRequests.containsKey(player.getUniqueId().toString())))
        {
            player.sendMessage(plugin.prefix + ChatColor.RED + "Error: It appears you don't have any tp requests currently. Maybe it timed out?");
            return;
        }
        Request request = currentRequests.get(player.getUniqueId().toString());

        Player rejectedPlayer = plugin.getServer().getPlayer(request.getSender());
        currentRequests.remove(player.getUniqueId().toString());

        if (rejectedPlayer == null)
        {
            return;
        }

        rejectedPlayer.sendMessage(plugin.prefix + ChatColor.RED + player.getUniqueId().toString() + " rejected your teleport request! :(");
        player.sendMessage(plugin.prefix + ChatColor.GRAY + rejectedPlayer.getUniqueId().toString() + " was rejected!");
    }

    @BetterCommand(name = "test", target = CommandTarget.PLAYER)
    public void test(CommandSender commandSender, String[] args, String commandLabel)
    {
        plugin.getLogger().info("TEST!");
    }
}
