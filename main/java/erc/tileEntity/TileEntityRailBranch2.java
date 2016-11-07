package erc.tileEntity;

import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import erc._core.ERC_Logger;
import erc._core.ERC_ReturnCoasterRot;
import erc.entity.ERC_EntityCoaster;
import erc.gui.GUIRail;
import erc.gui.GUIRail.editFlag;
import erc.math.ERC_MathHelper;
import erc.message.ERC_MessageRailStC;
import erc.message.ERC_PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class TileEntityRailBranch2 extends /*ERC_TileEntityRailBase*/Wrap_TileEntityRail{
	boolean toggleflag;
	int branchflag;
//	float branchLens[] = new float[2];
	TileEntityRailNormal rails[];
	
	public TileEntityRailBranch2()
	{
		super();
		
		toggleflag = false;
		branchflag = 0;
		rails = new TileEntityRailNormal[2];
		rails[0] = new TileEntityRailNormal();
		rails[1] = new TileEntityRailNormal();
		
		//branchのデータをRailsに反映させるやつ
		rails[0].setWorldObj(this.worldObj);
		rails[1].setWorldObj(this.worldObj);
	}

	public boolean getToggleFlag()
	{
		return toggleflag;
	}
	public void changeToggleFlag()
	{
		toggleflag = !toggleflag;
	}
	
	
	public void changeRail()
	{
		changeRail(branchflag==0?1:0);
	}
	public void changeRail(int flag)
	{
		if(flag > 1){
			ERC_Logger.warn("railbranch2 : out of index for railsflag");
			return;
		}
		
		branchflag = flag;
		setPosToRails();
	}
	public int getNowRailFlag()
	{
		return branchflag;
	}

	public World getWorldObj(){return worldObj;}
	public int getXcoord(){return this.xCoord;}
	public int getYcoord(){return this.yCoord;}
	public int getZcoord(){return this.zCoord;}
	public TileEntityRailBase getRail(){
		rails[branchflag].setWorldObj(this.worldObj);
		rails[branchflag].xCoord = xCoord;
		rails[branchflag].yCoord = yCoord;
		rails[branchflag].zCoord = zCoord;
		return rails[branchflag];
	}
	public Wrap_TileEntityRail getPrevRailTileEntity() {
		return (Wrap_TileEntityRail) worldObj.getTileEntity(rails[branchflag].BaseRail.cx, rails[branchflag].BaseRail.cy, rails[branchflag].BaseRail.cz);
	}
	public Wrap_TileEntityRail getNextRailTileEntity() {
		return ((Wrap_TileEntityRail)worldObj.getTileEntity(rails[branchflag].NextRail.cx, rails[branchflag].NextRail.cy, rails[branchflag].NextRail.cz));
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() 
	{
		return INFINITE_EXTENT_AABB;
	}
	
//	@Override
//	public void SetPrevRailPosition(int x, int y, int z) {
//		rails[0].SetPrevRailPosition(x, y, z);
//		rails[1].SetPrevRailPosition(x, y, z);
//	}
//
//	@Override
//	public void SetNextRailPosition(int x, int y, int z) {
//		rails[branchflag].SetNextRailPosition(x, y, z);
//	}

//	@Override
//	public ERC_TileEntityRailBase getPrevRailTileEntity() {
//		return rails[branchflag].getPrevRailTileEntity();
//	}
//
//	@Override
//	public ERC_TileEntityRailBase getNextRailTileEntity() {
//		return rails[branchflag].getNextRailTileEntity();
//	}

	public void SetPosNum(int num) {
		rails[branchflag].SetPosNum(num);
	}

	public int GetPosNum() {
		return rails[branchflag].GetPosNum();
	}

	// 分岐レールの後ろレールの位置設定はRails２つに設定するだけでおｋ
	public void SetPrevRailPosition(int x, int y, int z) {
		rails[0].BaseRail.SetPos(x, y, z);
		rails[1].BaseRail.SetPos(x, y, z);
	}
	public void SetBaseRailPosition(int x, int y, int z, Vec3 BaseDir, Vec3 up, float power) {
		rails[0].SetBaseRailPosition(x, y, z, BaseDir, up, power);
		rails[1].SetBaseRailPosition(x, y, z, BaseDir, up, power);
	}

	// GUIレール操作用関数ラップ
	public void AddControlPoint(int pointnum)
	{
		rails[0].AddControlPoint(pointnum);
		rails[1].AddControlPoint(pointnum);
	}
	public void Smoothing()
	{
		rails[0].setWorldObj(worldObj);
		rails[1].setWorldObj(worldObj);
		rails[0].Smoothing();
		rails[1].Smoothing();
	}
	public void AddPower(int flag)
	{
		rails[0].AddPower(flag);
		rails[1].AddPower(flag);
	}
	public void UpdateDirection(editFlag flag, int idx)
	{
		rails[branchflag].setWorldObj(worldObj);
		rails[branchflag].UpdateDirection(flag, idx);
		rails[branchflag==0?1:0].BaseRail.SetData(rails[branchflag].BaseRail);
	}
	public void ResetRot()
	{
		rails[0].ResetRot();
		rails[1].ResetRot();
	}

	public void SetNextRailVectors(Vec3 vecNext, Vec3 vecDir, Vec3 vecUp, float fUp, float fDirTwist, float Power,
			int cx, int cy, int cz) {
		rails[branchflag].SetNextRailVectors(vecNext, vecDir, vecUp, fUp, fDirTwist, Power, cx, cy, cz);
	}


	public double CalcRailPosition2(float t, ERC_ReturnCoasterRot ret, float viewyaw, float viewpitch, boolean riddenflag) {
		// 直接分岐レールにコースター置いたときだけ呼ばれる
		return rails[branchflag].CalcRailPosition2(t, ret, viewyaw, viewpitch, riddenflag);
	}

	public float CalcRailLength() {
		rails[branchflag==0?1:0].CalcRailLength();
		return rails[branchflag].CalcRailLength();
	}

	public void CalcPrevRailPosition() {
		rails[branchflag].CalcPrevRailPosition();
	}

	public void SpecialRailProcessing(ERC_EntityCoaster EntityCoaster) {}

	public void onPassedCoaster() {}

	public void onApproachingCoaster() {}

	public void SpecialGUIInit(GUIRail gui) {}

	public void SpecialGUISetData(int flag) {}

	////////////////////////////////////////////////////////////////////////////
	// Draw
	////////////////////////////////////////////////////////////////////////////
	public ResourceLocation getDrawTexture()
	{
		return this.rails[branchflag].RailTexture;
	}
	
	public void render(Tessellator tess) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		rails[branchflag].render(tess);
		GL11.glColor4f(0.5F, 0.5F, 0.5F, 1.0F);
		rails[branchflag==0?1:0].render(tess);
	}

	////////////////////////////////////////////////////////////////////////////
	// save, sync
	////////////////////////////////////////////////////////////////////////////
	public void SetRailDataFromMessage(ERC_MessageRailStC msg)
	{
		//branch2のデータ同期
		/////\\\\\
		Iterator<DataTileEntityRail> it = msg.raillist.iterator();
		// 0Base
		DataTileEntityRail e = it.next();
		rails[0].SetPosNum(msg.posnum);
		rails[0].SetBaseRailVectors(e.vecPos, e.vecDir, e.vecUp, e.Power);
		rails[0].SetBaseRailfUpTwist(e.fUp, e.fDirTwist);
		rails[0].BaseRail.SetPos(e.cx, e.cy, e.cz);
		// 1Base
		rails[1].SetPosNum(msg.posnum);	
		rails[1].SetBaseRailVectors(e.vecPos, e.vecDir, e.vecUp, e.Power);
		rails[1].SetBaseRailfUpTwist(e.fUp, e.fDirTwist);
		rails[1].BaseRail.SetPos(e.cx, e.cy, e.cz);
		// 0Next
		e = it.next();
		rails[0].SetNextRailVectors(e.vecPos, e.vecDir, e.vecUp, e.fUp, e.fDirTwist, e.Power, e.cx, e.cy, e.cz);
		// 1Next
		e = it.next();
		rails[1].SetNextRailVectors(e.vecPos, e.vecDir, e.vecUp, e.fUp, e.fDirTwist, e.Power, e.cx, e.cy, e.cz);
		/////\\\\\
		rails[0].xCoord = rails[1].xCoord = xCoord = msg.x;
		rails[0].yCoord = rails[1].yCoord = yCoord = msg.y;
		rails[0].zCoord = rails[1].zCoord = zCoord = msg.z;
//		rails[0].CreateNewRailVertexFromControlPoint();
//		rails[1].CreateNewRailVertexFromControlPoint();
		rails[0].CalcRailPosition();
		rails[1].CalcRailPosition();
	}
	
	public void setDataToByteMessage(ByteBuf buf) {
		buf.writeBoolean(this.toggleflag);
		buf.writeInt(branchflag);
	}

	public void getDataFromByteMessage(ByteBuf buf) {
		toggleflag = buf.readBoolean();
		branchflag = buf.readInt();
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		rails[0].loadFromNBT(nbt, "b0");
		rails[1].loadFromNBT(nbt, "b1");
		branchflag = nbt.getInteger("branch2flag");
		setPosToRails(0);
		setPosToRails(1);
	}

	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		rails[0].saveToNBT(nbt, "b0");
		rails[1].saveToNBT(nbt, "b1");
		nbt.setInteger("branch2flag", branchflag);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		this.writeToNBT(nbtTagCompound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTagCompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.func_148857_g());
		setPosToRails(0);
		setPosToRails(1);
	}

