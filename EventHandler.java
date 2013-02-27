package taigore.inventorysaver;

import java.util.ArrayList;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;

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
        }
		
		//Prevents the standard drop
		deathEvent.setCanceled(true);
	}
}
