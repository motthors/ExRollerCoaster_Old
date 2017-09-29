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

public class TileEntityRailConstVelosity extends TileEntityRailBase{
	
	float constVelosityParam;
	boolean toggleflag;
	boolean onflag;
	
	public TileEntityRailConstVelosity()
	{
		super();
		toggleflag = false;
		constVelosityParam = 0.07f;
		RailTexture = new ResourceLocation("textures/blocks/cobblestone.png");
	}
	
	public boolean getToggleFlag()
	{
		return toggleflag;
	}
	public void changeToggleFlag()
	{
		toggleflag = !toggleflag;
	}
	public void turnOnFlag()
	{
		onflag = !onflag;
	}

	public void setconstVelosityParam(float f)
	{
		constVelosityParam = f;
	}
	public float getconstVelosityParam()
	{
		return constVelosityParam;
	}
	
	public void SpecialRailProcessing(ERC_EntityCoaster coaster)
	{
		coaster.Speed -= constVelosityParam * (onflag?1f:0f);
		coaster.Speed *= 0.9;
		if(coaster.Speed < 0.008)coaster.Speed = 0;
		coaster.Speed += constVelosityParam * (onflag?1f:0f);
	}

	public void setDataToByteMessage(ByteBuf buf)
	{
		buf.writeBoolean(this.onflag);
		buf.writeFloat(constVelosityParam);
	}
	public void getDataFromByteMessage(ByteBuf buf)
	{
		onflag = buf.readBoolean();
		constVelosityParam = buf.readFloat();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) 
	{
		super.readFromNBT(nbt);
		onflag = nbt.getBoolean("const:onflag");
		constVelosityParam = nbt.getFloat("constvel");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) 
	{
		super.writeToNBT(nbt);
		nbt.setBoolean("const:onflag", onflag);
		nbt.setFloat("constvel", constVelosityParam);
	}
	
	// GUI
    public void SpecialGUIInit(GUIRail gui)
    {
    	gui.addButton4("Const Velocity Param", editFlag.SPECIAL);
    }
    public void SpecialGUISetData(int flag)
    {
    	switch(flag)
    	{
    	case 0 : constVelosityParam += -0.1;   break;
    	case 1 : constVelosityParam += -0.01;  break;
    	case 2 : constVelosityParam +=  0.01;  break;
    	case 3 : constVelosityParam +=  0.1;   break;
    	}
    	if(constVelosityParam > 5.0f)constVelosityParam = 0.1f;
    	else if(constVelosityParam < -5.0f) constVelosityParam = -5.0f;
    	ERC_PacketHandler.INSTANCE.sendToAll(new ERC_MessageRailMiscStC(this));
    }
    @Override
    public String SpecialGUIDrawString()
    {
    	return String.format("%02.1f", (constVelosityParam * 10f));
    }
    
    public void render(Tessellator tess)
   	{
       	float col = toggleflag?2.0f:0.6f;
       	GL11.glColor4f(col, col, col, 1.0F);
       	super.render(tess);
   	}
}
