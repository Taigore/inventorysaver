package taigore.inventorysaver;

import java.util.logging.Logger;

import net.minecraftforge.common.Configuration;
import taigore.inventorysaver.block.BlockBag;
import taigore.inventorysaver.common.ModSettings;
import taigore.inventorysaver.common.ModSettings.DebugLevel;
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
    modid=InventorySaver.modID,
    useMetadata=true    
)
@NetworkMod
(
	clientSideRequired=true,
	serverSideRequired=false,
	clientPacketHandlerSpec=@SidedPacketHandler
	(
		packetHandler=PacketHandler.class,
		channels={Packet250DeathUpdate.channel}
	)
)
public class InventorySaver
{
    static public final String modID = "Taigore_InventorySaver";
    
    public static String resource(String resourcePath) { return String.format("%s:%s", modID.toLowerCase(), resourcePath); }
    
    @Mod.Instance(InventorySaver.modID)
	public static InventorySaver instance;
	
	@SidedProxy(serverSide="taigore.inventorysaver.proxy.ProxyCommon", clientSide="taigore.inventorysaver.proxy.ProxyClient")
	public static ProxyCommon proxy;
	
	public static Logger log = Logger.getLogger(modID);
	
	/////////////////
	// Mod settings
	/////////////////
	public ModSettings settings = null;
	
	@Mod.EventHandler
	public void configSetup(FMLPreInitializationEvent event)
	{
	    InventorySaver.log.info("Reading config file");
	    
	    this.settings = new ModSettings(new Configuration(event.getSuggestedConfigurationFile()), DebugLevel.MODSETTINGS);
	    
	    this.settings.init("Item:DeathCompass", null, 4000);
        this.settings.alias("Item:DeathCompass", "DeathCompassID");
        
        this.settings.init("Block:Bag", null, 300);
        this.settings.alias("Block:Bag", "BagBlockID");
	    
	    this.settings.init("Bag:ProtectLoot", "Prevents the bag from interacting with someone that's not the owner", false);
	    this.settings.alias("Bag:ProtectLoot", "ProtectLoot");
	    
	    this.settings.init("Bag:Gravity", "If gravity affects the bag. If ProtectLoot is true, this is overridden as false", true);
	    this.settings.alias("Bag:Gravity", "BagGravity");
	    
	    this.settings.init("Bag:IgnoreLava", "If true, the bag is unaffected by lava. If ProtectLoot is true, this is overridden as true", false);
	    this.settings.alias("Bag:IgnoreLava", "IgnoreLava");
	    
	    this.settings.save();
	}
	
	///////////////////
	// Items & Blocks
	///////////////////
	public BlockBag blockBag = null;
	
    public ItemDeathCompass itemDeathCompass = null;
	
	@Mod.EventHandler
	public void initialization(FMLInitializationEvent event)
	{
	    proxy.registerItems();
		proxy.registerBlocks();
		proxy.registerEntities();
		proxy.registerHandlers();
	}
}