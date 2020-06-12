package codzombie.codzombie.DrawBox;

import codzombie.codzombie.CODZombie;
import codzombie.codzombie.CoinSystem.CoinCore;
import codzombie.codzombie.Expendables.ExpendableItem;
import codzombie.codzombie.Expendables.ExpendableType;
import codzombie.codzombie.GunSystem.Gun;
import codzombie.codzombie.GunSystem.GunAPI;
import codzombie.codzombie.GunSystem.GunItem;
import codzombie.codzombie.WallSystem.WallCore;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DrawBox {

    public static ArrayList<Location> DrawBoxLocation = new ArrayList<>();

    public static HashMap<Integer, ArrayList<Location>> DrawBoxIndex = new HashMap<>();

    public static HashMap<Location, Integer> DrawBoxIndexFromLocation = new HashMap<>();

    public static HashMap<Integer, ArmorStand> DrawBoxDisplayStand = new HashMap<>();

    public static HashMap<UUID, Block> LastTimeBlock = new HashMap<>();

    public static HashMap<Integer, Integer> DrawBoxTimer = new HashMap<>();

    public static HashMap<Integer, ItemStack> DrawItem = new HashMap<>();

    public static HashMap<Integer, Item> DrawBoxDisplayItem = new HashMap<>();

    public static ArrayList<Integer> CanTakeItemDrawBox = new ArrayList<>();

    public static ArrayList<Integer> CanUseDrawBox = new ArrayList<>();

    public static ArrayList<Integer> IgnoreDrawBox = new ArrayList<>();

    private static int DrawBoxTimes = 6;

    //設定抽獎箱
    public static void setDrawBox(BlockPlaceEvent e) {
        if (WallCore.RecordModePlayer.contains(e.getPlayer().getUniqueId())) {

            if (LastTimeBlock.containsKey(e.getPlayer().getUniqueId()) && LastTimeBlock.get(e.getPlayer().getUniqueId()).getType() == Material.CHEST) {

                Location location = e.getBlock().getLocation();

                int Index;

                if (CODZombie.BuildingLocationConfig.contains("DrawBoxLocationIndex")) {
                    Index = CODZombie.BuildingLocationConfig.getInt("DrawBoxLocationIndex");
                } else {
                    Index = 1;
                }

                CODZombie.BuildingLocationConfig.set("DrawBoxLocationList.Location " + Index + ".X", location.getX());
                CODZombie.BuildingLocationConfig.set("DrawBoxLocationList.Location " + Index + ".Y", location.getY());
                CODZombie.BuildingLocationConfig.set("DrawBoxLocationList.Location " + Index + ".Z", location.getZ());

                location = LastTimeBlock.get(e.getPlayer().getUniqueId()).getLocation();
                Index++;

                CODZombie.BuildingLocationConfig.set("DrawBoxLocationList.Location " + Index + ".X", location.getX());
                CODZombie.BuildingLocationConfig.set("DrawBoxLocationList.Location " + Index + ".Y", location.getY());
                CODZombie.BuildingLocationConfig.set("DrawBoxLocationList.Location " + Index + ".Z", location.getZ());

                Index++;

                CODZombie.BuildingLocationConfig.set("DrawBoxLocationIndex", Index);

                e.getPlayer().sendMessage(ChatColor.GREEN + "成功設立抽獎箱");
                LastTimeBlock.remove(e.getPlayer().getUniqueId());

                try {
                    CODZombie.BuildingLocationConfig.save(CODZombie.BuildingLocationFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                LastTimeBlock.put(e.getPlayer().getUniqueId(), e.getBlock());
            }
        }
    }

    //生成抽獎箱顯示價格盔甲架
    public static void SpawnDisplayPriceArmorStand(Location location, int Index) {

        List<Entity> NearEnt = (List<Entity>) location.getWorld().getNearbyEntities(location, 1.5, 1.5, 1.5);

        for (Entity Ent : NearEnt) {
            if (Ent.getType() == EntityType.ARMOR_STAND) {
                DrawBoxDisplayStand.put(Index, (ArmorStand) Ent);
                return;
            }
        }

        final int Price = CODZombie.Main.getConfig().getInt("DrawBoxPrice");

        ArmorStand Stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

        Stand.setInvulnerable(true);
        Stand.setVisible(false);
        Stand.setCustomNameVisible(true);
        Stand.setSmall(true);
        Stand.setCustomName(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "幸運箱 價格: " + Price);

        DrawBoxDisplayStand.put(Index, Stand);
    }

    //取得所有抽獎箱位置
    public static void LoadAllDrawBoxLocation() {
        if (CODZombie.BuildingLocationConfig.contains("DrawBoxLocationList")) {

            if (DrawBoxLocation.size() > 0) {
                DrawBoxLocation.clear();
            }

            int Index = 1;
            int RunTime = 0;
            int IndexScene = 2;
            ArrayList<Location> CurrentDrawBoxLocation = new ArrayList<>();
            for (String Key : CODZombie.BuildingLocationConfig.getConfigurationSection("DrawBoxLocationList").getKeys(false)) {
                RunTime++;
                double X = 0, Y = 0, Z = 0;
                for (String Axis : CODZombie.BuildingLocationConfig.getConfigurationSection("DrawBoxLocationList." + Key).getKeys(false)) {
                    switch (Axis) {
                        case "X":
                            X = CODZombie.BuildingLocationConfig.getDouble("DrawBoxLocationList." + Key + "." + Axis);
                            break;
                        case "Y":
                            Y = CODZombie.BuildingLocationConfig.getDouble("DrawBoxLocationList." + Key + "." + Axis);
                            break;
                        case "Z":
                            Z = CODZombie.BuildingLocationConfig.getDouble("DrawBoxLocationList." + Key + "." + Axis);
                            break;
                    }
                }

                final Location PutLocation = new Location(CODZombie.Main.getServer().getWorld("World"), X, Y, Z);
                CurrentDrawBoxLocation.add(PutLocation);

                DrawBoxIndexFromLocation.put(PutLocation, Index);
                if (RunTime == IndexScene) {
                    IndexScene += 2;
                    Location DisplayLocation = CurrentDrawBoxLocation.get(0).clone().add(CurrentDrawBoxLocation.get(0).clone().subtract(CurrentDrawBoxLocation.get(1)).multiply(-1.0));
                    SpawnDisplayPriceArmorStand(DisplayLocation, Index);
                    ArrayList<Location> NewLocation = (ArrayList<Location>) CurrentDrawBoxLocation.clone();
                    DrawBoxIndex.put(Index, NewLocation);
                    CurrentDrawBoxLocation.clear();
                    Index++;
                }
                DrawBoxLocation.add(PutLocation);
            }
            getRandomDrawBox();
        }
    }

    //將顯示價格盔甲架設定成抽獎模式
    public static void SetDisplayStandToShowGunName(ArmorStand Stand, String ItemName, boolean hasKillCount) {
        String KillCountText = "";
        if (hasKillCount) KillCountText = ChatColor.YELLOW + ChatColor.BOLD.toString() + " (KillCount)";
        Stand.setCustomName(ItemName + KillCountText);
    }

    //重置盔甲架
    public static void ResetArmorStand(ArmorStand Stand) {

        final int Price = CODZombie.Main.getConfig().getInt("DrawBoxPrice");

        Stand.setInvulnerable(true);
        Stand.setVisible(false);
        Stand.setCustomNameVisible(true);
        Stand.setSmall(true);
        Stand.setCustomName(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "幸運箱 價格: " + Price);
    }

    //關閉盔甲架
    public static void DisableArmorStand(ArmorStand Stand){
        Stand.setCustomNameVisible(false);
    }

    //開啟抽獎箱
    public static void OpenDrawBox(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && DrawBoxLocation.contains(e.getClickedBlock().getLocation())) {

            final Location SpawnLocation = e.getClickedBlock().getLocation().clone().add(0, 1, 0);

            if (!CanTakeItemDrawBox.contains(DrawBoxIndexFromLocation.get(e.getClickedBlock().getLocation())) && CanUseDrawBox.contains(DrawBoxIndexFromLocation.get(e.getClickedBlock().getLocation())) &&
                    !IgnoreDrawBox.contains(DrawBoxIndexFromLocation.get(e.getClickedBlock().getLocation()))) {

                final int Price = CODZombie.Main.getConfig().getInt("DrawBoxPrice");

                final double KillCountChance = CODZombie.Main.getConfig().getDouble("DrawKillCountChance");

                if (CoinCore.CheckHasCoin(e.getPlayer(), Price)) {

                    IgnoreDrawBox.add(DrawBoxIndexFromLocation.get(e.getClickedBlock().getLocation()));

                    CoinCore.MinesPlayerCoin(e.getPlayer(), Price);

                    DrawBoxTimer.put(DrawBoxIndexFromLocation.get(e.getClickedBlock().getLocation()), CODZombie.Main.getServer().getScheduler().scheduleSyncRepeatingTask(CODZombie.Main, new Runnable() {

                        float RunTimes = 0;
                        int ChestIndex = DrawBoxIndexFromLocation.get(e.getClickedBlock().getLocation());
                        boolean hasKillCount = false;
                        boolean isExpendItem = false;
                        Item item = null;
                        ArrayList<String> CanDrawItemName = new ArrayList<>();
                        String DrawItemName = null;

                        @Override
                        public void run() {
                            if (RunTimes == 0) {

                                for (String GunName : Gun.GunData.rowKeySet()) {

                                    CanDrawItemName.add(GunName);

                                }

                                for (ExpendableType Type : ExpendableType.values()){
                                    CanDrawItemName.add(Type.toString());
                                }

                            }
                            if (RunTimes >= 2.5) {

                                DrawBoxDisplayItem.put(ChestIndex, item);

                                if (!isExpendItem) DrawItem.put(ChestIndex, GunItem.CreateGunItem(DrawItemName, hasKillCount));

                                if (isExpendItem) DrawItem.put(ChestIndex, ExpendableItem.CreateExpendItem(DrawItemName, ExpendableType.getItemAmount(DrawItemName)));

                                SetDrawBoxItem(ChestIndex);

                                ClearTimer(DrawBoxIndexFromLocation.get(e.getClickedBlock().getLocation()));

                                return;
                            }

                            final int RandomNumber = GunAPI.random.nextInt(CanDrawItemName.size());

                            final String RandomName = CanDrawItemName.get(RandomNumber);

                            String DisplayName = null;

                            DrawItemName = RandomName;

                            Material DisplayType = ExpendableType.getItemMaterial(DrawItemName);

                            if (DisplayType != null){

                                isExpendItem = true;

                                hasKillCount = false;

                                DisplayName = ExpendableType.getItemDisplayName(DrawItemName);
                            }
                            else {
                                isExpendItem = false;
                            }

                            if (!isExpendItem) {

                                if (GunAPI.random.nextDouble() < KillCountChance) hasKillCount = true;

                                else hasKillCount = false;

                                DisplayName = Gun.GunDisplayName.get(DrawItemName);

                                DisplayType = Gun.GunMaterial.get(DrawItemName);
                            }

                            SetDisplayStandToShowGunName(DrawBoxDisplayStand.get(ChestIndex), DisplayName, hasKillCount);

                            RunTimes += 0.3;

                            SpawnLocation.getWorld().playSound(SpawnLocation, Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f, RunTimes);

                            Location PutLocation = SpawnLocation;

                            if (item != null) {

                                PutLocation = item.getLocation();

                                item.remove();

                            }

                            item = SpawnLocation.getWorld().dropItem(PutLocation, new ItemStack(DisplayType));

                            item.setGravity(false);

                            item.setPickupDelay(10000);

                            item.setVelocity(new Vector(0, 0, 0));
                        }
                    }, 0, 10));
                }   else {
                    e.getPlayer().sendMessage(ChatColor.RED + "金錢不足");
                }
            }
            e.setCancelled(true);
        }
    }

    //設定抽獎箱可拿取物品
    public static void SetDrawBoxItem(int Index) {
        CanTakeItemDrawBox.add(Index);
    }

    //拿取抽獎箱物品
    public static void getDrawBoxItem(Player p, Block ClickLocation) {
        if (ClickLocation != null && DrawBoxIndexFromLocation.containsKey(ClickLocation.getLocation())) {

            final int ChestIndex = DrawBoxIndexFromLocation.get(ClickLocation.getLocation());

            if (CanTakeItemDrawBox.contains(ChestIndex) && DrawItem.containsKey(ChestIndex)) {

                for (Player SendPlayer : CODZombie.Main.getServer().getOnlinePlayers()){
                    SendPlayer.sendMessage(ChatColor.GREEN + p.getName() + " 取得了: " + DrawItem.get(ChestIndex).getItemMeta().getDisplayName());
                }

                ExpendableItem.SetInteractCD(p, 0.5);
                DrawBoxTimes --;
                p.getInventory().addItem(DrawItem.get(ChestIndex));
                DrawItem.remove(ChestIndex);
                DrawBoxDisplayItem.get(ChestIndex).remove();
                DrawBoxDisplayItem.remove(ChestIndex);
                IgnoreDrawBox.remove((Object) ChestIndex);
                ResetArmorStand(DrawBoxDisplayStand.get(ChestIndex));
                if (DrawBoxTimes <= 0){
                    getRandomDrawBox();
                }

                CODZombie.Main.getServer().getScheduler().runTaskLater(CODZombie.Main, new Runnable() {
                    @Override
                    public void run() {
                        CanTakeItemDrawBox.remove((Object) ChestIndex);
                    }
                }, 20);
            }
        }
    }

    //取得隨機抽獎箱編號
    public static void getRandomDrawBox(){

        DrawBoxTimes = 6;

        for (Player p : CODZombie.Main.getServer().getOnlinePlayers()){
            p.sendTitle("", ChatColor.RED + "抽獎箱移動到其他地方了...", 10, 40, 10);
        }

        CanUseDrawBox.clear();

        final int RandomIndex = GunAPI.random.nextInt(DrawBoxIndex.size()) + 1;

        ResetArmorStand(DrawBoxDisplayStand.get(RandomIndex));

        CanUseDrawBox.add(RandomIndex);

        for (int Index : DrawBoxIndex.keySet()){
            if (RandomIndex != Index){
                DisableArmorStand(DrawBoxDisplayStand.get(Index));
            }
        }
    }

    //停止計時器
    public static void ClearTimer(int DrawBoxIndex) {
        CODZombie.Main.getServer().getScheduler().cancelTask(DrawBoxTimer.get(DrawBoxIndex));
        DrawBoxTimer.remove(DrawBoxIndex);
    }

}
