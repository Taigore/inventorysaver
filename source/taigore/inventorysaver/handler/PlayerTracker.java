package taigore.inventorysaver.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import taigore.inventorysaver.InventorySaver;
import taigore.inventorysaver.network.packet.Packet250DeathUpdate;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PlayerTracker implements IPlayerTracker
{
    @Override
    public void onPlayerLogin(EntityPlayer player)
    {
        if(!player.worldObj.isRemote)
        {
            Packet250CustomPayload toSend = Packet250DeathUpdate.makeForAllTracked(player.worldObj);
            
            PacketDispatcher.sendPacketToPlayer(toSend, (Player)player);
            InventorySaver.log.info(String.format("Sent death positions data to player %s", player.username));
        }
    }
    @Override
    public void onPlayerChangedDimension(EntityPlayer player)
    {
        if(!player.worldObj.isRemote)
        {
            Packet250CustomPayload toSend = Packet250DeathUpdate.makeForAllTracked(player.worldObj);
            
            PacketDispatcher.sendPacketToPlayer(toSend, (Player)player);
            InventorySaver.log.info(String.format("Sent death positions data to player %s", player.username));
        }
    }
    
    @Override
    public void onPlayerLogout(EntityPlayer player) {}
    @Override
    public void onPlayerRespawn(EntityPlayer player) {}
}
