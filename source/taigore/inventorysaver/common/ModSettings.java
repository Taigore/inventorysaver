package taigore.inventorysaver.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import net.minecraftforge.common.Property.Type;
import cpw.mods.fml.common.FMLLog;

public class ModSettings
{
    //STATIC FIELDS
    static private ModSettings instance;
    
    //STATIC METHODS
    static public ModSettings instance() { return instance; }
    
    //FIELDS
    private Configuration configFile;
    
    private DebugLevel debugLevel;
    
    private Map<String, String> aliases = new HashMap();
    private Map<String, ConfigCategory> categoryMap = new HashMap();
    private Map<String, Property> settingsMap = new HashMap();
    
    public ModSettings(Configuration configFile) { this(configFile, DebugLevel.NONE); }
    public ModSettings(Configuration configFile, DebugLevel level)
    {
        instance = this;
        this.configFile = configFile;
        this.debugLevel = level != null ? level : DebugLevel.NONE;
        this.configFile.load();
        
        Set<String> categoryNames = this.configFile.getCategoryNames();
        
        for(String catName : categoryNames)
        {
            this.categoryMap.put(catName, this.configFile.getCategory(catName));
            
            for(Map.Entry<String, Property> entry : this.configFile.getCategory(catName).entrySet())
            {
                this.settingsMap.put(String.format("%s:%s", catName, entry.getKey()), entry.getValue());
            }
        }
    }
    
    public void save() { this.configFile.save(); }
    
    //METHODS
    public void alias(String categoryAndKey, String alias)
    {
        if(categoryAndKey != null && alias != null
        && !categoryAndKey.isEmpty() && !alias.isEmpty()
        && categoryAndKey.contains(":") && !alias.contains(":"))
        {
            aliases.put(alias, categoryAndKey);
        }
    }
    
    protected String readAlias(String alias)
    {
        if(alias != null && !alias.contains(":"))
            alias = this.aliases.get(alias);
        
        return alias;
    }
    
    public void set(String key, Object value) throws ClassCastException
    {
        key = this.readAlias(key);
        Property prop = this.settingsMap.get(key);
        
        if(prop != null && value != null)
        {
            try
            {
                switch(prop.getType())
                {
                    case BOOLEAN:
                        if(prop.isList())
                        {
                            Boolean[] values = (Boolean[])value;
                            String[] strValues = new String[values.length];
                            
                            for(int i = 0; i < values.length; ++i)
                                strValues[i] = values[i].toString();
                            
                            prop.set(strValues);
                        }
                        else
                            prop.set((Boolean) value);
                        break;
                        
                    case INTEGER:
                        if(prop.isList())
                        {
                            Integer[] values = (Integer[])value;
                            String[] strValues = new String[values.length];
                            
                            for(int i = 0; i < values.length; ++i)
                                strValues[i] = values[i].toString();
                            
                            prop.set(strValues);
                        }
                        else
                            prop.set((Integer) value);
                        break;
                        
                    case DOUBLE:
                        if(prop.isList())
                        {
                            Double[] values = (Double[])value;
                            String[] strValues = new String[values.length];
                            
                            for(int i = 0; i < values.length; ++i)
                                strValues[i] = values[i].toString();
                            
                            prop.set(strValues);
                        }
                        else
                            prop.set((Double) value);
                        break;
                        
                    case STRING:
                        if(prop.isList())
                        {
                            String[] values = (String[])value;
                            
                            prop.set(values);
                        }
                        else
                            prop.set((String) value);
                        break;
                }
            }
            catch(ClassCastException e)
            {
                ClassCastException newE = (ClassCastException)new ClassCastException("invalid setting assignment").initCause(e);
                throw newE;
            }
        }
    }
    
