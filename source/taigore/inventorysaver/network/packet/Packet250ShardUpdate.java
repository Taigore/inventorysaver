package taigore.inventorysaver.network.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Set;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import taigore.inventorysaver.world.ShardPositions;
import cpw.mods.fml.common.FMLLog;

public class Packet250ShardUpdate extends Packet250CustomPayload
{
    public static final String channel = "TgrInvSvr_Shards";
    
    public static Packet250CustomPayload makeForAllTracked(World toUpdate) { return makeForAllTracked(ShardPositions.getShardPositions(toUpdate)); }
    
    public static Packet250CustomPayload makeForAllTracked(ShardPositions toSync)
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
                    byte[] data = null;
                    
                    //Structure:
                    //Int: First bit means "Reset local data", deleting any tracked shard on the client.
                    //     The rest is an unsigned int indicating the number of tracked objects to read.
                    //Repeating group - Each one is a single tracked object to update
                    //    Int - First bit means "isEmerald", rest is an unsigned int for the strength
                    //  2xLong - Most and Least significant bits of the entity UUID
                    //    Three doubles - X, Y and Z of the last position
                    
                    //The bitwise not of MAX_VALUE is the sign bit
                    {
                        int firstInt = ~Integer.MAX_VALUE;
                        firstInt |= toSync.diamondShards.size() + toSync.emeraldShards.size();
                        dataConverter.writeInt(firstInt);
                        
                        //TODO Debug
                        FMLLog.info("First Int: %d", firstInt);
                    }
                    
                    for(int i = 0; i < 2; ++i)
                    {
                        boolean isEmerald = i != 0;
                        Set<ShardPositions.Tracked> toEncode = isEmerald ? toSync.emeraldShards : toSync.diamondShards;
                        
                        for(ShardPositions.Tracked toWrite : toEncode)
                        {
                            int firstInt = isEmerald ? ~Integer.MAX_VALUE : 0;
                            firstInt |= toWrite.strength;
                            
                            dataConverter.writeInt(firstInt);
                            
                            dataConverter.writeLong(toWrite.entityUUID.getMostSignificantBits());
                            dataConverter.writeLong(toWrite.entityUUID.getLeastSignificantBits());
                            
                            dataConverter.writeDouble(toWrite.lastPosition.xCoord);
                            dataConverter.writeDouble(toWrite.lastPosition.yCoord);
                            dataConverter.writeDouble(toWrite.lastPosition.zCoord);
                            
                            FMLLog.info("Writing: %d\t%d\t%s", firstInt, toWrite.strength, toWrite.lastPosition.toString());
                        }
                    }
                    
