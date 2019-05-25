package io.github.caprapaul.bettercommandexecutor.initiators;

import io.github.caprapaul.bettercommandexecutor.listeners.ToggleListener;

import java.util.ArrayList;

public class ToggleInitiator
{
    private ArrayList<ToggleListener> listeners = new ArrayList<ToggleListener>();

    public void addListener(ToggleListener listener)
    {
        listeners.add(listener);
    }

    public void removeListener(ToggleListener listener)
    {
        listeners.remove(listener);
    }

    public void clearListeners()
    {
        listeners.clear();
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
