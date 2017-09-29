package erc.renderer;

import org.lwjgl.opengl.GL11;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erc._core.ERC_Core;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;


@SideOnly(Side.CLIENT)
public class renderBlockRail implements ISimpleBlockRenderingHandler
{
	//インベントリ向け
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
	{
		if (modelId == this.getRenderId())
		{
			Tessellator tessellator = Tessellator.instance;

			//ココをいじるとブロックの大きさが変わる
			renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 0.4D, 1.0D);
			//描画位置の調整。ココをいじると、中心にレンダー持ってきたり、遊べる
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			//くコ:彡 、コピペ☆　RenderBlocksみてね
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, -1.0F, 0.0F);
			renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, metadata));
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 1.0F, 0.0F);
			renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, metadata));
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, -1.0F);
			renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, metadata));
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, 1.0F);
			renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, metadata));
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(-1.0F, 0.0F, 0.0F);
			renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, metadata));
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(1.0F, 0.0F, 0.0F);
			renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, metadata));
			tessellator.draw();
			//描画位置の調整。遊んだ後はお片づけ
			//上のヤツ→GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		}
	}
	
	//ワールドでのレンダー
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		if (modelId == this.getRenderId())
		{
			//ココをいじればブロックの大きさが変わる。
			//renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 0.4D, 1.0D);
			int meta = world.getBlockMetadata(x, y, z);
			switch(meta&7){
			case 0: // 上
				renderer.setRenderBounds(0.2F, 0.7F, 0.2F, 0.8F, 1.0F, 0.8F);
	            break;                                                       
			case 1: // 下                                                     
				renderer.setRenderBounds(0.2F, 0.0F, 0.2F, 0.8F, 0.3F, 0.8F);
	            break;                                                       
			case 2: // 南                                                     
				renderer.setRenderBounds(0.2F, 0.2F, 0.7F, 0.8F, 0.8F, 1.0F);
	            break;                                                       
			case 3: // 北                                                     
				renderer.setRenderBounds(0.2F, 0.2F, 0.0F, 0.8F, 0.8F, 0.3F);
	            break;                                                       
			case 4: // 東                                                     
				renderer.setRenderBounds(0.7F, 0.2F, 0.2F, 1.0F, 0.8F, 0.8F);
	            break;                                                       
			case 5: // 西                                                     
				renderer.setRenderBounds(0.0F, 0.2F, 0.2F, 0.3F, 0.8F, 0.8F);
	            break;                                                       
	        default:                                                         
	        	renderer.setRenderBounds(0.4F, 0.4F, 0.4F, 0.6F, 0.6F, 0.6F);
	            break;
			}
			renderer.renderStandardBlock(block, x, y, z);
			return true;
		}
		return false;
	}
	
	//インベントリのレンダーが面倒くさいなら、ココをfalseに。テクスチャだけ表示されるようになる
	@Override
	public boolean shouldRender3DInInventory(int modelId)
	{
		return true;
	}

	//レンダーIDを返す
	@Override
	public int getRenderId()
	{
		return ERC_Core.blockRailRenderId;
	}

}