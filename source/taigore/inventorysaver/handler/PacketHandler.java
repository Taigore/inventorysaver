package taigore.inventorysaver.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import taigore.inventorysaver.entity.item.EntityBag;
import taigore.inventorysaver.handler.Packet250Helper.InventorySync;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler implements IPacketHandler
{
	public static final String chanInventorySync = "TgrInvSvrInvSync";
	
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
	{
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		{
			if(packet.channel.equals(chanInventorySync))
			{
				this.handleInventorySync(Packet250Helper.decodeInventorySync(packet));
			}
		}
	}

	private void handleInventorySync(InventorySync toRelay)
	{
		Entity receiver = Minecraft.getMinecraft().theWorld.getEntityByID(toRelay.entityId);
		
		if(EntityBag.class.isInstance(receiver))
		{
			((EntityBag)receiver).inventory.readFromNBT(toRelay.inventoryData);
		}
	}
}
