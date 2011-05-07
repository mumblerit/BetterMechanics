package com.edoxile.bukkit.bettermechanics.Mechanics;

import com.edoxile.bukkit.bettermechanics.Utils.BlockMapper;
import com.edoxile.bukkit.bettermechanics.Utils.MechanicsConfig;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class HiddenSwitch {
    private Sign sign;
    private Player player;
    private MechanicsConfig.HiddenSwitchConfig config;
    private HashSet<Block> levers;

    public HiddenSwitch(MechanicsConfig c, Sign s, Player p) {
        sign = s;
        player = p;
        config = c.getHiddenSwitchConfig();
    }

    public boolean map(){
        levers = BlockMapper.mapAllInCuboidRegion(sign.getBlock(),1, Material.LEVER);
        return (!levers.isEmpty());
    }

    public void toggleLevers(){
        for(Block b : levers){
            b.setData((byte)(b.getData() ^ 0x8));
        }
    }
}
