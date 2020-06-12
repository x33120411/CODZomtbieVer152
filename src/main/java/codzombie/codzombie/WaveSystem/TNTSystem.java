package codzombie.codzombie.WaveSystem;

import codzombie.codzombie.CODZombie;
import codzombie.codzombie.GunSystem.GunAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class TNTSystem {

    public static ArrayList<Material> ExplodeMaterial = new ArrayList<>();

    public static HashMap<Location, Material> ExplodeLocation = new HashMap<>();

    public static void getExplodeMaterial(){
        if (CODZombie.ExplodeMaterialConfig.contains("ExplodeMaterialList")){
            for (String Key : CODZombie.ExplodeMaterialConfig.getConfigurationSection("ExplodeMaterialList").getKeys(false)){
                ExplodeMaterial.add(Material.valueOf(CODZombie.ExplodeMaterialConfig.getString("ExplodeMaterialList." + Key)));
            }
        }
    }

    public static void getTNT(){

        final int Amount = CODZombie.Main.getConfig().getInt("TNTNum");

        final List<Player> AllPlayer = (List<Player>) CODZombie.Main.getServer().getOnlinePlayers();

        final ItemStack Item = new ItemStack(Material.TNT);

        Item.setAmount(1);

        int GiveNumber = 0;

        for (int i = 0; i < Amount; i++){
            if (GiveNumber >= AllPlayer.size()){
                GiveNumber = 0;
            }

            AllPlayer.get(GiveNumber).getInventory().addItem(Item);

            for (Player p : AllPlayer){
                p.sendMessage(ChatColor.GREEN + "有 1 顆TNT在 " + p.getName() + " 身上");
            }
            GiveNumber++;
        }
    }

    public static void TNTExplode(EntityExplodeEvent e){
        if (e.getEntity().getType() == EntityType.PRIMED_TNT){

            e.setCancelled(true);

            for (Block block : e.blockList()){
                if (ExplodeMaterial.contains(block.getType())){
                    ExplodeLocation.put(block.getLocation(), block.getType());
                    block.setType(Material.AIR);
                }
            }

            e.getEntity().getWorld().playSound(e.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 1.0f);
            e.getEntity().getWorld().spawnParticle(Particle.EXPLOSION_HUGE, e.getLocation(), 1);
        }
    }

    public static void PutTNT(PlayerInteractEvent e){
        if (e.hasItem() && e.getItem().getType() == Material.TNT && e.hasBlock()){
            switch (e.getAction()){
                case RIGHT_CLICK_BLOCK:
                case RIGHT_CLICK_AIR:
                    Vector Direction = e.getPlayer().getEyeLocation().subtract(e.getClickedBlock().getLocation()).toVector().normalize();
                    SpawnTNT(e.getClickedBlock().getLocation().add(Direction));
                    ItemStack SetItem = e.getItem();
                    SetItem.setAmount(SetItem.getAmount() - 1);
                    e.getPlayer().getInventory().setItem(e.getPlayer().getInventory().getHeldItemSlot(), SetItem);
                    break;
            }

            for (Player p : CODZombie.Main.getServer().getOnlinePlayers()){
                p.sendMessage(ChatColor.LIGHT_PURPLE + "已放置TNT");
            }
        }
    }

    public static void SpawnTNT(Location location){
        location.getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
    }

    public static void ResetExplodeLocation(){
        for (Location location : ExplodeLocation.keySet()){
            location.getBlock().setType(ExplodeLocation.get(location));
        }
        ExplodeLocation.clear();
    }

}
