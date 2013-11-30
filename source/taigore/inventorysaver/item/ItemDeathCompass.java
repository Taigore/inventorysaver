package taigore.inventorysaver.item;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;
import taigore.inventorysaver.item.texture.TextureDeathCompass;
import taigore.inventorysaver.main.InventorySaver;
import taigore.inventorysaver.world.DeathPositions;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemDeathCompass extends Item
{
    public ItemDeathCompass(int itemID)
    {
        super(itemID);
        
        this.setMaxStackSize(64);
        this.setUnlocalizedName("invsaver.deathcompass");
        this.setTextureName(InventorySaver.resource("deathCompass"));
        this.setCreativeTab(CreativeTabs.tabTools);
        
        GameRegistry.registerItem(this, "ItemDeathCompass");
        LanguageRegistry.addName(this, "Death compass");
        
        this.addCraftingRecipes();
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.itemIcon = new TextureDeathCompass(InventorySaver.resource("textures/item/deathCompass.png"));
        
        ((TextureMap)par1IconRegister).setTextureEntry(this.getIconString(), (TextureAtlasSprite)this.itemIcon);
    }
    
    public void addCraftingRecipes()
    {
        InventorySaver.log.info("Registering death compass crafting recipes");
        
        ItemStack deathCompass = new ItemStack(this, 1);
        ItemStack compass = new ItemStack(Item.compass, 1);
        ItemStack rottenFlesh = new ItemStack(Item.rottenFlesh, 1);
        ItemStack ironIngot = new ItemStack(Item.ingotIron, 1);
        
        //Recipe: vanilla compass + rotten flesh = death compass
        GameRegistry.addRecipe(new ShapelessRecipes(deathCompass, Arrays.asList(compass, rottenFlesh)));
        
        //Recipe: compass crafting pattern with rotten flesh in the middle = death compass
        GameRegistry.addRecipe(new ShapedRecipes(3, 3, new ItemStack[]{null, ironIngot, null, ironIngot, rottenFlesh, ironIngot, null, ironIngot, null}, deathCompass));
    }
    
    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        /* Needs tooltip like other items
        if(par2CreativeTabs == CreativeTabs.tabAllSearch)
            par3List.add(new ItemStack(this, 1))
        else*/
            par3List.add(new ItemStack(this, 1));
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        if(par3EntityPlayer.isSneaking())
        {
            DeathPositions toUpdate = DeathPositions.getDeathPositions(par2World);
            toUpdate.popDeathPoint(par3EntityPlayer);
        }
        
        return par1ItemStack;
    }
}
