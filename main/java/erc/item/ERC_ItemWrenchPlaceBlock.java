package erc.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erc._core.ERC_CONST;
import erc.message.ERC_MessageItemWrenchSync;
import erc.message.ERC_PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ERC_ItemWrenchPlaceBlock extends Item {

	protected IIcon temIcon;
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
	{
		if(world.isRemote)
		{
			Block placedBlock = Blocks.dirt;
			Item placedBlockItem = Item.getItemFromBlock(placedBlock);
			
			boolean iscreative = player.capabilities.isCreativeMode;
			if(!player.inventory.hasItem(placedBlockItem) && !iscreative)return itemstack;
	
			double pit = Math.cos(Math.toRadians(player.rotationPitch));
			int x = (int) Math.floor(player.posX - Math.sin(Math.toRadians(player.rotationYaw))*2*pit);
			int y = (int) Math.floor(player.posY - Math.sin(Math.toRadians(player.rotationPitch))*2);
			int z = (int) Math.floor(player.posZ + Math.cos(Math.toRadians(player.rotationYaw))*2*pit);
			
			if (!world.canPlaceEntityOnSide(placedBlock, x, y, z, false, x, player, itemstack))return itemstack;
			
			// ブロックを設置できるかチェック
			boolean canPlaceBlock = (world.getBlock(x, y, z) == Blocks.air) || (world.getBlock(x, y, z) == Blocks.water) || (world.getBlock(x, y, z) == Blocks.flowing_water);
			
			if(canPlaceBlock)
			{
	    		if(!iscreative)player.inventory.consumeInventoryItem(placedBlockItem);
	    		world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), Blocks.dirt.stepSound.func_150496_b(), (Blocks.dirt.stepSound.getVolume() + 1.0F) / 2.0F, Blocks.dirt.stepSound.getPitch() * 0.8F);	
	    		player.swingItem();
	    		ERC_PacketHandler.INSTANCE.sendToServer(new ERC_MessageItemWrenchSync(2,x,y,z));
			}
			return itemstack;
			
		}
		return super.onItemRightClick(itemstack, world, player);
	}
	
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, Block block)
    {
		if( (world.getBlock(x, y, z) != Blocks.air) )return false;
    	if (!world.setBlock(x, y, z, block, 0, 0))
    	{
    		return false;
    	}
//    	ERC_Logger.info("place block");
    	if (world.getBlock(x, y, z) == block)
    	{
    		block.onBlockPlacedBy(world, x, y, z, player, stack);
    		block.onPostBlockPlaced(world, x, y, z, 0);
    		world.markBlockForUpdate(x, y, z);
    	}
    	return true;
    }
	
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ)
	{
		Block placedBlock = Blocks.dirt;
		Item placedBlockItem = Item.getItemFromBlock(placedBlock);
		
		boolean iscreative = player.capabilities.isCreativeMode;
		if(!player.inventory.hasItem(placedBlockItem) && !iscreative)return false;

		double pit = Math.cos(Math.toRadians(player.rotationPitch));
		x = (int) Math.floor(player.posX - Math.sin(Math.toRadians(player.rotationYaw))*2*pit);
		y = (int) Math.floor(player.posY - Math.sin(Math.toRadians(player.rotationPitch))*2);
		z = (int) Math.floor(player.posZ + Math.cos(Math.toRadians(player.rotationYaw))*2*pit);
		
		if (!world.canPlaceEntityOnSide(placedBlock, x, y, z, false, x, player, player.getHeldItem()))return false;
		
		// ブロックを設置できるかチェック
		boolean canPlaceBlock = (world.getBlock(x, y, z) == Blocks.air) || (world.getBlock(x, y, z) == Blocks.water) || (world.getBlock(x, y, z) == Blocks.flowing_water);
		
		if(canPlaceBlock)
		{
    		if(!iscreative)player.inventory.consumeInventoryItem(placedBlockItem);
    		world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), Blocks.dirt.stepSound.func_150496_b(), (Blocks.dirt.stepSound.getVolume() + 1.0F) / 2.0F, Blocks.dirt.stepSound.getPitch() * 0.8F);	

    		ERC_PacketHandler.INSTANCE.sendToServer(new ERC_MessageItemWrenchSync(2,x,y,z));
		}
        return true;
	}

	
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ)
	{
		return false;
	}
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister p_94581_1_)
    {
		this.itemIcon = p_94581_1_.registerIcon(ERC_CONST.DOMAIN+":"+"wrench_p");
    }

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int p_77617_1_) 
	{
		return itemIcon;
	}

	@Override
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) 
	{
		return itemIcon;
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) 
	{
		return itemIcon;
	}
    
}
