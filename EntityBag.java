package taigore.inventorysaver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;

public class EntityBag extends Entity
{
	public final InventoryBag inventory = this.new InventoryBag();
	
	/////////////////
	// Constructors
	/////////////////
	/**
	 * Default world only construct.
	 */
	public EntityBag(World spawnWorld)
	{
		super(spawnWorld);
		
		this.setSize(8.0f / 16, 11.0f / 16);
	    this.yOffset = this.height / 2.0f;
	    
	    this.motionX = 0.0D;
	    this.motionY = 0.0D;
	    this.motionZ = 0.0D;
	    this.prevPosX = this.posX;
	    this.prevPosY = this.posY;
	    this.prevPosZ = this.posZ;
	    
	    this.isDead = false;
	    this.preventEntitySpawning = false;
	}
	/**
	 * Constructor that assigns a list of ItemStack as bagContents, retrieving
	 * them from the EntityItem list provided, and sets the bag owner to the
	 * given name.
	 * @param spawnWorld
	 * @param drops
	 * @param ownerName
	 */
	public EntityBag(World spawnWorld, List<EntityItem> drops, String ownerName)
	{
		this(spawnWorld);
		
		int index = 0;
		
		for(EntityItem toSave : drops)
		{
			if(toSave != null)
			{
				//Func_92014_d: method to access dataWatcher object 10
				//Returns the item stack held by the entity.
				this.inventory.inventory.add(toSave.func_92014_d());
				
				index += 1;
			}
		}
		
		if(ownerName == null) ownerName = "";
		this.dataWatcher.updateObject(dwOwnerName, ownerName);
	}
	@Override
	protected void entityInit()
	{
		this.dataWatcher.addObject(dwOwnerName, String.valueOf(""));
	}
	
	///////////
	// Entity
	///////////
	/**
     * Called when the entity is attacked.
     * The bag is invulnerable, but it will be destroyed if it gets any damage from
     * being in lava or falling into the void.
     * Hitting it with anything else will display the owner name.
     */
	@Override
	public boolean attackEntityFrom(DamageSource damageType, int damageAmount)
    {
        if(damageType == DamageSource.lava || damageType == DamageSource.outOfWorld)
        {
        	this.setDead();
        	return true;
        }
        else
        {
        	//Shows the bag owner's name for 3.5 seconds when an attack (punch or whatever) comes from a player
        	if(this.worldObj.isRemote && damageType.getEntity() == FMLClientHandler.instance().getClient().thePlayer)
        	{
        		this.renderNameTicks = 70;
        	}
        	return false;
        }
    }
	/**
     * Will get destroyed next tick.
     * Also pops out every content of the bag, but only server side.
     */
    public void setDead()
    {
    	while(this.popItemStack()) {}
    	
        super.setDead();
    }
    /**
     * Destroys the bag if empty.
     * Also makes the bag fall.
     */
    @Override
    public void onUpdate()
    {
    	super.onUpdate();
    	
    	if(!this.worldObj.isRemote && this.inventory.inventory.isEmpty())
    		this.setDead();
    	
    	if(!this.isDead)
    	{
    		if(this.renderNameTicks > 0) this.renderNameTicks -= 1;
    		
    		if(this.onGround && this.motionY < 0.0d)
    			this.motionY = 0.0d;
    		else
    			this.motionY -= 9.8d / (20 * 20);
    		
    		this.moveEntity(this.motionX, this.motionY, this.motionZ);
    		
    		this.motionX *= this.isCollidedVertically ? 0.5d : 0.95d;
    		this.motionY *= this.isCollidedHorizontally ? 0.5d : 0.95d;
    		this.motionZ *= this.isCollidedVertically ? 0.5d : 0.95d;
    	}
    }
    /** 
     *	Called on player right click on the bag.
     *	Pops out one of the stacks if a normal right click,
     *	opens the GUI if sneak clicked.
     */
    @Override
    public boolean interact(EntityPlayer interacting)
    {
    	if(interacting.isSneaking())
    	{
    		//Sets up this field to allow for clicked bag recognition from the GuiHandler.
    		//Only the first click actually triggers an opened bag, though. All subsequent clicks 
    		//won't work, until both the client and the server side GUIs has been opened.
    		if(!this.worldObj.isRemote)
    		{
				interacting.openGui(InventorySaver.instance, 1, this.worldObj, this.entityId, 0, 0);
    		}
    		return true;
    	}
    	else
    	{
    		this.popItemStack();
    	
    		return true;
    	}
    }
    
	@Override
    public boolean canBeCollidedWith() { return true; }
	
