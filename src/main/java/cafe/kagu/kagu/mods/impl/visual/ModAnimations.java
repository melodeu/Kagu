/**
 * 
 */
package cafe.kagu.kagu.mods.impl.visual;

import cafe.kagu.kagu.eventBus.Handler;
import cafe.kagu.kagu.eventBus.EventHandler;
import cafe.kagu.kagu.eventBus.impl.EventCheatRenderTick;
import cafe.kagu.kagu.eventBus.impl.EventRenderItem;
import cafe.kagu.kagu.eventBus.impl.EventTick;
import cafe.kagu.kagu.mods.Module;
import cafe.kagu.kagu.settings.impl.BooleanSetting;
import cafe.kagu.kagu.settings.impl.DoubleSetting;
import cafe.kagu.kagu.settings.impl.ModeSetting;
import cafe.kagu.kagu.utils.ChatUtils;
import cafe.kagu.kagu.utils.SpoofUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.EnumAction;
import net.minecraft.util.MathHelper;

/**
 * @author lavaflowglow
 *
 */
public class ModAnimations extends Module {

	public ModAnimations() {
		super("Animations", Category.VISUAL);
		setSettings(blockAnimations, itemScaleX, itemScaleY, itemScaleZ, itemTranslateX, itemTranslateY, itemTranslateZ);
	}
	
	// Block animations
	public ModeSetting blockAnimations = new ModeSetting("Animation", "1.7", "1.7", "Orbit", "Spin", "Lollipop", "Slash", "Tap", "Wiggle", "Swipe", "Bump", "Float", "None", "Test");
	
	// Item scale
	public DoubleSetting itemScaleX = new DoubleSetting("Item scale x", 1, -2, 2, 0.05);
	public DoubleSetting itemScaleY = new DoubleSetting("Item scale y", 1, -2, 2, 0.05);
	public DoubleSetting itemScaleZ = new DoubleSetting("Item scale z", 1, -2, 2, 0.05);
	
	// Item translate
	public DoubleSetting itemTranslateX = new DoubleSetting("Item translate x", 0, -2, 2, 0.001);
	public DoubleSetting itemTranslateY = new DoubleSetting("Item translate y", 0, -2, 2, 0.001);
	public DoubleSetting itemTranslateZ = new DoubleSetting("Item translate z", 0, -2, 2, 0.001);
	
	@EventHandler
	private Handler<EventRenderItem> renderItem = e -> {
		if (e.isPost())
			return;
		
		setInfo(blockAnimations.getMode());
		ItemRenderer ir = mc.getItemRenderer();
		
		// Scaling and translations
		GlStateManager.scale(itemScaleX.getValue(), itemScaleY.getValue(), itemScaleZ.getValue());
		GlStateManager.translate(itemTranslateX.getValue(), itemTranslateY.getValue(), itemTranslateZ.getValue());
		
		// Sword block animations
		if (!blockAnimations.is("None") && e.getAction() == EnumAction.BLOCK && (mc.thePlayer.isUsingItem() || SpoofUtils.isSpoofBlocking())) {
			e.setAction(EnumAction.CUSTOMBLOCK);
			
			switch (blockAnimations.getMode()) {
			
			case "1.7":{
				ir.transformFirstPersonItem(e.getEquipProgress(), e.getSwingProgress());
				ir.transformFirstPersonBlock();
			}break;
			
			case "Orbit":{
				GlStateManager.rotate(e.getSwingProgress() * 360, 0, 1, 0);
				ir.transformFirstPersonItem(e.getEquipProgress(), 0);
				ir.transformFirstPersonBlock();
			}break;
			
			case "Spin":{
				GlStateManager.rotate(e.getSwingProgress() * 360, 0, 0, 1);
				ir.transformFirstPersonItem(e.getEquipProgress(), 0);
				ir.transformFirstPersonBlock();
			}break;
			
			case "Lollipop":{
				ir.transformFirstPersonItem(e.getEquipProgress(), e.getSwingProgress());
				ir.transformFirstPersonBlock();
				ir.transformFirstPersonBlock();
			}break;
			
			case "Slash":{
				ir.func_178105_d(e.getSwingProgress());
				ir.transformFirstPersonItem(e.getEquipProgress(), e.getSwingProgress());
				ir.transformFirstPersonBlock();
			}break;
			
			case "Tap":{
				ir.transformFirstPersonItem(e.getEquipProgress(), -e.getSwingProgress());
				ir.transformFirstPersonBlock();
			}break;
			
			case "Wiggle":{
				ir.transformFirstPersonItem(-(e.getSwingProgress() < 0.5 ? e.getSwingProgress() : (1 - e.getSwingProgress())) * 0.5f, e.getSwingProgress());
				ir.transformFirstPersonBlock();
			}break;
			
			case "Swipe":{
				ir.transformFirstPersonItem(e.getEquipProgress(), (e.getSwingProgress() < 0.5 ? e.getSwingProgress() : (1 - e.getSwingProgress())) * 0.5f);
				ir.transformFirstPersonBlock();
			}break;
			
			case "Bump":{
				ir.transformFirstPersonItem(-(e.getSwingProgress() < 0.5 ? e.getSwingProgress() : (1 - e.getSwingProgress())) * 0.5f, (e.getSwingProgress() < 0.5 ? e.getSwingProgress() : (1 - e.getSwingProgress())) * 0.15f);
				ir.transformFirstPersonBlock();
			}break;
			
			case "Float":{
		        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
		        GlStateManager.translate(0.0F, e.getEquipProgress() * -0.6F, 0.0F);
		        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
		        float f = MathHelper.sin(e.getSwingProgress() * e.getSwingProgress() * (float)Math.PI);
		        float f1 = MathHelper.sin(MathHelper.sqrt_float(e.getSwingProgress()) * (float)Math.PI);
		        f = -0.2f * f;
		        f1 = -0.2f * f1;
		        GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
		        GlStateManager.rotate(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
		        GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
		        GlStateManager.scale(0.4F, 0.4F, 0.4F);
		        ir.transformFirstPersonBlock();
			}break;
			
			case "Test":{
				ir.transformFirstPersonItem(e.getEquipProgress(), e.getSwingProgress());
				ir.transformFirstPersonBlock();
			}break;
			
			default:{}break;
			}
			
		}
		
	};
	
}
