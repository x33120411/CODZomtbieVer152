package codzombie.codzombie.Command;

import codzombie.codzombie.WallSystem.CoinWall;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetCoinWallDisplayName implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        CoinWall.SetDisplayNameArmorStandLocation((Player) commandSender);
        return false;
    }
}
