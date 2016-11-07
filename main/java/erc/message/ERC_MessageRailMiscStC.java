package erc.message;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import erc.tileEntity.Wrap_TileEntityRail;
import io.netty.buffer.ByteBuf;

public class ERC_MessageRailMiscStC implements IMessage, IMessageHandler<ERC_MessageRailMiscStC, IMessage>{
		
	int x;
	int y;
	int z;
	Wrap_TileEntityRail rail;

	public ERC_MessageRailMiscStC(){}
	
	public ERC_MessageRailMiscStC(Wrap_TileEntityRail r)
	{
	    this.rail = r;
	    this.x = rail.xCoord;
	    this.y = rail.yCoord;
	    this.z = rail.zCoord;
  	}
	
	@Override
    public void toBytes(ByteBuf buf)
    {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		rail.setDataToByteMessage(buf);
    }
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		Wrap_TileEntityRail rail = (Wrap_TileEntityRail)FMLClientHandler.instance().getClient().theWorld.getTileEntity(x, y, z);
		if(rail==null)return;
		rail.getDataFromByteMessage(buf);
    }
	
	
	@Override
    public IMessage onMessage(ERC_MessageRailMiscStC message, MessageContext ctx)
    {
        return null;
    }
    
}