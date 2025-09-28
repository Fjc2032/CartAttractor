package dev.Fjc.cartAttractor.cmd;

import dev.Fjc.cartAttractor.CartAttractor;
import dev.Fjc.cartAttractor.builder.FileBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class GetEjectCommand implements CommandExecutor {

    private final CartAttractor plugin;

    private final FileBuilder fileBuilder;

    public GetEjectCommand(@NotNull CartAttractor plugin) {
        this.plugin = plugin;
        this.fileBuilder = this.plugin.getFileBuilder();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.isOp()) {
            sender.sendMessage("You need proper permissions before running this command.");
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage("Eject chance: " + this.fileBuilder.getEjectChance());
        }
        return true;
    }
}
