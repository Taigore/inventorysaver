package taigore.inventorysaver.main;

import java.util.logging.Logger;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Configuration;
import taigore.inventorysaver.common.configuration.ForgeConfiguration;
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
	public Settings configuration;
	
	@Mod.EventHandler
	public void configSetup(FMLPreInitializationEvent event)
	{
		ForgeConfiguration configHandler = new ForgeConfiguration(new Configuration(event.getSuggestedConfigurationFile())); 
		
	    configuration = new Settings(configHandler);
	    
	    configHandler.registerAll();
	}
	
	///////////////////
	// Items & Blocks
	///////////////////
	@Mod.EventHandler
	public void initialization(FMLInitializationEvent event)
	{
		proxy.registerEntities();
		proxy.registerHandlers();
		proxy.registerBlockRender();
	}
}