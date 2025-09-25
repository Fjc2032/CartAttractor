package dev.Fjc.cartAttractor;

import dev.Fjc.cartAttractor.cmd.CallCommand;
import dev.Fjc.cartAttractor.listener.Attractor;
import dev.Fjc.cartAttractor.listener.Ejector;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class CartAttractor extends JavaPlugin {

    Logger logger = this.getLogger();

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
    private void setExecutor(CommandExecutor executor) {
        this.getServer().getPluginCommand("call-passengers").setExecutor(executor);
    }
    private void startup() {
        logger.info("Plugin is starting!");

        logger.info("Attempting to register all events...");
        setListener(new Attractor(this));
        setListener(new Ejector());
        setExecutor(new CallCommand(this));

        logger.info("Everything is OK.");
    }
    private void shutdown() {
        logger.info("Stopping all tasks...");
    }
}
