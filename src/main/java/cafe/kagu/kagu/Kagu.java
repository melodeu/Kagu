package cafe.kagu.kagu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL13;

import cafe.kagu.kagu.commands.CommandManager;
import cafe.kagu.kagu.eventBus.EventBus;
import cafe.kagu.kagu.eventBus.Event.EventPosition;
import cafe.kagu.kagu.eventBus.impl.EventCheatTick;
import cafe.kagu.kagu.font.FontUtils;
import cafe.kagu.kagu.managers.AltManager;
import cafe.kagu.kagu.managers.FileManager;
import cafe.kagu.kagu.managers.KeybindManager;
import cafe.kagu.kagu.managers.SessionManager;
import cafe.kagu.kagu.mods.ModuleManager;
import cafe.kagu.kagu.ui.Hud;
import cafe.kagu.kagu.ui.clickgui.GuiCsgoClickgui;
import cafe.kagu.kagu.ui.gui.GuiDefaultMainMenu;
import cafe.kagu.kagu.ui.gui.MainMenuHandler;
import cafe.kagu.kagu.utils.InventoryUtils;
import cafe.kagu.kagu.utils.MovementUtils;
import cafe.kagu.kagu.utils.OSUtil;
import cafe.kagu.kagu.utils.RotationUtils;
import cafe.kagu.kagu.utils.SpoofUtils;
import cafe.kagu.kagu.utils.StencilUtil;
import cafe.kagu.kagu.utils.WorldUtils;

/**
 * @author lavaflowglow
 *
 */
public class Kagu {
	
	private static String name = "Kagu";
	private static double version = 0.3621;
	
	private static Logger logger = LogManager.getLogger();
	
//	public static final char UNIT_SEPARATOR = (char)31;
//	public static final char RECORD_SEPARATOR = (char)30;
//	public static final char GROUP_SEPARATOR = (char)29;
	
	public static final String UNIT_SEPARATOR = "﷽";
	public static final String GROUP_SEPARATOR = "🐀";
	public static final String RECORD_SEPARATOR = "👺";
	
	private static boolean destroyDisplay = false;
	
	private static int activeTexture = GL13.GL_TEXTURE0;
	
	// Only used if the font texture size is greater than the size limit
	public static final char[] FONT_RENDERER_SUPPORTED_CHARACTERS = new char[] {
			'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 
			'q', 'w', 'e', 'r', 't', 'y', 'y', 'u', 'i', 'o', 'p', 
			'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 
			'z', 'x', 'c', 'v', 'b', 'n', 'm', 
			'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', 
			'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 
			'Z', 'X', 'C', 'V', 'B', 'N', 'M', 
			'`', '~', '!', '#', '$', '%', '^', '&', '*', '(', ')', '-', '_', '=', '+', 
			'[', '{', '}', ']', '\\', '|', 
			';', ':', '\'', '"', 
			'<', '>', ',', '.', '/', '?', ' '
	};
	
