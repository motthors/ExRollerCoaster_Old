package erc.gui.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

public class DefContainer extends Container {

    int xCoord, yCoord, zCoord;
    TileEntity tile;
    
    public DefContainer(int x, int y, int z, TileEntity tile) {
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
        this.tile = tile;
    }
 
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        //もし、ブロックとの位置関係でGUI制御したいなら、こちらを使う
//        return player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64D;
        return true;
    }
}
