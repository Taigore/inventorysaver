package taigore.inventorysaver.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import taigore.inventorysaver.bag.ContainerBag;
import taigore.inventorysaver.bag.TileEntityBag;
import taigore.inventorysaver.bag.client.GuiBag;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int arg1, int arg2, int arg3)
	{
		if(ID == 1) //Arg1 = x, arg2 = y, arg3 = z
		{
			final TileEntityBag clickedBag = (TileEntityBag)world.getBlockTileEntity(arg1, arg2, arg3);
			
			return new ContainerBag(player.inventory, clickedBag.inventory_);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int arg1, int arg2, int arg3)
	{
		if(ID == 1)
		{
		    final TileEntityBag clickedBag = (TileEntityBag)world.getBlockTileEntity(arg1, arg2, arg3);
			
			return new GuiBag(player.inventory, clickedBag.inventory_);
		}
		return null;
	}
}
