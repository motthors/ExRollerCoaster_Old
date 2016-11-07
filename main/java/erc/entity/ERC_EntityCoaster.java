package erc.entity;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erc._core.ERC_Core;
import erc._core.ERC_Logger;
import erc._core.ERC_ReturnCoasterRot;
import erc.item.ERC_ItemCoasterConnector;
import erc.manager.ERC_CoasterAndRailManager;
import erc.manager.ERC_ManagerCoasterLoad;
import erc.manager.ERC_ModelLoadManager;
import erc.manager.ERC_ModelLoadManager.ModelOptions;
import erc.message.ERC_MessageCoasterMisc;
import erc.message.ERC_MessageCoasterStC;
import erc.message.ERC_PacketHandler;
import erc.model.ERC_ModelCoaster;
import erc.tileEntity.TileEntityRailBase;
import erc.tileEntity.TileEntityRailBranch2;
import erc.tileEntity.Wrap_TileEntityRail;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;


public class ERC_EntityCoaster extends Wrap_EntityCoaster{

	protected String entityName;
	protected TileEntityRailBase tlrail;

	// モデル描画用パラメータ
    protected int CoasterType = -1;
    protected int ModelID = -1;
	protected ERC_ModelCoaster modelCoaster;
	public ERC_ModelCoaster getModelRenderer(){return modelCoaster;}
    
    private int savex;
    private int savey;
    private int savez;
    
    public int connectNum;
    
    public ERC_ReturnCoasterRot ERCPosMat;
    public float paramT;
    public double Speed;
	public double sumSpeed;
//    public float rotationViewYaw;
//    public float prevRotationViewYaw;
//    public float rotationViewPitch;
//    public float prevRotationViewPitch;
    // サーバーのパケット送信カウント
    protected int UpdatePacketCounter = 5;
    // Tick初回更新用カウンタ保存
    protected int prevTickCount;
    
//    public float coasterLength; //連結コースターの後ろそらし距離
    
    public ERC_ModelLoadManager.ModelOptions CoasterOptions; // コースターのオプション
    public int seatsNum = -1;
    protected ERC_EntityCoasterSeat[] seats;
    public boolean updateFlag = false;
//    public Entity[] riddenByEntities = new Entity[1];

    // 連結用パラメータ
    private List<ERC_EntityCoasterConnector> connectCoasterList = new ArrayList<ERC_EntityCoasterConnector>();
    
//    public AxisAlignedBB collisionAABB;
    
	public ERC_EntityCoaster(World world) 
	{
		super(world);
		CoasterOptions = new ModelOptions();
		this.preventEntitySpawning = true;
		this.isCollidedHorizontally = false;
		this.isCollidedVertically = false;
		this.isCollided = false;

		this.setSize(1.7f, 0.4f);
		renderDistanceWeight = 1000f;
		this.yOffset = 0.0f;//(this.height / 2.0F) - 0.3F;
		prevRotationRoll = rotationRoll = 0f;
        this.rotationYaw = this.prevRotationYaw = 0.0f;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        
		ERCPosMat = new ERC_ReturnCoasterRot();
		Speed = 0;
		paramT = 0;
		savex = -1;
		savey = -1;
		savez = -1;
		
		connectNum = 0;
		prevTickCount = -1;
		
        connectCoasterList.clear();
        
        CoasterType = 0;
        seats = null;
        seatsNum = -1;
        
		if(worldObj.isRemote)
		{
			ERC_ManagerCoasterLoad.registerParentCoaster(this);
			if(ERC_CoasterAndRailManager.coastersetY > -1)
			{
				int x = ERC_CoasterAndRailManager.coastersetX;
				int y = ERC_CoasterAndRailManager.coastersetY;
				int z = ERC_CoasterAndRailManager.coastersetZ;
				Wrap_TileEntityRail rail = (Wrap_TileEntityRail) worldObj.getTileEntity(x, y, z);
				if(rail==null){
					ERC_Logger.warn("ERC_EntityCoaster.constractor : can't get rail... xyz:"+x+"."+y+"."+z);
					return;
				}
				tlrail = rail.getRail();
				this.setPosition(x+0.5, y+1.0, z+0.5);
			}
			
			if(ERC_CoasterAndRailManager.saveModelID > -1)
			{
				setModel(ERC_CoasterAndRailManager.saveModelID, CoasterType);
				setModelOptions();
				ERC_CoasterAndRailManager.saveModelID = -1;
			}
		}
//		ERC_Logger.info("EntityCoaster:construct ... modelID:"+ModelID+", CoasterType:"+CoasterType);
//		ERC_Logger.info("EntityCoaster:construct ... pos init2  :"+posX+"."+posY+"."+posZ);
//		ERC_Logger.info("EntityCoaster:construct ... aabb  :"+boundingBox.toString()+", width:"+width);
	}
	
