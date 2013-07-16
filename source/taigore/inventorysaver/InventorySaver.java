package taigore.inventorysaver;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import taigore.inventorysaver.entity.item.EntityBag;
import taigore.inventorysaver.handler.EventHandler;
import taigore.inventorysaver.handler.GuiHandler;
import taigore.inventorysaver.handler.TickHandler;
import taigore.inventorysaver.item.ItemDeathCompass;
import taigore.inventorysaver.network.PacketHandler;
import taigore.inventorysaver.network.packet.Packet250BagInventory;
import taigore.inventorysaver.network.packet.Packet250DeathUpdate;
import taigore.inventorysaver.proxy.ProxyCommon;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod
(
	modid=InventorySaver.modId,
	useMetadata=true
)
@NetworkMod
(
	clientSideRequired=true,
	serverSideRequired=false,
	clientPacketHandlerSpec=@SidedPacketHandler
	(
		packetHandler=PacketHandler.class,
		channels={Packet250DeathUpdate.channel, Packet250BagInventory.channel}
	)
)
public class InventorySaver
{
    public static final String modId = "Taigore_InventorySaver";
    
    public static String getResourceId(String resourcePath) { return String.format("%s:%s", modId.toLowerCase(), resourcePath); }
    
	@Mod.Instance("Taigore_InventorySaver")
	public static InventorySaver instance;
	
	@SidedProxy(clientSide="taigore.inventorysaver.proxy.ProxyClient", serverSide="taigore.inventorysaver.proxy.ProxyCommon")
	public static ProxyCommon proxy;
	
	/////////////////
	// Mod settings
	/////////////////
	public Configuration configFile;
	
	//System properties
	public boolean onlyOwnerLoots;     //If a bag can only be emptied by its owner
	public boolean canOpsLoot;         //If OPs can access a bag's contents regardless of settings
	
	//Bag properties
	public boolean bagIgnoresLava;     //If the bag gets destroyed by lava
	
	@Mod.EventHandler
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
	    {
	        Property canOpsLoot = this.configFile.get("General", "OPs override", true);
	        this.canOpsLoot = canOpsLoot.getBoolean(true);
	        
	        canOpsLoot.comment = "If true, OPs can access any bag, even if 'Protect bag' is true.";
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
	
	//////////
	// Items
	//////////
	public ItemDeathCompass deathCompass;
	
	@Mod.EventHandler
	public void initialization(FMLInitializationEvent event)
	{
		EntityRegistry.registerModEntity(EntityBag.class, "entity.bag", 1, instance, 160, Integer.MAX_VALUE, false);
		proxy.registerRenderers();
		
		this.deathCompass = new ItemDeathCompass(1000);
		
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		NetworkRegistry.instance().registerGuiHandler(this, new GuiHandler());
		TickRegistry.registerTickHandler(new TickHandler(), Side.SERVER);
	}
}