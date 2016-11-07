package erc.sound;

import erc.entity.ERC_EntityCoasterSeat;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class ERCMovingSound extends MovingSound {
	
	private final EntityPlayer player;
	private final ERC_EntityCoasterSeat seat;

	public ERCMovingSound(EntityPlayer p_i45106_1_, ERC_EntityCoasterSeat p_i45106_2_)
	{
		super(new ResourceLocation("minecraft:minecart.base"));
	    this.player = p_i45106_1_;
	    this.seat = p_i45106_2_;
	    this.field_147666_i = ISound.AttenuationType.NONE;
	    this.repeat = true;
	    this.field_147665_h = 0;
	    this.field_147663_c = 0.2f;
	}

	public void update() {
		if ((!this.seat.isDead) && (this.player.isRiding()) && (this.player.ridingEntity == this.seat)) 
		{
			float f = seat.parent==null ? 0 : ((float) this.seat.parent.Speed);
			if (Math.abs(f) >= 0.01D) 
			{
				this.volume = (MathHelper.clamp_float(Math.abs(f)*0.5F, 0.0F, 1.0F));
				
//				ERC_Logger.debugInfo(""+volume);
			} 
			else 
			{
				this.volume = 0.0F;
			}
		} 
		else 
		{
			this.donePlaying = true; //or repeat?
		}
	}
}