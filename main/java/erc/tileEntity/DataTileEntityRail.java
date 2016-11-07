package erc.tileEntity;

import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class DataTileEntityRail {
	public Vec3 vecPos;
	public Vec3 vecDir;
	public Vec3 vecUp;
	public float fUp;
	public float fDirTwist;
	public float Power;	// 	Power‚Ì‚ÝBase‚ÆNext‚ÌŠÔ‚Å‹¤’Ê
	public int cx, cy, cz; // connection rail ŽŸ‚â‘O‚ª‚Ç‚±‚ÉŒq‚ª‚Á‚Ä‚¢‚é‚©
	
	public DataTileEntityRail()
	{
		vecPos = Vec3.createVectorHelper(0, 0, 0);
		vecDir = Vec3.createVectorHelper(0, 0, 0);
		vecUp = Vec3.createVectorHelper(0, 0, 0);
		fUp = 0;
		fDirTwist = 0;
		Power = 25f;
		cx = cy = cz = -1;
	}
	
	public void SetData(Vec3 pos, Vec3 dir, Vec3 up, float fup, float fdir, float pow, int x, int y, int z)
	{
		vecPos = pos;                      
		vecDir = dir;                    
		vecUp = up;
		fUp = fup;
		fDirTwist = fdir;
		Power = pow;
		cx = x;
		cy = y;
		cz = z;
	}
	
	public void SetData(DataTileEntityRail src)
	{
		vecPos = src.vecPos;                      
		vecDir = src.vecDir;                    
		vecUp = src.vecUp;
		fUp = src.fUp;
		fDirTwist = src.fDirTwist;
		Power = src.Power;
		cx = src.cx;
		cy = src.cy;
		cz = src.cz;
	}
	
	public void SetPos(int x, int y, int z)
	{
		cx = x;
		cy = y;
		cz = z;
	}
	
	public void addFUp(float rot)
	{
		fUp += rot; if(fUp > 1f)fUp=1f; else if(fUp < -1f)fUp=-1f;
	}
	public void addTwist(float rot)
	{
		fDirTwist -= rot; if(fDirTwist > 1f)fDirTwist=1f; else if(fDirTwist < -1f)fDirTwist=-1f;
	}
	public void resetRot()
	{
		fUp = 0;
		fDirTwist = 0;
	}
	
	public Vec3 CalcVec3DIRxPOW(float pow)
	{
		return Vec3.createVectorHelper(
				(vecDir.xCoord+vecUp.xCoord*fUp)*pow, 
				(vecDir.yCoord+vecUp.yCoord*fUp)*pow, 
				(vecDir.zCoord+vecUp.zCoord*fUp)*pow);
	}
	public Vec3 CalcVec3UpTwist()
	{
		Vec3 vecpitch1 = vecUp.crossProduct(vecDir).normalize();
		return Vec3.createVectorHelper(
				vecUp.xCoord+vecpitch1.xCoord*fDirTwist,
				vecUp.yCoord+vecpitch1.yCoord*fDirTwist,
				vecUp.zCoord+vecpitch1.zCoord*fDirTwist).normalize();
	}
	
	public Wrap_TileEntityRail getConnectionTileRail(World world)
	{
		return (Wrap_TileEntityRail) world.getTileEntity(cx, cy, cz);
	}
	
}
