package taigore.inventorysaver.entity.item;

import java.util.LinkedList;
import java.util.List;

import taigore.inventorysaver.InventorySaver;
import taigore.inventorysaver.inventory.InventoryBag;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;

public class EntityBag extends Entity
{
    //Constants
    private final static String ownerNameTag = "OwnerName";
    private final static String onlyOwnerTag = "OnlyOwnerLoots";
    private final static String ignoreLavaTag = "IgnoresLava";
    
    private final static int dwOwnerName = 30;
    private final static int dwFlags = 29;
    
    private final static int onlyOwnerPosition = 0;
    private final static int ignoreLavaPosition = 1;
    
	public final InventoryBag inventory = new InventoryBag(this);
	
	/**
     * Default world only construct.
     */
    public EntityBag(World spawnWorld)
    {
        super(spawnWorld);
        
        this.setSize(8.0f / 16, 11.0f / 16);
        this.yOffset = this.height / 2.0f;
        
        if(!spawnWorld.isRemote)
        {
            this.setBagFlag(onlyOwnerPosition, InventorySaver.instance.onlyOwnerLoots);
            this.setBagFlag(ignoreLavaPosition, InventorySaver.instance.bagIgnoresLava);
        }
        
        this.preventEntitySpawning = false;
    }
    /**
     * Constructor that assigns a list of ItemStack as bagContents, retrieving
     * them from the EntityItem list provided, and sets the bag owner to the
     * given name.
     */
    public EntityBag(World spawnWorld, List<EntityItem> drops, String ownerName)
    {
        this(spawnWorld);
        
        List<ItemStack> droppedStacks = new LinkedList();
        
        for(EntityItem toSave : drops)
            if(toSave != null)
                droppedStacks.add(toSave.getEntityItem());
        
        if(!this.inventory.addItemCollection(droppedStacks))
            for(ItemStack toDrop : droppedStacks)
                this.entityDropItem(toDrop, this.yOffset);
        
        this.setOwnerName(ownerName);
    }
    
    ///////////////////////
    // Synced data access
	///////////////////////
	public void setOwnerName(String ownerName)
	{
	    if(ownerName == null) ownerName = "";
	    
	    this.dataWatcher.updateObject(dwOwnerName, ownerName);
	}
	public String getOwnerName() { return this.dataWatcher.getWatchableObjectString(dwOwnerName); }
	private void setupOwnerName() { this.dataWatcher.addObject(dwOwnerName, ""); } 
	
	public void setBagFlag(int flagPosition, boolean value)
	{
	    byte flags = this.dataWatcher.getWatchableObjectByte(dwFlags);
	    
	    if(value)
	        flags |= 1 << flagPosition;
	    else
	        flags &= ~(1 << flagPosition);
	    
	    this.dataWatcher.updateObject(dwFlags, flags);
	}
	public boolean getBagFlag(int flagPosition)
	{
	    byte flags = this.dataWatcher.getWatchableObjectByte(dwFlags);
	    
	    return ((flags >> flagPosition) & 1) != 0;
	}
	private void setupFlags() { this.dataWatcher.addObject(dwFlags, (byte)0); }
	
	public boolean ignoresLava() { return this.getBagFlag(ignoreLavaPosition); }
	public boolean onlyOwner() { return this.getBagFlag(onlyOwnerPosition) && !this.getOwnerName().isEmpty(); }
	
	//////////
	// Rules
	//////////
	public boolean canLoot(EntityPlayer looter)
	{
	    MinecraftServer instance = FMLCommonHandler.instance().getMinecraftServerInstance();
	    
	    if(looter != null && instance != null)
        {
	        if(InventorySaver.instance.onlyOwnerLoots)
	        {
        	    boolean isOwner = this.getOwnerName().contentEquals(looter.username);
        	    boolean isOp = instance.getConfigurationManager().getOps().contains(looter.username.toLowerCase());
        	    
        	    if(isOp && InventorySaver.instance.canOpsLoot)
        	        return true;
        	    else
        	        return !this.onlyOwner() || isOwner;
	        }
	        else return true;
        }
	    else return false;
	}
	
	private boolean ignoreLavaDamage() { return this.ignoresLava() || (this.onlyOwner() && !this.getOwnerName().isEmpty()); }
	
	///////////
	// Entity
	///////////
	@Override
    protected void entityInit()
	{
	    this.setupOwnerName();
	    this.setupFlags();
	}
	
	/**
     * Called when the entity is attacked.
     * The bag is invulnerable, but it will be destroyed if it gets any damage from
     * being in lava or falling into the void.
     * Hitting it with anything else will display the owner name.
     * If only the owner can loot, lava damage is disabled to prevent griefing.
     */
	@Override
	public boolean attackEntityFrom(DamageSource damageType, int damageAmount)
    {
        if(damageType == DamageSource.outOfWorld)
        {
            this.setDead();
            return true;
        }
        else if(damageType == DamageSource.lava)
        {
            if(!this.ignoreLavaDamage())
            {
                this.setDead();
                return true;
            }
        }
        
        return false;
    }
	/**
     * Will get destroyed next tick.
     * Also pops out every content of the bag.
     */
    public void setDead()
    {
       	this.inventory.popNumStack(this.inventory.getSizeInventory());
        super.setDead();
    }
    /**
     * Destroys the bag if empty.
     * Also makes the bag fall.
     * If only the owner can access the bag, fall and movement are disabled
     * to prevent griefing.
     */
    @Override
    public void onUpdate()
    {
    	super.onUpdate();
    	
    	if(!this.isDead)
    	{
    		if(!this.onlyOwner())
    		{
        		if(this.onGround && this.motionY < 0.0d)
        			this.motionY = 0.0d;
        		else
        			this.motionY -= 9.8d / (20 * 20);
        		
        		this.moveEntity(this.motionX, this.motionY, this.motionZ);
        		
        		this.motionX *= this.isCollidedVertically ? 0.5d : 0.95d;
        		this.motionY *= this.isCollidedHorizontally ? 0.5d : 0.95d;
        		this.motionZ *= this.isCollidedVertically ? 0.5d : 0.95d;
    		}
    		
    		if(!this.worldObj.isRemote && this.ticksExisted % 20 == 0)
    		    if(this.inventory.isEmpty())
    		        this.setDead();
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
        if(this.canLoot(interacting))
        {
        	if(interacting.isSneaking())
        	{
        		if(!this.worldObj.isRemote)
    				interacting.openGui(InventorySaver.instance, 1, this.worldObj, this.entityId, 0, 0);
        	}
        	else
        		this.inventory.popNumStack(1);
        }
    	
    	return true;
    }
    
	@Override
    public boolean canBeCollidedWith() { return true; }
	
	//////////////
	// Save/Load
	//////////////
	@Override
	protected void writeEntityToNBT(NBTTagCompound toSave)
	{
		toSave.setString(ownerNameTag, this.getOwnerName());
		toSave.setBoolean(onlyOwnerTag, this.onlyOwner());
		toSave.setBoolean(ignoreLavaTag, this.ignoresLava());

		this.inventory.writeToNBT(toSave);
	}
	@Override
	protected void readEntityFromNBT(NBTTagCompound toLoad)
	{
		//Loading owner name
		String ownerName = toLoad.getString(ownerNameTag);
		this.setOwnerName(ownerName);
		
		this.setBagFlag(onlyOwnerPosition, toLoad.getBoolean(onlyOwnerTag));
		this.setBagFlag(ignoreLavaPosition, toLoad.getBoolean(ignoreLavaTag));
		
		//Loading items
		this.inventory.readFromNBT(toLoad);
	}
}
