package taigore.inventorysaver;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import taigore.inventorysaver.Packet250Helper.EntityPing;
import taigore.inventorysaver.Packet250Helper.InventorySync;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler implements IPacketHandler
{
	public static final String chanInventorySync = "TgrInvSvrInvSync";
	public static final String chanEntityPing 	 = "TgrInvSvrEntPing";
	
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
		else if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
		{
			if(packet.channel.equals(chanEntityPing))
			{
				this.handleEntityPing(Packet250Helper.decodeEntityPing(packet), (EntityPlayer)player);
			}
		}
	}

	private void handleInventorySync(InventorySync toRelay)
	{
		Entity receiver = Minecraft.getMinecraft().theWorld.getEntityByID(toRelay.entityId);
		
		if(EntityBag.class.isInstance(receiver))
		{
			//What was saved in the bag is outdated anyway
			((EntityBag)receiver).inventory.inventory.clear();
			((EntityBag)receiver).inventory.inventory.addAll(toRelay.inventory);
		}
	}
	
	private void handleEntityPing(EntityPing toRelay, EntityPlayer sender)
	{
		Entity receiver = sender.worldObj.getEntityByID(toRelay.entityId);
		
		if(EntityBag.class.isInstance(receiver))
		{
			((EntityBag)receiver).inventory.switchPage();
		}
	}
}
