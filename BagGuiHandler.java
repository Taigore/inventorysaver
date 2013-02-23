package taigore.inventorysaver;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import taigore.inventorysaver.client.GuiBag;
import cpw.mods.fml.common.network.IGuiHandler;

public class BagGuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == 1)
		{
			List<EntityBag> bagsInTheBlock = world.getEntitiesWithinAABB(EntityBag.class, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1));
			
			for(EntityBag toCheck : bagsInTheBlock)
			{
				if(toCheck.clicker == player)
					return new ContainerBag(player.inventory, toCheck);
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == 1)
		{
			List<EntityBag> bagsInTheBlock = world.getEntitiesWithinAABB(EntityBag.class, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1));
			
			for(EntityBag toCheck : bagsInTheBlock)
			{
				if(toCheck.clicker == player)
				{
					toCheck.clicker = null;
					
					try
					{
						byte[] outgoingData;
						ByteArrayOutputStream byteWriter = new ByteArrayOutputStream();
						DataOutputStream dataConverter = new DataOutputStream(byteWriter);
						
						
					}
					catch(Exception exc)
					{
						
					}
					
					return new GuiBag(player.inventory, toCheck);
				}
			}
		}
		return null;
	}
}
