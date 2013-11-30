package taigore.inventorysaver.bag;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import taigore.inventorysaver.main.InventorySaver;

import com.google.common.base.Strings;

public class InventoryBag implements IInventory
{
	private static class ItemsInventory
	{
		static private final String tagName = "Items";
		
		private final List<ItemStack> itemsInventory_ = new LinkedList();
		
		ItemsInventory(ItemStack[] items)
		{
			if(items != null)
	        {
	        	itemsInventory_.addAll(Arrays.asList(items));
	            Arrays.fill(items, null);
	            
	            while(itemsInventory_.remove(null)) {}
	        }
		}
		
		boolean isEmpty()
		{
			return itemsInventory_.isEmpty();
		}
		
		void writeToNBT(NBTTagCompound compound)
		{
			if(compound != null)
			{
				final NBTTagList itemsTags = new NBTTagList();
				
				for(ItemStack stack : itemsInventory_)
				{
					final NBTTagCompound itemData = stack.writeToNBT(new NBTTagCompound());
					
					itemsTags.appendTag(itemData);
				}
				
				compound.setTag(tagName, itemsTags);
			}
		}
		
		void readFromNBT(NBTTagCompound compound)
		{
			final boolean hasTag = compound != null && compound.hasKey(tagName);
			final NBTTagList itemsTags = hasTag ? compound.getTagList(tagName) : null;
			
			if(itemsTags != null)
			{
				itemsInventory_.clear();
				
				try
				{
					for(int i = 0; i < itemsTags.tagCount(); ++i)
					{
						final ItemStack loadedStack = ItemStack.loadItemStackFromNBT((NBTTagCompound)itemsTags.tagAt(i));
						
						if(loadedStack != null)
							itemsInventory_.add(loadedStack);
					}
				}
				catch(ClassCastException e)
				{
					InventorySaver.log.severe("Invalid bag saved data");
				}
			}
		}
		
		boolean moveIntoPlayerInventory(InventoryPlayer inventory)
		{
			final ItemStack[] mainInventory = inventory.mainInventory;
			boolean changed = false;
			
			for(int i = 0; i < mainInventory.length && !this.isEmpty(); ++i)
			{
				if(mainInventory[i] == null)
				{
					final ItemStack toMove = itemsInventory_.remove(0);
					
					mainInventory[i] = toMove;
					changed = true;
				}
			}
			
			return changed;
		}
		
		int getInventorySize()
		{
			return itemsInventory_.size();
		}
		
		void setStackInSlot(ItemStack stack, int slotNum)
		{
			if(0 <= slotNum && slotNum < itemsInventory_.size())
			{
				if(stack == null)
					itemsInventory_.remove(slotNum);
				else
					itemsInventory_.set(slotNum, stack);
			}
		}
		
		ItemStack getStackInSlot(int slotNum)
		{
			ItemStack value = null;
			
			if(0 <= slotNum && slotNum < itemsInventory_.size())
				value = itemsInventory_.get(slotNum);
			
			return value;
		}
	}
	
	private static class ArmorInventory
	{
		static private final String tagName = "Armor";
		
		private final ItemStack[] armorInventory_ = new ItemStack[4];
		
		ArmorInventory(ItemStack[] armor)
		{
			if(armor != null)
	        {
	        	try
	        	{
		        	for(int i = 0; i < armorInventory_.length; ++i)
		        		armorInventory_[i] = armor[i];
		        	
		        	Arrays.fill(armor, null);
	        	}
	        	catch(ArrayIndexOutOfBoundsException e)
	        	{
	        		InventorySaver.log.severe("Armor items was shorter than 4 slots");
	        	}
	        }
		}
		
		boolean isEmpty()
		{
			boolean isEmpty = true;
			
			for(int i = 0; isEmpty && i < armorInventory_.length; ++i)
				isEmpty = armorInventory_[i] == null;
			
			return isEmpty;
		}
		
