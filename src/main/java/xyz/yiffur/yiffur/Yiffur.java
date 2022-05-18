package xyz.yiffur.yiffur;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import xyz.yiffur.yiffur.commands.CommandManager;
import xyz.yiffur.yiffur.eventBus.EventBus;
import xyz.yiffur.yiffur.eventBus.Event.EventPosition;
import xyz.yiffur.yiffur.eventBus.impl.EventCheatTick;
import xyz.yiffur.yiffur.font.FontUtils;
import xyz.yiffur.yiffur.managers.FileManager;
import xyz.yiffur.yiffur.managers.KeybindManager;
import xyz.yiffur.yiffur.mods.ModuleManager;
import xyz.yiffur.yiffur.ui.GuiMainMenu;
import xyz.yiffur.yiffur.ui.Hud;
import xyz.yiffur.yiffur.ui.clickgui.GuiClickgui;

/**
 * @author lavaflowglow
 *
 */
public class Yiffur {
	
	private static String name = "Yiffur";
	private static double version = 0;
	
	private static Logger logger = LogManager.getLogger();
	
//	public static final char UNIT_SEPARATOR = (char)31;
//	public static final char RECORD_SEPARATOR = (char)30;
//	public static final char GROUP_SEPARATOR = (char)29;
	
	public static final String UNIT_SEPARATOR = "﷽";
	public static final String RECORD_SEPARATOR = "👺";
	public static final String GROUP_SEPARATOR = "🐀";
	
	/**
	 * The start method, everything should be initialized here
	 */
	public static void start() {
		
		logger.info("Starting Yiffur :3");
		
		// Starts the event bus
		logger.info("Starting the event bus...");
		EventBus.start();
		logger.info("Started the event bus");
		
		// Start the module manager
		logger.info("Starting the module manager...");
		ModuleManager.start();
		logger.info("Started the module manager");
		
		// Start the command manager
		logger.info("Starting the command manager...");
		CommandManager.start();
		logger.info("Started the command manager");
		
		// Start the file manager
		logger.info("Starting the file manager...");
		FileManager.start();
		logger.info("Started the file manager");
		
		// Start the file manager
		logger.info("Starting the keybind manager...");
		KeybindManager.start();
		logger.info("Started the keybind manager");
		
		// Start the clickgui
		logger.info("Starting the clickgui...");
		GuiClickgui.getInstance().start();
		logger.info("Started the clickgui");
		
		// Start a cheat loop thread
		logger.info("Starting the cheat loop thread...");
		Thread cheatThread = new Thread(() -> {
			double tps = 64;
			while (true) {
				try {
					Thread.sleep((long) (1000 / tps));
				} catch (Exception e) {
					// TODO: handle exception
				}
				// Yiffur hook
				{
					EventCheatTick eventCheatTick = new EventCheatTick(EventPosition.PRE);
					eventCheatTick.post();
					if (eventCheatTick.isCanceled())
						continue;
				}
				// Yiffur hook
				{
					EventCheatTick eventCheatTick = new EventCheatTick(EventPosition.POST);
					eventCheatTick.post();
					if (eventCheatTick.isCanceled())
						continue;
				}
			}
		}, "Cheat loop");
		cheatThread.setDaemon(true);
		cheatThread.start();
		logger.info("Started the cheat loop thread");
		
		// Load fonts
		logger.info("Loading fonts...");
		FontUtils.start();
		logger.info("Loaded fonts");
		
		// Load the main menu
		logger.info("Loading the main menu...");
		GuiMainMenu.start();
		logger.info("Loaded the main menu");
		
		// Hook the hud
		logger.info("Hooking the hud...");
		EventBus.setSubscriber(new Hud(), true);
		logger.info("Hooked the hud");
		
		logger.info("Yiffur has been started");
		
	}

	/**
	 * @return the name
	 */
	public static String getName() {
		return name;
	}

	/**
	 * @return the version
	 */
	public static double getVersion() {
		return version;
	}

}
