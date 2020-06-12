package codzombie.codzombie.GunSystem;

import codzombie.codzombie.CODZombie;
import codzombie.codzombie.GunSkill.SkillType;
import codzombie.codzombie.ServerCore;
import codzombie.codzombie.WaveSystem.WaveCore;
import com.google.common.collect.HashBasedTable;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Gun {

    public static HashBasedTable<String, GunDataType, Double> GunData = HashBasedTable.create();

    public static HashBasedTable<UUID, String, Double> CDTime = HashBasedTable.create();

    public static HashMap<String, String> GunDisplayName = new HashMap<>();

    public static HashMap<String, Material> GunMaterial = new HashMap<>();

    public static HashMap<String, GunType> GunType = new HashMap<>();

    public static HashMap<UUID, ArrayList<String>> ShotCD = new HashMap<>();

    public static ArrayList<Material> AllGunType = new ArrayList<>();

    //載入槍械數據
    public static void LoadGunData() {
        for (String GunName : CODZombie.GunSettingConfig.getConfigurationSection("GunSettingList").getKeys(false)) {
            for (String DataType : CODZombie.GunSettingConfig.getConfigurationSection("GunSettingList." + GunName).getKeys(false)) {

                GunDataType GunDataType = codzombie.codzombie.GunSystem.GunDataType.valueOf(DataType);

                switch (GunDataType) {
                    case SpeedUpgrade:
                    case SpeedUpgradeMines:
                    case DamageUpgrade:
                    case DamageUpgradeMines:
                    case OffSetUpgrade:
                    case OffSetUpgradeMines:
                    case MagAmmoUpgrade:
                    case MagAmmoUpgradeMines:
                    case MakeMoneyUpgrade:
                    case MakeMoneyUpgradeMines:
                    case ShotTimesUpgrade:
                    case ShotTimesUpgradeMines:
                    case TotalAmmoUpgrade:
                    case TotalAmmoUpgradeMines:
                    case ReloadTimeUpgrade:
                    case ReloadTimeUpgradeMines:
                    case ActiveRangeUpgrade:
                    case ActiveRangeUpgradeMines:
                    case Speed:
                    case DamageAmount:
                    case Offset:
                    case ReloadTime:
                    case TotalAmmo:
                    case ActiveRange:
                    case MagAmmo:
                    case ShotTimes:
                    case MakeMoney:
                        GunData.put(GunName, GunDataType, CODZombie.GunSettingConfig.getDouble("GunSettingList." + GunName + "." + DataType));
                        break;
                    case MaterialType:
                        GunMaterial.put(GunName, Material.valueOf(CODZombie.GunSettingConfig.getString("GunSettingList." + GunName + "." + DataType)));
                        AllGunType.add(Material.valueOf(CODZombie.GunSettingConfig.getString("GunSettingList." + GunName + "." + DataType)));
                        break;
                    case DisplayName:
                        GunDisplayName.put(GunName, CODZombie.GunSettingConfig.getString("GunSettingList." + GunName + "." + DataType));
                        break;
                    case FireMode:
                        GunType.put(GunName, codzombie.codzombie.GunSystem.GunType.valueOf(CODZombie.GunSettingConfig.getString("GunSettingList." + GunName + "." + DataType)));
                        break;
                }
            }
        }
    }

    //設定射擊冷卻
    public static void setShotCD(Player p, String GunName, double Time) {
        if (ShotCD.containsKey(p.getUniqueId()) && !ShotCD.get(p.getUniqueId()).contains(GunName)) {
            ShotCD.get(p.getUniqueId()).add(GunName);
        } else {
            ArrayList<String> NewGunName = new ArrayList<>();
            NewGunName.add(GunName);
            ShotCD.put(p.getUniqueId(), NewGunName);
        }
        CDTime.put(p.getUniqueId(), GunName, ServerCore.ServerOpenTime + Time);
    }

    //檢測射擊冷卻
    public static synchronized void SceneShotCD() {
        HashMap<UUID, String> WaitRemove = new HashMap<>();
        for (UUID UID : CDTime.rowKeySet()) {
            for (String Name : CDTime.row(UID).keySet()) {
                if (ServerCore.ServerOpenTime >= CDTime.get(UID, Name) && ShotCD.containsKey(UID)) {
                    ShotCD.get(UID).remove(Name);
                    WaitRemove.put(UID, Name);
                }
            }
        }

        for (UUID UID : WaitRemove.keySet()) {
            CDTime.remove(UID, WaitRemove.get(UID));
            WaitRemove.clear();
        }
    }

    //確認玩家是否正在射擊冷卻
    public static boolean CheckShotCD(Player p, String Name) {
        if (ShotCD.containsKey(p.getUniqueId()) && ShotCD.get(p.getUniqueId()).contains(Name)) {
            return false;
        }
        return true;
    }

    //射擊
    public static void Shot(Player p) {
        if (p.getInventory().getItemInMainHand().getType() != Material.AIR &&
                CraftItemStack.asNMSCopy(p.getInventory().getItemInMainHand()).hasTag() &&
                CraftItemStack.asNMSCopy(p.getInventory().getItemInMainHand()).getTag().hasKey(NBTKeyType.FireMode.toString()) &&
                !WaveCore.DeathPlayerUID.contains(p.getUniqueId())) {
            switch (codzombie.codzombie.GunSystem.GunType.valueOf(CraftItemStack.asNMSCopy(p.getInventory().getItemInMainHand()).getTag().getString(NBTKeyType.FireMode.toString()))) {
                case ROCKET:
                    break;
                case SHOT_GUN:
                    ShotGun.Shot(p);
                    break;
                case RIFLE_GUN:
                    Rifle.Shot(p);
                    break;
                case SNIPER_GUN:
                    SniperGun.Shot(p);
                    break;
                case Teaser:
                    Teaser.Shot(p);
                    break;
            }
        }
    }

    //取得槍枝數據
    public static double getGunData(String Name, GunDataType DataType) {
        if (GunData.contains(Name, DataType)) {
            return GunData.get(Name, DataType);
        }
        return 0.0;
    }

    //玩家丟棄槍枝
    public static void PlayerDropGun(PlayerDropItemEvent e) {
        if (CraftItemStack.asNMSCopy(e.getItemDrop().getItemStack()).hasTag() && CraftItemStack.asNMSCopy(e.getItemDrop().getItemStack()).getTag().hasKey(NBTKeyType.GunName.toString())) {
            if (e.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR && e.getPlayer().getInventory().getItemInMainHand().getAmount() > 1) {
                Item item = e.getPlayer().getWorld().dropItem(e.getItemDrop().getLocation(), e.getPlayer().getInventory().getItemInMainHand());
                e.getPlayer().getInventory().removeItem(e.getPlayer().getInventory().getItemInMainHand());
                item.setPickupDelay(40);
                item.setVelocity(e.getPlayer().getEyeLocation().getDirection().multiply(0.5));
                e.getItemDrop().remove();
            }
        }
    }

    //增加殺傷計數器
    public static ItemStack AddKillCount(ItemStack Item) {
        if (CraftItemStack.asNMSCopy(Item).hasTag() && CraftItemStack.asNMSCopy(Item).getTag().hasKey(NBTKeyType.KillCount.toString())) {

            net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(Item);

            final NBTTagCompound compound = CraftItemStack.asNMSCopy(Item).getTag();

            final int CurrentKillCount = compound.getInt(NBTKeyType.KillCount.toString());

            compound.setInt(NBTKeyType.KillCount.toString(), CurrentKillCount + 1);

            NMSItem.setTag(compound);

            ItemStack NewItem = CraftItemStack.asBukkitCopy(NMSItem);

            return NewItem;
        }
        return null;
    }

    //增強成長型武器
    public static ItemStack UpgradeGun(ItemStack Item) {

        final net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(Item);

        final NBTTagCompound compound = NMSItem.getTag();

        if (compound != null && compound.hasKey(NBTKeyType.KillCount.toString())) {

            final String GunName = compound.getString(NBTKeyType.GunName.toString());

            for (GunDataType DataType : GunDataType.values()) {
                if (DataType.toString().contains("Upgrade") && !DataType.toString().contains("UpgradeMines")) {
                    switch (DataType) {
                        case SpeedUpgrade:
                            compound.setDouble(GunDataType.Speed.toString(), compound.getDouble(GunDataType.Speed.toString()) + compound.getDouble(DataType.toString()));
                            compound.setDouble(DataType.toString(), compound.getDouble(DataType.toString()) - Gun.getGunData(GunName, GunDataType.valueOf(DataType.toString() + "Mines")));
                            break;
                        case DamageUpgrade:
                            compound.setDouble(GunDataType.DamageAmount.toString(), compound.getDouble(GunDataType.DamageAmount.toString()) + compound.getDouble(DataType.toString()));
                            compound.setDouble(DataType.toString(), compound.getDouble(DataType.toString()) - Gun.getGunData(GunName, GunDataType.valueOf(DataType.toString() + "Mines")));
                            break;
                        case OffSetUpgrade:
                            compound.setDouble(GunDataType.Offset.toString(), compound.getDouble(GunDataType.Offset.toString()) + compound.getDouble(DataType.toString()));
                            compound.setDouble(DataType.toString(), compound.getDouble(DataType.toString()) - Gun.getGunData(GunName, GunDataType.valueOf(DataType.toString() + "Mines")));
                            break;
                        case MagAmmoUpgrade:
                            compound.setDouble(GunDataType.MagAmmo.toString(), compound.getDouble(GunDataType.MagAmmo.toString()) + compound.getDouble(DataType.toString()));
                            compound.setDouble(DataType.toString(), compound.getDouble(DataType.toString()) - Gun.getGunData(GunName, GunDataType.valueOf(DataType.toString() + "Mines")));
                            break;
                        case MakeMoneyUpgrade:
                            compound.setDouble(GunDataType.MakeMoney.toString(), compound.getDouble(GunDataType.MakeMoney.toString()) + compound.getDouble(DataType.toString()));
                            compound.setDouble(DataType.toString(), compound.getDouble(DataType.toString()) - Gun.getGunData(GunName, GunDataType.valueOf(DataType.toString() + "Mines")));
                            break;
                        case ShotTimesUpgrade:
                            compound.setDouble(GunDataType.ShotTimes.toString(), compound.getDouble(GunDataType.ShotTimes.toString()) + compound.getDouble(DataType.toString()));
                            compound.setDouble(DataType.toString(), compound.getDouble(DataType.toString()) - Gun.getGunData(GunName, GunDataType.valueOf(DataType.toString() + "Mines")));
                            break;
                        case ReloadTimeUpgrade:
                            compound.setDouble(GunDataType.ReloadTime.toString(), compound.getDouble(GunDataType.ReloadTime.toString()) + compound.getDouble(DataType.toString()));
                            compound.setDouble(DataType.toString(), compound.getDouble(DataType.toString()) - Gun.getGunData(GunName, GunDataType.valueOf(DataType.toString() + "Mines")));
                            break;
                        case ActiveRangeUpgrade:
                            compound.setDouble(GunDataType.ActiveRange.toString(), compound.getDouble(GunDataType.ActiveRange.toString()) + compound.getDouble(DataType.toString()));
                            compound.setDouble(DataType.toString(), compound.getDouble(DataType.toString()) - Gun.getGunData(GunName, GunDataType.valueOf(DataType.toString() + "Mines")));
                            break;
                    }
                }
            }

            NMSItem.setTag(compound);

            Item = CraftItemStack.asBukkitCopy(NMSItem);

            Item = AddKillCount(Item);

            Item = UpdateGunLore(Item);
        }

        return Item;
    }

    //更新武器描述
    public static ItemStack UpdateGunLore(ItemStack Item) {

        final NBTTagCompound compound = CraftItemStack.asNMSCopy(Item).getTag();

        final int KillCount;

        final List<String> Lore = new ArrayList<>();

        final String GunName = compound.getString(NBTKeyType.GunName.toString());

        for (GunDataType DataType : GunDataType.values()) {

            String Text = GunDataType.ShotText(DataType);

            final double DataValue = compound.getDouble(DataType.toString());

            if (DataValue == 0.0 || Text == null || DataType == GunDataType.TotalAmmo) {
                continue;
            }

            if (DataValue > Gun.getGunData(GunName, DataType) || DataValue < Gun.getGunData(GunName, DataType)) {
                Lore.add(Text + Gun.getGunData(GunName, DataType) + ChatColor.YELLOW + " to " + ChatColor.GREEN + GunAPI.nf.format(DataValue));
                continue;
            } else {
                Lore.add(Text + compound.getDouble(DataType.toString()));
            }
        }

        if (compound.hasKey(NBTKeyType.KillCount.toString())) {

            KillCount = compound.getInt(NBTKeyType.KillCount.toString());

            for (short i = 0; i < Lore.size(); i++) {
                if (Lore.get(i).contains("殺傷記數")) {
                    Lore.remove(i);
                    Lore.add(i, ChatColor.RED + ChatColor.BOLD.toString() + "殺傷記數: " + KillCount);
                }
            }

            if (!Lore.contains("殺傷記數")) {
                Lore.add(ChatColor.RED + ChatColor.BOLD.toString() + "殺傷記數: " + KillCount);
            }
        }

        final ItemMeta Meta = Item.getItemMeta();

        Meta.setLore(Lore);

        Item.setItemMeta(Meta);

        UpdateGunSkillLore(compound, Item);

        return Item;
    }

    //取得隨機技能
    static ItemStack getRandomGunSkill(Player p, ItemStack Item){

        final net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(Item);

        final NBTTagCompound compound = NMSItem.getTag();

        if (compound.getInt(NBTKeyType.KillCount.toString()) >= compound.getInt(NBTKeyType.GetSkillCount.toString())) {

            final int GunSKillKillCountIncrease = CODZombie.Main.getConfig().getInt("KillCountGetSkillIncrease");

            int Index = compound.getInt(GunDataType.GunSkillIndex.toString());;

            boolean isCompleteGet = true;

            do {

                if (Index > SkillType.values().length) {
                    return Item;
                }

                compound.setInt(NBTKeyType.GetSkillCount.toString(), compound.getInt(NBTKeyType.KillCount.toString()) + GunSKillKillCountIncrease);

                SkillType RandomType = SkillType.values()[GunAPI.random.nextInt(SkillType.values().length)];

                if (!CheckHasSameGunSkill(compound, RandomType)) {

                    compound.setString(GunDataType.GunSkill.toString() + Index, RandomType.toString());

                    p.sendMessage(ChatColor.LIGHT_PURPLE + "此槍枝已取得新技能" + "'" + SkillType.getSkillDisplayName(RandomType) + "'");

                    Index++;

                    isCompleteGet = false;
                }

            } while (isCompleteGet);

            compound.setInt(GunDataType.GunSkillIndex.toString(), Index);

            NMSItem.setTag(compound);

            Item = CraftItemStack.asBukkitCopy(NMSItem);
        }

        return Item;

    }

    //更新槍枝技能描述
    private static void UpdateGunSkillLore(NBTTagCompound compound, ItemStack Item){

        final ItemMeta Meta = Item.getItemMeta();

        List<String> Lore = Meta.getLore();

        for (short i = 1; i < compound.getInt(GunDataType.GunSkillIndex.toString()); i++){
            Lore.add(SkillType.getSkillDisplayName(SkillType.valueOf(compound.getString(GunDataType.GunSkill.toString() + i))));
        }

        Meta.setLore(Lore);

        Item.setItemMeta(Meta);

    }

    //確認槍枝是否有相同技能
    private static boolean CheckHasSameGunSkill(NBTTagCompound compound, SkillType CheckType){
        if (compound.hasKey(GunDataType.GunSkillIndex.toString())){

            final int Index = compound.getInt(GunDataType.GunSkillIndex.toString());

            for (int i = 1; i < Index; i++){
                if (compound.getString(GunDataType.GunSkill.toString() + i).equals(CheckType.toString())){
                    return true;
                }
            }
            return false;
        }
        else return false;
    }
}
