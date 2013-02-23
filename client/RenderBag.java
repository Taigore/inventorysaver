package taigore.inventorysaver.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import taigore.inventorysaver.EntityBag;
import taigore.inventorysaver.InventorySaver;

public class RenderBag extends Render
{
	ModelBase bagModel;
	
	public RenderBag()
	{
		this.shadowSize = 0.0f;
		this.bagModel = new ModelBag();
	}
	
	public void bagRendering(EntityBag toRender, double renderX, double renderY, double renderZ, float renderYaw, float partTick)
	{
		try
		{
			GL11.glPushMatrix();
			GL11.glTranslatef((float)renderX, (float)renderY, (float)renderZ);
			GL11.glRotatef(180.0F - renderYaw, 0.0F, 1.0F, 0.0F);
			this.loadTexture(InventorySaver.proxy.BAG_TEXTURE);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			GL11.glScalef(-1.0f, -1.0f, 1.0F);
			this.bagModel.render(toRender, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f / 16f);
			GL11.glPopMatrix();
			
			this.renderName(toRender, renderX, renderY, renderZ);
		}
		catch (Exception graphicException)
        {
            graphicException.printStackTrace();
        }
	}
	
	protected void renderName(EntityBag toRender, double renderX, double renderY, double renderZ)
    {
        if (Minecraft.isGuiEnabled() && toRender.renderNameTicks > 0)
        {
            float var8 = 1.6F;
            float var9 = 0.016666668F * var8;
            double playerDistance = toRender.getDistanceSqToEntity(this.renderManager.livingPlayer);
            float nameTagHeight = (toRender.height + toRender.yOffset) * 1.1f;
            String nameTag = toRender.getOwnerName() + "'s spoils";

            if (playerDistance < (64.0d * 64.0d))
            {
                GL11.glPushMatrix();
                GL11.glTranslatef((float)renderX, (float)renderY + nameTagHeight, (float)renderZ);
                GL11.glNormal3f(0.0F, 1.0f, 0.0f);
                GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                GL11.glScalef(-var9, -var9, var9);
                
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glTranslatef(0.0F, 0.25f / var9, 0.0F);
                GL11.glDepthMask(false);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                
                //Paints the dark grey background
                Tessellator tessellator = Tessellator.instance;
                FontRenderer nameRenderer = this.getFontRendererFromRenderManager();
                int halfNameTagWidth = nameRenderer.getStringWidth(nameTag) / 2;
                tessellator.startDrawingQuads();
                tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
                tessellator.addVertex((double)(-halfNameTagWidth - 1), -1.0D, 0.0D);
                tessellator.addVertex((double)(-halfNameTagWidth - 1), 8.0D, 0.0D);
                tessellator.addVertex((double)(halfNameTagWidth + 1), 8.0D, 0.0D);
                tessellator.addVertex((double)(halfNameTagWidth + 1), -1.0D, 0.0D);
                tessellator.draw();
                
                //Paints the semi-transparent string you can see through walls
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                nameRenderer.drawString(nameTag, -halfNameTagWidth, 0, 0x80FFFFFF);
                
                //Paints the white string you can see while in line of sight
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glDepthMask(true);
                nameRenderer.drawString(nameTag, -halfNameTagWidth, 0, 0xFFFFFFFF);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
            }
        }
    }
	
	@Override
	public void doRender(Entity toRender, double renderX, double renderY, double renderZ, float renderYaw, float partTick)
	{
		this.bagRendering((EntityBag)toRender, renderX, renderY, renderZ, renderYaw, partTick);
	}

}
