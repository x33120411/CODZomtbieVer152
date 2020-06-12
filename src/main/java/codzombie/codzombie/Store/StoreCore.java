package codzombie.codzombie.Store;

import codzombie.codzombie.CODZombie;
import codzombie.codzombie.CoinSystem.CoinCore;
import codzombie.codzombie.GunSystem.Gun;
import codzombie.codzombie.GunSystem.GunItem;
import codzombie.codzombie.WallSystem.WallCore;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class StoreCore {

    public static HashMap<Location, String> StoreName = new HashMap<>();

    public static HashMap<Location, Integer> StorePrice = new HashMap<>();

    public static HashMap<Location, ArmorStand> StoreStand = new HashMap<>();

    public static HashMap<ArmorStand, Location> GetLocationFromStand = new HashMap<>();

    public static HashMap<Integer, Location> SettingStoreIndex = new HashMap<>();

    public static void LoadAllStoreData() {
        if (CODZombie.BuildingLocationConfig.contains("StoreIndex")) {

            StoreName.clear();
            StoreStand.clear();
            GetLocationFromStand.clear();

            for (String LocationName : CODZombie.BuildingLocationConfig.getConfigurationSection("StoreList").getKeys(false)) {

                double X = 0, Y = 0, Z = 0;
                String Type = null;
                int Price = 0;

                for (String Axis : CODZombie.BuildingLocationConfig.getConfigurationSection("StoreList." + LocationName).getKeys(false)) {
                    switch (Axis) {
                        case "X":
                            X = CODZombie.BuildingLocationConfig.getDouble("StoreList." + LocationName + "." + Axis);
                            break;
                        case "Y":
                            Y = CODZombie.BuildingLocationConfig.getDouble("StoreList." + LocationName + "." + Axis);
                            break;
                        case "Z":
                            Z = CODZombie.BuildingLocationConfig.getDouble("StoreList." + LocationName + "." + Axis);
                            break;
                        case "Type":
                            Type = CODZombie.BuildingLocationConfig.getString("StoreList." + LocationName + "." + Axis);
                            break;
                        case "Price":
                            Price = CODZombie.BuildingLocationConfig.getInt("StoreList." + LocationName + "." + Axis);
                            break;
                    }
                }
                final Location FinalLocation = new Location(CODZombie.Main.getServer().getWorld("World"), X, Y, Z);
                StoreName.put(FinalLocation, Type);
                StorePrice.put(FinalLocation, Price);
                SpawnArmorStand(FinalLocation, Type);
                ResetArmorStand(FinalLocation);
                SpawnDisplayItem(FinalLocation, Type);
                GetLocationFromStand.put(StoreStand.get(FinalLocation), FinalLocation);
            }
        }
    }

    public static void SpawnDisplayItem(Location location, String StoreName){

        Item item = null;

        Material Type = StoreType.getShowItemType(StoreName);

        if (Type == null) {
            if (Gun.GunMaterial.containsKey(StoreName)){
                Type = Gun.GunMaterial.get(StoreName);
            }
        }

        for (Entity Ent : location.getWorld().getNearbyEntities(location, 1, 1, 1)){
            if (Ent instanceof Item){
                if (((Item) Ent).getItemStack().getType() == Type){
                    item = (Item) Ent;
                }
            }
        }

        if (item == null) item = location.getWorld().dropItem(location, new ItemStack(Type));
        item.setVelocity(new Vector(0, 0, 0));
        item.setPickupDelay(36000000);
    }

    public static void SpawnArmorStand(Location location, String StoreName) {

        List<Entity> NearEnt = (List<Entity>) location.getWorld().getNearbyEntities(location, 1, 1, 1);

        for (Entity Ent : NearEnt){
            if (Ent instanceof ArmorStand){
                StoreStand.put(location, (ArmorStand) Ent);
                return;
            }
        }

        final int Price = StorePrice.get(location);

        String Text = StoreType.getShowText(StoreName);

        if (Text == null) {
            if (Gun.GunDisplayName.containsKey(StoreName)){
                Text = Gun.GunDisplayName.get(StoreName);
            }
        }

        ArmorStand Stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        Stand.setCustomNameVisible(true);
        Stand.setCustomName(Text + " 價格: " + Price);
        Stand.setInvulnerable(true);
        Stand.setVisible(false);
        Stand.setGravity(false);
        Stand.setSmall(true);

        GetLocationFromStand.put(Stand, location);
        StoreStand.put(location, Stand);
    }

    public static void ResetArmorStand(Location location) {

        ArmorStand Stand = StoreStand.get(location);

        final int Price = StorePrice.get(location);

        String Text = StoreType.getShowText(StoreName.get(location));

        if (Text == null) {
            if (Gun.GunDisplayName.containsKey(StoreName.get(location))){
                Text = Gun.GunDisplayName.get(StoreName.get(location));
            }
        }

        Stand.setCustomNameVisible(true);
        Stand.setCustomName(Text + " 價格: " + Price);
        Stand.setInvulnerable(true);
        Stand.setVisible(false);
        Stand.setGravity(false);
    }

    public static void StoreCatch(Player p, Block block) {

        if (WallCore.RecordModePlayer.contains(p.getUniqueId()) && block.getType() == Material.ENDER_CHEST) {

            int Index;

            if (CODZombie.BuildingLocationConfig.contains("StoreIndex")) {
                Index = CODZombie.BuildingLocationConfig.getInt("StoreIndex");
            } else {
                Index = 1;
            }

            SettingStoreIndex.put(Index, block.getLocation());

            p.sendMessage(ChatColor.GREEN + "有新的商店被設立 編號: " + Index);
            p.sendMessage(ChatColor.GREEN + "請進行設定 /registerStore <價格> <編號> <商店類型>");

            Index++;

            CODZombie.BuildingLocationConfig.set("StoreIndex", Index);

            try {
                CODZombie.BuildingLocationConfig.save(CODZombie.BuildingLocationFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setStore(Player p, int Price, int Index, String StoreName) {

        if (SettingStoreIndex.containsKey(Index)) {

            String ShowText = StoreType.getShowText(StoreName);

            Location location = SettingStoreIndex.get(Index);

            SettingStoreIndex.remove(Index);

            if (ShowText == null) {
                if (Gun.GunDisplayName.containsKey(StoreName)){
                    ShowText = Gun.GunDisplayName.get(StoreName);
                }
                else {
                    p.sendMessage(ChatColor.RED + "所輸入的商店類別錯誤");
                    return;
                }
            }

            CODZombie.BuildingLocationConfig.set("StoreList.Location " + Index + ".X", location.getX());
            CODZombie.BuildingLocationConfig.set("StoreList.Location " + Index + ".Y", location.getY());
            CODZombie.BuildingLocationConfig.set("StoreList.Location " + Index + ".Z", location.getZ());
            CODZombie.BuildingLocationConfig.set("StoreList.Location " + Index + ".Type", StoreName);
            CODZombie.BuildingLocationConfig.set("StoreList.Location " + Index + ".Price", Price);

            Index++;

            CODZombie.BuildingLocationConfig.set("StoreIndex", Index);

            try {
                CODZombie.BuildingLocationConfig.save(CODZombie.BuildingLocationFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            p.sendMessage(ChatColor.GREEN + "商店類別: " + ShowText + "  價格: " + Price);

        } else {
            p.sendMessage(ChatColor.RED + "編號輸入錯誤");
        }
    }

    public static void BuyItem(PlayerInteractAtEntityEvent e){
        if (GetLocationFromStand.containsKey(e.getRightClicked())){

            final Location KeyLocation = GetLocationFromStand.get(e.getRightClicked());

            if (CoinCore.CheckHasCoin(e.getPlayer(), StorePrice.get(KeyLocation))) {

                String BuyItemName = StoreName.get(KeyLocation);

                ItemStack Item = null;

                boolean isCompleteToBuy = StoreType.BuyItem(BuyItemName, e.getPlayer());

                if (!isCompleteToBuy) {
                    Item = GunItem.CreateGunItem(BuyItemName, false);
                    if (Item != null) isCompleteToBuy = true;
                }

                if (Item != null) {
                    e.getPlayer().getInventory().addItem(Item);
                    isCompleteToBuy = true;
                }

                if (isCompleteToBuy){
                    CoinCore.MinesPlayerCoin(e.getPlayer(), StorePrice.get(KeyLocation));
                    e.getPlayer().sendMessage(ChatColor.GREEN + "購買成功");
                } else {
                    e.getPlayer().sendMessage(ChatColor.RED + "購買失敗");
                }
            }
            else {
                e.getPlayer().sendMessage(ChatColor.RED + "金錢不足");
            }
        }
    }

}
