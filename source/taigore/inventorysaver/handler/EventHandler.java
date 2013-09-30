package taigore.inventorysaver.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.world.WorldEvent;
import taigore.inventorysaver.InventorySaver;
import taigore.inventorysaver.entity.EntityFallingBag;
import taigore.inventorysaver.tileentity.TileEntityBag;
import taigore.inventorysaver.world.DeathPositions;

public class EventHandler
{
	@ForgeSubscribe(priority = EventPriority.LOW)
	public void dropInventoryBag(PlayerDropsEvent dropEvent)
	{
    	EntityPlayer droppingPlayer = dropEvent.entityPlayer;
    	ArrayList<EntityItem> drops = dropEvent.drops;
    	World currentWorld = droppingPlayer.worldObj;
    	
    	if(!drops.isEmpty() && InventorySaver.instance.bag.isRegistered() && droppingPlayer.posY > 0)
    	{
    	    InventorySaver.log.info("Dropping bag!");
    	    
        	ItemStack[] armorItems = new ItemStack[4];
        	List<ItemStack> normalItems = new LinkedList();
        	int armorType = 0;
        	
        	//Removing armor items
        	for(int slot = drops.size() - 1, lastSlot = drops.size() - 4; slot >= lastSlot; --slot)
        	{
        	    ItemStack dropped = drops.get(slot).getEntityItem();
        	    
        	    while(!dropped.getItem().isValidArmor(dropped, armorType, droppingPlayer) && armorType < armorItems.length)
        	        armorType += 1;
        	    
        	    if(armorType < armorItems.length)
        	    {
        	        armorItems[armorType] = dropped;
        	        armorType += 1;
        	        drops.remove(slot);
        	    }
        	    else break;
        	}
        	
        	//Saving the other items
        	Iterator<EntityItem> iter = drops.iterator();
        	while(iter.hasNext() && normalItems.size() < 36)
        	{
        	    normalItems.add(iter.next().getEntityItem());
        	    iter.remove();
        	}
        	
        	TileEntityBag bag = new TileEntityBag(droppingPlayer.username, armorItems, normalItems);
        	bag.ownerName = droppingPlayer.username;
        	
        	if(droppingPlayer.posY < droppingPlayer.worldObj.provider.getHeight())
        	{
            	int posX = MathHelper.floor_double(droppingPlayer.posX);
            	int posY = MathHelper.floor_double(droppingPlayer.posY);
            	int posZ = MathHelper.floor_double(droppingPlayer.posZ);
            	
            	currentWorld.setBlock(posX, posY, posZ, InventorySaver.instance.bag.getBlock().blockID);
            	currentWorld.setBlockTileEntity(posX, posY, posZ, bag);
            	currentWorld.scheduleBlockUpdate(posX, posY, posZ, InventorySaver.instance.bag.getBlock().blockID, 2);
        	}
        	else if(!currentWorld.isRemote)
        	{
        	    EntityFallingBag bagEntity = new EntityFallingBag(currentWorld,
        	                                                      droppingPlayer.posX,
        	                                                      droppingPlayer.posY,
        	                                                      droppingPlayer.posZ);
        	    NBTTagCompound tileEntityData = new NBTTagCompound();
        	    
        	    bag.writeToNBT(tileEntityData);
        	    bagEntity.fallingBlockTileEntityData = tileEntityData;
        	    currentWorld.spawnEntityInWorld(bagEntity);
        	}
        	
        	InventorySaver.log.info(String.format("%d items were not stored", drops.size()));
    	}
    	
    	if(InventorySaver.instance.compass.isRegistered())
    	{
        	InventorySaver.log.info("Adding death position");
        	DeathPositions.getDeathPositions(currentWorld).addDeathPoint(droppingPlayer);
    	}
    	
    	//Excess items will still be handled by whatever comes after
	}
	
	@ForgeSubscribe
	public void loadDeathData(WorldEvent.Load event)
	{
	    if(!event.world.isRemote && InventorySaver.instance.compass.isRegistered())
	    {
	        DeathPositions.getDeathPositions(event.world);
	        InventorySaver.log.info("Loaded death position data");
	    }
	}
}
