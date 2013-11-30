package taigore.inventorysaver.proxy;

import net.minecraftforge.common.MinecraftForge;
import taigore.inventorysaver.bag.BagDropHandler;
import taigore.inventorysaver.bag.EntityFallingBag;
import taigore.inventorysaver.handler.CompassHandler;
import taigore.inventorysaver.handler.GuiHandler;
import taigore.inventorysaver.handler.PlayerTracker;
import taigore.inventorysaver.main.InventorySaver;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class ProxyCommon
{
    public void registerHandlers()
    {
        InventorySaver.log.info("Registering handlers");
        
        final boolean bagAvailable = InventorySaver.instance.configuration.bag.available();
        final boolean compassAvailable = InventorySaver.instance.configuration.deathCompass.available();
        
        if(bagAvailable)
        {
            NetworkRegistry.instance().registerGuiHandler(InventorySaver.instance, new GuiHandler());
            MinecraftForge.EVENT_BUS.register(new BagDropHandler());
        }
        else
        {
            InventorySaver.log.info("Skipping GUI Handler: no bag block available");
            InventorySaver.log.info("Skipping BagDropHandler: no bag block available");
        }
        
        if(compassAvailable)
        {
        	MinecraftForge.EVENT_BUS.register(new CompassHandler());
        	GameRegistry.registerPlayerTracker(new PlayerTracker());
        }
        else
        {
        	InventorySaver.log.info("Skipping CompassHandler: no compass available");
        	InventorySaver.log.info("Skipping Player Tracker: no compass available");
        }
    }
    
    public void registerBlockRender() {}
    
    public void registerEntities()
    {
        InventorySaver.log.info("Registering entities");
        
        final boolean bagAvailable = InventorySaver.instance.configuration.bag.available();
        
        if(bagAvailable)
            EntityRegistry.registerModEntity(EntityFallingBag.class, "Bag", 0, InventorySaver.instance, 160, 20, true);
    }
}