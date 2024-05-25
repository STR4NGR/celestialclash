package com.celestialclash.pit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;

interface Item { }

enum Equipment implements Item {
    AIR(0),
    WOODEN_SWORD(1),
    STONE_SWORD(2),
    IRON_SWORD(3),
    GOLDEN_SWORD(4),
    DIAMOND_SWORD(5),
    WOODEN_AXE(6),
    STONE_AXE(7),
    IRON_AXE(8),
    GOLDEN_AXE(9),
    DIAMOND_AXE(10),
    BOW(11),
    CROSSBOW(12),
    POISON_ARROW(13),
    WEAKNESS_ARROW(14),
    SLOWNESS_ARROW(15),
    HARMING_ARROW(16),
    TRIDENT(17),
    CHAINMAIL_HELMET(18),
    SHIELD(19),
    LEATHER_HELMET(20),
    LEATHER_CHESTPLATE(21),
    LEATHER_LEGGINGS(22),
    LEATHER_BOOTS(23),
    CHAINMAIL_CHESTPLATE(24),
    CHAINMAIL_LEGGINGS(25),
    CHAINMAIL_BOOTS(26),
    IRON_HELMET(27),
    IRON_CHESTPLATE(28),
    IRON_LEGGINGS(29),
    IRON_BOOTS(30),
    GOLDEN_HELMET(31),
    GOLDEN_CHESTPLATE(32),
    GOLDEN_LEGGINGS(33),
    GOLDEN_BOOTS(34),
    DIAMOND_HELMET(35),
    DIAMOND_CHESTPLATE(36),
    DIAMOND_LEGGINGS(37),
    DIAMOND_BOOTS(38),
    POTION_HEALING(39),
    TOTEM_OF_UNDYING(40);

    private final int index;

    Equipment(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static Equipment getEquipmentByIndex(int index) {
        for (Equipment equipment : Equipment.values()) {
            if (equipment.getIndex() == index) {
                return equipment;
            }
        }
        return null; 
    }

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
        Collections.addAll(Items, Equipment.values());
        shuffleLoot(Items);
        return Items.subList(0, Math.min(collectionSize, Items.size()));
    }

    private List<Item> fillingVoids() {
        for (int i = Loot.size(); i <= size - 1; i++) {
            Loot.add(Equipment.AIR);
        }
        return Loot;
    }

    public List<Item> shuffleLoot(List<Item> Loot) {
        Collections.shuffle(Loot);
        return Loot;
    }

}

