package codzombie.codzombie.Store;

import codzombie.codzombie.ArmorSystem.ArmorSystem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public enum StoreType {

    Ammo,
    Armor
    ;

    public static String getShowText(String Type){
        if (Type != null) {
            switch (Type) {
                case "Ammo":
                    return ChatColor.GREEN + ChatColor.BOLD.toString() + "彈藥";
                case "Armor":
                    return ChatColor.GREEN + ChatColor.BOLD.toString() + "護甲";
            }
        }
        return null;
    }

    public static Material getShowItemType(String Type){
        if (Type != null) {
            switch (Type) {
                case "Ammo":
                    return Material.ENDER_CHEST;
                case "Armor":
                    return Material.IRON_CHESTPLATE;
            }
        }
        return null;
    }

    public static boolean BuyItem(String Type, Player p){
        if (Type != null) {

            BuyItemData Data;

            switch (Type) {
                case "Ammo":
                    Data =  codzombie.codzombie.GunSystem.Ammo.SetToMaxAmmo(p.getInventory().getItemInMainHand());
                    p.getInventory().setItem(p.getInventory().getHeldItemSlot(), Data.BuyItem);
                    return Data.isCompleteBuy;
                case "Armor":
                    Data = ArmorSystem.BuyArmorValue(p);
                    return Data.isCompleteBuy;
            }
        }
        return false;
    }
}
