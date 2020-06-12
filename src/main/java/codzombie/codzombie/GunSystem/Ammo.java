package codzombie.codzombie.GunSystem;

import codzombie.codzombie.CODZombie;
import codzombie.codzombie.Store.BuyItemData;
import com.google.common.collect.HashBasedTable;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Ammo {

    public static HashBasedTable<Player, String, Integer> ReloadSoundTimerID = HashBasedTable.create();

    public static void ReloadTrigger(PlayerInteractEvent e){
        if (CraftItemStack.asNMSCopy(e.getItem()).hasTag() && CraftItemStack.asNMSCopy(e.getItem()).getTag().hasKey(NBTKeyType.GunName.toString())) {
            switch (e.getAction()) {
                case RIGHT_CLICK_AIR:
                case RIGHT_CLICK_BLOCK:
                    Gun.Shot(e.getPlayer());
                    break;
                case LEFT_CLICK_AIR:
                case LEFT_CLICK_BLOCK:
                    Ammo.Reload(e.getPlayer(), e.getItem(), e.getPlayer().getInventory().getHeldItemSlot());
                    break;
            }
            e.setCancelled(true);
        }
    }

    public static void ReloadTrigger(PlayerInteractAtEntityEvent e){
        if (CraftItemStack.asNMSCopy(e.getPlayer().getInventory().getItemInMainHand()).hasTag() && CraftItemStack.asNMSCopy(e.getPlayer().getInventory().getItemInMainHand()).getTag().hasKey(NBTKeyType.GunName.toString())) {
            Gun.Shot(e.getPlayer());
            e.setCancelled(true);
        }
    }

    public static void UpdateDisplayTotalAmmo(Player p){
        if (p.getInventory().getItemInMainHand().getType() != Material.AIR && CraftItemStack.asNMSCopy(p.getInventory().getItemInMainHand()).hasTag()){

            final int TotalAmmo = CraftItemStack.asNMSCopy(p.getInventory().getItemInMainHand()).getTag().getInt(NBTKeyType.TotalAmmo.toString());

            if (TotalAmmo > 0) p.setLevel(TotalAmmo);
            else p.setLevel(0);
        }
    }

    public static void UpdateDisplayTotalAmmo(Player p, ItemStack CheckItem){
        if (CheckItem != null && CheckItem.getType() != Material.AIR && CraftItemStack.asNMSCopy(CheckItem).hasTag()){

            final int TotalAmmo = CraftItemStack.asNMSCopy(CheckItem).getTag().getInt(NBTKeyType.TotalAmmo.toString());

            if (TotalAmmo > 0) p.setLevel(TotalAmmo);
            else p.setLevel(0);
        }
    }

    public static boolean MinesAmmo(Player p, ItemStack GunItem){

        net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(GunItem);

        NBTTagCompound compound = NMSItem.getTag();

        if (compound.getInt(NBTKeyType.TotalAmmo.toString()) <= 0){
            return false;
        }

        final int CurrentAmmo = compound.getInt(NBTKeyType.TotalAmmo.toString());

        if (GunItem.getAmount() - 1 <= 0){
            compound.setInt(NBTKeyType.TotalAmmo.toString(), CurrentAmmo - 1);
            NMSItem.setTag(compound);
            GunItem = CraftItemStack.asBukkitCopy(NMSItem);
            Reload(p, GunItem, p.getInventory().getHeldItemSlot());
        }
        else {
            compound.setInt(NBTKeyType.TotalAmmo.toString(), CurrentAmmo - 1);

            NMSItem.setTag(compound);

            GunItem = CraftItemStack.asBukkitCopy(NMSItem);

            GunItem.setAmount(GunItem.getAmount() - 1);

            p.getInventory().setItem(p.getInventory().getHeldItemSlot(), GunItem);
        }
        UpdateDisplayTotalAmmo(p);
        return true;
    }

    public static void Reload(Player p, ItemStack GunItem, int ItemSlot){

        final net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(GunItem);

        final String GunName = NMSItem.getTag().getString(NBTKeyType.GunName.toString());

        final double ReloadTime = NMSItem.getTag().getDouble(GunDataType.ReloadTime.toString());

        final int MagAmmo = (int) NMSItem.getTag().getDouble(GunDataType.MagAmmo.toString());

        final int CurrentTotalAmmo = CraftItemStack.asNMSCopy(GunItem).getTag().getInt(NBTKeyType.TotalAmmo.toString());

        if (GunItem.getAmount() >= MagAmmo){
            return;
        }

        SetToReloadMode(p, GunName);

        CODZombie.Main.getServer().getScheduler().runTaskLater(CODZombie.Main, new Runnable() {
            @Override
            public void run() {
                if (CurrentTotalAmmo > MagAmmo){
                    GunItem.setAmount(MagAmmo);
                }
                if (CurrentTotalAmmo < MagAmmo){
                    if (p.getLevel() <= 0){
                        RemoveReloadMode(p, GunName);
                        return;
                    }
                    GunItem.setAmount(p.getLevel());
                }
                if (CraftItemStack.asNMSCopy(p.getInventory().getItem(ItemSlot)).hasTag() &&
                        CraftItemStack.asNMSCopy(p.getInventory().getItem(ItemSlot)).getTag().getString(NBTKeyType.GunName.toString()).equals(GunName)) {
                    p.getInventory().setItem(ItemSlot, GunItem);
                }
                RemoveReloadMode(p, GunName);
            }
        }, (long) (20 * ReloadTime));

        //播放音效
        ReloadSoundTimerID.put(p, GunName, CODZombie.Main.getServer().getScheduler().scheduleSyncRepeatingTask(CODZombie.Main, new Runnable() {
            int RunTimes = 0;
            @Override
            public void run() {
                RunTimes++;
                if (RunTimes >= 4){
                    ClearReloadSoundTimer(p, GunName);
                    return;
                }
                p.playSound(p.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1.0f, 3.0f);
            }
        }, 0, 10));
    }

    public static void ClearReloadSoundTimer(Player p, String GunName){
        CODZombie.Main.getServer().getScheduler().cancelTask(ReloadSoundTimerID.get(p, GunName));
    }

    public static void SetToReloadMode(Player p, String GunName){
        if (!Gun.ShotCD.containsKey(p.getUniqueId())){
            ArrayList<String> NewGunName = new ArrayList<>();
            NewGunName.add(GunName);
            Gun.ShotCD.put(p.getUniqueId(), NewGunName);
        }
        if (Gun.ShotCD.get(p.getUniqueId()).contains(GunName)) {
            Gun.CDTime.remove(p.getUniqueId(), GunName);
        }
        else {
            Gun.ShotCD.get(p.getUniqueId()).add(GunName);
        }
    }

    public static void RemoveReloadMode(Player p, String GunName){
        if (Gun.ShotCD.get(p.getUniqueId()).contains(GunName)){
            Gun.ShotCD.get(p.getUniqueId()).remove(GunName);
        }
    }

    public static BuyItemData SetToMaxAmmo(ItemStack Item){

        final net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(Item);

        if (NMSItem.hasTag()) {

            final NBTTagCompound compound = NMSItem.getTag();

            final String GunName = compound.getString(NBTKeyType.GunName.toString());

            compound.setDouble(GunDataType.TotalAmmo.toString(), Gun.getGunData(GunName, GunDataType.TotalAmmo));

            NMSItem.setTag(compound);

            Item = CraftItemStack.asBukkitCopy(NMSItem);

            Item.setAmount((int) compound.getDouble(GunDataType.MagAmmo.toString()));

            return new BuyItemData(true, Item);
        }
        else {
            return new BuyItemData(false, null);
        }
    }

}
