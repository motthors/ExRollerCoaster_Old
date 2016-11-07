package erc.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class ERC_TickEventHandler {
	
	private static int tickcounter = 0;
	@SubscribeEvent
	public void onTickEvent(TickEvent.ServerTickEvent event)
	{
		if (event.phase == TickEvent.Phase.START) 
		{
			setTickcounter(getTickcounter() + 1);
//			ERC_ManagerPrevTickCoasterSeatSetPos_server.update();
		}
		if (event.phase == TickEvent.Phase.END)
		{
			
		}
	}

	public static int getTickcounter() {
		return tickcounter;
	}

	public static void setTickcounter(int tickcounter) {
		ERC_TickEventHandler.tickcounter = tickcounter;
	}
	
}
