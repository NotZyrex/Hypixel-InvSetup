package com.github.notzyrex.hypixelinvsetup.main;

import com.github.notzyrex.hypixelinvsetup.util.Config;
import com.github.notzyrex.hypixelinvsetup.util.Inventory;
import com.github.notzyrex.hypixelinvsetup.util.ItemMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;

import java.util.Map;

public class SetupManager {
    private static boolean doingSetup = false;
    private static boolean containerWasOpen = false;
    private static boolean doneHotbar = false;
    private static int delayTicks = 5;
    private static int ticksPassed = 0;

    private static int currentHotbarSlot = 0;
    private static int currentQuickBuySlot = 0;

    public static void start() {
        doingSetup = true;
        System.out.println("Starting inventory setup...");
    }

    public static void abort() {
        doingSetup = false;
        containerWasOpen = false;
        doneHotbar = false;
        ticksPassed = 0;
        currentHotbarSlot = 0;
        currentQuickBuySlot = 0;

        // System.out.println("Setup aborted.");
    }

    public static boolean isRunning() {
        return doingSetup;
    }

    public static void setDelayTicks(int ticks) {
        delayTicks = ticks;
        Config.updateSetting("delay", ticks);
    }

    public static void onGuiTick() {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiChest) {
            containerWasOpen = true;
        } else {
            if (containerWasOpen) {
                abort();
            }
            containerWasOpen = false;
        }
    }

    public static void update() {
        if (!doingSetup) return;
        if (++ticksPassed < delayTicks) return;
        ticksPassed = 0;

        if (Inventory.getOpenContainer() != null) {
            ContainerChest openContainer = (ContainerChest) Inventory.getOpenContainer();
            int settingsSlot = Inventory.findInContainer("Bed Wars Settings");
            int hotbarManager = Inventory.findInContainer("Hotbar Manager");
            int editQuickBuy = Inventory.findInContainer("Edit Quick Buy");

            String menuName = openContainer.getLowerChestInventory().getName();

            if (menuName.contains("Hotbar Manager")) {
                if (currentHotbarSlot >= Config.hotbar.size()) {
                    doneHotbar = true;
                    int backButton = Inventory.findInContainer("Go Back");
                    if (backButton != -1) Inventory.click(backButton, 0);
                    return;
                }

                int targetSlot = 27 + currentHotbarSlot;
                String itemName = Config.hotbar.get(currentHotbarSlot);

                if (Inventory.checkSlot(openContainer.getLowerChestInventory(), targetSlot, itemName) == null) {
                    System.out.println("Dragging hotbar item into place...");
                    Inventory.dragAndDrop(ItemMapping.getPosition(itemName), targetSlot);
                } else {
                    System.out.println("Hotbar item placed. Moving to next...");
                    currentHotbarSlot++;
                }
            } else if (menuName.contains("Edit Quick Buy")) {
                if (currentQuickBuySlot >= Config.quickBuy.size()) {
                    abort();
                    return;
                }

                int row = currentQuickBuySlot / 7;
                int col = currentQuickBuySlot % 7;
                int targetSlot = (row + 1) * 9 + (col + 1);
                String itemName = Config.quickBuy.get(currentQuickBuySlot);

                if (Inventory.checkSlot(openContainer.getLowerChestInventory(), targetSlot, itemName) == null) {
                    System.out.println("Adding item to quick buy...");
                    Inventory.click(targetSlot, 0);
                } else {
                    currentQuickBuySlot++;
                }
            } else if (menuName.contains("Adding to Quick Buy")) {
                int itemSlot = Inventory.findInContainer(Config.quickBuy.get(currentQuickBuySlot));

                if (itemSlot != -1) {
                    Inventory.click(itemSlot, 0);
                } else {
                    int nextPage = Inventory.findInContainer("Next Page");
                    if (nextPage != -1) Inventory.click(nextPage, 0);
                }
            } else {
                if (hotbarManager != -1 && !doneHotbar) {
                    Inventory.click(hotbarManager, 0);
                } else if (editQuickBuy != -1 && doneHotbar) {
                    Inventory.click(editQuickBuy, 0);
                } else if (settingsSlot != -1) {
                    Inventory.click(settingsSlot, 0);
                }
            }
        } else {
            Map.Entry<Integer, ItemStack> shopSlot = Inventory.findInHotbar("Bed Wars Menu & Shop");

            if (shopSlot == null) {
                System.out.println("Could not find shop item!");
                abort();
                return;
            }

            if (shopSlot.getValue().equals(Inventory.getCurrentItem())) {
                Inventory.useCurrentItem();
            } else {
                Inventory.switchToItem(shopSlot.getKey());
            }
        }
    }
}