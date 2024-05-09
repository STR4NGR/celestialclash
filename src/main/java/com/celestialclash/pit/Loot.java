package com.celestialclash.pit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;

interface Item { }

enum Weapon implements Item {
    WOODEN_SWORD,
    STONE_SWORD,
    IRON_SWORD,
    GOLDEN_SWORD,
    DIAMOND_SWORD,
    NETHERITE_SWORD,
    WOODEN_AXE,
    STONE_AXE,
    IRON_AXE,
    GOLDEN_AXE,
    DIAMOND_AXE,
    NETHERITE_AXE,
    WOODEN_SHOVEL,
    STONE_SHOVEL,
    IRON_SHOVEL,
    GOLDEN_SHOVEL,
    DIAMOND_SHOVEL,
    NETHERITE_SHOVEL,
    WOODEN_HOE,
    STONE_HOE,
    IRON_HOE,
    GOLDEN_HOE,
    DIAMOND_HOE,
    NETHERITE_HOE,
    BOW,
    CROSSBOW,
    TRIDENT,
    FIRE_CHARGE,
}

enum Arrow implements Item {
    ARROW
}

enum Equip implements Item {
    SHIELD,
    ENDER_PEARL,
    TOTEM_OF_UNDYING,
    LEATHER_HELMET,
    LEATHER_CHESTPLATE,
    LEATHER_LEGGINGS,
    LEATHER_BOOTS,
    CHAINMAIL_HELMET,
    CHAINMAIL_CHESTPLATE,
    CHAINMAIL_LEGGINGS,
    CHAINMAIL_BOOTS,
    IRON_HELMET,
    IRON_CHESTPLATE,
    IRON_LEGGINGS,
    IRON_BOOTS,
    GOLDEN_HELMET,
    GOLDEN_CHESTPLATE,
    GOLDEN_LEGGINGS,
    GOLDEN_BOOTS,
    DIAMOND_HELMET,
    DIAMOND_CHESTPLATE,
    DIAMOND_LEGGINGS,
    DIAMOND_BOOTS,
    NETHERITE_HELMET,
    NETHERITE_CHESTPLATE,
    NETHERITE_LEGGINGS,
    NETHERITE_BOOTS,
    GOLDEN_APPLE,
    COOKED_PORKCHOP,
    COOKED_BEEF,  
    POTION 
}

enum Filler implements Item {
    EMERALD,
    COBBLESTONE
}

public class Loot {
    private List<Item> Loot;
    private int size;
    private int collectionSize;

    public Loot(int size, int collectionSize) {
        this.size = size;
        this.collectionSize = collectionSize;
        fillLoot();
    }

    public String getAnswer() {
        StringBuilder result = new StringBuilder(); 
            for (Item item : Loot) {
                result.append(item).append(", ");
            }
            Bukkit.getLogger().info("" + Loot.size());
        return "" + result;
    }

    public List<Item> getItems() {
        return Loot;
    }

    private List<Item> fillLoot() {
        Loot = new ArrayList<Item>(); 
        Loot = randomLoot();
        fillingVoids();
        shuffleLoot(Loot);
        return Loot;
    }

    private List<Item> randomLoot() {
        List<Item> Items = new ArrayList<Item>();
        Collections.addAll(Items, Weapon.values());
        Collections.addAll(Items, Equip.values());
        shuffleLoot(Items);
        return Items.subList(0, Math.min(collectionSize, Items.size()));
    }

    private List<Item> fillingVoids() {
        for (int i = Loot.size(); i <= size - 1; i++) {
            if (i % 5 == 0) Loot.add(Filler.EMERALD);
            else Loot.add(Filler.COBBLESTONE);
        }
        return Loot;
    }

    public List<Item> shuffleLoot(List<Item> Loot) {
        Collections.shuffle(Loot);
        return Loot;
    }

}

