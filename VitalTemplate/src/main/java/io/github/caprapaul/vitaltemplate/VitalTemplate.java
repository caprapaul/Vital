package io.github.caprapaul.vitaltemplate;

import io.github.caprapaul.vitalcore.VitalCore;

public class VitalTemplate extends VitalCore
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
