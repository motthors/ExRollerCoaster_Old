package erc.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import erc.tileEntity.Wrap_TileEntityRail;
import io.netty.buffer.ByteBuf;

public class ERC_MessageConnectRailCtS implements IMessage, IMessageHandler<ERC_MessageConnectRailCtS, IMessage>{

	// クライアントから来るレールの接続要請メッセージ
	
	public int bx, by, bz;
	public int nx, ny, nz;
	
	public ERC_MessageConnectRailCtS(){}
	
	public ERC_MessageConnectRailCtS(int bx, int by, int bz, int nx, int ny, int nz)
	{
	    this.bx = bx;
	    this.by = by;
	    this.bz = bz;
	    this.nx = nx;
	    this.ny = ny;
	    this.nz = nz;
  	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.bx);
		buf.writeInt(this.by);
		buf.writeInt(this.bz);
		buf.writeInt(this.nx);
		buf.writeInt(this.ny);
		buf.writeInt(this.nz);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
	    this.bx = buf.readInt();
	    this.by = buf.readInt();
	    this.bz = buf.readInt();
	    this.nx = buf.readInt();
	    this.ny = buf.readInt();
	    this.nz = buf.readInt();
    }
	
	@Override
    public IMessage onMessage(ERC_MessageConnectRailCtS message, MessageContext ctx)
    {
		Wrap_TileEntityRail Wbase = (Wrap_TileEntityRail)ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.bx, message.by, message.bz);
		Wrap_TileEntityRail Wnext = (Wrap_TileEntityRail)ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.nx, message.ny, message.nz);
    	
        if ((Wbase != null && Wnext != null))
        {
//        	ERC_TileEntityRailBase base = Wbase.getRail();
//        	ERC_TileEntityRailBase next = Wnext.getRail();
        	

//        	next.SetPrevRailPosition(message.bx, message.by, message.bz);
//        	next.CreateNewRailVertexFromControlPoint();
//        	next.CalcRailLength();
//        	next.syncData();
        	Wnext.connectionFromBack(message.bx, message.by, message.bz);
        	
//        	base.BaseRail.Power = power; // TODO check
//        	base.SetNextRailPosition(message.nx, message.ny, message.nz);
//        	base.SetNextRailVectors(next);
//        	base.CreateNewRailVertexFromControlPoint();
//        	base.CalcRailLength();
//        	base.syncData();
        	Wbase.connectionToNext(Wnext.getRail().BaseRail, message.nx, message.ny, message.nz);
   
        }
        return null;
    }
    
}