package erc.model;

import org.lwjgl.opengl.GL11;

import erc._core.ERC_Logger;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.IModelCustom;

public class ERC_ModelDefaultRail extends Wrap_RailRenderer {
	
	private int PosNum;
	Vec3 posArray[];
	Vec3 normalArray[];
	
	public ERC_ModelDefaultRail(){} //ロードするファイル名未指定インスタンス生成拒否できる？
	
	public ERC_ModelDefaultRail(IModelCustom Obj, ResourceLocation Tex){}
	
//	public void render(double x, double y, double z, double yaw, double pitch, double roll, double length) 
//	{
// 		GL11.glPushMatrix();
//		GL11.glTranslated(x, y, z);
//// 		if(coaster.ERCPosMat != null)
////		{
//// 			GL11.glMultMatrix(coaster.ERCPosMat.rotmat);
////		}
//		GL11.glRotated(yaw, 0, -1, 0);
// 		GL11.glRotated(pitch,1, 0, 0);
// 		GL11.glRotated(roll, 0, 0, 1);
//
//		GL11.glScaled(1.0, 1.0, length);
//		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureResource);
//		this.renderModel();
//		GL11.glPopMatrix();
//	}

	public void setModelNum(int PosNum_org)
	{
		PosNum = PosNum_org;
		posArray = new Vec3[PosNum_org*4];
		normalArray = new Vec3[PosNum_org];
		for(int i=0;i<PosNum*4;++i)posArray[i] = Vec3.createVectorHelper(0, 0, 0);
		for(int i=0;i<PosNum;++i)normalArray[i] = Vec3.createVectorHelper(0, 0, 0);
	}
	
	public void construct(int idx, Vec3 Pos, Vec3 Dir, Vec3 Cross, float exParam)
	{
		int j = idx*4;
		double t1 = 0.4 + 0.1;
		double t2 = 0.4 - 0.1;
		
		if(j>=posArray.length)
		{
			ERC_Logger.warn("ERC_DefaultRailModel : index exception");
			return;
		}
		
		// 左
		posArray[j  ].xCoord = Pos.xCoord - Cross.xCoord*t1;
		posArray[j  ].yCoord = Pos.yCoord - Cross.yCoord*t1;
		posArray[j  ].zCoord = Pos.zCoord - Cross.zCoord*t1;
		posArray[j+1].xCoord = Pos.xCoord - Cross.xCoord*t2;
		posArray[j+1].yCoord = Pos.yCoord - Cross.yCoord*t2;
		posArray[j+1].zCoord = Pos.zCoord - Cross.zCoord*t2;
		// 右 
		posArray[j+2].xCoord = Pos.xCoord + Cross.xCoord*t2;
		posArray[j+2].yCoord = Pos.yCoord + Cross.yCoord*t2;
		posArray[j+2].zCoord = Pos.zCoord + Cross.zCoord*t2;
		posArray[j+3].xCoord = Pos.xCoord + Cross.xCoord*t1;
		posArray[j+3].yCoord = Pos.yCoord + Cross.yCoord*t1;
		posArray[j+3].zCoord = Pos.zCoord + Cross.zCoord*t1;
		
		normalArray[idx] = Dir.crossProduct(Cross).normalize();
	}

	public void render(Tessellator tess)
	{
		float turnflag = 0f;
		tess.startDrawing(GL11.GL_TRIANGLE_STRIP);
		
		for(int i = 0; i<PosNum; ++i)
		{
			int index = i*4;
			tess.addVertexWithUV(posArray[index].xCoord, posArray[index].yCoord, posArray[index].zCoord, 0.0d, turnflag);
			tess.addVertexWithUV(posArray[index+1].xCoord, posArray[index+1].yCoord, posArray[index+1].zCoord, 1.0d, turnflag);
			turnflag = turnflag>0?0f:1f;
			tess.setNormal((float)normalArray[i].xCoord, (float)normalArray[i].yCoord, (float)normalArray[i].zCoord);
		}
		tess.draw();
		turnflag = 0f;
		tess.startDrawing(GL11.GL_TRIANGLE_STRIP);
		for(int i = 0; i<PosNum; ++i)
		{
			int index = i*4+2;
			tess.addVertexWithUV(posArray[index].xCoord, posArray[index].yCoord, posArray[index].zCoord, 0.0d, turnflag);
			tess.addVertexWithUV(posArray[index+1].xCoord, posArray[index+1].yCoord, posArray[index+1].zCoord, 1.0d, turnflag);
			turnflag = turnflag>0?0f:1f;
			tess.setNormal((float)normalArray[i].xCoord, (float)normalArray[i].yCoord, (float)normalArray[i].zCoord);
		}
		tess.draw();
	}
}