package taigore.inventorysaver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.crash.CrashReport;
import net.minecraft.item.ItemStack;
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
	public static Packet250CustomPayload makeEntityPingPacket(EntityBag toPing)
	{
		return new Packet250CustomPayload(PacketHandler.chanEntityPing, encodePing(toPing));
	}
	private static byte[] encodePing(EntityBag toEncode)
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
					//First int - entityID
					dataConverter.writeInt(toEncode.entityId);
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
					//First int - entityID
					//Second int - amount of stacks
					//Subsequent data - item stacks in NBT format
					dataConverter.writeInt(toEncode.entityId);
					dataConverter.writeInt(toEncode.inventory.inventory.size());
					
					for(ItemStack toSend : toEncode.inventory.inventory)
					{
						Packet.writeNBTTagCompound(toSend.writeToNBT(new NBTTagCompound()), dataConverter);
					}
					
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
					//Second int - amount of stacks
					//Subsequent data - item stacks in NBT format
					int entityID = dataConverter.readInt();
					int stacksAmount = dataConverter.readInt();
					List<ItemStack> inventory = new LinkedList();
					
					for(int i = 0; i < stacksAmount; i++)
					{
						NBTTagCompound itemNBT = Packet.readNBTTagCompound(dataConverter);
						
						inventory.add(ItemStack.loadItemStackFromNBT(itemNBT));
					}
					
					decodedData = new InventorySync(entityID, inventory);
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
	
	public static EntityPing decodeEntityPing(Packet250CustomPayload toDecode)
	{
		if(toDecode.channel.equals(PacketHandler.chanEntityPing))
		{
			EntityPing decodedData;
			ByteArrayInputStream byteReader = new ByteArrayInputStream(toDecode.data);
			DataInputStream dataConverter = new DataInputStream(byteReader);
			
			try
			{
				try
				{
					//Specifications:
					//First int - entityID
					//Second int - amount of stacks
					//Subsequent data - item stacks in NBT format
					int entityID = dataConverter.readInt();
					
					decodedData = new EntityPing(entityID);
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
	
	public static class EntityPing
	{
		public final int entityId;
		
		public EntityPing(int entityId)
		{
			this.entityId = entityId;
		}
	}
	
	public static class InventorySync extends EntityPing
	{
		public final List<ItemStack> inventory;
		
		public InventorySync(int entityId, List<ItemStack> inventory)
		{
			super(entityId);
			
			this.inventory = inventory;
		}
	}
}
