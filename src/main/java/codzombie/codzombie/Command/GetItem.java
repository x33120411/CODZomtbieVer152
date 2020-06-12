package codzombie.codzombie.Command;

import codzombie.codzombie.Expendables.ExpendableItem;
import codzombie.codzombie.GunSystem.GunItem;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GetItem implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        ItemStack Item = null;
        if (strings.length > 0) {
            Item = GunItem.CreateGunItem(strings[0], false);
            if (Item == null) Item = ExpendableItem.CreateExpendItem(strings[0], 1);
        }
        if (Item != null){
            ((Player) commandSender).getInventory().addItem(Item);
            commandSender.sendMessage(ChatColor.GREEN + "成功取得物品");
        }
        else {
            commandSender.sendMessage(ChatColor.RED + "此名稱沒有任何對應物品");
        }
        return false;
    }
}
