package taigore.inventorysaver.inventory;

import java.util.Collection;
import java.util.Iterator;

import taigore.inventorysaver.entity.item.EntityBag;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.FMLLog;

///////////////
// Subclasses
///////////////
public class InventoryBag implements IInventory
{
    private final EntityBag ownerBag;
    private ItemStack[] inventory = new ItemStack[this.getSizeInventory()];
	
	public InventoryBag(EntityBag entityBag) { this.ownerBag = entityBag;}

	public EntityBag getEntity() { return this.ownerBag; }
	
	///////////////
	// IInventory
	///////////////
	@Override
	public ItemStack getStackInSlot(int slotNum)
	{
		if(slotNum < 0 || slotNum >= this.getSizeInventory())
		{
			FMLLog.warning("Taigore InventorySaver: slot %d out of bounds for inventory read", slotNum);
			return null;
		}
		else
		    return this.inventory[slotNum];
	}

	@Override
	public ItemStack decrStackSize(int slotNum, int amount)
	{
		ItemStack inSlot = this.getStackInSlot(slotNum);
		ItemStack returnStack = null;
		
		if(inSlot != null && amount > 0)
		{
			if(inSlot.stackSize > 0)
			{
				if(inSlot.stackSize <= amount)
				{
					returnStack = inSlot;
					this.setInventorySlotContents(slotNum, null);
				}
				else
				{
					returnStack = inSlot.splitStack(amount);
				}
			}
			else
				this.setInventorySlotContents(slotNum, null);
		}

		return returnStack;
	}

	@Override
	public void setInventorySlotContents(int slotNum, ItemStack toSet)
	{
		if(slotNum < 0 || slotNum >= this.getSizeInventory())
		{
		    FMLLog.warning("Taigore InventorySaver: slot %d out of bounds for inventory write", slotNum);
		    
		    if(toSet != null)
		        this.getEntity().entityDropItem(toSet, 0.0f);
		}
		else
		    this.inventory[slotNum] = toSet;
	}

	@Override
	public int getSizeInventory() { return 40; }
	@Override
	public ItemStack getStackInSlotOnClosing(int slotNum) { return null; }
	@Override
	public String getInvName() { return this.getEntity().getOwnerName(); }
	@Override
	public int getInventoryStackLimit() { return 64; }
	@Override
	public void onInventoryChanged() {}
	@Override
	public boolean isUseableByPlayer(EntityPlayer user) { return !this.getEntity().isDead; }
	@Override
	public void openChest() {}
	@Override
	public void closeChest() {}
	@Override
	public boolean isInvNameLocalized() { return false; }
	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack) { return false; }
	
	//////////////
	// Save/Load
	//////////////
	public static final String inventoryTag = "Inventory";
	
	public NBTTagCompound writeToNBT(NBTTagCompound toWriteOn)
    {
	    if(toWriteOn != null)
	    {
	        NBTTagCompound inventoryData = new NBTTagCompound();
	        
	        for(int i = 0; i < this.getSizeInventory(); ++i)
	        {
	            ItemStack toSave = this.getStackInSlot(i);
	            
	            if(toSave != null)
	            {
	                NBTTagCompound itemData = toSave.writeToNBT(new NBTTagCompound());
	                itemData.setInteger("SlotNum", i);
	                
	                inventoryData.setCompoundTag(String.format("Slot%d", i), itemData);
	            }
	        }
	        
	        toWriteOn.setCompoundTag(inventoryTag, inventoryData);
	    }
	    
	    return toWriteOn;
    }
    
    public void readFromNBT(NBTTagCompound toReadFrom)
    {
        if(toReadFrom != null && toReadFrom.hasKey(inventoryTag))
        {
            NBTTagCompound inventoryData = toReadFrom.getCompoundTag(inventoryTag);
            
            for(int i = 0; i < this.getSizeInventory(); ++i)
            {
                String slotTag = String.format("Slot%d", i);
                
                if(inventoryData.hasKey(slotTag))
                    this.setInventorySlotContents(i, ItemStack.loadItemStackFromNBT(inventoryData.getCompoundTag(slotTag)));
                else
                    this.setInventorySlotContents(i, null);
            }
        }
    }
    
    ////////////
    // Utility
    ////////////
    /**
     * Adds all ItemStacks to empty slots in this inventory.
     * Returns true if it placed all stacks in the collection.
     */
    public boolean addItemCollection(Collection<ItemStack> itemCollection)
    {
        if(itemCollection != null && !itemCollection.isEmpty())
        {
            int i = 0;
            Iterator<ItemStack> itemIterator = itemCollection.iterator();
            
            while(itemIterator.hasNext() && i < this.getSizeInventory())
            {
                ItemStack toAdd = itemIterator.next();
                
                if(toAdd != null)
                {
                    for(; i < this.getSizeInventory() && this.getStackInSlot(i) != null; ++i);
                    
                    if(i < this.getSizeInventory())
                        this.setInventorySlotContents(i, toAdd);
                }
                
                itemIterator.remove();
            }
            
            return !itemIterator.hasNext();
        }
        
        return true;
    }
    /**
     * Throws out the given number of item stacks.
     * Stops only if there are no more stacks to throw away.
     */
    public void popNumStack(int amount)
    {
        for(int i = 0; i < this.getSizeInventory() && amount > 0; ++i)
        {
            ItemStack toDrop = this.getStackInSlot(i);
            
            if(toDrop != null && !this.getEntity().worldObj.isRemote)
            {
                EntityItem droppedItem = this.getEntity().entityDropItem(toDrop, ownerBag.yOffset);
                
                droppedItem.motionX = this.getEntity().motionX;
                droppedItem.motionY = 0.30f + this.getEntity().motionY;
                droppedItem.motionZ = this.getEntity().motionZ;
                droppedItem.delayBeforeCanPickup = 10;
                
                this.setInventorySlotContents(i, null);
                amount -= 1;
            }
        }
    }
    
    public boolean isEmpty()
    {
        for(int i = 0; i < this.getSizeInventory(); ++i)
            if(this.getStackInSlot(i) != null)
                return false;
        
        return true;
    }
}