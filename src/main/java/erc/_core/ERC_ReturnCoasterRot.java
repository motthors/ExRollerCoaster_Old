package erc._core;

import net.minecraft.util.Vec3;

public class ERC_ReturnCoasterRot {
	// コースターの制御
	public Vec3 Pos;
	public float roll;
	public float yaw;
	public float pitch;
	public float prevRoll;
	public float prevYaw;
	public float prevPitch;
//	public DoubleBuffer rotmat;
	// プレイヤーの制御
	public float viewRoll;
	public float viewYaw;
	public float viewPitch;
	public Vec3 offsetX;
	public Vec3 offsetY;
	public Vec3 offsetZ;
	// 座席のオフセット用
	public Vec3 Dir;
	public Vec3 Pitch;
	
	public ERC_ReturnCoasterRot()
	{
		Pos = Vec3.createVectorHelper(0, 0, 0);
 		
 		roll = 0;
 		yaw = 0;
 		pitch = 0;
 		viewRoll = 0;
 		viewYaw = 0; 
 		viewPitch = 0;
 		
 		offsetX = Vec3.createVectorHelper(1, 0, 0);
 		offsetY = Vec3.createVectorHelper(0, 1, 0);
 		offsetZ = Vec3.createVectorHelper(0, 0, 1);
	}
	
	public float getFixedRoll(float partialTicks)
	{
		return prevRoll + (roll - prevRoll)*partialTicks;
	}
	public float getFixedYaw(float partialTicks)
	{
		return prevYaw + (yaw - prevYaw)*partialTicks;
	}
	public float getFixedPitch(float partialTicks)
	{
		return prevPitch + (pitch - prevPitch)*partialTicks;
	}
}
