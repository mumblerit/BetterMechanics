package com.edoxile.bukkit.bettermechanics.utils;


import com.edoxile.bukkit.bettermechanics.exceptions.InvalidConstructionException;

public class IntIntMapIterator {
    private int[] _keys, _values;
    private int pointer = 0;
    private int size;

    public IntIntMapIterator(int[] keys, int[] values) throws InvalidConstructionException {
        if (keys.length != values.length)
            throw new InvalidConstructionException();
        _keys = keys;
        _values = values;
        size = _keys.length;
    }

    public boolean hasNext() {
        return ((pointer + 1) < _keys.length);
    }

    public boolean hasPrevious() {
        return ((pointer - 1) > 0);
    }

    public void next() {
        pointer++;
    }

    public void previous() {
        pointer--;
    }

    public int key() {
        return _keys[pointer];
    }

    public int value() {
        return _values[pointer];
    }

    public void rewind() {
        pointer = 0;
    }

    public void end() {
        pointer = size - 1;
    }

    @Override
    public String toString() {
        if (size == 0)
            return "{}";
        String msg = "[" + size + " items] :: { ";
        for (int index = 0; index < size; index++) {
            msg += "[" + _keys[index] + ", " + _values[index] + "], ";
        }
        return msg.substring(0, msg.length() - 2) + " } @ " + pointer;
    }
}
