package erc.block;

import erc.tileEntity.TileEntityRailBase;
import erc.tileEntity.TileEntityRailInvisible;

public class blockRailInvisible extends blockRailBase{

	@Override
	public TileEntityRailBase getTileEntityInstance() 
	{
		return new TileEntityRailInvisible();
	}

}
