package erc.block;

import erc.message.ERC_MessageRailMiscStC;
import erc.message.ERC_PacketHandler;
import erc.tileEntity.TileEntityRailBase;
import erc.tileEntity.TileEntityRailConstVelosity;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class blockRailConstVelocity extends blockRailBase{

	@Override
	public TileEntityRailBase getTileEntityInstance() 
	{
		return new TileEntityRailConstVelosity();
	}
	
	
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        if (!world.isRemote)
        {
            boolean flag = world.isBlockIndirectlyGettingPowered(x, y, z);
            
            if (flag || block.canProvidePower())
            {
            	TileEntityRailConstVelosity rail = (TileEntityRailConstVelosity)world.getTileEntity(x, y, z);
            	boolean tgle = rail.getToggleFlag();
            	
            	if (flag && !tgle)
                {
            		rail.changeToggleFlag();
            		rail.turnOnFlag();
                	ERC_PacketHandler.INSTANCE.sendToAll(new ERC_MessageRailMiscStC(rail));
                	world.playAuxSFXAtEntity((EntityPlayer)null, 1003, x, y, z, 0); //���ʉ��H
                }
            	else if(!flag && tgle)
                {
                	rail.changeToggleFlag();
                }
            }
        }
    }
}
