package codzombie.codzombie.WaveSystem;

import codzombie.codzombie.ArmorSystem.ArmorSystem;
import codzombie.codzombie.CODZombie;
import codzombie.codzombie.CoinSystem.CoinCore;
import codzombie.codzombie.DrawBox.DrawBox;
import codzombie.codzombie.Expendables.Turret;
import codzombie.codzombie.GunSystem.Gun;
import codzombie.codzombie.GunSystem.GunAPI;
import codzombie.codzombie.GunSystem.GunItem;
import codzombie.codzombie.Store.StoreCore;
import codzombie.codzombie.WallSystem.CoinWall;
import codzombie.codzombie.WallSystem.WallCore;
import com.google.common.collect.HashBasedTable;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class WaveCore {

    public static HashBasedTable<Integer, String, Integer> WaveMonsterData = HashBasedTable.create();

    public static HashMap<UUID, Location> MobLocation = new HashMap<>();

    public static HashMap<UUID, Integer> SpecterModeIndex = new HashMap<>();

    private static HashMap<UUID, ArmorStand> PlayerTomb = new HashMap<>();

    public static ArrayList<UUID> AllMonsterUID = new ArrayList<>();

    public static ArrayList<String> CanSpawnMonsterName = new ArrayList<>();

    public static ArrayList<String> RandomDeathText = new ArrayList<>();

    public static ArrayList<UUID> DeathPlayerUID = new ArrayList<>();

    public static ArrayList<UUID> RevivePlayer = new ArrayList<>();

    private static int CurrentWaveIndex = 1;

    private static int TotalMonsterNum = 0;

    public static boolean isGameStart = false;

    public static boolean isShowGameOver = false;

    public static boolean CanSpawnMonster = true;

    public static boolean isUnlimitedMode = false;

    public static void SetDefaultData() {
        RandomDeathText.add(ChatColor.YELLOW + "醫護兵!!");
        RandomDeathText.add(ChatColor.YELLOW + "我他媽倒地啦!!");
        RandomDeathText.add(ChatColor.YELLOW + "臥操~ 我血流滿地阿!!");
        RandomDeathText.add(ChatColor.YELLOW + "操! 我受傷了!");
        RandomDeathText.add(ChatColor.YELLOW + "MB的誰打中我啊!!");
        RandomDeathText.add(ChatColor.YELLOW + "老子被TM的打中啦!!");
        RandomDeathText.add(ChatColor.YELLOW + "受傷啊!!");
        RandomDeathText.add(ChatColor.YELLOW + "醫療兵 NMB的快來救我!!");
    }

    public static void LoadWaveMonsterData() {

        int WaveIndex = 0;

        if (!isUnlimitedMode) {
            for (String WaveNumber : CODZombie.WaveSettingConfig.getConfigurationSection("WaveList").getKeys(false)) {

                WaveIndex++;

                for (String MonsterName : CODZombie.WaveSettingConfig.getConfigurationSection("WaveList." + WaveNumber).getKeys(false)) {
                    WaveMonsterData.put(WaveIndex, MonsterName, CODZombie.WaveSettingConfig.getInt("WaveList." + WaveNumber + "." + MonsterName + ".Amount"));
                }
            }
        } else {

            final int MinMonster = CODZombie.Main.getConfig().getInt("UnlimitedModMinMonster");

            final int MaxMonster = CODZombie.Main.getConfig().getInt("UnlimitedModMaxMonster");

            final int MonsterNumMultiply = CODZombie.Main.getConfig().getInt("UnlimitedModMonsterNumMultiply");

            for (int Wave = 0; Wave < 200; Wave++) {

                WaveIndex++;

                int SpawnMonsterNum = MinMonster * WaveIndex * MonsterNumMultiply;

                if (SpawnMonsterNum > MaxMonster) {
                    SpawnMonsterNum = MaxMonster;
                }

                String LastTimeMonsterName = null;

                int CurrentNumber = 0;

                boolean isCompleteWave = true;

                do {
                    for (String MonsterName : MonsterData.MonsterData.rowKeySet()) {

                        final int SpawnWave = CODZombie.MonsterSettingConfig.getInt("MonsterDataList." + MonsterName + ".UnlimitedModSpawnWave");

                        final int UnSpawnWave = CODZombie.MonsterSettingConfig.getInt("MonsterDataList." + MonsterName + ".UnlimitedModUnSpawnWave");

                        if (WaveIndex >= SpawnWave && WaveIndex <= UnSpawnWave) {

                            if (CurrentNumber >= SpawnMonsterNum) {
                                isCompleteWave = false;
                                break;
                            }

                            if (GunAPI.random.nextDouble() < 0.5) {
                                if (WaveMonsterData.contains(WaveIndex, MonsterName)) {
                                    WaveMonsterData.put(WaveIndex, MonsterName, WaveMonsterData.get(WaveIndex, MonsterName) + 1);
                                } else {
                                    WaveMonsterData.put(WaveIndex, MonsterName, 1);
                                }
                            } else if (LastTimeMonsterName != null) {
                                if (WaveMonsterData.contains(WaveIndex, LastTimeMonsterName)) {
                                    WaveMonsterData.put(WaveIndex, LastTimeMonsterName, WaveMonsterData.get(WaveIndex, LastTimeMonsterName) + 1);
                                } else {
                                    WaveMonsterData.put(WaveIndex, LastTimeMonsterName, 1);
                                }
                            } else {
                                if (WaveMonsterData.contains(WaveIndex, MonsterName)) {
                                    WaveMonsterData.put(WaveIndex, MonsterName, WaveMonsterData.get(WaveIndex, MonsterName) + 1);
                                } else {
                                    WaveMonsterData.put(WaveIndex, MonsterName, 1);
                                }
                            }

                            CurrentNumber++;

                            LastTimeMonsterName = MonsterName;
                        }
                    }
                } while (isCompleteWave);
            }
        }
    }

    public static void OpenUnlimitedMod() {
        String ShowText;
        if (isUnlimitedMode) {
            isUnlimitedMode = false;
            ShowText = ChatColor.RED + "已關閉無盡模式";
        } else {
            isUnlimitedMode = true;
            ShowText = ChatColor.GREEN + "已開啟無盡模式";
        }
        for (Player p : CODZombie.Main.getServer().getOnlinePlayers()) {
            p.sendTitle(ShowText, "", 10, 40, 10);
            p.sendMessage(ShowText);
        }
        ResetGame();
    }

    public static void LoadCurrentWave() {
        int TotalNum = 0;

        for (String MonsterName : WaveMonsterData.row(CurrentWaveIndex).keySet()) {
            CanSpawnMonsterName.add(MonsterName);
            TotalNum += WaveMonsterData.get(CurrentWaveIndex, MonsterName);
        }

        TotalMonsterNum = TotalNum;
    }

    public static void SpawnMonster() {
        if (isGameStart && CanSpawnMonster && CanSpawnMonsterName.size() > 0 && WallCore.CanUseSpawnLocation.size() > 0) {

            CanSpawnMonster = false;

            final String SpawnMonsterName = CanSpawnMonsterName.get(GunAPI.random.nextInt(CanSpawnMonsterName.size()));

            final double SummonTime = GunAPI.random.nextDouble() * MonsterData.getMonsterData(SpawnMonsterName, MonsterDataType.SummonTime);

            final double Health = MonsterData.getMonsterData(SpawnMonsterName, MonsterDataType.Health);

            final double Damage = MonsterData.getMonsterData(SpawnMonsterName, MonsterDataType.Damage);

            final double KnockBackResistance = MonsterData.getMonsterData(SpawnMonsterName, MonsterDataType.KnockBackResistance);

            final double MovementSpeed = MonsterData.getMonsterData(SpawnMonsterName, MonsterDataType.MovementSpeed);

            final double Armor = MonsterData.getMonsterData(SpawnMonsterName, MonsterDataType.Armor);

            final Location SpawnLocation;

            if (WaveMonsterData.get(CurrentWaveIndex, SpawnMonsterName) <= 0) {
                CanSpawnMonsterName.remove(SpawnMonsterName);
                CanSpawnMonster = true;
                return;
            }

            SpawnLocation = WallCore.CanUseSpawnLocation.get(GunAPI.random.nextInt(WallCore.CanUseSpawnLocation.size()));

            final EntityType Type = MonsterData.getMonsterType(SpawnMonsterName);

            CODZombie.Main.getServer().getScheduler().runTaskLater(CODZombie.Main, new Runnable() {
                @Override
                public void run() {
                    Monster Mob = (Monster) SpawnLocation.getWorld().spawnEntity(SpawnLocation, Type);
                    Mob.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(Armor);
                    Mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(Damage);
                    Mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(MovementSpeed);
                    Mob.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(KnockBackResistance);
                    Mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(Health);
                    Mob.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(100);

                    if (MonsterData.hasMonsterEquipment(SpawnMonsterName, MonsterDataType.Helmet))
                        Mob.getEquipment().setHelmet(new ItemStack(MonsterData.getMonsterEquipment(SpawnMonsterName, MonsterDataType.Helmet)));
                    if (MonsterData.hasMonsterEquipment(SpawnMonsterName, MonsterDataType.Chestplate))
                        Mob.getEquipment().setChestplate(new ItemStack(MonsterData.getMonsterEquipment(SpawnMonsterName, MonsterDataType.Chestplate)));
                    if (MonsterData.hasMonsterEquipment(SpawnMonsterName, MonsterDataType.Leg))
                        Mob.getEquipment().setLeggings(new ItemStack(MonsterData.getMonsterEquipment(SpawnMonsterName, MonsterDataType.Leg)));
                    if (MonsterData.hasMonsterEquipment(SpawnMonsterName, MonsterDataType.Boot))
                        Mob.getEquipment().setBoots(new ItemStack(MonsterData.getMonsterEquipment(SpawnMonsterName, MonsterDataType.Boot)));

                    registerMonster(Mob.getUniqueId());
                    Mob.setTarget(getNearPlayer(Mob));
                    WaveMonsterData.put(CurrentWaveIndex, SpawnMonsterName, WaveMonsterData.get(CurrentWaveIndex, SpawnMonsterName) - 1);
                    MonsterData.MonsterNameFromUID.put(Mob.getUniqueId(), SpawnMonsterName);
                    CanSpawnMonster = true;
                }
            }, (long) (20 * SummonTime));
        }
    }

    public static void CheckNoTargetMonster() {
        for (int i = 0; i < AllMonsterUID.size(); i++) {
            final Monster Mob = (Monster) CODZombie.Main.getServer().getEntity(AllMonsterUID.get(i));
            if (Mob != null && Mob.getTarget() != null && WaveCore.DeathPlayerUID.contains(Mob.getTarget().getUniqueId())) {
                Mob.setTarget(null);
            }
            if (Mob != null && Mob.getTarget() == null) {
                final LivingEntity livingEntity = getNearPlayer(Mob);
                if (Mob.getTarget() != livingEntity && !DeathPlayerUID.contains(livingEntity.getUniqueId())) {
                    Mob.setTarget(livingEntity);
                }
            }
        }
    }

    public static LivingEntity getNearPlayer(Entity Ent) {
        double Distance = 0;
        LivingEntity FinalTarget = null;
        for (Player p : CODZombie.Main.getServer().getOnlinePlayers()) {
            if (DeathPlayerUID.contains(p.getUniqueId())) continue;
            if (Distance == 0) {
                Distance = Ent.getLocation().distance(p.getLocation());
                FinalTarget = p;
                continue;
            }
            if (Ent.getLocation().distance(p.getLocation()) < Distance) {
                FinalTarget = p;
                Distance = Ent.getLocation().distance(p.getLocation());
            }
        }
        return FinalTarget;
    }

    public static void registerMonster(UUID UID) {
        AllMonsterUID.add(UID);
    }

    public static void StartGame() {
        isGameStart = true;
        LoadCurrentWave();
        TNTSystem.getTNT();
        for (Player p : CODZombie.Main.getServer().getOnlinePlayers()) {
            p.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "第" + CurrentWaveIndex + "波", "", 0, 40, 10);
            p.playSound(p.getLocation(), Sound.AMBIENT_CAVE, 1.0f, 1.0f);
            p.getInventory().addItem(GunItem.CreateGunItem(CODZombie.Main.getConfig().getString("GameStartGun"), false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 3000000, 1));
            p.setFoodLevel(20);
            ArmorSystem.SetToDefaultArmorValue(p);
        }
    }

    public static void isGotoNextWave(UUID BeKillUID) {
        if (AllMonsterUID.contains(BeKillUID)) {
            AllMonsterUID.remove(BeKillUID);
            MobLocation.remove(BeKillUID);
            MonsterData.MonsterNameFromUID.remove(BeKillUID);
            TotalMonsterNum--;
            if (TotalMonsterNum <= 0) {
                GotoNextWave();
            }
        }
    }

    public static void GotoNextWave() {
        CurrentWaveIndex++;
        AllMonsterUID.clear();

        final double NextWaveBetweenTime = CODZombie.Main.getConfig().getDouble("NextWaveBetweenTime");

        if (!isUnlimitedMode) {
            if (CurrentWaveIndex > WaveMonsterData.size()) {
                for (Player p : CODZombie.Main.getServer().getOnlinePlayers()) {
                    p.sendTitle(ChatColor.GREEN + "恭喜完成所有波數", "", 10, 200, 10);
                }

                CODZombie.Main.getServer().getScheduler().runTaskLater(CODZombie.Main, new Runnable() {
                    @Override
                    public void run() {
                        ResetGame();
                    }
                }, 200);
                return;
            }
        }

        for (Player p : CODZombie.Main.getServer().getOnlinePlayers()) {
            p.sendTitle(ChatColor.RED + "第" + CurrentWaveIndex + "波", "", 0, 40, 10);
            p.playSound(p.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1.0f, 1.0f);
        }

        if (isUnlimitedMode) {
            MonsterData.UpgradeUnlimitedModMonster();
        }

        CODZombie.Main.getServer().getScheduler().runTaskLater(CODZombie.Main, new Runnable() {
            @Override
            public void run() {
                LoadCurrentWave();
            }
        }, (long) (20 * NextWaveBetweenTime));
    }

    public static int getWaveNumber() {
        return CurrentWaveIndex;
    }

    public static int getWaveCurrentEnemyNumber() {
        return TotalMonsterNum;
    }

    public static void ResetGame() {

        boolean ResetAll = false;

        if (isGameStart) ResetAll = true;

        CurrentWaveIndex = 1;
        WaveMonsterData.clear();
        isGameStart = false;
        isShowGameOver = false;
        CoinWall.hasUnlockIndex.clear();
        MonsterData.MonsterData.clear();
        WaveCore.DeathPlayerUID.clear();
        WaveCore.RevivePlayer.clear();
        MonsterData.LoadMonsterData();
        WallCore.getDefaultIndexLocation();
        WallCore.getAllWindowLocation();
        WallCore.getAllSpawnLocation();
        StoreCore.LoadAllStoreData();
        DrawBox.LoadAllDrawBoxLocation();
        WallCore.RepairAllWall();
        LoadWaveMonsterData();
        CoinWall.LoadCoinWallData();
        TNTSystem.ResetExplodeLocation();
        Turret.KillAllTurret();
        ClearAllMob();
        ClearDropItem();
        CoinWall.ResetAllWall();
        Gun.CDTime.clear();
        Gun.ShotCD.clear();

        for (UUID UID : PlayerTomb.keySet()){
            PlayerTomb.get(UID).remove();
            PlayerTomb.remove(UID);
        }

        for (Player p : CODZombie.Main.getServer().getOnlinePlayers()) {
            p.sendMessage("已準備進行下一場遊戲");
            p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
            p.setGlowing(false);
            CoinCore.SetPlayerCoin(p, 0);
            p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            if (ResetAll) {
                p.setGameMode(GameMode.ADVENTURE);
                p.teleport(p.getWorld().getSpawnLocation());
                p.getInventory().clear();
            }
        }
    }

    public static void ClearAllMob() {
        if (AllMonsterUID.size() > 0) {
            for (UUID UID : AllMonsterUID) {
                CODZombie.Main.getServer().getEntity(UID).remove();
            }
        }
    }

    public static void ClearDropItem() {
        for (Entity Ent : CODZombie.Main.getServer().getWorld("World").getEntities()) {
            if (Ent instanceof Item) {
                Ent.remove();
            }
        }
    }

    public static void UnStuckMobSpawn() {
        if (AllMonsterUID.size() > 0) {
            for (int i = 0; i < AllMonsterUID.size(); i++) {
                if (CODZombie.Main.getServer().getEntity(AllMonsterUID.get(i)) == null) {
                    isGotoNextWave(AllMonsterUID.get(i));
                }
            }
        }
    }

    public static void UnStuckMobMove() {
        CODZombie.Main.getServer().getScheduler().scheduleSyncRepeatingTask(CODZombie.Main, new Runnable() {
            @Override
            public void run() {
                if (AllMonsterUID.size() > 0) {
                    for (int i = 0; i < AllMonsterUID.size(); i++) {

                        Monster Mob = (Monster) CODZombie.Main.getServer().getEntity(AllMonsterUID.get(i));

                        if (MobLocation.containsKey(AllMonsterUID.get(i))) {

                            if (Mob.getLocation().distance(MobLocation.get(AllMonsterUID.get(i))) < 3 && (Mob.getLocation().getY() - MobLocation.get(AllMonsterUID.get(i)).getY()) < 2) {

                                final Location TPLocation;

                                if (Mob.getTarget() != null)
                                    TPLocation = GetStuckTPLocation(Mob.getTarget().getLocation());

                                else continue;

                                Mob.teleport(TPLocation);

                            } else {
                                MobLocation.remove(Mob.getUniqueId());
                            }
                        } else {
                            MobLocation.put(Mob.getUniqueId(), Mob.getLocation());
                        }
                    }
                }
            }
        }, 200, 200);
    }

    public static void SetUnStuck(EntityDamageByEntityEvent e){
        if (MobLocation.containsKey(e.getDamager().getUniqueId())) {
            MobLocation.remove(e.getDamager().getUniqueId());
        }
    }

    public static Location GetStuckTPLocation(Location SceneLocation) {

        Location FinalLocation = null;

        ArrayList<Location> PickLocation = new ArrayList<>();

        double Distance = 0;

        for (Location location : WallCore.CanUseSpawnLocation) {
            if (location.getY() > SceneLocation.getY() - 3){
                PickLocation.add(location);
            }
        }

        if (PickLocation.size() <= 0){
            for (Location location : WallCore.CanUseSpawnLocation) {
                if (FinalLocation == null) {
                    FinalLocation = location;
                    Distance = SceneLocation.distance(location);
                }
                if (location.distance(SceneLocation) < Distance) {
                    FinalLocation = location;
                    Distance = SceneLocation.distance(location);
                }
            }
            return FinalLocation;
        }

        for (Location location : PickLocation) {
            if (FinalLocation == null) {
                FinalLocation = location;
                Distance = SceneLocation.distance(location);
            }
            if (location.distance(SceneLocation) < Distance) {
                FinalLocation = location;
                Distance = SceneLocation.distance(location);
            }
        }
        return FinalLocation;
    }

    public static void SetWaveMonsterData(int WaveIndex, String MonsterName, int NewValue) {
        WaveMonsterData.put(WaveIndex, MonsterName, NewValue);
        if (NewValue > 0 && !CanSpawnMonsterName.contains(MonsterName)) {
            CanSpawnMonsterName.add(MonsterName);
        }
    }

    public static void PlayerDeath(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (((LivingEntity) e.getEntity()).getHealth() - e.getFinalDamage() <= 0 && !DeathPlayerUID.contains(e.getEntity().getUniqueId())) {
                DeathPlayerUID.add(e.getEntity().getUniqueId());

                final int RandomNumber = GunAPI.random.nextInt(RandomDeathText.size());

                SpecterModeIndex.put(e.getEntity().getUniqueId(), 0);

                SpawnPlayerTomb((Player) e.getEntity());

                ((Player) e.getEntity()).setGameMode(GameMode.SPECTATOR);

                for (Player p : CODZombie.Main.getServer().getOnlinePlayers()) {
                    p.sendTitle(ChatColor.RED + "有人血流滿地阿!!", RandomDeathText.get(RandomNumber), 10, 60, 10);
                }
            }
        }
        if (DeathPlayerUID.contains(e.getEntity().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    private static void SpawnPlayerTomb(Player p){

        ItemStack PlayerHead = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta Meta = (SkullMeta) PlayerHead.getItemMeta();

        assert Meta != null;

        Meta.setOwningPlayer(p);

        PlayerHead.setItemMeta(Meta);

        ArmorStand Stand = (ArmorStand) p.getWorld().spawnEntity(p.getLocation(), EntityType.ARMOR_STAND);

        Stand.setInvulnerable(true);

        Stand.setCustomNameVisible(true);

        Stand.setGlowing(true);

        Stand.setCustomName(ChatColor.GREEN + ChatColor.BOLD.toString() + p.getName() + " 的墳墓");

        Stand.getEquipment().setHelmet(PlayerHead);

        PlayerTomb.put(p.getUniqueId(), Stand);
    }

    public static void PlayerSpecterMode(PlayerInteractEvent e){
        if (DeathPlayerUID.contains(e.getPlayer().getUniqueId())){
            final ArrayList<Player> CanLookingPlayer = new ArrayList<>();
            final List<Player> AllPlayer = (List<Player>) CODZombie.Main.getServer().getOnlinePlayers();
            for (Player p : AllPlayer){
                if (!DeathPlayerUID.contains(p.getUniqueId())){
                    CanLookingPlayer.add(p);
                }
            }

            if (CanLookingPlayer.size() > 0) {
                switch (e.getAction()) {
                    case RIGHT_CLICK_AIR:
                    case RIGHT_CLICK_BLOCK:
                        if (SpecterModeIndex.get(e.getPlayer().getUniqueId()) + 1 == CanLookingPlayer.size() - 1) {
                            SpecterModeIndex.put(e.getPlayer().getUniqueId(), 0);
                        } else {
                            SpecterModeIndex.put(e.getPlayer().getUniqueId(), SpecterModeIndex.get(e.getPlayer().getUniqueId()) + 1);
                        }
                        break;
                }
            }
            e.getPlayer().sendTitle("", ChatColor.GREEN + "觀戰中: " + CanLookingPlayer.get(SpecterModeIndex.get(e.getPlayer().getUniqueId())).getName(), 0, 20, 0);
            e.getPlayer().setSpectatorTarget(CanLookingPlayer.get(SpecterModeIndex.get(e.getPlayer().getUniqueId())));
        }
    }

    public static void LimitDeathPlayer(PlayerMoveEvent e) {
        if (DeathPlayerUID.contains(e.getPlayer().getUniqueId())) {
            final Location SetLocation = e.getPlayer().getLocation();
            SetLocation.setPitch(e.getTo().getPitch());
            SetLocation.setYaw(e.getTo().getYaw());
            e.setTo(SetLocation);
        }
    }

    public static void SceneDeathPlayerNearPlayer() {

        for (UUID UID : DeathPlayerUID) {

            final Player ScenePlayer = CODZombie.Main.getServer().getPlayer(UID);

            final ArmorStand DeathPlayerTomb = PlayerTomb.get(ScenePlayer.getUniqueId());

            for (Player p : CODZombie.Main.getServer().getOnlinePlayers()) {
                if (DeathPlayerTomb != null && DeathPlayerTomb.getLocation().distance(p.getLocation()) <= 2.5 && !RevivePlayer.contains(p.getUniqueId()) && !DeathPlayerUID.contains(p.getUniqueId())) {
                    RevivePlayer.add(p.getUniqueId());
                    ScenePlayer.sendTitle(ChatColor.GREEN + "醫護兵正在救治...", "", 0, 40, 0);
                    p.sendTitle(ChatColor.GREEN + "你正在當個醫護兵...", "", 0, 40, 0);

                    CODZombie.Main.getServer().getScheduler().runTaskLater(CODZombie.Main, new Runnable() {
                        @Override
                        public void run() {
                            if (DeathPlayerTomb.getLocation().distance(p.getLocation()) <= 2.5) {

                                RevivePlayer.remove(p.getUniqueId());

                                DeathPlayerUID.remove(UID);

                                ScenePlayer.setHealth(ScenePlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() * 0.5);

                                ScenePlayer.sendTitle(ChatColor.GREEN + "已被救治", "", 0, 20, 0);

                                p.sendTitle(ChatColor.GREEN + "已成功救治", "", 0, 20, 0);

                                ScenePlayer.setGlowing(false);

                                ScenePlayer.teleport(DeathPlayerTomb.getLocation());

                                ScenePlayer.setGameMode(GameMode.ADVENTURE);

                                PlayerTomb.remove(ScenePlayer.getUniqueId());

                                DeathPlayerTomb.remove();
                            } else {

                                RevivePlayer.remove(ScenePlayer.getUniqueId());
                            }
                        }
                    }, 40);
                } else if (ScenePlayer != null && ScenePlayer.getLocation().distance(p.getLocation()) >= 2.5 && RevivePlayer.contains(p.getUniqueId())) {
                    RevivePlayer.remove(p.getUniqueId());
                }
            }
        }

        int DeathPlayer = DeathPlayerUID.size();

        for (UUID UID : DeathPlayerUID) {

            if (CODZombie.Main.getServer().getPlayer(UID) == null) {
                DeathPlayer -= 1;
            }
        }
        if (isGameStart && DeathPlayer >= CODZombie.Main.getServer().getOnlinePlayers().size()) {
            GameOver();
        }
    }

    public static void GameOver() {
        if (!isShowGameOver) {
            for (Player p : CODZombie.Main.getServer().getOnlinePlayers()) {
                p.sendTitle(ChatColor.RED + "遊戲結束", ChatColor.RED + "你生存了" + WaveCore.getWaveNumber() + "波", 0, 200, 0);
                p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1.0f, 0.2f);
            }

            isShowGameOver = true;

            CODZombie.Main.getServer().getScheduler().runTaskLater(CODZombie.Main, new Runnable() {
                @Override
                public void run() {
                    ResetGame();
                }
            }, 200);
        }
    }

}
