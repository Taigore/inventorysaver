package taigore.inventorysaver.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import taigore.inventorysaver.entity.item.EntityBag;

import net.minecraft.crash.CrashReport;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.ReportedException;

public class Packet250Helper extends Packet250CustomPayload
{
	private Packet250Helper() {}

	public static Packet250CustomPayload makeInventorySyncPacket(EntityBag toSync)
	{
		return new Packet250CustomPayload(PacketHandler.chanInventorySync, encodeInventory(toSync));
	}
	
	private static byte[] encodeInventory(EntityBag toEncode)
	{
		if(toEncode != null)
		{
			byte[] outputData = null;
			ByteArrayOutputStream byteWriter = new ByteArrayOutputStream();
			DataOutputStream dataConverter = new DataOutputStream(byteWriter);
			
			try
			{
				try
				{
					//Specifications:
					//int - entityID
					//NBTTagCompound - Inventory in NBT format
					dataConverter.writeInt(toEncode.entityId);
					
					Packet.writeNBTTagCompound(toEncode.inventory.writeToNBT(new NBTTagCompound()), dataConverter);
					
					dataConverter.flush();
					outputData = byteWriter.toByteArray();
				}
				finally
				{
					if(dataConverter != null) dataConverter.close();
					if(byteWriter != null) byteWriter.close();
				}
			}
			catch(Exception exc)
			{
				CrashReport rep = CrashReport.makeCrashReport(exc, "Taigore InventorySaver: failed an I/O operation while creating a custom packet");
				throw new ReportedException(rep);
			}
			
			return outputData;
		}
		else throw new IllegalArgumentException("Taigore InventorySaver: invalid entity reference for packet factory");
	}
	
	public static InventorySync decodeInventorySync(Packet250CustomPayload toDecode)
	{
		if(toDecode.channel.equals(PacketHandler.chanInventorySync))
		{
			InventorySync decodedData;
			ByteArrayInputStream byteReader = new ByteArrayInputStream(toDecode.data);
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

					decodedData = new InventorySync(entityID, inventoryData);
				}
				finally
				{
					if(dataConverter != null) dataConverter.close();
					if(byteReader != null) byteReader.close();
				}
			}
			catch(Exception exc)
			{
				CrashReport rep = CrashReport.makeCrashReport(exc, "Taigore InventorySaver: failed an I/O operation while reading a custom packet");
				throw new ReportedException(rep);
			}
			
			return decodedData;
		}
		else throw new IllegalArgumentException(String.format("Taigore InventorySaver: unknown packet of channel \"%s\" given for processing", toDecode.channel));
	}

	public static class InventorySync
	{
	    public final int entityId;
		public final NBTTagCompound inventoryData;
		
		public InventorySync(int entityId, NBTTagCompound inventoryData)
		{
			this.entityId = entityId;
			this.inventoryData = inventoryData;
		}
	}
}
