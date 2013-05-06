package taigore.inventorysaver;

import taigore.inventorysaver.entity.item.EntityBag;
import taigore.inventorysaver.handler.EventHandler;
import taigore.inventorysaver.handler.GuiHandler;
import taigore.inventorysaver.handler.PacketHandler;
import taigore.inventorysaver.proxy.ProxyCommon;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;

@Mod
(
	modid="Taigore_InventorySaver",
	useMetadata=true
)
@NetworkMod
(
	clientSideRequired=true,
	serverSideRequired=false,
	clientPacketHandlerSpec=@SidedPacketHandler
	(
		packetHandler=PacketHandler.class,
		channels={PacketHandler.chanInventorySync}
	)
)
public class InventorySaver
{
    
	@Mod.Instance("Taigore_InventorySaver")
	public static InventorySaver instance;
	
	@SidedProxy(clientSide="taigore.inventorysaver.client.ProxyClient", serverSide="taigore.inventorysaver.ProxyCommon")
	public static ProxyCommon proxy;
	
	/////////////////
	// Mod settings
	/////////////////
	public Configuration configFile;
	
	//System properties
	public boolean onlyOwnerLoots;     //If a bag can only be emptied by its owner
	
	//Bag properties
	public boolean bagIgnoresLava;     //If the bag gets destroyed by lava
	
	@Mod.PreInit
	public void configSetup(FMLPreInitializationEvent event)
	{
	    this.configFile = new Configuration(event.getSuggestedConfigurationFile());
	    this.configFile.load();
	    
	    //System properties
	    {
	        Property onlyOwnerLoots = this.configFile.get("General", "Protect bag", false);
	        this.onlyOwnerLoots = onlyOwnerLoots.getBoolean(false);
	        onlyOwnerLoots.comment = "Protects the bag from damage and fixes its position to prevent griefing.";
	        onlyOwnerLoots.comment+= "\nOnly the owner can make it drop items and open the interface.";
	    }
	    
	    //Bag properties;
	    {
    	    Property bagIgnoresLava = this.configFile.get("Bag properties", "Ignore lava", false);
    	    this.bagIgnoresLava = bagIgnoresLava.getBoolean(false) || this.onlyOwnerLoots;
    	    bagIgnoresLava.comment = "If the bag can fall in lava without getting damaged.";
    	    bagIgnoresLava.comment+= "\nIt will be submerged though, so retrieving it may be difficult.";
    	    bagIgnoresLava.comment+= "\nOverwritten by \"Protect bag\" if true.";
	    }
	    
	    this.configFile.save();
	}
	
	@Mod.Init
	public void initialization(FMLInitializationEvent event)
	{
		EntityRegistry.registerModEntity(EntityBag.class, "entity.bag", 1, instance, 160, Integer.MAX_VALUE, false);
		
		proxy.registerRenderers();
		
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		NetworkRegistry.instance().registerGuiHandler(this, new GuiHandler());
	}
}