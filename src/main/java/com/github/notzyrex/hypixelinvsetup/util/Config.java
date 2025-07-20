package com.github.notzyrex.hypixelinvsetup.util;

import com.github.notzyrex.hypixelinvsetup.main.SetupManager;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.common.config.Configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class Config {
    private static Configuration config;
    public static List<String> hotbar = Collections.emptyList();
    public static List<String> quickBuy = Collections.emptyList();

    public static String[] defaultHotbar = {"Melee", "Blocks", "Utility", "Potions", "Potions", "Tools",
            "Tools", "Tools", "Utility"};
    public static String[] defaultQuickBuy = {
            "Wool", "Stone Sword", "Iron Boots", "Wooden Pickaxe", "Golden Apple", "Jump V Potion", "TNT",
            "End Stone", "Iron Sword", "Diamond Boots", "Wooden Axe", "Fireball", "Invisibility Potion", "Magic Milk",
            "Oak Wood Planks", "Diamond Sword", "Chainmail Boots", "Permanent Shears", "Bridge Egg", "Speed II Potion", "Water Bucket"
    };
    public static int defaultDelay = 3;

    public static void load() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            File configDir = new File("config");
            if (!configDir.exists()) configDir.mkdirs();

            File configFile = new File("config/inv-setup.json");

            if (configFile.exists()) {
                try (FileReader reader = new FileReader(configFile)) {
                    Type type = new TypeToken<Map<String, Map<String, Object>>>() {}.getType();
                    Map<String, Map<String, Object>> rootData = gson.fromJson(reader, type);

                    Map<String, Object> main = rootData.getOrDefault("main", Collections.emptyMap());
                    Map<String, Object> settings = rootData.getOrDefault("settings", Collections.emptyMap());

                    hotbar = main.containsKey("hotbar")
                            ? (List<String>) main.get("hotbar")
                            : Arrays.asList(defaultHotbar);

                    quickBuy = main.containsKey("quick-buy")
                            ? (List<String>) main.get("quick-buy")
                            : Arrays.asList(defaultQuickBuy);

                    Number delayValue = settings.containsKey("delay") && settings.get("delay") instanceof Number
                            ? (Number) settings.get("delay")
                            : defaultDelay;

                    SetupManager.setDelayTicks(delayValue.intValue());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Map<String, Object> main = new HashMap<>();
                main.put("hotbar", Arrays.asList(defaultHotbar));
                main.put("quick-buy", Arrays.asList(defaultQuickBuy));

                Map<String, Object> settings = new HashMap<>();
                settings.put("delay", defaultDelay);

                Map<String, Object> rootData = new HashMap<>();
                rootData.put("main", main);
                rootData.put("settings", settings);

                try (FileWriter writer = new FileWriter(configFile)) {
                    gson.toJson(rootData, writer);
                }

                hotbar = Arrays.asList(defaultHotbar);
                quickBuy = Arrays.asList(defaultQuickBuy);
                SetupManager.setDelayTicks(defaultDelay);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateSetting(String key, Object value) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File configFile = new File("config/inv-setup.json");

        try {
            Map<String, Object> rootData;

            if (configFile.exists()) {
                try (FileReader reader = new FileReader(configFile)) {
                    Type type = new TypeToken<Map<String, Object>>() {}.getType();
                    rootData = gson.fromJson(reader, type);
                }
            } else {
                rootData = new HashMap<>();
            }

            // Get or create "settings"
            Map<String, Object> settings = (Map<String, Object>) rootData.get("settings");
            if (settings == null) {
                settings = new HashMap<>();
            }

            settings.put(key, value);
            rootData.put("settings", settings);  // Ensure settings is placed back

            // Write updated config back
            try (FileWriter writer = new FileWriter(configFile)) {
                gson.toJson(rootData, writer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    public static void load(File file) {
        config = new Configuration(file);
        config.load();

        String[] hotbarFromConfig = config.get("main", "hotbar", defaultHotbar).getStringList();
        String[] quickBuyFromConfig = config.get("main", "quick-buy", defaultQuickBuy).getStringList();

        hotbar = Arrays.asList(hotbarFromConfig);
        quickBuy = Arrays.asList(quickBuyFromConfig);
    } */
}