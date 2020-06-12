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
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ShotGun {

    //射擊
    public static void Shot (Player p) {

        final NBTTagCompound compound = CraftItemStack.asNMSCopy(p.getInventory().getItemInMainHand()).getTag();

        final String Name = compound.getString(NBTKeyType.GunName.toString());

        if (Gun.CheckShotCD(p, Name)) {

            final double HeadShotDamageMultiply = CODZombie.Main.getConfig().getDouble("HeadShotDamageMultiply");

            final double HeadShotMakeCoinMultiply = CODZombie.Main.getConfig().getDouble("HeadShotMakeCoinMultiply");

            double Damage = compound.getDouble(GunDataType.DamageAmount.toString());

            final double ActiveRange = compound.getDouble(GunDataType.ActiveRange.toString());

            final double BulletOffsetStrength = compound.getDouble(GunDataType.Offset.toString());

            final double ShotSpeed = compound.getDouble(GunDataType.Speed.toString());

            final int ShotTimes = (int) compound.getDouble(GunDataType.ShotTimes.toString());

            int MakeCoin = (int) compound.getDouble(GunDataType.MakeMoney.toString());

            boolean isAddKillCount = false;

            final Location ForwardLocation = p.getEyeLocation().add(p.getEyeLocation().getDirection().multiply(ActiveRange));

            Gun.setShotCD(p, Name, ShotSpeed);

            if (Ammo.MinesAmmo(p, p.getInventory().getItemInMainHand())) {

                for (short i = 0; i < ShotTimes; i++) {

                    double RandomNumber = GunAPI.random.nextDouble() * BulletOffsetStrength;

                    RandomNumber *= GunAPI.random.nextDouble() < 0.5 ? 1.0 : -1.0;

                    double X = ForwardLocation.getX() + RandomNumber;

                    RandomNumber = GunAPI.random.nextDouble() * BulletOffsetStrength;

                    RandomNumber *= GunAPI.random.nextDouble() < 0.5 ? 1.0 : -1.0;

                    double Y = ForwardLocation.getY() + RandomNumber;

                    RandomNumber = GunAPI.random.nextDouble() * BulletOffsetStrength;

                    RandomNumber *= GunAPI.random.nextDouble() < 0.5 ? 1.0 : -1.0;

                    double Z = ForwardLocation.getZ() + RandomNumber;

                    final Particle.DustOptions ParticleSetting = new Particle.DustOptions(Color.BLUE, 0.5f);

                    final Location RandomLocation = new Location(p.getWorld(), X, Y, Z);

                    final LineTraceFindEntityData Data = GunAPI.LineTraceFindEntity(p.getEyeLocation(), RandomLocation, 0.8, true);

                    final Entity DamageEnt = Data.FindEnt;

                    final ArrayList<Location> ParticleLocation = GunAPI.getLineAllLocation(p.getEyeLocation(), Data.FindEntLocation);

                    final Location MuzzleParticleLocation = p.getEyeLocation().add(p.getEyeLocation().getDirection().multiply(2));

                    if (DamageEnt != null) {
                        GunSkillListenerEvent.OnHitMonster(p, DamageEnt, compound, Damage);
                        ((LivingEntity) DamageEnt).setNoDamageTicks(0);
                        final boolean isHeadShot = GunAPI.isHeadShot(Data.FindEntLocation, (LivingEntity) DamageEnt);
                        if (isHeadShot) {
                            Damage *= HeadShotDamageMultiply;
                            MakeCoin *= HeadShotMakeCoinMultiply;
                            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                            GunSkillListenerEvent.OnHeadShotEvent(p, DamageEnt, compound, Damage);
                        }
                        ((LivingEntity) DamageEnt).damage(Damage, p);
                        CoinCore.HitMonsterAddCoin(p, MakeCoin, isHeadShot);
                        if (!isAddKillCount && DamageEnt.isDead()) {
                            p.getInventory().setItem(p.getInventory().getHeldItemSlot(), Gun.UpgradeGun(p.getInventory().getItemInMainHand()));
                            p.getInventory().setItem(p.getInventory().getHeldItemSlot(), Gun.getRandomGunSkill(p, p.getInventory().getItemInMainHand()));
                            isAddKillCount = true;
                        }
                        Damage = compound.getDouble(GunDataType.DamageAmount.toString());
                        MakeCoin = (int) compound.getDouble(GunDataType.MakeMoney.toString());
                    }

                    p.getWorld().spawnParticle(Particle.REDSTONE, MuzzleParticleLocation, 3, new Particle.DustOptions(Color.WHITE, 2));

                    for (Location ParticleLoc : ParticleLocation) {
                        ParticleLoc.getWorld().spawnParticle(Particle.REDSTONE, ParticleLoc, 1, ParticleSetting);
                    }
                }

                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.1f);
            }
        }
    }
}
