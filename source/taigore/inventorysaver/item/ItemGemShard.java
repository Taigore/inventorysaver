package taigore.inventorysaver.item;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.Icon;
import taigore.inventorysaver.client.renderer.texture.TextureShardCompass;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemGemShard extends Item
{
    private Icon[] diamondShards;
    private Icon[] emeraldShards;
    private Icon emeraldCompass;
    private Icon diamondCompass;
    
    private final int shardsFromGem = 4;
    private final int maxSplits = (int)Math.ceil(Math.log(shardsFromGem) / Math.log(2));
    
    public ItemGemShard(int itemID)
    {
        super(itemID);
        
        this.setMaxStackSize(64);
        this.setHasSubtypes(true);
        
        this.addCraftingRecipes();
    }
    
    public boolean isCompass(int damageValue) { return (damageValue & 8) != 0; }
    public boolean isCompass(ItemStack toCheck) { return toCheck != null ? this.isCompass(this.getItemDamageFromStack(toCheck)) : false; }
    
    public boolean isEmerald(int damageValue) { return (damageValue & 4) != 0; }
    public boolean isEmerald(ItemStack toCheck) { return toCheck != null ? this.isEmerald(this.getItemDamageFromStack(toCheck)) : false; }
    
    public int getSplitsLeft(int damageValue) { return damageValue & 3; }
    public int getSplitsLeft(ItemStack toCheck) { return toCheck != null ? this.getSplitsLeft(this.getItemDamageFromStack(toCheck)) : 0; }
    
    public ItemStack makeStack(boolean isCompass, boolean isEmerald, int splitsLeft, int size)
    {
        if(size < 0) size = 1;
        else if(size > this.getItemStackLimit()) size = this.getItemStackLimit();
        
        if(splitsLeft < 0 || isCompass)
            splitsLeft = 0;
        else if(splitsLeft >= this.maxSplits)
            splitsLeft = this.maxSplits - 1;
        
        int damageValue = 0;
        damageValue |= isCompass ? 8 : 0;
        damageValue |= isEmerald ? 4 : 0;
        damageValue |= splitsLeft;
        
        return new ItemStack(this, size, damageValue);
    }
    public ItemStack makeStack(boolean isCompass, boolean isEmerald, int splitsLeft) { return this.makeStack(isCompass, isEmerald, splitsLeft, 1); }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.diamondShards = new Icon[this.maxSplits];
        this.emeraldShards = new Icon[this.maxSplits];
        
        for(int i = 0; i < this.maxSplits; ++i)
        {
            this.diamondShards[i] = par1IconRegister.registerIcon(String.format("Taigore_InventorySaver:dshard%d", i + 1));
            this.emeraldShards[i] = par1IconRegister.registerIcon(String.format("Taigore_InventorySaver:eshard%d", i + 1));
        }
        
        this.diamondCompass = new TextureShardCompass("Taigore_InventorySaver:diamondCompass", false);
        this.emeraldCompass = new TextureShardCompass("Taigore_InventorySaver:emeraldCompass", true);
        
        Minecraft.getMinecraft().renderEngine.textureMapItems.setTextureEntry(this.diamondCompass.getIconName(), (TextureStitched) this.diamondCompass);
        Minecraft.getMinecraft().renderEngine.textureMapItems.setTextureEntry(this.emeraldCompass.getIconName(), (TextureStitched) this.emeraldCompass);
    }
    
    @Override
    public ItemStack getContainerItemStack(ItemStack itemStack)
    {
        int damageValue = this.getItemDamageFromStack(itemStack);
        
        if(this.isCompass(damageValue))
            return new ItemStack(Item.compass, 1);
        else
        {
            ItemStack returnValue = new ItemStack(Item.shears, 1);
            returnValue.setItemDamage(Integer.MAX_VALUE);
            
            return returnValue;
        }
    }
    
    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        int damage = this.getItemDamageFromStack(par1ItemStack);
        
        String firstPart = this.isCompass(damage) ? "compass" : "shard";
        String secondPart = this.isEmerald(damage) ? "emerald" : "diamond";
        
        return String.format("%s.%s", firstPart, secondPart);
    }
    
    @Override
    public Icon getIconFromDamage(int damage)
    {
        if(this.isCompass(damage))
        {
            if(this.isEmerald(damage))
                return this.emeraldCompass;
            else
                return this.diamondCompass;
        }
        else
        {
            int splitsLeft = this.getSplitsLeft(damage);
            
            if(this.isEmerald(damage))
                return this.emeraldShards[splitsLeft];
            else
                return this.diamondShards[splitsLeft];
        }
    }
    
    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        if(par2CreativeTabs == CreativeTabs.tabTools || par2CreativeTabs == CreativeTabs.tabAllSearch)
        {
            //Compasses
            par3List.add(this.makeStack(true, true, 0)); //Emerald
            par3List.add(this.makeStack(true, false, 0)); //Diamond
        }
        if(par2CreativeTabs == CreativeTabs.tabMisc || par2CreativeTabs == CreativeTabs.tabAllSearch)
        {
            //Shards
            for(int i = 0; i < this.maxSplits; ++i)
            {
                par3List.add(this.makeStack(false, true, i)); //Emerald
                par3List.add(this.makeStack(false, false, i)); //Diamond
            }
        }
    }
    
    public void addCraftingRecipes()
    {
        //Shards: Diamond & emerald
        for(int i = 0; i < this.maxSplits; ++i)
        {
            ItemStack shardDiamond = this.makeStack(false, false, i, 2);
            ItemStack shardEmerald = this.makeStack(false, true, i, 2);
            
            ItemStack sourceDiamond = i == (this.maxSplits - 1) ? new ItemStack(Item.diamond, 1) : this.makeStack(false, false, i + 1);
            ItemStack sourceEmerald = i == (this.maxSplits - 1) ? new ItemStack(Item.emerald, 1) : this.makeStack(false, true, i + 1);
            
            CraftingManager.getInstance().getRecipeList().add(new ShapelessRecipes(shardDiamond, Arrays.asList(sourceDiamond)));
            CraftingManager.getInstance().getRecipeList().add(new ShapelessRecipes(shardEmerald, Arrays.asList(sourceEmerald)));
        }
        
        //Compass: diamond & emerald
        ItemStack compass = new ItemStack(Item.compass, 1);
        
        ItemStack emeraldCompass = this.makeStack(true, true, 0);
        ItemStack diamondCompass = this.makeStack(true, false, 0);
        
        ItemStack emeraldShard = this.makeStack(false, true, 0);
        ItemStack diamondShard = this.makeStack(false, false, 0);
        
        //Shard + Compass = right compass
        CraftingManager.getInstance().getRecipeList().add(new ShapelessRecipes(emeraldCompass, Arrays.asList(compass, emeraldShard)));
        CraftingManager.getInstance().getRecipeList().add(new ShapelessRecipes(diamondCompass, Arrays.asList(compass, diamondShard)));
        
        //Emerald/Diamond compass = shard, compass as leftover
        CraftingManager.getInstance().getRecipeList().add(new ShapelessRecipes(emeraldShard, Arrays.asList(emeraldCompass)));
        CraftingManager.getInstance().getRecipeList().add(new ShapelessRecipes(diamondShard, Arrays.asList(diamondCompass)));
    }
    
    @Override
    public CreativeTabs[] getCreativeTabs() { return new CreativeTabs[]{CreativeTabs.tabAllSearch, CreativeTabs.tabMisc, CreativeTabs.tabTools}; }
    
    @Override
    public boolean hasContainerItem() { return true; }
}
