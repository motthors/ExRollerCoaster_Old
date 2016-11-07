package erc.renderer;

import erc.entity.entitySUSHI;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class renderEntitySUSIHI extends Render {
	
	protected boolean canBePushed = true;
	
	public void doRender(entitySUSHI Coaster, double x, double y, double z, float f, float p_76986_9_)
	{
		Coaster.render(x, y, z, p_76986_9_);
	}
	
	@Override
	public void doRender(Entity entity, double x, double y, double z, float f, float p_76986_9_) {
		doRender((entitySUSHI)entity, x, y, z, f, p_76986_9_);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity e) {
		// TODO Auto-generated method stub
		return null;
	}

}
