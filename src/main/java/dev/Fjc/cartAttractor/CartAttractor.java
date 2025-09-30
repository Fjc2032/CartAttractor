package dev.Fjc.cartAttractor;

import dev.Fjc.cartAttractor.builder.FileBuilder;
import dev.Fjc.cartAttractor.builder.goals.DocileGoal;
import dev.Fjc.cartAttractor.cmd.*;
import dev.Fjc.cartAttractor.listener.Attractor;
import dev.Fjc.cartAttractor.listener.Ejector;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.logging.Logger;

public final class CartAttractor extends JavaPlugin {

    Logger logger = this.getLogger();

    private final FileBuilder fileBuilder = new FileBuilder(this);

    private static Ejector ejector;

    @Override
    public void onEnable() {
        startup();
    }

    @Override
    public void onDisable() {
        shutdown();
    }

    private void setListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }
    private void setExecutor(String name, CommandExecutor executor) {
        this.getServer().getPluginCommand(name).setExecutor(executor);
    }
    private void startup() {
        ejector = new Ejector(this);
        logger.info("Plugin is starting!");

        logger.info("Attempting to build configuration...");
        try {
            getFileBuilder().build();
        } catch (Exception exception) {
            logger.info("Something has gone wrong while attempting this action.");
            logger.info(exception.toString());
        } finally {
            getFileBuilder().loadDefaults();
            logger.info("Configuration is done.");
        }

        logger.info("Attempting to register all events...");
        setListener(ejector);
        setListener(new Attractor(this));
        setListener(new DocileGoal(this));
        setExecutor("call-passengers", new CallCommand(this));
        setExecutor("cartattractor-reload", new Reload(this));
        setExecutor("getnearbymobs", new NearbyMobsCommand(this));
        setExecutor("getejectchance", new GetEjectCommand(this));
        setExecutor("flushexclusionlist", new FlushExclusionListCommand(this));

        logger.info("Everything is OK.");
    }

    /**
     * Clears the exclusion list every 10 minutes to prevent a memory leak
     * @return The task clearing the exclusion
     */
    public BukkitTask task() {
        return getServer().getScheduler().runTaskTimer(this, () -> ejector.clear(), 5000L, 12000L);
    }

    /**
     * Clears the exclusion list periodically, at the specified time.
     * @param initial Delay before the task should start, in ticks
     * @param period Period to wait until the task starts again, in ticks
     * @return The task clearing the exclusion
     */
    public BukkitTask task(long initial, long period) {
        return getServer().getScheduler().runTaskTimer(this, () -> ejector.clear(), initial, period);
    }

    private void shutdown() {
        logger.info("Stopping all tasks...");

        this.saveConfig();
        cancel();
    }

    public FileBuilder getFileBuilder() {
        return this.fileBuilder;
    }

    public static Ejector getEjector() {
        return ejector;
    }

    private void cancel() {
        ejector.clear();
        task().cancel();
    }
}
