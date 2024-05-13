package com.celestialclash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.celestialclash.arena.Manager;
import com.celestialclash.pit.Pit;
import com.celestialclash.pit.PitBlock;
import com.celestialclash.utils.Vector3D;

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
    private int frags = 25;
    private int startDelay = 7;

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
        getReady(player, startDelay);
    
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
                    {
                        event.setCancelled(false);
                        firstPitBlock = firstPit.findBlockByCoords(new Vector3D(block.getX(), block.getY(), block.getZ()));
                        Material dropType = firstPitBlock.Drop();
                        if (dropType == Material.BOW || dropType == Material.CROSSBOW) {
                            block.getWorld().dropItemNaturally(blockLocation, new ItemStack(dropType));
                            block.getWorld().dropItemNaturally(blockLocation, new ItemStack(Material.ARROW, 12));
                        } else {
                            block.getWorld().dropItemNaturally(blockLocation, new ItemStack(dropType));
                        }
                    }
                    } else if ((x <= -3) && (x >= -7) && (y <= -54) && (y >= -63) && (z <= -7) && (z >= -11)) {
                        event.setCancelled(false);
                        secondPitBlock = secondPit.findBlockByCoords(new Vector3D(block.getX(), block.getY(), block.getZ()));
                        Material dropType = secondPitBlock.Drop();
                        if (dropType == Material.BOW || dropType == Material.CROSSBOW) {
                            block.getWorld().dropItemNaturally(blockLocation, new ItemStack(dropType));
                            block.getWorld().dropItemNaturally(blockLocation, new ItemStack(Material.ARROW, 12));
                        } else {
                            block.getWorld().dropItemNaturally(blockLocation, new ItemStack(dropType));
                        }
                } else {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
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
        pitStart();
        isDigging = true;
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
                    getReady(player, startDelay);                    
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
                    pitTimer(player, startDelay);
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
        Vector3D redButton = new Vector3D(-2, -51, -9);
        Vector3D blueButton = new Vector3D(-9, -52, 15);
        if ((event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.STONE_BUTTON) && (isNotMoving)) {
            Location buttonLocation = event.getClickedBlock().getLocation();
            Vector3D blockLoc = new Vector3D(buttonLocation.getBlockX(), buttonLocation.getBlockY(), buttonLocation.getBlockZ());
            if (blockLoc.getVector().equals(redButton.getVector())) {
                player.sendMessage("Расположение вещей в яме изменено!");
                secondPit.regeneratePit();
            }
            if (blockLoc.getVector().equals(blueButton.getVector())) {
                player.sendMessage("Расположение вещей в яме изменено!");
                firstPit.regeneratePit();
            }
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
                        startNewMatch(killer, killed);
                    }
                }.runTaskLater(this, 60L);  
        
            } else {
                afterDeath(killed);
                afterDeath(killer);
            }
            killsObjective.getScore(killer.getName()).setScore(currentKills + 1);
        }

    }

    public void prepareNewMatch(Player player) {
        Team redTeam = scoreboard.getTeam("Red");
        Team blueTeam = scoreboard.getTeam("Blue");
        killsObjective.getScore(player.getName()).setScore(0);
        if (redTeam.hasEntry(player.getName())) {
            manager.reSpawn(player, "Red");
        } else if (blueTeam.hasEntry(player.getName())) {
            manager.reSpawn(player, "Blue");
        }
        getReady(player, startDelay);
    }

    public void startNewMatch(Player killer, Player killed) {
        prepareNewMatch(killed);
        prepareNewMatch(killer);
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
        Location redSpawn = new Location(Bukkit.getWorld("world"), -2.5, -52, -5.3, -155, 2);
        Location blueSpawn = new Location(Bukkit.getWorld("world"), -8.181, -53, 13.758, 26, 5);  
        if (redTeam.hasEntry(killed.getName())) { 
            event.setRespawnLocation(redSpawn); 
        }
        if (blueTeam.hasEntry(killed.getName())) {
            event.setRespawnLocation(blueSpawn);         
        }
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
            getReady(connectedPlayers.get(connectedPlayers.size() - 1), startDelay);
        }

        killsObjective.getScore(player.getName()).setScore(0);
    }

    public void pitStart() {
        initialize();
        firstPitLocation = new Vector3D(-6.700, -63, 15.300);
        secondPitLocation = new Vector3D(-6.5, -63, -11);
        firstPit = new Pit(firstPitLocation);
        secondPit = new Pit(firstPit, secondPitLocation);       
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        pitStart();
    }

}

