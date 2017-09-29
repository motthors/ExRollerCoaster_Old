package erc.tileEntity;

import erc.entity.ERC_EntityCoaster;
import erc.entity.ERC_EntityCoasterConnector;
import erc.message.ERC_MessageRailMiscStC;
import erc.message.ERC_PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;

public class TileEntityRailDetector extends TileEntityRailBase{
	
	boolean outputFlag;
	
	public TileEntityRailDetector()
	{
		super();
		outputFlag = false;
//		RailTexture = new ResourceLocation("textures/blocks/stone.png");
	}
	
	public void changeOutput()
	{
		outputFlag = !outputFlag;
	}
	public void setOutput(boolean flag)
	{
		outputFlag = flag;
	}
	public boolean getFlag()
	{
		return outputFlag;
	}
	
	public void SpecialRailProcessing(ERC_EntityCoaster coaster)
	{
		if(!outputFlag && !(coaster instanceof ERC_EntityCoasterConnector))
		{
			// 出力開始
			Block block = worldObj.getBlock(xCoord, yCoord, zCoord);
			
			outputFlag = true;
			ERC_PacketHandler.INSTANCE.sendToAll(new ERC_MessageRailMiscStC(this));
	        worldObj.notifyBlocksOfNeighborChange (xCoord, yCoord, zCoord, block);
	        worldObj.notifyBlocksOfNeighborChange (xCoord, yCoord - 1,zCoord, block);
	        worldObj.playSoundEffect((double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)xCoord + 0.5D, "random.click", 0.3F, 0.6F);
		}
	}

	@Override
	public void onPassedCoaster(ERC_EntityCoaster coaster) 
	{
		if(!(coaster instanceof ERC_EntityCoasterConnector))
		{
			stopOutput();
		}
	}
	
	public void onDeleteCoaster()
	{
		stopOutput();
	}
	
	private void stopOutput()
	{
		// 出力停止
		outputFlag = false;
		Block block = worldObj.getBlock(xCoord, yCoord, zCoord);
        worldObj.notifyBlocksOfNeighborChange (xCoord, yCoord, zCoord, block);
        worldObj.notifyBlocksOfNeighborChange (xCoord, yCoord - 1,zCoord, block);
        worldObj.playSoundEffect((double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)xCoord + 0.5D, "random.click", 0.3F, 0.6F);

	}
	
	public void setDataToByteMessage(ByteBuf buf)
	{
		buf.writeBoolean(this.outputFlag);
	}
	public void getDataFromByteMessage(ByteBuf buf)
	{
		outputFlag = buf.readBoolean();
	}
}