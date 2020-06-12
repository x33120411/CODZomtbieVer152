package codzombie.codzombie.Command;

import codzombie.codzombie.Store.StoreCore;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegisterStore implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 3 && NumberUtils.isNumber(strings[0]) && NumberUtils.isNumber(strings[1])){
            StoreCore.setStore((Player) commandSender, Integer.valueOf(strings[0]), Integer.valueOf(strings[1]), strings[2]);
        }
        else {
            commandSender.sendMessage(ChatColor.RED + "你輸入的不是數字");
        }
        return false;
    }
}
