package de.peettea.minecraft.menuApi.core.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemBuilder {

    private HashMap<ItemData, Object> metadata = new HashMap<>();

    public ItemBuilder(Material material) {
        metadata.put(ItemData.MATERIAL, material);
    }

    public ItemBuilder amount(int amount) {
        metadata.put(ItemData.AMOUNT, amount);
        return this;
    }

    public ItemBuilder data(short data) {
        metadata.put(ItemData.DATA, data);
        return this;
    }

    public ItemBuilder displayName(String displayName) {
        metadata.put(ItemData.DISPLAY_NAME, displayName);
        return this;
    }

    public ItemBuilder localizedName(String localizedName) {
        metadata.put(ItemData.LOCALIZED_NAME, localizedName);
        return this;
    }

    public ItemBuilder displayLore(List<String> displayLore) {
        metadata.put(ItemData.DISPLAY_LORE, displayLore);
        return this;
    }

    public ItemBuilder skullOwner(Player skullOwner) {
        metadata.put(ItemData.SKULL_OWNER, skullOwner);
        return this;
    }

    public ItemBuilder glowing() {
        metadata.put(ItemData.GLOWING, true);
        return this;
    }

    public ItemBuilder armorColour(Color color) {
        metadata.put(ItemData.ARMOR_COLOR, color);
        return this;
    }

    public Material getMaterial() {
        if (!metadata.containsKey(ItemData.MATERIAL)) {
            return null;
        }

        return (Material) metadata.get(ItemData.MATERIAL);
    }

    public Integer getAmount() {
        if (!metadata.containsKey(ItemData.AMOUNT)) {
            return null;
        }

        return (int) metadata.get(ItemData.AMOUNT);
    }

    public Short getData() {
        if (!metadata.containsKey(ItemData.DATA)) {
            return null;
        }

        return (short) metadata.get(ItemData.DATA);
    }

    public String getDisplayName() {
        if (!metadata.containsKey(ItemData.DISPLAY_NAME)) {
            return null;
        }

        return (String) metadata.get(ItemData.DISPLAY_NAME);
    }

    public String getLocalizedName() {
        if (!metadata.containsKey(ItemData.LOCALIZED_NAME)) {
            return null;
        }

        return (String) metadata.get(ItemData.LOCALIZED_NAME);
    }

    public List<String> getDisplayLore() {
        if (!metadata.containsKey(ItemData.DISPLAY_LORE)) {
            return null;
        }

        return (List<String>) metadata.get(ItemData.DISPLAY_LORE);
    }

    public Player getSkullOwner() {
        if (!metadata.containsKey(ItemData.SKULL_OWNER)) {
            return null;
        }

        return (Player) metadata.get(ItemData.SKULL_OWNER);
    }

    public boolean isGlowing() {
        return metadata.containsKey(ItemData.GLOWING) && (boolean) metadata.get(ItemData.GLOWING);
    }

    public ItemStack getItem() {
        ItemStack itemStack = new ItemStack(getMaterial(), getAmount() != null ? getAmount() : 1, getData() != null ? getData() : 0);
        ItemMeta itemMeta = itemStack.getItemMeta();

        String displayName = getDisplayName();
        String localizedName = getLocalizedName();
        List<String> displayLore = getDisplayLore();
        Player skullOwner = getSkullOwner();

        if (displayName != null) {
            assert itemMeta != null;
            itemMeta.setDisplayName(displayName);
        }

        if (localizedName != null) {
            assert itemMeta != null;
            itemMeta.setLocalizedName(localizedName);
        }

        if (displayLore != null) {
            assert itemMeta != null;
            itemMeta.setLore(displayLore);
        }

        if (isGlowing()) {
            assert itemMeta != null;
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        }

        itemStack.setItemMeta(itemMeta);

//        if (isGlowing()) {
//            itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
//        }

        if (skullOwner != null && itemMeta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            skullMeta.setOwningPlayer(skullOwner);
            itemStack.setItemMeta(skullMeta);
        }

        if (metadata.containsKey(ItemData.ARMOR_COLOR) && itemMeta instanceof LeatherArmorMeta) {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
            assert leatherArmorMeta != null;
            leatherArmorMeta.setColor((Color) metadata.get(ItemData.ARMOR_COLOR));
            itemStack.setItemMeta(leatherArmorMeta);
        }

        return itemStack;
    }

    private enum ItemData {
        MATERIAL, AMOUNT, DATA, DISPLAY_NAME, LOCALIZED_NAME, DISPLAY_LORE, SKULL_OWNER, GLOWING, ARMOR_COLOR
    }
}
