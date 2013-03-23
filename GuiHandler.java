package taigore.inventorysaver;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import taigore.inventorysaver.client.GuiBag;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class GuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int entityId, int dummy, int dummier)
	{
		if(ID == 1)
		{
			EntityBag clickedBag = (EntityBag)world.getEntityByID(entityId);
			
			PacketDispatcher.sendPacketToPlayer(Packet250Helper.makeInventorySyncPacket(clickedBag), (Player)player);
			
			return new ContainerBag(player.inventory, clickedBag);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int entityId, int dummy, int dummier)
	{
		if(ID == 1)
		{
			EntityBag clickedBag = (EntityBag)world.getEntityByID(entityId);
			
			return new GuiBag(player.inventory, clickedBag);
		}
		return null;
	}
}