    public ERC_EntityCoaster(World world, TileEntityRailBase rail, double x, double y, double z)
    {
        this(world);
        this.tlrail = rail;
        
        this.setPosition(x, y, z);
		this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        
//        ERC_Logger.info("EntityCoaster:construct ... pos init   :"+posX+"."+posY+"."+posZ);
//        ERC_Logger.info("EntityCoaster:construct2 ... aabb  :"+boundingBox.toString()+", width:"+width);
    }
    
	@Override
    protected void entityInit()
    {
//        this.dataWatcher.addObject(17, new Integer(0));
//        this.dataWatcher.addObject(18, new Integer(1));
//        this.dataWatcher.addObject(19, new Float(0.0F));
//        this.dataWatcher.addObject(20, new Integer(0));
//        this.dataWatcher.addObject(21, new Integer(6));
//        this.dataWatcher.addObject(22, Byte.valueOf((byte)0));
    }

	// return true if kill coaster
	protected boolean checkTileEntity()
	{
		TileEntity tile = worldObj.getTileEntity(savex, savey, savez);
		if(!(tile instanceof Wrap_TileEntityRail))
		{
			this.killCoaster();
			return true;
		}
		tlrail = ((Wrap_TileEntityRail) tile).getRail();
		savex = savey = savez = -1;
		return false;
	}
	
    protected void setSize(float w, float h)
    {
//    	w*=10.0;h*=10.0;
        if (w != this.width || h != this.height)
        {
            this.width = w;// + 40f;
            this.height = h;
//            this.boundingBox.maxX = this.boundingBox.minX + (double)this.width;
//            this.boundingBox.maxZ = this.boundingBox.minZ + (double)this.width;
//            this.boundingBox.maxY = this.boundingBox.minY + (double)this.height;
    		this.boundingBox.minX = -w/2 + this.posX;
    		this.boundingBox.minY = +h/2 + this.posY;
    		this.boundingBox.minZ = -w/2 + this.posZ;
    		this.boundingBox.maxX = +w/2 + this.posX; 
    		this.boundingBox.maxY = +h/2 + this.posY;
    		this.boundingBox.maxZ = +w/2 + this.posZ;
    		
//            if (this.width > f2 && !this.worldObj.isRemote)
//            {
//                this.moveEntity((double)(f2 - this.width), 0.0D, (double)(f2 - this.width));
//            }
        }

        this.myEntitySize = Entity.EnumEntitySize.SIZE_6;
        
    }
    
    public AxisAlignedBB getCollisionBox(Entity entity)
    {
//        return entity.boundingBox;
    	return null;
    }
	
    public AxisAlignedBB getBoundingBox()
    {
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float p_70070_1_)
    {
        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.posZ);

