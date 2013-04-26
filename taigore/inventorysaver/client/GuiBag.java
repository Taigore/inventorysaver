package taigore.inventorysaver.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import taigore.inventorysaver.ContainerBag;
import taigore.inventorysaver.EntityBag;
import taigore.inventorysaver.InventorySaver;

public class GuiBag extends GuiContainer
{
	public GuiBag(InventoryPlayer playerInventory, EntityBag openedBag)
	{
		super(new ContainerBag(playerInventory, openedBag));
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
	{
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		this.mc.renderEngine.bindTexture(InventorySaver.proxy.BAGGUI_TEXTURE);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, xSize, ySize);
	}
	
	@Override
	public void initGui()
	{
		super.initGui();
		
		int guiX = (this.width - this.xSize) / 2;
		int guiY = (this.height - this.ySize) / 2;
		
		this.buttonList.add(new GuiButton(1, guiX + 33, guiY +  6, 20, 20, "T"));
		this.buttonList.add(new GuiButton(2, guiX + 33, guiY + 42, 20, 20, "S"));
	}
	
	protected void actionPerformed(GuiButton pressed)
	{
        //id is the id you give your button
        switch(pressed.id)
        {
        	case 1: //Take everything (Button T)
        		Slot firstBagSlot = this.inventorySlots.getSlotFromInventory(((ContainerBag)this.inventorySlots).bagInventory, 0);
        		
        		ItemStack prevItemStack;
        		
        		do
        		{
        			prevItemStack = firstBagSlot.getStack();
        			
        			this.handleMouseClick(firstBagSlot, firstBagSlot.slotNumber, 0, 1);
        		}
        		while(firstBagSlot.getHasStack() && firstBagSlot.getStack() != prevItemStack && firstBagSlot.getStack().stackSize != prevItemStack.stackSize);
        		
                break;
        	case 2: //Switch page (Button S)
        		/*
        		EntityBag toSwitch = ((ContainerBag)this.inventorySlots).bagInventory.getEntity();
        		PacketDispatcher.sendPacketToServer(Packet250Helper.makeEntityPingPacket(toSwitch));*/
        		((ContainerBag)this.inventorySlots).bagInventory.switchPage();
        }
	}
}
