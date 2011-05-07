package com.edoxile.bukkit.bettermechanics.Mechanics;

import com.edoxile.bukkit.bettermechanics.Exceptions.BlockNotFoundException;
import com.edoxile.bukkit.bettermechanics.MechanicsType;
import com.edoxile.bukkit.bettermechanics.Utils.BlockMapper;
import com.edoxile.bukkit.bettermechanics.Utils.MechanicsConfig;
import com.edoxile.bukkit.bettermechanics.Utils.SignUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class Lift {
    private Sign sign;
    private Player player;
    private MechanicsConfig.LiftConfig config;
    private static List<Integer> nonSolidBlockList = Arrays.asList(0,6,8,9,10,11,27,28,30,3,38,39,40,50,51,55,59,63,65,66,68,69,70,72,75,76,78,83,90,93,94);
    private Location destination;

    public Lift(MechanicsConfig c, Sign s, Player p) {
        sign = s;
        player = p;
        config = c.getLiftConfig();
    }

    public boolean map() throws BlockNotFoundException {
        Sign endSign;
        BlockFace direction;
        if (sign.getLine(1).equals("[Lift Down]")) {
            direction = BlockFace.DOWN;
        } else {
            direction = BlockFace.UP;
        }
        endSign = BlockMapper.findMechanicsSign(sign.getBlock(), direction, MechanicsType.LIFT, config.maxSearchHeight);
        int x = player.getLocation().getBlockX();
        int z = player.getLocation().getBlockZ();
        int y = endSign.getBlock().getLocation().getBlockY();
        Block dest1 = sign.getWorld().getBlockAt(x, y, z);
        Block dest2 = dest1.getRelative(BlockFace.UP);
        Block dest3 = dest1.getRelative(BlockFace.DOWN);
        if (canPassThrough(dest1.getType()) && canPassThrough(dest2.getType())) {
            destination = player.getLocation();
            destination.setY(((double)dest1.getY()));
            if(player.teleport(destination)){
                if(endSign.getLine(0).equals("")){
                    player.sendMessage(ChatColor.GOLD + "You went a floor " + direction.name().toLowerCase() + "!");
                }else{
                    player.sendMessage(ChatColor.GOLD + "You went to floor: " + endSign.getLine(0));
                }
            }
        } else if (canPassThrough(dest1.getType()) && canPassThrough(dest3.getType())) {
            destination = player.getLocation();
            destination.setY(((double)dest3.getY()));
            if(player.teleport(destination)){
                if(endSign.getLine(0).equals("")){
                    player.sendMessage(ChatColor.GOLD + "You went a floor " + direction.name().toLowerCase() + "!");
                }else{
                    player.sendMessage(ChatColor.GOLD + "You went to floor: " + endSign.getLine(0));
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "You would be obscured!");
        }
        return false;
    }

    public boolean movePlayer() {

        return false;
    }

    private boolean canPassThrough(Material m){
        return nonSolidBlockList.contains(m.getId());
    }
}
