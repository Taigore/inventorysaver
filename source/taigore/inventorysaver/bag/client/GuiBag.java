package taigore.inventorysaver.bag.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import taigore.inventorysaver.bag.ContainerBag;
import taigore.inventorysaver.bag.InventoryBag;
import taigore.inventorysaver.main.InventorySaver;

public class GuiBag extends GuiContainer
{
    public static final ResourceLocation guiBackground = InventorySaver.resourceLoc("textures/gui/BagGui.png");
    
	public GuiBag(InventoryPlayer playerInventory, InventoryBag openedBag)
	{
	    super(new ContainerBag(playerInventory, openedBag));
	    this.xSize = 182;
	    this.ySize = 182;
	}
	
	/////////////////
	// GuiContainer
	/////////////////
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
	{
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		this.mc.renderEngine.bindTexture(guiBackground);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, xSize, ySize);
		
		String playerName = ((ContainerBag)this.inventorySlots).bagInventory.ownerName;
		Minecraft.getMinecraft().fontRenderer.drawString(playerName, this.guiLeft + 4, this.guiTop - 8, -1);
	}
	
	@Override
	public void initGui()
	{
		super.initGui();
		/*
		int guiX = (this.width - this.xSize) / 2;
		int guiY = (this.height - this.ySize) / 2;
		
		this.buttonList.add(new GuiButton(1, guiX + 9, guiY +  81, 16, 16, "T"));
		//*/
	}
	/**
	 * The only button on the interface tries taking everything from the inventory.
	 */
	protected void actionPerformed(GuiButton pressed)
	{
        if(pressed.id == 1)
        {
            ContainerBag container = (ContainerBag)this.inventorySlots;
            container.bagInventory.moveIntoPlayerInventory(container.playerInventory);
            //TODO Mandare pacchetto
        }
	}
}
