package com.github.notzyrex.hypixelinvsetup.util;

public class ItemMapping {
    public static String[] map = {"Blocks", "Melee", "Tools", "Ranged", "Potions", "Utility", "Compass"};

    public static int getPosition(String query) {
        for (int i = 0; i < map.length; i++) {
            if (map[i].equals(query)) return i + 10;
        }

        return -1;
    }
}
