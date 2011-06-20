package com.edoxile.bukkit.bettermechanics.utils;

import com.edoxile.bukkit.bettermechanics.exceptions.InvalidConstructionException;
import com.edoxile.bukkit.bettermechanics.exceptions.KeyNotFoundException;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class IntIntMap {
    private int size = 0;

    private transient int[] _keys;
    private transient int[] _values;

    public IntIntMap() {
        _keys = new int[size];
        _values = new int[size];
    }

    private IntIntMap(int[] k, int[] v, int s) {
        _keys = k;
        _values = v;
        size = s;
    }

    public void put(int key, int value) {
        try {
            update(key, value);
        } catch (KeyNotFoundException e) {
            int[] keys = new int[size + 1];
            int[] values = new int[size + 1];

            for (int i = 0; i < size; i++) {
                keys[i] = _keys[i];
                values[i] = _values[i];
            }

            keys[size] = key;
            values[size] = value;

            _values = values;
            _keys = keys;

            size++;
        }
    }

    public void add(int key, int amount) {
        try {
            int index = getIndex(key);
            _values[index] += amount;
        } catch (KeyNotFoundException e) {
            put(key, amount);
        }
    }

    public void remove(int key, int amount) {
        try {
            int index = getIndex(key);
            _values[index] -= amount;
        } catch (KeyNotFoundException e) {
            put(key, amount);
        }
    }

    public void remove(int key) throws IndexOutOfBoundsException, KeyNotFoundException {
        int keyIndex = getIndex(key);

        size--;

        int[] keys = new int[size];
        int[] values = new int[size];

        int index = 0;
        for (int k = 0; k < size + 1; k++) {
            if (index == keyIndex)
                continue;
            values[index] = _values[k];
            keys[index] = _keys[k];
            index++;
        }
    }

    public int get(int key) throws KeyNotFoundException {
        return _values[getIndex(key)];
    }

    public IntIntMapIterator iterator() {
        try {
            return new IntIntMapIterator(_keys, _values);
        } catch (InvalidConstructionException e) {
            return null;
        }
    }

    private int getIndex(int key) throws KeyNotFoundException {
        for (int index = 0; index < size; index++) {
            if (_keys[index] == key)
                return index;
        }
        throw new KeyNotFoundException();
    }

    private void update(int key, int value) throws KeyNotFoundException {
        _values[getIndex(key)] = value;
    }

    @Override
    public String toString() {
        if (size == 0)
            return "{}";
        String msg = "{ ";
        for (int index = 0; index < size; index++) {
            msg += "[" + _keys[index] + ", " + _values[index] + "], ";
        }
        return msg.substring(0, msg.length() - 2) + " }";
    }

    @Override
    public IntIntMap clone() {
        IntIntMap clone = new IntIntMap();
        IntIntMapIterator iterator = iterator();
        while (iterator.hasNext()) {
            iterator.next();
            clone.put(iterator.key(), iterator.value());
        }
        return clone;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}

