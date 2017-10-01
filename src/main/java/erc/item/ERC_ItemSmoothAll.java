package erc.item;

import erc.tileEntity.TileEntityRailBase;
import erc.tileEntity.Wrap_TileEntityRail;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ERC_ItemSmoothAll extends Item {

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ) {
		
		if(world.isRemote == false)
		{
			TileEntity te = world.getTileEntity(x, y, z);
			if(te instanceof Wrap_TileEntityRail)
			{
				TileEntityRailBase rail = ((Wrap_TileEntityRail) te).getRail();
				if(rail == null)return true;
//				ERC_Logger.info("start");
				smoothrail(0, rail, (Wrap_TileEntityRail) te, world, 1);
				smoothrail(0, rail, (Wrap_TileEntityRail) te, world, -1);
			}
		}
			
		
		return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
	}

	private void smoothrail(int num, TileEntityRailBase root, Wrap_TileEntityRail rail, World world, int v)
	{
//		ERC_Logger.info("num"+num);
		if(num>=100)return;
		if(num<=-100)return;
		if(num != 0 && root == rail)return;
		if(rail == null)return;
		
		rail.Smoothing();
		rail.CalcRailLength();
		rail.syncData();
    	Wrap_TileEntityRail prev = rail.getPrevRailTileEntity();
    	if(prev!=null)
    	{
    		TileEntityRailBase r = prev.getRail();
    		r.SetNextRailVectors((TileEntityRailBase) rail.getRail());
    		r.CalcRailLength();
    		prev.syncData();
    	}
		smoothrail(num+v, root, (v==1?rail.getNextRailTileEntity():rail.getPrevRailTileEntity()), world, v);
	}
}
