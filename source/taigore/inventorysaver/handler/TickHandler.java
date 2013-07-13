package taigore.inventorysaver.handler;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import taigore.inventorysaver.network.packet.Packet250ShardUpdate;
import taigore.inventorysaver.world.ShardPositions;
import taigore.inventorysaver.world.ShardPositions.Tracked;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TickHandler implements ITickHandler
{
    private Set<EntityPlayerMP> alreadyUpdatedPlayers = new HashSet();
    
    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
        World updated = (World)tickData[0];
        
        if(!updated.isRemote)
        {
            ShardPositions toSync = ShardPositions.getShardPositions(updated);
            
            for(int i = 0; i < 2; ++i)
            {
                boolean isEmerald = i == 0;
                Set<Tracked> toUpdate = isEmerald ? toSync.emeraldShards : toSync.diamondShards;
                
                for(Tracked updating : toUpdate)
                    updating.updatePosition();
            }
            
            Set<EntityPlayerMP> newPlayers = new HashSet(updated.playerEntities);
            newPlayers.removeAll(this.alreadyUpdatedPlayers);
            
            if(!newPlayers.isEmpty())
            {
                Packet250CustomPayload toSend = Packet250ShardUpdate.makeForAllTracked(toSync);
                
                for(EntityPlayerMP player : newPlayers)
                    player.playerNetServerHandler.sendPacketToPlayer(toSend);
                
                this.alreadyUpdatedPlayers.clear();
                this.alreadyUpdatedPlayers.addAll(updated.playerEntities);
            }
        }
    }
    
    @Override
    public EnumSet<TickType> ticks() { return EnumSet.of(TickType.WORLD); }
    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {}
    @Override
    public String getLabel() { return null; }
    
}
