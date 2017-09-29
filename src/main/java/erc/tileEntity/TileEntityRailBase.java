package erc.tileEntity;

import java.util.Iterator;

import cpw.mods.fml.common.FMLCommonHandler;
import erc._core.ERC_Logger;
import erc._core.ERC_ReturnCoasterRot;
import erc.entity.ERC_EntityCoaster;
import erc.gui.GUIRail;
import erc.manager.ERC_CoasterAndRailManager;
import erc.manager.ERC_ModelLoadManager;
import erc.math.ERC_MathHelper;
import erc.message.ERC_MessageRailStC;
import erc.message.ERC_PacketHandler;
import erc.model.ERC_ModelDefaultRail;
import erc.model.Wrap_RailRenderer;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public abstract class TileEntityRailBase extends Wrap_TileEntityRail{
	public Wrap_RailRenderer modelrail; //test
	public int modelrailindex;
	//base
	public DataTileEntityRail BaseRail;
	//next
	public DataTileEntityRail NextRail;
	// コースター長
	public float Length;
	// 描画関連パラメタ
	protected int PosNum = 15;
//	public int VertexNum = PosNum*4;
//	Vec3 posArray[] = new Vec3[VertexNum];
	public boolean doesDrawGUIRotaArraw;
	public ResourceLocation RailTexture;
	
	public float fixedParamTTable[] = new float[PosNum];
		
	protected boolean isBreak;
	
	public TileEntityRailBase()
	{
		super();
//		for(int i = 0; i<VertexNum; ++i) posArray[i] = Vec3.createVectorHelper(0.0, 0.0, 0.0);
		for(int i = 0; i<PosNum; ++i) fixedParamTTable[i] =0;
		
		BaseRail = new DataTileEntityRail();
		NextRail = new DataTileEntityRail();

		Length = 1f;
		isBreak = false;
		
		RailTexture = new ResourceLocation("textures/blocks/iron_block.png");
		modelrail = new ERC_ModelDefaultRail();
//		modelrail = ERC_ModelLoadManager.getRailModel(2, 0);
	}
	
	public void Init()
	{
		BaseRail.SetPos(-1, -1, -1);
		NextRail.SetPos(-1, -1, -1);
	}
	
	public World getWorldObj(){return worldObj;}
	public int getXcoord(){return this.xCoord;}
	public int getYcoord(){return this.yCoord;}
	public int getZcoord(){return this.zCoord;}
	public TileEntityRailBase getRail(){return this;}
	
	@Override
	public double getMaxRenderDistanceSquared() 
	{
		return 100000d;
	}

	//@Override
	public boolean myisInvalid() 
	{
		return super.isInvalid();
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() 
	{
		return INFINITE_EXTENT_AABB;
	}
	
	private void SetPrevRailPosition(int x, int y, int z)
	{
		if(x==xCoord && y==yCoord && z==zCoord)
		{
			BaseRail.cx=-1;	BaseRail.cy=-1;	BaseRail.cz=-1;
			ERC_Logger.warn("TileEntityRail SetPrevRailPosition : connect oneself");
			return;
		}
		BaseRail.cx = x; BaseRail.cy = y; BaseRail.cz = z;
	}
//	private void SetNextRailPosition(int x, int y, int z)
//	{
//		if(x==xCoord && y==yCoord && z==zCoord)
//		{
//			NextRail.cx=-1;	NextRail.cy=-1;	NextRail.cz=-1;
//			ERC_Logger.warn("TileEntityRail SetNextRailPosition : connect oneself");
//			return;
//		}
//		NextRail.cx = x; NextRail.cy = y; NextRail.cz = z;
//	}

	public Wrap_TileEntityRail getPrevRailTileEntity()
	{
		return (Wrap_TileEntityRail) worldObj.getTileEntity(BaseRail.cx, BaseRail.cy, BaseRail.cz);
	}
	public Wrap_TileEntityRail getNextRailTileEntity()
	{
		return ((Wrap_TileEntityRail)worldObj.getTileEntity(NextRail.cx, NextRail.cy, NextRail.cz));
	}
	
	public void SetPosNum(int num)
	{
		this.PosNum = num;
		fixedParamTTable = new float[PosNum];
		for(int i = 0; i<PosNum; ++i) fixedParamTTable[i] = 0;
//		CreateNewRailVertexFromControlPoint();
	}
	
	public int GetPosNum(){return this.PosNum;}
	
	public boolean isBreak(){return isBreak;}
	public void setBreak(boolean flag){isBreak = flag;}
	
	@Override
	public void invalidate() {
		// ブロック削除の接続レール保持テスト
		if(worldObj.isRemote)
		{
			double dist = Minecraft.getMinecraft().thePlayer.getDistance(xCoord+0.5, yCoord, zCoord+0.5);
			if(6d > dist)
			{
				ERC_CoasterAndRailManager.SetPrevData(BaseRail.cx, BaseRail.cy, BaseRail.cz);
				ERC_CoasterAndRailManager.SetNextData(NextRail.cx, NextRail.cy, NextRail.cz);
			}
		}
		super.invalidate();
	}

	public void SetRailDataFromMessage(ERC_MessageRailStC msg)
	{
    	this.SetPosNum(msg.posnum);	
		/////\\\\\
		Iterator<DataTileEntityRail> it = msg.raillist.iterator();
		// Base
		DataTileEntityRail e = it.next();
		SetBaseRailVectors(e.vecPos, e.vecDir, e.vecUp, e.Power);
		SetBaseRailfUpTwist(e.fUp, e.fDirTwist);
		SetPrevRailPosition(e.cx, e.cy, e.cz);
		// Next
		e = it.next();
		SetNextRailVectors(e.vecPos, e.vecDir, e.vecUp, e.fUp, e.fDirTwist, e.Power, e.cx, e.cy, e.cz);
		/////\\\\\
//    	this.CreateNewRailVertexFromControlPoint();
    	this.CalcRailPosition();
	}
	
	public void SetBaseRailPosition(int x, int y, int z, Vec3 BaseDir, Vec3 up, float power)
	{
		//　next_n：新しく設置されたブロックの座標  prev_n：前回設置したブロックの座標
		BaseRail.vecPos.xCoord = (double)x + 0.5;
		BaseRail.vecPos.yCoord = (double)y + 0.5;
		BaseRail.vecPos.zCoord = (double)z + 0.5;
		SetBaseRailVectors(BaseRail.vecPos, BaseDir, up, power);
	}
	
	public void SetBaseRailVectors(Vec3 posBase, Vec3 dirBase, Vec3 vecup, float power)
	{
		CopyVector(BaseRail.vecPos, posBase);
		CopyVector(BaseRail.vecDir, dirBase);
		CopyVector(BaseRail.vecUp, vecup);
		BaseRail.Power = power;
	}
	
	public void SetBaseRailfUpTwist(float up, float twist)
	{
		BaseRail.fUp = up;
		BaseRail.fDirTwist = twist;
	}
	
	public void SetNextRailVectors(TileEntityRailBase nexttile)
	{
		SetNextRailVectors(nexttile.BaseRail, nexttile.xCoord, nexttile.yCoord, nexttile.zCoord);//vecBase, nexttile.dirBase, nexttile.vecUp, nexttile.fUp, nexttile.fDirTwist, nexttile.Power);
	}
	public void SetNextRailVectors(DataTileEntityRail rail, int x, int y, int z)
	{
		SetNextRailVectors(rail.vecPos, rail.vecDir, rail.vecUp, rail.fUp, rail.fDirTwist, rail.Power, x, y, z);
	}
	public void SetNextRailVectors(Vec3 vecNext, Vec3 vecDir, Vec3 vecUp, float fUp, float fDirTwist, float Power, int cx, int cy, int cz) {
		this.NextRail.SetData(vecNext, vecDir, vecUp, fUp, fDirTwist, Power, cx, cy, cz);
	}
	
//	public ERC_TileEntityRailBase getOwnRailData()
//	{
//		return this;
//	}
	
	private void CopyVector(Vec3 dest, Vec3 src)
	{
		dest.xCoord = src.xCoord;
		dest.yCoord = src.yCoord;
		dest.zCoord = src.zCoord;
	}
	
	public void AddControlPoint(int n)
	{
		n = n*2-1;
		if( this.PosNum+n > 50) SetPosNum(50);
		else if( this.PosNum+n < 2) SetPosNum(2);
		else SetPosNum(this.PosNum + n);
//		CreateNewRailVertexFromControlPoint();
	}

//	public void CreateNewRailVertexFromControlPoint()
//	{
//		this.VertexNum = this.PosNum*4;
//		posArray = new Vec3[VertexNum];
//		for(int i = 0; i<VertexNum; ++i) posArray[i] = Vec3.createVectorHelper(0.0, 0.0, 0.0);
//		CalcRailPosition();
//	}
	
	public void AddPower(int idx)
	{
		float f=0;
		switch(idx)
		{
		case 0 : f = -1.0f; break;
		case 1 : f = -0.1f; break;
		case 2 : f =  0.1f; break;
		case 3 : f =  1.0f; break;
		}
		if( BaseRail.Power+f > 100f) BaseRail.Power = 100f;
		else if( BaseRail.Power+f < 0.1f) BaseRail.Power = 0.1f;
		else BaseRail.Power += f;
//		CreateNewRailVertexFromControlPoint(); //->	CalcRailPosition();
//		CalcPrevRailPosition();
	}
	
	public void UpdateDirection(GUIRail.editFlag flag, int idx)
	{
		float rot=0;
		switch(idx)
		{
		case 0 : rot = -0.5f; break;
		case 1 : rot = -0.05f; break;
		case 2 : rot =  0.05f; break;
		case 3 : rot =  0.5f; break;
		}
		int meta = worldObj.getBlockMetadata((int)this.xCoord, (int)this.yCoord, (int)this.zCoord);
		switch(flag)
		{
		case ROTRED : // レール設置面に対して水平回転　制限無し
			ConvertVec3FromMeta(meta&7, BaseRail.vecDir, rot); break;
		case ROTGREEN : // 勾配増減　fUpをいじる
			BaseRail.addFUp(rot); break;
		case ROTBLUE : // ひねり追加
			BaseRail.addTwist(rot); break;
		default:
			break;
		}
	}
	 private void ConvertVec3FromMeta(int meta, Vec3 dir, float rot)
    {
    	switch(meta){
    	case 0:dir.rotateAroundY(-rot); break;
    	case 1:dir.rotateAroundY(-rot); break;
    	case 2:dir.rotateAroundZ(-rot); break;
    	case 3:dir.rotateAroundZ(-rot); break;
    	case 4:dir.rotateAroundX(-rot); break;
    	case 5:dir.rotateAroundX(-rot); break;
    	}
    }
	
	public void ResetRot()
	{
		BaseRail.resetRot();
	}
	 
	public void Smoothing()
	{
		if(isConnectRail_prev1_next2());
		// PrevとNextへのそれぞれのベクトルのNormalizeのSubを自身のDirBaseへ
		// PrevとNextどちらかのレールが無い場合は無効
		Wrap_TileEntityRail prevtl = BaseRail.getConnectionTileRail(worldObj);
		Wrap_TileEntityRail nexttl = NextRail.getConnectionTileRail(worldObj);
		if(prevtl == null) return;
		if(nexttl == null) return;
		
		//Smoothingはthis.vecBaseからPrev.vecBaseとNext.vecBaseへのそれぞれのベクトルのNormalize：n'-p'がBase.dirBase
//		Vec3 n = BaseRail.vecPos.subtract(nexttl.getRail().BaseRail.vecPos).normalize();
//		Vec3 p = BaseRail.vecPos.subtract(prevtl.getRail().BaseRail.vecPos).normalize();
		Vec3 n = nexttl.getRail().BaseRail.vecPos;
		Vec3 p = prevtl.getRail().BaseRail.vecPos;
		Vec3 tempDir = p.subtract(n).normalize();
		BaseRail.vecDir = tempDir.normalize();
		switch(worldObj.getBlockMetadata(xCoord, yCoord, zCoord)&7)
		{
		case 0: case 1: // 上下	y
			BaseRail.vecDir.yCoord = 0; 
			BaseRail.fUp = (tempDir.yCoord>0?-1:1) * (float) (tempDir.yCoord / Math.sqrt(tempDir.xCoord*tempDir.xCoord+tempDir.zCoord*tempDir.zCoord));
			break;
		case 2: case 3: // 南北	z
			BaseRail.vecDir.zCoord = 0; 
			BaseRail.fUp = (tempDir.zCoord>0?-1:1) * (float) (tempDir.zCoord / Math.sqrt(tempDir.xCoord*tempDir.xCoord+tempDir.yCoord*tempDir.yCoord));
			break;
		case 4: case 5: // 東西	x
			BaseRail.vecDir.xCoord = 0; 
			BaseRail.fUp = (tempDir.xCoord>0?-1:1) * (float) (tempDir.xCoord / Math.sqrt(tempDir.yCoord*tempDir.yCoord+tempDir.zCoord*tempDir.zCoord));
			break;
		}
		BaseRail.vecDir = BaseRail.vecDir.normalize();
		
		BaseRail.Power = (float) p.subtract(n).lengthVector()/2;
//		BaseRail.Power = ERC_MathHelper.CalcSmoothRailPower(
//    			BaseRail.vecDir, nexttl.getRail().BaseRail.vecDir, 
//    			BaseRail.vecPos, nexttl.getRail().BaseRail.vecPos
//    			);
		CalcRailPosition();
		prevtl.getRail().SetNextRailVectors(this);
		prevtl.getRail().CalcRailPosition();
		BaseRail.Power = (float) p.subtract(n).lengthVector()/2;
//		BaseRail.Power = ERC_MathHelper.CalcSmoothRailPower(BaseRail.vecDir, NextRail.vecDir, BaseRail.vecPos, NextRail.vecPos);

//		prevtl.SetNextRailPosition(vecBase, dirBase, vecUp, Power);
	}
	
	public boolean isConnectRail_prev1_next2()
	{
		return false;
//		if(getPrevRailTileEntity()==null)return false;
//		else
//		{
//			Wrap_TileEntityRail next1 = getNextRailTileEntity();
//			if(next1==null)return false;
//			if(next1.getNextRailTileEntity() == null)return false;
//			return true;
//		}
	}
	
	public void SmoothingSpecial()
	{
		Smoothing();
	}
	
	public void CalcRailPosition()
	{	
//		if(!worldObj.isRemote)return;
		
		////pos
		Vec3 Base = Vec3.createVectorHelper(BaseRail.vecUp.xCoord, BaseRail.vecUp.yCoord, BaseRail.vecUp.zCoord);
		Base.xCoord *= 0.5; Base.yCoord *= 0.5; Base.zCoord *= 0.5;
		Vec3 Next = BaseRail.vecPos.subtract(NextRail.vecPos);
		Next.xCoord += NextRail.vecUp.xCoord*0.5; 
		Next.yCoord += NextRail.vecUp.yCoord*0.5; 
		Next.zCoord += NextRail.vecUp.zCoord*0.5;
		
		////dir
//		float basepow = ERC_MathHelper.Lerp(0.2f, BaseRail.Power, NextRail.Power);
//		float nextpow = ERC_MathHelper.Lerp(0.8f, BaseRail.Power, NextRail.Power);
		Vec3 DirxPowb = BaseRail.CalcVec3DIRxPOW(BaseRail.Power);//basepow);
		Vec3 DirxPown = NextRail.CalcVec3DIRxPOW(NextRail.Power);//nextpow);
		
		////pair of rail Vertex
//		Vec3 vecpitch1 = vecUp.crossProduct(dirBase).normalize();
//		Vec3 vecpitch2 = vecNextUp.crossProduct(dirNext).normalize();
		Vec3 vecUp_1 = BaseRail.CalcVec3UpTwist();
		Vec3 vecUp_2 = NextRail.CalcVec3UpTwist();

		// スプライン曲線距離計算準備
		Length = 0;
		Vec3 tempPrev = null;
		fixedParamTTable[0] = 0;
		if(modelrail!=null)modelrail.setModelNum(PosNum);
		
		for(int i = 0; i<PosNum; ++i)
		{
//			int j = i*4; // VertexIndex
			float f = (float)i/(float)(PosNum-1);
			
			////spline
			Vec3 center = ERC_MathHelper.Spline(f, Base, Next, DirxPowb, DirxPown);
			if(i>0)
			{
				Length += center.distanceTo(tempPrev);
				fixedParamTTable[i] = Length;
			}
			tempPrev = center;
		}
		
		calcFixedParamT();//////////////////////////////////////////////////////////////////////////
		
		float ModelLen = Length/(PosNum-1);
		
		for(int i = 0; i<PosNum; ++i){
			
			float f = (float)i/(float)(PosNum-1);
			////fixed spline
//			float lT;
			int T = (int)Math.floor(f * (PosNum-1));
//			if(PosNum-1 <= T) lT = fixedParamTTable[T];
			f = fixedParamTTable[T];
//			else lT = ERC_MathHelper.Lerp(f-(T/(float)(PosNum-1)), fixedParamTTable[T], fixedParamTTable[T+1]);
			Vec3 center = ERC_MathHelper.Spline(f, Base, Next, DirxPowb, DirxPown);
//			ERC_Logger.info("f-t:"+(f-(T/(float)(PosNum-1))));
					
			Vec3 dir1;
			// 制御点が中間地点であれば前後のベクトル、制御基礎点であれば制御方向ベクトルを用いる
			if(f <= 0.01f)
			{
				dir1 = DirxPowb.normalize();
			}
			if(f >= 0.99f)
			{
				dir1 = DirxPown.normalize();
			}
			else
			{
					 dir1 = ERC_MathHelper.Spline((f+0.01f), Base, Next, DirxPowb, DirxPown);
				Vec3 dir2 = ERC_MathHelper.Spline((f-0.01f), Base, Next, DirxPowb, DirxPown);
				dir1 = dir2.subtract(dir1); // dir1 - dir2
			}
			
			////pair of rail Vertex
			Vec3 up = ERC_MathHelper.Slerp(f, vecUp_1, vecUp_2).normalize();
			Vec3 cross = up.crossProduct(dir1);
			cross = cross.normalize().normalize();

			
//			if(j>=posArray.length)
//			{
//				ERC_Logger.warn("index exception");
//				return;
//			}
			
			if(modelrail!=null)modelrail.construct(i, center, dir1, cross, ModelLen);
//			// 左
//			posArray[j  ].xCoord = center.xCoord - cross.xCoord*t1;
//			posArray[j  ].yCoord = center.yCoord - cross.yCoord*t1;
//			posArray[j  ].zCoord = center.zCoord - cross.zCoord*t1;
//			posArray[j+1].xCoord = center.xCoord - cross.xCoord*t2;
//			posArray[j+1].yCoord = center.yCoord - cross.yCoord*t2;
//			posArray[j+1].zCoord = center.zCoord - cross.zCoord*t2;
//			// 右 
//			posArray[j+2].xCoord = center.xCoord + cross.xCoord*t2;
//			posArray[j+2].yCoord = center.yCoord + cross.yCoord*t2;
//			posArray[j+2].zCoord = center.zCoord + cross.zCoord*t2;
//			posArray[j+3].xCoord = center.xCoord + cross.xCoord*t1;
//			posArray[j+3].yCoord = center.yCoord + cross.yCoord*t1;
//			posArray[j+3].zCoord = center.zCoord + cross.zCoord*t1;
			
//			// 位置
//			posArray[j  ] = center;
//			// 角度
//			Vec3 crossHorz = Vec3.createVectorHelper(0, 1, 0).crossProduct(dir1);
//			Vec3 dir_horz = Vec3.createVectorHelper(dir1.xCoord, 0, dir1.zCoord);
//			posArray[j+1].xCoord = -Math.toDegrees( Math.atan2(dir1.xCoord, dir1.zCoord) );
//			posArray[j+1].yCoord = Math.toDegrees( ERC_MathHelper.angleTwoVec3(dir1, dir_horz) * (dir1.yCoord>0?-1f:1f) );
//			posArray[j+1].zCoord = Math.toDegrees( ERC_MathHelper.angleTwoVec3(cross, crossHorz) * (cross.yCoord>0?1f:-1f) );
//			// 長さ
//			if(i!=PosNum-1)posArray[j+2].xCoord = (fixedParamTTable[i+1]-fixedParamTTable[i])*Length;

		
		}
//		calcFixedParamT();
	}
	
	
	protected void calcFixedParamT()
	{
		///////////// fixedParamT修正
		
		// [0,1]のPosNum個分割の間隔距離の計算
		float div = Length / (float)(PosNum-1);
//		float divT = 1.0f / (float)PosNum;
		float tempFixed[] = new float[PosNum];
		// 線形補間でdivの位置を探す
		int I=1;
		for(int i=1; i<PosNum; ++i)
		{
//			ERC_Logger.info("i I div:"+i+" "+I+" "+div*i/Length
//					+ "  fixedParamTTable[I]"+fixedParamTTable[I]/Length
//					+"  fixedParamTTable[I-1]"+fixedParamTTable[I-1]/Length);
			if(div*i < fixedParamTTable[I] && div*i >= fixedParamTTable[I-1])
			{
				float divnum = PosNum - 1f;
				float t = (div*i - fixedParamTTable[I-1]) / (fixedParamTTable[I] - fixedParamTTable[I-1]);
				tempFixed[i] = (I-1)/divnum + t*(1f/divnum);
//				ERC_Logger.info("tempfix[i]:"+tempFixed[i]);
			}
			else 
			{
				if(I<PosNum-1)
				{
					++I;
					--i;
				}
				else
				{
					tempFixed[i] = 1.0f;
				}
			}
		}
		tempFixed[PosNum-1] = 1.0f;
		fixedParamTTable = tempFixed;
//        ERC_Logger.info(""+fixedParamTTable[3]);
	}
	
	// コースターの座標更新とプレイヤーカメラ回転用	戻り値はレールの傾き
	public double CalcRailPosition2(float t, ERC_ReturnCoasterRot ret, float viewyaw, float viewpitch, boolean riddenflag)
	{	
		//////////////コースター制御計算
		
		////pos
		Vec3 Base = Vec3.createVectorHelper(BaseRail.vecUp.xCoord, BaseRail.vecUp.yCoord, BaseRail.vecUp.zCoord);
		Base.xCoord *= 0.5; Base.yCoord *= 0.5; Base.zCoord *= 0.5;
		Vec3 Next = BaseRail.vecPos.subtract(NextRail.vecPos);
		Next.xCoord += NextRail.vecUp.xCoord*0.5; 
		Next.yCoord += NextRail.vecUp.yCoord*0.5; 
		Next.zCoord += NextRail.vecUp.zCoord*0.5;
		
		////dir
//		float basepow = ERC_MathHelper.Lerp(0.2f, BaseRail.Power, NextRail.Power);
//		float nextpow = ERC_MathHelper.Lerp(0.8f, BaseRail.Power, NextRail.Power);
		Vec3 DirxPowb = BaseRail.CalcVec3DIRxPOW(BaseRail.Power);//basepow);  
		Vec3 DirxPown = NextRail.CalcVec3DIRxPOW(NextRail.Power);//nextpow);  
		
		////pair of rail Vertex
		Vec3 vecUp_1 = BaseRail.CalcVec3UpTwist();
		Vec3 vecUp_2 = NextRail.CalcVec3UpTwist();
	    
		////spline
		float lT=0f;
		if(t < 0)
		{
			ERC_Logger.warn("tileentityrailbase.calcposition2 : paramT is smaller than 0");
			t = 0;
		}
		int T = (int)Math.floor(t * (PosNum-1));
		if(PosNum-1 <= T) lT = fixedParamTTable[PosNum-1];
		else lT = ERC_MathHelper.Lerp(t*(PosNum-1)-T, fixedParamTTable[T], fixedParamTTable[T+1]);
		t = lT;
		
		ret.Pos = ERC_MathHelper.Spline(t, Base, Next, DirxPowb, DirxPown);

		Vec3 dir1;
		// 制御点が中間地点であれば前後のベクトル、制御基礎点であれば制御方向ベクトルを用いる
		if(t <= 0.01f)
		{
//			dir1 = Vec3.createVectorHelper(BaseRail.vecDir.xCoord, BaseRail.vecDir.yCoord, BaseRail.vecDir.zCoord);
			dir1 = DirxPowb.normalize();
		}
		if(t >= 0.99f)
		{
//			dir1 = Vec3.createVectorHelper(NextRail.vecDir.xCoord, NextRail.vecDir.yCoord, NextRail.vecDir.zCoord);
			dir1 = DirxPown.normalize();
		}
		else
		{
				 dir1 = ERC_MathHelper.Spline((t+0.01f), Base, Next, DirxPowb, DirxPown);
			Vec3 dir2 = ERC_MathHelper.Spline((t-0.01f), Base, Next, DirxPowb, DirxPown);
			dir1 = dir2.subtract(dir1).normalize(); // dir1 - dir2
		}
		
		////pair of rail Vertex
		Vec3 up = ERC_MathHelper.Slerp(t, vecUp_1, vecUp_2).normalize();
		Vec3 cross = up.crossProduct(dir1).normalize();
		
//		ERC_MathHelper.CalcCoasterRollMatrix(ret, ret.Pos, dir1, up);
		
		ret.Pos.xCoord += this.xCoord + 0.5 ;//+ up.xCoord;
		ret.Pos.yCoord += this.yCoord + 0.5 ;//+ up.yCoord;
		ret.Pos.zCoord += this.zCoord + 0.5 ;//+ up.zCoord;
		
		ret.Dir = dir1;
		ret.Pitch = cross;
		//////// プレイヤー座標Offset計算
		Vec3 fixUp = dir1.crossProduct(cross);
		ret.offsetX = cross;
		ret.offsetY = fixUp;
		ret.offsetZ = dir1;
		
		
//		if(riddenflag)
		{
			////////////// プレイヤー回転量計算
			
			/* memo
			 * 元Vec・・・ dir1,up,cross
			 * 視点回転後・・・ dir_rotView
			 */
			
			// ViewYaw回転ベクトル　dir1->dir_rotView, cross->turnCross
			Vec3 dir_rotView = ERC_MathHelper.rotateAroundVector(dir1, fixUp, Math.toRadians(viewyaw));
			Vec3 turnCross = ERC_MathHelper.rotateAroundVector(cross, fixUp, Math.toRadians(viewyaw));
			// ViewPitch回転ベクトル dir1->dir_rotView
			Vec3 dir_rotViewPitch = ERC_MathHelper.rotateAroundVector(dir_rotView, turnCross, Math.toRadians(viewpitch));
			// pitch用 dir_rotViewPitchの水平ベクトル
//			Vec3 dir_rotViewPitchHorz = Vec3.createVectorHelper(dir_rotViewPitch.xCoord, 0, dir_rotViewPitch.zCoord);
			// roll用turnCrossの水平ベクトル　テスト
			Vec3 crossHorz = Vec3.createVectorHelper(0, 1, 0).crossProduct(dir1);
			if(crossHorz.lengthVector()==0.0)crossHorz=Vec3.createVectorHelper(1, 0, 0);
			Vec3 crossHorzFix = Vec3.createVectorHelper(0, 1, 0).crossProduct(dir_rotViewPitch);
			if(crossHorzFix.lengthVector()==0.0)crossHorzFix=Vec3.createVectorHelper(1, 0, 0);
			
			Vec3 dir_horz = Vec3.createVectorHelper(dir1.xCoord, 0, dir1.zCoord);
			if(dir_horz.lengthVector()==0.0)dir_horz=fixUp;
//			Vec3 dir_WorldUp = Vec3.createVectorHelper(0, 1, 0);
			
			// yaw OK
			ret.yaw = (float) -Math.toDegrees( Math.atan2(dir1.xCoord, dir1.zCoord) );
//			ret.viewYaw = (float) -Math.toDegrees( Math.atan2(dir_rotViewPitch.xCoord, dir_rotViewPitch.zCoord) );

			// pitch OK
			ret.pitch = (float) Math.toDegrees( ERC_MathHelper.angleTwoVec3(dir1, dir_horz) * (dir1.yCoord>=0?-1f:1f) );
//			ret.viewPitch = (float) Math.toDegrees( ERC_MathHelper.angleTwoVec3(dir_rotViewPitch, dir_rotViewPitchHorz) * (dir_rotViewPitch.yCoord>=0?-1f:1f) );
//			if(Float.isNaN(ret.viewPitch))
//				ret.viewPitch=0;
			
			// roll
			ret.roll = (float) Math.toDegrees( ERC_MathHelper.angleTwoVec3(cross, crossHorz) * (cross.yCoord>=0?1f:-1f) );
//			ret.viewRoll = (float) Math.toDegrees( ERC_MathHelper.angleTwoVec3(turnCross, crossHorzFix) * (turnCross.yCoord>=0?1f:-1f) );
//			if(Float.isNaN(ret.viewRoll))
//				ret.viewRoll=0;
		}
		
		return -dir1.normalize().yCoord;
	}
	
	public float CalcRailLength()
	{	
		////pos
		Vec3 Base = Vec3.createVectorHelper(BaseRail.vecUp.xCoord, BaseRail.vecUp.yCoord, BaseRail.vecUp.zCoord);
		Base.xCoord *= 0.5; Base.yCoord *= 0.5; Base.zCoord *= 0.5;
		Vec3 Next = BaseRail.vecPos.subtract(NextRail.vecPos);
		Next.xCoord += NextRail.vecUp.xCoord*0.5; 
		Next.yCoord += NextRail.vecUp.yCoord*0.5; 
		Next.zCoord += NextRail.vecUp.zCoord*0.5;
		
		////dir
//		float basepow = ERC_MathHelper.Lerp(0.2f, BaseRail.Power, NextRail.Power);
//		float nextpow = ERC_MathHelper.Lerp(0.8f, BaseRail.Power, NextRail.Power);
		Vec3 DirxPowb = BaseRail.CalcVec3DIRxPOW(BaseRail.Power);//basepow);  
		Vec3 DirxPown = NextRail.CalcVec3DIRxPOW(NextRail.Power);//nextpow);  

		// スプライン曲線距離計算準備
		Length = 0;
		Vec3 tempPrev = Base;
		fixedParamTTable[0]=0;
		
		for(int i = 0; i<PosNum; ++i)
		{
//			int j = i*4; // VertexIndex
			float f = (float)i/(float)(PosNum-1);
			
			////spline
			Vec3 center = ERC_MathHelper.Spline(f, Base, Next, DirxPowb, DirxPown);
			if(i>0)
			{
				Length += center.distanceTo(tempPrev);
				fixedParamTTable[i] = Length;
			}
			tempPrev = center;
		}
		
		calcFixedParamT();
		return Length;
	}
	
	public void CalcPrevRailPosition()
	{
		Wrap_TileEntityRail Wprevtile = BaseRail.getConnectionTileRail(worldObj);
		if(Wprevtile == null)
		{
			return;
		}
		TileEntityRailBase prevtile = Wprevtile.getRail();
		prevtile.SetNextRailVectors(this);
//		prevtile.CreateNewRailVertexFromControlPoint();
//		prevtile.CalcRailPosition();
	}
	
	public abstract void SpecialRailProcessing(ERC_EntityCoaster EntityCoaster);
	
	public void onPassedCoaster(ERC_EntityCoaster EntityCoaster){}
	public void onApproachingCoaster(){}
	public void onDeleteCoaster(){}
//	public void onTileSetToWorld_Init(){}
	
	// 特殊レール用GUI操作関数
	public void SpecialGUIInit(GUIRail gui){}
	public void SpecialGUISetData(int flag){}
	public String SpecialGUIDrawString(){return "";}
	
	// 汎用レールメッセージ用のデータ読み書き関数
	public void setDataToByteMessage(ByteBuf buf){}
	public void getDataFromByteMessage(ByteBuf buf){}
	
	public ResourceLocation getDrawTexture()
	{
		return this.RailTexture;
	}
	
	public void render(Tessellator tess)
	{
		modelrail.render(tess);	
	}
	
	public void changeRailModelRenderer(int index) //TODO
	{
		modelrailindex = index;
		
//		if(worldObj==null)return;
//		if(worldObj.isRemote)
		if(FMLCommonHandler.instance().getSide().isClient())
		{
			modelrail = ERC_ModelLoadManager.createRailRenderer(index, this);
			modelrail.setModelNum(PosNum);
			CalcRailPosition();
		}
	}
	
	// NBTの読み取り。
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.readFromNBT(par1NBTTagCompound);      
    	loadFromNBT(par1NBTTagCompound, "");
    }
    public void loadFromNBT(NBTTagCompound nbt, String tag)
    {
    	SetPosNum(nbt.getInteger(tag+"posnum"));
//    	this.VertexNum = PosNum*4;

        readRailNBT(nbt, BaseRail, tag+"");
        readRailNBT(nbt, NextRail, tag+"n");

        modelrailindex = nbt.getInteger(tag+"railmodelindex");
        changeRailModelRenderer(modelrailindex);
//        this.CreateNewRailVertexFromControlPoint();
//        if(worldObj.isRemote)
        	this.CalcRailPosition();
//        else this.CalcRailLength();
    }
    // レールの情報1つ分読み取り用
    protected void readRailNBT(NBTTagCompound nbt, DataTileEntityRail rail, String tag)
    {
    	readVec3(nbt, rail.vecPos,  tag+"pos");
    	readVec3(nbt, rail.vecDir,  tag+"dir");
        readVec3(nbt, rail.vecUp, 	tag+"up");
        
        rail.fUp 		= nbt.getFloat(tag+"fup");
        rail.fDirTwist 	= nbt.getFloat(tag+"fdt");
        rail.Power 		= nbt.getFloat(tag+"pow");
        
        rail.cx = nbt.getInteger(tag+"cx"); 
        rail.cy = nbt.getInteger(tag+"cy"); 
        rail.cz = nbt.getInteger(tag+"cz"); 
        
     // 自分自身に繋がるのを防ぐ           
        if(rail.cx==xCoord && rail.cy==yCoord && rail.cz==zCoord)
        {
        	rail.cx=-1; rail.cy=-1; rail.cz=-1;
        }
    }
    // NBT読み込み補助
    private void readVec3(NBTTagCompound nbt, Vec3 vec, String name)
    {
    	vec.xCoord = nbt.getDouble(name+"x");
    	vec.yCoord = nbt.getDouble(name+"y");
    	vec.zCoord = nbt.getDouble(name+"z");
    }
    
    /*
     * こちらはNBTを書き込むメソッド。
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        saveToNBT(par1NBTTagCompound, "");
       
    }
    public void saveToNBT(NBTTagCompound nbt, String tag)
    {
    	nbt.setInteger(tag+"posnum", this.PosNum);
        writeRailNBT(nbt, BaseRail, tag+"");
     	writeRailNBT(nbt, NextRail, tag+"n");
     	nbt.setInteger(tag+"railmodelindex", modelrailindex);
    }
    protected void writeRailNBT(NBTTagCompound nbt, DataTileEntityRail rail, String tag)
    {
    	writeVec3(nbt, rail.vecPos,  tag+"pos");
    	writeVec3(nbt, rail.vecDir,  tag+"dir");
    	writeVec3(nbt, rail.vecUp, 	tag+"up");
        
    	nbt.setFloat(tag+"fup", rail.fUp);
    	nbt.setFloat(tag+"fdt", rail.fDirTwist);
    	nbt.setFloat(tag+"pow", rail.Power);
    	
        nbt.setInteger(tag+"cx", rail.cx); 
        nbt.setInteger(tag+"cy", rail.cy); 
        nbt.setInteger(tag+"cz", rail.cz); 
        
     // 自分自身に繋がるのを防ぐ           
        if(rail.cx==xCoord && rail.cy==yCoord && rail.cz==zCoord)
        {
        	rail.cx=-1; rail.cy=-1; rail.cz=-1;
        }
    }
    // NBT書き込み補助
    private void writeVec3(NBTTagCompound nbt, Vec3 vec, String name)
    {
    	nbt.setDouble(name+"x", vec.xCoord);
    	nbt.setDouble(name+"y", vec.yCoord);
    	nbt.setDouble(name+"z", vec.zCoord);
    }
    

	@Override
	public Packet getDescriptionPacket()
	{
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        this.writeToNBT(nbtTagCompound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTagCompound);
	}
 
	@Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
	{
        this.readFromNBT(pkt.func_148857_g());
    }
	
	//サーバーがクライアントへ制御点を送信するための同期関数 送信対象プレイヤーが決まっているとき
    public void syncData(EntityPlayerMP player)
    {
//    	ERC_MessageRailStC packet = new ERC_MessageRailStC(
//    			xCoord, yCoord, zCoord, PosNum, 
//    			px, py, pz, nx, ny, nz,
//    			BaseRail.vecPos, BaseRail.vecDir, BaseRail.vecUp, 
//    			NextRail.vecPos, NextRail.vecDir, NextRail.vecUp, 
//    			BaseRail.Power, BaseRail.fUp, BaseRail.fDirTwist,
//    			NextRail.Power, NextRail.fUp, NextRail.fDirTwist
//    			);
    	ERC_MessageRailStC packet = new ERC_MessageRailStC(xCoord, yCoord, zCoord, PosNum, modelrailindex);
    	packet.addRail(BaseRail);
    	packet.addRail(NextRail);
	    ERC_PacketHandler.INSTANCE.sendTo(packet, player);
//	    ERC_PacketHandler.INSTANCE.sendToAll(packet);
    }
	
	//自身を同期 プレイヤー全員が対象（のはず？）
	public void syncData()
	{
		ERC_MessageRailStC packet = new ERC_MessageRailStC(xCoord, yCoord, zCoord, PosNum, modelrailindex);
    	packet.addRail(BaseRail);
    	packet.addRail(NextRail);
	    ERC_PacketHandler.INSTANCE.sendToAll(packet);
	}

	public void connectionFromBack(int x, int y, int z)
	{
		// 自分につなげる要請は破棄
		if(x==xCoord && y==yCoord && z==zCoord)return;
				
		this.SetPrevRailPosition(x, y, z);
    	this.syncData();
	}
	public void connectionToNext(DataTileEntityRail next, int x, int y, int z)
	{
		// 自分につなげる要請は破棄
		if(x==xCoord && y==yCoord && z==zCoord)return;
		
    	float power = ERC_MathHelper.CalcSmoothRailPower(BaseRail.vecDir, next.vecDir, BaseRail.vecPos, next.vecPos);
		this.BaseRail.Power = power;
    	this.SetNextRailVectors(next,x,y,z);
    	Wrap_TileEntityRail prev = this.getPrevRailTileEntity();
    	if(prev!=null)
    	{
    		TileEntityRailBase r = prev.getRail();
    		r.SetNextRailVectors(this.getRail());
    		r.CalcRailLength();
    		prev.syncData();
    	}
//    	this.CreateNewRailVertexFromControlPoint();
    	this.CalcRailLength();
    	this.syncData();
	}
}
