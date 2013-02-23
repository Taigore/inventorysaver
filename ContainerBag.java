package taigore.inventorysaver;

import java.lang.reflect.Constructor;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

import net.minecraft.src.Block;
import net.minecraft.src.Container;
import net.minecraft.src.ContainerPlayer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemArmor;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

public class ContainerBag extends Container
{
	private EntityBag bagInventory;
	private InventoryPlayer playerInventory;
	
	public ContainerBag(InventoryPlayer playerInventory, EntityBag openedBag)
	{
		this.bagInventory = openedBag;
		this.playerInventory = playerInventory;
		
		int startingX = 44;
		int startingY = 8;
		
		for(int i = 0; i < openedBag.getSizeInventory(); i++)
		{
			this.addSlotToContainer(new SlotBag(openedBag, i, startingX + 18 * (i % 7), startingY + 18 * (i / 7)));
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
		if(clickedSlotID < this.bagInventory.getSizeInventory())
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
	public void onCraftGuiClosed(EntityPlayer par1EntityPlayer)
	{
		super.onCraftGuiClosed(par1EntityPlayer);
		
		this.bagInventory.clicker = null;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer var1) { return !this.bagInventory.isDead; }
	
	private class SlotBag extends Slot
	{
		public SlotBag(EntityBag managedBag, int par2, int par3, int par4) { super(managedBag, par2, par3, par4); }
		
		@Override
		public boolean isItemValid(ItemStack toCheck) { return false; }
	}
	
	/**
	 * Shameless copy. Thanks for setting it default visibility, Notch!
	 */
	private class SlotArmor extends Slot
	{
	    /**
	     * The armor type that can be placed on that slot, it uses the same values of armorType field on ItemArmor.
	     */
	    final int armorType;

	    public SlotArmor(IInventory par2IInventory, int par3, int par4, int par5, int par6)
	    {
	        super(par2IInventory, par3, par4, par5);
	        this.armorType = par6;
	    }
	    
	    /**
	     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
	     */
	    public boolean isItemValid(ItemStack par1ItemStack)
	    {
	        return par1ItemStack == null ? false : (par1ItemStack.getItem() instanceof ItemArmor ? ((ItemArmor)par1ItemStack.getItem()).armorType == this.armorType : (par1ItemStack.getItem().shiftedIndex != Block.pumpkin.blockID && par1ItemStack.getItem().shiftedIndex != Item.skull.shiftedIndex ? false : this.armorType == 0));
	    }
	    /**
	     * Returns the icon index on items.png that is used as background image of the slot.
	     */
	    @SideOnly(Side.CLIENT)
	    public int getBackgroundIconIndex() { return 15 + this.armorType * 16; }
	    /**
	     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case
	     * of armor slots)
	     */
	    public int getSlotStackLimit() { return 1; }
	}
}
