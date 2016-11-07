package erc.entity;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erc._core.ERC_CONST;
import erc._core.ERC_Core;
import erc.math.ERC_MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class entitySUSHI extends Entity {
	
	@SideOnly(Side.CLIENT)
	public static ResourceLocation tex;
	@SideOnly(Side.CLIENT)
	public static IModelCustom model1;
	@SideOnly(Side.CLIENT)
	public static IModelCustom model2;
	@SideOnly(Side.CLIENT)
	public static IModelCustom model3;
	@SideOnly(Side.CLIENT)
	public static IModelCustom model4;
	@SideOnly(Side.CLIENT)
	public static IModelCustom model5;
	@SideOnly(Side.CLIENT)
	public static IModelCustom[] models;

	@SideOnly(Side.CLIENT)
	public static void clientInitSUSHI()
	{
		tex = new ResourceLocation(ERC_CONST.DOMAIN,"textures/entities/SUSHI.png");
		model1 = AdvancedModelLoader.loadModel(new ResourceLocation(ERC_CONST.DOMAIN,"models/SUSHI/"+"SUSHI_m.obj"));
		model2 = AdvancedModelLoader.loadModel(new ResourceLocation(ERC_CONST.DOMAIN,"models/SUSHI/"+"SUSHI_t.obj"));
		model3 = AdvancedModelLoader.loadModel(new ResourceLocation(ERC_CONST.DOMAIN,"models/SUSHI/"+"SUSHI_w.obj"));
		model4 = AdvancedModelLoader.loadModel(new ResourceLocation(ERC_CONST.DOMAIN,"models/SUSHI/"+"SUSHI_e.obj"));
		model5 = AdvancedModelLoader.loadModel(new ResourceLocation(ERC_CONST.DOMAIN,"models/SUSHI/"+"SUSHI_g.obj"));
		models = new IModelCustom[5];
		models[0] = model2;
		models[1] = model3;
		models[2] = model4;
		models[3] = model5;
		models[4] = model1;
	}
	
	float rotation;
	float prevRotation;
	
	public entitySUSHI(World world)
	{
		super(world);
		setSize(0.9f, 0.4f);
		
	}
	public entitySUSHI(World world, double posX, double posY, double posZ)
	{
		this(world);
		setPosition(posX, posY, posZ);
	}
	
	@Override
	protected void entityInit()
	{
		Random r = new Random();
		
		dataWatcher.addObject(20, new Float(0f));
//		dataWatcher.addObject(21, new Integer(1));
		dataWatcher.addObject(19, new Integer((int) Math.floor(r.nextInt(44)/10d)));
	}
	
	public AxisAlignedBB getBoundingBox()
    {
        return boundingBox;
    }
	
	@Override
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }
	
	@Override
    public boolean attackEntityFrom(DamageSource ds, float p_70097_2_)
    {
    	boolean flag = ds.getEntity() instanceof EntityPlayer;

	    if (flag)
	    {
	        setDead();
	        boolean flag1 = ((EntityPlayer)ds.getEntity()).capabilities.isCreativeMode;
	        if(!flag1 && !worldObj.isRemote)entityDropItem(new ItemStack(ERC_Core.ItemSUSHI,1,0), 0f);
	    }
	    
    	return false;
    }
	
	@Override
	public boolean interactFirst(EntityPlayer player)
	{
		if(player.isSneaking())
		{
			setRot(getRot()*1.1f);
		}
		else
		{
			if(getRot()==0)setRot(3.0f);
			else if(getRot()>0)setRot(-3.0f);
			else if(getRot()<0)setRot(0);
		}
		player.swingItem();
		return false;
	}
	
	public void onUpdate()
	{
//		setDead();
		prevRotation = rotation;
		rotation += getRot();
		ERC_MathHelper.fixrot(rotation, prevRotation);
	}

	public ResourceLocation getTexture()
	{
		return tex;
	}
	
	public void render(double x, double y, double z, float f)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x, (float)y-0.2f, (float)z);
		GL11.glRotatef(prevRotation+(rotation-prevRotation)*f, 0f, -1f, 0f);
// 		GL11.glRotatef(coaster.ERCPosMat.getFixedPitch(t),1f, 0f, 0f);
// 		GL11.glRotatef(coaster.ERCPosMat.getFixedRoll(t), 0f, 0f, 1f);

		GL11.glScalef(1.2f, 1.2f, 1.2f);
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(tex);
		models[getId()].renderAll();
		GL11.glPopMatrix();
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setRot(nbt.getFloat("speed"));
		int id = nbt.getInteger("modelid");
		if(id>0)setId(id);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setFloat("speed",getRot());
		nbt.setInteger("modelid",getId());
	}

	public float getRot()
	{
		return dataWatcher.getWatchableObjectFloat(20);
	}
	public void setRot(float rot)
	{
		dataWatcher.updateObject(20, Float.valueOf(rot));
	}
	
	public int getId()
	{
		return dataWatcher.getWatchableObjectInt(19);
	}
	public void setId(int rot)
	{
		dataWatcher.updateObject(19, Integer.valueOf(rot));
	}
}
