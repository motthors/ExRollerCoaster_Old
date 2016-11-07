package erc.entity;

import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erc._core.ERC_Logger;
import erc.item.itemSUSHI;
import erc.manager.ERC_CoasterAndRailManager;
import erc.manager.ERC_ModelLoadManager.ModelOptions;
import erc.math.ERC_MathHelper;
import erc.message.ERC_MessageCoasterMisc;
import erc.message.ERC_PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ERC_EntityCoasterSeat extends Wrap_EntityCoaster{

	public ERC_EntityCoaster parent;
	private int UpdatePacketCounter = 4;
	boolean canRide = true;
	public boolean updateFlag = false;
	public boolean waitUpdateRiderFlag = false;
	//part of Options -> datawatcher
//	int seatIndex = -1;
//	public float offsetX;
//	public float offsetY;
//	public float offsetZ;
//	public float rotX;
//	public float rotY;
//	public float rotZ;
	
	public ERC_EntityCoasterSeat(World world) {
		super(world);
//		forceSpawn = true;
		setSize(1.1f, 0.8f);
//		ERC_ManagerPrevTickCoasterSeatSetPos.addSeat(this);
	}
	
	public ERC_EntityCoasterSeat(World world, ERC_EntityCoaster parent, int i) {
		this(world);
		this.parent = parent;
		setSeatIndex(i);
//		spawnControl = true;
//		ERC_Logger.info("***seat create, x:"+posX+", y:"+posY+", z:"+posZ);
	}
	
	public void setOptions(ModelOptions op, int idx)
	{
		if(op==null)return;
		if(op.offsetX==null)return;
		if(op.offsetX.length <= idx)return;
		setSize(op.size[idx], op.size[idx]);
		if(worldObj.isRemote)return;
		setOffsetX(op.offsetX[idx]);
		setOffsetY(op.offsetY[idx]);
		setOffsetZ(op.offsetZ[idx]);  
		setRotX(op.rotX[idx]);
		setRotY(op.rotY[idx]);
		setRotZ(op.rotZ[idx]);
		canRide = op.canRide;
	}

	@Override
	protected void entityInit()
	{
		dataWatcher.addObject(21, new Integer(-1));	// seatIndex
		dataWatcher.addObject(22, new Float(0f));	// offsetX
		dataWatcher.addObject(23, new Float(0f));	// offsetY
		dataWatcher.addObject(24, new Float(0f));	// offsetZ
		dataWatcher.addObject(25, new Float(0f));	// rotX
		dataWatcher.addObject(26, new Float(0f));	// rotY
		dataWatcher.addObject(27, new Float(0f));	// rotZ
	}
	
    protected void setSize(float w, float h)
    {
//    	w*=10.0;h*=10.0;
        if (w != this.width || h != this.height)
        {
            this.width = w;// + 40f;
            this.height = h;
    		this.boundingBox.minX = -w/2 + this.posX;
    		this.boundingBox.minY = +h/2 + this.posY;
    		this.boundingBox.minZ = -w/2 + this.posZ;
    		this.boundingBox.maxX = +w/2 + this.posX; 
    		this.boundingBox.maxY = +h/2 + this.posY;
    		this.boundingBox.maxZ = +w/2 + this.posZ;
        }
        this.myEntitySize = Entity.EnumEntitySize.SIZE_2;
    }
    
	public boolean canBeCollidedWith()
    {
        return true;
    }
	public AxisAlignedBB getBoundingBox()
    {
        return boundingBox;
    }
	
    public boolean attackEntityFrom(DamageSource ds, float p_70097_2_)
    {
    	if(parent==null)return true;
    	return parent.attackEntityFrom(ds, p_70097_2_);
//    	parent.setDead();
//    	this.setPosition(posX, posY, posZ);
//    	return true;
    }
    
	public boolean canBeRidden()
    {
		if(worldObj.isRemote)return false;
        return canRide; // true : 乗れる
    }
	
    // 右クリックされたらくる
    public boolean interactFirst(EntityPlayer player)
    {
    	if(parent==null)return true;
    	if(parent.requestConnectCoaster(player))return true;
    	if(isRiddenSUSHI(player))return true;
    	if(requestRidingMob(player))return true;
    	if(!canBeRidden())return true;
    	
    	//何か乗ってる　＋　プレイヤーが座ってる　＋　右クリックしたプレイヤーと違うプレイヤーが座ってる
        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != player)
        {
            return true;
        }
        //何かが乗ってる　＋　右クリックしたプレイヤー以外の何かが乗ってる
        else if (this.riddenByEntity != null && this.riddenByEntity != player)
        {
        	//おろす
        	riddenByEntity.mountEntity((Entity)null);
        	riddenByEntity = null;
            return true;
        }
        //何かが乗ってる　自分かもしれない
        else if (this.riddenByEntity != null)
        {
        	return true;
        }
        else
        {
            if (!this.worldObj.isRemote)
            {
            	ERC_CoasterAndRailManager.resetViewAngles();
                player.mountEntity(this);
            }
            return true;
        }
    }
		
    protected boolean isRiddenSUSHI(EntityPlayer player)
	{
		if(player.getHeldItem()==null)return false;
		if(player.getHeldItem().getItem() instanceof itemSUSHI)
		{
			if(!worldObj.isRemote)
			{
				entitySUSHI e = new entitySUSHI(worldObj,posX,posY,posZ);
				worldObj.spawnEntityInWorld(e);	
				e.mountEntity(this);
				if(!player.capabilities.isCreativeMode)--player.getHeldItem().stackSize;
			}
			player.swingItem();
			return true;
		}
		return false;
	}
	
	protected boolean requestRidingMob(EntityPlayer player)
	{
		if(worldObj.isRemote)return false;
		ItemStack is = player.getHeldItem();
		if(is==null)return false;
		if(is.getItem() instanceof ItemMonsterPlacer)
		{
			Entity entity = ItemMonsterPlacer.spawnCreature(worldObj, is.getItemDamage(), posX, posY, posZ);
			entity.mountEntity(this);
			if (!player.capabilities.isCreativeMode)--is.stackSize;
			player.swingItem();
			return true;
		}
		if(is.getItem() instanceof ItemLead)
		{
	        double d0 = 7.0D;
			@SuppressWarnings("unchecked")
			List<EntityLiving> list = worldObj.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getBoundingBox(posX-d0, posY-d0, posZ-d0, posX+d0, posY+d0, posZ+d0));
	        if (list != null)
	        {
	            Iterator<EntityLiving> iterator = list.iterator();
	            while (iterator.hasNext())
	            {
	                EntityLiving entityliving = iterator.next();

	                if (entityliving.getLeashed() && entityliving.getLeashedToEntity() == player)
	                {
	                	entityliving.mountEntity(this);
	                    entityliving.clearLeashed(true, !player.capabilities.isCreativeMode);
	                    player.swingItem();
	                    return true;
	                }
	            }
	        }
		}
		return false;
	}
	
	@Override
	public void setDead() {
//		ERC_Logger.debugInfo("seat is dead ... id:"+this.getEntityId());
		super.setDead();
	}
	
	@Override
	public void onUpdate() 
	{
//		if(worldObj.isRemote)ERC_Logger.debugInfo("end seat onUpdate");
//		setDead();
		if(updateInit())return;
		if(updateFlag==parent.updateFlag)
		{
//			ERC_Logger.debugInfo("seat stay");
		} 	// ２．待機
		else
		{
//			ERC_Logger.debugInfo("seat update");
			_onUpdate();						// ６．親より後だから更新する
		}
		
		updateFlag = !updateFlag;
	}

	public void _onUpdate() 
	{
//		if(worldObj.isRemote)ERC_Logger.debugInfo("end seat _onUpdate");
//		if(updateInit())return;
		syncToClient();
		savePrevData();
		double ox = getOffsetX();
		double oy = getOffsetY();
		double oz = getOffsetZ();
		this.setPosition(
				parent.posX + parent.ERCPosMat.offsetX.xCoord*ox + parent.ERCPosMat.offsetY.xCoord*oy + parent.ERCPosMat.offsetZ.xCoord*oz, 
				parent.posY + parent.ERCPosMat.offsetX.yCoord*ox + parent.ERCPosMat.offsetY.yCoord*oy + parent.ERCPosMat.offsetZ.yCoord*oz, 
				parent.posZ + parent.ERCPosMat.offsetX.zCoord*ox + parent.ERCPosMat.offsetY.zCoord*oy + parent.ERCPosMat.offsetZ.zCoord*oz);

		if(waitUpdateRiderFlag)updateRiderPosition2();
	}

	protected void syncToClient()
	{
		if(this.UpdatePacketCounter--<=0)
		{
			UpdatePacketCounter = 40;
			if(!worldObj.isRemote)
			{
				if(parent!=null)
				{
					ERC_MessageCoasterMisc packet = new ERC_MessageCoasterMisc(this,4);
					ERC_PacketHandler.INSTANCE.sendToAll(packet);
//					parent.resetSeat(getSeatIndex(), this);
//					ERC_Logger.info("Server teach client parentid");
				}
				else
				{
//					if(parent.resetSeat(getSeatIndex(), this))
//						setDead();
				}
			}
			else // client
			{
//				parent.resetSeat(getSeatIndex(), this);
			}
		}
	}
	
	protected boolean updateInit()
	{
		if(parent==null)
		{
			if(!worldObj.isRemote)
			{
//				if(searchParent())return false;
//				else 
				ERC_Logger.debugInfo("seat log : parent is null.");
				if(!isDead)setDead();
			}
			return true; 
		}
		if(parent.isDead)
		{
			if(!worldObj.isRemote)if(!isDead)setDead();
			return true;
		}
		if(!worldObj.isRemote && getSeatIndex() == -1) // リログなどの再スポーンで親が再スポーンさせなかった場合false(serverのみ)
		{
			if(!isDead)setDead();
			return true;
		}
		return false;
	}
	
	protected void savePrevData()
    {
    	this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;             
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;       
        this.prevRotationRoll = this.rotationRoll;
    }
	
	public boolean searchParent()
	{
		return false;
//		double x = posX - getOffsetX();
//		double y = posY - getOffsetY();
//		double z = posZ - getOffsetZ();
//		double s = Math.max(Math.abs(getOffsetX()), Math.abs(getOffsetY()));
//		s = Math.max(s, Math.abs(getOffsetZ()));
//		@SuppressWarnings("unchecked")
//		List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(this, AxisAlignedBB.getBoundingBox(x-s, y-s, z-s, x+s, y+s, z+s));
//		for(Entity e : list)
//		{
//			if(e instanceof ERC_EntityCoaster)
//			{
//				parent = (ERC_EntityCoaster) e;
//				parent.resetSeat(getSeatIndex(), this);
//				return true;
//			}
//		}
//		return false;
	}
	
	public double getMountedYOffset()
    {
        return (double)this.height * 0.4;
    }
	
	@Override
	public void updateRiderPosition()
	{
		if(parent == null)return;
		if(updateFlag!=parent.updateFlag)
		{
//			ERC_Logger.debugInfo("seat rider stay");
			waitUpdateRiderFlag = true;
		} 	// ２．待機
		else
		{
//			ERC_Logger.debugInfo("seat rider update");
			updateRiderPosition2();					
			// ６．親より後だから更新する
		}
	}
	public void updateRiderPosition2()
	{
//		updateRiderPosFlag = true;
//		ERC_Logger.info("entityseat::updateRilderPosition");
		if(parent==null)return;
    	if (this.riddenByEntity != null)
        {
    		waitUpdateRiderFlag = false;
    		// 基準軸回転
//    		if(worldObj.isRemote)ERC_Logger.debugInfo("seat updateRiderposition");
    		Vec3 vx = parent.ERCPosMat.offsetX;
    		Vec3 vy = parent.ERCPosMat.offsetY;
    		Vec3 vz = parent.ERCPosMat.offsetZ;
    		// Z軸回転
    		vx = ERC_MathHelper.rotateAroundVector(vx, vz, getRotZ());
    		vy = ERC_MathHelper.rotateAroundVector(vy, vz, getRotZ());
    		// Y軸回転
    		vx = ERC_MathHelper.rotateAroundVector(vx, parent.ERCPosMat.offsetY, getRotY());
    		vz = ERC_MathHelper.rotateAroundVector(vz, parent.ERCPosMat.offsetY, getRotY());
    		// X軸回転
    		vy = ERC_MathHelper.rotateAroundVector(vy, parent.ERCPosMat.offsetX, getRotX());
    		vz = ERC_MathHelper.rotateAroundVector(vz, parent.ERCPosMat.offsetX, getRotX());
    		{
    			////////////// プレイヤー回転量計算
    			// ViewYaw回転ベクトル　dir1->dir_rotView, cross->turnCross
    			Vec3 dir_rotView = ERC_MathHelper.rotateAroundVector(vz, vy, Math.toRadians(ERC_CoasterAndRailManager.rotationViewYaw));
    			Vec3 turnCross = ERC_MathHelper.rotateAroundVector(vx, vy, Math.toRadians(ERC_CoasterAndRailManager.rotationViewYaw));
    			// ViewPitch回転ベクトル dir1->dir_rotView
    			Vec3 dir_rotViewPitch = ERC_MathHelper.rotateAroundVector(dir_rotView, turnCross, Math.toRadians(ERC_CoasterAndRailManager.rotationViewPitch));
    			// pitch用 dir_rotViewPitchの水平ベクトル
    			Vec3 dir_rotViewPitchHorz = Vec3.createVectorHelper(dir_rotViewPitch.xCoord, 0, dir_rotViewPitch.zCoord);
    			// roll用turnCrossの水平ベクトル
    			Vec3 crossHorzFix = Vec3.createVectorHelper(0, 1, 0).crossProduct(dir_rotViewPitch);
    			if(crossHorzFix.lengthVector()==0.0)crossHorzFix=Vec3.createVectorHelper(1, 0, 0);
		
    			// yaw OK
    			 rotationYaw = (float) -Math.toDegrees( Math.atan2(dir_rotViewPitch.xCoord, dir_rotViewPitch.zCoord) );

    			// pitch OK
    			rotationPitch = (float) Math.toDegrees( ERC_MathHelper.angleTwoVec3(dir_rotViewPitch, dir_rotViewPitchHorz) * (dir_rotViewPitch.yCoord>=0?-1f:1f) );
    			if(Float.isNaN(rotationPitch))
    				rotationPitch=0;
    			
    			// roll
    			rotationRoll = (float) Math.toDegrees( ERC_MathHelper.angleTwoVec3(turnCross, crossHorzFix) * (turnCross.yCoord>=0?1f:-1f) );
    			if(Float.isNaN(rotationRoll))
    				rotationRoll=0;
    		}
    		prevRotationYaw = ERC_MathHelper.fixrot(rotationYaw, prevRotationYaw);
    		prevRotationPitch = ERC_MathHelper.fixrot(rotationPitch, prevRotationPitch);
    		prevRotationRoll = ERC_MathHelper.fixrot(rotationRoll, prevRotationRoll);
          
    		this.riddenByEntity.rotationYaw = this.rotationYaw;
    		this.riddenByEntity.rotationPitch = this.rotationPitch;
    		this.riddenByEntity.prevRotationYaw = this.prevRotationYaw;
    		this.riddenByEntity.prevRotationPitch = this.prevRotationPitch; 
//    		this.riddenByEntity.rotationYaw = 0;
//    		this.riddenByEntity.rotationPitch = -ERC_CoasterAndRailManager.rotationViewPitch;
    		
    		double toffsety = this.riddenByEntity.getYOffset();
//            this.riddenByEntity.setPosition(
//            		this.posX + vy.xCoord*toffsety, 
//            		this.posY + vy.yCoord*toffsety,
//            		this.posZ + vy.zCoord*toffsety
//            		);
//    		double ox = getOffsetX();
//    		double oy = getOffsetY();
//    		double oz = getOffsetZ();
//            this.riddenByEntity.setPosition(
//    				parent.posX + vy.xCoord*toffsety + parent.ERCPosMat.offsetX.xCoord*ox + parent.ERCPosMat.offsetY.xCoord*oy + parent.ERCPosMat.offsetZ.xCoord*oz, 
//    				parent.posY + vy.yCoord*toffsety + parent.ERCPosMat.offsetX.yCoord*ox + parent.ERCPosMat.offsetY.yCoord*oy + parent.ERCPosMat.offsetZ.yCoord*oz, 
//    				parent.posZ + vy.zCoord*toffsety + parent.ERCPosMat.offsetX.zCoord*ox + parent.ERCPosMat.offsetY.zCoord*oy + parent.ERCPosMat.offsetZ.zCoord*oz);
            this.riddenByEntity.setPosition(
    				this.posX + vy.xCoord*toffsety, 
    				this.posY + vy.yCoord*toffsety, 
    				this.posZ + vy.zCoord*toffsety);
            
            this.riddenByEntity.motionX = this.parent.Speed * parent.ERCPosMat.Dir.xCoord * 1;
            this.riddenByEntity.motionY = this.parent.Speed * parent.ERCPosMat.Dir.yCoord * 1;
            this.riddenByEntity.motionZ = this.parent.Speed * parent.ERCPosMat.Dir.zCoord * 1;
            ERC_Logger.info("" + riddenByEntity.motionX + riddenByEntity.motionY + riddenByEntity.motionZ );
			
            if(worldObj.isRemote && riddenByEntity instanceof EntityLivingBase)
            {
            	EntityLivingBase el = (EntityLivingBase) this.riddenByEntity;
            	el.renderYawOffset = parent.ERCPosMat.yaw; 
            	if(riddenByEntity == Minecraft.getMinecraft().thePlayer)
            		el.rotationYawHead = ERC_CoasterAndRailManager.rotationViewYaw + el.renderYawOffset;
//            	el.head
            }
            
        }
//    	ERC_CoasterAndRailManager.setRotRoll(rotationRoll, prevRotationRoll);
	}        
	  
    @SideOnly(Side.CLIENT)
    public void setAngles(float deltax, float deltay)
    {
//    	ERC_CoasterAndRailManager.setAngles(deltax, deltay);
    }
    
	@Override
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pit, int p_70056_9_)
    {
    	//仕様として何も無し　サーバーからの規定のEntity同期で使われており、同期を無効にするため
//		ERC_Logger.debugInfo("catch!");
//		super.setPositionAndRotation2(x, y, z, yaw, pit, p_70056_9_);
    }
	
