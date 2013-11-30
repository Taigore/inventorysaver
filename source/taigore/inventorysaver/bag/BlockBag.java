package taigore.inventorysaver.bag;

import java.util.Random;

import net.minecraft.block.BlockSand;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingSand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import taigore.inventorysaver.main.InventorySaver;
import cpw.mods.fml.common.registry.GameRegistry;

public class BlockBag extends BlockSand implements ITileEntityProvider
{
    public BlockBag(int blockID)
    {
        super(blockID, Material.cloth);
        
        this.setBlockUnbreakable();
        this.setResistance(Float.MAX_VALUE);
        this.setCreativeTab(null);
        this.setLightOpacity(0);
        
        this.enableStats = false;
        this.isBlockContainer = true;
        
        this.minX = 0.25f;
        this.minY = 0.0f;
        this.minZ = 0.25f;
        
        this.maxX = 0.75f;
        this.maxY = 10.0f / 16;
        this.maxZ = 0.75f;
        
        this.setUnlocalizedName("invsaver.bag");
        GameRegistry.registerBlock(this, ItemBlock.class, "BlockBag");
        GameRegistry.registerTileEntity(TileEntityBag.class, "TInvSavBag");
    }
    
    @Override
    public TileEntity createNewTileEntity(World world) { return new TileEntityBag(); }
    
    //////////
    // BlockSand
    //////////
    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World world, int posX, int posY, int posZ, Random par5Random)
    {
    	final boolean lavaImmunity = InventorySaver.instance.configuration.ignoreLava.read();
    	final boolean canFall = InventorySaver.instance.configuration.bagGravity.read();
    	
    	final AxisAlignedBB expandedBB = AxisAlignedBB.getBoundingBox(posX - 0.1f, posX + 1.1f,
    													   			  posY - 0.1f, posY + 1.1f,
    													   			  posZ - 0.1f, posZ + 1.1f);
    	final boolean nearLava = world.isAABBInMaterial(expandedBB, Material.lava); 
    			
        if(nearLava && !lavaImmunity)
        {
        	TileEntityBag tileEntity = (TileEntityBag)world.getBlockTileEntity(posX, posY, posZ);
        	
            while(tileEntity.giveStack(null) != null) {}
            
            world.setBlockToAir(posX, posY, posZ);
        }
        else if(canFall
             && !world.isRemote)
        {
            this.tryToFall(world, posX, posY, posZ);
        }
    }

    /**
     * If there is space to fall below will start this block falling
     */
    private void tryToFall(World par1World, int par2, int par3, int par4)
    {
        if (canFallBelow(par1World, par2, par3 - 1, par4) && par3 >= 0)
        {
            byte b0 = 32;

            if (!fallInstantly && par1World.checkChunksExist(par2 - b0, par3 - b0, par4 - b0, par2 + b0, par3 + b0, par4 + b0))
            {
                if (!par1World.isRemote)
                {
                    EntityFallingBag entityfallingsand = new EntityFallingBag(par1World, (double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F));
                    this.onStartFalling(entityfallingsand);
                    par1World.spawnEntityInWorld(entityfallingsand);
                }
            }
            else
            {
                par1World.setBlockToAir(par2, par3, par4);

                while (canFallBelow(par1World, par2, par3 - 1, par4) && par3 > 0)
                {
                    --par3;
                }

                if (par3 > 0)
                {
                    par1World.setBlock(par2, par3, par4, this.blockID);
                }
            }
        }
    }
    
    @Override
    public void onStartFalling(EntityFallingSand fallingBlock)
    {
        //I don't know why I have to do this, but it seems that the falling block
        //appears shifted towards the 0, if the original coordinate was negative.
        //Why this doesn't happen on vanilla falling blocks is a mistery
        NBTTagCompound tileEntityData = new NBTTagCompound();
        TileEntityBag bag = (TileEntityBag) fallingBlock.worldObj.getBlockTileEntity((int)(fallingBlock.posX - 0.5f), (int)(fallingBlock.posY - 0.5f), (int)(fallingBlock.posZ - 0.5f));
        
        if(bag != null)
            bag.writeToNBT(tileEntityData);
        else
            throw new RuntimeException(String.format("Taigore InventorySaver: couldn't find tile entity for block %d %d %d. Throwing to prevent loss of items. Place something under the block itself to prevent it from falling and making this happen again.", (int)fallingBlock.posX, (int)fallingBlock.posY, (int)fallingBlock.posZ));
        
        fallingBlock.fallingBlockTileEntityData = tileEntityData;
    }
    
    //////////
    // Block
    //////////
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer activator, int par6, float par7, float par8, float par9)
    {
        if(activator.isSneaking())
        {
            if(!world.isRemote)
                activator.openGui(InventorySaver.instance, 1, world, x, y, z);
        }
        else if(!world.isRemote)
            ((TileEntityBag)world.getBlockTileEntity(x, y, z)).giveStack(activator);
        
        return true;
    }
    
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon(InventorySaver.resource("blockbag"));
    }
    
    @Override
    public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) { return false; }
    @Override
    public int getRenderType() { return this.blockID; }
    @Override
    public boolean renderAsNormalBlock() { return false; }
    @Override
    public boolean canEntityDestroy(World world, int x, int y, int z, Entity entity) { return false; }
    @Override
    public boolean canHarvestBlock(EntityPlayer player, int meta) { return false; }
    @Override
    public boolean isOpaqueCube() { return false; }
}
