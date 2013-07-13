package taigore.inventorysaver.handler;

import java.util.ArrayList;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import taigore.inventorysaver.InventorySaver;
import taigore.inventorysaver.entity.item.EntityBag;
import taigore.inventorysaver.item.ItemGemShard;
import taigore.inventorysaver.world.ShardPositions;

public class EventHandler
{
	@ForgeSubscribe
	public void dropInventoryBag(PlayerDropsEvent deathEvent)
	{
    	EntityPlayer droppingPlayer = deathEvent.entityPlayer;
    	ArrayList<EntityItem> drops = deathEvent.drops;
    	World currentWorld = droppingPlayer.worldObj;
    		
    	if (!currentWorld.isRemote)
        {
    		EntityBag dropsBag = new EntityBag(currentWorld, drops, droppingPlayer.username);
    		dropsBag.setPosition(droppingPlayer.posX, droppingPlayer.posY + 1.0d, droppingPlayer.posZ);
    		dropsBag.rotationYaw = droppingPlayer.rotationYaw;
    			
    		currentWorld.spawnEntityInWorld(dropsBag);
    		
    		ShardPositions savedShards = ShardPositions.getShardPositions(currentWorld);
    		ItemGemShard itemType = InventorySaver.instance.resonantShard;
    		
    		for(EntityItem dropped : drops)
    		{
    		    ItemStack item = dropped != null ? dropped.getEntityItem() : null;
    		    
    		    if(item != null && item.getItem() == itemType)
    		        savedShards.changeShardStrength(dropsBag, item.stackSize, itemType.isEmerald(item));
    		}
        }
    		
    	//Prevents the standard drop
    	deathEvent.setCanceled(true);
	}
}
