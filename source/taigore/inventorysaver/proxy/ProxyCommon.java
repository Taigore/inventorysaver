package taigore.inventorysaver.proxy;

import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;
import taigore.inventorysaver.InventorySaver;
import taigore.inventorysaver.block.BlockBag;
import taigore.inventorysaver.common.ModSettings;
import taigore.inventorysaver.entity.EntityFallingBag;
import taigore.inventorysaver.handler.EventHandler;
import taigore.inventorysaver.handler.GuiHandler;
import taigore.inventorysaver.handler.PlayerTracker;
import taigore.inventorysaver.item.ItemDeathCompass;
import taigore.inventorysaver.tileentity.TileEntityBag;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ProxyCommon
{
    public void registerBlocks()
    {
        InventorySaver.log.info("Registering blocks");
        
        //Block instantiation
        ModSettings settings = InventorySaver.instance.settings;
        
        Integer id = settings.get("BagBlockID", 0);
        
        if(id != null && 0 < id)
        {
            InventorySaver.instance.blockBag = new BlockBag(id);
            GameRegistry.registerBlock(InventorySaver.instance.blockBag, ItemBlock.class, "BlockBag");
            GameRegistry.registerTileEntity(TileEntityBag.class, "TInvSavBag");
        }
        else
            InventorySaver.log.info("Skipping bag block: invalid ID %s");
    }

    public void registerItems()
    {
        InventorySaver.log.info("Registering items");
        
        //Block instantiation
        ModSettings settings = InventorySaver.instance.settings;
        
        Integer id = settings.get("DeathCompassID", 0);
        
        if(id != null && 0 < id)
        {
            InventorySaver.instance.itemDeathCompass = new ItemDeathCompass(id);
            GameRegistry.registerItem(InventorySaver.instance.itemDeathCompass, "ItemDeathCompass");
            LanguageRegistry.addName(InventorySaver.instance.itemDeathCompass, "Death compass");
        }
        else
            InventorySaver.log.info("Skipping DeathCompass: invalid ID");
    }
    
    public void registerHandlers()
    {
        InventorySaver.log.info("Registering handlers");
        
        if(InventorySaver.instance.blockBag != null)
            NetworkRegistry.instance().registerGuiHandler(InventorySaver.instance, new GuiHandler());
        else
            InventorySaver.log.info("Skipping GUI Handler: no block bag available");
        
        if(InventorySaver.instance.blockBag != null || InventorySaver.instance.itemDeathCompass != null)
            MinecraftForge.EVENT_BUS.register(new EventHandler());
        else
            InventorySaver.log.info("Skipping Event Handler: no bag nor compass available");
        
        if(InventorySaver.instance.itemDeathCompass != null)
            GameRegistry.registerPlayerTracker(new PlayerTracker());
        else
            InventorySaver.log.info("Skipping Player Tracker: no compass available");
    }
    
    public void registerEntities()
    {
        InventorySaver.log.info("Registering entities");
        
        if(InventorySaver.instance.blockBag != null)
            EntityRegistry.registerModEntity(EntityFallingBag.class, "Bag", 0, InventorySaver.instance, 160, 20, true);
    }
}