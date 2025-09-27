package dev.Fjc.cartAttractor.builder.goals;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import dev.Fjc.cartAttractor.CartAttractor;
import dev.Fjc.cartAttractor.builder.FileBuilder;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.WanderingTrader;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class NoInvisGoal implements Goal<@NotNull WanderingTrader> {

    private final CartAttractor plugin;

    private final FileBuilder fileBuilder;

    private final NamespacedKey key;

    private static GoalKey<@NotNull WanderingTrader> goalkey;

    private final WanderingTrader mob;

    /**
     * Specifically targets Wandering Traders, preventing them from becoming invisible while this goal is active.
     * @param plugin The plugin handling this WanderingTrader
     * @param entity The Mob being targeted. Must be an instance of WanderingTrader
     */
    public NoInvisGoal(@NotNull CartAttractor plugin, @NotNull WanderingTrader entity) {
        this.plugin = plugin;
        this.fileBuilder = this.plugin.getFileBuilder();
        this.key = new NamespacedKey(this.plugin, "noinvisgoal");
        this.mob = entity;
        goalkey = GoalKey.of(WanderingTrader.class, key);
    }

    @Override
    public boolean shouldActivate() {
        return this.fileBuilder.isEnabled();
    }

    @Override
    public boolean shouldStayActive() {
        return shouldActivate();
    }

    @Override
    public void start() {
        this.mob.setCanDrinkMilk(false);
        this.mob.setCanDrinkPotion(false);
    }

    @Override
    public void stop() {
        this.mob.setCanDrinkPotion(true);
        this.mob.setCanDrinkMilk(true);
        Bukkit.getMobGoals().removeGoal(this.mob, this);
    }

    @Override
    public void tick() {
        if (this.mob.isInvisible()) this.mob.setInvisible(false);
    }

    @Override
    public @NotNull GoalKey<@NotNull WanderingTrader> getKey() {
        return goalkey;
    }

    @Override
    public @NotNull EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.UNKNOWN_BEHAVIOR);
    }

    public NamespacedKey getNamespacedKey() {
        return this.key;
    }
    public @NotNull WanderingTrader getMob() {
        return this.mob;
    }
}
