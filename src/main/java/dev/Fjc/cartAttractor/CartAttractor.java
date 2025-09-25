package dev.Fjc.cartAttractor;

import dev.Fjc.cartAttractor.builder.FileBuilder;
import dev.Fjc.cartAttractor.cmd.CallCommand;
import dev.Fjc.cartAttractor.cmd.Reload;
import dev.Fjc.cartAttractor.listener.Attractor;
import dev.Fjc.cartAttractor.listener.Ejector;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Mob;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class CartAttractor extends JavaPlugin {

    Logger logger = this.getLogger();

    FileBuilder fileBuilder = new FileBuilder(this);

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

        logger.info("Attempting to build configuration...");
        try {
            getFileBuilder().build();
        } catch (Exception exception) {
            logger.info("Something has gone wrong while attempting this action.");
            logger.info(exception.toString());
        } finally {
            logger.info("Configuration is done.");
        }

        logger.info("Attempting to register all events...");
        setListener(new Attractor(this));
        setListener(new Ejector(this));
        setExecutor(new CallCommand(this));
        setExecutor(new Reload(this));

        logger.info("Everything is OK.");
    }
    private void shutdown() {
        logger.info("Stopping all tasks...");
    }

    public FileBuilder getFileBuilder() {
        return this.fileBuilder;
    }
}
