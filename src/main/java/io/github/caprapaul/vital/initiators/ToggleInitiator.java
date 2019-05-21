package io.github.caprapaul.vital.initiators;

import io.github.caprapaul.vital.listeners.ToggleListener;

import java.util.ArrayList;

public class ToggleInitiator
{
    private ArrayList<ToggleListener> listeners = new ArrayList<ToggleListener>();

    public void addListener(ToggleListener listener)
    {
        listeners.add(listener);
    }

    public void enable()
    {
        for (ToggleListener l : listeners)
        {
            l.onEnable();
        }
    }

    public void disable()
    {
        for (ToggleListener l : listeners)
        {
            l.onDisable();
        }
    }
}
