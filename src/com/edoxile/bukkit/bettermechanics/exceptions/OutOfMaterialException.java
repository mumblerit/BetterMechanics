package com.edoxile.bukkit.bettermechanics.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class OutOfMaterialException extends Exception {
    private int amount;
    public OutOfMaterialException(int a){
        amount = a;
    }

    public int getAmount(){
        return amount;
    }
}
