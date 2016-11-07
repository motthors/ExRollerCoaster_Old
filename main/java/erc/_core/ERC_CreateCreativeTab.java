package erc._core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ERC_CreateCreativeTab extends CreativeTabs{
	
	private Item IconItem;
	
	public ERC_CreateCreativeTab(String label, Item icon)
	{
		super(label);
		IconItem = icon;
	}
 
	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem()
	{
		return IconItem;
	}
 
	@Override
	@SideOnly(Side.CLIENT)
	public String getTranslatedTabLabel()
	{
		return "ExRollerCoaster";
	}
}