    //Getting methods. Type is there to select the return type
    public Boolean get(String key, Boolean type) throws ClassCastException
    {
        Property prop = this.settingsMap.get(this.readAlias(key));
        
        if(prop != null)
        {
            if(prop.getType() == Type.BOOLEAN && prop.isList() == false)
                return prop.getBoolean(false);
            else
                throw new ClassCastException(String.format("setting is %s%s, not boolean", prop.getType().toString(), prop.isList() ? " List" : ""));
        }
        else
            return null;
    }
    public Integer get(String key, Integer type) throws ClassCastException
    {
        Property prop = this.settingsMap.get(this.readAlias(key));
        
        if(prop != null)
        {
            if(prop.getType() == Type.INTEGER && prop.isList() == false)
                return prop.getInt(0);
            else
                throw new ClassCastException(String.format("setting is %s%s, not integer", prop.getType().toString(), prop.isList() ? " List" : ""));
        }
        else
            return null;
    }
    public Double get(String key, Double type) throws ClassCastException
    {
        Property prop = this.settingsMap.get(this.readAlias(key));
        
        if(prop != null)
        {
            if(prop.getType() == Type.DOUBLE && prop.isList() == false)
                return prop.getDouble(0.0d);
            else
                throw new ClassCastException(String.format("setting is %s%s, not double", prop.getType().toString(), prop.isList() ? " List" : ""));
        }
        else
            return null;
    }
    public String get(String key, String type) throws ClassCastException
    {
        Property prop = this.settingsMap.get(this.readAlias(key));
        
        if(prop != null)
        {
            if(prop.getType() == Type.STRING && prop.isList() == false)
                return prop.getString();
            else
                throw new ClassCastException(String.format("setting is %s%s, not string", prop.getType().toString(), prop.isList() ? " List" : ""));
        }
        else
            return null;
    }
    public Boolean[] get(String key, Boolean[] type) throws ClassCastException
    {
        Property prop = this.settingsMap.get(this.readAlias(key));
        
        if(prop != null)
        {
            if(prop.getType() == Type.BOOLEAN && prop.isList() == true)
            {
                boolean[] valList = prop.getBooleanList();
                Boolean[] obValList = new Boolean[valList.length];
                
                for(int i = 0; i < valList.length; ++i)
                    obValList[i] = valList[i];
                
                return obValList;
            }
            else
                throw new ClassCastException(String.format("setting is %s%s, not boolean list", prop.getType().toString(), prop.isList() ? " List" : ""));
        }
        else
            return null;
    }
    public Integer[] get(String key, Integer[] type) throws ClassCastException
    {
        Property prop = this.settingsMap.get(this.readAlias(key));
        
        if(prop != null)
        {
            if(prop.getType() == Type.INTEGER && prop.isList() == true)
            {
                int[] valList = prop.getIntList();
                Integer[] obValList = new Integer[valList.length];
                
                for(int i = 0; i < valList.length; ++i)
                    obValList[i] = valList[i];
                
                return obValList;
            }
            else
                throw new ClassCastException(String.format("setting is %s%s, not int list", prop.getType().toString(), prop.isList() ? " List" : ""));
        }
        else
            return null;
    }
    public Double[] get(String key, Double[] type) throws ClassCastException
    {
        Property prop = this.settingsMap.get(this.readAlias(key));
        
        if(prop != null)
        {
            if(prop.getType() == Type.DOUBLE && prop.isList() == true)
            {
                double[] valList = prop.getDoubleList();
                Double[] obValList = new Double[valList.length];
                
                for(int i = 0; i < valList.length; ++i)
                    obValList[i] = valList[i];
                
                return obValList;
            }
            else
                throw new ClassCastException(String.format("setting is %s%s, not boolean", prop.getType().toString(), prop.isList() ? " List" : ""));
        }
        else
            return null;
    }
    public String[] get(String key, String[] type) throws ClassCastException
    {
        Property prop = this.settingsMap.get(this.readAlias(key));
        
        if(prop.getType() == Type.STRING && prop.isList() == true)
            return prop.getStringList();
        else
            throw new ClassCastException(String.format("setting is %s%s, not boolean", prop.getType().toString(), prop.isList() ? " List" : ""));
    }
    
