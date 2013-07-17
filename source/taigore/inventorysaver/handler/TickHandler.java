package taigore.inventorysaver.handler;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import taigore.inventorysaver.network.packet.Packet250DeathUpdate;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class TickHandler implements ITickHandler
{
    private Map<World, List<EntityPlayer>> updatedPlayers = new HashMap();
    
    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        //This adds new players
        if(type.contains(TickType.WORLD))
        {
            World updated = (World)tickData[0];
            
            if(!this.updatedPlayers.containsKey(updated))
                this.updatedPlayers.put(updated, new LinkedList());
            
            List<EntityPlayer> worldUpdatedPlayers = this.updatedPlayers.get(updated);
            
            List<EntityPlayer> newPlayers = new LinkedList(updated.playerEntities);
            newPlayers.removeAll(worldUpdatedPlayers);
            
            Packet250CustomPayload toSend = null;
            
            if(!newPlayers.isEmpty())
                toSend = Packet250DeathUpdate.makeForAllTracked(updated);
            
            for(Object player : newPlayers)
                if(!this.updatedPlayers.get(updated).contains(player))
                {
                    //TODO Debug
                    FMLLog.info("Adding player");
                    
                    worldUpdatedPlayers.add((EntityPlayer)player);
                    
                    PacketDispatcher.sendPacketToPlayer(toSend, (Player)player);
                }
            
            worldUpdatedPlayers.retainAll(updated.playerEntities);
        }
    }
    
    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {}
    @Override
    public EnumSet<TickType> ticks() { return EnumSet.of(TickType.SERVER, TickType.WORLD); }
    @Override
    public String getLabel() { return null; }
    
}
