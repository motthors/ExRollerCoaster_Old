package erc.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erc._core.ERC_CONST;
import erc.block.blockRailBase;
import erc.manager.ERC_CoasterAndRailManager;
import erc.message.ERC_MessageConnectRailCtS;
import erc.message.ERC_MessageItemWrenchSync;
import erc.message.ERC_PacketHandler;
import erc.tileEntity.Wrap_TileEntityRail;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ERC_ItemWrench extends Item {

	/**
	 * アイテム　レンチ
	 * 機能
	 * ・レール間の接続
	 * ・レールの角度など調節
	 * スニーク＋右クリックで機能切り替え
	 * 通常右クリックで機能発動
	 */
	
	int mode = 0;
	static final int modenum = 2;
	final String ModeStr[] = {	"Connection mode", 
								"Adjustment mode"};
	final String texStr[] = {	"wrench_c1", 
								"wrench_c2",
								"wrench_e1",
								"wrench_e2"};
	
	protected IIcon itemIcons[] = new IIcon[texStr.length];
	protected IIcon temIcon = itemIcons[0];
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
	{
		if(player.isSneaking())
		{
			player.motionY = 1;
			if(world.isRemote) 
			{	//client
				// モード変更はクラで
				mode = (++mode)%modenum;
				ERC_CoasterAndRailManager.ResetData(); // モードチェンジで記憶消去
				player.addChatComponentMessage(new ChatComponentText(ModeStr[mode]));
			}
			return itemstack;
		}
		return super.onItemRightClick(itemstack, world, player);
	}
	
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, Block block)
    {
		Block convblock = world.getBlock(x, y, z);
		if( (convblock != Blocks.air) && (convblock != Blocks.water) && (convblock != Blocks.flowing_water) )return false;
		
    	if (!world.setBlock(x, y, z, block, 0, 3))
    	{
    		return false;
    	}
//    	ERC_Logger.info("place block");
    	if (world.getBlock(x, y, z) == block)
    	{
    		block.onBlockPlacedBy(world, x, y, z, player, stack);
    		block.onPostBlockPlaced(world, x, y, z, 0);
    		world.markBlockForUpdate(x, y, z);
    	}
    	return true;
    }
	
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ)
	{
		if(player.isSneaking()) return false;
		
		// func_150051...関数への入力の型がBlockRailTestならtrue
    	if (blockRailBase.isBlockRail(world.getBlock(x, y, z)))
    	{
    		if(world.isRemote) // クライアント
    		{
    			switch(mode)
        		{
        		case 0 : 
        			//クライアントはレールが無ければ登録、あればサーバーに接続用パケット送信
        			if(!ERC_CoasterAndRailManager.isPlacedPrevRail())ERC_CoasterAndRailManager.SetPrevData(x, y, z);
        			else{
        				ERC_MessageConnectRailCtS packet 
        					= new ERC_MessageConnectRailCtS(
        							ERC_CoasterAndRailManager.prevX, ERC_CoasterAndRailManager.prevY, ERC_CoasterAndRailManager.prevZ, 
        							x, y, z
							);
        				ERC_PacketHandler.INSTANCE.sendToServer(packet);
//        				ERC_Logger.info("connection : "+"."+ERC_CoasterAndRailManager.prevX+"."+ERC_CoasterAndRailManager.prevY+"."+ERC_CoasterAndRailManager.prevZ
//    	        				+" -> "+x+"."+y+"."+z);
        				ERC_CoasterAndRailManager.ResetData();
        			}
        			break;
        		case 1 : 
        			Wrap_TileEntityRail tile =  (Wrap_TileEntityRail)world.getTileEntity(x, y, z);
        			ERC_CoasterAndRailManager.OpenRailGUI(tile.getRail());
//        			ERC_Logger.warn("save rail to manager : "+tile.getRail().getClass().getName());
        			break;
        		}
    			
			    ERC_PacketHandler.INSTANCE.sendToServer(new ERC_MessageItemWrenchSync(mode,x,y,z));
        		return false;
    		}
    		
    		return true;
    	}
        return false;
	}

	
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ)
	{
		if (blockRailBase.isBlockRail(world.getBlock(x, y, z)))
			return true;
		return false;
	}
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister p_94581_1_)
    {
    	for(int i=0;i<texStr.length;++i)
    	{
    		this.itemIcons[i] = p_94581_1_.registerIcon(ERC_CONST.DOMAIN+":"+texStr[i]);
    	}
    	temIcon = itemIcons[0];
    }

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int p_77617_1_) 
	{
		return ERCwrench_getIcon();
	}

	@Override
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) 
	{
		return ERCwrench_getIcon();
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) 
	{
		return ERCwrench_getIcon();
	}
    
    private IIcon ERCwrench_getIcon()
    {
    	int iconid=0;
    	switch(mode)
    	{
    	case 0:/*connect*/ 	iconid = ERC_CoasterAndRailManager.isPlacedRail() ? 1 : 0; break;
    	case 1:/*adjust*/	iconid = ERC_CoasterAndRailManager.isPlacedRail() ? 3 : 2; break;
    	}
    	
		return this.itemIcons[iconid];
    }
}
