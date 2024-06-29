package com.k1nz.freezepro;

import com.k1nz.freezepro.Commands.FreezeCommand;
import com.k1nz.freezepro.Commands.UnfreezeCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FreezePro extends JavaPlugin implements Listener {

    private final Set<UUID> frozenPlayers = new HashSet<>();
    private String freezeTitle;
    private String freezeSubtitle;
    private String unfreezeActionbar;
    private File frozenPlayersFile;
    private FileConfiguration frozenPlayersConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadMessagesFromConfig();
        loadFrozenPlayers();

        getServer().getPluginManager().registerEvents(this, this);
        getCommand("freeze").setExecutor(new FreezeCommand(this));
        getCommand("unfreeze").setExecutor(new UnfreezeCommand(this));

        startTitleTask();
    }

    @Override
    public void onDisable() {
        saveFrozenPlayers();
    }

    public Set<UUID> getFrozenPlayers() {
        return frozenPlayers;
    }

    public String getFreezeTitle() {
        return freezeTitle;
    }

    public String getFreezeSubtitle() {
        return freezeSubtitle;
    }

    public String getUnfreezeActionbar() {
        return unfreezeActionbar;
    }

    private void loadMessagesFromConfig() {
        FileConfiguration config = getConfig();
        freezeTitle = ChatColor.translateAlternateColorCodes('&', config.getString("freeze.title", "&b&lZAMROŻONY"));
        freezeSubtitle = ChatColor.translateAlternateColorCodes('&', config.getString("freeze.subtitle", "&aPoczekaj na start serwera"));
        unfreezeActionbar = ChatColor.translateAlternateColorCodes('&', config.getString("unfreeze.actionbar", "&aZostałeś odmrożony"));
    }

    private void loadFrozenPlayers() {
        frozenPlayersFile = new File(getDataFolder(), "frozenPlayers.yml");
        if (!frozenPlayersFile.exists()) {
            frozenPlayersFile.getParentFile().mkdirs();
            saveResource("frozenPlayers.yml", false);
        }
        frozenPlayersConfig = YamlConfiguration.loadConfiguration(frozenPlayersFile);
        for (String key : frozenPlayersConfig.getKeys(false)) {
            frozenPlayers.add(UUID.fromString(key));
        }
    }

    private void saveFrozenPlayers() {
        frozenPlayersConfig = new YamlConfiguration();
        for (UUID uuid : frozenPlayers) {
            frozenPlayersConfig.set(uuid.toString(), true);
        }
        try {
            frozenPlayersConfig.save(frozenPlayersFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void freezePlayer(UUID playerUUID) {
        frozenPlayers.add(playerUUID);
        saveFrozenPlayers();
    }

    public void unfreezePlayer(UUID playerUUID) {
        frozenPlayers.remove(playerUUID);
        saveFrozenPlayers();
    }

    private void startTitleTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : frozenPlayers) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null && player.isOnline()) {
                        player.sendTitle(freezeTitle, freezeSubtitle, 0, 20, 0);
                    }
                }
            }
        }.runTaskTimer(this, 0L, 30L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (frozenPlayers.contains(player.getUniqueId())) {
            player.sendTitle(freezeTitle, freezeSubtitle, 0, 20, 0);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (frozenPlayers.contains(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendTitle(freezeTitle, freezeSubtitle, 0, 20, 0);
        }
    }
}
