package codzombie.codzombie.Command;

import codzombie.codzombie.GunSystem.Gun;
import codzombie.codzombie.WallSystem.WallCore;
import codzombie.codzombie.WaveSystem.WaveCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ResetAllData implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        WaveCore.ResetGame();
        return false;
    }
}
