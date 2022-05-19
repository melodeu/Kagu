/**
 * 
 */
package cafe.kagu.kagu.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cafe.kagu.kagu.Kagu;
import cafe.kagu.kagu.eventBus.EventBus;
import cafe.kagu.kagu.eventBus.Subscriber;
import cafe.kagu.kagu.eventBus.EventHandler;
import cafe.kagu.kagu.eventBus.impl.EventKeyUpdate;
import cafe.kagu.kagu.mods.Module;
import cafe.kagu.kagu.mods.ModuleManager;

/**
 * @author lavaflowglow
 *
 */
public class KeybindManager {
	
	/**
	 * Private because I don't want anything else creating an instance of this class
	 */
	private KeybindManager() {}
	
	private static Map<String, Integer[]> keybinds = new HashMap<>();
	
	/**
	 * Called when the client starts
	 */
	public static void start() {
		
		// Load the default keybinds if they exist
		if (FileManager.DEFAULT_KEYBINDS.exists()) {
			load(FileManager.DEFAULT_KEYBINDS);
		}
		
		// Subscribe to the key event so we can use the keybinds
		EventBus.setSubscriber(new KeybindManager(), true);
		
	}
	
	/**
	 * Adds a keybind
	 * @param module The name of the module
	 * @param keyCode The keycode to bind
	 */
	public static void addKeybind(String module, int keyCode) {
		module = module.toLowerCase();
		List<Integer> binds = new ArrayList<>(Arrays.asList(getKeybinds(module)));
		if (binds.contains(keyCode))
			return;
		binds.add(keyCode);
		if (keybinds.containsKey(module)) {
			keybinds.replace(module, binds.toArray(new Integer[0]));
		}else {
			keybinds.put(module, binds.toArray(new Integer[0]));
		}
		save(FileManager.DEFAULT_KEYBINDS);
	}
	
	/**
	 * Removes all keybinds for a module
	 * @param module The name of the module
	 */
	public static void removeKeybind(String module) {
		module = module.toLowerCase();
		keybinds.remove(module);
		save(FileManager.DEFAULT_KEYBINDS);
	}
	
	/**
	 * Returns an array of keybinds for the module
	 * @param module The name of the module
	 * @return
	 */
	public static Integer[] getKeybinds(String module) {
		module = module.toLowerCase();
		List<Integer> binds = new ArrayList<>();
		try {
			binds.addAll(Arrays.asList(keybinds.get(module)));
		} catch (Exception e) {}
		
		return binds.toArray(new Integer[0]);
	}
	
	/**
	 * Saves the keybinds to a file
	 * @param saveFile The file to save them to
	 */
	public static void save(File saveFile) {
		String save = "";
		for (String module : keybinds.keySet()) {
			save += (save.isEmpty() ? "" : Kagu.UNIT_SEPARATOR) + module + Kagu.GROUP_SEPARATOR;
			String binds = "";
			for (Integer bind : getKeybinds(module)) {
				binds += (binds.isEmpty() ? "" : Kagu.RECORD_SEPARATOR) + bind;
			}
			save += binds;
		}
		FileManager.writeStringToFile(saveFile, save);
	}
	
	/**
	 * Saves the keybinds from a file
	 * @param file The file to load them from
	 */
	public static void load(File file) {
		String fileData = FileManager.readStringFromFile(file);
		if (fileData.isEmpty())
			return;
		keybinds.clear();
		for (String bind : fileData.split(Kagu.UNIT_SEPARATOR)) {
			try {
				String[] bindArray = bind.split(Kagu.GROUP_SEPARATOR);
				
				List<Integer> keyCodes = new ArrayList<>();
				for (String code : bindArray[1].split(Kagu.RECORD_SEPARATOR)) {
					keyCodes.add(Integer.valueOf(code));
				}
				
				keybinds.put(bindArray[0], keyCodes.toArray(new Integer[0]));
			} catch (Exception e) {
				
			}
		}
	}
	
	/**
	 * Listener for the keybinds
	 */
	@EventHandler
	private Subscriber<EventKeyUpdate> subscriber = e -> {
		if (e.isPost() || !e.isPressed())
			return;
		
		int keyCode = e.getKeyCode();
		for (Module module : ModuleManager.getModules()) {
			if (Arrays.asList(getKeybinds(module.getName())).contains(keyCode)) {
				module.toggle();
			}
		}
		
	};
	
}