                    dataConverter.flush();
                    data = byteWriter.toByteArray();
                    returnValue = new Packet250CustomPayload(channel, data);
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
                FMLLog.info("Taigore InventorySaver: exception while writing packet %s", Packet250ShardUpdate.class.getName());
                e.printStackTrace();
                returnValue = null;
            }
            
        }
        else
            FMLLog.info("Taigore InventorySaver: null ShardPositions for packet %s", Packet250ShardUpdate.class.getName());
        
        return returnValue;
    }
    
    public static Packet250CustomPayload makeForSomeTracked(boolean isEmerald, ShardPositions.Tracked...toSync)
    {
        Packet250CustomPayload returnValue = null;
        
        if(toSync.length > 0)
        {
            ByteArrayOutputStream byteWriter = new ByteArrayOutputStream();
            DataOutputStream dataConverter = new DataOutputStream(byteWriter);
            
            try
            {
                try
                {
                    byte[] data = null;
                    //Structure:
                    //Int: First bit means "Reset local data", deleting any tracked shard on the client.
                    //     The rest is an unsigned int indicating the number of tracked objects to read.
                    //Repeating group - Each one is a single tracked object to update
                    //    Int - First bit means "isEmerald", rest is an unsigned int for the strength
                    //  2xLong - Most and Least significant bits of the entity UUID
                    //    Three doubles - X, Y and Z of the last position
                    
                    //The bitwise not of MAX_VALUE is the sign bit
                    {
                        int firstInt = 0;
                        firstInt |= toSync.length;
                        dataConverter.writeInt(firstInt);
                    }
                    
                    for(ShardPositions.Tracked toWrite : toSync)
                    {
                        int firstInt = isEmerald ? ~Integer.MAX_VALUE : 0;
                        firstInt |= toWrite.strength;
                        
                        dataConverter.writeInt(firstInt);
                        
                        dataConverter.writeLong(toWrite.entityUUID.getMostSignificantBits());
                        dataConverter.writeLong(toWrite.entityUUID.getLeastSignificantBits());
                        
                        dataConverter.writeDouble(toWrite.lastPosition.xCoord);
                        dataConverter.writeDouble(toWrite.lastPosition.yCoord);
                        dataConverter.writeDouble(toWrite.lastPosition.zCoord);
                    }
                    
                    dataConverter.flush();
                    data = byteWriter.toByteArray();
                    returnValue = new Packet250CustomPayload(channel, data);
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
                FMLLog.info("Taigore InventorySaver: exception while writing packet %s", Packet250ShardUpdate.class.getName());
                e.printStackTrace();
                returnValue = null;
            }
            
        }
        else
            FMLLog.info("Taigore InventorySaver: null ShardPositions for packet %s", Packet250ShardUpdate.class.getName());
        
        return returnValue;
    }
    
    public static void updateShardPosition(Packet250CustomPayload shardsPacket)
    {
        if(isPacketThisType(shardsPacket))
        {
            try
            {
                ByteArrayInputStream byteReader = new ByteArrayInputStream(shardsPacket.data);
                DataInputStream dataConverter = new DataInputStream(byteReader);
                
                try
                {
                    ShardPositions toUpdate = ShardPositions.getShardPositions(Minecraft.getMinecraft().thePlayer.worldObj);
                    
                    //Structure:
                    //Int: First bit means "Reset local data", deleting any tracked shard on the client.
                    //     The rest is an unsigned int indicating the number of tracked objects to read.
                    //Repeating group - Each one is a single tracked object to update
                    //    Int - First bit means "isEmerald", rest is an unsigned int for the strength
                    //  2xLong - Most and Least significant bits of the entity UUID
                    //    Three doubles - X, Y and Z of the last position
                    
                    int metadata = dataConverter.readInt();
                    boolean resetData = (metadata & ~Integer.MAX_VALUE) != 0;
                    int trackedCount = metadata & Integer.MAX_VALUE;
                    
                    if(resetData)
                    {
                        toUpdate.diamondShards.clear();
                        toUpdate.emeraldShards.clear();
                    }
                    
                    ShardPositions.Tracked tempTracked = toUpdate.new Tracked();
                    
                    for(int i = 0; i < trackedCount; ++i)
                    {
                        int trackedMetadata = dataConverter.readInt();
                        
                        boolean isEmerald = (trackedMetadata & ~Integer.MAX_VALUE) != 0;
                        tempTracked.strength = trackedMetadata & Integer.MAX_VALUE;
                        
                        long mostUUID = dataConverter.readLong();
                        long leastUUID = dataConverter.readLong();
                        
                        tempTracked.entityUUID = new UUID(mostUUID, leastUUID);
                        
                        double lastX = dataConverter.readDouble();
                        double lastY = dataConverter.readDouble();
                        double lastZ = dataConverter.readDouble();
                        tempTracked.lastPosition = Vec3.createVectorHelper(lastX, lastY, lastZ);
                        
                        Set<ShardPositions.Tracked> shardSet = isEmerald ? toUpdate.emeraldShards : toUpdate.diamondShards;
                        
                        shardSet.remove(tempTracked);
                        
                        if(tempTracked.strength > 0)
                        {
                            shardSet.add(tempTracked);
                            tempTracked = toUpdate.new Tracked();
                        }
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
                FMLLog.info("Taigore InventorySaver: exception while syncing shard positions");
                e.printStackTrace();
            }
        }
        else
        if(shardsPacket != null)
            FMLLog.info("Taigore InventorySaver: invalid packet with channel %s handled by %s class.\n%s", shardsPacket.channel, Packet250ShardUpdate.class.toString(), String.valueOf(shardsPacket));
    }
    
    public static boolean isPacketThisType(Packet250CustomPayload toCheck)
    {
        return toCheck != null && toCheck.channel.equals(channel);
    }
    
    //Cannot be instantiated
    private Packet250ShardUpdate() {}
}
