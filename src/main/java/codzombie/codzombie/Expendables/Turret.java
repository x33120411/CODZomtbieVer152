package codzombie.codzombie.Expendables;

import codzombie.codzombie.ArmorSystem.ArmorSystem;
import codzombie.codzombie.CODZombie;
import codzombie.codzombie.CoinSystem.CoinCore;
import codzombie.codzombie.GunSystem.*;
import codzombie.codzombie.WaveSystem.WaveCore;
import com.google.common.collect.HashBasedTable;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Turret {

    public static HashBasedTable<UUID, ArmorStand, Integer> AttackTimerID = HashBasedTable.create();

    public static HashMap<ArmorStand, UUID> TurretOwner = new HashMap<>();

    public static HashMap<ArmorStand, Double> TurretHealth = new HashMap<>();

    public static void SpawnTurret(Player p, Location location, TurretType Type) {

        ArmorStand Stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

        double Health = 0;

        switch (Type) {
            case FireTurret:
                FireTurretAttack(p, Stand);
                Stand.getEquipment().setHelmet(new ItemStack(Material.MAGMA_BLOCK));
                Stand.setSmall(true);
                Stand.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(Health);
                Health = 250;
                break;
            case GunTurret:
                GunTurretAttack(p, Stand);
                Stand.getEquipment().setHelmet(new ItemStack(Material.DISPENSER));
                Stand.setSmall(true);
                Health = 50;
                break;
        }

        Stand.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(Health);
        Stand.setCustomNameVisible(true);

        TurretHealth.put(Stand, Health);
        TurretOwner.put(Stand, p.getUniqueId());

        UpdateTurretDisplayHealth(Stand);
    }

    public static void FireTurretAttack(Player Owner, ArmorStand Stand) {

        final double WeaponUsage = CODZombie.Main.getConfig().getDouble("FireTurretWeaponUsage");

        AttackTimerID.put(Owner.getUniqueId(), Stand, CODZombie.Main.getServer().getScheduler().scheduleSyncRepeatingTask(CODZombie.Main, new Runnable() {

            int RunTime = 0;

            @Override
            public void run() {
                final NBTTagCompound compound = CraftItemStack.asNMSCopy(Owner.getInventory().getItemInMainHand()).getTag();

                if (compound != null && !WaveCore.DeathPlayerUID.contains(Owner.getUniqueId()) && compound.getDouble(GunDataType.TotalAmmo.toString()) > 0) {

                    final double ActiveRange = compound.getDouble(GunDataType.ActiveRange.toString()) * WeaponUsage;

                    final ArrayList<Location> CircleLocation = GunAPI.getCircleLocation(Stand.getLocation(), ActiveRange, 30);

                    if (RunTime >= CircleLocation.size()) {
                        RunTime = 0;
                    }

                    LineTraceDataNoCollide Data = GunAPI.LineTraceFindEntity(Stand.getEyeLocation(), CircleLocation.get(RunTime), 1.0);

                    for (Entity Ent : Data.SceneEnt) {
                        if (Ent instanceof Monster) {
                            ((Monster) Ent).setTarget(Stand);
                            if (!((Monster) Ent).hasPotionEffect(PotionEffectType.SLOW))
                                ((Monster) Ent).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1));
                            Ent.setFireTicks(100);
                        }
                    }

                    Vector ParticleFlyDirection = CircleLocation.get(RunTime).clone().subtract(Stand.getLocation()).toVector().normalize();
                    Stand.getWorld().spawnParticle(Particle.FLAME, Stand.getEyeLocation(), 0, ParticleFlyDirection.getX(), ParticleFlyDirection.getY(), ParticleFlyDirection.getZ(), ActiveRange * 0.1, null, true);

                    RunTime++;
                }
            }
        }, 2, 2));
    }

    public static void GunTurretAttack(Player Owner, ArmorStand Stand) {

        final double AttackTime = CODZombie.Main.getConfig().getDouble("GunTurretAttackTime");

        final double ActiveRangeUsage = CODZombie.Main.getConfig().getDouble("GunTurretWeaponActiveRangeUsage");

        AttackTimerID.put(Owner.getUniqueId(), Stand, CODZombie.Main.getServer().getScheduler().scheduleSyncRepeatingTask(CODZombie.Main, new Runnable() {
            @Override
            public void run() {
                final NBTTagCompound compound = CraftItemStack.asNMSCopy(Owner.getInventory().getItemInMainHand()).getTag();

                if (compound != null && !WaveCore.DeathPlayerUID.contains(Owner.getUniqueId()) && compound.getDouble(GunDataType.TotalAmmo.toString()) > 0) {

                    final double ActiveRange = compound.getDouble(GunDataType.ActiveRange.toString()) * ActiveRangeUsage;

                    final GunType Type = GunType.valueOf(compound.getString(NBTKeyType.FireMode.toString()));

                    List<Entity> NearEnt = Stand.getNearbyEntities(ActiveRange, 3, ActiveRange);

                    LivingEntity DamageEnt = null;

                    for (Entity Ent : NearEnt) {
                        if (Ent instanceof Monster) {
                            DamageEnt = (LivingEntity) Ent;
                            ((Monster) Ent).setTarget(Stand);
                        }
                    }

                    if (DamageEnt != null) {

                        if (!Stand.hasLineOfSight(DamageEnt)) {
                            return;
                        }

                        switch (Type) {
                            case RIFLE_GUN:
                                RifleGunMode(Stand, compound, DamageEnt);
                                break;
                            case SHOT_GUN:
                                ShotGunMode(Stand, compound, DamageEnt);
                                break;
                            case SNIPER_GUN:
                                SniperGunMode(Stand, compound, DamageEnt);
                                break;
                        }

                    }
                }
            }
        }, (long) (20 * AttackTime), (long) (20 * AttackTime)));
    }

    public static void RifleGunMode(ArmorStand Stand, NBTTagCompound compound, LivingEntity DamageEnt) {

        final Player Owner = CODZombie.Main.getServer().getPlayer(TurretOwner.get(Stand));

        final double WeaponUsage = CODZombie.Main.getConfig().getDouble("GunTurretWeaponUsage");

        final double Damage = compound.getDouble(GunDataType.DamageAmount.toString()) * WeaponUsage;

        final int MakeMoney = (int) (compound.getDouble(GunDataType.ActiveRange.toString()) * WeaponUsage);

        DamageEnt.setNoDamageTicks(0);

        DamageEnt.damage(Damage, Owner);

        CoinCore.HitMonsterAddCoin(Owner, MakeMoney, false);

        Vector StandFaceDirection = DamageEnt.getLocation().subtract(Stand.getLocation()).toVector().normalize();

        Location TPLocation = Stand.getLocation();

        TPLocation.setDirection(StandFaceDirection);

        Stand.teleport(TPLocation);

        ArrayList<Location> ParticleLine = GunAPI.getLineAllLocation(Stand.getEyeLocation(), DamageEnt.getEyeLocation());

        Stand.getWorld().playSound(Stand.getLocation(), Sound.BLOCK_ANVIL_HIT, 1.0f, 2.5f);

        for (Location location : ParticleLine) {
            location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, new Particle.DustOptions(Color.YELLOW, 1.5f));
        }
    }

    public static void SniperGunMode (ArmorStand Stand, NBTTagCompound compound, LivingEntity DamageEnt) {

        final Player Owner = CODZombie.Main.getServer().getPlayer(TurretOwner.get(Stand));

        double Damage = compound.getDouble(GunDataType.DamageAmount.toString());

        final double ActiveRange = compound.getDouble(GunDataType.ActiveRange.toString());

        int MakeCoin = (int) compound.getDouble(GunDataType.MakeMoney.toString());

        final LineTraceDataNoCollide Data = GunAPI.LineTraceFindEntity(Stand.getEyeLocation(), Stand.getEyeLocation().add(Stand.getEyeLocation().getDirection().multiply(ActiveRange)), 0.8);

        final ArrayList<Location> ParticleLocation = GunAPI.getLineAllLocation(Stand.getEyeLocation(), Stand.getEyeLocation().add(Stand.getEyeLocation().getDirection().multiply(ActiveRange)));

        for (int i = 0; i < Data.SceneEnt.size(); i++) {

            final LivingEntity AttackEnt = (LivingEntity) Data.SceneEnt.get(i);

            if (AttackEnt != null) {

                AttackEnt.setNoDamageTicks(0);

                AttackEnt.damage(Damage, Stand);

                CoinCore.HitMonsterAddCoin(Owner, MakeCoin, false);

                MakeCoin = (int) compound.getDouble(GunDataType.MakeMoney.toString());

                Damage = compound.getDouble(GunDataType.DamageAmount.toString());

                DamageEnt.damage(Damage, Owner);

                Vector StandFaceDirection = DamageEnt.getLocation().subtract(Stand.getLocation()).toVector().normalize();

                Location TPLocation = Stand.getLocation();

                TPLocation.setDirection(StandFaceDirection);

                Stand.teleport(TPLocation);
            }
        }

        Stand.getWorld().playSound(Stand.getLocation(), Sound.ENTITY_IRON_GOLEM_HURT, 1.0f, 0.2f);

        final Particle.DustOptions ParticleSetting = new Particle.DustOptions(Color.YELLOW, 1.0f);

        for (Location location : ParticleLocation) {
            location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, ParticleSetting);
        }
    }

    public static void ShotGunMode(ArmorStand Stand, NBTTagCompound compound, LivingEntity AttackTarget) {

        final double WeaponUsage = CODZombie.Main.getConfig().getDouble("GunTurretWeaponUsage");

        double Damage = compound.getDouble(GunDataType.DamageAmount.toString()) * WeaponUsage;

        final double ActiveRange = compound.getDouble(GunDataType.ActiveRange.toString()) * WeaponUsage;

        final double BulletOffsetStrength = compound.getDouble(GunDataType.Offset.toString()) * WeaponUsage;

        final int ShotTimes = (int) compound.getDouble(GunDataType.ShotTimes.toString());

        final Player Owner = CODZombie.Main.getServer().getPlayer(TurretOwner.get(Stand));

        int MakeCoin = (int) (compound.getDouble(GunDataType.MakeMoney.toString()) * WeaponUsage);

        List<Entity> NearEnt = Stand.getNearbyEntities(ActiveRange, ActiveRange, ActiveRange);

        for (Entity Ent : NearEnt) {
            if (Ent instanceof Monster) {
                AttackTarget = (LivingEntity) Ent;
            }
        }

        if (AttackTarget != null) {

            final Vector StandFaceDirection = AttackTarget.getLocation().subtract(Stand.getLocation()).toVector().normalize();

            final Location DirectionLoc = Stand.getLocation();

            DirectionLoc.setDirection(StandFaceDirection);

            Stand.teleport(DirectionLoc);

            final Location ForwardLocation = Stand.getEyeLocation().add(Stand.getEyeLocation().getDirection().multiply(ActiveRange));

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

                final Particle.DustOptions ParticleSetting = new Particle.DustOptions(Color.YELLOW, 1.5f);

                final Location RandomLocation = new Location(Stand.getWorld(), X, Y, Z);

                final LineTraceFindEntityData Data = GunAPI.LineTraceFindEntity(Stand.getEyeLocation(), RandomLocation, 0.8, true);

                final Entity DamageEnt = Data.FindEnt;

                final ArrayList<Location> ParticleLocation = GunAPI.getLineAllLocation(Stand.getEyeLocation(), RandomLocation);

                if (DamageEnt != null) {

                    ((LivingEntity) DamageEnt).setNoDamageTicks(0);

                    ((LivingEntity) DamageEnt).damage(Damage, Stand);

                    CoinCore.HitMonsterAddCoin(Owner, MakeCoin, false);
                }

                for (Location ParticleLoc : ParticleLocation) {
                    ParticleLoc.getWorld().spawnParticle(Particle.REDSTONE, ParticleLoc, 1, ParticleSetting);
                }
            }
        }
    }

    public static void UpdateTurretDisplayHealth(ArmorStand Stand) {
        Stand.setCustomName(ChatColor.RED + "生命: " + ChatColor.GREEN + GunAPI.nf.format(TurretHealth.get(Stand)) + "/" + Stand.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
    }

    public static void TurretBeDamage(EntityDamageByEntityEvent e) {
        if (TurretOwner.containsKey(e.getEntity()) && e.getDamager() instanceof Monster) {
            ((Monster) e.getDamager()).setTarget(null);
            if (TurretHealth.get(e.getEntity()) <= 0) {
                ((Monster) e.getDamager()).setTarget(null);
            }
        }
    }

    public static void TurretDeath(EntityDamageEvent e) {
        if (TurretOwner.containsKey(e.getEntity())) {

            final double PlayerArmor = ArmorSystem.getArmorValue(TurretOwner.get(e.getEntity()));

            TurretHealth.put((ArmorStand) e.getEntity(), TurretHealth.get(e.getEntity()) - (e.getFinalDamage() * PlayerArmor));
            e.getEntity().getWorld().spawnParticle(Particle.BLOCK_CRACK, ((ArmorStand) e.getEntity()).getEyeLocation(), 3, Material.OAK_WOOD.createBlockData());
            e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENTITY_ARMOR_STAND_HIT, 1.0f, 1.0f);
            e.setCancelled(true);
            UpdateTurretDisplayHealth((ArmorStand) e.getEntity());
        } else {
            return;
        }
        if (TurretHealth.get(e.getEntity()) <= 0) {
            ArmorStand Stand = (ArmorStand) e.getEntity();
            e.getEntity().getWorld().spawnParticle(Particle.BLOCK_CRACK, ((ArmorStand) e.getEntity()).getEyeLocation(), 3, Material.REDSTONE_BLOCK.createBlockData());
            CODZombie.Main.getServer().getScheduler().cancelTask(AttackTimerID.get(TurretOwner.get(Stand), Stand));
            AttackTimerID.remove(TurretOwner.get(Stand), Stand);
            TurretOwner.remove(Stand);
            Stand.getWorld().playSound(Stand.getLocation(), Sound.BLOCK_WOOD_BREAK, 1.0f, 1.0f);
            Stand.remove();
        }
    }

    public static void KillAllTurret() {
        try {
            ArrayList<ArmorStand> RemoveStand = new ArrayList<>();
            for (ArmorStand Stand : TurretOwner.keySet()) {
                CODZombie.Main.getServer().getScheduler().cancelTask(AttackTimerID.get(TurretOwner.get(Stand), Stand));
                AttackTimerID.remove(TurretOwner.get(Stand), Stand);
                RemoveStand.add(Stand);
                Stand.getWorld().playSound(Stand.getLocation(), Sound.BLOCK_WOOD_BREAK, 1.0f, 1.0f);
                Stand.remove();
            }
            for (ArmorStand Stand : RemoveStand) {
                TurretOwner.remove(Stand);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void PlayerInteractArmorStand(PlayerInteractEntityEvent e) {
        if (TurretOwner.containsKey(e.getRightClicked())) {
            e.setCancelled(true);
        }
    }
}
