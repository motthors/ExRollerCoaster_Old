package erc.item;

import erc.entity.ERC_EntityCoaster;
import erc.entity.ERC_EntityCoasterConnector;
import erc.tileEntity.Wrap_TileEntityRail;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ERC_ItemCoasterConnector extends ERC_ItemCoaster{

	public ERC_ItemCoasterConnector()
	{
		super();
		CoasterType = 1;
	}
	
	public ERC_EntityCoaster getItemInstance(World world, Wrap_TileEntityRail tile, double x, double y, double z)
	{
		return new ERC_EntityCoasterConnector(world, tile.getRail(), x, y, z);
	}

	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_)
    {
		return false; // 連結用コースターは直接設置できない
    }
}
