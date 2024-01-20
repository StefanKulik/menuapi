package de.peettea.minecraft.menuApi.core;

import lombok.Getter;
import org.bukkit.plugin.Plugin;

public abstract class PaginatedMenu extends Menu {


    /**
     * keep track of the page the menu is on
     */
    protected int page = 0;

    /**
     * 28 max items because with the border set below
     */
    @Getter
    protected int maxItemsPerPage = 28;

    /**
     * the index represents the index of the slot
     * that the loop is on
     */
    protected int index = 0;

    public PaginatedMenu(PlayerMenuUtility playerMenuUtility, Plugin plugin) {
        super(playerMenuUtility, plugin);
    }
}
