package dev.Fjc.cartAttractor.builder.goals;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import dev.Fjc.cartAttractor.CartAttractor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class DocileGoal implements Goal<@NotNull Mob> {

    private final CartAttractor plugin;
    private final NamespacedKey key;

    private static GoalKey<@NotNull Mob> goalKey;

    private final Mob mob;

    /**
     * This Goal will set the targeted Mob into a docile state.
     * @param plugin The plugin handling the Mob
     * @param entity The Mob being targeted
     */
    public DocileGoal(@NotNull CartAttractor plugin, @NotNull Mob entity) {
        this.plugin = plugin;
        this.mob = entity;

        this.key = new NamespacedKey(this.plugin, "docilegoal");
        goalKey = GoalKey.of(Mob.class, key);
    }

    @Override
    public boolean shouldActivate() {
        return true;
    }

    @Override
    public void start() {
        this.mob.setAggressive(false);
    }

    @Override
    public void tick() {
        this.mob.setTarget(null);
    }

    @Override
    public void stop() {
        Bukkit.getMobGoals().removeGoal(this.mob, this);
    }

    @Override
    public @NotNull GoalKey<@NotNull Mob> getKey() {
        return goalKey;
    }

    @Override
    public @NotNull EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.TARGET);
    }

    public NamespacedKey getNamespacedKey() {
        return this.key;
    }
}
