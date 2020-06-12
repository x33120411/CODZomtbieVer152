package codzombie.codzombie.WaveSystem;

import codzombie.codzombie.CODZombie;
import com.google.common.collect.HashBasedTable;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.inventory.InventoryType;

import java.util.HashMap;
import java.util.UUID;

public class MonsterData {

    public static HashBasedTable<String, MonsterDataType, Double> MonsterData = HashBasedTable.create();

    public static HashBasedTable<String, MonsterDataType, Double> UnlimitedModMonsterData = HashBasedTable.create();

    public static HashBasedTable<String, MonsterDataType, Material> MonsterEquipment = HashBasedTable.create();

    public static HashMap<String, EntityType> MonsterType = new HashMap<>();

    public static HashMap<UUID, String> MonsterNameFromUID = new HashMap<>();

    public static void LoadMonsterData() {
        for (String MonsterName : CODZombie.Main.MonsterSettingConfig.getConfigurationSection("MonsterDataList").getKeys(false)) {
            for (String DataType : CODZombie.Main.MonsterSettingConfig.getConfigurationSection("MonsterDataList." + MonsterName).getKeys(false)) {

                if (DataType.equals("UnlimitedModSpawnWave") || DataType.equals("UnlimitedModUnSpawnWave")){
                    continue;
                }

                final MonsterDataType MonsterDataType = codzombie.codzombie.WaveSystem.MonsterDataType.valueOf(DataType);

                final double DataValue = CODZombie.Main.MonsterSettingConfig.getDouble("MonsterDataList." + MonsterName + "." + DataType);

                switch (MonsterDataType) {
                    case Type:
                        MonsterType.put(MonsterName, EntityType.valueOf(CODZombie.Main.MonsterSettingConfig.getString("MonsterDataList." + MonsterName + "." + DataType)));
                        break;
                    case Armor:
                    case KnockBackResistance:
                    case MovementSpeed:
                    case Health:
                    case Damage:
                    case SummonTime:
                        MonsterData.put(MonsterName, MonsterDataType, DataValue);
                        break;
                    case Helmet:
                    case Leg:
                    case Boot:
                    case Chestplate:
                        MonsterEquipment.put(MonsterName, MonsterDataType, Material.valueOf(CODZombie.Main.MonsterSettingConfig.getString("MonsterDataList." + MonsterName + "." + DataType)));
                        break;
                }

                if (MonsterDataType == codzombie.codzombie.WaveSystem.MonsterDataType.Damage) {

                    final double DamageMultiply = CODZombie.Main.getConfig().getDouble("UnlimitedModDamageMultiply");

                    UnlimitedModMonsterData.put(MonsterName, MonsterDataType, MonsterData.get(MonsterName, MonsterDataType) * DamageMultiply);
                }

                if (MonsterDataType == codzombie.codzombie.WaveSystem.MonsterDataType.Health) {

                    final double HealthMultiply = CODZombie.Main.getConfig().getDouble("UnlimitedModHealthMultiply");

                    UnlimitedModMonsterData.put(MonsterName, MonsterDataType, MonsterData.get(MonsterName, MonsterDataType) * HealthMultiply);
                }
            }
        }
    }

    public static Material getMonsterEquipment(String MonsterName, MonsterDataType DataType){
        if (MonsterEquipment.containsRow(MonsterName)){
            switch (DataType){
                case Helmet:
                case Chestplate:
                case Leg:
                case Boot:
                    if (MonsterEquipment.contains(MonsterName, DataType)) return MonsterEquipment.get(MonsterName, DataType);
                    else return null;
            }
        }
        return null;
    }

    public static boolean hasMonsterEquipment(String MonsterName, MonsterDataType DataType){
        if (MonsterEquipment.contains(MonsterName, DataType)){
            return true;
        }
        return false;
    }

    public static void UpgradeUnlimitedModMonster() {
        for (String MonsterName : MonsterData.rowKeySet()) {
            for (MonsterDataType DataType : MonsterData.row(MonsterName).keySet()) {
                if (UnlimitedModMonsterData.contains(MonsterName, DataType)) {
                    switch (DataType) {
                        case Damage:
                        case Health:
                            final double UpgradeValue = MonsterData.get(MonsterName, DataType) + (UnlimitedModMonsterData.get(MonsterName, DataType) * WaveCore.getWaveNumber());
                            MonsterData.put(MonsterName, DataType, UpgradeValue);
                            break;
                    }
                }
            }
        }
    }

    public static String getMonsterName(UUID UID) {
        return MonsterNameFromUID.get(UID);
    }

    public static double getMonsterData(String MonsterName, MonsterDataType DataType) {
        return MonsterData.get(MonsterName, DataType);
    }

    public static EntityType getMonsterType(String MonsterName) {
        return MonsterType.get(MonsterName);
    }

}
