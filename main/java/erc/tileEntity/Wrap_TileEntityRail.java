package erc.tileEntity;

import erc.gui.GUIRail.editFlag;
import erc.message.ERC_MessageRailStC;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class Wrap_TileEntityRail extends TileEntity{

//	public abstract Wrap_TileEntityRail getOwnRailData();
	public abstract void setDataToByteMessage(ByteBuf buf);
	public abstract void getDataFromByteMessage(ByteBuf buf);
	public abstract World getWorldObj();
	public abstract int getXcoord();
	public abstract int getYcoord();
	public abstract int getZcoord();
	public abstract TileEntityRailBase getRail();
	
	// 元RailBaseメンバ関数アブスト
	public abstract void syncData();
	public abstract Wrap_TileEntityRail getPrevRailTileEntity();
	public abstract Wrap_TileEntityRail getNextRailTileEntity();
	
//	public abstract void SetBaseRailPosition(int x, int y, int z, Vec3 BaseDir, Vec3 up, float power);
//	public abstract void SetNextRailVectors(Vec3 vecNext, Vec3 vecDir, Vec3 vecUp, 
//							float fUp, float fDirTwist, float Power, int cx, int cy, int cz);
	
	public abstract void connectionFromBack(int x, int y, int z);
	public abstract void connectionToNext(DataTileEntityRail otherrail, int x, int y, int z);
	
	// Wrap同期関数
	public abstract void SetRailDataFromMessage(ERC_MessageRailStC message);
	
	// 描画関連メンバアブスト
	public abstract ResourceLocation getDrawTexture();
	public abstract void render(Tessellator tess);
	
	// GUIレール操作用関数ラップ
	public abstract void AddControlPoint(int pointnum);
	public abstract void Smoothing();
	public abstract void AddPower(int idx);
	public abstract void UpdateDirection(editFlag flag, int idx);
	public abstract void ResetRot();
	public abstract void SpecialGUISetData(int flag);
	public abstract float CalcRailLength();
	
	// モデル描画クラス変更
	//sideonly client
	public abstract void changeRailModelRenderer(int index);
}
