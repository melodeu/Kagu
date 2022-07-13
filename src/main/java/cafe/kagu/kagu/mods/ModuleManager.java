/**
 * 
 */
package cafe.kagu.kagu.mods;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cafe.kagu.kagu.eventBus.EventBus;
import cafe.kagu.kagu.mods.impl.combat.ModBacktrack;
import cafe.kagu.kagu.mods.impl.combat.ModHitboxes;
import cafe.kagu.kagu.mods.impl.combat.ModKillAura;
import cafe.kagu.kagu.mods.impl.combat.ModReach;
import cafe.kagu.kagu.mods.impl.exploit.ModAntiCrash;
import cafe.kagu.kagu.mods.impl.exploit.ModBlink;
import cafe.kagu.kagu.mods.impl.exploit.ModCrasher;
import cafe.kagu.kagu.mods.impl.exploit.ModCreative64Stack;
import cafe.kagu.kagu.mods.impl.exploit.ModNoHCollisionSlowdown;
import cafe.kagu.kagu.mods.impl.exploit.ModTimer;
import cafe.kagu.kagu.mods.impl.move.ModFly;
import cafe.kagu.kagu.mods.impl.move.ModMoonJump;
import cafe.kagu.kagu.mods.impl.move.ModSpeed;
import cafe.kagu.kagu.mods.impl.move.ModSprint;
import cafe.kagu.kagu.mods.impl.player.ModAntiAim;
import cafe.kagu.kagu.mods.impl.player.ModDisabler;
import cafe.kagu.kagu.mods.impl.visual.ModAmbience;
import cafe.kagu.kagu.mods.impl.visual.ModAnimations;
import cafe.kagu.kagu.mods.impl.visual.ModDistastefulEars;
import cafe.kagu.kagu.mods.impl.visual.ModChestEsp;
import cafe.kagu.kagu.mods.impl.visual.ModClickGui;
import cafe.kagu.kagu.mods.impl.visual.ModEsp;
import cafe.kagu.kagu.mods.impl.visual.ModHud;
import cafe.kagu.kagu.mods.impl.visual.ModIndicators;
import cafe.kagu.kagu.mods.impl.visual.ModNormalZoomCam;
import cafe.kagu.kagu.mods.impl.visual.ModViewModels;
import cafe.kagu.kagu.mods.impl.yiff.ModDebugBoundingBoxes;
import cafe.kagu.kagu.mods.impl.yiff.ModTest;

/**
 * @author lavaflowglow
 *
 */
public class ModuleManager {

	private static Logger logger = LogManager.getLogger();
	
	// All the modules in the client
	
	// Combat
	public static ModKillAura modKillAura = new ModKillAura();
	public static ModBacktrack modBacktrack = new ModBacktrack();
	public static ModReach modReach = new ModReach();
	public static ModHitboxes modHitboxes = new ModHitboxes();
	
	// Movement
	public static ModMoonJump modMoonJump = new ModMoonJump();
	public static ModSprint modSprint = new ModSprint();
	public static ModSpeed modSpeed = new ModSpeed();
	public static ModFly modFly = new ModFly();
	
	// Player
	public static ModAntiAim modAntiAim = new ModAntiAim();
	public static ModDisabler modDisabler = new ModDisabler();
	
	// Visual
	public static ModClickGui modClickGui = new ModClickGui();
	public static ModHud modHud = new ModHud();
	public static ModViewModels modViewModels = new ModViewModels();
	public static ModAnimations modAnimations = new ModAnimations();
	public static ModChestEsp modChestEsp = new ModChestEsp();
	public static ModEsp modEsp = new ModEsp();
	public static ModNormalZoomCam modNormalZoomCam = new ModNormalZoomCam();
	public static ModIndicators modIndicators = new ModIndicators();
	public static ModDistastefulEars modDistastefulEars = new ModDistastefulEars();
	public static ModAmbience modAmbience = new ModAmbience();
	
	// Exploit
	public static ModCreative64Stack modCreative64Stack = new ModCreative64Stack();
	public static ModAntiCrash modAntiCrash = new ModAntiCrash();
	public static ModTimer modTimer = new ModTimer();
	public static ModBlink modBlink = new ModBlink();
	public static ModCrasher modCrasher = new ModCrasher();
	public static ModNoHCollisionSlowdown modNoHCollisionSlowdown = new ModNoHCollisionSlowdown();
	
	// Development
	public static ModTest modTest = new ModTest();
	public static ModDebugBoundingBoxes modDebugBoundingBoxes = new ModDebugBoundingBoxes();
	
	// An array of all the modules in the client
	private static final Module[] MODULES = new Module[] {
			modClickGui,
			modAntiAim,
			modHud,
			modAnimations,
			modChestEsp,
			modEsp,
			modNormalZoomCam,
			modMoonJump,
			modSprint,
			modDisabler,
			modCreative64Stack,
			modDebugBoundingBoxes,
			modTest,
			modAntiCrash,
			modViewModels,
			modKillAura,
			modBacktrack,
			modTimer,
			modCrasher,
			modReach,
			modHitboxes,
			modIndicators,
			modNoHCollisionSlowdown,
			modSpeed,
			modDistastefulEars,
			modAmbience,
			modFly,
			
			modBlink
	};

	/**
	 * Called at the start of the client
	 */
	public static void start() {
		logger.info("Loading modules...");
		
		for (Module module : MODULES) {
			EventBus.setSubscriber(module, true); // Subscribe any listeners to the event bus
		}
		
		logger.info("Loaded all the modules");
	}

	/**
	 * @return the modules
	 */
	public static Module[] getModules() {
		return MODULES;
	}

}
