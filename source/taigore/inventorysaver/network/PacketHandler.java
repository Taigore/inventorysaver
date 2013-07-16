package taigore.inventorysaver.network;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import taigore.inventorysaver.network.packet.Packet250BagInventory;
import taigore.inventorysaver.network.packet.Packet250DeathUpdate;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler
{
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
	{
	    World playerWorld = ((EntityClientPlayerMP)player).worldObj;
	    
		if(playerWorld.isRemote)
		{
			if(Packet250DeathUpdate.isPacketThisType(packet))
			    Packet250DeathUpdate.updateDeathPosition(packet);
			
			else if(Packet250BagInventory.isPacketThisType(packet))
			    Packet250BagInventory.syncInventory(playerWorld, packet);
		}
	}
}
