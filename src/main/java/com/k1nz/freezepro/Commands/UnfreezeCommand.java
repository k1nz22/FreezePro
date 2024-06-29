package com.k1nz.freezepro.Commands;

import com.k1nz.freezepro.FreezePro;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnfreezeCommand implements CommandExecutor {

    private final FreezePro plugin;

    public UnfreezeCommand(FreezePro plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            return false;
        }

        if (args[0].equals("*")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID playerUUID = player.getUniqueId();
                plugin.getFrozenPlayers().remove(playerUUID);
                player.resetTitle();
                player.sendActionBar(plugin.getUnfreezeActionbar());
                sender.sendMessage(ChatColor.GREEN + "Odmroziłeś wszystkich graczy!");
            }
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                UUID targetUUID = target.getUniqueId();
                plugin.getFrozenPlayers().remove(targetUUID);
                target.resetTitle();
                target.sendActionBar(plugin.getUnfreezeActionbar());
                sender.sendMessage(ChatColor.GREEN + "Odmroziłeś gracza: " + target.getName()+"!");
            } else {
                sender.sendMessage("Gracz nie został znaleziony.");
            }
        }
        return true;
    }
}
