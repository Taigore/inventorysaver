package taigore.inventorysaver.tileentity;

import java.util.Collection;
import java.util.Iterator;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import taigore.inventorysaver.InventorySaver;

import com.google.common.base.Strings;

public class TileEntityBag extends TileEntity implements IInventory
{
    //FIELDS
    private ItemStack[] itemsInventory = new ItemStack[36];
    private ItemStack[] armorInventory = new ItemStack[4];
    
    public String ownerName;
    
    public long creationTime = -1;
    
    //METHODS
    public TileEntityBag() {}
    
    /**
     * Adds the given items to the inventory of this tile entity.
     * Armor items are stored separately and handled in a different way
     * when moving the items to the player's inventory.
     * If armorItems is longer than 4 slots, excess items are added to otherItems.
     * If otherItems is longer than this.getSizeInventory() - 4, excessItems are ignored.
     * All items successfully added to the inventory are removed from their container.
     * All armor items get removed from armorItems, and may end in otherItems if not added.
     */
    public TileEntityBag(String ownerName, ItemStack[] armorItems, Collection<ItemStack> otherItems)
    {
        this.ownerName = ownerName;
        
        if(armorItems != null)
        {
            for(int i = 0; i < armorItems.length; ++i)
            {
                if(armorItems[i] != null)
                {
                    if(i < this.armorInventory.length)
                        this.armorInventory[i] = armorItems[i];
                    else
                        otherItems.add(armorItems[i]);
                    
                    armorItems[i] = null;
                }
            }
        }
        
        if(otherItems != null)
        {
            int slot = 0;
            
            for(Iterator<ItemStack> iter = otherItems.iterator(); iter.hasNext() && slot < this.itemsInventory.length;)
            {
                ItemStack next = iter.next();
                
                if(next != null)
                    this.itemsInventory[slot++] = next;
                
                iter.remove();
            }
        }
    }
    
    /**
     * Returns if there are items left in the bag.
     */
    public boolean isEmpty()
    {
        for(int i = 0; i < this.getSizeInventory(); ++i)
            if(this.getStackInSlot(i) != null)
                return false;
        
        return true;
    }
    
    ///////////////
    // TileEntity
    ///////////////
    @Override
    public void updateEntity()
    {
    	if(this.creationTime <= 0) this.creationTime = this.worldObj.getTotalWorldTime();
    	else
    	{
    		int cleanupTime = InventorySaver.instance.cleanupTime.getValue();
    		
    		if(cleanupTime > 0
    		&& this.creationTime + cleanupTime * 3600 * 20 < this.worldObj.getTotalWorldTime())
    		{
    			this.worldObj.setBlockToAir(this.xCoord, this.yCoord, this.zCoord);
    			this.invalidate();
    		}
    	}
    }
    
    /**
     * Saves the inventory contents.
     */
    @Override
    public void writeToNBT(NBTTagCompound toWriteOn)
    {
        if(toWriteOn != null)
        {
            super.writeToNBT(toWriteOn);
            
            if(!Strings.isNullOrEmpty(this.ownerName))
                toWriteOn.setString("Owner", this.ownerName);
            
            toWriteOn.setLong("Creation", this.creationTime);
            
            for(int slot = 0; slot < this.getSizeInventory(); ++slot)
            {
                ItemStack toSave = this.getStackInSlot(slot);
                
                if(toSave != null)
                {
                    String tagName = String.format("Slot%d", slot);
                    NBTTagCompound itemData = toSave.writeToNBT(new NBTTagCompound());
                    
                    toWriteOn.setCompoundTag(tagName, itemData);
                }
            }
        }
    }
    /**
     * Loads the inventory contents.
     * Leaves the tile entity untouched if supplied a null NBTCompound
     */
    @Override
    public void readFromNBT(NBTTagCompound toRead)
    {
        if(toRead != null)
        {
            super.readFromNBT(toRead);
            
            this.ownerName = toRead.getString("Owner");
            
            this.creationTime = toRead.getLong("Creation");
            
            for(int slot = 0; slot < this.getSizeInventory(); ++slot)
            {
                ItemStack loaded = null;
                String tagName = String.format("Slot%d", slot);
                
                if(toRead.hasKey(tagName))
                    loaded = ItemStack.loadItemStackFromNBT(toRead.getCompoundTag(tagName));
                    
                this.setInventorySlotContents(slot, loaded);
            }
        }
    }
    
    /**
     * Pops the first stack in line as an entity.
     */
    public void giveStack(EntityPlayer receiver)
    {
        if(!InventorySaver.instance.protectLoot.getValue()
         || Strings.isNullOrEmpty(this.ownerName)
         || receiver.username.equals(this.ownerName))
        {
            ItemStack first = null;
            
            for(int i = 0; i < this.getSizeInventory() && first == null; ++i)
            {
                first = this.getStackInSlot(i);
                
                if(first != null)
                    this.setInventorySlotContents(i, null);
            }
            
            if(first != null)
            {
                EntityItem item = new EntityItem(this.worldObj,
                                                 0.5f + this.xCoord,
                                                 1.1f + this.yCoord,
                                                 0.5f + this.zCoord,
                                                 first);
                item.setVelocity(0.0f, 0.08f, 0.0f);
                
                this.worldObj.spawnEntityInWorld(item);
                this.onInventoryChanged();
            }
        }
    }
    
    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound tileEntityData = new NBTTagCompound();
        this.writeToNBT(tileEntityData);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 0, tileEntityData);
    }
    
    @Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt)
    {
        this.readFromNBT(pkt.data);
    }
    
    ///////////////
    // IInventory
    ///////////////
    @Override
    public ItemStack getStackInSlot(int slotNum) throws ArrayIndexOutOfBoundsException
    {
        return slotNum < this.itemsInventory.length ? this.itemsInventory[slotNum] : this.armorInventory[slotNum - this.itemsInventory.length];
    }
    
    @Override
    public void setInventorySlotContents(int slotNum, ItemStack toSet) throws ArrayIndexOutOfBoundsException
    {
        if(slotNum < this.itemsInventory.length)
            this.itemsInventory[slotNum] = toSet;
        else
            this.armorInventory[slotNum - this.itemsInventory.length] = toSet;
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
    public void onInventoryChanged()
    {
        if(this.isEmpty())
        {
            this.worldObj.setBlockToAir(this.xCoord, this.yCoord, this.zCoord);
            this.invalidate();
        }
        else
            this.getWorldObj().scheduleBlockUpdate(this.xCoord, this.yCoord, this.zCoord, this.getWorldObj().getBlockId(this.xCoord, this.yCoord, this.zCoord), 2);
    }
    @Override
    public int getSizeInventory() { return this.itemsInventory.length + this.armorInventory.length; }
    @Override
    public ItemStack getStackInSlotOnClosing(int slotNum) { return null; }
    @Override
    public String getInvName() { return String.format("Bag of %s", this.ownerName); }
    @Override
    public int getInventoryStackLimit() { return 64; }
    @Override
    public boolean isUseableByPlayer(EntityPlayer user) { return !this.isInvalid() && (!InventorySaver.instance.protectLoot.getValue() || Strings.isNullOrEmpty(this.ownerName) || user.username.equals(this.ownerName)); }
    @Override
    public void openChest() {}
    @Override
    public void closeChest() {}
    @Override
    public boolean isInvNameLocalized() { return true; }
    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) { return false; }
}
