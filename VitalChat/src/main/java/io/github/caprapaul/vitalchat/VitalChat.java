package io.github.caprapaul.vitalchat;

import io.github.caprapaul.vitalcore.VitalCore;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VitalChat extends VitalCore
{
    @Override
    public void onEnabled()
    {
        loadCommands();
    }


    @Override
    public void onDisabled()
    {
    }

}
