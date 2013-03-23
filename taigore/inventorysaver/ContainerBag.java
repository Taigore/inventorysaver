package taigore.inventorysaver;

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
import taigore.inventorysaver.EntityBag.InventoryBag;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerBag extends Container
{
	public final InventoryBag bagInventory;
	public final InventoryPlayer playerInventory;
	
	public ContainerBag(InventoryPlayer playerInventory, EntityBag openedBag)
	{
		this.bagInventory = openedBag.inventory;
		this.playerInventory = playerInventory;
		
		int startingX = 62;
		int startingY = 8;
		
		int rows = 4;
		int columns = 6;
		
		for(int i = 0; i < rows; i++)
		{
			for(int j = 0; j < columns; j++)
			{
				this.addSlotToContainer(new SlotBag(openedBag, i * columns + j, startingX + 18 * j, startingY + 18 * i));
			}
		}
		
		for(int i = 0; i < 4; ++i)
        {
            this.addSlotToContainer(new SlotArmor(playerInventory, playerInventory.getSizeInventory() - 1 - i, 8, 8 + i * 18, i));
        }

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(playerInventory, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
	}
	
	public ItemStack transferStackInSlot(EntityPlayer playerClicker, int clickedSlotID)
    {
		//The bag is take only, so I don't bother to handle shift click from the
		//player's inventory.
		if(clickedSlotID < bagInventory.getSizeInventory())
		{
			//Tries to merge the ItemStack with the player inventory.
			//It uses the calling routine to get re-called in case the stack
			//couldn't get fully merged, otherwise, if the stack was fully merged
			//or not a single item could have been moved, it stops the recalling.
			Slot bagSlot = this.getSlot(clickedSlotID);
			ItemStack stackToMerge = bagSlot.getStack();
			
			if(stackToMerge != null)
			{
				//Merges from the last slot to the first slot of the player's inventory.
				//+4 is to exclude the four armor slots, which are actually included in this.playerInventory.getSizeInventory()
				if(!this.mergeItemStack(stackToMerge, this.inventorySlots.size() - this.playerInventory.getSizeInventory() + 4, this.inventorySlots.size(), true))
					return null;
				else
				{
					if(stackToMerge.stackSize == 0)
					{
						bagSlot.putStack(null);
						return null;
					}
					else
						return stackToMerge;
				}
			}
			else return null;
		}
		else return null;
    }
	
	@Override
	public boolean canInteractWith(EntityPlayer interactor) { return !bagInventory.getEntity().isDead; }
	
	/**
	 * Simple "no input" slot
	 */
	private class SlotBag extends Slot
	{
		public SlotBag(EntityBag managedBag, int slotIndex, int slotX, int slotY) { super(managedBag.inventory, slotIndex, slotX, slotY); }
		
		@Override
		public boolean isItemValid(ItemStack toCheck) { return false; }
	}
	
	/**
	 * Shameless copy. Thanks for setting it default visibility, Notch!
	 * Thanks for not fixing the visibility in 1.4.7, Jeb!
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
