package erc.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import erc._core.ERC_Core;
import erc.item.ERC_ItemWrench;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ERC_MessageItemWrenchSync implements IMessage, IMessageHandler<ERC_MessageItemWrenchSync, IMessage>{
	
	public Item classItem;
	public int mode=-1;
	public int x;
	public int y;
	public int z;
	
	public ERC_MessageItemWrenchSync(){}
	
	public ERC_MessageItemWrenchSync(int mode,int x, int y, int z)
	{
		this.mode = mode;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(mode);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		mode = buf.readInt();
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}
	
	
	@Override
	public IMessage onMessage(ERC_MessageItemWrenchSync message, MessageContext ctx)
	{
//		((ERC_ItemWrench)ERC_Core.ItemWrench).receivePacket(message.mode);
		
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		switch(message.mode)
		{
		case 0 : break;// connect ‚È‚É‚à‚È‚¢
		case 1 : //gui
			player.openGui(ERC_Core.INSTANCE, ERC_Core.GUIID_RailBase, player.worldObj, message.x, message.y, message.z);
			break;
		case 2 : 
			((ERC_ItemWrench)ERC_Core.ItemWrench).placeBlockAt(new ItemStack(ERC_Core.ItemWrench), player, player.worldObj, message.x, message.y, message.z, Blocks.dirt);
			break;
		}
	    return null;
	}

}