package codzombie.codzombie.Command;

import codzombie.codzombie.WallSystem.WallCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WindowRecordMode implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        WallCore.RecordMode((Player) commandSender);
        return false;
    }
}
