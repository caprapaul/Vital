package io.github.caprapaul.vital;

import io.github.caprapaul.vitalcore.VitalCore;

public class Vital extends VitalCore
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
