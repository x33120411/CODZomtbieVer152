package codzombie.codzombie.Command;

import codzombie.codzombie.WaveSystem.WaveCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UnlimitedMode implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        WaveCore.OpenUnlimitedMod();
        return false;
    }
}
