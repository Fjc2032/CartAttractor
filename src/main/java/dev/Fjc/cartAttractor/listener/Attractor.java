package dev.Fjc.cartAttractor.listener;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import dev.Fjc.cartAttractor.CartAttractor;
import dev.Fjc.cartAttractor.builder.FileBuilder;
import dev.Fjc.cartAttractor.builder.PathManager;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Attractor implements Listener {

    private final CartAttractor plugin;

    private final PathManager pathManager;

    private final FileBuilder fileBuilder;

    private static Ejector ejector;

    private final boolean isEnabled;

    public Attractor(@NotNull CartAttractor plugin) {
        this.plugin = plugin;
        this.pathManager = new PathManager(this.plugin);
        this.fileBuilder = this.plugin.getFileBuilder();
        this.isEnabled = this.fileBuilder.isEnabled();

        ejector = CartAttractor.getEjector();
    }

    @EventHandler
    public void onCartNearbyNoPassenger(SignActionEvent event) {
        MinecartGroup group = event.getGroup();
        if (!isEnabled) return;
        if (!event.isType("station")) return;
        if (group.isMoving()) return;

        try {

            for (Mob entity : getNearbyEntities(event.getLocation(), 30)) {
                if (getLastAvailableCarLocation(group, entity) == null) continue;

                if (entity.getLocation().distanceSquared(getLastAvailableCarLocation(group, entity)) <= 0.15) continue;

                pathManager.buildPath(getLastAvailableCarLocation(group), entity, true);
            }
        } catch (NullPointerException ignored) {}
    }

    @EventHandler
    public void onCartNearby(SignActionEvent event) {
        MinecartGroup group = event.getGroup();
        if (!isEnabled) return;
        if (!event.isType("station")) return;
        if (getMembersInTrain(group).isEmpty()) return;
        if (group.isMoving()) return;

        try {

            for (Mob entity : getNearbyEntities(event.getLocation())) {
                if (entity.isInWater() || entity.isInsideVehicle()) continue;

                if (getLastAvailableCarLocation(group, entity) == null) continue;

                if (entity.getLocation().distanceSquared(getLastAvailableCarLocation(group, entity)) <= 0.15) continue;

                pathManager.buildPath(getLastAvailableCarLocation(group), entity, true);
                enterVehicle(entity, getLastAvailableCar(group));
            }
        } catch (NullPointerException ignored) {}
    }

    @EventHandler
    public void onTrainEnter(VehicleEnterEvent event) {
        MinecartMember<?> vehicle = MinecartMemberStore.getFromEntity(event.getVehicle());
        if (!isEnabled) return;
        if (vehicle == null) return;
        if (!(event.getEntered() instanceof LivingEntity entity)) return;

        if (entity.isInvisible()) entity.setInvisible(false);
    }

    public static List<? extends Mob> getMembersInTrain(MinecartGroup group) {
        List<Mob> targets = new ArrayList<>();
        if (group == null) return List.of();
        for (MinecartMember<?> member : group) if (member.getEntity().hasPassenger()) {
            targets.addAll(member.getEntity().getPassengers().stream()
                    .filter(Mob.class::isInstance)
                    .map(Mob.class::cast)
                    .toList());
        }
        return targets;
    }
    private List<? extends Mob> getNearbyEntities(Location location) {
        return getNearbyEntities(location, this.fileBuilder.getRadius());
    }

    /**
     * Gets a list of all nearby entities of the specified radius.
     * @param location The starting Location
     * @param radius the radius
     * @return A list of all nearby entities, which are all wildcards of Mob.
     */
    public static List<? extends Mob> getNearbyEntities(Location location, int radius) {
        return location.getWorld().getNearbyEntities(location, radius, radius, radius).stream()
                .filter(Mob.class::isInstance)
                .filter(obj -> !ejector.exclusions().contains(obj.getUniqueId()))
                .map(obj -> (Mob) obj)
                .toList();
    }

    /**
     * Gets the location of the furthest back MinecartMember in a MinecartGroup, or
     * null if there are no members available.
     * @param group The MinecartGroup
     * @return The Location, or null if there are no valid MinecartMembers.
     */
    public @Nullable Location getLastAvailableCarLocation(MinecartGroup group) {
        for (MinecartMember<?> member : group.reversed()) {
            if (member.getEntity().getPassengers().isEmpty()) return member.getEntity().getLocation();
        }

        return null;
    }

    /**
     * Gets the location of the furthest back MinecartMember in a MinecartGroup.
     * If there are no MinecartMember instances available, this will return the
     * location of the Mob.
     * @param group The MinecartGroup
     * @param target The Mob
     * @return The Location of the MinecartMember, or the location of the Mob specified, or null if neither exist.
     */
    public Location getLastAvailableCarLocation(MinecartGroup group, Mob target) {
        for (MinecartMember<?> member : group.reversed()) {
            if (member.getEntity().getPassengers().isEmpty()) return member.getEntity().getLocation();
        }

        return target.getLocation();
    }

    /**
     * Gets the furthest back MinecartMember in a MinecartGroup, or null if there are no members available.
     * @param group The MinecartGroup
     * @return The MinecartMember, or null if none is available.
     */
    public @Nullable MinecartMember<?> getLastAvailableCar(MinecartGroup group) {
        for (MinecartMember<?> member : group.reversed()) {
            if (member.getEntity().getPassengers().isEmpty()) return member;
        }

        return null;
    }

    /**
     * Forces a Mob into a MinecartMember, if there is one available. More reliable than collisions.
     * @param entity The Mob being put into the MinecartMember
     * @param vehicle The MinecartMember the Mob will enter
     * @apiNote Be careful when using this. Without additional checks, it will pull ANY mob in radius.
     */
    public void enterVehicle(Mob entity, MinecartMember<?> vehicle) {
       if (!vehicle.getEntity().hasPassenger()) vehicle.addPassengerForced(entity);
    }
}
