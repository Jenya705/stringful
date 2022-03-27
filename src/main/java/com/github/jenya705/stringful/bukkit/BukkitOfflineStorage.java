package com.github.jenya705.stringful.bukkit;

import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jenya705
 */
public class BukkitOfflineStorage implements Listener {

    @Getter
    private static BukkitOfflineStorage instance;

    private final JavaPlugin plugin;
    private final File file;
    private final Map<String, UUID> nameToUUID;

    public BukkitOfflineStorage(JavaPlugin plugin) throws IOException {
        if (instance != null) {
            throw new IllegalStateException("Can not create this object twice");
        }
        this.plugin = plugin;
        instance = this;
        file = new File(plugin.getDataFolder(), "stringful-otuuid.bin");
        file.createNewFile();
        nameToUUID = loadStringToUUID(file);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void save() throws IOException {
        try (OutputStream os = new FileOutputStream(file)) {
            writeStringToUUIDMap(nameToUUID, os);
        }
    }

    public UUID getUUID(String name) {
        return nameToUUID.get(name.toLowerCase(Locale.ROOT));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerJoin(PlayerJoinEvent event) {
        nameToUUID.put(
                event.getPlayer().getName().toLowerCase(Locale.ROOT),
                event.getPlayer().getUniqueId()
        );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void pluginDisable(PluginDisableEvent event) throws IOException {
        if (event.getPlugin().equals(plugin)) {
            save();
        }
    }

    /**
     *
     * Validating that offline storage is initialized.
     *
     * Better to use because the {@link BukkitOfflineStorage(JavaPlugin)} will cause IllegalStateException if it was initialized by another plugin.
     * This method won't cause this exception.
     *
     * @param plugin Plugin which will be used to initialize storage
     * @throws IOException If failed to load saved values
     */
    public static void validateInitialized(JavaPlugin plugin) throws IOException {
        if (instance == null) {
            new BukkitOfflineStorage(plugin);
        }
    }

    private static Map<String, UUID> loadStringToUUID(File file) throws IOException {
        Map<String, UUID> result = new ConcurrentHashMap<>();
        try (DataInputStream is = new DataInputStream(new FileInputStream(file))) {
            UUID uuid = new UUID(is.readLong(), is.readLong());
            StringBuilder nicknameBuilder = new StringBuilder();
            while (true) {
                char ch = is.readChar();
                if (ch == '\0') break;
                nicknameBuilder.append(ch);
            }
            result.put(nicknameBuilder.toString(), uuid);
        }
        return result;
    }

    private static void writeStringToUUIDMap(Map<String, UUID> result, OutputStream to) throws IOException {
        DataOutputStream os = new DataOutputStream(to);
        for (Map.Entry<String, UUID> stringUUIDEntry : result.entrySet()) {
            os.writeLong(stringUUIDEntry.getValue().getMostSignificantBits());
            os.writeLong(stringUUIDEntry.getValue().getLeastSignificantBits());
            os.writeChars(stringUUIDEntry.getKey());
            os.writeChar(0);
        }
    }

}
