package taigore.inventorysaver.common;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import org.apache.commons.lang3.Validate;

import com.google.common.base.Strings;

import cpw.mods.fml.common.FMLLog;

public class ConfigManager
{
    public final Configuration configFile;
    public final Logger logger;
    
    private List<SettingRegistration> settings = new LinkedList();
    
    private int defaultBlockID = Block.blocksList.length - 1;
    private int defaultItemID = Item.itemsList.length - 256 - 1;
    
    public ConfigManager(Configuration configFile, Logger logChannel)
    {
        if(configFile != null)
            this.configFile = configFile;
        else
            throw new IllegalArgumentException("null configuration file");
        
        if(logChannel == null) logChannel = FMLLog.getLogger();
        
        this.logger = logChannel;
        
        this.configFile.load();
    }
    
    public void doRegistrations()
    {
        for(SettingRegistration toRegister : this.settings)
        	toRegister.register();
        
        this.settings.clear();
        
        this.configFile.save();
    }
    
    protected int getNextItemID() { return this.defaultItemID--; }
    protected int getNextBlockID() { return this.defaultBlockID--; }
    
    public class SettingRegistration<T>
    {
    	public final String category;
    	public final String optionName;
    	public String comment = null;
    	
    	protected final Object defaultValue;
    	protected Property value = null;
    	protected Property.Type type;
    	
    	public SettingRegistration(String settingName, int defaultValue) throws NullPointerException, IllegalArgumentException, ClassCastException
    		{ this(settingName, (String)null, defaultValue); }
    	public SettingRegistration(String settingName, double defaultValue) throws NullPointerException, IllegalArgumentException, ClassCastException
    		{ this(settingName, (String)null, defaultValue); }
    	public SettingRegistration(String settingName, boolean defaultValue) throws NullPointerException, IllegalArgumentException, ClassCastException
    		{ this(settingName, (String)null, defaultValue); }
    	public SettingRegistration(String settingName, String defaultValue) throws NullPointerException, IllegalArgumentException, ClassCastException
    		{ this(settingName, (String)null, defaultValue); }
    	
    	public SettingRegistration(String settingName, String categoryName, int defaultValue) throws NullPointerException, IllegalArgumentException, ClassCastException
    		{ this(settingName, categoryName, (T)new Integer(defaultValue)); this.type = Property.Type.INTEGER; }
    	public SettingRegistration(String settingName, String categoryName, double defaultValue) throws NullPointerException, IllegalArgumentException, ClassCastException
			{ this(settingName, categoryName, (T)new Double(defaultValue)); this.type = Property.Type.DOUBLE; }
    	public SettingRegistration(String settingName, String categoryName, boolean defaultValue) throws NullPointerException, IllegalArgumentException, ClassCastException
			{ this(settingName, categoryName, (T)new Boolean(defaultValue)); this.type = Property.Type.BOOLEAN; }
    	public SettingRegistration(String settingName, String categoryName, String defaultValue) throws NullPointerException, IllegalArgumentException, ClassCastException
			{ this(settingName, categoryName, (T)defaultValue); this.type = Property.Type.STRING; }
    		
    	private SettingRegistration(String settingName, String categoryName, T defaultValue) throws NullPointerException, IllegalArgumentException
    	{
    		Validate.notBlank(settingName, "Invalid setting name");
    		
    		if(Strings.isNullOrEmpty(categoryName))
    			categoryName = "general";
    		
    		this.category = categoryName.toLowerCase();
    		this.optionName = settingName;
    		this.defaultValue = defaultValue == null ? "" : defaultValue;
    		
    		ConfigManager.this.settings.add(this);
    	}
    	
    	public void register()
    	{
    		switch(this.type)
			{
			case BOOLEAN:
				this.value = ConfigManager.this.configFile.get(this.category, this.optionName, (Boolean)this.defaultValue, this.comment);
				break;
				
			case INTEGER:
				this.value = ConfigManager.this.configFile.get(this.category, this.optionName, (Integer)this.defaultValue, this.comment);
				break;
				
			case DOUBLE:
				this.value = ConfigManager.this.configFile.get(this.category, this.optionName, (Double)this.defaultValue, this.comment);
				break;
				
			case STRING:
				this.value = ConfigManager.this.configFile.get(this.category, this.optionName, (String)this.defaultValue, this.comment);
				break;
			}
    	}
    	
