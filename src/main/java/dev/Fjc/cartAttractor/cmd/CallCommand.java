package dev.Fjc.cartAttractor.cmd;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.destroystokyo.paper.entity.Pathfinder;
import dev.Fjc.cartAttractor.CartAttractor;
import dev.Fjc.cartAttractor.builder.FileBuilder;
import dev.Fjc.cartAttractor.builder.PathManager;
import dev.Fjc.cartAttractor.listener.Attractor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CallCommand implements CommandExecutor {

    private final CartAttractor plugin;

    private final FileBuilder fileBuilder;

    private final PathManager manager;

    private final Attractor listener;

    public CallCommand(@NotNull CartAttractor plugin) {
        this.plugin = plugin;
        this.fileBuilder = this.plugin.getFileBuilder();
        this.manager = new PathManager(this.plugin);
        this.listener = new Attractor(this.plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.isOp()) {
            sender.sendMessage("You must have proper permissions before running this command.");
            return false;
        }

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("You must be a player to run this command.");
                return false;
            }
            if (!player.isInsideVehicle() || !(player.getVehicle() instanceof Minecart)) {
                player.sendMessage("You must be inside a minecart to run this command.");
                return false;
            }

            MinecartMember<?> initial = MinecartMemberStore.getFromEntity(player.getVehicle());
            MinecartGroup group = initial.getGroup();
            Location target = listener.getLastAvailableCar(group);

            if (target == null) {
                player.sendMessage("The location is null. It either no longer exists or was modified somehow.");
                return false;
            }
            for (Mob entity : Attractor.getNearbyEntities(player.getLocation(), this.fileBuilder.getRadius())) {
                Pathfinder.PathResult result = manager.buildPath(
                        listener.getLastAvailableCar(group),
                        entity,
                        true
                );

                if (result == null) {
                    player.sendMessage("The path specified does not exist.");
                    return false;
                }

                if (listener.getLastAvailableCar(group) == null) continue;

                if (entity.getLocation().distanceSquared(listener.getLastAvailableCar(group, entity)) <= 0.15) continue;
                if (Objects.nonNull(manager.buildPath(listener.getLastAvailableCar(group), entity, true)))
                    manager.buildPath(listener.getLastAvailableCar(group), entity, true);
                else entity.teleport(listener.getLastAvailableCar(group, entity));
            }
            sender.sendMessage("Attempting to call potential nearby passengers...");
            return true;
        }
        return false;
    }
}
