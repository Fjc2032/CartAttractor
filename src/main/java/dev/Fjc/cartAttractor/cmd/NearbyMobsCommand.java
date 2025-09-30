package dev.Fjc.cartAttractor.cmd;

import dev.Fjc.cartAttractor.CartAttractor;
import dev.Fjc.cartAttractor.builder.FileBuilder;
import dev.Fjc.cartAttractor.listener.Attractor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NearbyMobsCommand implements CommandExecutor {

    private final CartAttractor plugin;

    private final FileBuilder fileBuilder;

    public NearbyMobsCommand(@NotNull CartAttractor plugin) {
        this.plugin = plugin;
        this.fileBuilder = this.plugin.getFileBuilder();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.isOp()) {
            sender.sendMessage("You need proper permissions before running this command.");
            return false;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to run this command.");
            return false;
        }

        List<? extends Mob> mobs = Attractor.getNearbyEntities(player.getLocation(), this.fileBuilder.getRadius());

        if (args.length == 0) {
            player.sendMessage("Amount nearby: " + mobs.size());
            for (Mob entity : mobs) {
                player.sendMessage(entity.toString());
            }
            return true;
        }
        return false;
    }
}
