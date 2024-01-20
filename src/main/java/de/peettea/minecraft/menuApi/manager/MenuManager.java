package de.peettea.minecraft.menuApi.manager;

import de.peettea.minecraft.menuApi.core.Menu;
import de.peettea.minecraft.menuApi.core.PlayerMenuUtility;
import de.peettea.minecraft.menuApi.exceptions.MenuManagerException;
import de.peettea.minecraft.menuApi.exceptions.MenuManagerNotSetupException;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class MenuManager {
    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

    public static PlayerMenuUtility getPlayerMenuUtility(Player player) {
        if (playerMenuUtilityMap.containsKey(player))
            return playerMenuUtilityMap.get(player);

        playerMenuUtilityMap.put(player, new PlayerMenuUtility(player));
        return playerMenuUtilityMap.get(player);
    }

    public static Menu buildMenu(Class<? extends Menu> menuClass, Player player) throws MenuManagerException, MenuManagerNotSetupException {
        try {
            return menuClass.getConstructor(PlayerMenuUtility.class).newInstance(getPlayerMenuUtility(player));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new MenuManagerException();
        }
    }
}
