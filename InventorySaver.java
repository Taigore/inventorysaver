package taigore.inventorysaver;

import java.util.ArrayList;

import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;

@Mod(modid="Taigore_InventorySaver", useMetadata=true)
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
public class InventorySaver
{
    // The instance of your mod that Forge uses.
	@Mod.Instance("Taigore_InventorySaver")
	public static InventorySaver instance;
	
	// Says where the client and server 'proxy' code is loaded.
	@SidedProxy(clientSide="taigore.inventorysaver.client.ProxyClientInvSaver", serverSide="taigore.inventorysaver.ProxyCommonInvSaver")
	public static ProxyCommonInvSaver proxy;
	
	@Mod.Init
	public void Initialization(FMLInitializationEvent event)
	{
		EntityRegistry.registerModEntity(EntityBag.class, "entity.bag", 1, instance, 160, Integer.MAX_VALUE, false);
		
		proxy.registerRenderers();
		
		MinecraftForge.EVENT_BUS.register(new InvSaverEventHandler());
		NetworkRegistry.instance().registerGuiHandler(this, new BagGuiHandler());
	}
}