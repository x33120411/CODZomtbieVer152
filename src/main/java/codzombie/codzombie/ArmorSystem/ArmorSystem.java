package codzombie.codzombie.ArmorSystem;

import codzombie.codzombie.CODZombie;
import codzombie.codzombie.Store.BuyItemData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.UUID;

public class ArmorSystem {

    public static HashMap<UUID, Double> ArmorData = new HashMap<>();

    public static HashMap<UUID, Double> BuyArmorValue = new HashMap<>();

    public static double getArmorValue (UUID UID){
        return ArmorData.get(UID);
    }

    public static void SetArmorValue(UUID UID, double NewValue){
        ArmorData.put(UID, NewValue);
    }

    public static double AddArmorValue(UUID UID, double Value){
        ArmorData.put(UID, getArmorValue(UID) + Value);
        return getArmorValue(UID);
    }

    public static double MinesArmorValue(UUID UID, double Value){
        ArmorData.put(UID, getArmorValue(UID) - Value);
        return getArmorValue(UID);
    }

    public static void SetToDefaultArmorValue(Player p){

        final double Value = CODZombie.Main.getConfig().getDouble("DefaultArmorValue");

        final double BuyArmorNum =CODZombie.Main.getConfig().getDouble("BuyArmorValue");

        ArmorData.put(p.getUniqueId(), Value);

        BuyArmorValue.put(p.getUniqueId(), BuyArmorNum);
    }

    public static void hasDefaultArmorValue(Player p){
        if (!ArmorData.containsKey(p.getUniqueId())){
            SetToDefaultArmorValue(p);
        }
    }

    public static BuyItemData BuyArmorValue(Player p){

        final double MaxArmor = CODZombie.Main.getConfig().getDouble("ArmorMax");

        final double BuyArmorNum = BuyArmorValue.get(p.getUniqueId());

        final double BuyArmorValueMines = CODZombie.Main.getConfig().getDouble("BuyArmorValueMines");

        final double MinBuyArmorValue = CODZombie.Main.getConfig().getDouble("MinBuyArmorValue");

        if (getArmorValue(p.getUniqueId()) <= MaxArmor){
            return new BuyItemData(false, null);
        }

        MinesArmorValue(p.getUniqueId(), BuyArmorNum);

        if (BuyArmorNum > MinBuyArmorValue){
            BuyArmorValue.put(p.getUniqueId(), BuyArmorNum - BuyArmorValueMines);
        }

        p.sendMessage(ChatColor.GREEN + "減傷率提升至: " + (1 - getArmorValue(p.getUniqueId())));

        return new BuyItemData(true, null);
    }

    public static void TriggerArmor(EntityDamageByEntityEvent e){
        if (e.getEntity() instanceof Player){
            e.setDamage(e.getDamage() * getArmorValue(e.getEntity().getUniqueId()));
        }
    }

}
