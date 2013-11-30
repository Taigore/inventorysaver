package taigore.inventorysaver.bag;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import taigore.inventorysaver.main.InventorySaver;

public class TileEntityBag extends TileEntity
{
    //FIELDS
    private long creationTime_ = -1;
    public final InventoryBag inventory_;
    
    //METHODS
    public TileEntityBag()
    {
    	inventory_ = new InventoryBag();
    }
    
    public TileEntityBag(String ownerName, ItemStack[] armorItems, ItemStack[] otherItems)
    {
    	inventory_ = new InventoryBag(ownerName, armorItems, otherItems);
    }
    
    /**
     * Pops the first stack in line as an entity.
     */
    public EntityItem giveStack(EntityPlayer receiver)
    {
    	final boolean canBeLooted = receiver == null || inventory_.isUseableByPlayer(receiver);
    	EntityItem value = null;
    	
        if(canBeLooted)
        {
            final ItemStack first = inventory_.removeFirstStack();
            
            if(first != null && !worldObj.isRemote)
            {
                value = new EntityItem(this.worldObj,
                					   0.5f + this.xCoord,
                					   1.1f + this.yCoord,
                					   0.5f + this.zCoord,
                					   first);
                
                value.motionX = 0.0f;
                value.motionY = 0.8f;
                value.motionZ = 0.0f;
                
                worldObj.spawnEntityInWorld(value);
                inventory_.onInventoryChanged();
            }
        }
        
        return value;
    }
    
    public void moveToPlayerInventory(EntityPlayer receiver)
    {
    	final boolean canBeLooted = inventory_.isUseableByPlayer(receiver);
    	
    	if(canBeLooted)
	    	inventory_.moveIntoPlayerInventory(receiver.inventory);
    }
    
    ///////////////
    // TileEntity
    ///////////////
    @Override
    public void updateEntity()
    {
    	if(this.creationTime_ <= 0) this.creationTime_ = this.worldObj.getTotalWorldTime();
    	else
    	{
    		final long cleanupTime = InventorySaver.instance.configuration.cleanupTime.read() * 3600 * 20;
    		final long elapsedTicks = this.worldObj.getTotalWorldTime() - this.creationTime_;
    		
    		if(cleanupTime > 0
    		&& elapsedTicks > cleanupTime)
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
            
            toWriteOn.setLong("Creation", creationTime_);
            
            inventory_.writeToNBT(toWriteOn);
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
            
            creationTime_ = toRead.getLong("Creation");
            
            inventory_.readFromNBT(toRead);
        }
    }
    
    @Override
    public Packet getDescriptionPacket()
    {
        final NBTTagCompound tileEntityData = new NBTTagCompound();
        this.writeToNBT(tileEntityData);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 0, tileEntityData);
    }
    
    @Override
    public boolean isInvalid()
    {
    	return super.isInvalid() || inventory_.isEmpty();
    }
    
    @Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt)
    {
        this.readFromNBT(pkt.data);
    }
}
