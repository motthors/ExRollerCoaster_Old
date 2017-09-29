package erc.message;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import erc.entity.Wrap_EntityCoaster;
import io.netty.buffer.ByteBuf;

public class ERC_MessageCoasterMisc implements IMessage, IMessageHandler<ERC_MessageCoasterMisc, IMessage>{

	public Wrap_EntityCoaster coaster;
	public int EntityID;
	public int flag;

	public ERC_MessageCoasterMisc(){}
	
	public ERC_MessageCoasterMisc(Wrap_EntityCoaster c, int flag)
	{
		this.coaster = c;
		this.EntityID = c.getEntityId();
		this.flag = flag;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.EntityID);
		buf.writeInt(this.flag);
		coaster.SyncCoasterMisc_Send(buf, flag);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
		this.EntityID = buf.readInt();
	    this.flag = buf.readInt();
	       	
	    Wrap_EntityCoaster coaster = (Wrap_EntityCoaster)FMLClientHandler.instance().getClient().theWorld.getEntityByID(EntityID);
		if(coaster!=null)coaster.SyncCoasterMisc_Receive(buf, flag);
    }
	
	@Override
    public IMessage onMessage(ERC_MessageCoasterMisc message, MessageContext ctx)
    {
		return null;
    }
    
}