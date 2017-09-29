package erc.item;

import erc.entity.entitySUSHI;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class itemSUSHI extends Item{

	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_)
    {
    	
    	if (!world.isRemote)
    	{
    		Entity e = new entitySUSHI(world,x+0.5,y+0.8,z+0.5);
//    		Entity e = new entityPartsTestBase(world,x+0.5,y+1.5,z+0.5);
    		world.spawnEntityInWorld(e);
    	}
    	--itemStack.stackSize;
    	return true;
    }
}
	