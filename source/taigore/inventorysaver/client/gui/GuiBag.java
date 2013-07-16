package taigore.inventorysaver.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import taigore.inventorysaver.InventorySaver;
import taigore.inventorysaver.entity.item.EntityBag;
import taigore.inventorysaver.inventory.ContainerBag;

public class GuiBag extends GuiContainer
{
    public static final ResourceLocation guiBackground = new ResourceLocation(InventorySaver.modId.toLowerCase(), "textures/gui/BagGui.png");
    
	public GuiBag(InventoryPlayer playerInventory, EntityBag openedBag)
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
		this.mc.func_110434_K().func_110577_a(guiBackground);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, xSize, ySize);
	}
	
	@Override
	public void initGui()
	{
		super.initGui();
		
		int guiX = (this.width - this.xSize) / 2;
		int guiY = (this.height - this.ySize) / 2;
		
		this.buttonList.add(new GuiButton(1, guiX + 9, guiY +  81, 16, 16, "T"));
	}
	/**
	 * The only button on the interface tries taking everything from the inventory.
	 */
	protected void actionPerformed(GuiButton pressed)
	{
        if(pressed.id == 1)
        {
            ContainerBag container = (ContainerBag)this.inventorySlots;
            
            for(int i = 0; i < container.bagInventory.getSizeInventory(); ++i)
            {
                Slot clicked = container.getSlotFromInventory(container.bagInventory, i);
                
                this.handleMouseClick(clicked, clicked.slotNumber, 0, 1);
            }
        }
	}
}
