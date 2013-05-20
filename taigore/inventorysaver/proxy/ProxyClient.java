package taigore.inventorysaver.proxy;

import taigore.inventorysaver.client.entity.render.RenderBag;
import taigore.inventorysaver.entity.item.EntityBag;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ProxyClient extends ProxyCommon
{
	//Client only
	@Override
	public void registerRenderers()
	{
		//Rendering registration
		RenderingRegistry.registerEntityRenderingHandler(EntityBag.class, new RenderBag());
	}
}
