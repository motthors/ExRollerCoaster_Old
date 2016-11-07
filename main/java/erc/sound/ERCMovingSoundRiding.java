package erc.sound;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erc.entity.ERC_EntityCoasterSeat;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class ERCMovingSoundRiding extends MovingSound {
	private final EntityPlayer player;
	private final ERC_EntityCoasterSeat seat;

	public ERCMovingSoundRiding(EntityPlayer p_i45106_1_, ERC_EntityCoasterSeat p_i45106_2_)
	{
	    super(new ResourceLocation("minecraft:minecart.inside"));
	    this.player = p_i45106_1_;
	    this.seat = p_i45106_2_;
	    this.field_147666_i = ISound.AttenuationType.NONE;
	    this.repeat = true;
	    this.field_147665_h = 0;
	    this.field_147663_c = 10.7f;
	}

	public void update() 
	{
		if ((!this.seat.isDead) && (this.player.isRiding()) && (this.player.ridingEntity == this.seat))
		{
			float f = seat.parent==null ? 0 : ((float) this.seat.parent.Speed);
			if (f >= 0.01D) 
			{
				this.volume = (MathHelper.clamp_float((float) Math.pow(Math.abs(f), 3.0D) * 0.5F, 0.0F, 1.0F));
				this.field_147663_c = 1.3f;
			} 
			else 
			{
				this.volume = 0.0F;
			}
		} 
		else
		{
			this.donePlaying = true;
		}
	}
}