    	public T getValue()
    	{
    		if(this.value != null)
    		{
				switch(this.type)
				{
				case BOOLEAN:
					return (T)new Boolean(this.value.getBoolean((Boolean)this.defaultValue));
					
				case INTEGER:
					return (T)new Integer(this.value.getInt());
					
				case DOUBLE:
					return (T)new Double(this.value.getDouble((Double)this.defaultValue));
					
				case STRING:
					return (T)this.value.getString();
				}
    		}
			
			return null;
    	}
    	
    	public Property getProperty() { return this.value; }
    }
    
    public abstract class ItemRegistration<T extends Item> extends SettingRegistration<Integer>
    {
        private T registered = null;
        
        public ItemRegistration(String optionName) { super(optionName, Configuration.CATEGORY_ITEM, ConfigManager.this.getNextItemID()); }
        public ItemRegistration(String optionName, String category) { super(optionName, Strings.isNullOrEmpty(category) ? Configuration.CATEGORY_ITEM : category, ConfigManager.this.getNextItemID()); }
        
        public T getItem() { return this.registered; }
        public boolean isRegistered() { return this.registered != null; }
        
        @Override
        public void register()
        {
        	try
        	{
	        	this.value = ConfigManager.this.configFile.getItem(this.optionName, this.category, (Integer)this.defaultValue, this.comment);
	        	int itemID = this.value.getInt();
	        	
	        	if(itemID > 0)
	        		this.registered = this.instantiateItem(itemID);
	        	
	        	if(this.isRegistered()) ConfigManager.this.logger.info(String.format("Added item %s with ID %d", this.optionName, itemID));
	        	else ConfigManager.this.logger.info(String.format("Skipped item with option name %s. ID: %d", this.optionName, itemID));
        	}
        	catch(RuntimeException e)
        	{
        		this.registered = null;
        		ConfigManager.this.logger.severe(String.format("Unable to register item %s", this.optionName));
        		e.printStackTrace();
        	}
        }
        @Override
        public Integer getValue() { return this.value != null ? this.value.getInt() : -1; }
        
        public abstract T instantiateItem(int itemID);
    }
    
    public abstract class BlockRegistration<T extends Block> extends SettingRegistration<Integer>
    {
        private T registered = null;
        
        public BlockRegistration(String optionName) { super(optionName, Configuration.CATEGORY_BLOCK, ConfigManager.this.getNextBlockID()); }
        public BlockRegistration(String optionName, String category) { super(optionName, Strings.isNullOrEmpty(category) ? Configuration.CATEGORY_BLOCK : category, ConfigManager.this.getNextBlockID()); }
        
        public T getBlock() { return this.registered; }
        public boolean isRegistered() { return this.registered != null; }
        
        public void register()
        {
        	try
        	{
	        	this.value = ConfigManager.this.configFile.getBlock(this.optionName, this.category, (Integer)this.defaultValue, this.comment);
	        	int blockID = this.value.getInt();
	        	
	        	if(blockID > 0)
	        		this.registered = this.instantiateBlock(blockID);
	        	
	        	if(this.isRegistered()) ConfigManager.this.logger.info(String.format("Added block %s with ID %d", this.optionName, blockID));
	        	else ConfigManager.this.logger.info(String.format("Skipped block with option name %s. ID: %d", this.optionName, blockID));
        	}
        	catch(RuntimeException e)
        	{
        		this.registered = null;
        		ConfigManager.this.logger.severe(String.format("Unable to register block %s", this.optionName));
        		e.printStackTrace();
        	}
        }
        @Override
        public Integer getValue() { return this.value != null ? this.value.getInt() : -1; }
        
        public abstract T instantiateBlock(int blockID);
    }
}
