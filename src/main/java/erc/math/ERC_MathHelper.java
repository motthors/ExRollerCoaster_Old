package erc.math;

import net.minecraft.util.Vec3;

public class ERC_MathHelper {
	
	public static class Vec4{
		float x; float y; float z; float w;
		public Vec4(){x=0;y=0;z=0;w=0;}
		public Vec4(float x,float y,float z,float w){this.x = x; this.y = y; this.z = z; this.w = w;}
		public Vec4(double x,double y,double z,double w){this.x = (float) x; this.y = (float) y; this.z = (float) z; this.w = (float) w;}
	}
	
	public static Vec3 Spline(float t, Vec3 base, Vec3 next, Vec3 dir1, Vec3 dir2)
	{
		// 媒介変数ベクトル生成
//		Vec4 vec_t = new Vec4(t,t*t,t*t*t,1.0f);
		
//		// スプライン定数行列生成
//		Matrix4f spline_c = new Matrix4f();
//		spline_c.m00 = 2f; spline_c.m01 = -2f; spline_c.m02 = 1f; spline_c.m03 = 1f;
//		spline_c.m10 = -3f; spline_c.m11 = 3f; spline_c.m12 = -2f; spline_c.m13 = -1f;
//		spline_c.m20 = 0f; spline_c.m21 = 0f; spline_c.m22 = 1f; spline_c.m23 = 0f;
//		spline_c.m30 = 1f; spline_c.m31 = 0f; spline_c.m32 = 0f; spline_c.m33 = 0f;
//		
//		// 制御点座標方向ベクトル合成行列生成
//		Matrix4f spline_V = new Matrix4f();
//		spline_V.m00 = (float) base.xCoord; spline_V.m01 = (float)base.yCoord; spline_V.m02 = (float)base.zCoord; spline_V.m03 = 1f;
//		spline_V.m10 = (float) next.xCoord; spline_V.m11 = (float)next.yCoord; spline_V.m12 = (float)next.zCoord; spline_V.m13 = 1f;
//		spline_V.m20 = (float) dir1.xCoord; spline_V.m21 = (float)dir1.yCoord; spline_V.m22 = (float)dir1.zCoord; spline_V.m23 = 0f;
//		spline_V.m30 = (float) dir2.xCoord; spline_V.m31 = (float)dir2.yCoord; spline_V.m32 = (float)dir2.zCoord; spline_V.m33 = 0f;
//		
//		Matrix4f spline_c = new Matrix4f(
//				2f,-3f,0f,1f,
//				-2f,3f,0f,0f,
//				1f,-2f,1f,0f,
//				1f,-1f,0f,0f);
//		
//		Matrix4f spline_V = new Matrix4f(
//				(float)base.xCoord,
//				(float)next.xCoord,
//				(float)dir1.xCoord,
//				(float)dir2.xCoord,
//				(float)base.yCoord,
//				(float)next.yCoord,
//				(float)dir1.yCoord,
//				(float)dir2.yCoord,
//				(float)base.zCoord,
//				(float)next.zCoord,
//				(float)dir1.zCoord,
//				(float)dir2.zCoord,
//				1f,1f,0f,0f
//				);
		
		float t2 = t*t;
		float t3 = t2*t;
		Vec4 vt = new Vec4(2f*t3-3f*t2+1f, -2f*t3+3f*t2, t3-2f*t2+t, t3-t2);
				
		Vec4 ans = new Vec4(
			(float)base.xCoord*vt.x+
			(float)next.xCoord*vt.y+
			(float)dir1.xCoord*vt.z+
			(float)dir2.xCoord*vt.w,
			(float)base.yCoord*vt.x+
			(float)next.yCoord*vt.y+
			(float)dir1.yCoord*vt.z+
			(float)dir2.yCoord*vt.w,
			(float)base.zCoord*vt.x+
			(float)next.zCoord*vt.y+
			(float)dir1.zCoord*vt.z+
			(float)dir2.zCoord*vt.w,
			1f*vt.x+
			1f*vt.y);
//		vec_t = Matrix4f.transform(spline_c, vec_t, vec_t);
//		vec_t = Matrix4f.transform(spline_V, vec_t, vec_t);
//		spline_V.mul(spline_c);
//		spline_V.transform(vec_t);
		
		return Vec3.createVectorHelper(ans.x, ans.y, ans.z);
	}
	
	public static Vec3 Lerp(float t, Vec3 base, Vec3 next)
	{
		return Vec3.createVectorHelper(
				base.xCoord*(1-t)+next.xCoord*t, 
				base.yCoord*(1-t)+next.yCoord*t, 
				base.zCoord*(1-t)+next.zCoord*t
				);
	}
	
