package dev.Fjc.cartAttractor.listener;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Random;

public class Ejector implements Listener {

    @EventHandler
    public void onArrival(SignActionEvent event) {
        MinecartGroup group = event.getGroup();
        if (!event.isType("station")) return;
        if (!group.hasPassenger()) return;
        if (group.isMoving()) return;

        for (MinecartMember<?> member : group) {
            Random random = new Random();
            double chance = random.nextDouble();

            if (member.getEntity().hasPlayerPassenger()) continue;
            if (!member.getEntity().hasPassenger()) continue;
            if (chance <= 0.07) member.eject();
        }
    }
}
