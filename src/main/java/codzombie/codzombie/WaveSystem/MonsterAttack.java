package codzombie.codzombie.WaveSystem;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class MonsterAttack {

    //弓箭手設定傷害
    public static void MonsterArrowDamage(EntityDamageByEntityEvent e){
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Arrow && WaveCore.AllMonsterUID.contains(((Arrow) e.getDamager()).getShooter())){

            if (WaveCore.DeathPlayerUID.contains(e.getEntity().getUniqueId())){
                e.setCancelled(true);
                return;
            }

            Arrow arrow = (Arrow) e.getDamager();

            final double Damage = MonsterData.getMonsterData(MonsterData.getMonsterName(((Entity) arrow.getShooter()).getUniqueId()), MonsterDataType.Damage);

            e.setDamage(Damage);
        }
    }

}