    //INIT
    public void init(String key, String comment, boolean value)
    {
        if(!this.settingsMap.containsKey(key) && key != null && !key.isEmpty() && key.contains(":"))
        {
            String[] catAndKey = key.split(":");
            catAndKey[0] = catAndKey[0].toLowerCase();
            Property newProperty = this.configFile.get(catAndKey[0], catAndKey[1], value);
            
            if(comment != null)
                newProperty.comment = comment;
            
            this.categoryMap.put(catAndKey[0], this.configFile.getCategory(catAndKey[0]));
            this.settingsMap.put(key, newProperty);
        }
    }
    public void init(String key, String comment, int value)
    {
        if(!this.settingsMap.containsKey(key) && key != null && !key.isEmpty() && key.contains(":"))
        {
            String[] catAndKey = key.split(":");
            catAndKey[0] = catAndKey[0].toLowerCase();
            Property newProperty;
            
            if(catAndKey[0].toLowerCase().equals("item")) newProperty = this.configFile.getItem(catAndKey[1], value, comment);
            if(catAndKey[0].toLowerCase().equals("block")) newProperty = this.configFile.getBlock(catAndKey[1], value, comment);
            else
                newProperty = this.configFile.get(catAndKey[0], catAndKey[1], value);
            
            if(comment != null)
                newProperty.comment = comment;
            
            this.categoryMap.put(catAndKey[0], this.configFile.getCategory(catAndKey[0]));
            this.settingsMap.put(key, newProperty);
        }
    }
    public void init(String key, String comment, double value)
    {
        if(!this.settingsMap.containsKey(key) && key != null && !key.isEmpty() && key.contains(":"))
        {
            String[] catAndKey = key.split(":");
            catAndKey[0] = catAndKey[0].toLowerCase();
            Property newProperty = this.configFile.get(catAndKey[0], catAndKey[1], value);
            
            if(comment != null)
                newProperty.comment = comment;
            
            this.categoryMap.put(catAndKey[0], this.configFile.getCategory(catAndKey[0]));
            this.settingsMap.put(key, newProperty);
        }
    }
    public void init(String key, String comment, String value)
    {
        if(!this.settingsMap.containsKey(key) && key != null && !key.isEmpty() && key.contains(":"))
        {
            String[] catAndKey = key.split(":");
            catAndKey[0] = catAndKey[0].toLowerCase();
            Property newProperty = this.configFile.get(catAndKey[0], catAndKey[1], value);
            
            if(comment != null)
                newProperty.comment = comment;
            
            this.categoryMap.put(catAndKey[0], this.configFile.getCategory(catAndKey[0]));
            this.settingsMap.put(key, newProperty);
        }
    }
    public void init(String key, String comment, boolean[] value)
    {
        if(!this.settingsMap.containsKey(key) && key != null && !key.isEmpty() && key.contains(":"))
        {
            String[] catAndKey = key.split(":");
            catAndKey[0] = catAndKey[0].toLowerCase();
            Property newProperty = this.configFile.get(catAndKey[0], catAndKey[1], value);
            
            if(comment != null)
                newProperty.comment = comment;
            
            this.categoryMap.put(catAndKey[0], this.configFile.getCategory(catAndKey[0]));
            this.settingsMap.put(key, newProperty);
        }
    }
    public void init(String key, String comment, int[] value)
    {
        if(!this.settingsMap.containsKey(key) && key != null && !key.isEmpty() && key.contains(":"))
        {
            String[] catAndKey = key.split(":");
            catAndKey[0] = catAndKey[0].toLowerCase();
            Property newProperty = this.configFile.get(catAndKey[0], catAndKey[1], value);
            
            if(comment != null)
                newProperty.comment = comment;
            
            this.categoryMap.put(catAndKey[0], this.configFile.getCategory(catAndKey[0]));
            this.settingsMap.put(key, newProperty);
        }
    }
    public void init(String key, String comment, double[] value)
    {
        if(!this.settingsMap.containsKey(key) && key != null && !key.isEmpty() && key.contains(":"))
        {
            String[] catAndKey = key.split(":");
            catAndKey[0] = catAndKey[0].toLowerCase();
            Property newProperty = this.configFile.get(catAndKey[0], catAndKey[1], value);
            
            if(comment != null)
                newProperty.comment = comment;
            
            this.categoryMap.put(catAndKey[0], this.configFile.getCategory(catAndKey[0]));
            this.settingsMap.put(key, newProperty);
        }
    }
    public void init(String key, String comment, String[] value)
    {
        if(!this.settingsMap.containsKey(key) && key != null && !key.isEmpty() && key.contains(":"))
        {
            String[] catAndKey = key.split(":");
            catAndKey[0] = catAndKey[0].toLowerCase();
            Property newProperty = this.configFile.get(catAndKey[0], catAndKey[1], value);
            
            if(comment != null)
                newProperty.comment = comment;
            
            this.categoryMap.put(catAndKey[0], this.configFile.getCategory(catAndKey[0]));
            this.settingsMap.put(key, newProperty);
        }
    }
    
    //Debug
    public void debugMessage(DebugLevel level, String message) { if(level.ordinal() <= this.debugLevel.ordinal()) FMLLog.info(message); }
    
    /////////////
    // SUBTYPES
    /////////////
    public enum DebugLevel
    {
        NONE,
        MOD,
        MODSETTINGS;
    }
}
