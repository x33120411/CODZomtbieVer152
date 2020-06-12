package codzombie.codzombie.GunSkill;

import codzombie.codzombie.GunSystem.GunDataType;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class GunSkillListenerEvent {

    public static void OnHitMonster(Player p, Entity HitEnt, NBTTagCompound WeaponNBT, double DamageAmount){
        for (short i = 1; i < WeaponNBT.getInt(GunDataType.GunSkillIndex.toString()); i++){
            switch (SkillType.valueOf(WeaponNBT.getString(GunDataType.GunSkill.toString() + i))){

            }
        }
    }

    public static void OnHeadShotEvent(Player p, Entity HitEnt, NBTTagCompound WeaponNBT, double DamageAmount){
        for (short i = 1; i < WeaponNBT.getInt(GunDataType.GunSkillIndex.toString()); i++){
            switch (SkillType.valueOf(WeaponNBT.getString(GunDataType.GunSkill.toString() + i))){
                case FireCircle:
                    FireCircle.Trigger(p, HitEnt, WeaponNBT);
                    break;
            }
        }
    }

}
