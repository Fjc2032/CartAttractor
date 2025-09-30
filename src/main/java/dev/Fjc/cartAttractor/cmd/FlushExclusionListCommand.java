package dev.Fjc.cartAttractor.cmd;

import dev.Fjc.cartAttractor.CartAttractor;
import dev.Fjc.cartAttractor.listener.Ejector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class FlushExclusionListCommand implements CommandExecutor {

    private final CartAttractor plugin;

    private static Ejector ejector;

    public FlushExclusionListCommand(@NotNull CartAttractor plugin) {
        this.plugin = plugin;
        ejector = CartAttractor.getEjector();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.isOp()) {
            sender.sendMessage("You need proper permissions before running this command.");
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage("Clearing the exclusion list now...");
            ejector.clear();
            return true;
        }

        return false;
    }
}
