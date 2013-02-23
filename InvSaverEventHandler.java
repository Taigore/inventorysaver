package taigore.inventorysaver;

import java.util.ArrayList;

import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import cpw.mods.fml.common.FMLLog;

public class InvSaverEventHandler
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
