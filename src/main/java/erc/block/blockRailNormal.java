package erc.block;

import erc.tileEntity.TileEntityRailBase;
import erc.tileEntity.TileEntityRailNormal;

public class blockRailNormal extends blockRailBase{

	@Override
	public TileEntityRailBase getTileEntityInstance() 
	{
		return new TileEntityRailNormal();
	}

}
