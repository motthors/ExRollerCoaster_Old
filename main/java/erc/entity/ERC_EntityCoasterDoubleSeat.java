package erc.entity;

import erc.tileEntity.TileEntityRailBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ERC_EntityCoasterDoubleSeat extends ERC_EntityCoaster{

	ERC_EntityCoasterSeat secondseat;

	public ERC_EntityCoasterDoubleSeat(World world)
	{
		super(world);
		this.setSize(1.4f, 0.6f);
		this.yOffset = (this.height / 2.0F) - 0.3F;
	}
	
	public ERC_EntityCoasterDoubleSeat(World world, TileEntityRailBase tile, double x, double y, double z) {
		super(world, tile, x, y, z);
	}
	
	@Override
	public boolean canBeRidden()
    {
        return true; // true : 乗れる
    }
	
	// 右クリックで乗る処理っぽい
    public boolean interactFirst(EntityPlayer player)
    {
    	if(!canBeRidden())return false;
    	Entity THIS = this;
    	Entity riddenbyentity = this.riddenByEntity;
    	if(riddenbyentity != null)
    	{
    		THIS = secondseat;
    		riddenbyentity = secondseat.riddenByEntity;
    	}
    	
    	//　ｺｰｽﾀｰに何か乗ってる　　　　　　　　　　　　　　　　　　プレイヤーの誰かが乗っている　　　　　　　　　　　　　　　　　　　　乗り物をたたいたのは乗っている人じゃない
        if (riddenbyentity != null && riddenbyentity instanceof EntityPlayer && riddenbyentity != player)
        {
            return true; 
        }
        //それでなくても	何か乗ってて				叩いたプレイヤーじゃないなにかが乗っていたら
        else if (riddenbyentity != null && riddenbyentity != player)
        {
            return false; 
        }
        else
        {
            if (!this.worldObj.isRemote)
            {
                player.mountEntity(THIS); // 乗せる
            }

            return true;
        }
    }
    
    @Override
    public void onUpdate()
    {
    	super.onUpdate();
    }

	@Override
	public Entity[] getParts() {
		// ここから返すEntityも全て登録される　セカンドシート登録もアリか？
		Entity[] ret = new Entity[1];
		ret[0] = secondseat = new ERC_EntityCoasterSeat(worldObj);
		return ret;
	}
    
    
}
