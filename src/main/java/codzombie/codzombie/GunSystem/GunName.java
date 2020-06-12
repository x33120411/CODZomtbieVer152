package codzombie.codzombie.GunSystem;

import org.bukkit.ChatColor;

public enum GunName {
    RainBowGun,
    ShotGun
    ;

    public static String getName(GunName gunName){
        switch (gunName){
            case RainBowGun:
                return ChatColor.YELLOW + "彩" + ChatColor.GREEN + "紅" + ChatColor.BLUE + "步" + ChatColor.LIGHT_PURPLE + "槍";
            case ShotGun:
                return ChatColor.BLUE + "散彈槍";
        }
        return null;
    }
}
