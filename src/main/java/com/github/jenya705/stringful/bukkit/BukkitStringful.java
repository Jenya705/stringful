package com.github.jenya705.stringful.bukkit;

import com.github.jenya705.stringful.Stringful;
import com.github.jenya705.stringful.StringfulArgument;
import com.github.jenya705.stringful.StringfulArgumentParser;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 
 * Some bukkit things for Stringful.
 *
 * features:
 * - tabs for bukkit interfaces
 * - parsers for bukkit interfaces
 *
 * better to do:
 * - initialize {@link BukkitOfflineStorage} using method {@link BukkitOfflineStorage#validateInitialized(JavaPlugin)}
 * 
 * @author Jenya705
 */
@UtilityClass
public class BukkitStringful {

    @Getter
    private final boolean added = isBukkitAPI();

    /**
     *
     * Checks if the bukkit api is in runtime
     *
     * @return true if bukkit api in runtime otherwise false
     */
    private boolean isBukkitAPI() {
        try {
            Class.forName("org.bukkit.entity.Player");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public void addParsersIfNeed(StringfulArgumentParser parser) {
        if (!added) return;
        parser.newParser(Player.class, iterator -> Bukkit.getPlayer(iterator.next()));
        parser.newParser(OfflinePlayer.class, iterator -> {
            if (BukkitOfflineStorage.getInstance() != null) {
                UUID uuid = BukkitOfflineStorage.getInstance().getUUID(iterator.next());
                return uuid == null ? null : Bukkit.getOfflinePlayer(uuid);
            }
            return Bukkit.getOfflinePlayer(iterator.next());
        });
        parser.newParser(World.class, iterator -> Bukkit.getWorld(NamespacedKey.minecraft(iterator.next())));
    }

    public void addDefaultArgumentIfNeed(Stringful<?> stringful) {
        if (!added) return;
        stringful.argumentCreator(Player.class, arg -> arg
                .tab(data -> Bukkit
                        .getOnlinePlayers()
                        .stream()
                        .map(Player::getName)
                        .collect(Collectors.toList())
                )
        );
        stringful.argumentCreator(World.class, arg -> arg
                .tab(data -> Bukkit
                        .getWorlds()
                        .stream()
                        .map(world -> world.getKey().getKey())
                        .collect(Collectors.toList())
                )
        );
    }

}
