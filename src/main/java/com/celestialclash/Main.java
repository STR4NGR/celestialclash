package com.celestialclash;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.celestialclash.pit.Loot;
import com.celestialclash.pit.Pit;
import com.celestialclash.pit.PitBlock;
import com.celestialclash.utils.Vector3D;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Main extends JavaPlugin implements Listener {

    public static final String CUSTOM_BLOCK_ID = "Celestial Stone";

    private Vector3D startLocation;
    private PitBlock foundedBlock; 
    private Pit firstPit;
    private Boolean isSwitching = true;
    private Boolean isDigging = true;


    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        String message = "Плагин успешно запущен и работает!";

        Bukkit.getServer().broadcastMessage(message);
        this.getLogger().info("Plugin is started!");
        
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("Добро пожаловать на сервер!");
        Player player = event.getPlayer();
        Location loc;
        if (player.getName().equals("STRANGR")) {
            loc = new Location(Bukkit.getWorld("world"), -8.181, -53, 13.758, 26, 5);
            player.teleport(loc);
            pitTimer(player, 7);
            initialize(player);
        }
        
        //Player player = event.getPlayer();
        //ItemStack pickaxe = new ItemStack(Material.WARPED_DOOR);
        //ItemStack pickaxe = new ItemStack(Material.CRIMSON_DOOR);
        //player.getInventory().addItem(pickaxe);
    }

    private void initialize(Player player) {
        Location doorLocation = new Location(player.getWorld(), -5.494, -53, 12.588); // Замените x, y, z на координаты вашей двери
        Block doorBlock = doorLocation.getBlock();
        if (doorBlock.getType() == Material.WARPED_DOOR) {
            Door door = (Door) doorBlock.getBlockData();
            door.setOpen(false);
            doorBlock.setBlockData(door);
        }
    }
    

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        Loot test = new Loot(96, 40);
        if (message.contains("test")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(" ");
           event.getPlayer().sendMessage(" " + firstPit.checkPit());
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
            if ((x <= -4) && (x >= -7) && (y <= -54) && (y >= -63) && (z >= 15) && (z <= 18)) {
                event.setCancelled(false);
            } else {
                event.getPlayer().sendMessage("Index " + x + y + z);
                event.setCancelled(true);
            }
            // Проверяем тип блока
            if (blockType == Material.STONE) {
                // Получаем координаты блока
                event.setDropItems(false);

                foundedBlock = firstPit.findBlockByCoords(new Vector3D(block.getX(), block.getY(), block.getZ()));
                event.getPlayer().sendMessage("Index " + firstPit.getPitBlocks(firstPit).indexOf(foundedBlock) + ": " + foundedBlock.getItem());
                block.getWorld().dropItemNaturally(blockLocation, new ItemStack(foundedBlock.Drop()));

            }
        } else {
            event.setCancelled(true);
            event.getPlayer().sendMessage("Больше копать нельзя! Пройдите на арену");
        }
    }

    private void pitTimer(Player player, int delay) {
        final int[] pitSeconds = {delay};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (pitSeconds[0] >= 0) {
                    sendActionBar(player, "Время до начала: " + pitSeconds[0]);
                    pitSeconds[0]--;
                } else {
                    isSwitching = false;
                    diggingTimer();
                }
            }
            
        }.runTaskTimer(this, 0, 20);
    }


    private void diggingTimer() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            isDigging = false;
            Bukkit.broadcastMessage("Больше копать нельзя! Пройдите на арену");
        }, 20 * 60);
}

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        event.setCancelled(isSwitching);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if ((event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.STONE_BUTTON) && (isSwitching)) {
            player.sendMessage("Расположение вещей в яме изменено!");
            firstPit.regeneratePit();
        }
        if (event.getClickedBlock() != null) {
            Material blockType = event.getClickedBlock().getType();
            if (blockType == Material.WARPED_DOOR || blockType == Material.CRIMSON_DOOR) {
                if (isDigging)  player.sendMessage("Время арены еще не пришло!");
                event.setCancelled(isDigging);
            }
        }
    }

    public void sendActionBar(Player player, String message) {
            TextComponent text = new TextComponent(message);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, text);
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        startLocation = new Vector3D(-6.700, -63, 15.300);
        firstPit = new Pit(startLocation);
    }

}

