package taigore.inventorysaver.common.configuration;

import net.minecraft.block.Block;

public class BlockSetting
{
	private Setting<Integer> configID_;
	private RegistrationLogic<? extends Block> logic_;
	private Block blockInstance_;
	
	BlockSetting(Setting<Integer> configID, RegistrationLogic<? extends Block> logic)
	{
		configID_ = configID;
		logic_ = logic;
		blockInstance_ = null;
	}
	
	void register()
	{
		final Integer itemID = configID_.read();
		
		if(itemID > 0)
			blockInstance_ = logic_.register(itemID);
	}
	
	public Block getBlock()
	{
		return blockInstance_;
	}
	
	public boolean available()
	{
		return blockInstance_ != null;
	}
}
