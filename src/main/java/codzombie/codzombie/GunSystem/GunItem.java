package codzombie.codzombie.GunSystem;

import codzombie.codzombie.CODZombie;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GunItem {

    //創建槍物品
    public static ItemStack CreateGunItem(String GunName, boolean HasKillCount){

        if (Gun.GunMaterial.containsKey(GunName)) {

            ItemStack GunItem = new ItemStack(Gun.GunMaterial.get(GunName));

            final List<String> Lore = new ArrayList<>();

            final net.minecraft.server.v1_15_R1.ItemStack NMSItem = CraftItemStack.asNMSCopy(GunItem);

            final NBTTagCompound compound = NMSItem.hasTag() ? NMSItem.getTag() : new NBTTagCompound();

            HashMap<GunDataType, Double> GunData = new HashMap<>();

            String DisplayName = Gun.GunDisplayName.get(GunName);

            for (GunDataType DataType : GunDataType.values()) {

                GunData.put(DataType, Gun.getGunData(GunName, DataType));

                compound.setDouble(DataType.toString(), GunData.get(DataType));

                String Text = GunDataType.ShotText(DataType);

                if (Gun.getGunData(GunName, DataType) == 0.0 || Text == null) {
                    continue;
                }

                Lore.add(Text + GunData.get(DataType));
            }

            compound.setString(NBTKeyType.GunName.toString(), GunName);

            compound.setString(NBTKeyType.FireMode.toString(), Gun.GunType.get(GunName).toString());

            if (HasKillCount) {

                final int GetSkillKillCount = CODZombie.Main.getConfig().getInt("KillCountGetSkillIncrease");

                compound.setInt(GunDataType.GunSkillIndex.toString(), 1);
                compound.setInt(NBTKeyType.GetSkillCount.toString(), GetSkillKillCount);
                compound.setInt(NBTKeyType.KillCount.toString(), 0);
                DisplayName += ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + " (KillCount)";
                Lore.add(ChatColor.RED + ChatColor.BOLD.toString() + "殺傷記數: " + 0);
            }

            NMSItem.setTag(compound);

            GunItem = CraftItemStack.asBukkitCopy(NMSItem);

            final ItemMeta Meta = GunItem.getItemMeta();

            Meta.setDisplayName(DisplayName);

            Meta.setLore(Lore);

            GunItem.setItemMeta(Meta);

            GunItem.setAmount(GunData.get(GunDataType.MagAmmo).intValue());

            if (HasKillCount) GunItem = AddGlow(GunItem);

            return GunItem;
        }
        return null;
    }

    public static ItemStack AddGlow(ItemStack Item){

        final ItemMeta Meta = Item.getItemMeta();

        Meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, false);
        Meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        Item.setItemMeta(Meta);

        return Item;
    }

    public static void CheckPlayerHeldButton(InventoryDragEvent e){
        if (CraftItemStack.asNMSCopy(e.getOldCursor()).hasTag()){
            e.setCancelled(true);
        }
    }

    public static void CheckPlayerClickButton(InventoryClickEvent e) {
        if (e.getCursor() != null && e.getClick().isRightClick() && CraftItemStack.asNMSCopy(e.getCursor()).hasTag()) {
            e.setCancelled(true);
        }
        if (e.getCurrentItem() != null && e.getClick() == ClickType.RIGHT && CraftItemStack.asNMSCopy(e.getCurrentItem()).hasTag()){
            e.setCancelled(true);
        }
    }
}