		void writeToNBT(NBTTagCompound compound)
		{
			if(compound != null)
			{
				final NBTTagCompound itemsCompound = new NBTTagCompound();
				
				for(int i = 0; i < armorInventory_.length; ++i)
				{
					if(armorInventory_[i] != null)
					{
						final String stackTag = String.format("Slot%d", i);
						final NBTTagCompound stackData = armorInventory_[i].writeToNBT(new NBTTagCompound());
						
						itemsCompound.setCompoundTag(stackTag, stackData);
					}
				}
				
				compound.setCompoundTag(tagName, itemsCompound);
			}
		}
		
		void readFromNBT(NBTTagCompound compound)
		{
			final boolean hasTag = compound != null && compound.hasKey(tagName);
			final NBTTagCompound armorTags = hasTag ? compound.getCompoundTag(tagName) : null;
			
			if(armorTags != null)
			{
				Arrays.fill(armorInventory_, null);
				
				if(!armorTags.hasNoTags())
				{
					for(int i = 0; i < armorInventory_.length; ++i)
					{
						final String stackTag = String.format("Slot%d", i);
						
						if(armorTags.hasKey(tagName))
						{
							final NBTTagCompound stackData = armorTags.getCompoundTag(stackTag);
							final ItemStack loadedStack = ItemStack.loadItemStackFromNBT(stackData);
							
							armorInventory_[i] = loadedStack;
						}
					}
				}
			}
		}
		
		boolean moveIntoPlayerInventory(InventoryPlayer inventory)
		{
			final ItemStack[] playerArmor = inventory.armorInventory;
			final int commonLength = Math.min(armorInventory_.length, playerArmor.length);
			boolean changed = false;
			
			for(int i = 0; i < commonLength; ++i)
			{
				if(playerArmor[i] == null)
				{
					playerArmor[i] = armorInventory_[i];
					armorInventory_[i] = null;
					
					changed = true;
				}
			}
			
			if(!this.isEmpty())
			{
				final ItemStack[] mainInventory = inventory.mainInventory;
				
				for(int i = 0; i < mainInventory.length; ++i)
				{
					if(mainInventory[i] == null)
					{
						int j = 0;
						
						for(; j < armorInventory_.length; ++j)
						{
							if(armorInventory_[j] != null)
							{
								mainInventory[i] = armorInventory_[j];
								armorInventory_[j] = null;
								changed = true;
								break;
							}
						}
						
						if(j >= armorInventory_.length)
							break;
					}
				}
			}
			
			return changed;
		}
		
		int getInventorySize()
		{
			return armorInventory_.length;
		}
		
		void setStackInSlot(ItemStack stack, int slotNum)
		{
			if(0 <= slotNum && slotNum < armorInventory_.length)
				armorInventory_[slotNum] = stack;
		}
		
		ItemStack getStackInSlot(int slotNum)
		{
			ItemStack value = null;
			
			if(0 <= slotNum && slotNum < armorInventory_.length)
				value = armorInventory_[slotNum];
			
			return value;
		}
	}
	
	static private final String tagName = "Inventory";
	
	private final ItemsInventory itemsInventory_;
    private final ArmorInventory armorInventory_;
    
    public String ownerName;
    
    //METHODS
    public InventoryBag()
    {
    	itemsInventory_ = new ItemsInventory(null);
    	armorInventory_ = new ArmorInventory(null);
    }
    
    public InventoryBag(String ownerName, ItemStack[] armorItems, ItemStack[] otherItems)
    {
        this.ownerName = ownerName;
        
        itemsInventory_ = new ItemsInventory(otherItems);
        armorInventory_ = new ArmorInventory(armorItems);
    }
    
    public void moveIntoPlayerInventory(InventoryPlayer inventory)
    {
    	boolean changed = armorInventory_.moveIntoPlayerInventory(inventory)
    				   || itemsInventory_.moveIntoPlayerInventory(inventory);
    	
    	if(changed)
    		inventory.onInventoryChanged();
    }
    
    /**
     * Returns if there are items left in the bag.
     */
    public boolean isEmpty()
    {
        return itemsInventory_.isEmpty() && armorInventory_.isEmpty();
    }
    
