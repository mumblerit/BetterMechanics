package com.edoxile.bukkit.bettermechanics.Utils;

import com.edoxile.bukkit.bettermechanics.MechanicsType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class SignUtil {
    public static boolean isSign(Block b){
        return (b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN);
    }

    public static Sign getSign(Block b){
        if(!isSign(b))
            return null;
        BlockState s = b.getState();
        if(s instanceof Sign)
            return (Sign)s;
        else
            return null;
    }

    public static MechanicsType getMechanicsType(Sign s){
        String str = s.getLine(1);
        if(s.equals("") || s == null)
            return null;

        if(str.equals("[Lift Up]") || str.equals("[Lift Down]") || str.equals("[Lift]"))
            return MechanicsType.LIFT;
        else if(str.equals("[Gate]") || str.equals("[Dgate]"))
            return MechanicsType.GATE;
        else if(str.equals("[Bridge]") || str.equals("[Bridge End]"))
            return MechanicsType.BRIDGE;
        else if(str.equals("[Door]") || str.equals("[Door Down]") || str.equals("[Door Up]"))
            return MechanicsType.DOOR;
        else if(str.equals("[X]"))
            return MechanicsType.HIDDEN_SWITCH;
        else if(str.equals("[I]"))
            return MechanicsType.LIGHT_SWITCH;
        else
            return null;
    }
}
