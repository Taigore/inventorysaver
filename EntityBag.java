package taigore.inventorysaver;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.src.DamageSource;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;

public class EntityBag extends Entity implements IInventory
{
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
		
		for(EntityItem toSave : drops)
		{
			if(toSave != null)
			{
				this.bagContents.add(toSave.item);
			}
		}
		
		if(ownerName == null) ownerName = "";
		this.dataWatcher.updateObject(dwOwnerName, ownerName);
	}
	@Override
	protected void entityInit()
	{
		this.dataWatcher.addObject(this.dwOwnerName, String.valueOf(""));
	}
	
	///////////////
	// IInventory
	///////////////
	//The contents of this bag. IInventory's methods treat it as an inventory
	//with size of 28n, with (n - 1) < bagContents.size() / 28 <= n
	public final LinkedList<ItemStack> bagContents = new LinkedList();
	
	/**
	 * Returns the item stack in the given slot.
	 * Returns null if the slotNum is over the amount of available stacks.
	 * Also removes null elements from the list.
	 */
	@Override
	public ItemStack getStackInSlot(int slotNum)
	{
		if(this.bagContents.size() > slotNum)
			return this.bagContents.get(slotNum);
		else
			return null;
	}
	@Override
	public ItemStack decrStackSize(int slotNum, int amount)
	{
		ItemStack inSlot = this.getStackInSlot(slotNum);
		
		if(inSlot != null && amount > 0)
		{
			if(inSlot.stackSize <= amount)
			{
				this.bagContents.remove(slotNum);
				return inSlot;
			}
			else
			{
				return inSlot.splitStack(amount);
			}
		}
		else
			return null;
	}
	@Override
	public void setInventorySlotContents(int slotNum, ItemStack toSet)
	{
		if(toSet == null || toSet.stackSize == 0)
		{
			if(this.bagContents.size() > slotNum)
				this.bagContents.remove(slotNum);
		}
		else
		{
			if(this.bagContents.size() <= slotNum)
				this.bagContents.add(toSet);
			else
				this.bagContents.set(slotNum, toSet);
		}
	}
	@Override
	public int getSizeInventory() { return 28; }
	@Override
	public ItemStack getStackInSlotOnClosing(int slotNum) { return null; }
	@Override
	public String getInvName() { return this.getOwnerName(); }
	@Override
	public int getInventoryStackLimit() { return 64; }
	@Override
	public void onInventoryChanged() {}
	@Override
	public boolean isUseableByPlayer(EntityPlayer user) { return true; }
	@Override
	public void openChest() {}
	@Override
	public void closeChest() {}
	
	///////////
	// Entity
	///////////
	protected EntityPlayer clicker = null;
	
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
    	if(!this.worldObj.isRemote)
    	{
	    	while(!this.bagContents.isEmpty())
	    		this.popItemStack();
    	}
    	
        super.setDead();
    }
    /**
     * Updates entity position.
     * 
     */
    @Override
    public void onUpdate()
    {
    	super.onUpdate();
    	
    	if(!this.worldObj.isRemote && this.bagContents.isEmpty())
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
     *	Simply pops up a stack of the item contents.
     */
    @Override
    public boolean interact(EntityPlayer interacting)
    {
    	if(interacting.isSneaking())
    	{
    		//Sets up this field to allow for clicked bag recognition from the GuiHandler.
    		//Only the first click actually triggers an opened bag, though. All subsequent clicks 
    		//won't work, until both the client and the server side GUIs has been opened.
    		if(this.clicker == null)
    		{
	    		this.clicker = interacting;
	    		
	    		if(!this.worldObj.isRemote)
	    		{
					interacting.openGui(InventorySaver.instance, 1, this.worldObj, (int)Math.floor(this.posX), (int)Math.floor(this.posY), (int)Math.floor(this.posZ));
	    		}
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
		
		for(ItemStack stackToSave : this.bagContents)
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
			
			this.bagContents.add(loadedStack);
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
	 * This function pops up a dropped item and removes it from the bag.
	 * Returns false if no item is left or if spawnEntityInWorld returns false.
	 */
	public boolean popItemStack()
	{
		if(this.bagContents != null && !this.bagContents.isEmpty())
		{
			ItemStack toDrop = null;
			
			while(toDrop == null && this.bagContents != null && !this.bagContents.isEmpty())
				toDrop = this.bagContents.poll();
			
			if(toDrop != null && !this.worldObj.isRemote)
			{
				EntityItem droppedItem = new EntityItem(this.worldObj,
														this.posX, this.posY, this.posZ,
														toDrop);
			
				droppedItem.motionX = this.motionX;
				droppedItem.motionY = 0.30f + this.motionY;
				droppedItem.motionZ = this.motionZ;
				droppedItem.delayBeforeCanPickup = 10;
				
				return this.worldObj.spawnEntityInWorld(droppedItem);
			}
		}
		
		return false;
	}
}
