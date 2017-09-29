package erc.model;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import erc.math.ERC_MathHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.IModelCustom;

public class ERC_ModelAddedRail extends Wrap_RailRenderer {
	
	private IModelCustom modelRail;
	private ResourceLocation TextureResource;
	private int ModelNum;
	private Vec3[] pos;
	private Vec3[] rot;
	private float[] Length;
	
	@SuppressWarnings("unused")
	private ERC_ModelAddedRail(){} //ロードするファイル名未指定インスタンス生成拒否できる？
	
	public ERC_ModelAddedRail(IModelCustom Obj, ResourceLocation Tex)
	{
		modelRail = Obj;
		TextureResource = Tex;
	}
	
	private void renderModel() 
	{
		modelRail.renderAll();
	}
	
	public void render(double x, double y, double z, double yaw, double pitch, double roll, double length) 
	{
 		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
// 		if(coaster.ERCPosMat != null)
//		{
// 			GL11.glMultMatrix(coaster.ERCPosMat.rotmat);
//		}
		GL11.glRotated(yaw, 0, -1, 0);
 		GL11.glRotated(pitch,1, 0, 0);
 		GL11.glRotated(roll, 0, 0, 1);

		GL11.glScaled(1.0, 1.0, length);
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureResource);
		this.renderModel();
		GL11.glPopMatrix();
	}

	public void setModelNum(int PosNum_org)
	{
		ModelNum = PosNum_org-1;
		pos = new Vec3[ModelNum];
		rot = new Vec3[ModelNum];
		Length = new float[ModelNum];
		for(int i=0;i<ModelNum;++i)pos[i] = Vec3.createVectorHelper(0, 0, 0);
		for(int i=0;i<ModelNum;++i)rot[i] = Vec3.createVectorHelper(0, 0, 0);
	}
	
	public void construct(int idx, Vec3 Pos, Vec3 Dir, Vec3 Cross, float exParam)
	{
		if(idx>=ModelNum)return;
		// 位置
		pos[idx] = Pos;
		// 角度
		Vec3 crossHorz = Vec3.createVectorHelper(0, 1, 0).crossProduct(Dir);
		Vec3 dir_horz = Vec3.createVectorHelper(Dir.xCoord, 0, Dir.zCoord);
		rot[idx].xCoord = -Math.toDegrees( Math.atan2(Dir.xCoord, Dir.zCoord) );
		rot[idx].yCoord = Math.toDegrees( ERC_MathHelper.angleTwoVec3(Dir, dir_horz) * (Dir.yCoord>0?-1f:1f) );
		rot[idx].zCoord = Math.toDegrees( ERC_MathHelper.angleTwoVec3(Cross, crossHorz) * (Cross.yCoord>0?1f:-1f) );
		// 長さ
		Length[idx] = exParam;
	}

	public void render(Tessellator tess)
	{
		for(int i=0;i<ModelNum;++i)
		render(pos[i].xCoord, pos[i].yCoord, pos[i].zCoord,
				rot[i].xCoord, rot[i].yCoord, rot[i].zCoord, Length[i]);
	}
}