package taigore.inventorysaver.common.configuration;

import net.minecraftforge.common.Property;

public class Setting<Type>
{
	static private SettingLogic defaultLogic = new SettingLogic();
	
	private SettingLogic<Type> logic_;
	private Property property_;
	
	Setting(SettingLogic<Type> logic, Property property)
	{
		if(logic == null)
			logic_ = defaultLogic;
		else
			logic_ = logic;
		
		property_ = property;
	}
	
	String getName()
	{
		return property_.getName();
	}
	
	public Type read()
	{
		Type value = null;
		
		switch(property_.getType())
		{
		case BOOLEAN:
			value = (Type)new Boolean(property_.getBoolean(false));
			break;
			
		case INTEGER:
			value = (Type)new Integer(property_.getInt());
			break;
			
		case DOUBLE:
			value = (Type)new Double(property_.getDouble(0.0f));
			break;
			
		case STRING:
			value = (Type)property_.getString();
			break;
		}
		
		return logic_.transform(value);
	}
}
