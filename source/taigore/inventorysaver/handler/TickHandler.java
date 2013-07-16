package taigore.inventorysaver.handler;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import taigore.inventorysaver.network.packet.Packet250DeathUpdate;
import taigore.inventorysaver.world.DeathPositions;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.server.FMLServerHandler;

public class TickHandler implements ITickHandler
{
    private Set<Object> updatedPlayers = new HashSet();
    
    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
        if(type.contains(TickType.PLAYER))
        {
            World updated = ((EntityPlayer)tickData[0]).worldObj;
            
            if(!updated.isRemote)
            {
                if(!updatedPlayers.contains(tickData[0]))
                {
                    DeathPositions toSync = DeathPositions.getDeathPositions(updated);
                    Packet250CustomPayload toSend = Packet250DeathUpdate.makeForAllTracked(toSync);
                    
                    PacketDispatcher.sendPacketToPlayer(toSend, (Player)tickData[0]);
                    
                    this.updatedPlayers.add(tickData[0]);
                }
            }
        }
        if(type.contains(TickType.SERVER))
        {
            MinecraftServer server = FMLServerHandler.instance().getServer();
            
            if(server != null)
            {
                WorldServer[] worlds = server.worldServers;
                Set<Object> allPlayers = new HashSet();
                
                for(WorldServer world : worlds)
                    allPlayers.addAll(world.playerEntities);
                
                this.updatedPlayers.retainAll(allPlayers);
            }
        }
    }
    
    @Override
    public EnumSet<TickType> ticks() { return EnumSet.of(TickType.PLAYER, TickType.SERVER); }
    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {}
    @Override
    public String getLabel() { return null; }
    
}
