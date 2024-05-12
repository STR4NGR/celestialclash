package com.celestialclash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.celestialclash.arena.Manager;
import com.celestialclash.pit.Loot;
import com.celestialclash.pit.Pit;
import com.celestialclash.pit.PitBlock;
import com.celestialclash.utils.Vector3D;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Main extends JavaPlugin implements Listener {

    public static final String CUSTOM_BLOCK_ID = "Celestial Stone";

    private Vector3D firstPitLocation, secondPitLocation;
    private PitBlock firstPitBlock, secondPitBlock;   
    private Pit firstPit, secondPit;
    private Boolean isNotMoving = true;
    private Boolean isDigging = true;

    private Objective killsObjective;
    private Manager manager;
    private Scoreboard scoreboard;

    private List<Player> connectedPlayers = new ArrayList<>();
    private Player killed;
    private int frags = 3;

    private HashMap<UUID, ArrayList<Block>> blocksPlacedByPlayers = new HashMap<>();


    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        manager = new Manager();
        String message = "Плагин успешно запущен и работает!";

        Bukkit.getServer().broadcastMessage(message);
        setupScoreboard();
        this.getLogger().info("Plugin is started!");
        
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("Добро пожаловать на сервер!");
        Player player = event.getPlayer();
        connectedPlayers.add(player);
        manager.assignPlayerToTeam(player);
        killsObjective.getScore(player.getName()).setScore(0);
        manager.checkOpponent(player);
        getReady(player, 7);
    
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        UUID playerUUID = player.getUniqueId();

        ArrayList<Block> playerBlocks = blocksPlacedByPlayers.getOrDefault(playerUUID, new ArrayList<>());
        playerBlocks.add(block);
        blocksPlacedByPlayers.put(playerUUID, playerBlocks);
    }


    private void initialize() {
        manager.doorPrepare(false);
        manager.clearArena(blocksPlacedByPlayers);
    }
    

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        if (message.contains("test")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(" ");
            event.getPlayer().sendMessage(" " + firstPit.checkPit());
            event.getPlayer().sendMessage(" ");
            event.getPlayer().sendMessage(" " + secondPit.checkPit());
        }
        if (message.contains("shuffle")) {
            event.setCancelled(true);
            firstPit.regeneratePit();
        }
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material blockType = block.getType();
        Location blockLocation = block.getLocation();

        int x = blockLocation.getBlock().getX();
        int y = blockLocation.getBlock().getY();
        int z = blockLocation.getBlock().getZ();

        if (isDigging) {
            if (blockType == Material.STONE) {
                event.setDropItems(false);
                if ((x <= -4) && (x >= -7) && (y <= -54) && (y >= -63) && (z >= 15) && (z <= 18)) {
                    event.setCancelled(false);
                    firstPitBlock = firstPit.findBlockByCoords(new Vector3D(block.getX(), block.getY(), block.getZ()));
                    event.getPlayer().sendMessage("Index " + firstPit.getPitBlocks(firstPit).indexOf(firstPitBlock) + ": " + firstPitBlock.getItem());
                    block.getWorld().dropItemNaturally(blockLocation, new ItemStack(firstPitBlock.Drop()));
                } else if ((x <= -3) && (x >= -7) && (y <= -54) && (y >= -63) && (z <= -7) && (z >= -11)) {
                    event.setCancelled(false);
                    secondPitBlock = secondPit.findBlockByCoords(new Vector3D(block.getX(), block.getY(), block.getZ()));
                    event.getPlayer().sendMessage("Index " + secondPit.getPitBlocks(secondPit).indexOf(secondPitBlock) + ": " + secondPitBlock.getItem());
                    block.getWorld().dropItemNaturally(blockLocation, new ItemStack(secondPitBlock.Drop()));
                } else {
                    event.getPlayer().sendMessage("Index " + x + y + z);
                    event.setCancelled(true);
                }
            }
        } else {
            event.setCancelled(true);
            event.getPlayer().sendMessage("Во время битвы на арене копать нельзя!");
        }
    }

    private void getReady(Player player, int delay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (manager.isPlayerReady) {
                    cancel();
                    pitTimer(player, delay);
                } else {
                    manager.sendActionBar(player, "Ожидание других игроков");
                }
            }
            
        }.runTaskTimer(this, 0, 20);
    }

    private void pitTimer(Player player, int delay) {
        manager.removeItems(player);
        ItemStack pickaxe = new ItemStack(Material.STONE_PICKAXE);
        player.getInventory().addItem(pickaxe);
        initialize();
        final int[] pitSeconds = {delay};
        Team redTeam = scoreboard.getTeam("Red");
        Team blueTeam = scoreboard.getTeam("Blue");
        isNotMoving = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (connectedPlayers.size() > 1) {
                    if (pitSeconds[0] >= 0) {
                        manager.sendActionBar(player, "Время до начала: " + pitSeconds[0]);
                        pitSeconds[0]--;
                    } else {
                        isNotMoving = false;
                        cancel();
                        digTimer(player, 60);
                    }
                } else if (connectedPlayers.size() == 1) {
                    cancel();
                    isNotMoving = true;
                    manager.isPlayerReady = false;
                    if (redTeam.hasEntry(player.getName())) {
                        manager.reSpawn(player, "Red");
                    } else if (blueTeam.hasEntry(player.getName())) {
                        manager.reSpawn(player, "Blue");
                    }
                    getReady(player, delay);
                } else { cancel(); }
            }
            
        }.runTaskTimer(this, 0, 20);
    }

    private void digTimer(Player player, int delay) {
        final int[] digSeconds = {delay};
        Team redTeam = scoreboard.getTeam("Red");
        Team blueTeam = scoreboard.getTeam("Blue");
        new BukkitRunnable() {
            @Override
            public void run() {
                if (connectedPlayers.size() > 1) {
                    if (digSeconds[0] >= 0) {
                        manager.sendActionBar(player, "Время до выхода на арену: " + digSeconds[0]);
                        digSeconds[0]--;
                    } else {
                        isDigging = false;
                        cancel();
                        manager.sendActionBar(player, "Больше копать нельзя! Пройдите на арену");
                        manager.doorPrepare(true);
                    }
                } else if (connectedPlayers.size() == 1) {
                    cancel();
                    isNotMoving = true;
                    manager.isPlayerReady = false;
                    if (redTeam.hasEntry(player.getName())) {
                        manager.reSpawn(player, "Red");
                    } else if (blueTeam.hasEntry(player.getName())) {
                        manager.reSpawn(player, "Blue");
                    }
                    getReady(player, 7);                    
                } else { cancel(); }
            }
            
        }.runTaskTimer(this, 0, 20);
    }


    private void afterDeath(Player player) {
        final int[] newRound = {5};
        Team redTeam = scoreboard.getTeam("Red");
        Team blueTeam = scoreboard.getTeam("Blue");
        new BukkitRunnable() {
            @Override
            public void run() {
                if (newRound[0] >= 0) {
                    manager.sendActionBar(player, "До начала следующего раунда: " + newRound[0]);
                    newRound[0]--;
                } else {
                    cancel();
                    if (redTeam.hasEntry(player.getName())) {
                        manager.reSpawn(player, "Red");
                    } else if (blueTeam.hasEntry(player.getName())) {
                        manager.reSpawn(player, "Blue");
                    }
                    pitTimer(player, 7);
                }
            }
            
        }.runTaskTimer(this, 0, 20);
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        event.setCancelled(isNotMoving);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if ((event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.STONE_BUTTON) && (isNotMoving)) {
            player.sendMessage("Расположение вещей в яме изменено!");
            firstPit.regeneratePit();
        }
        if (event.getClickedBlock() != null) {
            Material blockType = event.getClickedBlock().getType();
            if (blockType == Material.WARPED_DOOR || blockType == Material.MANGROVE_DOOR) {
                if (isDigging)  player.sendMessage("Время арены еще не пришло!");
                event.setCancelled(isDigging);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        killed = event.getEntity();
        Player killer = killed.getKiller();
        killsObjective = scoreboard.getObjective("Kills");

        if (killer != null) {
            int currentKills = killsObjective.getScore(killer.getName()).getScore();
            if (currentKills == (frags - 1)) {
                isNotMoving = true;
                manager.sendActionBar(killed, "Матч выиграл: " + killer.getName());
                manager.sendActionBar(killer, "Матч выиграл: " + killer.getName());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        startNewMatch(killer, killed); // Метод для начала нового матча
                    }
                }.runTaskLater(this, 60L); // 60 тиков = 3 секунды    
        
            } else {
                afterDeath(killed);
                afterDeath(killer);
            }
            killsObjective.getScore(killer.getName()).setScore(currentKills + 1);
        }

    }

    public void startNewMatch(Player killer, Player killed) {
        killsObjective.getScore(killed.getName()).setScore(0);
        killsObjective.getScore(killer.getName()).setScore(0);    
        Team redTeam = scoreboard.getTeam("Red");
        Team blueTeam = scoreboard.getTeam("Blue");
        if (redTeam.hasEntry(killer.getName())) {
            manager.reSpawn(killer, "Red");
        } else if (blueTeam.hasEntry(killer.getName())) {
            manager.reSpawn(killer, "Blue");
        }
        if (redTeam.hasEntry(killed.getName())) {
            manager.reSpawn(killed, "Red");
        } else if (blueTeam.hasEntry(killed.getName())) {
            manager.reSpawn(killed, "Blue");
        }
        getReady(killer, 7);
        getReady(killed, 7);

    }

    public void setupScoreboard() {
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        killsObjective = scoreboard.getObjective("Kills");
        if (killsObjective == null) {
            killsObjective = scoreboard.registerNewObjective("Kills", Criteria.DUMMY, "Kills");
            killsObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Team redTeam = scoreboard.getTeam("Red");
        Team blueTeam = scoreboard.getTeam("Blue");
        Location respawnLocation = new Location(Bukkit.getWorld("world"), -2.5, -52, -5.3, -155, 2);
        if (redTeam.hasEntry(killed.getName())) { 
            respawnLocation = new Location(Bukkit.getWorld("world"), -2.5, -52, -5.3, -155, 2);
        }
        if (blueTeam.hasEntry(killed.getName())) {
            respawnLocation = new Location(Bukkit.getWorld("world"), -8.181, -53, 13.758, 26, 5);           
        }
        event.setRespawnLocation(respawnLocation);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Team redTeam = scoreboard.getTeam("Red");
        Team blueTeam = scoreboard.getTeam("Blue");

        if (redTeam.hasEntry(player.getName())) {
            redTeam.removeEntry(player.getName());
        } else if (blueTeam.hasEntry(player.getName())) {
            blueTeam.removeEntry(player.getName());
        }

        manager.removeItems(player);
        manager.isPlayerReady = false;
        connectedPlayers.remove(player);
        if (!connectedPlayers.isEmpty()) {
            getReady(connectedPlayers.get(connectedPlayers.size() - 1), 7);
        }

        killsObjective.getScore(player.getName()).setScore(0);
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        initialize();
        firstPitLocation = new Vector3D(-6.700, -63, 15.300);
        secondPitLocation = new Vector3D(-6.5, -63, -11);
        firstPit = new Pit(firstPitLocation);
        secondPit = new Pit(firstPit, secondPitLocation);
    }

}

