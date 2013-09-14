package taigore.inventorysaver.inventory;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import taigore.inventorysaver.tileentity.TileEntityBag;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerBag extends Container
{
	public final TileEntityBag bagInventory;
	public final InventoryPlayer playerInventory;
	
	public ContainerBag(InventoryPlayer playerInventory, TileEntityBag openedBag)
    {
        this.bagInventory = openedBag;
        this.playerInventory = playerInventory;
        
        int startingX = 31;
        int startingY = 9;
        
        int rows = 5;
        int columns = 8;
        
        for(int i = 0; i < rows; i++)
        {
            for(int j = 0; j < columns; j++)
            {
                this.addSlotToContainer(new SlotBag(openedBag, i * columns + j, startingX + 18 * j, startingY + 18 * i));
            }
        }
        
        startingX = 9;
        startingY = 9;
        
        for(int i = 0; i < 4; ++i)
        {
            this.addSlotToContainer(new SlotArmor(playerInventory, playerInventory.getSizeInventory() - 1 - i, startingX, startingY + i * 18, i));
        }
        
        startingX = 11;
        startingY = 101;

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(playerInventory, j + (i + 1) * 9, startingX + j * 18, startingY + i * 18));
            }
        }
        
        startingX = 11;
        startingY = 159;
        
        for (int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(playerInventory, i, startingX + i * 18, startingY));
        }
    }
	
	//////////////
	// Container
	//////////////
	public ItemStack transferStackInSlot(EntityPlayer playerClicker, int clickedSlotID)
    {
		//The bag is take only, so I don't handle shift click from the
		//player's inventory.
		if(clickedSlotID < bagInventory.getSizeInventory())
		{
			//Tries to merge the ItemStack with the player inventory.
			//It uses the calling routine to get re-called in case the stack
			//couldn't get fully merged. If the stack was fully merged
			//or not a single item has been moved, it stops the recalling.
			Slot bagSlot = this.getSlot(clickedSlotID);
			int armorSlot = bagSlot.getSlotIndex() - 36;
			ItemStack stackToMerge = bagSlot.getStack();
			
			if(stackToMerge != null)
			{
			    if(armorSlot > 0)
			    {
			        playerClicker.inventory.armorInventory[3 - armorSlot] = stackToMerge;
			        bagSlot.putStack(null);
			    }
			    else
				//Merges from the last slot to the first slot of the player's inventory.
				//+4 is to exclude the four armor slots, which are actually included in this.playerInventory.getSizeInventory()
				if(this.mergeItemStack(stackToMerge, this.inventorySlots.size() - this.playerInventory.getSizeInventory() + 4, this.inventorySlots.size(), true))
				{
					if(stackToMerge.stackSize == 0)
						bagSlot.putStack(null);
					else
						return stackToMerge;
				}
			}
		}

		return null;
    }
	
	@Override
	public boolean canInteractWith(EntityPlayer interactor) { return this.bagInventory.isUseableByPlayer(interactor); }
	
	///////////////
	// Subclasses
	///////////////
	/**
	 * Simple "no input" slot
	 */
	private class SlotBag extends Slot
	{
		public SlotBag(TileEntityBag managedBag, int slotIndex, int slotX, int slotY) { super(managedBag, slotIndex, slotX, slotY); }
		
		@Override
		public boolean isItemValid(ItemStack toCheck) { return false; }
	}
	
	/**
	 * Shameless copy. Thanks for setting it default visibility, Notch!
	 * Thanks for not fixing the visibility in 1.5.2, Jeb!
	 */
	private class SlotArmor extends Slot
	{
		/**
	     * The armor type that can be placed on that slot, it uses the same values of armorType field on ItemArmor.
	     */
	    final int armorType;
	
	    SlotArmor(IInventory par2IInventory, int par3, int par4, int par5, int par6)
	    {
	        super(par2IInventory, par3, par4, par5);
	        this.armorType = par6;
	    }
	
	    /**
	     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case
	     * of armor slots)
	     */
	    public int getSlotStackLimit()
	    {
	        return 1;
	    }
	
	    /**
	     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
	     */
	    public boolean isItemValid(ItemStack par1ItemStack)
	    {
	        return par1ItemStack == null ? false : (par1ItemStack.getItem() instanceof ItemArmor ? ((ItemArmor)par1ItemStack.getItem()).armorType == this.armorType : (par1ItemStack.getItem().itemID != Block.pumpkin.blockID && par1ItemStack.getItem().itemID != Item.skull.itemID ? false : this.armorType == 0));
	    }
	    
	    @SideOnly(Side.CLIENT)
	    /**
	     * Returns the icon index on items.png that is used as background image of the slot.
	     */
	    public Icon getBackgroundIconIndex() { return ItemArmor.func_94602_b(this.armorType); }
	}
}
