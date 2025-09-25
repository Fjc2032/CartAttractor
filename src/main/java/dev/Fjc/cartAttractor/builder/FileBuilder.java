package dev.Fjc.cartAttractor.builder;

import dev.Fjc.cartAttractor.CartAttractor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class FileBuilder {

    private final CartAttractor plugin;

    private final FileConfiguration configuration;

    private final File file;

    public FileBuilder(@NotNull CartAttractor plugin) {
        this.plugin = plugin;
        this.configuration = this.plugin.getConfig();
        this.file = new File(this.plugin.getDataFolder(), "config.yml");
    }

    public File build() {
        if (this.file.exists()) return this.file;
        boolean success = this.file.mkdirs() || this.file.mkdir();

        if (success) return this.file; else return null;
    }

    public void loadDefaults() {
        configuration.addDefault("isEnabled", true);
        configuration.addDefault("radius", 24);
        configuration.addDefault("ejectChange", 0.14);
    }

    public boolean reload() {
        YamlConfiguration.loadConfiguration(this.file);
        return (this.file.canRead());
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
}
