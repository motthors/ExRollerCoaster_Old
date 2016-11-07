package erc.tileEntity;

import org.lwjgl.opengl.GL11;

import erc.entity.ERC_EntityCoaster;
import erc.gui.GUIRail;
import erc.gui.GUIRail.editFlag;
import erc.message.ERC_MessageRailMiscStC;
import erc.message.ERC_PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class TileEntityRailRedstoneAccelerator extends TileEntityRailBase{
	
	float accelParam = 0.04f;
	boolean toggleflag;
	
	public TileEntityRailRedstoneAccelerator()
	{
		super();
		toggleflag = false;
		RailTexture = new ResourceLocation("textures/blocks/redstone_block.png");
	}

	public boolean getToggleFlag()
	{
		return toggleflag;
	}
	public void setToggleFlag(boolean flag)
	{
		toggleflag = flag;
	}
	public void changeToggleFlag()
	{
		toggleflag = !toggleflag;
	}
	
	public void setAccelParam(float f)
	{
		accelParam = f;
	}
	public float getAccelBase()
	{
		return accelParam;
	}
	
	public void SpecialRailProcessing(ERC_EntityCoaster coaster)
	{
		// ‰Á‘¬
		if(toggleflag)
		{
			coaster.Speed += accelParam;
		}
		// Œ¸‘¬
		else
		{
			coaster.Speed *= 0.8;
			if(coaster.Speed < 0.008)coaster.Speed = 0;
		}
	}

	
	public void setDataToByteMessage(ByteBuf buf)
	{
		buf.writeBoolean(this.toggleflag);
		buf.writeFloat(accelParam);
	}
	public void getDataFromByteMessage(ByteBuf buf)
	{
		toggleflag = buf.readBoolean();
		accelParam = buf.readFloat();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) 
	{
		super.readFromNBT(nbt);
		toggleflag = nbt.getBoolean("red:toggleflag");
		accelParam = nbt.getFloat("red:accelparam");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) 
	{
		super.writeToNBT(nbt);
		nbt.setBoolean("red:toggleflag", toggleflag);
		nbt.setFloat("red:accelparam", accelParam);
	}
	
    // GUI
    public void SpecialGUIInit(GUIRail gui)
    {
    	gui.addButton4("Accel Param", editFlag.SPECIAL);
    }
    public void SpecialGUISetData(int flag)
    {
    	switch(flag)
    	{
    	case 0 : accelParam += -0.01;   break;
    	case 1 : accelParam += -0.001;  break;
    	case 2 : accelParam +=  0.001;  break;
    	case 3 : accelParam +=  0.01;   break;
    	}
    	if(accelParam > 0.1f)accelParam = 0.1f;
    	else if(accelParam < -0.1f) accelParam = -0.1f;
    	ERC_PacketHandler.INSTANCE.sendToAll(new ERC_MessageRailMiscStC(this));
    }
    @Override
    public String SpecialGUIDrawString()
    {
    	return String.format("%02.1f", (accelParam * 100f));
    }
    
    public void render(Tessellator tess)
	{
    	float col = toggleflag?2.0f:0.3f;
    	GL11.glColor4f(col, col, col, 1.0F);
    	super.render(tess);
	}
}
