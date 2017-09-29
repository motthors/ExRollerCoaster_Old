package erc.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erc.entity.ERC_EntityCoaster;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class ERC_RenderEntityCoaster extends Render {
	
	protected boolean canBePushed = true;
	
	public void doRender(ERC_EntityCoaster Coaster, double x, double y, double z, float f, float p_76986_9_)
	{
		if(Coaster.getModelRenderer()==null)return;
		Coaster.getModelRenderer().render((ERC_EntityCoaster)Coaster, x, y, z, p_76986_9_);
//		Entity[] ea = Coaster.getParts();
//		for(int i=0; i<ea.length; i++)
//		{
//			renderOffsetAABB(ea[i].boundingBox.getOffsetBoundingBox(-Coaster.posX, -Coaster.posY, -Coaster.posZ), x, y, z);
//		}
	}
	
	@Override
	public void doRender(Entity entity, double x, double y, double z, float f, float p_76986_9_) {
		doRender((ERC_EntityCoaster)entity, x, y, z, f, p_76986_9_);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		// TODO Auto-generated method stub
		return null;
	}


}
