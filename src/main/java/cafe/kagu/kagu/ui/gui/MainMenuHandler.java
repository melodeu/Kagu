/**
 * 
 */
package cafe.kagu.kagu.ui.gui;

import net.minecraft.client.gui.GuiScreen;

/**
 * @author lavaflowglow
 *
 */
public class MainMenuHandler {
	
	/**
	 * Called when the client starts
	 */
	public static void start() {
		
	}
	
	public static MainMenu mainMenu;
	public static GuiScreen getMainMenu() {
		
		// If the main menu is null then return the main menu selection screen, not used at the moment
		if (mainMenu == null) {
			mainMenu = MainMenu.COMPACT;
		}
		
		return mainMenu.getMenu();
	}
	
	private enum MainMenu{
		
		COMPACT(new GuiCompactMainMenu());
		
		private final GuiScreen menu;
		
		/**
		 * @param The main menu associated with this type
		 */
		private MainMenu(GuiScreen menu) {
			this.menu = menu;
		}
		
		/**
		 * @return the menu
		 */
		public GuiScreen getMenu() {
			return menu;
		}
		
	}
	
}