//	public float getRoll(float partialTicks)
//	{
//		return offsetRot + parent.prevRotationRoll + (parent.rotationRoll - parent.prevRotationRoll)*partialTicks;
//	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt)
	{
		setSeatIndex(nbt.getInteger("seatindex"));
		setOffsetX(nbt.getFloat("seatoffsetx"));
		setOffsetY(nbt.getFloat("seatoffsety"));
		setOffsetZ(nbt.getFloat("seatoffsetz"));
		setRotX(nbt.getFloat("seatrotx"));   
		setRotY(nbt.getFloat("seatroty"));   
		setRotZ(nbt.getFloat("seatrotz"));   
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) 
	{
		nbt.setInteger("seatindex", getSeatIndex());
		nbt.setFloat("seatoffsetx", getOffsetX());
		nbt.setFloat("seatoffsety", getOffsetY());
		nbt.setFloat("seatoffsetz", getOffsetZ());
		nbt.setFloat("seatrotx", getRotX());   
		nbt.setFloat("seatroty", getRotY());   
		nbt.setFloat("seatrotz", getRotZ());   
	}


	public void SyncCoasterMisc_Send(ByteBuf buf, int flag)
	{
		switch(flag)
		{
		case 3 : //CtS 申請
			break;
		case 4 : //StC 親をクラに教える
			buf.writeInt(parent.getEntityId());
			break;
		}
	}
	public void SyncCoasterMisc_Receive(ByteBuf buf, int flag)
	{
		switch(flag)
		{
		case 3:
			ERC_MessageCoasterMisc packet = new ERC_MessageCoasterMisc(this,4);
			ERC_PacketHandler.INSTANCE.sendToAll(packet);
//			ERC_Logger.info("server repost parentID to client");
			break;
		case 4 :
			int parentid = buf.readInt();
			parent = (ERC_EntityCoaster) worldObj.getEntityByID(parentid);
			if(parent==null){
				ERC_Logger.warn("parent id is Invalid.  id:"+parentid);
				return;
			}
			parent.addSeat(this, getSeatIndex());
//			ERC_Logger.info("client get parent");
			return;
		}
	}
	
	////////////////////////////////////////datawatcher
	public int getSeatIndex()
	{
		return dataWatcher.getWatchableObjectInt(21);
	}
	public void setSeatIndex(int idx)
	{
		dataWatcher.updateObject(21, Integer.valueOf(idx));
	}
	
	public float getOffsetX()
	{
		return dataWatcher.getWatchableObjectFloat(22);
	}
	public void setOffsetX(float offsetx)
	{
		dataWatcher.updateObject(22, Float.valueOf(offsetx));
	}

	public float getOffsetY()
	{
		return dataWatcher.getWatchableObjectFloat(23);
	}
	public void setOffsetY(float offsety)
	{
		dataWatcher.updateObject(23, Float.valueOf(offsety));
	}
	
	public float getOffsetZ()
	{
		return dataWatcher.getWatchableObjectFloat(24);
	}
	public void setOffsetZ(float offsetz)
	{
		dataWatcher.updateObject(24, Float.valueOf(offsetz));
	}
	
	public float getRotX()
	{
		return dataWatcher.getWatchableObjectFloat(25);
	}
	public void setRotX(float rot)
	{
		dataWatcher.updateObject(25, Float.valueOf(rot));
	}
	
	public float getRotY()
	{
		return dataWatcher.getWatchableObjectFloat(26);
	}
	public void setRotY(float rot)
	{
		dataWatcher.updateObject(26, Float.valueOf(rot));
	}
	
	public float getRotZ()
	{
		return dataWatcher.getWatchableObjectFloat(27);
	}
	public void setRotZ(float rot)
	{
		dataWatcher.updateObject(27, Float.valueOf(rot));
	}
}
