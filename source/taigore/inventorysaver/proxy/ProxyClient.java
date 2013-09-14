package taigore.inventorysaver.proxy;

import taigore.inventorysaver.InventorySaver;
import taigore.inventorysaver.block.render.BlockRenderBag;
import taigore.inventorysaver.entity.EntityFallingBag;
import taigore.inventorysaver.entity.render.RenderFallingBag;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ProxyClient extends ProxyCommon
{
	//Client only
	@Override
	public void registerBlocks()
	{
	    super.registerBlocks();
	    
	    if(InventorySaver.instance.blockBag != null)
	        RenderingRegistry.registerBlockHandler(new BlockRenderBag());
	    else
	        InventorySaver.log.info("Skipped block renderer registration: no block bag available");
	}
	
	@Override
	public void registerEntities()
	{
	    super.registerEntities();
	    
	    if(InventorySaver.instance.blockBag != null)
	        RenderingRegistry.registerEntityRenderingHandler(EntityFallingBag.class, new RenderFallingBag());
	    else
	        InventorySaver.log.info("Skipped entity renderer registration: no block bag available");
	}
}
