package erc.model;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import erc.entity.ERC_EntityCoaster;
import net.minecraft.client.model.ModelBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;

public class ERC_ModelCoaster extends ModelBase {
	
	private IModelCustom modelCoaster;
	private ResourceLocation TextureResource;
	
	@SuppressWarnings("unused")
	private ERC_ModelCoaster(){} //ロードするファイル名未指定インスタンス生成拒否できる？
	
	public ERC_ModelCoaster(IModelCustom Obj, ResourceLocation Tex)
	{
		modelCoaster = Obj;
		TextureResource = Tex;
	}
	
	private void render() 
	{
		modelCoaster.renderAll();
	}
	
	public void render(ERC_EntityCoaster coaster, double x, double y, double z, float t) 
	{
 		GL11.glPushMatrix();
		GL11.glTranslatef((float)x, (float)y, (float)z);
// 		if(coaster.ERCPosMat != null)
//		{
// 			GL11.glMultMatrix(coaster.ERCPosMat.rotmat);
//		}
		GL11.glRotatef(coaster.ERCPosMat.getFixedYaw(t), 0f, -1f, 0f);
 		GL11.glRotatef(coaster.ERCPosMat.getFixedPitch(t),1f, 0f, 0f);
 		GL11.glRotatef(coaster.ERCPosMat.getFixedRoll(t), 0f, 0f, 1f);

		GL11.glScalef(1.0f, 1.0f, 1.0f);
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureResource);
		this.render();
		GL11.glPopMatrix();
	}

}