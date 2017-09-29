package erc.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import erc._core.ERC_Core;
import erc.handler.ERC_TickEventHandler;

public class ERC_ServerProxy implements IProxy{
	
	@Override
	public int getNewRenderType()
	{
		return -1;
	}

	@Override
	public void preInit()
	{
		ERC_Core.tickEventHandler = new ERC_TickEventHandler();
		FMLCommonHandler.instance().bus().register(ERC_Core.tickEventHandler);
	}

	@Override
	public void init() {}

	@Override
	public void postInit() {}
}
