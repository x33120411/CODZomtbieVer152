package codzombie.codzombie.CoinSystem;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.UUID;

public class CoinCore {

    //玩家金錢
    private static HashMap<UUID, Integer> PlayerCoinNumber = new HashMap<>();

    //取得玩家金錢
    public static int getPlayerCoin(Player p) {
        if (PlayerCoinNumber.containsKey(p.getUniqueId())) {
            return PlayerCoinNumber.get(p.getUniqueId());
        }
        return 0;
    }

    //增加玩家金錢
    public static int AddPlayerCoin(Player p, int AddCoin) {
        final int PutNumber = getPlayerCoin(p) + AddCoin;
        PlayerCoinNumber.put(p.getUniqueId(), PutNumber);
        return PutNumber;
    }

    //減少玩家金錢
    public static int MinesPlayerCoin(Player p, int MinesCoin) {
        final int PutNumber = getPlayerCoin(p) - MinesCoin;
        PlayerCoinNumber.put(p.getUniqueId(), PutNumber);
        return PutNumber;
    }

    //設定玩家金錢
    public static void SetPlayerCoin(Player p, int NewValue) {
        PlayerCoinNumber.put(p.getUniqueId(), NewValue);
    }

    //確認玩家是否有足夠的金錢
    public static boolean CheckHasCoin(Player p, int CheckValue) {
        if (getPlayerCoin(p) >= CheckValue) {
            return true;
        }
        return false;
    }

    //怪物被擊中增加金錢
    public static void HitMonsterAddCoin(Player p, int Coin, boolean isHeadShot) {
        AddPlayerCoin(p, Coin);

        String Text = ChatColor.GREEN + "+" + Coin + "金錢";

        if (isHeadShot) Text += " (爆擊) ";

        p.sendMessage(Text);
    }

}
