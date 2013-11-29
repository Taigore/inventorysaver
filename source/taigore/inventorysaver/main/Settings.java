package taigore.inventorysaver.main;

import taigore.inventorysaver.block.BlockBag;
import taigore.inventorysaver.common.configuration.BlockSetting;
import taigore.inventorysaver.common.configuration.ForgeConfiguration;
import taigore.inventorysaver.common.configuration.ItemSetting;
import taigore.inventorysaver.common.configuration.RegistrationLogic;
import taigore.inventorysaver.common.configuration.Setting;
import taigore.inventorysaver.common.configuration.SettingLogic;
import taigore.inventorysaver.item.ItemDeathCompass;

public class Settings
{
	final public Setting<Boolean> protectLoot;
	final public Setting<Boolean> bagGravity;
	final public Setting<Boolean> ignoreLava;
	final public Setting<Integer> cleanupTime;
	
	final public BlockSetting<BlockBag> bag;
    final public ItemSetting<ItemDeathCompass> deathCompass;
	
	public Settings(ForgeConfiguration configuration)
	{
	    protectLoot = configuration.getSettingB("Protect loot", null, false, null);
	    cleanupTime = configuration.getSettingI("Cleanup time", null, -1, null);
	    
	    {
	    	SettingLogic<Boolean> gravityLogic = new SettingLogic<Boolean>()
	    	{
	    		@Override
	    		public Boolean transform(Boolean value)
	    		{
	    			return value && !protectLoot.read();
	    		}
	    	};
	    	
	    	bagGravity = configuration.getSettingB("Bag gravity", null, true, gravityLogic);
	    }
	    
	    {
	    	SettingLogic<Boolean> lavaProtectionLogic = new SettingLogic<Boolean>()
			{
	    		@Override
	    		public Boolean transform(Boolean value)
	    		{
	    			return value || protectLoot.read();
	    		}
			};
			
			ignoreLava = configuration.getSettingB("Ignore lava", null, false, lavaProtectionLogic);
	    }
	    
	    {
		    RegistrationLogic<BlockBag> logic = new RegistrationLogic<BlockBag>()
			{
		    	@Override
		    	public BlockBag register(Integer id)
		    	{
		    		return new BlockBag(id);
		    	}
			};
		    bag = configuration.getBlock("Bag", "Blocks and items", 4000, logic);
	    }
	    
	    {
	    	RegistrationLogic<ItemDeathCompass> logic = new RegistrationLogic<ItemDeathCompass>()
			{
	    		@Override
	    		public ItemDeathCompass register(Integer id)
	    		{
	    			return new ItemDeathCompass(id);
	    		}
			};
			deathCompass = configuration.getItem("Death compass", "Blocks and items", 30000, logic);
	    }
	    
	    protectLoot.setComment("Prevents the bag from falling, being destroyed by lava and being looted by other players");
	    
	    configuration.save();
	}
}
