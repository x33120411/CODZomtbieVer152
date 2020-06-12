package codzombie.codzombie.GunSkill;

import codzombie.codzombie.CODZombie;
import codzombie.codzombie.CoinSystem.CoinCore;
import codzombie.codzombie.GunSystem.GunAPI;
import codzombie.codzombie.GunSystem.GunDataType;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class FireCircle {

    public static void Trigger(Player p, Entity DamageEnt, NBTTagCompound compound){

        final double Damage = compound.getDouble(GunDataType.DamageAmount.toString()) * 1.2;

        final double ActiveRange = compound.getDouble(GunDataType.ActiveRange.toString()) * 0.1;

        final int MakeCoin = compound.getInt(GunDataType.MakeMoney.toString());

        final ArrayList<Location> ParticleLocation = GunAPI.getCircleLocation(DamageEnt.getLocation().add(0, 1.5, 0), ActiveRange, 20);

        for (Location location : ParticleLocation){
            location.getWorld().spawnParticle(Particle.DRIP_LAVA, location, 1);
        }

        CODZombie.Main.getServer().getScheduler().runTaskLater(CODZombie.Main, new Runnable() {
            @Override
            public void run() {
                
                final List<Entity> NearEnt = (List<Entity>) DamageEnt.getWorld().getNearbyEntities(DamageEnt.getLocation(), ActiveRange, 3, ActiveRange);

                for (Entity Ent : NearEnt){
                    if (Ent instanceof LivingEntity && !(Ent instanceof Player)) {
                        CoinCore.HitMonsterAddCoin(p, MakeCoin, false);
                        Ent.setVelocity(new Vector(0, 1, 0));
                        ((LivingEntity) Ent).damage(Damage);
                    }
                }
            }
        }, 20);

    }

}
