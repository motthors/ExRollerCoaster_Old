package erc.block;

import erc.tileEntity.TileEntityNonGravityRail;
import erc.tileEntity.Wrap_TileEntityRail;

/**
 * Created by MOTTY on 2017/09/30.
 */
public class BlockNonGravityRail extends blockRailBase {

    @Override
    public Wrap_TileEntityRail getTileEntityInstance() {
        return new TileEntityNonGravityRail();
    }


}
