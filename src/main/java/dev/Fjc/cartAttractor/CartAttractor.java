package dev.Fjc.cartAttractor;

import dev.Fjc.cartAttractor.builder.FileBuilder;
import dev.Fjc.cartAttractor.cmd.CallCommand;
import dev.Fjc.cartAttractor.cmd.Reload;
import dev.Fjc.cartAttractor.listener.Attractor;
import dev.Fjc.cartAttractor.listener.Ejector;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class CartAttractor extends JavaPlugin {

    Logger logger = this.getLogger();

    private final FileBuilder fileBuilder = new FileBuilder(this);

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
        setListener(new Attractor(this));
        setListener(new Ejector(this));
        setExecutor("call-passengers", new CallCommand(this));
        setExecutor("cartattractor-reload", new Reload(this));

        logger.info("Everything is OK.");
    }
    private void shutdown() {
        logger.info("Stopping all tasks...");
    }

    public FileBuilder getFileBuilder() {
        return this.fileBuilder;
    }
}
