package taigore.inventorysaver;

import java.lang.reflect.Constructor;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.InventoryPlayer;
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
		
		int startingX = 43;
		int startingY = 7;
		
		for(int i = 0; i < openedBag.getSizeInventory(); i++)
		{
			this.addSlotToContainer(new SlotOutput(openedBag, i, startingX + 18 * (i % 7), startingY + 18 * (i / 7)));
		}
		
/*		for(int i = 0; i < 4; ++i)
        {
            this.addSlotToContainer(new SlotArmor(this, playerInventory, playerInventory.getSizeInventory() - 1 - i, 8, 8 + i * 18, i));
        }*/

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
		ItemStack returnValue = null;
		Slot clickedSlot = (Slot)this.inventorySlots.get(clickedSlotID);
		
        if(clickedSlot.inventory == this.bagInventory && clickedSlot.getHasStack())
        {
            ItemStack stackInSlot = clickedSlot.getStack();
            returnValue = stackInSlot.copy();

            if (!this.mergeItemStack(stackInSlot, this.bagInventory.getSizeInventory(), this.inventorySlots.size(), true))
            {
                return null;
            }

            if (stackInSlot.stackSize == 0)
            {
                clickedSlot.putStack((ItemStack)null);
            }
            else
            {
                clickedSlot.onSlotChanged();
            }
        }
		
        return returnValue;
    }
	
	@Override
	public void onCraftGuiClosed(EntityPlayer par1EntityPlayer)
	{
		super.onCraftGuiClosed(par1EntityPlayer);
		
		this.bagInventory.clicker = null;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer var1) { return true; }
	
	private class SlotOutput extends Slot
	{
		public SlotOutput(EntityBag managedBag, int par2, int par3, int par4) { super(managedBag, par2, par3, par4); }
		
		@Override
		public boolean isItemValid(ItemStack toCheck) { return false; }
		
		public void onPickupFromSlot(EntityPlayer changerPlayer, ItemStack stackTaken)
		{
			super.onPickupFromSlot(changerPlayer, stackTaken);
			
			//Removes the first null ItemStack that, if the inventory was managed completely
			//using Slots, will be the null ItemStack left from emptying the slot, or another
			//null element that should have been removed, or absolutely nothing.
			((EntityBag)this.inventory).bagContents.remove(null);
		}
	}
}
