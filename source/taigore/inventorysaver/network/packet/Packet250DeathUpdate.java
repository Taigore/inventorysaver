package taigore.inventorysaver.network.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import taigore.inventorysaver.world.DeathPositions;
import cpw.mods.fml.common.FMLLog;

public class Packet250DeathUpdate extends Packet250CustomPayload
{
    public static final String channel = "TgrInvSvr_Deaths";
    
    public static Packet250CustomPayload makeForAllTracked(World toUpdate) { return makeForAllTracked(DeathPositions.getDeathPositions(toUpdate)); }
    
    public static Packet250CustomPayload makeForAllTracked(DeathPositions toSync)
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
                    
                    NBTTagCompound deathsData = new NBTTagCompound();
                    toSync.writeToNBT(deathsData);
                    
                    Packet250CustomPayload.writeNBTTagCompound(deathsData, dataConverter);
                    
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
                FMLLog.info("Taigore InventorySaver: exception while writing packet %s", Packet250DeathUpdate.class.getName());
                e.printStackTrace();
                returnValue = null;
            }
            
        }
        else
            FMLLog.info("Taigore InventorySaver: null ShardPositions for packet %s", Packet250DeathUpdate.class.getName());
        
        return returnValue;
    }
    
    public static void updateDeathPosition(Packet250CustomPayload shardsPacket)
    {
        if(isPacketThisType(shardsPacket))
        {
            try
            {
                ByteArrayInputStream byteReader = new ByteArrayInputStream(shardsPacket.data);
                DataInputStream dataConverter = new DataInputStream(byteReader);
                
                try
                {
                    DeathPositions toUpdate = DeathPositions.getDeathPositions(Minecraft.getMinecraft().thePlayer.worldObj);
                    
                    NBTTagCompound deathsData = Packet250CustomPayload.readNBTTagCompound(dataConverter);
                    
                    toUpdate.readFromNBT(deathsData);
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
            FMLLog.info("Taigore InventorySaver: invalid packet with channel %s handled by %s class.\n%s", shardsPacket.channel, Packet250DeathUpdate.class.toString(), String.valueOf(shardsPacket));
    }
    
    public static boolean isPacketThisType(Packet250CustomPayload toCheck)
    {
        return toCheck != null && toCheck.channel.equals(channel);
    }
    
    //Cannot be instantiated
    private Packet250DeathUpdate() {}
}
