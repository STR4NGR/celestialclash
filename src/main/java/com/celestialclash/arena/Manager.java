package com.celestialclash.arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Manager {
    private Objective killsObjective;
    private Team redTeam, blueTeam;
    public Scoreboard scoreboard;
    public Boolean isPlayerReady = false;
    public double[] redSpawn = new double[]{-2.5, -52, -5.3, -155, 2};
    public double[] blueSpawn = new double[]{-8.181, -53, 13.758, 26, 5};

    public Manager() {
        setupScoreboard();
        createTeams();
    }

    private void createTeams() {
        if (scoreboard.getTeam("Red") == null) {
            redTeam = scoreboard.registerNewTeam("Red");
            redTeam.setPrefix("§c");
        }
        if (scoreboard.getTeam("Blue") == null) {
            blueTeam = scoreboard.registerNewTeam("Blue");
            blueTeam.setPrefix("§9");
        }
    }

    public void assignPlayerToTeam(Player player) {
        redTeam = scoreboard.getTeam("Red");
        blueTeam = scoreboard.getTeam("Blue");
        if (redTeam.getEntries().size() < 1) {
            redTeam.addEntry(player.getName());
            reSpawn(player, "Red");
            player.sendMessage("Вы в команде §cRed§r.");
        } else if (blueTeam.getEntries().size() < 1) {
            blueTeam.addEntry(player.getName());
            reSpawn(player, "Blue");
            player.sendMessage("Вы в команде §9Blue§r.");
        }
    }

    public void reSpawn(Player player, String team) {
        Location spawn;
        if (team == "Red") {
            spawn = new Location(Bukkit.getWorld("world"), redSpawn[0], redSpawn[1], redSpawn[2], (int) redSpawn[3], (int) redSpawn[4]);
            player.teleport(spawn);
        }
        if (team == "Blue") {
            spawn = new Location(Bukkit.getWorld("world"), blueSpawn[0], blueSpawn[1], blueSpawn[2], (int) blueSpawn[3], (int) blueSpawn[4]);
            player.teleport(spawn);
        }
    }

    public void checkOpponent(Player player) {
        if ((redTeam.getEntries().size() == 1) && (blueTeam.getEntries().size() == 1)) {
            isPlayerReady = true;
        } else isPlayerReady = false;
    }

    public void sendActionBar(Player player, String message) {
            TextComponent text = new TextComponent(message);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, text);
    }
    
    public void setupScoreboard() {
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        killsObjective = scoreboard.getObjective("Kills");
        if (killsObjective == null) {
            killsObjective = scoreboard.registerNewObjective("Kills", Criteria.DUMMY, "Kills");
            killsObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
    }

    public void clearArena(HashMap<UUID, ArrayList<Block>> blocksPlacedByPlayers) {
        World world = Bukkit.getWorld("world");
        List<Entity> entList = world.getEntities();
        for (Entity current : entList) {
            if (current instanceof Item) {
                current.remove();
            }
        }
        for (ArrayList<Block> blocks : blocksPlacedByPlayers.values()) {
            for (Block block : blocks) {
                block.setType(Material.AIR);
            }
        }
        blocksPlacedByPlayers.clear();
    }

    public void doorPrepare(Boolean isOpened) {
        World world = Bukkit.getWorld("world");
        Location [] doorsLoc = {
            new Location(world, -5.494, -53, 12.588),
            new Location(world, -4.488, -53, 12.588),
            new Location(world, -5.494, -53, -4.488),
            new Location(world, -4.488, -53, -4.488)
        };
        for (Location doorLoc : doorsLoc) {
            Block doorBlock = doorLoc.getBlock();
            if (doorBlock.getType() == Material.MANGROVE_DOOR || doorBlock.getType() == Material.WARPED_DOOR) {
                Door door = (Door) doorBlock.getBlockData();
                door.setOpen(isOpened);
                doorBlock.setBlockData(door);

            }
        }
    }

    public void removeItems(Player player) {
        ItemStack[] playerItems = player.getInventory().getContents();
        for (ItemStack item : playerItems) {
            if (item != null) {
                player.getPlayer().getInventory().remove(item);
            }
        }
    }
    
    
}
