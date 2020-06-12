package codzombie.codzombie.Command;

import codzombie.codzombie.WallSystem.CoinWall;
import codzombie.codzombie.WallSystem.WallCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoinWallRecordMode implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        CoinWall.EnableRecordMode((Player) commandSender);
        return false;
    }
}
