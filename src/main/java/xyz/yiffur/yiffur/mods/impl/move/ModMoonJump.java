/**
 * 
 */
package xyz.yiffur.yiffur.mods.impl.move;

import net.minecraft.client.Minecraft;
import xyz.yiffur.yiffur.eventBus.Event;
import xyz.yiffur.yiffur.eventBus.Subscriber;
import xyz.yiffur.yiffur.eventBus.YiffEvents;
import xyz.yiffur.yiffur.eventBus.impl.EventTick;
import xyz.yiffur.yiffur.mods.Module;

/**
 * @author lavaflowglow
 *
 */
public class ModMoonJump extends Module {
	
	public ModMoonJump() {
		super("MoonJump", Category.MOVEMENT);
	}
	
	@YiffEvents
	public Subscriber<EventTick> tickStrafeEvent = e -> {
		if (e.isPre()) {
			Minecraft.getMinecraft().thePlayer.motionY += 0.05;
		}
	};
	
}