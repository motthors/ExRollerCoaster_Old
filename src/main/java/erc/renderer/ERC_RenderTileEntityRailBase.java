package erc.renderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erc._core.ERC_CONST;
import erc.manager.ERC_CoasterAndRailManager;
import erc.tileEntity.Wrap_TileEntityRail;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

@SideOnly(Side.CLIENT)
public class ERC_RenderTileEntityRailBase extends TileEntitySpecialRenderer{
	
//	private static final ResourceLocation TEXTURE  ;
	private static final ResourceLocation TEXTUREguiarraw = new ResourceLocation(ERC_CONST.DOMAIN,"textures/gui/ringarraw.png");
	//new ResourceLocation(", "textures/blocks/pink.png");
	
	public void renderTileEntityAt(Wrap_TileEntityRail t, double x, double y, double z, float f)
	{
		Tessellator tessellator = Tessellator.instance;
		this.bindTexture(t.getDrawTexture());
		GL11.glDisable(GL11.GL_CULL_FACE); // カリングOFF
		GL11.glPushMatrix();
		GL11.glTranslated(x+0.5, y+0.5, z+0.5);
		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);

		t.render(tessellator);
		
		
		//GL11.glTranslated(t.xCoord, t.yCoord, t.zCoord);
		//GL11.glTranslated(0.5, y-t.yCoord, z-t.zCoord);
    	
    	if(t == ERC_CoasterAndRailManager.clickedTileForGUI){
    		DrawRotaArrow(tessellator, t);
    	}
    	//DrawArrow(tessellator, t.vecUp);
    	
      	GL11.glEnable(GL11.GL_CULL_FACE); // カリングON
      	GL11.glPopMatrix();
	}
	
	public void p_bindTexture(ResourceLocation texture){ this.bindTexture(texture);}
	
	@SuppressWarnings("unused")
	private void DrawArrow(Tessellator tess, Vec3 vec)
	{
      	tess.startDrawing(GL11.GL_TRIANGLES);
      	tess.addVertexWithUV(0.2d, 0d, 0.2d, 0.0d, 0.0d);
      	tess.addVertexWithUV(vec.xCoord*3d, vec.yCoord*3d, vec.zCoord*3d, 0.0d, 0.0d);
      	tess.addVertexWithUV(-0.2d, 0d, -0.2d, 0.0d, 0.0d);
      	tess.draw();
	}
	
	// GUI表示中の回転矢印描画用
	public void DrawRotaArrow(Tessellator tess, Wrap_TileEntityRail tile)
	{
		this.bindTexture(TEXTUREguiarraw);
      	Vec3 d = tile.getRail().BaseRail.vecDir;
		Vec3 u = tile.getRail().BaseRail.vecUp;
      	Vec3 p = d.crossProduct(u);
      	
      	d = d.normalize();
      	u = u.normalize();
      	p = p.normalize();
      	
      	float s = 2.0f; // s
      	
      	// yaw axis
      	GL11.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);
		tess.startDrawing(GL11.GL_TRIANGLE_STRIP);
	    tess.addVertexWithUV(( d.xCoord+p.xCoord)*s, ( d.yCoord+p.yCoord)*s, ( d.zCoord+p.zCoord)*s, 0.0d, 0.0d);
		tess.addVertexWithUV(( d.xCoord-p.xCoord)*s, ( d.yCoord-p.yCoord)*s, ( d.zCoord-p.zCoord)*s, 1.0d, 0.0d);
		tess.addVertexWithUV((-d.xCoord+p.xCoord)*s, (-d.yCoord+p.yCoord)*s, (-d.zCoord+p.zCoord)*s, 0.0d, 1.0d);
		tess.addVertexWithUV((-d.xCoord-p.xCoord)*s, (-d.yCoord-p.yCoord)*s, (-d.zCoord-p.zCoord)*s, 1.0d, 1.0d);
		tess.draw();
		// pitch axis
		s = 1.5f;
		GL11.glColor4f(0.0F, 1.0F, 0.0F, 1.0F);  
		tess.startDrawing(GL11.GL_TRIANGLE_STRIP);
	    tess.addVertexWithUV(( u.xCoord+d.xCoord)*s, ( u.yCoord+d.yCoord)*s, ( u.zCoord+d.zCoord)*s, 0.0d, 0.0d);
		tess.addVertexWithUV(( u.xCoord-d.xCoord)*s, ( u.yCoord-d.yCoord)*s, ( u.zCoord-d.zCoord)*s, 1.0d, 0.0d);
		tess.addVertexWithUV((-u.xCoord+d.xCoord)*s, (-u.yCoord+d.yCoord)*s, (-u.zCoord+d.zCoord)*s, 0.0d, 1.0d);
		tess.addVertexWithUV((-u.xCoord-d.xCoord)*s, (-u.yCoord-d.yCoord)*s, (-u.zCoord-d.zCoord)*s, 1.0d, 1.0d);
		tess.draw();
		// roll axis
		s = 1.0f;
		GL11.glColor4f(0.0F, 0.0F, 1.0F, 1.0F);                       
		tess.startDrawing(GL11.GL_TRIANGLE_STRIP);              
	    tess.addVertexWithUV(( u.xCoord+p.xCoord)*s, ( u.yCoord+p.yCoord)*s, ( u.zCoord+p.zCoord)*s, 0.0d, 0.0d);
		tess.addVertexWithUV(( u.xCoord-p.xCoord)*s, ( u.yCoord-p.yCoord)*s, ( u.zCoord-p.zCoord)*s, 1.0d, 0.0d);
		tess.addVertexWithUV((-u.xCoord+p.xCoord)*s, (-u.yCoord+p.yCoord)*s, (-u.zCoord+p.zCoord)*s, 0.0d, 1.0d);
		tess.addVertexWithUV((-u.xCoord-p.xCoord)*s, (-u.yCoord-p.yCoord)*s, (-u.zCoord-p.zCoord)*s, 1.0d, 1.0d);
		tess.draw();                            
	}

	@Override
	public void renderTileEntityAt(TileEntity t, double x, double y, double z, float f)
	{
		renderTileEntityAt((Wrap_TileEntityRail)t,x,y,z,f);
	}
}