	//////////////
	// Save/Load
	//////////////
	private final static String ownerNameTag = "OwnerName";
	private final static String contentsListTag = "ContentsList";
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound toSave)
	{
		NBTTagList contentsList = new NBTTagList();
		
		for(ItemStack stackToSave : this.inventory.inventory)
		{
			NBTTagCompound itemNBT = stackToSave.writeToNBT(new NBTTagCompound());
			
			contentsList.appendTag(itemNBT);
		}
		
		toSave.setString(ownerNameTag, this.getOwnerName());
		toSave.setTag(contentsListTag, contentsList);
	}
	@Override
	protected void readEntityFromNBT(NBTTagCompound toLoad)
	{
		//Loading owner name
		String ownerName = toLoad.getString(ownerNameTag);
		this.dataWatcher.updateObject(dwOwnerName, ownerName);
		
		//Loading items
		NBTTagList contentsList = toLoad.getTagList(contentsListTag);
		int stacksAmount = contentsList.tagCount();
		
		for(int i = 0; i < stacksAmount; i++)
		{
			NBTTagCompound loadedStackNBT = (NBTTagCompound)contentsList.tagAt(i);
			ItemStack loadedStack = ItemStack.loadItemStackFromNBT(loadedStackNBT);
			
			this.inventory.inventory.add(loadedStack);
		}
	}
	
	//////////////
	// Data Sync
	//////////////
	private final static int dwOwnerName = 30;
	
	/**
	 * Returns the owner name of the bag.
	 */
	public String getOwnerName() { return this.dataWatcher.getWatchableObjectString(dwOwnerName); }
	
	/////////////////////
	// Specific methods
	/////////////////////
	public int renderNameTicks = 0;
	
	/**
	 * This function pops an item in the bag and removes it from the inventory.
	 * Spawn happens only server side, inventory cleanup happens on both.
	 */
	public boolean popItemStack()
	{
		if(!this.inventory.inventory.isEmpty())
		{
			ItemStack toDrop = null;
			
			while(toDrop == null && !this.inventory.inventory.isEmpty())
				toDrop = this.inventory.inventory.poll();
			
			if(toDrop != null && !this.worldObj.isRemote)
			{
				EntityItem droppedItem = this.entityDropItem(toDrop, yOffset);
			
				droppedItem.motionX = this.motionX;
				droppedItem.motionY = 0.30f + this.motionY;
				droppedItem.motionZ = this.motionZ;
				droppedItem.delayBeforeCanPickup = 10;
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Multi-paged inventory.
	 * Standard slot access available through the inventory field.
	 */
	public class InventoryBag implements IInventory
	{
		//The two pages of this multi-page inventory
		public static final int pages = 2;
		public final LinkedList<ItemStack> inventory = new LinkedList();
		public int page = 0;
		
		private InventoryBag() {}

		public EntityBag getEntity() { return EntityBag.this; }
		
		public int getCurrentPage() { return this.page; }
		public void switchPage()
		{
			int currentPage = this.getCurrentPage() + 1;
			
			if(currentPage >= pages) currentPage = 0;
			
			this.page = currentPage;
		}
		
		/**
		 * Returns the ItemStack in the given slot in the current page.
		 */
		@Override
		public ItemStack getStackInSlot(int slotNum)
		{
			if(slotNum < 0 || slotNum >= this.getSizeInventory())
				throw new IllegalArgumentException(String.format("Taigore InventorySaver: slot number out of bounds, %d", slotNum));
			else
			{
				int actualSlotNum = this.getSizeInventory() * this.getCurrentPage() + slotNum;
				
				if(actualSlotNum < this.inventory.size())
					return this.inventory.get(actualSlotNum);
				else
					return null;
			}
		}
		/**
		 * Returns a substack of what is in the specified slot in the current page.
		 * Returns the stack if the desired size is equal to or greater than the
		 * actual stack size.
		 */
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
		/**
		 * Sets the stack in the given slot on the current page.
		 * Deletes the stack if toSet == null or has a stackSize of 0.
		 */
		@Override
		public void setInventorySlotContents(int slotNum, ItemStack toSet)
		{
			if(slotNum < this.getSizeInventory())
			{
				int actualSlotNum = this.getSizeInventory() * this.getCurrentPage() + slotNum;
				
				if(actualSlotNum < this.inventory.size())
				{
					if(toSet != null && toSet.stackSize != 0)
						this.inventory.set(actualSlotNum, toSet);
					else
						this.inventory.remove(actualSlotNum);
				}
				else
					this.inventory.add(toSet);
			}
			else
				throw new IllegalArgumentException(String.format("Taigore InventorySaver: slotNum out of bounds %d", slotNum));
		}
		/**
		 * Actually specifies the size of an inventory page.
		 */
		@Override
		public int getSizeInventory() { return 24; }
		@Override
		public ItemStack getStackInSlotOnClosing(int slotNum) { return null; }
		@Override
		public String getInvName() { return EntityBag.this.getOwnerName(); }
		@Override
		public int getInventoryStackLimit() { return 64; }
		@Override
		public void onInventoryChanged() {}
		@Override
		public boolean isUseableByPlayer(EntityPlayer user) { return !EntityBag.this.isDead; }
		@Override
		public void openChest() {}
		@Override
		public void closeChest() {}
	}
}
