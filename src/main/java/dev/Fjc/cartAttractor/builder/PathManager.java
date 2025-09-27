package dev.Fjc.cartAttractor.builder;

import com.destroystokyo.paper.entity.Pathfinder;
import com.destroystokyo.paper.entity.ai.*;
import dev.Fjc.cartAttractor.CartAttractor;
import dev.Fjc.cartAttractor.builder.goals.DocileGoal;
import dev.Fjc.cartAttractor.builder.goals.NoInvisGoal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Mob;
import org.bukkit.entity.WanderingTrader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PathManager {

    private final CartAttractor plugin;

    private Pathfinder pathfinder;

    /**
     * The main manager that handles all Mob path routes
     * @param plugin The plugin hosting the manager.
     */
    public PathManager(@NotNull CartAttractor plugin) {
        this.plugin = plugin;
    }

    /**
     * Builds a new path for a Mob to follow. May return null if the path is unreachable.
     * @param location The location the Mob should go
     * @param entity The Mob you are targeting
     * @return The path, or null if unreachable
     */
    public @Nullable Pathfinder.PathResult buildPath(Location location, Mob entity) {
        this.pathfinder = entity.getPathfinder();
        pathfinder.setCanOpenDoors(true);
        pathfinder.setCanPassDoors(true);
        pathfinder.setCanFloat(true);
        applyGoals(entity);

        pathfinder.moveTo(location);

        return pathfinder.findPath(location);
    }

    /**
     * Builds a new path for a Mob to follow, then assigns the path to that Mob.
     * If the location is unreachable, the Mob will just teleport to it if "versatile" is true.
     * @param location The location the Mob should go
     * @param entity The Mob you are targeting
     * @param versatile Whether the Mob can use unorthodox methods to reach its location
     * @return The path, or null if the path is unreachable
     */
    public @Nullable Pathfinder.PathResult buildPath(Location location, Mob entity, boolean versatile) {
        this.pathfinder = entity.getPathfinder();
        pathfinder.setCanOpenDoors(versatile);
        pathfinder.setCanPassDoors(versatile);
        pathfinder.setCanFloat(versatile);
        applyGoals(entity);

        if (entity.isInsideVehicle()) return null;

        if (!pathfinder.findPath(location).canReachFinalPoint() || pathfinder.findPath(location) == null) {
            if (versatile) entity.teleport(location);
            else {
                pathfinder.moveTo(location);
                return pathfinder.findPath(location);
            }
        }

        return pathfinder.findPath(nonNullLocation(entity.getWorld()));
    }

    public @Nullable Pathfinder.PathResult buildRandomPath(Mob entity, List<Location> points) {
        Pathfinder.PathResult result;
        for (Location location : points) {
            result = entity.getPathfinder().findPath(location);
            if (result != null) return result;
        }

        return null;
    }

    /**
     * Modifies the behavior of the targeted Mob to be more... obedient.
     * @param entity The Mob being targeted
     */
    public void applyGoals(Mob entity) {
        MobGoals goals = Bukkit.getMobGoals();

        goals.removeAllGoals(entity, GoalType.TARGET);
        goals.addGoal(entity, 0, new DocileGoal(this.plugin, entity));

        if (entity instanceof WanderingTrader trader) goals.addGoal(trader, 1, new NoInvisGoal(this.plugin, trader));

    }

    private @NotNull Location nonNullLocation(World world) {
        return new Location(world, 0, 0, 0);
    }

    private void teleport(Mob entity, Location location, long delay) {
        entity.getScheduler().runDelayed(this.plugin, task -> entity.teleport(location), null, delay);
    }
}
