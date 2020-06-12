package codzombie.codzombie.WallSystem;

import codzombie.codzombie.CODZombie;
import codzombie.codzombie.CoinSystem.CoinCore;
import codzombie.codzombie.WaveSystem.WaveCore;
import com.google.common.collect.HashBasedTable;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class CoinWall {

    public static HashBasedTable<UUID, Location, Material> CoinWallLocationCatch = HashBasedTable.create();

    public static HashBasedTable<UUID, Location, Integer> CoinWallSpawnMonsterIndexCatch = HashBasedTable.create();

    public static HashBasedTable<Integer, Location, Material> CoinWallLocationData = HashBasedTable.create();

    public static HashMap<Location, Integer> IndexFromLocation = new HashMap<>();

    public static HashMap<Location, Integer> DetectLocation = new HashMap<>();

    public static HashMap<Integer, ArrayList<ArmorStand>> DisplayArmorStand = new HashMap<>();

    public static HashMap<Integer, Integer> CoinWallPrice = new HashMap<>();

    public static HashMap<UUID, ArrayList<Location>> DetectLocationCatch = new HashMap<>();

    public static HashMap<UUID, ArrayList<Location>> DisplayNameLocation = new HashMap<>();

    public static HashMap<UUID, Integer> RecordIndexTimes = new HashMap<>();

    public static HashMap<UUID, Location> ScenePlayerLocation = new HashMap<>();

    public static ArrayList<UUID> RecordCoinWallPlayer = new ArrayList<>();

    public static ArrayList<UUID> PutUnlockIndexPlayer = new ArrayList<>();

    public static ArrayList<UUID> PutDisplayNameStandPlayer = new ArrayList<>();

    public static ArrayList<Integer> hasUnlockIndex = new ArrayList<>();

    public static void EnableRecordMode(Player p){
        if (RecordCoinWallPlayer.contains(p.getUniqueId())){
            RecordCoinWallPlayer.remove(p.getUniqueId());
            p.sendMessage(ChatColor.RED + "已關閉付費牆壁紀錄模式");
        }
        else {
            RecordCoinWallPlayer.add(p.getUniqueId());
            p.sendMessage(ChatColor.GREEN + "已開啟付費牆壁紀錄模式");
        }
    }

    //設定付費牆壁座標
    public static void setCoinWallLocation(Player p, Location location, Material Type){
        if (RecordCoinWallPlayer.contains(p.getUniqueId())) {
            CoinWallLocationCatch.put(p.getUniqueId(), location, Type);

            p.sendMessage(ChatColor.GREEN + "付費牆壁紀錄成功");
        }
        if (PutUnlockIndexPlayer.contains(p.getUniqueId()) && Type == Material.PLAYER_HEAD){
            if (DetectLocationCatch.containsKey(p.getUniqueId())) DetectLocationCatch.get(p.getUniqueId()).add(location);
            else {
                ArrayList<Location> NewLocation = new ArrayList<>();
                NewLocation.add(location);
                DetectLocationCatch.put(p.getUniqueId(), NewLocation);
            }
            p.sendMessage(ChatColor.GREEN + "已記錄編號位置 請全部放好再輸入編號 無須任何前綴");
        }
    }

    //刪除付費牆壁座標
    public static void DeleteCoinWallLocation(Player p, Location location){
        if (RecordCoinWallPlayer.contains(p.getUniqueId()) && CoinWallLocationCatch.contains(p.getUniqueId(), location)) {

            CoinWallLocationCatch.remove(p.getUniqueId(), location);

            p.sendMessage(ChatColor.GREEN + "座標刪除成功");
        }
    }

    public static void EnableRecordIndexMode(Player p){
        if (!PutUnlockIndexPlayer.contains(p.getUniqueId())){
            PutUnlockIndexPlayer.add(p.getUniqueId());
            RecordIndexTimes.put(p.getUniqueId(), 2);
            EnableRecordMode(p);
        }
        p.sendMessage(ChatColor.GREEN + "已開啟設定編號模式 請放置玩家頭顱來進行設定");
    }

    public static void RecordMonsterSpawnLocationIndex(AsyncPlayerChatEvent e){
        if (DetectLocationCatch.containsKey(e.getPlayer().getUniqueId()) && DetectLocationCatch.get(e.getPlayer().getUniqueId()).size() > 0 && NumberUtils.isNumber(e.getMessage())){
            for (Location location : DetectLocationCatch.get(e.getPlayer().getUniqueId())){
                CoinWallSpawnMonsterIndexCatch.put(e.getPlayer().getUniqueId(), location, Integer.valueOf(e.getMessage()));
            }
            DetectLocationCatch.remove(e.getPlayer().getUniqueId());
            e.getPlayer().sendMessage(ChatColor.GREEN + "成功將放置的區域設定為編號: " + e.getMessage());
            RecordIndexTimes.put(e.getPlayer().getUniqueId(), RecordIndexTimes.get(e.getPlayer().getUniqueId()) - 1);
            if (RecordIndexTimes.get(e.getPlayer().getUniqueId()) <= 0){
                RecordIndexTimes.remove(e.getPlayer().getUniqueId());
                e.getPlayer().sendMessage(ChatColor.GREEN + "已放置兩個編號");
                EnableDisplayNameArmorStand(e.getPlayer());
            }
            e.setCancelled(true);
        }
        else if (PutUnlockIndexPlayer.contains(e.getPlayer().getUniqueId())){
            e.getPlayer().sendMessage(ChatColor.RED + "其他錯誤");
            e.setCancelled(true);
        }
    }

    public static void EnableDisplayNameArmorStand(Player p){
        PutUnlockIndexPlayer.remove(p.getUniqueId());
        PutDisplayNameStandPlayer.add(p.getUniqueId());
        p.sendMessage(ChatColor.GREEN + "進入顯示價格盔甲架設定位置 角色看到要放置的位置後輸入/DisplayNameStand");
    }

    public static void SetDisplayNameArmorStandLocation(Player p){
        RayTraceResult result = p.rayTraceBlocks(15);
        Vector SetPosition;
        if (PutDisplayNameStandPlayer.contains(p.getUniqueId()) && result != null){
            SetPosition = result.getHitPosition();
            if (DisplayNameLocation.containsKey(p.getUniqueId())) DisplayNameLocation.get(p.getUniqueId()).add(SetPosition.toLocation(p.getWorld()));

            else {
                ArrayList<Location> NewLocation = new ArrayList<>();
                NewLocation.add(SetPosition.toLocation(p.getWorld()));
                DisplayNameLocation.put(p.getUniqueId(), NewLocation);
            }
            p.sendMessage(ChatColor.GREEN + "成功紀錄");
        }
        else {
            p.sendMessage(ChatColor.RED + "位置無效");
        }
    }

    public static void SaveCoinWall(Player p, int Price){

        int Index;

        if (CODZombie.BuildingLocationConfig.contains("CoinWallIndex")){
            Index = CODZombie.BuildingLocationConfig.getInt("CoinWallIndex");
        }
        else {
            Index = 1;
        }

        int LocationIndex = 1;

        ArrayList<Object> WaitRemove = new ArrayList<>();

        for (Location location : CoinWallLocationCatch.row(p.getUniqueId()).keySet()){
            CODZombie.BuildingLocationConfig.set("CoinWallList.CoinWall " + Index + ".Location " + LocationIndex + ".X", location.getX());
            CODZombie.BuildingLocationConfig.set("CoinWallList.CoinWall " + Index + ".Location " + LocationIndex + ".Y", location.getY());
            CODZombie.BuildingLocationConfig.set("CoinWallList.CoinWall " + Index + ".Location " + LocationIndex + ".Z", location.getZ());
            CODZombie.BuildingLocationConfig.set("CoinWallList.CoinWall " + Index + ".Location " + LocationIndex + ".Type", CoinWallLocationCatch.get(p.getUniqueId(), location).toString());
            LocationIndex++;
            WaitRemove.add(location);
        }

        for (Object location : WaitRemove){
            CoinWallLocationCatch.remove(p.getUniqueId(), location);
        }

        WaitRemove.clear();

        int DisplayLocationIndex = 1;

        for (Location location : DisplayNameLocation.get(p.getUniqueId())){
            CODZombie.BuildingLocationConfig.set("CoinWallList.CoinWall " + Index + ".DisplayLocation " + DisplayLocationIndex + ".X", location.getX());
            CODZombie.BuildingLocationConfig.set("CoinWallList.CoinWall " + Index + ".DisplayLocation " + DisplayLocationIndex + ".Y", location.getY());
            CODZombie.BuildingLocationConfig.set("CoinWallList.CoinWall " + Index + ".DisplayLocation " + DisplayLocationIndex + ".Z", location.getZ());
            DisplayLocationIndex++;
            WaitRemove.add(location);
        }

        for (Object location : WaitRemove){
            DisplayNameLocation.remove(p.getUniqueId(), location);
        }

        WaitRemove.clear();

        int DetectLocationIndex = 1;

        for (Location location : CoinWallSpawnMonsterIndexCatch.row(p.getUniqueId()).keySet()){
            CODZombie.BuildingLocationConfig.set("CoinWallList.CoinWall " + Index + ".DetectLocation " + DetectLocationIndex + ".X", location.getX());
            CODZombie.BuildingLocationConfig.set("CoinWallList.CoinWall " + Index + ".DetectLocation " + DetectLocationIndex + ".Y", location.getY());
            CODZombie.BuildingLocationConfig.set("CoinWallList.CoinWall " + Index + ".DetectLocation " + DetectLocationIndex + ".Z", location.getZ());
            CODZombie.BuildingLocationConfig.set("CoinWallList.CoinWall " + Index + ".DetectLocation " + DetectLocationIndex + ".UnlockAreaIndex", CoinWallSpawnMonsterIndexCatch.get(p.getUniqueId(), location));
            DetectLocationIndex++;
            WaitRemove.add(location);
        }

        for (Object location : WaitRemove){
            CoinWallSpawnMonsterIndexCatch.remove(p.getUniqueId(), location);
        }

        WaitRemove.clear();

        CODZombie.BuildingLocationConfig.set("CoinWallList.CoinWall " + Index + ".Data.Price", Price);

        Index++;

        CODZombie.BuildingLocationConfig.set("CoinWallIndex", Index);

        p.sendMessage(ChatColor.GREEN + "儲存成功");

        try {
            CODZombie.BuildingLocationConfig.save(CODZombie.BuildingLocationFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void LoadCoinWallData(){

        if (CODZombie.BuildingLocationConfig.contains("CoinWallList")) {

            DetectLocation.clear();

            CoinWallLocationData.clear();

            IndexFromLocation.clear();

            int CoinWallIndex = 1;

            for (String CoinWallName : CODZombie.BuildingLocationConfig.getConfigurationSection("CoinWallList").getKeys(false)) {

                for (String LocationIndex : CODZombie.BuildingLocationConfig.getConfigurationSection("CoinWallList." + CoinWallName).getKeys(false)) {

                    double X = 0, Y = 0, Z = 0;

                    Material Type = null;

                    int UnlockAreaIndex = 0;

                    int Price = 0;

                    for (String Axis : CODZombie.BuildingLocationConfig.getConfigurationSection("CoinWallList." + CoinWallName + "." + LocationIndex).getKeys(false)) {

                        switch (Axis) {
                            case "X":
                                X = CODZombie.BuildingLocationConfig.getDouble("CoinWallList." + CoinWallName + "." + LocationIndex + "." + Axis);
                                break;
                            case "Y":
                                Y = CODZombie.BuildingLocationConfig.getDouble("CoinWallList." + CoinWallName + "." + LocationIndex + "." + Axis);
                                break;
                            case "Z":
                                Z = CODZombie.BuildingLocationConfig.getDouble("CoinWallList." + CoinWallName + "." + LocationIndex + "." + Axis);
                                break;
                            case "Type":
                                Type = Material.valueOf(CODZombie.BuildingLocationConfig.getString("CoinWallList." + CoinWallName + "." + LocationIndex + "." + Axis));
                                break;
                            case "UnlockAreaIndex":
                                UnlockAreaIndex = CODZombie.BuildingLocationConfig.getInt("CoinWallList." + CoinWallName + "." + LocationIndex + "." + Axis);
                                break;
                            case "Price":
                                Price = CODZombie.BuildingLocationConfig.getInt("CoinWallList." + CoinWallName + "." + LocationIndex + "." + Axis);
                                break;
                        }
                    }

                    Location PutLocation = new Location(CODZombie.Main.getServer().getWorld("World"), X, Y, Z);

                    if (LocationIndex.contains("Detect")) {

                        DetectLocation.put(PutLocation, UnlockAreaIndex);

                        IndexFromLocation.put(PutLocation, CoinWallIndex);
                    } else if (LocationIndex.contains("DisplayLocation")) {

                        SpawnArmorStand(PutLocation, CoinWallIndex);

                        IndexFromLocation.put(PutLocation, CoinWallIndex);
                    } else if (LocationIndex.contains("Data")) {

                        CoinWallPrice.put(CoinWallIndex, Price);
                    } else {

                        CoinWallLocationData.put(CoinWallIndex, PutLocation, Type);

                        IndexFromLocation.put(PutLocation, CoinWallIndex);
                    }
                }

                CoinWallIndex++;
            }
        }
    }

    public static void SpawnArmorStand(Location location, int Index){

        CODZombie.Main.getServer().getScheduler().runTaskLater(CODZombie.Main, new Runnable() {
            @Override
            public void run() {

                ArmorStand Stand = null;

                for (Entity Ent : location.getWorld().getNearbyEntities(location, 1, 1, 1)){
                    if (Ent instanceof ArmorStand){
                        Stand = (ArmorStand) Ent;
                    }
                }

                if (Stand == null) {

                    Stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

                }

                Stand.setCustomNameVisible(true);

                Stand.setCustomName(ChatColor.YELLOW + "解鎖花費: " + CoinWallPrice.get(Index));

                Stand.setVisible(false);

                Stand.setInvulnerable(true);

                if (DisplayArmorStand.containsKey(Index)){
                    DisplayArmorStand.get(Index).add(Stand);
                }
                else {
                    ArrayList<ArmorStand> NewStand = new ArrayList<>();
                    NewStand.add(Stand);
                    DisplayArmorStand.put(Index, NewStand);
                }
            }
        }, 20);
    }

    public static void HideArmorStand(ArmorStand Stand){
        Stand.setCustomNameVisible(false);
    }

    public static void RunDetectLocation(){
        for (Location location : DetectLocation.keySet()){

            if (hasUnlockIndex.contains(IndexFromLocation.get(location))) continue;

            for (Player p : CODZombie.Main.getServer().getOnlinePlayers()) {
                if (!ScenePlayerLocation.containsKey(p.getUniqueId()) && location.distance(p.getLocation()) < 1){
                    ScenePlayerLocation.put(p.getUniqueId(), location);
                    p.sendTitle(ChatColor.WHITE + "右鍵解鎖", "", 10, 20, 10);
                }
                if (ScenePlayerLocation.containsKey(p.getUniqueId()) && ScenePlayerLocation.get(p.getUniqueId()).distance(p.getLocation()) > 2) {
                    ScenePlayerLocation.remove(p.getUniqueId());
                }
            }
        }
    }

    public static void UnlockArea(Player p){
        if (ScenePlayerLocation.containsKey(p.getUniqueId())){

            if (CoinCore.CheckHasCoin(p, CoinWallPrice.get(IndexFromLocation.get(ScenePlayerLocation.get(p.getUniqueId()))))) {

                final int CoinWallIndex = IndexFromLocation.get(ScenePlayerLocation.get(p.getUniqueId()));

                final int UnlockSpawnIndex = DetectLocation.get(ScenePlayerLocation.get(p.getUniqueId()));

                CoinCore.MinesPlayerCoin(p, CoinWallPrice.get(CoinWallIndex));

                WallCore.UnlockMonsterSpawnLocation(UnlockSpawnIndex);

                ClearCoinWall(CoinWallIndex);

                hasUnlockIndex.add(CoinWallIndex);

                ScenePlayerLocation.remove(p.getUniqueId());

                for (ArmorStand Stand : DisplayArmorStand.get(CoinWallIndex)) {
                    HideArmorStand(Stand);
                }
            }
            else {

                p.sendMessage(ChatColor.RED + "金錢不足");
            }
        }
    }

    public static void ClearCoinWall(int Index){
        for (Location location : CoinWallLocationData.row(Index).keySet()){
            location.getBlock().setType(Material.AIR);
        }
        for (Player p : CODZombie.Main.getServer().getOnlinePlayers()){
            p.sendTitle(ChatColor.GREEN + "解鎖新區域", "", 10, 40, 10);
        }
    }

    public static void ResetAllWall(){
        for (int Index : CoinWallLocationData.rowKeySet()){
            ResetCoinWall(Index);
        }
    }

    public static void ResetCoinWall(int Index){
        for (Location location : CoinWallLocationData.row(Index).keySet()){
            location.getBlock().setType(CoinWallLocationData.get(Index, location));
        }
    }

}
