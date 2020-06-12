package codzombie.codzombie.GunSystem;

import codzombie.codzombie.CODZombie;
import codzombie.codzombie.CoinSystem.CoinCore;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Teaser {

    //電擊槍射擊
    public static void Shot (Player p) {

        final NBTTagCompound compound = CraftItemStack.asNMSCopy(p.getInventory().getItemInMainHand()).getTag();

        final String Name = compound.getString(NBTKeyType.GunName.toString());

        if (Gun.CheckShotCD(p, Name)) {

            double Damage = compound.getDouble(GunDataType.DamageAmount.toString());

            final double ShotSpeed = compound.getDouble(GunDataType.Speed.toString());

            final double ActiveRange = compound.getDouble(GunDataType.ActiveRange.toString());

            int MakeCoin = (int) compound.getDouble(GunDataType.MakeMoney.toString());

            final Location MuzzleParticleLocation = p.getEyeLocation().add(p.getEyeLocation().getDirection().multiply(2));

            final LineTraceFindEntityData Data = GunAPI.LineTraceFindEntity(p.getEyeLocation(), p.getEyeLocation().add(p.getEyeLocation().getDirection().multiply(ActiveRange)), 0.8, true);

            final LivingEntity DamageEnt = (LivingEntity) Data.FindEnt;

            final Particle.DustOptions ParticleSetting = new Particle.DustOptions(Color.BLUE, 1.0f);

            final ArrayList<Location> ParticleLocation = GunAPI.getLineAllLocation(p.getEyeLocation(), Data.FindEntLocation);

            Gun.setShotCD(p, Name, ShotSpeed);

            if (Ammo.MinesAmmo(p, p.getInventory().getItemInMainHand())) {

                if (DamageEnt != null) {
                    List<Entity> NearEnt = DamageEnt.getNearbyEntities(5, 3, 5);
                    NearEnt.add(DamageEnt);
                    for (Entity Ent : NearEnt){
                        if (Ent instanceof LivingEntity){
                            ((LivingEntity) Ent).damage(Damage);
                            CoinCore.HitMonsterAddCoin(p, MakeCoin, false);
                            List<Location> Particle = GunAPI.getLineAllLocation(Data.FindEntLocation, ((LivingEntity) Ent).getEyeLocation());

                            if (DamageEnt.isDead()) {
                                p.getInventory().setItem(p.getInventory().getHeldItemSlot(), Gun.UpgradeGun(p.getInventory().getItemInMainHand()));
                                p.getInventory().setItem(p.getInventory().getHeldItemSlot(), Gun.getRandomGunSkill(p, p.getInventory().getItemInMainHand()));
                            }

                            for (Location location : Particle){
                                ParticleLocation.add(location);
                            }
                        }
                    }
                }

                for (Location location : ParticleLocation) {
                    location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, ParticleSetting);
                }

                p.getWorld().spawnParticle(Particle.REDSTONE, MuzzleParticleLocation, 1, new Particle.DustOptions(Color.WHITE, 0.5f));

                p.getWorld().playSound(p.getLocation(), Sound.BLOCK_STONE_BREAK, 1.0f, 0.2f);
            }
        }
    }

}