	public static float CalcSmoothRailPower(Vec3 dirBase, Vec3 dirNext, Vec3 vecBase, Vec3 vecNext)
	{
		double dot = dirBase.dotProduct(dirNext);
    	double len = vecBase.distanceTo(vecNext);
    	float f = (float) ((-dot+1d)*3d+len*0.8);
    	//ERC_Logger.info("math helper = "+f+", distance = "+len+", dot = "+dot);
    	return f;
	}
	
//	public static void CalcCoasterRollMatrix(ERC_ReturnCoasterRot out, Vec3 Pos, Vec3 Dir, Vec3 Up)
//	{
//		Vec3 zaxis = Dir.normalize();//Pos.subtract(At).normalize();
//		Vec3 xaxis = zaxis.crossProduct(Up).normalize();
//		Vec3 yaxis = xaxis.crossProduct(zaxis);
//
////		out.rotmat.clear();
//////		out.rotmat.put(xaxis.xCoord).put(yaxis.xCoord).put(-zaxis.xCoord).put(0);
//////		out.rotmat.put(xaxis.yCoord).put(yaxis.yCoord).put(-zaxis.yCoord).put(0);
//////		out.rotmat.put(xaxis.zCoord).put(yaxis.zCoord).put(-zaxis.zCoord).put(0);
//////		out.rotmat.put(0).put(0).put(0).put(1);
////		out.rotmat.put(xaxis.xCoord).put(xaxis.yCoord).put(xaxis.zCoord).put(0);
////		out.rotmat.put(yaxis.xCoord).put(yaxis.yCoord).put(yaxis.zCoord).put(0);
////		out.rotmat.put(zaxis.xCoord).put(zaxis.yCoord).put(zaxis.zCoord).put(0);
////		out.rotmat.put(0).put(0).put(0).put(1);
////		out.rotmat.flip();
//	}
	
	public static double angleTwoVec3(Vec3 a, Vec3 b)
	{
//		@SuppressWarnings("unused")
//		double temp = a.normalize().dotProduct(b.normalize());
		return Math.acos( clamp(a.normalize().dotProduct(b.normalize())) );
	}
	
	public static double clamp(double a)
	{
		return a>1d?1d:(a<-1d?-1d:a);
	}
	
	public static float wrap(float a)
	{
		if(a >  Math.PI)a -= Math.PI*2;
		if(a < -Math.PI)a += Math.PI*2;
		return a;
	}
	
	public static float Lerp(float t, float a1, float a2)
	{
		return a1*(1-t) + a2*t;
	}
	
	public static Vec3 rotateAroundVector(Vec3 rotpos, Vec3 axis, double radian)
	{
		radian *= 0.5;
		Vec4 Qsrc = new Vec4(0,rotpos.xCoord,rotpos.yCoord,rotpos.zCoord);
		Vec4 Q1 = new Vec4(Math.cos(radian), axis.xCoord*Math.sin(radian), axis.yCoord*Math.sin(radian), axis.zCoord*Math.sin(radian));
		Vec4 Q2 = new Vec4(Math.cos(radian),-axis.xCoord*Math.sin(radian),-axis.yCoord*Math.sin(radian),-axis.zCoord*Math.sin(radian));
	
		Vec4 ans = MulQuaternion(MulQuaternion(Q2, Qsrc), Q1);
		return Vec3.createVectorHelper(ans.y, ans.z, ans.w);
	}
	private static Vec4 MulQuaternion(Vec4 q1, Vec4 q2)
	{
		return new Vec4(
				q1.x*q2.x - (q1.y*q2.y+q1.z*q2.z+q1.w*q2.w),
				q1.x*q2.y + q2.x*q1.y + (q1.z*q2.w - q1.w*q2.z),
				q1.x*q2.z + q2.x*q1.z + (q1.w*q2.y - q1.y*q2.w),
				q1.x*q2.w + q2.x*q1.w + (q1.y*q2.z - q1.z*q2.y)
				);
	}
	
	// 球面線形補間
	public static Vec3 Slerp(float t, Vec3 Base, Vec3 Goal)
	{
		double theta = Math.acos(clamp(Base.dotProduct(Goal)));
		if(theta == 0 || theta == 1d)return Base;
		double sinTh = Math.sin(theta);
		double Pb = Math.sin(theta*(1-t));
		double Pg = Math.sin(theta*t);
		return Vec3.createVectorHelper(
				(Base.xCoord*Pb + Goal.xCoord*Pg)/sinTh, 
				(Base.yCoord*Pb + Goal.yCoord*Pg)/sinTh, 
				(Base.zCoord*Pb + Goal.zCoord*Pg)/sinTh);
	}
	
	public static float fixrot(float rot, float prevrot)
    {
    	if(rot - prevrot>180f)prevrot += 360f;
        else if(rot - prevrot<-180f)prevrot -= 360f;
    	return prevrot;
    }
}