//	public void syncData(EntityPlayerMP player) {
//		setPosToRails(0);
//		setPosToRails(1);
//		rails[0].syncData(player);
//		rails[1].syncData(player);
//	}

	public void syncData() {
//		setPosToRails(0);
//		setPosToRails(1);
//		rails[0].syncData();
//		rails[1].syncData();
//		ERC_MessageRailStC packet = new ERC_MessageRailStC(xCoord, yCoord, zCoord, 0);
//		ERC_PacketHandler.INSTANCE.sendToAll(packet);
		
		ERC_MessageRailStC packet = new ERC_MessageRailStC(xCoord, yCoord, zCoord, rails[0].PosNum, rails[0].modelrailindex);
    	packet.addRail(rails[branchflag].BaseRail);
    	packet.addRail(rails[0].NextRail);
    	packet.addRail(rails[1].NextRail);
	    ERC_PacketHandler.INSTANCE.sendToAll(packet);
	}
	
	public void connectionFromBack(int x, int y, int z)
	{
		// Rails２つともに後ろのレールを設定するだけ
		this.SetPrevRailPosition(x, y, z);
    	this.syncData();
	}
	public void connectionToNext(DataTileEntityRail next, int x, int y, int z)
	{
		// branchflagの先のレールにデータセットしてつなげる
    	float power = ERC_MathHelper.CalcSmoothRailPower(rails[branchflag].BaseRail.vecDir,next.vecDir,rails[branchflag].BaseRail.vecPos, next.vecPos);
    	rails[branchflag].BaseRail.Power = power; 
    	rails[branchflag].SetNextRailVectors(next,x,y,z);
//    	rails[branchflag].CreateNewRailVertexFromControlPoint();
    	rails[branchflag].CalcRailLength();
    	this.syncData();
    	
    	/////////////////////////rails[branchflag].syncData(); ・・・Branch2の内包RailNormalは個別syncDataしてはいけない
	}

//	public void onTileSetToWorld_Init()
//	{
//		setPosToRails();
//		changeRail();
//		setPosToRails();
//		changeRail();
//	}
	
	private void setPosToRails(){ setPosToRails(branchflag);}
	private void setPosToRails(int flag)
	{
		rails[flag].xCoord = this.xCoord;
		rails[flag].yCoord = this.yCoord;
		rails[flag].zCoord = this.zCoord;
		rails[flag].setWorldObj(this.worldObj);
	}

	@Override
	public void changeRailModelRenderer(int index)
	{
		rails[0].setWorldObj(this.worldObj);
		rails[1].setWorldObj(this.worldObj);
		rails[0].changeRailModelRenderer(index);
		rails[1].changeRailModelRenderer(index);	
	}
}
