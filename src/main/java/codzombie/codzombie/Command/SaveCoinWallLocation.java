package codzombie.codzombie.Command;

import codzombie.codzombie.WallSystem.CoinWall;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SaveCoinWallLocation implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (NumberUtils.isNumber(strings[0])) {
            CoinWall.SaveCoinWall((Player) commandSender, Integer.valueOf(strings[0]));
        }
        else {
            commandSender.sendMessage(ChatColor.RED + "你輸入的不是數字");
        }
        return false;
    }
}
