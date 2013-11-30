package taigore.inventorysaver.proxy;

import taigore.inventorysaver.bag.EntityFallingBag;
import taigore.inventorysaver.bag.client.BlockRenderBag;
import taigore.inventorysaver.bag.client.RenderFallingBag;
import taigore.inventorysaver.main.InventorySaver;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ProxyClient extends ProxyCommon
{
	//Client only
	@Override
	public void registerBlockRender()
	{
	    if(InventorySaver.instance.configuration.bag.available())
	        RenderingRegistry.registerBlockHandler(new BlockRenderBag());
	    else
	        InventorySaver.log.info("Skipped block renderer registration: no bag block available");
	}
	
	@Override
	public void registerEntities()
	{
	    super.registerEntities();
	    
	    if(InventorySaver.instance.configuration.bag.available())
	        RenderingRegistry.registerEntityRenderingHandler(EntityFallingBag.class, new RenderFallingBag());
	    else
	        InventorySaver.log.info("Skipped entity renderer registration: no bag block available");
	}
}
