package codzombie.codzombie.WeatherEvent;

import codzombie.codzombie.CODZombie;
import codzombie.codzombie.GunSystem.GunAPI;
import codzombie.codzombie.WaveSystem.MonsterData;
import codzombie.codzombie.WaveSystem.MonsterDataType;
import codzombie.codzombie.WaveSystem.WaveCore;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.UUID;

public class RainyEvent {

    static boolean isRainy = false;

    public static void RainyEvent(WeatherChangeEvent e) {
        if (e.toWeatherState()) {

            isRainy = true;

            for (Player p : CODZombie.Main.getServer().getOnlinePlayers()) {
                p.sendTitle(ChatColor.RED + "下雨了", ChatColor.RED + "殭屍漸漸的變強了", 20, 60, 20);
            }

        }
    }

    public static synchronized void UpgradeMonster() {

        if (isRainy) {

            for (UUID UID : WaveCore.AllMonsterUID) {

                Monster Mob = (Monster) CODZombie.Main.getServer().getEntity(UID);

                if (Mob != null) {

                    final double MovementSpeedMultiply = CODZombie.Main.getConfig().getDouble("RainyMonsterMovementMulti");

                    if (Mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() <= MonsterData.getMonsterData(MonsterData.MonsterNameFromUID.get(UID), MonsterDataType.MovementSpeed) && !GunAPI.LineTraceFindCover(Mob.getEyeLocation(), Mob.getEyeLocation().add(0, 10, 0))) {

                        Mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(Mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * MovementSpeedMultiply);
                    }
                    if (Mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() > MonsterData.getMonsterData(MonsterData.MonsterNameFromUID.get(UID), MonsterDataType.MovementSpeed) && GunAPI.LineTraceFindCover(Mob.getEyeLocation(), Mob.getEyeLocation().add(0, 10, 0))) {

                        Mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(MonsterData.getMonsterData(MonsterData.MonsterNameFromUID.get(UID), MonsterDataType.MovementSpeed));
                    }
                }
            }

        } else {
            for (UUID UID : WaveCore.AllMonsterUID) {

                Monster Mob = (Monster) CODZombie.Main.getServer().getEntity(UID);

                if (Mob != null) {

                    Mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(MonsterData.getMonsterData(MonsterData.MonsterNameFromUID.get(UID), MonsterDataType.MovementSpeed));
                }
            }
        }
    }
}
