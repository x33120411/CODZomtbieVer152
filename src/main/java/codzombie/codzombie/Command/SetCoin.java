package codzombie.codzombie.Command;

import codzombie.codzombie.CoinSystem.CoinCore;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetCoin implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length > 0 && NumberUtils.isNumber(strings[0])){
            CoinCore.AddPlayerCoin((Player) commandSender, Integer.valueOf(strings[0]));
            commandSender.sendMessage(ChatColor.GREEN + "設定成功");
        }
        else {
            commandSender.sendMessage(ChatColor.RED + "你輸入的不是數字");
        }
        return false;
    }
}
