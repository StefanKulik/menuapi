package de.peettea.minecraft.menuApi.core;

import de.peettea.minecraft.menuApi.core.item.BasicItem;
import de.peettea.minecraft.menuApi.core.item.Item;
import de.peettea.minecraft.menuApi.core.item.ItemBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class Menu implements InventoryHolder {
    protected PlayerMenuUtility playerMenuUtility;
    protected Inventory inventory;
    protected ItemStack FILLER_GLASS = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName(ChatColor.DARK_GRAY + "").getItem();

    protected Map<Integer, Item> items = new HashMap<>();

    @Getter
    protected boolean sticky = false;

    private final Plugin plugin;

    public Menu(PlayerMenuUtility playerMenuUtility, Plugin plugin) {
        this.playerMenuUtility = playerMenuUtility;
        this.plugin = plugin;
    }

    protected abstract String getMenuName();

    /**
     * @return Size for custom inventory, must be a multiple of 9 between 9 and 54 slots
     * acceptable values [9, 18, 27, 36, 45, 54]
     */
    protected abstract int getSlots();

    protected abstract InventoryType getInventoryType();

    /**
     * handles the click event in the menu
     *
     * @param e - corresponding event
     */
    public abstract void handleMenu(InventoryClickEvent e);

    /**
     * handles the close event for the menu
     *
     * @param e - corresponding event
     */
    public abstract void exitMenu(InventoryCloseEvent e);

    /**
     * handles the drag event in the menu
     *
     * @param e - corresponding event
     */
    public abstract void dragMenu(InventoryDragEvent e);

    protected abstract void setMenuItems();

    protected abstract Menu getParent();


    /**
     * creates inventory and will open for owner
     */
    public Menu open() {
        if (getInventoryType() != null)
            inventory = Bukkit.createInventory(this, getInventoryType(), getMenuName());
        else
            inventory = Bukkit.createInventory(this, getSlots(), getMenuName());

        this.setMenuItems();
        playerMenuUtility.getOwner().openInventory(inventory);
        return this;
    }

    /**
     * Show the menu to the inputted players
     *
     * @param players The players you wish to show the menu too
     */
    public Menu showTo(Player... players) {
        for (Player p : players) {
            if (p.equals(playerMenuUtility.getOwner())) continue;
            p.openInventory(inventory);
        }
        return this;
    }

    /**
     * makes the menu sticky -> not closeable
     */
    public Menu setSticky() {
        this.sticky = true;
        return this;
    }

    /**
     * Returns the item at specified index
     *
     * @param index - slot index
     * @return Found item
     */
    public Item itemAt(int index) {
        return items.get(index);
    }

    /**
     * Returns the item at specified coordinates, where z is on the horizontal axis and s is on the vertical axis.
     *
     * @param z Zeile
     * @param s Spalte
     * @return Found item
     */
    public Item itemAt(int z, int s) {
        return itemAt((z - 1) * 9 + (s - 1));
    }

    /**
     * @return the plugin instance of this project
     */
    protected Plugin getPlugin() {
        return plugin;
    }

    /**
     * Overridden method from the InventoryHolder interface
     *
     * @return inventory
     */
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Sets the item at the specified index
     *
     * @param index Index of the item you wish to set
     * @param item  The item you wish to set the index as
     */
    public void setItem(int index, Item item) {
        if (item == null) {
            inventory.setItem(index, null);
        } else {
            inventory.setItem(index, item.stack());
        }

        items.put(index, item);
    }

    /**
     * Sets the item at the specified coordinates, where z is on the horizontal axis and s is on the vertical axis.
     *
     * @param z    - gibt die Zeile an (beginnend ab 1, für lesbarkeit)
     * @param s    - gibt die Spalte an (beginnend ab 1, für lesbarkeit)
     * @param item - das Item, welches gesetzt werden soll
     */
    protected void setItem(int z, int s, Item item) {
        setItem((z - 1) * 9 + (s - 1), item);
    }

    /**
     * Helpful utility method to fill all remaining slots with "filler glass"
     */
    protected void setFillerGlass() {
        for (int i = 0; i < getSlots(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, FILLER_GLASS);
            }
        }
    }

    protected void addMenuBorder() {
        int slots = getSlots();
        if (slots < 27) {
            log.warn(String.format("Anzahl Slots zu niedrig um Border zu setzen. Slots: %d. Muss 27, 36, 45 oder 54 betragen.", slots));
            return;
        }

        setInventoryItems(slots);

        if (this instanceof PaginatedMenu) {
            setPaginatedButton(slots - 6, "Left");
            setPaginatedButton(slots - 4, "Right");
        }

        setItem(slots - 5, BasicItem.create(
                new ItemBuilder(Material.BARRIER).displayName(ChatColor.DARK_RED + "Close").getItem(),
                (p, t) -> p.closeInventory()
        ));
    }

    /**
     * placeholder method for items to do nothing when clicked on
     *
     * @param p    - player
     * @param type - type of click
     */
    protected static void doNothing(Player p, ClickType type) {
    }

    protected void basicHandleEvent(InventoryClickEvent e) {
        if (!e.getInventory().equals(inventory))
            return;

        if (e.getRawSlot() >= getSlots() && !e.getClick().isShiftClick())
            return;

        e.setCancelled(true);

        if (!items.containsKey(e.getSlot())) {
            return;
        }

        this.sticky = false;
        items.get(e.getSlot()).act((Player) e.getWhoClicked(), e.getClick());
    }

    protected void basicExitEvent(InventoryCloseEvent e) {
        if (!e.getInventory().equals(inventory)) return;

        if (getParent() != null) {
            openInventoryWithDelay(e, getParent());
        } else if (isSticky()) {
            openInventoryWithDelay(e, this);
        }
    }

    protected void basicDragEvent(InventoryDragEvent e) {
        if (!e.getInventory().equals(inventory))
            return;

        e.setCancelled(true);
    }


    private void openInventoryWithDelay(InventoryCloseEvent e, Menu menu) {
        new BukkitRunnable() {
            @Override
            public void run() {
                menu.open();
            }
        }.runTaskLater(getPlugin(), 2L);
    }

    private void setPaginatedButton(int position, String buttonName) {
        setItem(position, BasicItem.create(
                new ItemBuilder(Material.DARK_OAK_BUTTON).displayName(ChatColor.GREEN + buttonName).getItem(),
                Menu::doNothing
        ));
    }

    private void setInventoryItems(int slots) {
        setInventoryItemsInRange(0, 10);
        setInventoryItemsBySlotsThreshold(slots);
        setInventoryItemsInRange(slots - 10, slots);
    }

    private void setInventoryItemsInRange(int start, int end) {
        for (int i = start; i < end; i++) {
            setFillerItemIfAbsent(i);
        }
    }

    private void setInventoryItemsBySlotsThreshold(int slots) {
        int[][] slotsThresholds = {{36, 17, 18}, {45, 26, 27}, {54, 35, 36}};

        for (int[] slotsThreshold : slotsThresholds) {
            if (slots >= slotsThreshold[0]) {
                setFillerItemIfAbsent(slotsThreshold[1]);
                setFillerItemIfAbsent(slotsThreshold[2]);
            }
        }
    }

    private void setFillerItemIfAbsent(int i) {
        if (inventory.getItem(i) == null) {
            setItem(i, BasicItem.create(FILLER_GLASS, Menu::doNothing));
        }
    }
}
