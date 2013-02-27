package taigore.inventorysaver;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
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
	),
	serverPacketHandlerSpec=@SidedPacketHandler
	(
		packetHandler=PacketHandler.class,
		channels={PacketHandler.chanEntityPing}
	)
)
public class InventorySaver
{
    // The instance of your mod that Forge uses.
	@Mod.Instance("Taigore_InventorySaver")
	public static InventorySaver instance;
	
	// Says where the client and server 'proxy' code is loaded.
	@SidedProxy(clientSide="taigore.inventorysaver.client.ProxyClient", serverSide="taigore.inventorysaver.ProxyCommon")
	public static ProxyCommon proxy;
	
	@Mod.Init
	public void Initialization(FMLInitializationEvent event)
	{
		EntityRegistry.registerModEntity(EntityBag.class, "entity.bag", 1, instance, 160, Integer.MAX_VALUE, false);
		
		proxy.registerRenderers();
		
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		NetworkRegistry.instance().registerGuiHandler(this, new GuiHandler());
	}
}