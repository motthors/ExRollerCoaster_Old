package erc.block;

import erc.tileEntity.TileEntityRailBase;
import erc.tileEntity.TileEntityRailDetector;
import net.minecraft.world.IBlockAccess;

public class blockRailDetector extends blockRailBase{

	@Override
	public TileEntityRailBase getTileEntityInstance() 
	{
		return new TileEntityRailDetector();
	}
	
	public boolean canProvidePower()
	{
		return true;
	}
	

    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int p_149709_5_)
    {
    	TileEntityRailDetector rail = (TileEntityRailDetector)world.getTileEntity(x, y, z);
        return rail.getFlag() ? 15 : 0;
    }

    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int p_149748_5_)
    {
//        return (p_149748_1_.getBlockMetadata(p_149748_2_, p_149748_3_, p_149748_4_) & 8) == 0 ? 0 : (p_149748_5_ == 1 ? 15 : 0);
    	TileEntityRailDetector rail = (TileEntityRailDetector)world.getTileEntity(x, y, z);
        return rail.getFlag() ? 15 : 0;
    }
	
}
