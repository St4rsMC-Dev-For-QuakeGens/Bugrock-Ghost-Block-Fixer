package com.example.bugrockfix;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.lang.reflect.Method;
import java.util.UUID;

public final class BlockUpdateListener implements Listener {

    private final BugrockFixPlugin plugin;
    private Object floodgateApi;
    private Method isFloodgatePlayerMethod;

    public BlockUpdateListener(BugrockFixPlugin plugin) {
        this.plugin = plugin;
        try {
            Class<?> apiClass = Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            floodgateApi = apiClass.getMethod("getInstance").invoke(null);
            isFloodgatePlayerMethod = apiClass.getMethod("isFloodgatePlayer", UUID.class);
            plugin.getLogger().info("Floodgate API detected, using UUID-based Bedrock player detection");
        } catch (Exception e) {
            floodgateApi = null;
            isFloodgatePlayerMethod = null;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!isBedrockPlayer(player)) return;

        Block block = event.getBlock();
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            BlockData air = Material.AIR.createBlockData();
            player.sendBlockChange(block.getLocation(), air);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!isBedrockPlayer(player)) return;

        Block block = event.getBlockPlaced();
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            player.sendBlockChange(block.getLocation(), block.getBlockData());
        });
    }

    private boolean isBedrockPlayer(Player player) {
        if (floodgateApi != null && isFloodgatePlayerMethod != null) {
            try {
                return (boolean) isFloodgatePlayerMethod.invoke(floodgateApi, player.getUniqueId());
            } catch (Exception e) {
                // fall through to prefix check
            }
        }
        String prefix = plugin.getConfig().getString("bedrock-prefix", ".");
        return player.getName().startsWith(prefix);
    }
}
