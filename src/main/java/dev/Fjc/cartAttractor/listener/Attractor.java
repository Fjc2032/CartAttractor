package dev.Fjc.cartAttractor.listener;

import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import dev.Fjc.cartAttractor.CartAttractor;
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

    public Attractor(@NotNull CartAttractor plugin) {
        this.plugin = plugin;
        this.pathManager = new PathManager(this.plugin);
    }

    @EventHandler
    public void onCartNearbyNoPassenger(SignActionEvent event) {
        MinecartGroup group = event.getGroup();
        if (!event.isType("station")) return;

        try {

            for (Mob entity : getNearbyEntities(event.getLocation())) {

                if (getLastAvailableCar(group, entity) == null) continue;

                if (entity.getLocation().distanceSquared(getLastAvailableCar(group, entity)) <= 0.15) continue;

                if (pathManager.buildPath(getLastAvailableCar(group), entity, true) != null)
                    pathManager.buildPath(getLastAvailableCar(group), entity, true);
            }
        } catch (NullPointerException ignored) {}
    }

    @EventHandler
    public void onCartNearby(SignActionEvent event) {
        MinecartGroup group = event.getGroup();
        if (!event.isType("station")) return;
        if (getMembersInTrain(group).isEmpty()) return;

        try {

            for (Mob entity : getNearbyEntities(event.getLocation(), 24)) {

                if (getLastAvailableCar(group, entity) == null) continue;

                if (entity.getLocation().distanceSquared(getLastAvailableCar(group, entity)) <= 0.15) continue;
                entity.getPathfinder().moveTo(getLastAvailableCar(group, entity));
                if (pathManager.buildPath(getLastAvailableCar(group), entity, true) != null)
                    pathManager.buildPath(getLastAvailableCar(group), entity, true);
            }
        } catch (NullPointerException ignored) {}
    }

    @EventHandler
    public void onTrainEnter(VehicleEnterEvent event) {
        MinecartMember<?> vehicle = MinecartMemberStore.getFromEntity(event.getVehicle());
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
    private static List<? extends Mob> getNearbyEntities(Location location) {
        return getNearbyEntities(location, 16);
    }

    public static List<? extends Mob> getNearbyEntities(Location location, double radius) {
        return location.getWorld().getNearbyEntities(location, radius, radius, radius).stream()
                .filter(Mob.class::isInstance)
                .map(obj -> (Mob) obj)
                .toList();
    }

    public @Nullable Location getLastAvailableCar(MinecartGroup group) {
        for (MinecartMember<?> member : group.reversed()) {
            if (member.getEntity().getPassengers().isEmpty()) return member.getEntity().getLocation();
        }

        return null;
    }

    public Location getLastAvailableCar(MinecartGroup group, Mob target) {
        for (MinecartMember<?> member : group.reversed()) {
            if (member.getEntity().getPassengers().isEmpty()) return member.getEntity().getLocation();
        }

        return target.getLocation();
    }
}
