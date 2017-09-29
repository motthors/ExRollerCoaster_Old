package erc.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erc.entity.ERC_EntityCoaster;
import erc.manager.ERC_ModelLoadManager;
import erc.tileEntity.Wrap_TileEntityRail;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public abstract class Wrap_ItemCoaster extends Item{

	protected int modelCount = 0;
	protected int CoasterType = 0;
	protected IIcon itemIcons[];
	
	public int getModelCount(){return modelCount;}
	public int getCoasterType(){return CoasterType;}
	
	public abstract ERC_EntityCoaster getItemInstance(World world, Wrap_TileEntityRail tile, double x, double y, double z);
	
    @SideOnly(Side.CLIENT)
    public void ScrollMouseHweel(int dhweel)
    {
//    	ERC_Logger.info("wrap_itemcoaster : dhweel:"+dhweel);
    	modelCount += dhweel>0?1:-1;
    	if(modelCount >= ERC_ModelLoadManager.getModelPackNum(CoasterType)) modelCount=0;
    	else if(modelCount < 0)modelCount = ERC_ModelLoadManager.getModelPackNum(CoasterType)-1;
//    	ERC_Logger.info("modelcount:"+modelCount);
    }
    
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister p_94581_1_)
    {
		String[] names = ERC_ModelLoadManager.getCoasterIconStrings(CoasterType);
		itemIcons = new IIcon[names.length];
    	for(int i=0;i<names.length;++i)
    	{
    		this.itemIcons[i] = p_94581_1_.registerIcon(names[i]);
    	}
    	itemIcon = itemIcons[0];
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int p_77617_1_) 
	{
		return itemIcons[modelCount];
	}
		
}
