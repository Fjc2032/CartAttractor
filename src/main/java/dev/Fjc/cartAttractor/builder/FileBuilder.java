package dev.Fjc.cartAttractor.builder;

import dev.Fjc.cartAttractor.CartAttractor;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class FileBuilder {

    private final CartAttractor plugin;

    public FileBuilder(@NotNull CartAttractor plugin) {
        this.plugin = plugin;
    }

    public void build() {
        this.plugin.saveDefaultConfig();
    }

    public void loadDefaults() {
        FileConfiguration configuration = this.plugin.getConfig();

        configuration.addDefault("isEnabled", true);
        configuration.addDefault("radius", 24);
        configuration.addDefault("ejectChange", 0.07);

        configuration.options().copyDefaults(true);
        this.plugin.saveConfig();
    }

    public void reload() {
        this.plugin.reloadConfig();
    }

    public int getRadius() {
        return this.plugin.getConfig().getInt("radius", 24);
    }
    public boolean isEnabled() {
        return this.plugin.getConfig().getBoolean("isEnabled", true);
    }
    public double getEjectChance() {
        return this.plugin.getConfig().getDouble("ejectChance", 0.07);
    }

    public FileConfiguration getConfiguration() {
        return this.plugin.getConfig();
    }
}
