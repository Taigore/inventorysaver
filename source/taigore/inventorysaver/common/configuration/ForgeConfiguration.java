package taigore.inventorysaver.common.configuration;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;

public class ForgeConfiguration
{
	private Configuration file_;
	private Set<WeakReference<ItemSetting>> itemsToRegister_ = new HashSet();
	private Set<WeakReference<BlockSetting>> blocksToRegister_ = new HashSet();
	
	public ForgeConfiguration(Configuration file) throws IllegalArgumentException
	{
		if(file != null)
			file_ = file;
		else
			throw new IllegalArgumentException("null configuration file provided");
	}
	
	public Setting<Boolean> getSetting(String name, String category, boolean defaultValue, SettingLogic<Boolean> logic)
	{
		if(category == null)
			category = Configuration.CATEGORY_GENERAL;
		
		Setting<Boolean> value = null;
		
		if(name != null)
			value = new Setting(logic, file_.get(category, name, defaultValue));
		
		return value;
	}
	public Setting<Integer> getSetting(String name, String category, int defaultValue, SettingLogic<Integer> logic)
	{
		if(category == null)
			category = Configuration.CATEGORY_GENERAL;
		
		Setting<Integer> value = null;
		
		if(name != null)
			value = new Setting(logic, file_.get(category, name, defaultValue));
		
		return value;
	}
	public Setting<Double> getSetting(String name, String category, double defaultValue, SettingLogic<Double> logic)
	{
		if(category == null)
			category = Configuration.CATEGORY_GENERAL;
		
		Setting<Double> value = null;
		
		if(name != null)
			value = new Setting(logic, file_.get(category, name, defaultValue));
		
		return value;
	}
	public Setting<String> getSetting(String name, String category, String defaultValue, SettingLogic<String> logic)
	{
		if(category == null)
			category = Configuration.CATEGORY_GENERAL;
		
		Setting<String> value = null;
		
		if(name != null)
			value = new Setting(logic, file_.get(category, name, defaultValue));
		
		return value;
	}
	
	public ItemSetting getItem(String itemName, String category, int defaultID, RegistrationLogic<? extends Item> logic)
	{
		if(category == null)
			category = Configuration.CATEGORY_ITEM;
		
		Setting<Integer> idConfig = getSetting(category, itemName, defaultID, new SettingLogic<Integer>());
		ItemSetting value = null;
		
		Validate.notNull(itemsToRegister_, "cannot register item after registerAll has been called");
		
		if(idConfig != null && logic != null)
		{
			value = new ItemSetting(idConfig, logic);
			itemsToRegister_.add(new WeakReference(value));
		}
		
		return value;
	}
	
	public BlockSetting getBlock(String blockName, String category, int defaultID, RegistrationLogic<? extends Block> logic)
	{
		if(category == null)
			category = Configuration.CATEGORY_ITEM;
		
		Setting<Integer> idConfig = getSetting(category, blockName, defaultID, new SettingLogic<Integer>());
		BlockSetting value = null;
		
		Validate.notNull(blocksToRegister_, "cannot register block after registerAll has been called");
		
		if(idConfig != null && logic != null)
		{
			value = new BlockSetting(idConfig, logic);
			blocksToRegister_.add(new WeakReference(value));
		}
		
		return value;
	}
	
	public void registerAll()
	{
		for(WeakReference<ItemSetting> reference : itemsToRegister_)
		{
			ItemSetting setting = reference.get();
			
			if(setting != null)
				setting.register();
		}
		itemsToRegister_.clear();
		itemsToRegister_ = null;
		
		for(WeakReference<BlockSetting> reference : blocksToRegister_)
		{
			BlockSetting setting = reference.get();
			
			if(setting != null)
				setting.register();
		}
		blocksToRegister_.clear();
		blocksToRegister_ = null;
	}
}