        if (this.worldObj.blockExists(i, 0, j))
        {
//            double d0 = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66D;
            int k = MathHelper.floor_double(this.posY/* - (double)this.yOffset + d0*/);
            return this.worldObj.getLightBrightnessForSkyBlocks(i, k, j, 0);
        }
        else
        {
            return 0;
        }
    }

    @Override
	public boolean isInRangeToRenderDist(double p_70112_1_)
    {
		return true;
	}

	public Item getItem()
    {
    	return ERC_Core.ItemCoaster;
    }
    
	public int getModelID()
	{
		return ModelID;
	}
	
	public void setModelOptions(int modelID, ModelOptions op)
	{
		if(this.ModelID == modelID)return;
		setModel(modelID, CoasterType);
		setModelOptions(op);
	}
	
	public void setModel(int id, int ct)
	{
		ModelID = id;
		if(!worldObj.isRemote)return;
		if(id < 0)return;
//		ERC_Logger.info("EntityCoaster:setModel ... set ModelID : "+id+" -> "+(id%ERC_ModelLoadManager.getModelPackNum(CoasterType))+" CoasterType:"+CoasterType);
		ModelID = ModelID%ERC_ModelLoadManager.getModelPackNum(CoasterType);
		modelCoaster = ERC_ModelLoadManager.getModel(ModelID, CoasterType);
	}

	protected void setModelOptions()
	{
		// 今のとこコースターと連結コースターのコンストラクタからのみ呼ばれてる　気になったらチェック
		ModelOptions op = ERC_ModelLoadManager.getModelOP(ModelID, CoasterType);
		if(op==null)return;
		setModelOptions(op);
	}
	protected void setModelOptions(ModelOptions op)
	{
		if(op.SeatNum<0)return;
		CoasterOptions = op;
		setSize(op.Width, op.Height);
		addSeats(op);
	}
	protected void addSeats(ModelOptions op)
	{
		seatsNum = op.SeatNum;
		
		if(!worldObj.isRemote)
		{
			if(seatsNum <= 0)return;
			if(seats==null)seats = new ERC_EntityCoasterSeat[seatsNum];
			else if(seats.length != seatsNum)seats = new ERC_EntityCoasterSeat[seatsNum];
			
			for(int i=0;i<seatsNum;++i)
			{
				seats[i] = new ERC_EntityCoasterSeat(worldObj, this, i);
				seats[i].setOptions(CoasterOptions, i);
				seats[i].setPosition(posX, posY, posZ);
				worldObj.spawnEntityInWorld(seats[i]);
//				seats[i]._onUpdate();
//				ERC_Logger.debugInfo(flag?"***********success":"*********************failed");
			}
		}
		else
		{
			if(seats==null)seats = new ERC_EntityCoasterSeat[seatsNum];
			else if(seats.length != seatsNum)seats = new ERC_EntityCoasterSeat[seatsNum];
			for(int i=0;i<seats.length;++i)
			{
				if(seats[i]==null)continue;
				seats[i].setOptions(CoasterOptions, i);
			}
		}
	}
	// クライアント側で自動生成されたEntityの捕獲
	protected void addSeat(ERC_EntityCoasterSeat seat, int index)
	{
		if(index < 0)return;
		if(seats == null)
		{
			ERC_Logger.warn("ERC_EntityCoaster::addSeat, seats is null");
			return;
		}
		
		else if(seats.length <= index)return;
		
		seats[index] = seat;
		seats[index].setOptions(CoasterOptions, index);
		seat.updateFlag = this.updateFlag;
//		ERC_Logger.debugInfo("EntityCaoster::addSeat... seatidx:"+index);
	}
	
	public boolean resetSeat(int idx, ERC_EntityCoasterSeat seat)
	{
		if(idx < 0 || idx >= seats.length)
		{
			ERC_Logger.warn("EntityCoaster::resetSeat : out of index");
//			seat.setDead();
			return true;
		}
		seats[idx] = seat;
		return false;
	}
	
//	public Entity[] getParts()
//	{
//		return this.seats;
//    }
	
