package taigore.inventorysaver.common.configuration;

import taigore.inventorysaver.main.InventorySaver;
import net.minecraft.item.Item;

public class ItemSetting<Type extends Item>
{
	private Setting<Integer> configID_;
	private RegistrationLogic<Type> logic_;
	private Type itemInstance_;
	
	ItemSetting(Setting<Integer> configID, RegistrationLogic<Type> logic)
	{
		configID_ = configID;
		logic_ = logic;
		itemInstance_ = null;
	}
	
	void register()
	{
		final Integer itemID = configID_.read();
		
		try
		{
			if(itemID > 0)
				itemInstance_ = logic_.register(itemID);
			
			String logMessage;
			
			if(itemInstance_ != null)
				logMessage = String.format("Registered item %s with ID &d",
										   itemInstance_.getStatName(),
										   itemID);
			else
				logMessage = String.format("Skipped item %s (Config ID: %d)",
										   configID_.getName(),
										   itemID);
			
			InventorySaver.log.config(logMessage);
		}
		catch(Exception e)
		{
			String error = String.format("Unable to register item %s with ID %d:\n%s",
										 configID_.getName(),
										 itemID,
										 e.getMessage());
			
			InventorySaver.log.warning(error);
		}
	}
	
	public Type getItem()
	{
		return itemInstance_;
	}
	
	public boolean available()
	{
		return itemInstance_ != null;
	}
	
	public void setComment(String comment)
	{
		configID_.setComment(comment);
	}
}
