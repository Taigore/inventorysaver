package taigore.inventorysaver.entity;

import taigore.inventorysaver.InventorySaver;
import net.minecraft.entity.item.EntityFallingSand;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFallingBag extends EntityFallingSand
{
    {
        this.shouldDropItem = false;
        this.preventEntitySpawning = true;
        this.setSize(0.5f, 0.625f);
        this.yOffset = this.height / 2.0F;
    }
    
    public EntityFallingBag(World par1World)
    {
        super(par1World);
        
        this.blockID = InventorySaver.instance.bag.getBlock().blockID;
        this.metadata = 0;
        
    }
    public EntityFallingBag(World par1World, double par2, double par4, double par6)
    {
        super(par1World, par2, par4, par6, InventorySaver.instance.bag.getBlock().blockID, 0);
        
        this.setPosition(par2, par4, par6);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = par2;
        this.prevPosY = par4;
        this.prevPosZ = par6;
    }
    
    @Override
    public void onUpdate()
    {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.posY);
        int z = MathHelper.floor_double(this.posZ);
        
        if(this.posY > 256 || (this.fallTime > 1 && !this.worldObj.isAirBlock(x, y, z)))
        {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            ++this.fallTime;
            this.motionY -= 0.03999999910593033D;
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.9800000190734863D;
            this.motionY *= 0.9800000190734863D;
            this.motionZ *= 0.9800000190734863D;
        }
        else
            super.onUpdate();
    }
    
    @Override
    public boolean canBeCollidedWith() { return false; }
}
