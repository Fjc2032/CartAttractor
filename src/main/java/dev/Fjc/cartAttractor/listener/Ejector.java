package dev.Fjc.cartAttractor.listener;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import dev.Fjc.cartAttractor.CartAttractor;
import dev.Fjc.cartAttractor.builder.FileBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class Ejector implements Listener {

    private final CartAttractor plugin;

    private final FileBuilder fileBuilder;

    private final boolean isEnabled;

    private final Random random = new Random();

    private Location offset;

    public Ejector(@NotNull CartAttractor plugin) {
        this.plugin = plugin;
        this.fileBuilder = this.plugin.getFileBuilder();
        this.isEnabled = this.fileBuilder.isEnabled();
    }

    @EventHandler
    public void onArrival(SignActionEvent event) {
        if (!isEnabled) return;
        MinecartGroup group = event.getGroup();
        if (group == null) return;
        if (!event.isType("station")) return;
        if (!group.hasPassenger()) return;
        if (group.isMoving()) return;

        for (MinecartMember<?> member : group) {
            Random random = new Random();
            double chance = random.nextDouble();

            if (member.getEntity().hasPlayerPassenger()) continue;
            if (!member.getEntity().hasPassenger()) continue;

            if ((chance / 4) <= this.fileBuilder.getEjectChance()) {
                for (Entity entity : member.getEntity().getPassengers()) {
                    if (!(entity instanceof Mob livingEntity)) continue;
                    setLocation(livingEntity);
                }
            }
            if (chance <= this.fileBuilder.getEjectChance()) member.eject();

        }
    }

    private void setLocation(Mob entity) {
        this.offset = new Location(entity.getWorld(), random.nextDouble(25000), random.nextDouble(100) + 10, random.nextDouble(25000), random.nextFloat(), random.nextFloat());
        entity.getPathfinder().moveTo(this.offset);
    }
}
