package com.github.jenya705.stringful.bukkit;

import com.github.jenya705.stringful.StringfulArgumentParser;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
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
        parser.newParser(OfflinePlayer.class, iterator -> Bukkit.getOfflinePlayer(iterator.next()));
        parser.newParser(World.class, iterator -> Bukkit.getWorld(NamespacedKey.minecraft(iterator.next())));
    }

}
