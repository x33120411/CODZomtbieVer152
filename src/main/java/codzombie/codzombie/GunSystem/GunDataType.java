package codzombie.codzombie.GunSystem;

import org.bukkit.ChatColor;

public enum GunDataType {
    DamageAmount,
    ReloadTime,
    MagAmmo,
    TotalAmmo,
    Speed,
    ActiveRange,
    Offset,
    ShotTimes,
    MaterialType,
    DisplayName,
    FireMode,
    MakeMoney,
    DamageUpgrade,
    DamageUpgradeMines,
    ReloadTimeUpgrade,
    ReloadTimeUpgradeMines,
    MagAmmoUpgrade,
    MagAmmoUpgradeMines,
    TotalAmmoUpgrade,
    TotalAmmoUpgradeMines,
    SpeedUpgrade,
    SpeedUpgradeMines,
    ActiveRangeUpgrade,
    ActiveRangeUpgradeMines,
    OffSetUpgrade,
    OffSetUpgradeMines,
    ShotTimesUpgrade,
    ShotTimesUpgradeMines,
    MakeMoneyUpgrade,
    MakeMoneyUpgradeMines,
    GunSkillIndex,
    GunSkill
    ;

    public static String ShotText(GunDataType DataType){
        switch (DataType){
            case Speed:
                return ChatColor.GREEN + "射擊間格時間: ";
            case DamageAmount:
                return ChatColor.GREEN + "傷害: ";
            case Offset:
                return ChatColor.GREEN + "彈藥偏移: ";
            case MagAmmo:
                return ChatColor.GREEN + "彈夾: ";
            case ShotTimes:
                return ChatColor.GREEN + "彈丸數量: ";
            case ActiveRange:
                return ChatColor.GREEN + "有效射程: ";
            case TotalAmmo:
                return ChatColor.GREEN + "備彈量: ";
            case ReloadTime:
                return ChatColor.GREEN + "換彈時間: ";
            case MakeMoney:
                return ChatColor.GREEN + "賺取金錢: ";
        }
        return null;
    }
}
