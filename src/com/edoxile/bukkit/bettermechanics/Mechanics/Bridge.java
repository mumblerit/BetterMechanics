package com.edoxile.bukkit.bettermechanics.Mechanics;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class Bridge {
    private Sign sign;
    private Player player;

    public Bridge(Sign s, Player p) {
        sign = s;
        player = p;
    }

    public boolean map(){

        return false;
    }

    public void toggleOpen(){

    }

    public void toggleClosed(){

    }
}