	/**
	 * The start method, everything should be initialized here
	 */
	public static void start() {
		
		// winpcap check
		if (OSUtil.isWindows() && System.getProperty("user.language").equalsIgnoreCase("en")) {
			
			new Thread(() -> {
				while (true) {
					JFrame frame = new JFrame("Please disable winpcap");
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setResizable(false);
					frame.setSize(500, 200);
					frame.setAlwaysOnTop(true);
					JLabel label = new JLabel("Please disable winpcap (https://tinyurl.com/kagucap) and restart the client");
					
					t:
					try {
						Process proc = Runtime.getRuntime().exec(new String[]{"cmd", "/c", "net stop npf"});
						BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
						BufferedReader error = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
						String out = null;
						Thread.sleep(250);
						while ((out = input.readLine()) != null) {
							if (out.equalsIgnoreCase("The NetGroup Packet Filter Driver service is not started.")) {
								break t;
							}
						}
						while ((out = error.readLine()) != null) {
							if (out.equalsIgnoreCase("The NetGroup Packet Filter Driver service is not started.")) {
								break t;
							}
						}
						frame.add(label);
						frame.setVisible(true);
						destroyDisplay = true;
						EventBus.bootAll();
						while (true);
					} catch (IOException | InterruptedException e) {
						label.setText("Error occurred while preforming winpcap checks, please restart");
						frame.add(label);
						frame.setVisible(true);
						Display.destroy();
						EventBus.bootAll();
						while (true);
					}
					try {
						Thread.sleep(3000);
					}catch (Exception e){
						
					}
				}
			}).start();
			
		}
		
		logger.info("Starting " + name + " v" + version + " :3");
		
		// Starts the event bus
		logger.info("Starting the event bus...");
		EventBus.start();
		logger.info("Started the event bus");
		
		// Start the stencil util
		logger.info("Starting the stencil util...");
		StencilUtil.start();
		logger.info("Started the stencil util");
		
		// Start the spoof utils
		logger.info("Starting the spoof utils...");
		SpoofUtils.start();
		logger.info("Started the spoof utils");
		
		// Start the file manager
		logger.info("Starting the file manager...");
		FileManager.start();
		logger.info("Started the file manager");
		
		// Start the module manager
		logger.info("Starting the module manager...");
		ModuleManager.start();
		logger.info("Started the module manager");
		
		// Start the command manager
		logger.info("Starting the command manager...");
		CommandManager.start();
		logger.info("Started the command manager");
		
		// Start the keybind manager
		logger.info("Starting the keybind manager...");
		KeybindManager.start();
		logger.info("Started the keybind manager");
		
		// Start the alt manager
		logger.info("Starting the alt manager...");
		AltManager.start();
		logger.info("Started the alt manager");
		
		// Start the session manager
		logger.info("Starting the session manager...");
		SessionManager.start();
		logger.info("Started the session manager");
		
		// Start the clickgui
		logger.info("Starting the clickgui...");
		GuiCsgoClickgui.getInstance().start();
		logger.info("Started the clickgui");
		
		// Load fonts
		logger.info("Loading fonts...");
		FontUtils.start();
		logger.info("Loaded fonts");
		
		// Load the main menu
		logger.info("Loading the main menu handler...");
		MainMenuHandler.getMainMenu();
		logger.info("Loaded the main menu handler");
		
		// Load the main menu
		logger.info("Loading the main menus...");
		GuiDefaultMainMenu.start();
		logger.info("Loaded the main menus");
		
		// Load the movement utils
		logger.info("Loading the movement utils...");
		MovementUtils.start();
		logger.info("Loaded the movement utils");
		
		// Load the rotation utils
		logger.info("Loading the rotation utils...");
		RotationUtils.start();
		logger.info("Loaded the rotation utils");
		
		// Load the world utils
		logger.info("Loading the world utils...");
		WorldUtils.start();
		logger.info("Loaded the world utils");
		
		// Load the rotation utils
		logger.info("Loading the inventory utils...");
		InventoryUtils.start();
		logger.info("Loaded the inventory utils");
		
		// Hook the hud
		logger.info("Hooking the hud...");
		EventBus.setSubscriber(new Hud(), true);
		logger.info("Hooked the hud");
		
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
				// Kagu hook
				{
					EventCheatTick eventCheatTick = new EventCheatTick(EventPosition.PRE);
					eventCheatTick.post();
					if (eventCheatTick.isCanceled())
						continue;
				}
				// Kagu hook
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
		
		logger.info(name + " v" + version + " has been started");
		
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

	/**
	 * @return the activeTexture
	 */
	public static int getActiveTexture() {
		return activeTexture;
	}

	/**
	 * @param activeTexture the activeTexture to set
	 */
	public static void setActiveTexture(int activeTexture) {
		Kagu.activeTexture = activeTexture;
	}
	
	/**
	 * @return the destroyDisplay
	 */
	public static boolean isDestroyDisplay() {
		if (destroyDisplay && Display.isCreated()) {
			Display.destroy();
			EventBus.bootAll();
			while (true);
		}
		return destroyDisplay;
	}
	
}
