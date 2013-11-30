package taigore.inventorysaver.bag.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import taigore.inventorysaver.bag.EntityFallingBag;
import taigore.inventorysaver.main.InventorySaver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFallingBag extends Render
{
    private final RenderBlocks renderBlocks = new RenderBlocks();

    public RenderFallingBag() { this.shadowSize = 0.25F; }

    /**
     * The actual render method that is used in doRender
     */
    public void doRenderFallingBag(EntityFallingBag entityBag, double rendX, double rendY, double rendZ, float rendYaw, float interpolation)
    {
        World world  = entityBag.worldObj;
        Block block = InventorySaver.instance.configuration.bag.getBlock();
        
        int posX = MathHelper.floor_double(entityBag.posX);
        int posY = MathHelper.floor_double(entityBag.posY);
        int posZ = MathHelper.floor_double(entityBag.posZ);

        if (world.getBlockId(posX, posY, posZ) != block.blockID)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef((float)rendX, (float)rendY, (float)rendZ);
            GL11.glDisable(GL11.GL_LIGHTING);
            
            this.bindTexture(TextureMap.locationBlocksTexture);
            
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.setTranslation((double)((float)(-posX) - 0.5F), (double)((float)(-(posY - entityBag.yOffset)) - 0.5F), (double)((float)(-posZ) - 0.5F));
            
            this.renderBlocks.blockAccess = world;
            this.renderBlocks.renderBlockByRenderType(block, posX,
                                                             posY,
                                                             posZ);
            
            tessellator.setTranslation(0.0D, 0.0D, 0.0D);
            tessellator.draw();

            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        }
    }
    
    @Override
    protected ResourceLocation getEntityTexture(Entity entityBag)
    {
        return InventorySaver.resourceLoc("textures/blocks/blockbag.png");
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probability, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRenderFallingBag((EntityFallingBag)par1Entity, par2, par4, par6, par8, par9);
    }
}
