package taigore.inventorysaver.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.WorldEvent;
import taigore.inventorysaver.main.InventorySaver;
import taigore.inventorysaver.world.DeathPositions;

public class CompassHandler
{
	@ForgeSubscribe
	public void loadDeathData(WorldEvent.Load event)
	{
		final boolean isLocal = !event.world.isRemote;
		
	    if(isLocal)
	    {
	        DeathPositions.getDeathPositions(event.world);
	        InventorySaver.log.info("Loaded death position data");
	    }
	}
	
	@ForgeSubscribe
	public void addDeathPosition(LivingDeathEvent event)
	{
		final boolean isPlayer = EntityPlayer.class.isInstance(event.entity);
		
		if(isPlayer)
		{
			final EntityPlayer dead = (EntityPlayer)event.entity;
			final World deathWorld = dead.worldObj;
			
			InventorySaver.log.info("Adding death position");
	    	DeathPositions.getDeathPositions(deathWorld).addDeathPoint(dead);
		}
	}
}
