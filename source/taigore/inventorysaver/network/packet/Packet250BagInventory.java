package taigore.inventorysaver.network.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import taigore.inventorysaver.entity.item.EntityBag;
import taigore.inventorysaver.inventory.InventoryBag;
import cpw.mods.fml.common.FMLLog;

public class Packet250BagInventory extends Packet250CustomPayload
{
    public static final String channel = "TgrInvSvrInvSync";

	public static Packet250CustomPayload makeInventorySyncPacket(EntityBag toSync)
	{
	    Packet250CustomPayload returnValue = null;
	    
		if(toSync != null)
		{
			ByteArrayOutputStream byteWriter = new ByteArrayOutputStream();
			DataOutputStream dataConverter = new DataOutputStream(byteWriter);
			
			try
			{
				try
				{
				    byte[] outputData = null;
				    
					//Specifications:
					//int - entityID
					//NBTTagCompound - Inventory in NBT format
					dataConverter.writeInt(toSync.entityId);
					
					Packet.writeNBTTagCompound(toSync.inventory.writeToNBT(new NBTTagCompound()), dataConverter);
					
					dataConverter.flush();
					outputData = byteWriter.toByteArray();
					returnValue = new Packet250CustomPayload(channel, outputData);
				}
				finally
				{
					if(dataConverter != null)
					    dataConverter.close();
					if(byteWriter != null)
					    byteWriter.close();
				}
			}
			catch(Exception e)
            {
                FMLLog.info("Taigore InventorySaver: exception while writing packet %s", Packet250DeathUpdate.class.getName());
                e.printStackTrace();
                returnValue = null;
            }
		}
		
		return returnValue;
	}
	
	public static void syncInventory(World bagWorld, Packet250CustomPayload inventoryPacket)
	{
		if(bagWorld != null && isPacketThisType(inventoryPacket))
		{
			ByteArrayInputStream byteReader = new ByteArrayInputStream(inventoryPacket.data);
			DataInputStream dataConverter = new DataInputStream(byteReader);
			
			try
			{
				try
				{
					//Specifications:
					//First int - entityID
					//NBTTagCompound - Inventory in NBT format
					int entityID = dataConverter.readInt();
					NBTTagCompound inventoryData = Packet.readNBTTagCompound(dataConverter);
					
					Entity bag = bagWorld.getEntityByID(entityID);
					
					if(EntityBag.class.isInstance(bag))
					{
					    InventoryBag bagInventory = ((EntityBag)bag).inventory;
					    bagInventory.readFromNBT(inventoryData);
					}
				}
				finally
				{
					if(dataConverter != null)
					    dataConverter.close();
					if(byteReader != null)
					    byteReader.close();
				}
			}
			catch(Exception e)
            {
                FMLLog.info("Taigore InventorySaver: exception while syncing bag inventory");
                e.printStackTrace();
            }
		}
		else
		if(bagWorld != null && inventoryPacket != null)
            FMLLog.info("Taigore InventorySaver: invalid packet with channel %s handled by %s class.\n%s", inventoryPacket.channel, Packet250DeathUpdate.class.toString(), String.valueOf(inventoryPacket));
	}
	
	public static boolean isPacketThisType(Packet250CustomPayload toCheck)
    {
        return toCheck != null && toCheck.channel.equals(channel);
    }
	
	private Packet250BagInventory() {}
}
