package erc.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import erc.manager.ERC_CoasterAndRailManager;
import io.netty.buffer.ByteBuf;

public class ERC_MessageSaveBreakRailStC implements IMessage, IMessageHandler<ERC_MessageSaveBreakRailStC, IMessage>{

	// 壊したレールブロックの接続先情報
	public int bx, by, bz;
	public int nx, ny, nz;
	
	public ERC_MessageSaveBreakRailStC(){}
	
	public ERC_MessageSaveBreakRailStC(int bx, int by, int bz, int nx, int ny, int nz)
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
    public IMessage onMessage(ERC_MessageSaveBreakRailStC message, MessageContext ctx)
	{
		ERC_CoasterAndRailManager.SetPrevData(message.bx, message.by, message.bz);
		ERC_CoasterAndRailManager.SetNextData(message.nx, message.ny, message.nz);
        return null;
    }
    
}