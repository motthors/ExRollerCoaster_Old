package erc.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import erc.gui.GUIRail;
import erc.tileEntity.TileEntityRailBase;
import erc.tileEntity.Wrap_TileEntityRail;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;

public class ERC_MessageRailGUICtS implements IMessage, IMessageHandler<ERC_MessageRailGUICtS, IMessage>{

	// GUIÇ©ÇÁëóÇËÇΩÇ¢èÓïÒ
	public int x, y, z;
	public int FLAG;
	public int MiscInt;
//	public int ControlPointNum;
//	public boolean smoothflag;
//	public float pow;
//	public int rotflag;
//	public float rotration;
//	public boolean reset;
	
	public ERC_MessageRailGUICtS(){}
	
//	public ERC_MessageRailGUICtS(int x, int y, int z, int cpnum, boolean smooth, float pow, int rotflag, float rotratio, boolean reset)
	public ERC_MessageRailGUICtS(int x, int y, int z, int flag, int imisc)
	{
	    this.x = x;
	    this.y = y;
	    this.z = z;
	    this.FLAG = flag;
	    this.MiscInt = imisc;
//	    this.ControlPointNum = cpnum;
//	    this.smoothflag = smooth;
//	    this.pow = pow;
//	    this.rotflag = rotflag;
//	    this.rotration = rotratio;
//	    this.reset = reset;
  	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
	    this.x = buf.readInt();
	    this.y = buf.readInt();
	    this.z = buf.readInt();
	    this.FLAG = buf.readInt();
	    this.MiscInt = buf.readInt();
//	    this.ControlPointNum = buf.readInt();
//	    this.smoothflag = buf.readBoolean();
//	    this.pow = buf.readFloat();
//	    this.rotflag = buf.readInt();
//	    this.rotration = buf.readFloat();
//	    this.reset = buf.readBoolean();
    }
	
	@Override
    public void toBytes(ByteBuf buf)
    {
    	buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.FLAG);
        buf.writeInt(this.MiscInt);
//        buf.writeInt(this.ControlPointNum);
//        buf.writeBoolean(this.smoothflag);
//        buf.writeFloat(this.pow);
//        buf.writeInt(this.rotflag);
//        buf.writeFloat(this.rotration);
//        buf.writeBoolean(this.reset);
    }
	
	@Override
    public IMessage onMessage(ERC_MessageRailGUICtS message, MessageContext ctx)
    {
    	TileEntity Wte = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
        if ((Wte instanceof Wrap_TileEntityRail))
        {
        	Wrap_TileEntityRail te = ((Wrap_TileEntityRail) Wte);
        	GUIRail.editFlag[] values = GUIRail.editFlag.values();
        	GUIRail.editFlag align = values[message.FLAG];
        	switch(align)
        	{
        	case CONTROLPOINT:
        		te.AddControlPoint((int) message.MiscInt); break;
        		
        	case SMOOTH:
//        		if(te.isConnectRail_prev1_next2())
        		te.Smoothing(); 
        		break;
        		
        	case POW:
        		te.AddPower(message.MiscInt); break;
        		
        	case ROTRED:
        	case ROTGREEN:
        	case ROTBLUE:
        		te.UpdateDirection(align, message.MiscInt); break;
        	
        	case RESET:
        		te.ResetRot(); break;
        		
        	case SPECIAL:
        		te.SpecialGUISetData(message.MiscInt); break;
        		
        	case RailModelIndex: // modelIndex send to server ä‘éÿÇËÇµÇƒÇ‹Ç∑
        		te.changeRailModelRenderer(message.MiscInt);
        		return null;
        	}
        	
        	te.CalcRailLength();
        	te.syncData();
        	Wrap_TileEntityRail prev = ((Wrap_TileEntityRail) Wte).getPrevRailTileEntity();
        	if(prev!=null)
        	{
        		TileEntityRailBase r = prev.getRail();
        		r.SetNextRailVectors((TileEntityRailBase) te.getRail());
//        		r.CreateNewRailVertexFromControlPoint();
        		r.CalcRailLength();
        		prev.syncData();
        	}
        }
        return null;
    }
    
}
