package com.edoxile.bukkit.bettermechanics.Mechanics;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class HiddenSwitch {
    private Sign sign;
    private Player player;

    public HiddenSwitch(Sign s, Player p) {
        sign = s;
        player = p;
    }

    public boolean map(){

        return false;
    }

    public void toggleLevers(){

    }
}
