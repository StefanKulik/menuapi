package de.peettea.minecraft.menuApi.manager;

import de.peettea.minecraft.menuApi.core.Menu;
import de.peettea.minecraft.menuApi.core.PlayerMenuUtility;
import de.peettea.minecraft.menuApi.exceptions.MenuManagerException;
import de.peettea.minecraft.menuApi.exceptions.MenuManagerNotSetupException;
import de.peettea.minecraft.minigames.menu.ColorSelectionMenu;
import de.peettea.minecraft.minigames.menu.PaginatedTest;
import de.peettea.minecraft.minigames.menu.RockPaperScissorsMenu;
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

    public static void openColorSelection(Player player) {
        new ColorSelectionMenu(getPlayerMenuUtility(player)).setSticky().open();
    }

    public static void openPaginatedTest(Player player) {
        new PaginatedTest(getPlayerMenuUtility(player)).open();
    }

    public static void openRockPaperScissor(Player player) {
        new RockPaperScissorsMenu(getPlayerMenuUtility(player)).open();
    }
}
