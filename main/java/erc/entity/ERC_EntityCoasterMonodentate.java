package erc.entity;

import erc._core.ERC_Core;
import erc.message.ERC_MessageCoasterCtS;
import erc.message.ERC_PacketHandler;
import erc.tileEntity.TileEntityRailBase;
import erc.tileEntity.TileEntityRailBranch2;
import erc.tileEntity.Wrap_TileEntityRail;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.world.World;

/*
 * 単座のクライアント側位置処理コースター
 */
public class ERC_EntityCoasterMonodentate extends ERC_EntityCoaster{

	public ERC_EntityCoasterMonodentate(World world)
	{
		super(world);
	}
	
	public ERC_EntityCoasterMonodentate(World world, TileEntityRailBase tile, double x, double y, double z) {
		super(world, tile, x, y, z);
	}
	
	protected boolean canConnectForrowingCoaster()
	{
		return false;
	}
	
	public Item getItem()
    {
    	return ERC_Core.ItemCoasterMono;
    }

	@Override
	public void setParamFromPacket(float t, double speed, int x, int y, int z)
    {
    	 // 乗っているのが自分だったらパケット送り返し、他人のや誰も乗ってないコースターならサーバーと同期
    	if(this.riddenByEntity instanceof EntityClientPlayerMP)
    	{
    		if(tlrail==null)
    		{
    			if(checkTileEntity())
				{
    				killCoaster();
					return;
				}
    		}
	    	// send packet to server
	    	ERC_MessageCoasterCtS packet = new ERC_MessageCoasterCtS(getEntityId(), this.paramT, this.Speed, tlrail.xCoord, tlrail.yCoord, tlrail.zCoord);
		    ERC_PacketHandler.INSTANCE.sendToServer(packet);
    	}
    	else
    	{
    		Wrap_TileEntityRail rail = (Wrap_TileEntityRail)worldObj.getTileEntity(x,y,z);
    		if(rail == null)return;
    		if(rail instanceof TileEntityRailBranch2)return; // 分岐レール上のときは同期をちょっとやめてほしい
    		
    		this.setParamT(t);
    		this.Speed = speed;
    		this.setRail( rail.getRail() );
//    		if(tlrail==null)
//    		{
//    			if(checkTileEntity())
//				{
//    				killCoaster();
//					return;
//				}
//    		}
    	}
		
    }
}
