package taigore.inventorysaver.client;

import net.minecraftforge.client.MinecraftForgeClient;
import taigore.inventorysaver.EntityBag;
import taigore.inventorysaver.ProxyCommonInvSaver;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ProxyClientInvSaver extends ProxyCommonInvSaver
{
	//Client only
	@Override
	public void registerRenderers()
	{
		//Texture loading
		MinecraftForgeClient.preloadTexture(BAG_TEXTURE);
		MinecraftForgeClient.preloadTexture(BAGGUI_TEXTURE);
		
		//Rendering registration
		RenderingRegistry.registerEntityRenderingHandler(EntityBag.class, new RenderBag());
	}
}
