package erc.item;

import erc.block.blockRailBase;
import erc.entity.ERC_EntityCoaster;
import erc.manager.ERC_CoasterAndRailManager;
import erc.manager.ERC_ModelLoadManager;
import erc.message.ERC_MessageSpawnRequestWithCoasterOpCtS;
import erc.message.ERC_PacketHandler;
import erc.tileEntity.Wrap_TileEntityRail;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ERC_ItemCoaster extends Wrap_ItemCoaster
{
    public ERC_ItemCoaster()
    {
    	CoasterType = 0;
    }

    public ERC_EntityCoaster getItemInstance(World world, Wrap_TileEntityRail tile, double x, double y, double z)
    {
    	return new ERC_EntityCoaster(world, tile.getRail(),x, y, z);
    }
    
    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_)
    {
    	if (!blockRailBase.isBlockRail(world.getBlock(x, y, z)))
    	{
            return false;
        }
    	
    	if (world.isRemote)
    	{
    		setCoaster(x, y, z, -1);
    	}

        --itemStack.stackSize;
        return true;
    }
    
    public void setCoaster(int x, int y, int z, int parentID)
    {
    	ERC_CoasterAndRailManager.SetCoasterPos(x, y, z);
		ERC_CoasterAndRailManager.saveModelID = modelCount;
		ERC_MessageSpawnRequestWithCoasterOpCtS packet = new ERC_MessageSpawnRequestWithCoasterOpCtS(this, modelCount, ERC_ModelLoadManager.getModelOP(modelCount, CoasterType),x,y,z,parentID);
	    ERC_PacketHandler.INSTANCE.sendToServer(packet);
    }
}