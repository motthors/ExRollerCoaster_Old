package erc.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erc.block.blockRailBase;
import erc.gui.GUIRail;
import erc.manager.ERC_ModelLoadManager;
import erc.message.ERC_MessageRailGUICtS;
import erc.message.ERC_PacketHandler;
import erc.tileEntity.Wrap_TileEntityRail;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ERC_ItemSwitchingRailModel extends Item{

	private int modelCount = 0;
	protected IIcon itemIcons[];
	
	public ERC_ItemSwitchingRailModel(){}
	
    public int getModelCount(){return modelCount;}
    
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_)
    {
    	if (world.isRemote)
    	{
    		// 右クリックしたブロックがレールブロックならOK
	    	if (!blockRailBase.isBlockRail(world.getBlock(x, y, z))) return false;
	    	
	    	// Wrap_TileEntityRailがあればOK(たぶんある)
	    	Wrap_TileEntityRail tile = (Wrap_TileEntityRail) world.getTileEntity(x, y, z);
	    	
	    	// モデル描画クラス入れ替え
	    	tile.changeRailModelRenderer(modelCount);
	    	
	    	ERC_MessageRailGUICtS packet = new ERC_MessageRailGUICtS(x, y, z, GUIRail.editFlag.RailModelIndex.ordinal(), modelCount);
	    	ERC_PacketHandler.INSTANCE.sendToServer(packet);
    	}
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public void ScrollMouseHweel(int dhweel)
    {
//    	ERC_Logger.info("wrap_itemcoaster : dhweel:"+dhweel);
    	modelCount += dhweel>0?1:-1;
    	if(modelCount >= ERC_ModelLoadManager.getRailPackNum()+1) modelCount=0;
    	else if(modelCount < 0)modelCount = ERC_ModelLoadManager.getRailPackNum();
//    	ERC_Logger.info("modelcount:"+modelCount);
    }
    
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister p_94581_1_)
    {
		String[] names = ERC_ModelLoadManager.getRailIconStrings();
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