//	@Override
//	protected void setSize(float p_70105_1_, float p_70105_2_){}
//	protected void setSize(float width, float height, float length)
//    {
////        float f2;
//
//        if (width != this.width || height != this.height || length != this.length)
//        {
////            f2 = this.width;
//            this.width = width;
//            this.height = height;
//            this.length = length;
//            this.boundingBox.minX = this.posX - (double)this.width/2f; 
//            this.boundingBox.minZ = this.posZ - (double)this.length/2f;
//            this.boundingBox.minY = this.posY ;
//            this.boundingBox.maxX = this.boundingBox.minX + (double)this.width;
//            this.boundingBox.maxZ = this.boundingBox.minZ + (double)this.length;
//            this.boundingBox.maxY = this.boundingBox.minY + (double)this.height;
//
//            ERC_Logger.info("pos        :"+posX+"."+posY+"."+posZ);
//            ERC_Logger.info("boundingMin:"+boundingBox.minX+"."+boundingBox.minY+"."+boundingBox.minZ);
//            ERC_Logger.info("boundingMax:"+boundingBox.maxX+"."+boundingBox.maxY+"."+boundingBox.maxZ);
//        }
//    }

	@Override
    public boolean canBePushed()
    {
        return false;
    }

    @Override
    public boolean canBeCollidedWith()
    {
//        return !this.isDead;
    	return false;
    }
    
	@Override
    protected boolean canTriggerWalking()
    {
		// このEntityが乗っているブロックのonEntityWalkingを呼ぶかどうか
        return false;
    }
	
	protected boolean canConnectForrowingCoaster()
	{
		return true;
	}
	
    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double getMountedYOffset()
    {
        return (double)this.height * 0.4;
    }
    
    // 右クリックされたらくる
    public boolean interactFirst(EntityPlayer player)
    {
    	if(requestConnectCoaster(player))return true;
//    	if(isRiddenSUSHI(player))return true;
//    	if(!canBeRidden())return true;
    	
//        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != player)
//        {
//            return true;
//        }
//        else if (this.riddenByEntity != null && this.riddenByEntity != player)
//        {
//            return true;
//        }
//        else
//        {
//            if (!this.worldObj.isRemote)
//            {
//            	
////                player.mountEntity(this);
////                mountSeats(player);
//            }
            return true;
//        }
    }
    
    // return true to prevent any further processing.
    protected boolean requestConnectCoaster(EntityPlayer player)
    {
    	// 連結コースターをプレイヤーが持ってなかったらだめ
    	if(player.getCurrentEquippedItem() == null ) return false;
		if( !(player.getCurrentEquippedItem().getItem() instanceof ERC_ItemCoasterConnector) ) return false;
		
		//クリックしたコースターが連結許可されてるのじゃないとだめ
		if( !canConnectForrowingCoaster() )return false;
		
		AnswerRequestConnect(player);
		return true;
    }
    
    protected void AnswerRequestConnect(EntityPlayer player)
    {
		// 以下許可し、他の右クリック動作はさせない
		if(worldObj.isRemote)
		{
			ERC_ItemCoasterConnector itemcc = (ERC_ItemCoasterConnector) player.getCurrentEquippedItem().getItem();
			itemcc.setCoaster(tlrail.xCoord, tlrail.yCoord, tlrail.zCoord, this.getEntityId());
			ERC_CoasterAndRailManager.client_setParentCoaster(this);
		}
		if(!player.capabilities.isCreativeMode)--player.getCurrentEquippedItem().stackSize;
		player.swingItem();
    }

	public void connectionCoaster(ERC_EntityCoasterConnector followCoaster)
	{
		if(worldObj.isRemote)
		{

		}
		followCoaster.parent = this;
//		if(!connectCoasterList.isEmpty())connectCoasterList.get(connectCoasterList.size()-1).setNextCoasterConnect(followCoaster);
		float length = this.CoasterOptions.Length;
		for(ERC_EntityCoasterConnector c : connectCoasterList)
		{
			length += c.CoasterOptions.Length;
		}
		if(followCoaster.tlrail == null)followCoaster.tlrail = this.tlrail;
		followCoaster.initConnectParamT(paramT, length);
		connectCoasterList.add( followCoaster );
//		ERC_Logger.info("connectEntityIdx:"+followCoaster.connectIndex);
		followCoaster.connectIndex = connectCoasterList.size();
	}
	
	public void deleteConnectCoaster(ERC_EntityCoasterConnector c)
	{
		connectCoasterList.remove(c);
	}
	
	public void clearConnectCoaster()
	{
		connectCoasterList.clear();
	}
	
	protected void mountSeats(EntityPlayer player)
	{
		player.mountEntity(this);
//		player.mountEntity(this.seats[0]);
	}
	
    /**
     * Called when the entity is attacked.
     */
    @Override
    public boolean attackEntityFrom(DamageSource ds, float p_70097_2_)
    {
    	boolean flag = ds.getEntity() instanceof EntityPlayer;// && ((EntityPlayer)ds.getEntity()).capabilities.isCreativeMode;

    	if (this.worldObj.isRemote || this.isDead)
    	{	
    		if(ds.getEntity() instanceof EntityPlayer)this.killCoaster(!((EntityPlayer)ds.getEntity()).capabilities.isCreativeMode);
    		else this.killCoaster();
    		return true;
    	}
        
	    if (this.isEntityInvulnerable() || this.isDead)
	    {
	    	return false;
	    }
	
//	    this.setRollingDirection(-this.getRollingDirection());
//	    this.setRollingAmplitude(10);
//	    this.setBeenAttacked();
//	    this.setDamage(this.getDamage() + p_70097_2_ * 10.0F);
	
	    if (flag)
	    {
	        if (this.riddenByEntity != null)
	        {
	            this.riddenByEntity.mountEntity(this);
	        }
	
	        this.killCoaster(!((EntityPlayer)ds.getEntity()).capabilities.isCreativeMode); 
	    }
        
    	return true;
    }
    
    public void killCoaster()
    {
    	killCoaster(true);
    }
    public void killCoaster(boolean dropflag)
    {
    	if(worldObj.isRemote) return; //クラは勝手にKillしない
    	
    	if(tlrail!=null)tlrail.onDeleteCoaster();
    	tlrail = null;
    	if(this.isDead)return;
    	
        this.setDead();
        ItemStack itemstack = dropflag?new ItemStack(getItem(), 1):null;

        if(itemstack!=null)
        	this.entityDropItem(itemstack, 0.0F);
        
//        if(seats!=null)for(ERC_EntitySecondSeat s : seats)if(s!=null)s.setDead();
        
//        ERC_Logger.info("server coaster is dead : "+this.getEntityId());
        if(!connectCoasterList.isEmpty())connectCoasterList.get(0).killCoaster(dropflag);
        ERC_PacketHandler.INSTANCE.sendToAll(new ERC_MessageCoasterMisc(this,1));
    }
    public void killPrevCoasters(boolean dropflag, int idx)
    {
    	List<ERC_EntityCoasterConnector> removelist = new ArrayList<ERC_EntityCoasterConnector>();
    	for(ERC_EntityCoasterConnector c : connectCoasterList)
    	{
    		if(c.connectIndex > idx)removelist.add(c);
    	}
    	for(ERC_EntityCoasterConnector c : removelist)
    	{
    		c.killCoaster(dropflag);
    		connectCoasterList.remove(c);
    	}
    }
    protected void killCoaster_Clientside()
    {
    	if(tlrail!=null)tlrail.onDeleteCoaster();
    	tlrail = null;
    	if(this.isDead)return;
        this.setDead();
//        for(ERC_EntitySecondSeat s : seats)if(s!=null)s.setDead();
//        ERC_Logger.info("client coaster is dead : "+this.getEntityId());
        if(!connectCoasterList.isEmpty())connectCoasterList.get(0).killCoaster_Clientside();
    }

    public void onChunkLoad() {} //何かに使えるかも
    
	public void onUpdate()
    {
//		setDead();
//		if(!worldObj.isRemote)
//			ERC_Logger.info("update:"+this.getClass().getName()+"   speed:"+Speed);
		syncToClient();
    	if(updateInit())return;	
        savePrevData();
        
        // 媒介変数の更新
        updateParamT();
        // 現レールを超えたら次の設定
        AdjustParamT();

//        ERC_Logger.info("update parent pos");

        // シートの座標設定
//        ERC_Logger.info("update seats pos");
//    	if(seats!=null)
//		{
//    		for(int i=0; i<seats.length; ++i)if(seats[i]!=null)seats[i].onUpdate(i);
//    		if(worldObj.isRemote)ERC_Logger.debugInfo("seats id : "+seats[0].getUniqueID().toString());
//		}
        updateSpeedAndRot();
//        ERC_Logger.debugInfo("parent update ");
    	
    	// １．コースターとシートの同期処理　流れ TODO
    	// ２．Tick開始時全てのシートと親は同じフラグであるとする。
    	// ３．親より早く更新が来たシートは親と違うフラグに変えて待機。
    	// ４．親自身の更新が終わったら自身のシートの中で自分と違うフラグのシートの更新を全て行う（シートのフラグは変えない） RiddenbyがあればupdateRiderPositionもやる
    	// ５．親はシートの更新が終わったら自分のフラグを変える。
    	// ６．親より遅く更新が来たシートは親と自分のフラグが違うはず　親とフラグが違う場合はその場で更新 フラグも変える
    	// ７．２に戻る
        
        // ４
        if(seats!=null)
		{
    		for(int i=0; i<seats.length; ++i)if(seats[i]!=null)
    		{
    			if(!seats[i].addedToChunk && !worldObj.isRemote)worldObj.spawnEntityInWorld(seats[i]);
    			if(seats[i].updateFlag != this.updateFlag)seats[i]._onUpdate();
    		}
//    		if(worldObj.isRemote)ERC_Logger.debugInfo("end coaster onUpdate");
		}
        
        // ５
    	updateFlag = !updateFlag;
    	return;
    }
    
    protected boolean updateInit()
    {
//    	setDead();
    	if(tlrail==null)
    	{
    		if(checkTileEntity())
    		{
    			killCoaster();
    			return true;
    		}
    	}
        if (this.posY < -64.0D || Double.isNaN(this.Speed) || this.isDead || tlrail.isBreak())
        {
        	if(Double.isNaN(this.Speed))
        	{
        		Speed = -0.1;
        		paramT = -0.1f;
//        		tlrail = tlrail.getPrevRailTileEntity().getRail();
        		return false;
        	}
            this.killCoaster();
            return true;
        }
        
//        if (this.getRollingAmplitude() > 0)
//        {
//            this.setRollingAmplitude(this.getRollingAmplitude() - 1);
//        }
//        if(this.riddenByEntity == null)
//        {
//        	ERC_BlockRailManager.rotationViewYaw = 0;
//        	ERC_BlockRailManager.rotationViewPitch = 0;
//        	ERC_BlockRailManager.prevRotationViewYaw = 0;
//        	ERC_BlockRailManager.prevRotationViewPitch = 0;
//        }
//        this.killCoaster();return false;
        return false;
    }
    
    protected void syncToClient()
    {
    	if(this.UpdatePacketCounter--<=0)
        {
    		UpdatePacketCounter = 50;
    		if(!worldObj.isRemote && tlrail != null)
    		{
		    	ERC_MessageCoasterStC packet = new ERC_MessageCoasterStC(getEntityId(), this.paramT, this.Speed, tlrail.xCoord, tlrail.yCoord, tlrail.zCoord, ModelID, CoasterOptions);
			    ERC_PacketHandler.INSTANCE.sendToAll(packet);
    		}
//    		else
//    		{
//				// modelID送る用パケット
//				ERC_MessageCoasterCtS packet = new ERC_MessageCoasterCtS(getEntityId(), -100f, -100f, -1, -1, -1, ModelID, CoasterOptions);
//			    ERC_PacketHandler.INSTANCE.sendToServer(packet);
//    		}
		}
    }
    
    protected void savePrevData()
    {
    	this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;             
//        this.prevRotationPitch = this.rotationPitch;       
//        this.prevRotationYaw = this.rotationYaw;
//        this.prevRotationRoll = this.rotationRoll;
        
        ERCPosMat.prevRoll = ERCPosMat.roll;
        ERCPosMat.prevPitch = ERCPosMat.pitch;
        ERCPosMat.prevYaw = ERCPosMat.yaw;
    }
    
    /*
     * 連結コースターを考慮したparamTの更新関数
     */
    protected void updateParamT()
    {
    	updateParamTFirstOnTick();
    	paramT += sumSpeed / tlrail.Length;	
    }
    
    protected void updateParamTFirstOnTick()
    {
    	@SuppressWarnings("static-access")
		int tick = ERC_Core.tickEventHandler.getTickcounter();
    	if(prevTickCount != tick)
    	{
//    		if(worldObj.isRemote)ERC_Logger.info("updateparamtfistontick");
    		prevTickCount = tick;
    		
    		sumSpeed = Speed;
        	if(!connectCoasterList.isEmpty())
        	{
        		int num = 1;
        		for(ERC_EntityCoasterConnector c : connectCoasterList)
        		{
        			++num;
        			sumSpeed += c.getSpeed();
        		}
        		sumSpeed /= (double)(num);
        		for(ERC_EntityCoasterConnector c : connectCoasterList)
        		{
        			if(!c.isDead)
        				c.setSumSpeed(sumSpeed);
        		}
        	}
    	}
    }
    
    protected boolean AdjustParamT()
    {
        if(paramT>1.0f)
        {
        	if(tlrail==null)return true;
//        	ERC_Logger.info("adjust paramT .before : "+paramT);
        	tlrail.onPassedCoaster(this);

        	do{
        		// レールの先があるかどうか確認　あれば超え、無ければ反転減速
        		Wrap_TileEntityRail wr = tlrail.getNextRailTileEntity();
        		if(wr==null)
        		{
//        			this.killCoaster(); 
        			Speed = -Speed*0.1; // 反転減速
        			paramT = 0.99f;
        			if(!worldObj.isRemote)ERC_Logger.info("Rails aren't connected. Check status of connection. (next)"); 
        			return false; 
        		}
        		paramT -= 1f;
        		paramT = paramT * tlrail.Length;
        		TileEntityRailBase next = wr.getRail();
        		next.BaseRail.SetPos(tlrail.xCoord, tlrail.yCoord, tlrail.zCoord); //レールポイント超えるときに接続の設定をしちゃう
        		tlrail = wr.getRail();
        		paramT /= tlrail.Length;        		
        	}while(paramT >= 1f);
//        	ERC_Logger.info("adjust paramT .after : "+paramT);
        	
        }else if(paramT<0f)
        {       
        	tlrail.onPassedCoaster(this);
        	if(tlrail==null)return true;
//        	ERC_Logger.info("adjust paramT .before : "+paramT);
        	do
        	{
        		Wrap_TileEntityRail wr = tlrail.getPrevRailTileEntity();
        		if(wr==null)
        		{
//        			this.killCoaster(); 
        			Speed = -Speed*0.1; // 反転減速
        			paramT = 0.01f;
        			ERC_Logger.info("Rails aren't connected. Check status of connection. (prev)"); 
        			return false; 
        		}
		    	paramT = -paramT * tlrail.Length;
				tlrail = wr.getRail();
				paramT = -paramT / tlrail.Length + 1f;
		    }while(paramT < 0f);
//        	ERC_Logger.info("adjust paramT .after : "+paramT);
        }
        return false;
    }
    
    protected void updateSpeedAndRot()
    {
    	Speed *= 0.9985;
        Speed += 0.027 * tlrail.CalcRailPosition2(paramT, ERCPosMat, 
        								ERC_CoasterAndRailManager.rotationViewYaw, ERC_CoasterAndRailManager.rotationViewPitch, 
        								(riddenByEntity instanceof EntityPlayerMP && worldObj.isRemote));
        
//        rotationRoll = ERCPosMat.viewRoll;
//        rotationPitch = ERCPosMat.viewPitch;
//        rotationYaw = ERCPosMat.viewYaw;
       
//        prevRotationYaw = fixrot(rotationYaw, prevRotationYaw);
//        prevRotationPitch = fixrot(rotationPitch, prevRotationPitch);
//        prevRotationRoll = fixrot(rotationRoll, prevRotationRoll);
        
        ERCPosMat.prevYaw = fixrot(ERCPosMat.yaw, ERCPosMat.prevYaw);
        ERCPosMat.prevPitch = fixrot(ERCPosMat.pitch, ERCPosMat.prevPitch);
        ERCPosMat.prevRoll = fixrot(ERCPosMat.roll, ERCPosMat.prevRoll);
        
        tlrail.SpecialRailProcessing(this);

        // tを元に座標更新
    	this.setPosition(ERCPosMat.Pos.xCoord, ERCPosMat.Pos.yCoord, ERCPosMat.Pos.zCoord);

    }
    
    @Override
    public void setPosition(double x, double y, double z)
    {
    	super.setPosition(x, y, z);
    }

    @Override
	public void updateRiderPosition()
    {
//		if (riddenByEntity!= null)
//	    {
//	
////			int x = 0 % CoasterOptions.SeatLine;
////			int y = 0 / CoasterOptions.SeatNum;
////			float w = x - (CoasterOptions.SeatLine-1)*0.5f;
////			float h = y - (CoasterOptions.SeatNum-1)*0.5f;
//	//    		w /= width;
//	//    		h /= width;
////			float w = 0;
////			float h = 0;
//			if(ERCPosMat==null)return;
//			if(ERCPosMat.Dir==null)return;
//			if(ERCPosMat.Pitch==null)return;
//	        this.riddenByEntity.setPosition(
//	        		this.posX + this.ERCPosMat.offsetY.xCoord * this.riddenByEntity.getYOffset(),  
//	        		this.posY + this.ERCPosMat.offsetY.yCoord * this.riddenByEntity.getYOffset(),
//	        		this.posZ + this.ERCPosMat.offsetY.zCoord * this.riddenByEntity.getYOffset()
//	        		);
//            this.riddenByEntity.rotationYaw = this.rotationYaw;
//            this.riddenByEntity.rotationPitch = this.rotationPitch;
//            this.riddenByEntity.prevRotationYaw = this.prevRotationYaw;
//            this.riddenByEntity.prevRotationPitch = this.prevRotationPitch; 
//	        
//	    }
	}

	@Override
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pit, int p_70056_9_)
    {
    	//仕様として何も無し　サーバーからの規定のEntity同期で使われており、同期を無効にするため
    }
    
	public void setParamFromPacket(float t, double speed, int x, int y, int z)
    {
		Wrap_TileEntityRail rail = (Wrap_TileEntityRail)worldObj.getTileEntity(x,y,z);
		if(rail instanceof TileEntityRailBranch2)return; // 分岐レール上のときは同期をちょっとやめてほしい
		
		this.setParamT(t);
		this.Speed = speed;
//		if(this instanceof ERC_EntityCoasterConnector)
//			ERC_Logger.info("sync client from server packet : "+paramT);

		if(rail == null)return;
		this.setRail( rail.getRail() );
    	if(tlrail==null)
    	{
    		if(checkTileEntity())
			{
    			killCoaster();
				return;
			}
    	}	
    }
    
	@Override
	public void applyEntityCollision(Entity p_70108_1_) 
    {
//		super.applyEntityCollision(p_70108_1_);
	}
	
	@Override
	public boolean isInWater() {
		return false;
	}

	@Override
	public boolean isPushedByWater() {
		return false;
	}
	
	public void setRail(TileEntityRailBase rail)
	{
		this.tlrail = rail;
	}

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    @SideOnly(Side.CLIENT)
    public void setVelocity(double x, double y, double z)
    {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
 
        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt_double(x * x + z * z);
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(x, z) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(y, (double)f) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        }
    }

