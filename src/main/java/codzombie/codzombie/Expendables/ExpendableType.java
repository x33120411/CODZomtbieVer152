package codzombie.codzombie.Expendables;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public enum ExpendableType {
    FireTurret,
    GunTurret;

    public static Material getItemMaterial(String Type) {
        switch (Type) {
            case "FireTurret":
                return Material.MAGMA_BLOCK;
            case "GunTurret":
                return Material.DISPENSER;
        }
        return null;
    }

    public static String getItemDisplayName(String Type) {
        switch (Type) {
            case "GunTurret":
                return ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "機關槍哨塔";
            case "FireTurret":
                return ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "火焰哨塔";
        }
        return null;
    }

    public static List<String> getItemLore(String Type) {

        List<String> Lore = new ArrayList<>();

        switch (Type) {
            case "GunTurret":
                Lore.add(ChatColor.GREEN + "看來事實後要有更多個隊友了");
                Lore.add(ChatColor.GREEN + "不過請保護好你這個隊友因為他");
                Lore.add(ChatColor.GREEN + "只能發揮你手上武器百分之20的性能");
                Lore.add(ChatColor.GREEN + "這個隊友是會死掉的優請好好保護他");
                return Lore;
            case "FireTurret":
                Lore.add(ChatColor.GREEN + "看來事實後要有更多個隊友了");
                Lore.add(ChatColor.GREEN + "這個隊友火氣非常大請不要跟她開玩笑");
                return Lore;
        }
        return null;
    }

    public static int getItemAmount(String Type){
        switch (Type){
            case "GunTurret":
                return 3;
            case "FireTurret":
                return 2;
        }
        return 0;
    }
}
