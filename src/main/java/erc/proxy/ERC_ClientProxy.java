package erc.proxy;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import erc._core.ERC_CONST;
import erc._core.ERC_Core;
import erc.entity.ERC_EntityCoaster;
import erc.entity.ERC_EntityCoasterConnector;
import erc.entity.ERC_EntityCoasterDoubleSeat;
import erc.entity.ERC_EntityCoasterMonodentate;
import erc.entity.ERC_EntityCoasterSeat;
import erc.entity.entitySUSHI;
import erc.handler.ERC_ClientTickEventHandler;
import erc.handler.ERC_InputEventHandler;
import erc.handler.ERC_RenderEventHandler;
import erc.handler.ERC_TickEventHandler;
import erc.manager.ERC_ModelLoadManager;
import erc.manager.ERC_ModelLoadPlan;
import erc.renderer.ERC_RenderEntityCoaster;
import erc.renderer.ERC_RenderTileEntityRailBase;
import erc.renderer.renderBlockRail;
import erc.renderer.renderEntityCoasterSeat;
import erc.renderer.renderEntitySUSIHI;
import erc.tileEntity.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class ERC_ClientProxy implements IProxy{
	
	@Override
	public int getNewRenderType()
	{
		return RenderingRegistry.getNextAvailableRenderId();
	}
	

	@Override
	public void preInit()
	{
//		FMLClientHandler.instance().addModAsResource( (new customResourceLoader() ).get());
		
		// �f�t�H�R�[�X�^�[�o�^
		String defaultModel = ERC_CONST.DOMAIN+":models/coaster.obj";
		String defaultModel_c = ERC_CONST.DOMAIN+":models/coaster_connect.obj";
		String defaultTex = ERC_CONST.DOMAIN+":textures/gui/gui.png";
		String defaultIcon_c = "erc:coaster_c";
		String defaultIcon = "erc:coaster";
		
		ERC_ModelLoadPlan main = new ERC_ModelLoadPlan(defaultModel, defaultTex, defaultIcon);
		ERC_ModelLoadPlan connect = new ERC_ModelLoadPlan(defaultModel_c, defaultTex, defaultIcon_c);
		ERC_ModelLoadPlan mono = new ERC_ModelLoadPlan(defaultModel, defaultTex, defaultIcon);
		ERC_ModelLoadPlan inv = new ERC_ModelLoadPlan(ERC_CONST.DOMAIN+":models/coaster_inv.obj", defaultTex, "erc:coaster_inv");
		main.setSeatOffset(0, 0f, 0.4f, 0f);
		connect.setSeatOffset(0, 0f, 0.4f, 0f);
		mono.setSeatOffset(0, 0f, 0.4f, 0f);
		
		main.setCoasterMainData(1.5f, 2.2f, 2.0f, true);
		connect.setCoasterMainData(1.5f, 1.0f, 3.0f, true);
		mono.setCoasterMainData(1.5f, 2.2f, 2.0f, true);
		
		main.setSeatSize(0, 0.8f);
		connect.setSeatSize(0, 0.8f);
		mono.setSeatSize(0, 0.8f);
		
		ERC_ModelLoadManager.registerCoaster(main);
		ERC_ModelLoadManager.registerConnectionCoaster(connect);
		ERC_ModelLoadManager.registerMonoCoaster(mono);
		
		inv.setCoasterMainData(1.6f, 1.0f, 2.0f, true);
		inv.setSeatOffset(0, 0, 2.0f, 0.6f);
		inv.setSeatRotation(0, 0, 0f, (float)Math.PI);
		ERC_ModelLoadManager.registerCoaster(inv);
		ERC_ModelLoadManager.registerConnectionCoaster(inv);
		
		///// ����
		ERC_ModelLoadPlan head = new ERC_ModelLoadPlan("erc:models/coaster_d.obj", defaultTex, "erc:coaster_d");
		ERC_ModelLoadPlan sub = new ERC_ModelLoadPlan("erc:models/coaster_d_c.obj", defaultTex, "erc:coaster_dc");
		head.setSeatNum(2);
		head.setCoasterMainData(1.9f, 3f, 3f, true);
		head.setSeatSize(0, 0.9f);
		head.setSeatSize(1, 0.9f);
		head.setSeatOffset(0, -0.5f, 0.5f, 0);
		head.setSeatOffset(1, 0.5f, 0.5f, 0);
		ERC_ModelLoadManager.registerCoaster(head);
		
		sub.setSeatNum(2);
		sub.setCoasterMainData(1.9f, 3f, 3f, true);
		sub.setSeatSize(0, 0.9f);
		sub.setSeatSize(1, 0.9f);
		sub.setSeatOffset(0, -0.5f, 0.5f, 0);
		sub.setSeatOffset(1, 0.5f, 0.5f, 0);
		ERC_ModelLoadManager.registerConnectionCoaster(sub);
		

		// TileEntityRender�̓o�^
		ERC_RenderTileEntityRailBase tileRenderer = new ERC_RenderTileEntityRailBase();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRailNormal.class, tileRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRailRedstoneAccelerator.class, tileRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRailConstVelosity.class, tileRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRailDetector.class, tileRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRailBranch2.class,tileRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRailInvisible.class, tileRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityNonGravityRail.class, tileRenderer);

		// Entity�`��o�^
		ERC_RenderEntityCoaster renderer = new ERC_RenderEntityCoaster();
		RenderingRegistry.registerEntityRenderingHandler(ERC_EntityCoaster.class, renderer);
		RenderingRegistry.registerEntityRenderingHandler(ERC_EntityCoasterMonodentate.class, renderer);
		RenderingRegistry.registerEntityRenderingHandler(ERC_EntityCoasterDoubleSeat.class, renderer);
		RenderingRegistry.registerEntityRenderingHandler(ERC_EntityCoasterConnector.class, renderer);
		RenderingRegistry.registerEntityRenderingHandler(ERC_EntityCoasterSeat.class, new renderEntityCoasterSeat());
		
		RenderingRegistry.registerEntityRenderingHandler(entitySUSHI.class, new renderEntitySUSIHI());
		
		// �u���b�N�J�X�^�������_�[�̓o�^
		RenderingRegistry.registerBlockHandler(new renderBlockRail());
		
		// Handler�̓o�^
		Minecraft mc = Minecraft.getMinecraft();
		ERC_Core.tickEventHandler = new ERC_ClientTickEventHandler(mc);
		FMLCommonHandler.instance().bus().register(new ERC_TickEventHandler());
		FMLCommonHandler.instance().bus().register(ERC_Core.tickEventHandler);
		MinecraftForge.EVENT_BUS.register(new ERC_InputEventHandler(mc));
		MinecraftForge.EVENT_BUS.register(new ERC_RenderEventHandler());
		
		// �X�V�̃��f���Ƃ�
		entitySUSHI.clientInitSUSHI();
	}

	@Override
	public void init() 
	{
		
	}

	@Override
	public void postInit() 
	{
		// �������ǉ����f���̃��[�h
		ERC_ModelLoadManager.init();
	}
}