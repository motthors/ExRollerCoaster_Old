package erc.handler;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erc._core.ERC_Logger;
import erc.entity.ERC_EntityCoasterSeat;
import erc.sound.ERCMovingSound;
import erc.sound.ERCMovingSoundRiding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Timer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

@SideOnly(Side.CLIENT)
public class ERC_RenderEventHandler {
	

	ERC_EntityCoasterSeat getCoaster(EntityLivingBase target)
	{
		if (!target.isRiding()) {
			return null;
		}
		if (!(target.ridingEntity instanceof ERC_EntityCoasterSeat)) {
			return null;
		}
		return (ERC_EntityCoasterSeat)target.ridingEntity;
	}
	 
	@SideOnly(Side.CLIENT)
  	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void renderPre(RenderLivingEvent.Pre event)
	{
		if (event.isCanceled()) {
			return;
		}
		ERC_EntityCoasterSeat coaster;
		if ((coaster = getCoaster(event.entity)) == null) {
			return;
		}
		GL11.glPushMatrix();
	    
		Timer timer = (Timer)ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), new String[] { "field_71428_T", "timer" });
	    float partialTicks = timer.renderPartialTicks;
	    
	    float yaw = coaster.parent.ERCPosMat.getFixedYaw(partialTicks);
	    float pitch = coaster.parent.ERCPosMat.getFixedPitch(partialTicks);
	    float roll = coaster.parent.ERCPosMat.getFixedRoll(partialTicks) + (float)Math.toDegrees(coaster.getRotZ());
	    
//        event.entity.renderYawOffset = yaw;
//        event.entity.rotationYawHead = yaw;
	    Entity theplayer = Minecraft.getMinecraft().thePlayer;
	    Entity e = event.entity;
	    double x = theplayer.prevPosX+(theplayer.posX-theplayer.prevPosX)*partialTicks - (e.prevPosX+(e.posX-e.prevPosX)*partialTicks);
	    double y = theplayer.prevPosY+(theplayer.posY-theplayer.prevPosY)*partialTicks - (e.prevPosY+(e.posY-e.prevPosY)*partialTicks);
	    double z = theplayer.prevPosZ+(theplayer.posZ-theplayer.prevPosZ)*partialTicks - (e.prevPosZ+(e.posZ-e.prevPosZ)*partialTicks);
//	    double x = event.entity.prevPosX+(event.entity.posX-event.entity.prevPosX)*partialTicks;
//	    double y = event.entity.prevPosY+(event.entity.posY-event.entity.prevPosY)*partialTicks;
//	    double z = event.entity.prevPosZ+(event.entity.posZ-event.entity.prevPosZ)*partialTicks;
	    GL11.glTranslated(-x,-y,-z);
	    GL11.glRotatef(yaw, 0.0F, -1.0F, 0.0F);
	    GL11.glRotatef(pitch, 1.0F, 0.0F, 0.0F);
	    GL11.glRotatef(roll, 0.0F, 0.0F, 1.0F);
	    GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
	    GL11.glTranslated(x,y,z);
	}
	  
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void renderPost(RenderLivingEvent.Post event)
	{
		@SuppressWarnings("unused")
		ERC_EntityCoasterSeat coaster;
		if ((coaster = getCoaster(event.entity)) == null) {
			return;
		}
		GL11.glPopMatrix();
	}

	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void ridingErc(LivingEvent.LivingUpdateEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();
	    if (!(event.entity instanceof EntityClientPlayerMP)) return;
	    
	    EntityClientPlayerMP player = (EntityClientPlayerMP)event.entity;
	    
	    String key = "RideERC";
	    if (player.getEntityData().hasKey(key))
	    {
	    	if ((player.ridingEntity == null) || (!(player.ridingEntity instanceof ERC_EntityCoasterSeat))) 
	    	{
	    		player.getEntityData().removeTag(key);
	    	}
	    }
	    else if ((player.ridingEntity != null) && ((player.ridingEntity instanceof ERC_EntityCoasterSeat)))
	    {
	    	mc.getSoundHandler().playSound(new ERCMovingSoundRiding(player, (ERC_EntityCoasterSeat)player.ridingEntity));
	    	mc.getSoundHandler().playSound(new ERCMovingSound(player, (ERC_EntityCoasterSeat)player.ridingEntity));
	    	player.getEntityData().setBoolean(key, true);
	    	ERC_Logger.debugInfo("sound update");
	    }
	}
}
