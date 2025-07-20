package com.github.notzyrex.hypixelinvsetup;

import com.github.notzyrex.hypixelinvsetup.commands.InvSetupCommand;
import com.github.notzyrex.hypixelinvsetup.main.SetupManager;
import com.github.notzyrex.hypixelinvsetup.util.Config;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid = "hypixelinvsetup", useMetadata=true)
public class InvSetup {
    InvSetupCommand setup = new InvSetupCommand();
    Minecraft mc = Minecraft.getMinecraft();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        Config.load();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        ClientCommandHandler.instance.registerCommand(setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        if (mc.thePlayer == null && mc.theWorld == null) {
            SetupManager.abort();
            return;
        }

        if (SetupManager.isRunning()) {
            SetupManager.update();
        }
    }

    @SubscribeEvent
    public void onGuiScreenActionPerformed(TickEvent.ClientTickEvent event) {
        SetupManager.onGuiTick();
    }
}