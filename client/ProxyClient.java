package taigore.inventorysaver.client;

import net.minecraftforge.client.MinecraftForgeClient;
import taigore.inventorysaver.EntityBag;
import taigore.inventorysaver.ProxyCommon;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ProxyClient extends ProxyCommon
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
