package com.celestialclash.pit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import com.celestialclash.utils.Vector3D;

public class PitBlock {
    private Vector3D position; 
    private Item item;

    public PitBlock(Vector3D vector, Item item) {
        this.position = vector;
        this.item = item;
    }

    public void setItem(Item replaceItem) {
        this.item = replaceItem;
    }

    public Material Drop() {
        return getMaterial(item);
    }

    public Item getItem() {
        return item;
    }

    public Vector3D getPosition() {
        return position;
    }

    private Material getMaterial(Item item) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("CelestialClash");
        Material material;
        try {
            material = Material.matchMaterial(String.valueOf(item));
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().info("Invalid material name");
            Bukkit.getServer().getPluginManager().disablePlugin(plugin);
            material = null;
        }
        return material;
    }
    
}