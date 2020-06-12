package codzombie.codzombie.Expendables;

import codzombie.codzombie.GunSystem.NBTKeyType;
import codzombie.codzombie.ServerCore;
import codzombie.codzombie.WaveSystem.WaveCore;
import net.minecraft.server.v1_15_R1.ItemStack;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ExpendableItem {

    public static HashMap<UUID, Double> PlayerInteractCDTime = new HashMap<>();

    public static void PutExpendItem(PlayerInteractEvent e){
        if (CraftItemStack.asNMSCopy(e.getPlayer().getInventory().getItemInMainHand()).hasTag() &&
                CraftItemStack.asNMSCopy(e.getPlayer().getInventory().getItemInMainHand()).getTag().hasKey(NBTKeyType.ExpendName.toString()) &&
                !WaveCore.DeathPlayerUID.contains(e.getPlayer().getUniqueId()) && !hasInteractCD(e.getPlayer())){

            ExpendableType Type = ExpendableType.valueOf(CraftItemStack.asNMSCopy(e.getPlayer().getInventory().getItemInMainHand()).getTag().getString(NBTKeyType.ExpendName.toString()));

            RayTraceResult result = e.getPlayer().rayTraceBlocks(3);

            Vector vector = null;

            if (result != null){
                vector = result.getHitPosition();

                vector.add(e.getPlayer().getLocation().toVector().subtract(vector));
            }

            if (vector != null) {
                switch (Type) {
                    case FireTurret:
                        Turret.SpawnTurret(e.getPlayer(), vector.toLocation(e.getPlayer().getWorld()), TurretType.FireTurret);
                        break;
                    case GunTurret:
                        Turret.SpawnTurret(e.getPlayer(), vector.toLocation(e.getPlayer().getWorld()), TurretType.GunTurret);
                        break;
                }

                final org.bukkit.inventory.ItemStack SetItem = e.getPlayer().getInventory().getItemInMainHand();

                SetItem.setAmount(SetItem.getAmount() - 1);

                e.getPlayer().getInventory().setItem(e.getPlayer().getInventory().getHeldItemSlot(), SetItem);
            }

            SetInteractCD(e.getPlayer(), 0.5);

            e.setCancelled(true);
        }
    }

    public static org.bukkit.inventory.ItemStack CreateExpendItem(String Type, int Amount){

        Material ItemType = ExpendableType.getItemMaterial(Type);

        if (ItemType != null) {

            org.bukkit.inventory.ItemStack NewItem = new org.bukkit.inventory.ItemStack(ItemType);

            ItemStack NMSItem = CraftItemStack.asNMSCopy(NewItem);

            NBTTagCompound compound = new NBTTagCompound();

            compound.setString(NBTKeyType.ExpendName.toString(), Type);

            NMSItem.setTag(compound);

            NewItem = CraftItemStack.asBukkitCopy(NMSItem);

            NewItem.setAmount(Amount);

            ItemMeta Meta = NewItem.getItemMeta();

            Meta.setLore(ExpendableType.getItemLore(Type));

            Meta.setDisplayName(ExpendableType.getItemDisplayName(Type));

            NewItem.setItemMeta(Meta);

            return NewItem;
        }
        return null;
    }

    public static void SetInteractCD(Player p, double Time){
        PlayerInteractCDTime.put(p.getUniqueId(), ServerCore.ServerOpenTime + Time);
    }

    public static void SceneInteractCD(){
        ArrayList<UUID> WaitRemove = new ArrayList<>();
        for (UUID UID : PlayerInteractCDTime.keySet()){
            if (ServerCore.ServerOpenTime >= PlayerInteractCDTime.get(UID)){
                WaitRemove.add(UID);
            }
        }

        for (UUID UID : WaitRemove){
            PlayerInteractCDTime.remove(UID);
        }
        WaitRemove.clear();
    }

    public static boolean hasInteractCD(Player p){
        if (PlayerInteractCDTime.containsKey(p.getUniqueId())){
            return true;
        }
        return false;
    }

}
