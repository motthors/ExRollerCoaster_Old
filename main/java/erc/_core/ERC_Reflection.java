package erc._core;

import org.lwjgl.input.Mouse;

import cpw.mods.fml.common.ObfuscationReflectionHelper;

public class ERC_Reflection {

	public static void setCameraRoll(float roll)
	{
//		try
//		{
////			roll = (float) Math.toDegrees(roll);
//		  
//			Minecraft mc = Minecraft.getMinecraft();
//			ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, mc.entityRenderer, Float.valueOf(roll), new String[] { "field_78495_O", "camRoll" });
//		  
//			ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, mc.entityRenderer, Float.valueOf(roll), new String[] { "field_78505_P", "prevCamRoll" });
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
	}
	
	public static void setMouseDHweel(int dhweel)
	{
		try
		{
//			Mouse mc = Minecraft.getMinecraft();
			ObfuscationReflectionHelper.setPrivateValue(Mouse.class, null, dhweel, "event_dwheel");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
