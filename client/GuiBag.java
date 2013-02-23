package taigore.inventorysaver.client;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.GuiContainer;
import net.minecraft.src.InventoryPlayer;
import taigore.inventorysaver.ContainerBag;
import taigore.inventorysaver.EntityBag;
import taigore.inventorysaver.ProxyCommonInvSaver;

public class GuiBag extends GuiContainer
{
	public GuiBag(InventoryPlayer playerInventory, EntityBag openedBag)
	{
		super(new ContainerBag(playerInventory, openedBag));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
	{
		int guiBackground = this.mc.renderEngine.getTexture(ProxyCommonInvSaver.BAGGUI_TEXTURE);
		int guiX = (this.width - this.xSize) / 2;
		int guiY = (this.height - this.ySize) / 2;
		
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		this.mc.renderEngine.bindTexture(guiBackground);
		this.drawTexturedModalRect(guiX, guiY, 0, 0, xSize, ySize);
	}
}
