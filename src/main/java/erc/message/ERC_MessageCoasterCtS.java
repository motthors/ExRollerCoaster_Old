package erc.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import erc._core.ERC_Logger;
import erc.entity.ERC_EntityCoaster;
import erc.tileEntity.Wrap_TileEntityRail;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;

public class ERC_MessageCoasterCtS implements IMessage, IMessageHandler<ERC_MessageCoasterCtS, IMessage>{

	// クライアントからのレール座標用パラメータ関連メッセージ
	public int entityID;
	public float paramT;
	public double speed;
	// 現在乗っているレールの座標
	public int x;
	public int y;
	public int z;
//	// モデル描画オプション
//	public int modelID;
//	public ModelOptions ops;
	
	public ERC_MessageCoasterCtS(){/*ops = new ModelOptions();*/}
	
	public ERC_MessageCoasterCtS(int id, float t, double v, int x, int y, int z)
	{
		super();
		
	    this.paramT = t;
	    this.entityID = id;
	    this.speed = v;
	    this.x = x;
	    this.y = y;
	    this.z = z;
	    
//	    this.modelID = ID;
//	    this.ops = op;
  	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeFloat(this.paramT);
		buf.writeInt(this.entityID);
		buf.writeDouble(this.speed);
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		
//		buf.writeInt(this.modelID);
//		ops.WriteBuf(buf);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
	    this.paramT = buf.readFloat();
	    this.entityID = buf.readInt();
	    this.speed = buf.readDouble();
	    this.x = buf.readInt();
	    this.y = buf.readInt();
	    this.z = buf.readInt();
	    
//	    this.modelID = buf.readInt(); 
//	    ops.ReadBuf(buf);
    }
		
	@Override
    public IMessage onMessage(ERC_MessageCoasterCtS message, MessageContext ctx)
    {
		World world = ctx.getServerHandler().playerEntity.worldObj;
		ERC_EntityCoaster coaster = (ERC_EntityCoaster)world.getEntityByID(message.entityID);
		if(coaster == null)return null;
		if(message.paramT > -50f)
		{
			coaster.setParamT(message.paramT);
			coaster.Speed = message.speed;
			coaster.setRail( ((Wrap_TileEntityRail) world.getTileEntity(message.x, message.y, message.z)).getRail() );
//			coaster.setModel(message.modelID);
		}
		else
		{
			ERC_Logger.warn("MessageCoasterCtS : this code must not call.");
//			coaster.setModel(message.modelID);
//			coaster.setModelOptions(message.ops);
		}
        return null;
    }
    
}