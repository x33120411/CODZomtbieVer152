package codzombie.codzombie.WallSystem;

import codzombie.codzombie.CODZombie;
import codzombie.codzombie.CoinSystem.CoinCore;
import codzombie.codzombie.GunSystem.GunAPI;
import codzombie.codzombie.WaveSystem.WaveCore;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class WallCore {

    public static ArrayList<Location> AllWallLocation = new ArrayList<>();

    public static ArrayList<Player> RepairWindowPlayer = new ArrayList<>();

    public static ArrayList<Location> CanUseSpawnLocation = new ArrayList<>();

    public static ArrayList<UUID> RecordModePlayer = new ArrayList<>();

    public static HashMap<Location, Integer> SpawnLocationIndex = new HashMap<>();

    public static HashMap<Player, Location> LastTimeNearLocation = new HashMap<>();

    public static HashMap<Player, Integer> TimerReCord = new HashMap<>();

    public static HashMap<UUID, Location> MonsterSceneLocation = new HashMap<>();

    public static HashMap<UUID, Integer> MonsterDestroyWindowTimerID = new HashMap<>();


    public static HashMap<UUID, Location> RecordIndexPlayer = new HashMap<>();

    //取得所有窗戶座標
    public static void getAllWindowLocation() {

        if (!CODZombie.BuildingLocationConfig.contains("WindowLocationList")) {
            return;
        }

        for (String Key : CODZombie.BuildingLocationConfig.getConfigurationSection("WindowLocationList").getKeys(false)) {

            double X = 0, Y = 0, Z = 0;

            for (String Axis : CODZombie.BuildingLocationConfig.getConfigurationSection("WindowLocationList." + Key).getKeys(false)) {
                switch (Axis) {
                    case "X":
                        X = CODZombie.BuildingLocationConfig.getDouble("WindowLocationList." + Key + "." + Axis);
                        break;
                    case "Y":
                        Y = CODZombie.BuildingLocationConfig.getDouble("WindowLocationList." + Key + "." + Axis);
                        break;
                    case "Z":
                        Z = CODZombie.BuildingLocationConfig.getDouble("WindowLocationList." + Key + "." + Axis);
                        break;
                }
            }
            final Location CurrentGetLocation = new Location(CODZombie.Main.getServer().getWorld("World"), X, Y, Z);
            AllWallLocation.add(CurrentGetLocation);
        }
    }

    //取得所有怪物出生點座標
    public static void getAllSpawnLocation() {

        if (!CODZombie.BuildingLocationConfig.contains("SpawnLocationList")) {
            return;
        }

        for (String Key : CODZombie.BuildingLocationConfig.getConfigurationSection("SpawnLocationList").getKeys(false)) {

            double X = 0, Y = 0, Z = 0;

            int Index = 0;

            for (String Axis : CODZombie.BuildingLocationConfig.getConfigurationSection("SpawnLocationList." + Key).getKeys(false)) {
                switch (Axis) {
                    case "X":
                        X = CODZombie.BuildingLocationConfig.getDouble("SpawnLocationList." + Key + "." + Axis);
                        break;
                    case "Y":
                        Y = CODZombie.BuildingLocationConfig.getDouble("SpawnLocationList." + Key + "." + Axis);
                        break;
                    case "Z":
                        Z = CODZombie.BuildingLocationConfig.getDouble("SpawnLocationList." + Key + "." + Axis);
                        break;
                    case "Index":
                        Index = CODZombie.BuildingLocationConfig.getInt("SpawnLocationList." + Key + "." + Axis);
                        break;
                }
            }
            final Location CurrentGetLocation = new Location(CODZombie.Main.getServer().getWorld("World"), X, Y, Z);
            SpawnLocationIndex.put(CurrentGetLocation, Index);
        }
    }

    //載入預設編號位置
    public static void getDefaultIndexLocation(){

        final int Index = CODZombie.Main.getConfig().getInt("DefaultSpawnIndex");

        CanUseSpawnLocation.clear();

        for (Location location : SpawnLocationIndex.keySet()){
            if (SpawnLocationIndex.get(location) == Index){
                CanUseSpawnLocation.add(location);
            }
        }
    }

    //從編號取得所有此編號的生成怪物位置
    public static ArrayList<Location> getSpawnLocationFromIndex(int Index){
        ArrayList<Location> FindLocation = new ArrayList<>();
        for (Location location : SpawnLocationIndex.keySet()){
            if (SpawnLocationIndex.get(location) == Index){
                FindLocation.add(location);
            }
        }
        return FindLocation;
    }

    //解鎖怪物生成位置
    public static void UnlockMonsterSpawnLocation(int Index){
        for (Location location : getSpawnLocationFromIndex(Index)){
            if (!CanUseSpawnLocation.contains(location)){
                CanUseSpawnLocation.add(location);
            }
        }
    }

    //紀錄窗戶座標
    public static void setWindowLocation(Player p, Location location, Material Type) {

        if (RecordModePlayer.contains(p.getUniqueId()) && Type != Material.ZOMBIE_HEAD && Type == Material.OAK_SLAB) {

            int Index;

            if (CODZombie.BuildingLocationConfig.contains("WindowLocationIndex")) {
                Index = CODZombie.BuildingLocationConfig.getInt("WindowLocationIndex");
            } else {
                Index = 0;
            }

            Index++;

            CODZombie.BuildingLocationConfig.set("WindowLocationIndex", Index);
            CODZombie.BuildingLocationConfig.set("WindowLocationList.WindowLocation " + Index + ".X", location.getX());
            CODZombie.BuildingLocationConfig.set("WindowLocationList.WindowLocation " + Index + ".Y", location.getY());
            CODZombie.BuildingLocationConfig.set("WindowLocationList.WindowLocation " + Index + ".Z", location.getZ());

            p.sendMessage(ChatColor.GREEN + "窗戶座標紀錄成功");
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.7f, 1.0f);

            try {
                CODZombie.BuildingLocationConfig.save(CODZombie.BuildingLocationFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //紀錄怪物出生點
    public static void setMonsterSpawnLocation(Player p, Location location, Material Type) {

        if (RecordModePlayer.contains(p.getUniqueId()) && Type == Material.ZOMBIE_HEAD) {
            if (!RecordIndexPlayer.containsKey(p.getUniqueId())) {

                RecordIndexPlayer.put(p.getUniqueId(), location);

                p.sendMessage(ChatColor.GREEN + "請輸入區域編號至聊天列 無須任何前綴");
            }
            else {
                p.sendMessage(ChatColor.RED + "尚未輸入先前位置的編號 請先輸入再放置");
            }
        }
    }

    //輸入怪物生成編號
    public static void setMonsterSpawnIndex(AsyncPlayerChatEvent e){
        if (RecordIndexPlayer.containsKey(e.getPlayer().getUniqueId())) {
            if (NumberUtils.isNumber(e.getMessage())) {
                int SettingIndex;

                if (CODZombie.BuildingLocationConfig.contains("SpawnLocationIndex")) {
                    SettingIndex = CODZombie.BuildingLocationConfig.getInt("SpawnLocationIndex");
                } else {
                    SettingIndex = 0;
                }

                SettingIndex++;

                CODZombie.BuildingLocationConfig.set("SpawnLocationIndex", SettingIndex);
                CODZombie.BuildingLocationConfig.set("SpawnLocationList.SpawnLocation " + SettingIndex + ".X", RecordIndexPlayer.get(e.getPlayer().getUniqueId()).getX());
                CODZombie.BuildingLocationConfig.set("SpawnLocationList.SpawnLocation " + SettingIndex + ".Y", RecordIndexPlayer.get(e.getPlayer().getUniqueId()).getY());
                CODZombie.BuildingLocationConfig.set("SpawnLocationList.SpawnLocation " + SettingIndex + ".Z", RecordIndexPlayer.get(e.getPlayer().getUniqueId()).getZ());
                CODZombie.BuildingLocationConfig.set("SpawnLocationList.SpawnLocation " + SettingIndex + ".Index", Integer.valueOf(e.getMessage()));

                e.getPlayer().sendMessage(ChatColor.GREEN + "成功將 X: " + RecordIndexPlayer.get(e.getPlayer().getUniqueId()).getX() + ", Y: "
                        + RecordIndexPlayer.get(e.getPlayer().getUniqueId()).getY() + ", Z: " + RecordIndexPlayer.get(e.getPlayer().getUniqueId()).getZ() + " 設定為編號: " + Integer.valueOf(e.getMessage()));

                RecordIndexPlayer.remove(e.getPlayer().getUniqueId());

                try {
                    CODZombie.BuildingLocationConfig.save(CODZombie.BuildingLocationFile);
                } catch (IOException i) {
                    i.printStackTrace();
                }

            }
            else {
                e.getPlayer().sendMessage(ChatColor.RED + "你輸入的不是數字 請再輸入一次");
            }
            e.setCancelled(true);
        }
    }

    public static void RepairAllWall(){
        for (Location location : AllWallLocation){
            if (location.getBlock().getType() == Material.AIR){
                location.getBlock().setType(Material.OAK_SLAB);
            }
        }
    }

    public static void RecordMode(Player p){
        if (RecordModePlayer.contains(p.getUniqueId())){
            RecordModePlayer.remove(p.getUniqueId());
            p.sendMessage(ChatColor.GREEN + "已關閉紀錄模式");
        }
        else {
            RecordModePlayer.add(p.getUniqueId());
            p.sendMessage(ChatColor.GREEN + "已開啟紀錄模式");
        }
    }

    //清除窗戶座標
    public static void DeleteWindowLocation(Player p, Location location) {

        if (RecordModePlayer.contains(p.getUniqueId()) && p.getInventory().getItemInMainHand().getType() == Material.OAK_SLAB && CODZombie.BuildingLocationConfig.contains("WindowLocationList")) {

            for (String Key : CODZombie.BuildingLocationConfig.getConfigurationSection("WindowLocationList").getKeys(false)) {
                double X = 0, Y = 0, Z = 0;
                ArrayList<String> AllPath = new ArrayList<>();
                for (String Axis : CODZombie.BuildingLocationConfig.getConfigurationSection("WindowLocationList." + Key).getKeys(false)) {
                    switch (Axis) {
                        case "X":
                            X = CODZombie.BuildingLocationConfig.getDouble("WindowLocationList." + Key + "." + Axis);
                            break;
                        case "Y":
                            Y = CODZombie.BuildingLocationConfig.getDouble("WindowLocationList." + Key + "." + Axis);
                            break;
                        case "Z":
                            Z = CODZombie.BuildingLocationConfig.getDouble("WindowLocationList." + Key + "." + Axis);
                            break;
                    }

                    AllPath.add("WindowLocationList." + Key + "." + Axis);
                }

                if (location.getX() == X && location.getY() == Y && location.getZ() == Z) {
                    AllWallLocation.remove(location);
                    for (String Path : AllPath) {
                        CODZombie.BuildingLocationConfig.set(Path, null);
                    }
                    break;
                }
            }

            p.sendMessage(ChatColor.GREEN + "刪除成功");

            try {
                CODZombie.BuildingLocationConfig.save(CODZombie.BuildingLocationFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //檢測窗戶附近玩家
    public static void SceneWindowNearPlayer() {
        for (Player p : CODZombie.Main.getServer().getOnlinePlayers()) {
            for (Location location : AllWallLocation) {

                boolean hasNearMonster = false;

                for (Entity Ent : p.getNearbyEntities(3, 3, 3)){
                    if (Ent instanceof Monster){
                        hasNearMonster = true;
                        break;
                    }
                }

                if (p.getLocation().distance(location) < 4 && location.getBlock().getType() == Material.AIR) {

                    if (!RepairWindowPlayer.contains(p) && !hasNearMonster) {
                        p.sendTitle("", ChatColor.GREEN + "正在修復窗戶", 0, 20, 10);
                        RepairWindowPlayer.add(p);
                        LastTimeNearLocation.put(p, location);
                        sendRepairTask(p);
                        break;
                    }

                }

                if (RepairWindowPlayer.contains(p) && hasNearMonster){
                    RepairWindowPlayer.remove(p);
                    LastTimeNearLocation.remove(p);
                    ClearRepairTask(p);
                    p.sendTitle("", ChatColor.RED + "修復牆壁中斷", 0, 20, 0);
                }

                if (LastTimeNearLocation.containsKey(p)) {

                    if (LastTimeNearLocation.get(p).distance(p.getLocation()) >= 3.5) {
                        RepairWindowPlayer.remove(p);
                        LastTimeNearLocation.remove(p);
                        ClearRepairTask(p);
                    }

                }
            }
        }
    }

    //檢測窗戶附近怪物
    public static void SceneWindowNearMonster() {
        for (Location WindowLocation : AllWallLocation) {
            if (WindowLocation.getBlock().getType() == Material.AIR){
                continue;
            }
            List<Entity> NearEnt = (List<Entity>) WindowLocation.getWorld().getNearbyEntities(WindowLocation, 1.5, 1.5, 1.5);
            for (Entity Ent : NearEnt) {
                if (MonsterDestroyWindowTimerID.containsKey(Ent.getUniqueId())) {
                    if (MonsterSceneLocation.get(Ent.getUniqueId()).distance(Ent.getLocation()) > 3) {
                        CancelDestroyWindow(Ent.getUniqueId());
                        ((Monster) Ent).setAI(true);
                    }
                }
                if (WaveCore.AllMonsterUID.contains(Ent.getUniqueId()) && !MonsterDestroyWindowTimerID.containsKey(Ent.getUniqueId())) {
                    ((Monster) Ent).setAI(false);

                    MonsterSceneLocation.put(Ent.getUniqueId(), WindowLocation);

                    final ArrayList<Location> CanSceneLocation = new ArrayList<>();

                    MonsterDestroyWindowTimerID.put(Ent.getUniqueId(), CODZombie.Main.getServer().getScheduler().scheduleSyncRepeatingTask(CODZombie.Main, new Runnable() {
                        @Override
                        public void run() {

                            for (Location WallLoc : AllWallLocation) {
                                if (Ent.getLocation().distance(WallLoc) <= 5 && WallLoc.getBlock().getType() != Material.AIR){
                                    CanSceneLocation.add(WallLoc);
                                }
                            }

                            Location DestroyLocation = null;

                            if (CanSceneLocation.size() > 0) DestroyLocation = CanSceneLocation.get(GunAPI.random.nextInt(CanSceneLocation.size()));

                            else {
                                ((Monster) Ent).setAI(true);
                                CancelDestroyWindow(Ent.getUniqueId());
                                return;
                            }

                            DestroyLocation.getBlock().setType(Material.AIR);

                            DestroyLocation.getWorld().playSound(DestroyLocation, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1.0f, 1.0f);

                            if (Ent.isDead() || CanSceneLocation.size() <= 0){
                                ((Monster) Ent).setAI(true);
                                CancelDestroyWindow(Ent.getUniqueId());
                                return;
                            }

                            if (Ent instanceof Monster) ((Monster) Ent).setTarget(WaveCore.getNearPlayer(Ent));

                            CanSceneLocation.clear();
                        }
                    }, (long) (20 * CODZombie.Main.getConfig().getDouble("DestroyWindowTime")), (long) (20 * CODZombie.Main.getConfig().getDouble("DestroyWindowTime"))));
                }
            }
        }
    }

    //怪物死亡停止拆除窗戶
    public static void CancelDeathEntityDestroyWindow(UUID UID){
        if (MonsterDestroyWindowTimerID.containsKey(UID)){
            CancelDestroyWindow(UID);
        }
    }

    //取消怪物拆除窗戶
    public static void CancelDestroyWindow(UUID UID) {
        CODZombie.Main.getServer().getScheduler().cancelTask(MonsterDestroyWindowTimerID.get(UID));
        MonsterDestroyWindowTimerID.remove(UID);
        MonsterSceneLocation.remove(UID);
    }

    //傳送維修需求
    public static void sendRepairTask(Player p) {
        if (!TimerReCord.containsKey(p)) {
            TimerReCord.put(p, CODZombie.Main.getServer().getScheduler().scheduleSyncRepeatingTask(CODZombie.Main, new Runnable() {
                @Override
                public void run() {
                    final Location RepairLocation = FindRepairLocation(p);

                    if (RepairLocation != null) {
                        RepairWindow(p, RepairLocation);
                    } else {
                        ClearRepairTask(p);
                        p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 0.2f);
                    }
                }
            }, 20, 20));
        }
    }

    //清除計時器
    public static void ClearRepairTask(Player p) {
        if (TimerReCord.containsKey(p)) {
            CODZombie.Main.getServer().getScheduler().cancelTask(TimerReCord.get(p));
            TimerReCord.remove(p);
        }
    }

    //找出需要維修的地點
    public static Location FindRepairLocation(Player p) {
        ArrayList<Location> NearPlayerLocation = new ArrayList<>();
        for (Location location : AllWallLocation) {
            if (LastTimeNearLocation.containsKey(p) && location.distance(LastTimeNearLocation.get(p)) <= 5.0) {
                NearPlayerLocation.add(location);
            }
        }

        for (Location location : NearPlayerLocation) {
            if (location.getBlock().getType() == Material.AIR) {
                return location;
            }
        }
        return null;
    }

    //修復窗戶
    public static void RepairWindow(Player p, Location RepairLocation) {

        final int Coin = 10;

        RepairLocation.getBlock().setType(Material.OAK_SLAB);

        RepairLocation.getWorld().playSound(RepairLocation, Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1.0f, 1.0f);

        CoinCore.AddPlayerCoin(p, Coin);

        p.sendMessage(ChatColor.DARK_GREEN + "+" + Coin + "金錢");
    }

}
