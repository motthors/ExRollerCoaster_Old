package erc.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import erc._core.ERC_Core;
import erc.entity.ERC_EntityCoaster;
import erc.entity.ERC_EntityCoasterConnector;
import erc.item.ERC_ItemCoaster;
import erc.item.ERC_ItemCoasterConnector;
import erc.item.ERC_ItemCoasterMonodentate;
import erc.item.Wrap_ItemCoaster;
import erc.manager.ERC_ModelLoadManager.ModelOptions;
import erc.tileEntity.Wrap_TileEntityRail;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;

public class ERC_MessageSpawnRequestWithCoasterOpCtS implements IMessage, IMessageHandler<ERC_MessageSpawnRequestWithCoasterOpCtS, IMessage>{
	
	int itemID;
	int modelID;
	ModelOptions ops;
	int x, y, z;
	int parentID = -1;
	
	public ERC_MessageSpawnRequestWithCoasterOpCtS(){ops = new ModelOptions();}
	
	public ERC_MessageSpawnRequestWithCoasterOpCtS(Wrap_ItemCoaster item, int modelid, ModelOptions op,int x,int y,int z)
	{
		if(item instanceof ERC_ItemCoasterConnector)itemID = 2;
		else if(item instanceof ERC_ItemCoasterMonodentate)itemID = 3;
		else if(item instanceof ERC_ItemCoaster)itemID = 1;
		modelID = modelid;
		ops = op;
		this.x=x;this.y=y;this.z=z;
	}
	public ERC_MessageSpawnRequestWithCoasterOpCtS(Wrap_ItemCoaster item, int modelid, ModelOptions op,int x,int y,int z, int parentid)
	{
		this(item, modelid, op, x, y, z);
		parentID = parentid;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(itemID);
		buf.writeInt(modelID);
		ops.WriteBuf(buf);
		buf.writeInt(x); 
		buf.writeInt(y); 
		buf.writeInt(z); 
		buf.writeInt(parentID); 
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
	{
		itemID = buf.readInt(); 
		modelID = buf.readInt();
		ops.ReadBuf(buf);
		x = buf.readInt(); 
		y = buf.readInt(); 
		z = buf.readInt(); 
		parentID = buf.readInt();
	}

	@Override
    public IMessage onMessage(ERC_MessageSpawnRequestWithCoasterOpCtS m, MessageContext ctx)
    {
		// spawn!
		World world = ctx.getServerHandler().playerEntity.worldObj;
		Wrap_TileEntityRail tile = (Wrap_TileEntityRail)world.getTileEntity(m.x, m.y, m.z);
		Wrap_ItemCoaster item = (Wrap_ItemCoaster) (m.itemID==1?ERC_Core.ItemCoaster:(m.itemID==2?ERC_Core.ItemCoasterConnector:ERC_Core.ItemCoasterMono));
		ERC_EntityCoaster entitycoaster = item.getItemInstance(world, tile, (double)((float)m.x + 0.5F), (double)((float)m.y + 0.6F), (double)((float)m.z + 0.5F));
		entitycoaster.setModelOptions(m.modelID, m.ops); // ‹­§“I‚ÉOP‚ðÝ’è‚³‚¹‚é‚½‚ß‚Ì-1
		
		if(m.parentID > -1)
		{
			ERC_EntityCoaster parent = (ERC_EntityCoaster) world.getEntityByID(m.parentID);
			parent.connectionCoaster((ERC_EntityCoasterConnector) entitycoaster);
			((ERC_EntityCoasterConnector)entitycoaster).setParent(parent);
		}
		
		world.spawnEntityInWorld(entitycoaster);

		return null;
    }
}