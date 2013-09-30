package taigore.inventorysaver;

import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Configuration;
import taigore.inventorysaver.block.BlockBag;
import taigore.inventorysaver.common.ConfigManager;
import taigore.inventorysaver.common.ConfigManager.BlockRegistration;
import taigore.inventorysaver.common.ConfigManager.ItemRegistration;
import taigore.inventorysaver.common.ConfigManager.SettingRegistration;
import taigore.inventorysaver.constants.Reference;
import taigore.inventorysaver.item.ItemDeathCompass;
import taigore.inventorysaver.network.PacketHandler;
import taigore.inventorysaver.network.packet.Packet250DeathUpdate;
import taigore.inventorysaver.proxy.ProxyCommon;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;

@Mod
(
    modid = Reference.modID,
    useMetadata = true    
)
@NetworkMod
(
	clientSideRequired = true,
	serverSideRequired = false,
	clientPacketHandlerSpec = 
		@SidedPacketHandler
		(
			packetHandler=PacketHandler.class,
			channels={Packet250DeathUpdate.channel}
		)
)
public class InventorySaver
{
    public static String resource(String resourcePath) { return String.format("%s:%s", Reference.assetsKey, resourcePath); }
    public static ResourceLocation resourceLoc(String resourcePath) { return new ResourceLocation(Reference.assetsKey, resourcePath); }
    
    @Mod.Instance(Reference.modID)
	public static InventorySaver instance;
	
	@SidedProxy(serverSide=Reference.proxyCommon, clientSide=Reference.proxyClient)
	public static ProxyCommon proxy;
	
	public static Logger log = Logger.getLogger(Reference.modID);
	
	/////////////////
	// Mod settings
	/////////////////
	public ConfigManager configManager;
	
	public SettingRegistration<Boolean> protectLoot;
	public SettingRegistration<Boolean> bagGravity;
	public SettingRegistration<Boolean> ignoreLava;
	public SettingRegistration<Integer> cleanupTime;
	
	@Mod.EventHandler
	public void configSetup(FMLPreInitializationEvent event)
	{
	    InventorySaver.log.info("Reading config file");
	    
	    this.configManager = new ConfigManager(new Configuration(event.getSuggestedConfigurationFile()), log);
	    
	    this.protectLoot = this.configManager.new SettingRegistration("ProtectLoot", "Bag", false);
	    this.protectLoot.comment = "Prevents the bag from interacting with someone that's not the owner";
	    
	    this.bagGravity = 
	    		this.configManager.new SettingRegistration("Gravity", "Bag", true)
				{
	    			@Override
	    			public Boolean getValue()
	    			{
	    				if(!InventorySaver.instance.protectLoot.getValue())
	    					return (Boolean)super.getValue();
	    				else
	    					return false;
	    			}
				};
		this.bagGravity.comment = "If gravity affects the bag. If ProtectLoot is true, this is overridden as false";
	    
	    this.ignoreLava =
	    		this.configManager.new SettingRegistration("IgnoreLava", "Bag", true)
			    {
			    	@Override
			    	public Boolean getValue()
			    	{
			    		if(!InventorySaver.instance.protectLoot.getValue())
			    			return (Boolean)super.getValue();
			    		else
			    			return false;
			    	}
			    };
		this.ignoreLava.comment = "If true, the bag is unaffected by lava. If ProtectLoot is true, this is overridden as true";
		
		this.cleanupTime =
				this.configManager.new SettingRegistration("CleanupTime", "Bag", 0);
	    
	    this.bag =
	    		this.configManager.new BlockRegistration("Bag")
			    {
					@Override
					public Block instantiateBlock(int blockID) { return new BlockBag(blockID); }
			    };
			    
		this.compass =
				this.configManager.new ItemRegistration("DeathCompass")
				{
					@Override
					public Item instantiateItem(int itemID) { return new ItemDeathCompass(itemID); }
				};
				
		this.configManager.doRegistrations();
	    /*
	    this.settings.init("Item:DeathCompass", null, 4000);
        this.settings.alias("Item:DeathCompass", "DeathCompassID");
        
        this.settings.init("Block:Bag", null, 300);
        this.settings.alias("Block:Bag", "BagBlockID");
	    
	    this.settings.init("Bag:ProtectLoot", , false);
	    this.settings.alias("Bag:ProtectLoot", "ProtectLoot");
	    
	    this.settings.init("Bag:Gravity", "If gravity affects the bag. If ProtectLoot is true, this is overridden as false", true);
	    this.settings.alias("Bag:Gravity", "BagGravity");
	    
	    this.settings.init("Bag:IgnoreLava", "If true, the bag is unaffected by lava. If ProtectLoot is true, this is overridden as true", false);
	    this.settings.alias("Bag:IgnoreLava", "IgnoreLava");
	    
	    this.settings.save();*/
	}
	
	///////////////////
	// Items & Blocks
	///////////////////
	public BlockRegistration<BlockBag> bag;
    public ItemRegistration<ItemDeathCompass> compass;
	
	@Mod.EventHandler
	public void initialization(FMLInitializationEvent event)
	{
		proxy.registerEntities();
		proxy.registerHandlers();
		proxy.registerBlockRender();
	}
}