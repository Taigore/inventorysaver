package taigore.inventorysaver.client;

import net.minecraft.client.Minecraft;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import taigore.inventorysaver.EntityBag;
import taigore.inventorysaver.Packet250BagSync;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class InvSaverClientPacketHandler implements IPacketHandler
{
	public static final String chanBagInventorySync = "TgrInvsvrBagcont";
	
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
	{
		if(packet.channel.equals(chanBagInventorySync))
		{
			this.relayPacket(Packet250BagSync.decodePacket(packet));
		}
		else
			FMLLog.warning("Taigore InventorySaver: handled unknown packet with chanBagInventorySync %s", packet.channel);
	}

	private void relayPacket(Packet250BagSync toRelay)
	{
		EntityBag receiver = (EntityBag)Minecraft.getMinecraft().theWorld.getEntityByID(toRelay.entityID);
		
		if(receiver != null)
		{
			//What was saved in the bag is outdated anyway
			receiver.bagContents.clear();
			receiver.bagContents.addAll(toRelay.inventory);
		}
	}
}
