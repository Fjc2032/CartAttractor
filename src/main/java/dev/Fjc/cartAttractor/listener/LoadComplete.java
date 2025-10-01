package dev.Fjc.cartAttractor.listener;

import dev.Fjc.cartAttractor.CartAttractor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.jetbrains.annotations.NotNull;

public class LoadComplete implements Listener {

    private final CartAttractor plugin;

    public LoadComplete(@NotNull CartAttractor plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        this.plugin.getLogger().info("Server load complete. Initializing tasks...");
        this.plugin.task(0L, 10000L, "Exclusion list flushed.");
    }
}
