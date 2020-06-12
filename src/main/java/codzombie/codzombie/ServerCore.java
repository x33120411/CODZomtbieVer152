package codzombie.codzombie;

import codzombie.codzombie.CoinSystem.CoinCore;
import codzombie.codzombie.Command.*;
import codzombie.codzombie.Expendables.ExpendableItem;
import codzombie.codzombie.GunSystem.Gun;
import codzombie.codzombie.WallSystem.CoinWall;
import codzombie.codzombie.WallSystem.WallCore;
import codzombie.codzombie.WaveSystem.WaveCore;
import codzombie.codzombie.WeatherEvent.RainyEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.io.File;
import java.io.IOException;

public class ServerCore {

    public static double ServerOpenTime = 0;

    //建立文件
    public static void CreateFile (){
        CODZombie.Main.getConfig().options().copyDefaults();
        CODZombie.Main.saveDefaultConfig();

        CODZombie.Main.saveResource("WaveSetting.yml", false);
        CODZombie.Main.saveResource("MonsterSetting.yml", false);
        CODZombie.Main.saveResource("GunSetting.yml", false);
        CODZombie.Main.saveResource("ExplodeMaterial.yml", false);

        CODZombie.Main.BuildingLocationFile = new File(CODZombie.Main.getDataFolder(), "BuildingLocation.yml");
        CODZombie.Main.WaveSettingFile = new File(CODZombie.Main.getDataFolder(), "WaveSetting.yml");
        CODZombie.Main.MonsterSettingFile = new File(CODZombie.Main.getDataFolder(), "MonsterSetting.yml");
        CODZombie.GunSettingFile = new File(CODZombie.Main.getDataFolder(), "GunSetting.yml");
        CODZombie.ExplodeMaterialFile = new File(CODZombie.Main.getDataFolder(), "ExplodeMaterial.yml");

        try {
            CODZombie.Main.BuildingLocationFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            CODZombie.WaveSettingFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            CODZombie.Main.MonsterSettingFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            CODZombie.GunSettingFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            CODZombie.ExplodeMaterialFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        CODZombie.Main.BuildingLocationConfig = YamlConfiguration.loadConfiguration(CODZombie.Main.BuildingLocationFile);
        CODZombie.Main.WaveSettingConfig = YamlConfiguration.loadConfiguration(CODZombie.Main.WaveSettingFile);
        CODZombie.MonsterSettingConfig = YamlConfiguration.loadConfiguration(CODZombie.Main.MonsterSettingFile);
        CODZombie.GunSettingConfig = YamlConfiguration.loadConfiguration(CODZombie.GunSettingFile);
        CODZombie.ExplodeMaterialConfig = YamlConfiguration.loadConfiguration(CODZombie.ExplodeMaterialFile);
    }

    //伺服器Tick事件
    public static void ServerTickEvent(){
        CODZombie.Main.getServer().getScheduler().scheduleSyncRepeatingTask(CODZombie.Main, new Runnable() {
            @Override
            public void run() {
                synchronized(CODZombie.Main) {
                    RecordServerOpenTime();
                    WallCore.SceneWindowNearPlayer();
                    Gun.SceneShotCD();
                    UpdateScoreBoard();
                    WaveCore.SpawnMonster();
                    WallCore.SceneWindowNearMonster();
                    WaveCore.UnStuckMobSpawn();
                    WaveCore.CheckNoTargetMonster();
                    WaveCore.SceneDeathPlayerNearPlayer();
                    ExpendableItem.SceneInteractCD();
                    CoinWall.RunDetectLocation();
                    RainyEvent.UpgradeMonster();
                }
            }
        }, 2, 2);
    }

    //紀錄伺服器開啟時間
    public static synchronized void RecordServerOpenTime(){
        ServerOpenTime += 0.1;
    }

    //註冊指令
    public static void registerCommand(){
        CODZombie.Main.getCommand("reset").setExecutor(new ResetAllData());
        CODZombie.Main.getCommand("RecordMode").setExecutor(new WindowRecordMode());
        CODZombie.Main.getCommand("CoinWallRecord").setExecutor(new CoinWallRecordMode());
        CODZombie.Main.getCommand("CoinWallNext").setExecutor(new EnableCoinWallRecordIndex());
        CODZombie.Main.getCommand("SaveCoinWall").setExecutor(new SaveCoinWallLocation());
        CODZombie.Main.getCommand("StartGame").setExecutor(new StartGame());
        CODZombie.Main.getCommand("UnlimitedMode").setExecutor(new UnlimitedMode());
        CODZombie.Main.getCommand("RegisterStore").setExecutor(new RegisterStore());
        CODZombie.Main.getCommand("CODGive").setExecutor(new GetItem());
        CODZombie.Main.getCommand("SetCoin").setExecutor(new SetCoin());
        CODZombie.Main.getCommand("DisplayNameStand").setExecutor(new SetCoinWallDisplayName());
    }

    //更新記分板
    public static void UpdateScoreBoard(){
        if (WaveCore.isGameStart == true) {
            int ScoreIndex = 0;
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard Board = manager.getNewScoreboard();
            Objective o = Board.registerNewObjective("test", "Dummy", "Dummy");
            o.setDisplayName(ChatColor.BLUE + "金錢");
            o.setDisplaySlot(DisplaySlot.SIDEBAR);

            Score WaveNumber = o.getScore(ChatColor.LIGHT_PURPLE + "波數: " + WaveCore.getWaveNumber());
            WaveNumber.setScore(ScoreIndex);
            ScoreIndex++;

            Score TotalMonsterNum = o.getScore(ChatColor.LIGHT_PURPLE + "怪物數量: " + WaveCore.getWaveCurrentEnemyNumber());
            TotalMonsterNum.setScore(ScoreIndex);
            ScoreIndex++;

            for (Player p : CODZombie.Main.getServer().getOnlinePlayers()) {
                Score NewScore = o.getScore(ChatColor.GREEN + p.getName() + " : " + CoinCore.getPlayerCoin(p));
                NewScore.setScore(ScoreIndex);
                ScoreIndex++;
            }

            for (Player p : CODZombie.Main.getServer().getOnlinePlayers()) {
                p.setScoreboard(Board);
            }
        }
    }

}
