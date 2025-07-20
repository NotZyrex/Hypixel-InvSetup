package com.github.notzyrex.hypixelinvsetup.util;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

import java.util.AbstractMap;
import java.util.Map;

public class Inventory {
    public static final Minecraft mc = Minecraft.getMinecraft();

    public static ItemStack getCurrentItem() {
        return mc.thePlayer.getHeldItem();
    }

    public static void useCurrentItem() {
        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(getCurrentItem()));
    }

    public static void switchToItem(int slot) {
        mc.thePlayer.inventory.currentItem = slot;
    }

    public static ItemStack checkSlot(IInventory inventory, int slot, String match) {
        ItemStack stack = inventory.getStackInSlot(slot);

        if (stack != null && stack.getDisplayName().contains(match)) {
            return stack;
        }

        return null;
    }

    public static Map.Entry<Integer, ItemStack> findInHotbar(String match) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = checkSlot(mc.thePlayer.inventory, i, match);
            if (stack != null) {
                return new AbstractMap.SimpleEntry<>(i, stack);
            }
        }

        return null;
    }

    public static Container getOpenContainer() {
        Container container = mc.thePlayer.openContainer;
        return (container instanceof ContainerChest ? container : null);
    }

    public static int findInContainer(String match) {
        ContainerChest openContainer = (ContainerChest) getOpenContainer();
        if (openContainer == null) return -1;

        IInventory chestInventory = openContainer.getLowerChestInventory();
        int size = chestInventory.getSizeInventory();

        for (int i = 0; i < size; i++) {
            ItemStack stack = checkSlot(chestInventory, i, match);
            if (stack != null) {
                return i;
            }
        }

        return -1;
    }

    public static void click(int slot, int mode) {
        if (getOpenContainer() == null) return;

        mc.playerController.windowClick(getOpenContainer().windowId, slot, 0, mode, mc.thePlayer);
    }

    public static void dragAndDrop(int sourceSlot, int targetSlot) {
        click(sourceSlot, 0);
        click(targetSlot, 0);
    }
}
