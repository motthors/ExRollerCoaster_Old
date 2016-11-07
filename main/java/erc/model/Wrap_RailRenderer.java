package erc.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Vec3;

public abstract class Wrap_RailRenderer extends ModelBase{
	
	public abstract void setModelNum(int PosNum_org); // ƒŒ[ƒ‹\¬‚ÌÅ‰‚É1‰ñŒÄ‚Ô
	public abstract void construct(int idx, Vec3 Pos, Vec3 Dir, Vec3 Cross, float exParam);
	public abstract void render(Tessellator tess);
}
