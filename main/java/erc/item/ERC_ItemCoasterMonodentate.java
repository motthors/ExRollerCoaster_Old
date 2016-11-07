package erc.item;

import erc.entity.ERC_EntityCoaster;
import erc.entity.ERC_EntityCoasterMonodentate;
import erc.tileEntity.Wrap_TileEntityRail;
import net.minecraft.world.World;

public class ERC_ItemCoasterMonodentate extends ERC_ItemCoaster{

	public ERC_ItemCoasterMonodentate()
	{
		super();
		CoasterType = 2;
	}

	@Override
	public ERC_EntityCoaster getItemInstance(World world, Wrap_TileEntityRail tile, double x, double y, double z)
    {
    	return new ERC_EntityCoasterMonodentate(world, tile.getRail(),x, y, z);
    }

}
