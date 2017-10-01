package erc.tileEntity;

import erc._core.ERC_ReturnCoasterRot;
import erc.entity.ERC_EntityCoaster;
import net.minecraft.util.ResourceLocation;

/**
 * Created by MOTTY on 2017/09/30.
 */
public class TileEntityNonGravityRail extends TileEntityRailBase {

    public TileEntityNonGravityRail()
    {
        super();
        RailTexture = new ResourceLocation("textures/blocks/portal.png");
    }

    @Override
    public void SpecialRailProcessing(ERC_EntityCoaster EntityCoaster)
    {

    }

    @Override
    public double CalcRailPosition2(float t, ERC_ReturnCoasterRot ret, float viewyaw, float viewpitch, boolean riddenflag)
    {
        super.CalcRailPosition2(t, ret, viewyaw, viewpitch, riddenflag);
        return 0;
    }

}
