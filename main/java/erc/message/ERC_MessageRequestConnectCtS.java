package erc.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import erc.entity.ERC_EntityCoasterConnector;
import io.netty.buffer.ByteBuf;

public class ERC_MessageRequestConnectCtS implements IMessage, IMessageHandler<ERC_MessageRequestConnectCtS, IMessage>{

	public int playerEntityID;
	public int CoasterID;
	
	public ERC_MessageRequestConnectCtS(){}
	
	public ERC_MessageRequestConnectCtS(int playerid, int coasterid)
	{
		playerEntityID = playerid;
		CoasterID = coasterid;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.playerEntityID);
		buf.writeInt(this.CoasterID);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
		this.playerEntityID = buf.readInt();
		this.CoasterID = buf.readInt();
    }	    	

	@Override
    public IMessage onMessage(ERC_MessageRequestConnectCtS message, MessageContext ctx)
    {
		ERC_EntityCoasterConnector coaster = (ERC_EntityCoasterConnector) ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.CoasterID);
		coaster.receiveConnectionRequestFromClient(message.playerEntityID);
		return null;
    }
}