package com.k1nz.freezepro.Commands;

import com.k1nz.freezepro.FreezePro;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FreezeCommand implements CommandExecutor {

    private final FreezePro plugin;

    public FreezeCommand(FreezePro plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            return false;
        }

        if (args[0].equals("*")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasPermission("freezepro.bypass")) {
                    UUID playerUUID = player.getUniqueId();
                    plugin.getFrozenPlayers().add(playerUUID);
                    player.sendTitle(plugin.getFreezeTitle(), plugin.getFreezeSubtitle(), 0, 20, 0);
                    sender.sendMessage(ChatColor.GREEN + "Zamroziłeś wszystkich graczy!");
                }
            }
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null && !target.hasPermission("freezepro.bypass")) {
                UUID targetUUID = target.getUniqueId();
                plugin.getFrozenPlayers().add(targetUUID);
                target.sendTitle(plugin.getFreezeTitle(), plugin.getFreezeSubtitle(), 0, 20, 0);
                sender.sendMessage(ChatColor.GREEN + "Zamroziłeś gracza: " + target.getName()+"!");
            } else {
                sender.sendMessage(ChatColor.RED + "Gracz nie został znaleziony lub ma uprawnienia bypass.");
            }
        }
        return true;
    }
}
