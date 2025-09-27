package dev.Fjc.cartAttractor.builder.goals;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import dev.Fjc.cartAttractor.CartAttractor;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Random;

public class AvoidTrainGoal implements Goal<@NotNull Mob> {

    private final CartAttractor plugin;

    private final NamespacedKey key;

    private static GoalKey<@NotNull Mob> goalKey;

    private final Location location;

    private final Mob mob;

    private ScheduledTask[] task;

    /**
     * The main way mobs get into the train is via collision. This implementation attempts to stop that by turning
     * off the mob collision and setting their path to another location, away from the train.
     * (hint: it didn't fucking work)
     * @param plugin The plugin handling this goal
     * @param entity The Mob being targeted
     * @param otherLocation The secondary location
     */
    public AvoidTrainGoal(@NotNull CartAttractor plugin, Mob entity, Location otherLocation) {
        this.plugin = plugin;
        this.key = new NamespacedKey(this.plugin, "avoidtraingoal");
        this.mob = entity;
        goalKey = GoalKey.of(Mob.class, this.key);
        this.location = otherLocation;
    }

    @Override
    public boolean shouldActivate() {
        return false;
    }

    @Override
    public boolean shouldStayActive() {
        return shouldActivate();
    }

    @Override
    public void start() {
        this.mob.setCollidable(false);
    }

    @Override
    public void stop() {
        this.mob.setCollidable(true);
    }

    @Override
    public void tick() {
        if (this.task[0] == null) assignLocation();
    }

    @Override
    public @NotNull GoalKey<@NotNull Mob> getKey() {
        return goalKey;
    }

    @Override
    public @NotNull EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.MOVE);
    }

    private ScheduledTask assignLocation() {
        this.task[0] = this.mob.getScheduler().runAtFixedRate(this.plugin, task ->
                this.mob.getPathfinder().moveTo(this.location), null, random.nextLong(), 20L);

        return this.task[0];
    }

    private final Random random = new Random();
}
