package erc.manager;

import erc.manager.ERC_ModelLoadManager.ModelOptions;

public class ERC_ModelLoadPlan {

	private String ModelName;
	private String TextureName;
	private String IconName;
	private ModelOptions Option = new ModelOptions();
	
	public String getModelName() {return ModelName;}
	public String getTextureName() {return TextureName;}
	public String getIconName() {return IconName;}
	public ModelOptions getOption() {return Option;}
	
	/*
	 * 初期化。 最低限モデルファイル、テクスチャファイル、ｱｲｺﾝファイルの指定は必須です。
	 */
	public ERC_ModelLoadPlan(String ObjName, String TextureName, String IconName)
	{
		this.ModelName = ObjName;
		this.TextureName = TextureName;
		this.IconName = IconName;
		Option.setSeatNum(1);
	}
	
	/*
	 * 車両の基本設定を行います。　length:後続コースターを接続する間隔　width:コースター全体の幅　height:コースター全体の高さ　canRide:コースターに乗れるかどうかのフラグ
	 * width,heightはコースター全体を覆うことができるくらいのサイズを指定してください。
	 */
	public boolean setCoasterMainData(float length, float width, float height, boolean canRide)
	{
		Option.Length = length;
		Option.Width = width;
		Option.Height = height;
		Option.canRide = canRide;
		return true;
	}
	
	/*
	 * 座席数の指定を行います。 
	 */
	public boolean setSeatNum(int num)
	{
		if(num < 1)return false;
		Option.setSeatNum(num);
		return true;
	}
	
	/*
	 * 指定の番号の座席の位置設定を行います。　index:座席番号[1-設定した数] x:横方向　y:高さ方向　z:進行方向　rotation:座席の回転量(degree)
	 */
	public boolean setSeatOffset(int index, float offsetX, float offsetY, float offsetZ)
	{
		if(index < 0 || index >= Option.SeatNum)return false;
		Option.offsetX[index] = offsetX;
		Option.offsetY[index] = offsetY;
		Option.offsetZ[index] = offsetZ;
		return true;
	}
	
	/*
	 * 指定の番号の座席の回転量設定を行います。　index:座席番号[1-設定した数]　rotX:進行方向軸の回転量　rotY:垂直軸の回転量　rotZ:水平軸の回転量
	 * 回転量の単位は弧度法（radian）です。
	 * 回転の適用順はZ>Y>Xです。
	 */
	public boolean setSeatRotation(int index, float rotX, float rotY, float rotZ)
	{
		if(index < 0 || index >= Option.SeatNum)return false;
		Option.rotX[index] = rotX;
		Option.rotY[index] = rotY;
		Option.rotZ[index] = rotZ;
		return true;
	}
	
	/*
	 * 指定の番号の座席の当たり判定設定を行います。　index:座席番号[1-設定した数]　size:座席当たり判定の立方体のサイズ
	 * 座席に座るとき、連結コースターを接続するときにこの当たり判定が使われます。
	 */
	public boolean setSeatSize(int index, float size)
	{
		if(index < 0 || index >= Option.SeatNum)return false;
		Option.size[index] = size;
		return true;
	}
}
