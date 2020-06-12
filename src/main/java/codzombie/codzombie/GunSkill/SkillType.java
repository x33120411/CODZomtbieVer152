package codzombie.codzombie.GunSkill;

import org.bukkit.ChatColor;

public enum SkillType {
    FireCircle,
    ;

    public static String getSkillDisplayName(SkillType Type){
        switch (Type){
            case FireCircle:
                return ChatColor.RED + ChatColor.BOLD.toString() + "彈跳陷阱";
            default:
                return null;
        }
    }
}
