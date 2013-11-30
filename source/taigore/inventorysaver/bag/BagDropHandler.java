package taigore.inventorysaver.bag;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import taigore.inventorysaver.main.InventorySaver;

public class BagDropHandler
{
	private ItemStack[] collectStacks(ItemStack[] slots)
	{
		List<ItemStack> stacks = new LinkedList();
		
		for(ItemStack currentStack : slots)
			if(currentStack != null)
				stacks.add(currentStack);
		
		Arrays.fill(slots, null);
		
		return stacks.toArray(new ItemStack[stacks.size()]);
	}
	
	@ForgeSubscribe(priority = EventPriority.LOW)
	public void dropInventoryBag(LivingDeathEvent dropEvent)
	{
		final boolean isPlayer = EntityPlayer.class.isInstance(dropEvent.entityLiving); 
		
		if(isPlayer)
		{
	    	final EntityPlayer droppingPlayer = (EntityPlayer)dropEvent.entityLiving;
	    	final World currentWorld = droppingPlayer.worldObj;
	    	
	    	final boolean bagAvailable = InventorySaver.instance.configuration.bag.available();
	    	final boolean inventoryDrops = !currentWorld.getGameRules().getGameRuleBooleanValue("keepInventory");
	    	
	    	final boolean canCollectInventory = inventoryDrops && bagAvailable;
	    	
	    	if(canCollectInventory && !currentWorld.isRemote)
	    	{
	    	    InventorySaver.log.info("Dropping bag!");
	    	    
	        	ItemStack[] armorItems = droppingPlayer.inventory.armorInventory;
	        	droppingPlayer.inventory.armorInventory = new ItemStack[armorItems.length];
	        	
	        	ItemStack[] otherItems = collectStacks(droppingPlayer.inventory.mainInventory);
	        	
	        	TileEntityBag bag = new TileEntityBag(droppingPlayer.username, armorItems, otherItems);
	        	
        	    EntityFallingBag bagEntity = new EntityFallingBag(currentWorld,
        	                                                      droppingPlayer.posX,
        	                                                      droppingPlayer.posY + 0.5f,
        	                                                      droppingPlayer.posZ);
        	    NBTTagCompound tileEntityData = new NBTTagCompound();
        	    
        	    bag.writeToNBT(tileEntityData);
        	    bagEntity.fallingBlockTileEntityData = tileEntityData;
        	    bagEntity.fallTime = 2;
        	    currentWorld.spawnEntityInWorld(bagEntity);
	    	}
		}
    	
    	//Excess items will still be handled by whatever comes after
	}
}
