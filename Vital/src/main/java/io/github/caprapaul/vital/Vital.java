package io.github.caprapaul.vital;

import io.github.caprapaul.vital.data.Warp;
import io.github.caprapaul.vitalcore.VitalCore;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

public class Vital extends VitalCore
{

    @Override
    public void onEnabled()
    {
        ConfigurationSerialization.registerClass(Warp.class, "Warp");

        loadCommands();
    }


    @Override
    public void onDisabled()
    {
    }
}