package io.github.caprapaul.vital.data;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.Map;

@SerializableAs("Warp")
public class Warp implements ConfigurationSerializable
{
    private String name;
    private String world;
    private double x;
    private double y;
    private double z;

    public Warp()
    {
        this.name = "";
        this.world = "";
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Warp(String name, Location location)
    {
        this.name = name;
        this.world = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
    }

    public Warp(String name, String world, double x, double y, double z)
    {
        this.name = name;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public double getX()
    {
        return x;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public double getZ()
    {
        return z;
    }

    public void setZ(double z)
    {
        this.z = z;
    }

    public String getWorld()
    {
        return world;
    }

    public void setWorld(String world)
    {
        this.world = world;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (!(obj instanceof Warp))
        {
            return false;
        }

        Warp warp = (Warp) obj;
        return name.equals(warp.name) &&
                world.equals(warp.world) &&
                Double.compare(x, warp.x) == 0 &&
                Double.compare(y, warp.y) == 0 &&
                Double.compare(z, warp.z) == 0;
    }

    public Location toLocation(JavaPlugin plugin)
    {
        return new Location(plugin.getServer().getWorld(world), x, y,z);
    }

    public Map<String, Object> serialize()
    {
        LinkedHashMap result = new LinkedHashMap();

        result.put("name", name);
        result.put("world", world);
        result.put("x", x);
        result.put("y", y);
        result.put("z", z);

        return result;
    }

    public static Warp deserialize(Map<String, Object> args)
    {
        String name = "";
        String world = "";
        double x = 0.0D;
        double y = 0.0D;
        double z = 0.0D;

        if(args.containsKey("name"))
        {
            name = (String) args.get("name");
        }

        if(args.containsKey("world"))
        {
            world = (String) args.get("world");
        }

        if(args.containsKey("x"))
        {
            x = (Double) args.get("x");
        }

        if(args.containsKey("y"))
        {
            y = (Double) args.get("y");
        }

        if(args.containsKey("z"))
        {
            z = (Double) args.get("z");
        }

        return new Warp(name, world, x, y, z);
    }
}
