package erc.message;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import erc._core.ERC_Logger;
import erc.tileEntity.DataTileEntityRail;
import erc.tileEntity.Wrap_TileEntityRail;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;

public class ERC_MessageRailStC implements IMessage, IMessageHandler<ERC_MessageRailStC, IMessage>{

	// GUIÇ©ÇÁëóÇËÇΩÇ¢èÓïÒ
	public int x, y, z;
	int railnum;
	public int posnum;
	public List<DataTileEntityRail> raillist;
	public int modelIndex;

	public ERC_MessageRailStC(){}
	
	public ERC_MessageRailStC(int x, int y, int z, int cpnum, int modelidx)
	{
		this.x = x; this.y = y; this.z = z; this.posnum = cpnum; railnum = 0;
		raillist = new ArrayList<DataTileEntityRail>();
		modelIndex = modelidx;
	}
	
	public void addRail(DataTileEntityRail rail)
	{
		railnum++;
		raillist.add(rail);
	}
	
	private Vec3 readVec(ByteBuf buf)
	{
		 return Vec3.createVectorHelper(buf.readDouble(), buf.readDouble(), buf.readDouble());
	}
	private void writeVec(ByteBuf buf, Vec3 vec)
	{
		buf.writeDouble(vec.xCoord);
	 	buf.writeDouble(vec.yCoord);
	    buf.writeDouble(vec.zCoord);
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.modelIndex = buf.readInt();
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.posnum = buf.readInt();
		int railnum = buf.readInt();
		raillist = new ArrayList<DataTileEntityRail>();
		for(int i=0; i<railnum; ++i)
		{
			DataTileEntityRail r = new DataTileEntityRail();
			r.cx = buf.readInt();r.cy = buf.readInt();r.cz = buf.readInt();
			r.vecPos = readVec(buf);
			r.vecDir = readVec(buf);
			r.vecUp = readVec(buf);
		   	r.Power = buf.readFloat();
		   	r.fUp = buf.readFloat();
		   	r.fDirTwist = buf.readFloat();		   	
		   	raillist.add(r);
		}
	}
	
	@Override
    public void toBytes(ByteBuf buf)
    {
		buf.writeInt(modelIndex);
    	buf.writeInt(this.x); 
    	buf.writeInt(this.y); 
    	buf.writeInt(this.z);
    	buf.writeInt(this.posnum);
    	buf.writeInt(raillist.size());
    	for( DataTileEntityRail r : raillist)
		{
			buf.writeInt(r.cx); buf.writeInt(r.cy); buf.writeInt(r.cz);
		    writeVec(buf,r.vecPos);		 
		    writeVec(buf,r.vecDir);		 
		    writeVec(buf,r.vecUp);
		    buf.writeFloat(r.Power);
		    buf.writeFloat(r.fUp);
		    buf.writeFloat(r.fDirTwist);
		}
    }
	
	@Override
    public IMessage onMessage(ERC_MessageRailStC message, MessageContext ctx)
    {
//    	TileEntity te = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
    	TileEntity te = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x, message.y, message.z);
        if (!(te instanceof Wrap_TileEntityRail))
        {
        	ERC_Logger.info("MessageRailStC::onMessage, tileentity is not tilerail. pos:"+message.x+"."+message.y+"."+message.z);
        	if(te==null)ERC_Logger.info("ÅEÅEÅETileEntity is null.");
        	return null;
        }
              
        Wrap_TileEntityRail ter = ((Wrap_TileEntityRail)te);
        ter.SetRailDataFromMessage(message);
        ter.changeRailModelRenderer(message.modelIndex);
//        ERC_TileEntityRailBase ter = ((Wrap_TileEntityRail)te).getRail();
//    	ter.SetPosNum(message.posnum);	
//    	ter.SetRailDataFromRailListMessage(message.raillist);
//    	ter.CreateNewRailVertexFromControlPoint();
//    	ter.CalcRailPosition();
        return null;
    }
    
}
