package com.celestialclash.pit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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

    public EquipmentDropResult Drop(World world, Location blockLocation) {
        Material dropType = getMaterial(item);
        if (item == Equipment.POISON_ARROW) {
            ItemStack arrow = new ItemStack(Material.TIPPED_ARROW, 5);
            PotionMeta meta = (PotionMeta) arrow.getItemMeta();
            meta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 1000, 1), true);
            meta.setDisplayName("Стрела отравления");
            arrow.setItemMeta(meta);
            world.dropItemNaturally(blockLocation, new ItemStack(arrow));
        } else if (item == Equipment.WEAKNESS_ARROW) {
            ItemStack arrow = new ItemStack(Material.TIPPED_ARROW, 5);
            PotionMeta meta = (PotionMeta) arrow.getItemMeta();
            meta.addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1000, 1), true);
            meta.setDisplayName("Стрела слабости");
            arrow.setItemMeta(meta);
            world.dropItemNaturally(blockLocation, new ItemStack(arrow));
        } else if (item == Equipment.SLOWNESS_ARROW) {
            ItemStack arrow = new ItemStack(Material.TIPPED_ARROW, 5);
            PotionMeta meta = (PotionMeta) arrow.getItemMeta();
            meta.addCustomEffect(new PotionEffect(PotionEffectType.SLOWNESS, 1000, 1), true);
            meta.setDisplayName("Стрела замедления");
            arrow.setItemMeta(meta);
            world.dropItemNaturally(blockLocation, new ItemStack(arrow));
        } else if (item == Equipment.HARMING_ARROW) {
            ItemStack arrow = new ItemStack(Material.TIPPED_ARROW, 5);
            PotionMeta meta = (PotionMeta) arrow.getItemMeta();
            meta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1000, 1), true);
            meta.setDisplayName("Стрела вреда");
            arrow.setItemMeta(meta);
            world.dropItemNaturally(blockLocation, new ItemStack(arrow));
        } else if (item == Equipment.POTION_HEALING) {
            ItemStack potionItem = new ItemStack(Material.POTION);
            PotionMeta meta = (PotionMeta) potionItem.getItemMeta();
            meta.addCustomEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 600, 1), true);
            meta.setDisplayName("Зелье исцеления");
            potionItem.setItemMeta(meta);
            world.dropItemNaturally(blockLocation, new ItemStack(potionItem));
        } else { 
            world.dropItemNaturally(blockLocation, new ItemStack(dropType));
        }
        Equipment equip = Equipment.AIR;
        equip = (Equipment) item; 

        return new EquipmentDropResult(equip);
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