//	/**
//     * Sets the current amount of damage the minecart has taken. Decreases over time. The cart breaks when this is over
//     * 40.
//     */
//    public void setDamage(float p_70492_1_)
//    {
//        this.dataWatcher.updateObject(19, Float.valueOf(p_70492_1_));
//    }
//    /**
//     * Gets the current amount of damage the minecart has taken. Decreases over time. The cart breaks when this is over
//     * 40.
//     */
//    public float getDamage()
//    {
//        return this.dataWatcher.getWatchableObjectFloat(19);
//    }

    
    public void setParamT(float t)
    {
    	this.paramT = t;
//        this.dataWatcher.updateObject(30, Float.valueOf(t));
    }

    public float getParamT()
    {
    	return paramT;
//        return this.dataWatcher.getWatchableObjectFloat(30);
    }

//    /**
//     * Sets the rolling direction the cart rolls while being attacked. Can be 1 or -1.
//     */
//    public void setRollingDirection(int p_70494_1_)
//    {
//        this.dataWatcher.updateObject(18, Integer.valueOf(p_70494_1_));
//    }
//
//    /**
//     * Gets the rolling direction the cart rolls while being attacked. Can be 1 or -1.
//     */
//    public int getRollingDirection()
//    {
//        return this.dataWatcher.getWatchableObjectInt(18);
//    }
    
    protected float fixrot(float rot, float prevrot)
    {
    	if(rot - prevrot>180f)prevrot += 360f;
        else if(rot - prevrot<-180f)prevrot -= 360f;
    	return prevrot;
    }
