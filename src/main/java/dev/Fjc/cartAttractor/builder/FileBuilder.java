package dev.Fjc.cartAttractor.builder;

import dev.Fjc.cartAttractor.CartAttractor;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class FileBuilder {

    private final CartAttractor plugin;

    private final FileConfiguration configuration;

    public FileBuilder(@NotNull CartAttractor plugin) {
        this.plugin = plugin;
        this.configuration = this.plugin.getConfig();
    }

    public void build() {
        this.plugin.saveDefaultConfig();
    }

    public void loadDefaults() {
        configuration.addDefault("isEnabled", true);
        configuration.addDefault("radius", 24);
        configuration.addDefault("ejectChange", 0.14);
    }

    public void reload() {
        this.plugin.reloadConfig();
    }

    public int getRadius() {
        return this.configuration.getInt("radius", 24);
    }
    public boolean isEnabled() {
        return this.configuration.getBoolean("isEnabled", true);
    }
    public double getEjectChance() {
        return this.configuration.getDouble("ejectChance", 0.14);
    }

    public FileConfiguration getConfiguration() {
        return this.configuration;
    }
}
