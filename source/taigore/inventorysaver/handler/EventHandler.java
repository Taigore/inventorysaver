package taigore.inventorysaver.handler;

import java.util.ArrayList;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.world.WorldEvent;
import taigore.inventorysaver.entity.item.EntityBag;
import taigore.inventorysaver.network.packet.Packet250DeathUpdate;
import taigore.inventorysaver.world.DeathPositions;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class EventHandler
{
	@ForgeSubscribe
	public void dropInventoryBag(PlayerDropsEvent dropEvent)
	{
    	EntityPlayer droppingPlayer = dropEvent.entityPlayer;
    	ArrayList<EntityItem> drops = dropEvent.drops;
    	World currentWorld = droppingPlayer.worldObj;
    	
    	EntityBag dropsBag = new EntityBag(currentWorld, drops, droppingPlayer.username);
        dropsBag.setPosition(droppingPlayer.posX, droppingPlayer.posY + 1.0d, droppingPlayer.posZ);
        dropsBag.rotationYaw = droppingPlayer.rotationYaw;
    	
    	if (!currentWorld.isRemote)
        {
    		currentWorld.spawnEntityInWorld(dropsBag);
    		
    		DeathPositions savedShards = DeathPositions.getDeathPositions(currentWorld);
    		
    		savedShards.addNewDeathPointForPlayer(droppingPlayer);
    		
    		Packet250CustomPayload toSend = Packet250DeathUpdate.makeForAllTracked(savedShards);
    		PacketDispatcher.sendPacketToPlayer(toSend, (Player)droppingPlayer);
        }
    		
    	//Prevents the standard drop
    	dropEvent.setCanceled(true);
	}
	
	@ForgeSubscribe
	public void loadDeathData(WorldEvent.Load event)
	{
	    if(!event.world.isRemote)
	        DeathPositions.getDeathPositions(event.world);
	}
}
