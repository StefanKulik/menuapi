package de.peettea.minecraft.menuApi.core;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;

@Getter
public class PlayerMenuUtility {

    private final Player owner;
    private final HashMap<String, Object> data = new HashMap<>();

    public PlayerMenuUtility(Player owner) {
        this.owner = owner;
    }
}