//    private float fixrotRoll(float roll, float prevRoll)
//    {
//    	if(roll - prevRoll>160f)prevRoll += 180f;
//        else if(roll - prevRoll<-160f)prevRoll -= 180f;
//    	return prevRoll;
//    }

    
//    /**
//     * Sets the rolling amplitude the cart rolls while being attacked.
//     */
//    public void setRollingAmplitude(int p_70497_1_)
//    {
//        this.dataWatcher.updateObject(17, Integer.valueOf(p_70497_1_));
//    }
//
//    /**
//     * Gets the rolling amplitude the cart rolls while being attacked.
//     */
//    public int getRollingAmplitude()
//    {
//        return this.dataWatcher.getWatchableObjectInt(17);
//    }
    
//    /**
//     * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
//     */
//    @SideOnly(Side.CLIENT)
//    public void performHurtAnimation()
//    {
////        this.setRollingDirection(-this.getRollingDirection());
////        this.setRollingAmplitude(10);
////        this.setDamage(this.getDamage() + this.getDamage() * 10.0F);
//    }
	
	@Override
    protected void readEntityFromNBT(NBTTagCompound nbt)
    {
        savex = nbt.getInteger("railx");
        savey = nbt.getInteger("raily");
        savez = nbt.getInteger("railz");
        this.paramT = nbt.getFloat("coastert");
        this.Speed = nbt.getDouble("coasterSpeed");
        this.connectNum = nbt.getInteger("connectnum");
        this.seatsNum = nbt.getInteger("seatsnum");
        seats = new ERC_EntityCoasterSeat[seatsNum];
        if(CoasterOptions == null)CoasterOptions = new ModelOptions();
        CoasterOptions.ReadFromNBT(nbt);
        int modelid = nbt.getInteger("modelid");
        setModelOptions(modelid, CoasterOptions);
        if(this instanceof ERC_EntityCoaster)ERC_ManagerCoasterLoad.registerParentCoaster(this);
    }
	
	@Override
    protected void writeEntityToNBT(NBTTagCompound nbt)
    {
		if(this.tlrail==null)
			return;
        nbt.setInteger("railx", this.tlrail.xCoord);
        nbt.setInteger("raily", this.tlrail.yCoord);
        nbt.setInteger("railz", this.tlrail.zCoord);
        nbt.setFloat("coastert", this.paramT);
        nbt.setDouble("coasterSpeed", this.Speed);
        nbt.setInteger("connectnum", connectCoasterList.size());
        nbt.setInteger("modelid", ModelID);
        nbt.setInteger("seatsnum", seatsNum);
        CoasterOptions.WriteToNBT(nbt);
    }
	
	public void SyncCoasterMisc_Send(ByteBuf buf, int flag)
	{
		switch(flag)
		{
		case 1 : //killcoaster
			// 特に追加パラメータは無い
			break;
		}
	}
	public void SyncCoasterMisc_Receive(ByteBuf buf, int flag)
	{
		switch(flag)
		{
		case 1:
			killCoaster_Clientside();
			return;
		}
	}
	
}
