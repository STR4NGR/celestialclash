package com.celestialclash.pit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import com.celestialclash.utils.Vector3D;

public class Pit {
    private List<PitBlock> pitBlocks;
    private Vector3D startLocation;
    private Loot pitLoot;
    private int width = 4;
    private int length = 4;
    private int height = 9;

    public Pit(Vector3D startLocation) {
        this.startLocation = startLocation;
        pitBlocks = new ArrayList<PitBlock>();
        pitLoot = new Loot(width * height * length, 40);
        generatePit(width, length, height);
    }

    public Pit(Pit existingPit, Vector3D startLocation) {
        this.width = existingPit.width;
        this.length = existingPit.length;
        this.height = existingPit.height;
        this.startLocation = startLocation;
        this.pitBlocks = new ArrayList<PitBlock>();
        this.pitLoot = existingPit.getLoot();
        generatePit(this.width, this.length, this.height);
    }

    public Loot getLoot() {
        return pitLoot;
    }

    public String checkPit() {
        StringBuilder result = new StringBuilder(); 
        for (PitBlock element : pitBlocks) {
            result.append(element.getItem()).append("  "); 
        }      
        return "" + result; 
    }

    public List<PitBlock> getPitBlocks(Pit pit) {
        return pit.pitBlocks;
    }

    public PitBlock findBlockByCoords(Vector3D coords) {
        for (PitBlock pitblock: pitBlocks) {
            if ((pitblock.getPosition().getVector()).equals(coords.getVector())) {
                return pitblock;
            }
        }
        return null;
    }

    public void regeneratePit() {
        pitLoot.shuffleLoot(pitLoot.getItems());
        int index = 0;
        for (PitBlock pitBlock : pitBlocks) {
            Item selectedLoot = pitLoot.getItems().get(index % pitLoot.getItems().size());
            pitBlock.setItem(selectedLoot);
            index++;
        }
    }

    private List<PitBlock> generatePit(int width, int length, int height) {
        World world = Bukkit.getWorld("world");
        Location pitStart = new Location(world, startLocation.x(), startLocation.y(), startLocation.z());
        int blockIndex = 0;
        for (int x = 0; x <= width - 1; x++) {
            for (int z = 0; z <= length - 1; z++) {
                for (int y = 0; y <= height; y++) {
                    Location currentLocation = pitStart.clone().add(x, y, z);
                    currentLocation.getBlock().setType(Material.STONE);
                    Item selectedLoot = pitLoot.getItems().get(blockIndex % pitLoot.getItems().size());
                    pitBlocks.add(new PitBlock(new Vector3D(
                        currentLocation.getBlockX(), 
                        currentLocation.getBlockY(), 
                        currentLocation.getBlockZ()), 
                        selectedLoot));
                    blockIndex++;
                }
            }
        }
        return pitBlocks;
    }



}