    /**
     * Saves the inventory contents.
     */
    public void writeToNBT(NBTTagCompound toWriteOn)
    {
        if(toWriteOn != null)
        {
        	final NBTTagCompound data = new NBTTagCompound();
        	
            if(!Strings.isNullOrEmpty(this.ownerName))
                data.setString("Owner", this.ownerName);
            
            itemsInventory_.writeToNBT(data);
            armorInventory_.writeToNBT(data);
            
            toWriteOn.setCompoundTag(tagName, data);
        }
    }
    /**
     * Loads the inventory contents.
     * Leaves the tile entity untouched if supplied a null NBTCompound
     */
    public void readFromNBT(NBTTagCompound toRead)
    {
    	final boolean hasTag = toRead != null && toRead.hasKey(tagName);
    	final NBTTagCompound data = hasTag ? toRead.getCompoundTag(tagName) : null;
    	
        if(data != null)
        {
            this.ownerName = data.getString("Owner");
            
            itemsInventory_.readFromNBT(data);
            armorInventory_.readFromNBT(data);
        }
    }
    
    public ItemStack removeFirstStack()
    {
    	ItemStack value = null;
    	
    	itemsInventory_.getStackInSlot(0);
    	itemsInventory_.setStackInSlot(null, 0);
    	
    	for(int i = 0; value == null && i < armorInventory_.getInventorySize(); ++i)
    	{
    		value = armorInventory_.getStackInSlot(i);
    		armorInventory_.setStackInSlot(null, i);
    	}
    	
    	return value;
    }
    
    ///////////////
    // IInventory
    ///////////////
    @Override
	public ItemStack getStackInSlot(int slotNum)
    {
    	final boolean isArmor = slotNum < armorInventory_.getInventorySize();
    	final int index = isArmor ? slotNum : slotNum - armorInventory_.getInventorySize();
    	
    	ItemStack value = null;
    	
    	if(isArmor)
        	value = armorInventory_.getStackInSlot(index);
        else
        	value = itemsInventory_.getStackInSlot(index);
    	
        return value;
    }
    
    @Override
    public void setInventorySlotContents(int slotNum, ItemStack toSet) throws ArrayIndexOutOfBoundsException
    {
    	final boolean isArmor = slotNum < armorInventory_.getInventorySize();
    	final int index = isArmor ? slotNum : slotNum - armorInventory_.getInventorySize();
    	
        if(isArmor)
        	armorInventory_.setStackInSlot(toSet, index);
        else
        	itemsInventory_.setStackInSlot(toSet, index);
    }
    
    @Override
    public ItemStack decrStackSize(int slotNum, int amount) throws ArrayIndexOutOfBoundsException
    {
        ItemStack inSlot = this.getStackInSlot(slotNum);
        ItemStack returnStack = null;
        
        if(inSlot != null && amount > 0)
        {
            returnStack = inSlot.stackSize <= amount ? inSlot : inSlot.splitStack(amount);
            
            if(inSlot.stackSize <= 0 || returnStack == inSlot)
                this.setInventorySlotContents(slotNum, null);
        }
        
        return returnStack;
    }
    
    @Override
    public boolean isUseableByPlayer(EntityPlayer user)
    {
		final boolean isOwnerNotValid = Strings.isNullOrEmpty(this.ownerName);
		
		final boolean isLootProtected = InventorySaver.instance.configuration.protectLoot.read(); 
		final boolean isUserOwner = user.username.equals(this.ownerName);
		
		final boolean canUse = isOwnerNotValid || (isUserOwner || !isLootProtected);
    	
    	return canUse;
    }
    
    @Override
    public void onInventoryChanged() {}
    @Override
    public int getSizeInventory() { return this.itemsInventory_.getInventorySize() + this.armorInventory_.getInventorySize(); }
    @Override
    public ItemStack getStackInSlotOnClosing(int slotNum) { return null; }
    @Override
    public String getInvName() { return String.format("Bag of %s", this.ownerName); }
    @Override
    public int getInventoryStackLimit() { return 64; }
    @Override
    public void openChest() {}
    @Override
    public void closeChest() {}
    @Override
    public boolean isInvNameLocalized() { return false; }
    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) { return false; }
}
