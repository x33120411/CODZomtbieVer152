package codzombie.codzombie.GunSystem;

import codzombie.codzombie.CODZombie;
import codzombie.codzombie.CoinSystem.CoinCore;
import codzombie.codzombie.GunSkill.GunSkillListenerEvent;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class SniperGun {

    public static void Shot(Player p) {

        final NBTTagCompound compound = CraftItemStack.asNMSCopy(p.getInventory().getItemInMainHand()).getTag();

        final String Name = compound.getString(NBTKeyType.GunName.toString());

        if (Gun.CheckShotCD(p, Name)) {

            final double HeadShotDamageMultiply = CODZombie.Main.getConfig().getDouble("HeadShotDamageMultiply");

            final double HeadShotMakeCoinMultiply = CODZombie.Main.getConfig().getDouble("HeadShotMakeCoinMultiply");

            double Damage = compound.getDouble(GunDataType.DamageAmount.toString());

            final double ShotSpeed = compound.getDouble(GunDataType.Speed.toString());

            final double ActiveRange = compound.getDouble(GunDataType.ActiveRange.toString());

            int MakeCoin = (int) compound.getDouble(GunDataType.MakeMoney.toString());

            final Location MuzzleParticleLocation = p.getEyeLocation().add(p.getEyeLocation().getDirection().multiply(2));

            final LineTraceDataNoCollide Data = GunAPI.LineTraceFindEntity(p.getEyeLocation(), p.getEyeLocation().add(p.getEyeLocation().getDirection().multiply(ActiveRange)), 0.8);

            final ArrayList<Location> ParticleLocation = GunAPI.getLineAllLocation(p.getEyeLocation(), p.getEyeLocation().add(p.getEyeLocation().getDirection().multiply(ActiveRange)));

            Gun.setShotCD(p, Name, ShotSpeed);

            for (int i = 0; i < Data.SceneEnt.size(); i++) {

                final LivingEntity AttackEnt = (LivingEntity) Data.SceneEnt.get(i);

                if (Ammo.MinesAmmo(p, p.getInventory().getItemInMainHand())) {

                    if (AttackEnt != null) {
                        GunSkillListenerEvent.OnHitMonster(p, AttackEnt, compound, Damage);
                        AttackEnt.setNoDamageTicks(0);
                        final boolean isHeadShot = GunAPI.isHeadShot(Data.SceneLocation.get(i), AttackEnt);
                        if (isHeadShot) {
                            Damage *= HeadShotDamageMultiply;
                            MakeCoin *= HeadShotMakeCoinMultiply;
                            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                            GunSkillListenerEvent.OnHeadShotEvent(p, AttackEnt, compound, Damage);
                        }
                        AttackEnt.damage(Damage, p);

                        CoinCore.HitMonsterAddCoin(p, MakeCoin, isHeadShot);

                        MakeCoin = (int) compound.getDouble(GunDataType.MakeMoney.toString());

                        Damage = compound.getDouble(GunDataType.DamageAmount.toString());

                        if (AttackEnt.isDead()) {
                            p.getInventory().setItem(p.getInventory().getHeldItemSlot(), Gun.UpgradeGun(p.getInventory().getItemInMainHand()));
                            p.getInventory().setItem(p.getInventory().getHeldItemSlot(), Gun.getRandomGunSkill(p, p.getInventory().getItemInMainHand()));
                        }
                    }

                    p.getWorld().spawnParticle(Particle.REDSTONE, MuzzleParticleLocation, 1, new Particle.DustOptions(Color.WHITE, 0.5f));
                }
            }

            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_IRON_GOLEM_HURT, 1.0f, 0.2f);

            final Particle.DustOptions ParticleSetting = new Particle.DustOptions(Color.BLUE, 1.0f);

            for (Location location : ParticleLocation) {
                location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, ParticleSetting);
            }
        }
    }

}
