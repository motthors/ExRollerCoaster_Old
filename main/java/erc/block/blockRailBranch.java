package erc.block;

import erc.message.ERC_MessageRailMiscStC;
import erc.message.ERC_PacketHandler;
import erc.tileEntity.TileEntityRailBranch2;
import erc.tileEntity.Wrap_TileEntityRail;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class blockRailBranch extends blockRailBase{

	@Override
	public Wrap_TileEntityRail getTileEntityInstance() 
	{
		return new TileEntityRailBranch2();
	}

	 @Override
	public boolean canProvidePower() 
	 {
		return true;
	}

	// 赤石入力制御
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        if (!world.isRemote)
        {
            boolean flag = world.isBlockIndirectlyGettingPowered(x, y, z);
            boolean flag2 = block.canProvidePower();
            if (flag || flag2)
            {
            	TileEntityRailBranch2 rail = (TileEntityRailBranch2)world.getTileEntity(x, y, z);
            	boolean tgle = rail.getToggleFlag();
            	
                if (flag && !tgle)
                {
                	rail.changeRail();
                	rail.changeToggleFlag();
                	ERC_PacketHandler.INSTANCE.sendToAll(new ERC_MessageRailMiscStC(rail));
                	world.playAuxSFXAtEntity((EntityPlayer)null, 1003, x, y, z, 0); //効果音？
                }
                else if(!flag && tgle)
                {
                	rail.changeToggleFlag();
                }
            }
        }
    }
  
    // TileEntity初期化　Branch用特殊処理　レール２つとも初期化
    protected void onTileEntityInitFirst(World world, EntityLivingBase player, Wrap_TileEntityRail rail, int x, int y, int z)
	{
		// ブロック設置時のプレイヤーの向き
    	TileEntityRailBranch2 railb = (TileEntityRailBranch2) rail;
    	Vec3 metadir = ConvertVec3FromMeta(world.getBlockMetadata(x, y, z));
    	Vec3 BaseDir = Vec3.createVectorHelper(
				-Math.sin(Math.toRadians(player.rotationYaw)) * (metadir.xCoord!=0?0:1), 
				Math.sin(Math.toRadians(player.rotationPitch)) * (metadir.yCoord!=0?0:1), 
				Math.cos(Math.toRadians(player.rotationYaw)) * (metadir.zCoord!=0?0:1));
    	
    	railb.SetBaseRailPosition(x, y, z, BaseDir, metadir, 20f);
    	
    	int saveflag = railb.getNowRailFlag();
    	railb.changeRail(0);
    	for(int i=0; i<2; ++i)
    	{
    		railb.changeRail(i);
			double yaw = ((float)i-0.5) + Math.toRadians(player.rotationYaw);
			double pit = ((float)i-0.5) - Math.toRadians(player.rotationPitch);
			
			Vec3 vecDir = Vec3.createVectorHelper(
					-Math.sin(yaw) * (metadir.xCoord!=0?0:1), 
					Math.sin(pit) * (metadir.yCoord!=0?0:1), 
					Math.cos(yaw) * (metadir.zCoord!=0?0:1) );
						
			// 新規設置のレールに対して座標設定。　向きはプレイヤーの向いている方向へ			
//			railb.SetNextRailPosition(x+(int)(vecDir.xCoord*10), y+(int)(vecDir.yCoord*10), z+(int)(vecDir.zCoord*10));
			railb.SetNextRailVectors(
					Vec3.createVectorHelper(x+(int)(vecDir.xCoord*10)+0.5, y+(int)(vecDir.yCoord*10)+0.5, z+(int)(vecDir.zCoord*10)+0.5), 
					vecDir, 
					railb.getRail().BaseRail.vecUp, 
					0f, 0f,
					railb.getRail().BaseRail.Power,
					-1, -1, -1);
    	}
    	railb.changeRail(saveflag);
	}
}
