package taigore.inventorysaver.common.configuration;

import taigore.inventorysaver.main.InventorySaver;
import net.minecraft.block.Block;

public class BlockSetting<Type extends Block>
{
	private Setting<Integer> configID_;
	private RegistrationLogic<Type> logic_;
	private Type blockInstance_;
	
	BlockSetting(Setting<Integer> configID, RegistrationLogic<Type> logic)
	{
		configID_ = configID;
		logic_ = logic;
		blockInstance_ = null;
	}
	
	void register()
	{
		final Integer blockID = configID_.read();
		
		try
		{
			if(blockID > 0)
				blockInstance_ = logic_.register(blockID);
			
			String logMessage;
			
			if(blockInstance_ != null)
				logMessage = String.format("Registered block %s with ID &d",
										   blockInstance_.getLocalizedName(),
										   blockID);
			else
				logMessage = String.format("Skipped block %s (Config ID: %d)",
										   configID_.getName(),
										   blockID);
			
			InventorySaver.log.config(logMessage);
		}
		catch(Exception e)
		{
			String error = String.format("Unable to register block %s with ID %d:\n%s",
										 configID_.getName(),
										 blockID,
										 e.getMessage());
			
			InventorySaver.log.warning(error);
		}
	}
	
	public Type getBlock()
	{
		return blockInstance_;
	}
	
	public boolean available()
	{
		return blockInstance_ != null;
	}
	
	public void setComment(String comment)
	{
		configID_.setComment(comment);
	}
}
