package taigore.inventorysaver.common.configuration;

import net.minecraft.item.Item;

public class ItemSetting
{
	private Setting<Integer> configID_;
	private RegistrationLogic<? extends Item> logic_;
	private Item itemInstance_;
	
	ItemSetting(Setting<Integer> configID, RegistrationLogic<? extends Item> logic)
	{
		configID_ = configID;
		logic_ = logic;
		itemInstance_ = null;
	}
	
	void register()
	{
		final Integer itemID = configID_.read();
		
		if(itemID > 0)
			itemInstance_ = logic_.register(itemID);
	}
	
	public Item getItem()
	{
		return itemInstance_;
	}
	
	public boolean available()
	{
		return itemInstance_ != null;
	}
}
