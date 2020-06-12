package codzombie.codzombie;

import codzombie.codzombie.ArmorSystem.ArmorSystem;
import codzombie.codzombie.DrawBox.DrawBox;
import codzombie.codzombie.Expendables.ExpendableItem;
import codzombie.codzombie.Expendables.Turret;
import codzombie.codzombie.GunSystem.Ammo;
import codzombie.codzombie.GunSystem.Gun;
import codzombie.codzombie.GunSystem.GunAPI;
import codzombie.codzombie.GunSystem.GunItem;
import codzombie.codzombie.Store.StoreCore;
import codzombie.codzombie.WallSystem.CoinWall;
import codzombie.codzombie.WallSystem.WallCore;
import codzombie.codzombie.WaveSystem.MonsterAttack;
import codzombie.codzombie.WaveSystem.MonsterData;
import codzombie.codzombie.WaveSystem.TNTSystem;
import codzombie.codzombie.WaveSystem.WaveCore;
import codzombie.codzombie.WeatherEvent.RainyEvent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class CODZombie extends JavaPlugin implements Listener {

    public static CODZombie Main;

    public static File BuildingLocationFile;
    public static File WaveSettingFile;
    public static File MonsterSettingFile;
    public static File GunSettingFile;
    public static File ExplodeMaterialFile;

    public static YamlConfiguration BuildingLocationConfig;
    public static YamlConfiguration WaveSettingConfig;
    public static YamlConfiguration MonsterSettingConfig;
    public static YamlConfiguration GunSettingConfig;
    public static YamlConfiguration ExplodeMaterialConfig;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Main = this;
        this.getServer().getPluginManager().registerEvents(this, this);
        ServerCore.ServerTickEvent();
        ServerCore.CreateFile();
        ServerCore.registerCommand();
        GunAPI.SetDefaultData();
        Gun.LoadGunData();
        WallCore.getAllWindowLocation();
        WallCore.getAllSpawnLocation();
        MonsterData.LoadMonsterData();
        WaveCore.LoadWaveMonsterData();
        WaveCore.SetDefaultData();
        WallCore.getDefaultIndexLocation();
        DrawBox.LoadAllDrawBoxLocation();
        StoreCore.LoadAllStoreData();
        TNTSystem.getExplodeMaterial();
        WaveCore.UnStuckMobMove();
        CoinWall.LoadCoinWallData();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        WaveCore.ClearAllMob();
        Turret.KillAllTurret();
        TNTSystem.ResetExplodeLocation();
        CoinWall.ResetAllWall();
    }

    @EventHandler
    public void OnPlayerInteract(PlayerInteractEvent e){
        Ammo.ReloadTrigger(e);
        DrawBox.OpenDrawBox(e);
        DrawBox.getDrawBoxItem(e.getPlayer(), e.getClickedBlock());
        ExpendableItem.PutExpendItem(e);
        TNTSystem.PutTNT(e);
        CoinWall.UnlockArea(e.getPlayer());
        WaveCore.PlayerSpecterMode(e);
    }

    @EventHandler
    public void OnEntityDamageEntity(EntityDamageByEntityEvent e){
        MonsterAttack.MonsterArrowDamage(e);
        ArmorSystem.TriggerArmor(e);
        Turret.TurretBeDamage(e);
        WaveCore.SetUnStuck(e);
    }

    @EventHandler
    public void OnEntityDamage(EntityDamageEvent e){
        WaveCore.PlayerDeath(e);
        Turret.TurretDeath(e);
    }

    @EventHandler
    public void OnEntityDeath(EntityDeathEvent e){
        WaveCore.isGotoNextWave(e.getEntity().getUniqueId());
        WallCore.CancelDeathEntityDestroyWindow(e.getEntity().getUniqueId());
    }

    @EventHandler
    public void OnPlayerPutBlock(BlockPlaceEvent e){
        WallCore.setWindowLocation(e.getPlayer(), e.getBlock().getLocation(), e.getBlock().getType());
        WallCore.setMonsterSpawnLocation(e.getPlayer(), e.getBlock().getLocation(), e.getBlock().getType());
        CoinWall.setCoinWallLocation(e.getPlayer(), e.getBlock().getLocation(), e.getBlock().getType());
        DrawBox.setDrawBox(e);
        StoreCore.StoreCatch(e.getPlayer(), e.getBlock());
    }

    @EventHandler
    public void OnEntitySpawn(EntitySpawnEvent e){

    }

    @EventHandler
    public void OnPlayerDestroyBlock(BlockBreakEvent e){
        WallCore.DeleteWindowLocation(e.getPlayer(), e.getBlock().getLocation());
        CoinWall.DeleteCoinWallLocation(e.getPlayer(), e.getBlock().getLocation());
    }

    @EventHandler
    public void OnPlayerInteractEnt(PlayerInteractAtEntityEvent e){
        StoreCore.BuyItem(e);
        Ammo.ReloadTrigger(e);
        Turret.PlayerInteractArmorStand(e);
    }

    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent e){
        ArmorSystem.hasDefaultArmorValue(e.getPlayer());
    }

    @EventHandler
    public void OnChat(AsyncPlayerChatEvent e){
        WallCore.setMonsterSpawnIndex(e);
        CoinWall.RecordMonsterSpawnLocationIndex(e);
    }

    @EventHandler
    public void OnChangeHeldItem(PlayerItemHeldEvent e){
        Ammo.UpdateDisplayTotalAmmo(e.getPlayer(), e.getPlayer().getInventory().getItem(e.getNewSlot()));
    }

    @EventHandler
    public void OnDropItem(PlayerDropItemEvent e){
        Gun.PlayerDropGun(e);
    }

    @EventHandler
    public void OnPlayerMove(PlayerMoveEvent e){
        WaveCore.LimitDeathPlayer(e);
    }

    @EventHandler
    public void OnEntityExplode(EntityExplodeEvent e){
        TNTSystem.TNTExplode(e);
    }

    @EventHandler
    public void OnDragInventory(InventoryDragEvent e){
        GunItem.CheckPlayerHeldButton(e);
    }

    @EventHandler
    public void OnInventoryClick(InventoryClickEvent e){
        GunItem.CheckPlayerClickButton(e);
    }

    @EventHandler
    public void OnRainy(WeatherChangeEvent e){
        RainyEvent.RainyEvent(e);
    }
}
