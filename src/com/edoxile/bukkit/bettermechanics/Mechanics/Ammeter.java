package com.edoxile.bukkit.bettermechanics.Mechanics;

import com.edoxile.bukkit.bettermechanics.Utils.MechanicsConfig;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class Ammeter {
    private Block wire;
    private Player player;
    private MechanicsConfig.AmmeterConfig config;

    public Ammeter(MechanicsConfig c, Block w, Player p) {
        wire = w;
        player = p;
        config = c.getAmmeterConfig();
    }

    public void measure(){
        if(!config.enabled)
            return;
        if(wire.getType() == Material.REDSTONE_WIRE){
            player.sendMessage(ChatColor.GOLD + "Power in redstone wire: " + Byte.toString(wire.getData()) + "/15.");
        }
    }
}
