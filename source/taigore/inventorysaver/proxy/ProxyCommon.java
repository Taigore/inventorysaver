package taigore.inventorysaver.proxy;

import net.minecraftforge.common.MinecraftForge;
import taigore.inventorysaver.InventorySaver;
import taigore.inventorysaver.entity.EntityFallingBag;
import taigore.inventorysaver.handler.EventHandler;
import taigore.inventorysaver.handler.GuiHandler;
import taigore.inventorysaver.handler.PlayerTracker;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class ProxyCommon
{
    public void registerHandlers()
    {
        InventorySaver.log.info("Registering handlers");
        
        if(InventorySaver.instance.bag.isRegistered())
            NetworkRegistry.instance().registerGuiHandler(InventorySaver.instance, new GuiHandler());
        else
            InventorySaver.log.info("Skipping GUI Handler: no block bag available");
        
        if(InventorySaver.instance.bag.isRegistered() || InventorySaver.instance.compass.isRegistered())
            MinecraftForge.EVENT_BUS.register(new EventHandler());
        else
            InventorySaver.log.info("Skipping Event Handler: no bag nor compass available");
        
        if(InventorySaver.instance.compass.isRegistered())
            GameRegistry.registerPlayerTracker(new PlayerTracker());
        else
            InventorySaver.log.info("Skipping Player Tracker: no compass available");
    }
    
    public void registerBlockRender() {}
    
    public void registerEntities()
    {
        InventorySaver.log.info("Registering entities");
        
        if(InventorySaver.instance.bag.isRegistered())
            EntityRegistry.registerModEntity(EntityFallingBag.class, "Bag", 0, InventorySaver.instance, 160, 20, true);
    }
}