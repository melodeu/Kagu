/**
 * 
 */
package xyz.yiffur.yiffur.ui.clickgui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import xyz.yiffur.yiffur.eventBus.EventBus;
import xyz.yiffur.yiffur.eventBus.Subscriber;
import xyz.yiffur.yiffur.eventBus.YiffEvents;
import xyz.yiffur.yiffur.eventBus.impl.EventCheatTick;
import xyz.yiffur.yiffur.eventBus.impl.EventKeyUpdate;
import xyz.yiffur.yiffur.eventBus.impl.EventTick;
import xyz.yiffur.yiffur.font.FontRenderer;
import xyz.yiffur.yiffur.font.FontUtils;
import xyz.yiffur.yiffur.mods.Module;
import xyz.yiffur.yiffur.mods.ModuleManager;
import xyz.yiffur.yiffur.mods.Module.Category;
import xyz.yiffur.yiffur.utils.UiUtils;

/**
 * @author lavaflowglow
 *
 */
public class GuiClickgui extends GuiScreen {
	
	// Private because this is an a singleton class
	private GuiClickgui() {
		
	}
	
	private static GuiClickgui instance;
	/**
	 * @return The instance of the clickgui
	 */
	public static GuiClickgui getInstance() {
		if (instance == null) {
			instance = new GuiClickgui();
		}
		return instance;
	}
	
	/**
	 * Called when the client starts
	 */
	public void start() {
		EventBus.setSubscriber(this, true);
		
		// So we can calculate key presses
		degreesPerCategory = 360d / (double)Module.Category.values().length;
		{
			double degrees = 0;
			for (Category category : Category.values()) {
				Vector2d vector2d = new Vector2d(degrees, degrees + degreesPerCategory);
				categoryDegreesMap.put(category, vector2d);
				degrees += degreesPerCategory;
			}
		}
	}
	
	private double targetPosX = 0, targetPosY = 0, posX = 0, posY = 0, targetInnerCircleRadius = 0,
			innerCircleRadius = 0, targetOuterCircleRadius = 0, outerCircleRadius = 0, degreesPerCategory = 0,
			selectedScale = 1.075, boxSize = 0;
	private Map<Category, Vector2d> categoryDegreesMap = new HashMap<>();
	private Vector4d circleInnerSelectionColor = new Vector4d(1, 1, 1, 0.65),
			circleOuterSelectionColor = new Vector4d(1, 1, 1, 0.005),
			circleInnerBackgroundColor = new Vector4d(0.0784313725, 0.0784313725, 0.0784313725, 0.5),
			circleOuterBackgroundColor = circleInnerBackgroundColor,
			circleOutlineColor = new Vector4d(0.490196078, 0.490196078, 0.490196078, 1);
	private Category selectedCategory = null, lastHoveredCategory = null; // I am lazy so I'll just reuse the calculations from the render for the mouse click with this variable
	private Map<Category, Double> categorySliceScale = new HashMap<>();
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		
		// Draw all the category slices
		for (Category category : Category.values()) {
			
			if (!categorySliceScale.containsKey(category)) {
				categorySliceScale.put(category, Double.valueOf(1));
			}
			
			Vector2d angles = categoryDegreesMap.get(category);
			
			// Fill the white background of the slice
			drawSlice(category, mouseX, mouseY, angles, circleInnerBackgroundColor, circleOuterBackgroundColor);
			
			// Draw an outline around the slice
			drawSlice(category, mouseX, mouseY, angles, circleOutlineColor, circleOutlineColor, true);
			
			// Draw the category name
			FontRenderer categoryFr = FontUtils.STRATUM2_MEDIUM_13;
			{
				double rot = (angles.getX() + angles.getY()) * 0.5;
				double textSpacing = 0.65;
				double textPosX = posX + Math.cos(Math.toRadians(rot)) * ((outerCircleRadius * categorySliceScale.get(category)) * textSpacing);
				double textPosY = posY + Math.sin(Math.toRadians(rot)) * ((outerCircleRadius * categorySliceScale.get(category)) * textSpacing);
				categoryFr.drawString(category.getName().toUpperCase(), textPosX - (categoryFr.getStringWidth(category.getName().toUpperCase()) * 0.5), textPosY, -1);
			}
			
		}
		
		// Draw the module gui, used for configuration and toggling modules
		{
			
			double boxOffset = ((width * 0.425) * boxSize);
			double boxWidth = width * (0.35);
			
			double left = 0;
			double top = height * 0.15;
			double right = boxWidth;
			double bottom = height * 0.85;
			
			left += width + 1;
			right += width + 1;
			left -= boxOffset;
			right -= boxOffset;
			
			drawRect(left, top, right, bottom, UiUtils.getColorFromVector(circleInnerBackgroundColor));
			UiUtils.enableWireframe();
			drawRect(left, top, right, bottom, UiUtils.getColorFromVector(circleOutlineColor));
			UiUtils.disableWireframe();
		}
		
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
		
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	@YiffEvents
	public Subscriber<EventKeyUpdate> keyListener = eventKey -> {
		
		// We only want to listen for uncanceled, pre, key presses
		if (eventKey.isCanceled() || !eventKey.isPressed() || eventKey.isPost()) {
			return;
		}
		
		if (eventKey.getKeyCode() == Keyboard.KEY_RSHIFT) {
			Minecraft.getMinecraft().displayGuiScreen(Minecraft.getMinecraft().currentScreen == null ? getInstance() : null);	
		}
		
	};
	
	private double currentCategoryScale = 1;
	/**
	 * Draws a single slice of the circle
	 * @param category The category that the slice is for
	 * @param mouseX The x position of the mouse
	 * @param mouseY The y position of the mouse
	 * @param angles The start and end angles for the slice
	 * @param innerColor The color for the inner part of the circle
	 * @param outerColor The color for the outer part of the circle
	 */
	private void drawSlice(Category category, double mouseX, double mouseY, Vector2d angles, Vector4d innerColor, Vector4d outerColor) {
		drawSlice(category, mouseX, mouseY, angles, innerColor, outerColor, false);
	}
	
	/**
	 * Draws a single slice of the circle
	 * @param category The category that the slice is for
	 * @param mouseX The x position of the mouse
	 * @param mouseY The y position of the mouse
	 * @param angles The start and end angles for the slice
	 * @param innerColor The color for the inner part of the circle
	 * @param outerColor The color for the outer part of the circle
	 * @param outline Whether or not the outline is being rendered
	 */
	private void drawSlice(Category category, double mouseX, double mouseY, Vector2d angles, Vector4d innerColor, Vector4d outerColor, boolean outline) {
		
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldRenderer = tessellator.getWorldRenderer();
		double incr = degreesPerCategory * 0.05;
		
		// Get the scale for the slice
		if (category != null) {
			if (!categorySliceScale.containsKey(category))
				categorySliceScale.put(category, Double.valueOf(1));
			currentCategoryScale = categorySliceScale.get(category);
		}
		double scale = currentCategoryScale;
		
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GL11.glColor4d(1, 1, 1, 1);
		GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableTexture2D();
        GL11.glEnable(GL11.GL_BLEND);
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
		
		// Draw slice
		worldRenderer.begin(outline ? GL11.GL_LINE_STRIP : GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION_COLOR);
		
		if (outline) {
			
			GL11.glLineWidth(3);
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			// Render the outline of the slice
			for (double rot = angles.getX(); rot <= angles.getY(); rot += incr) {
				
				// Outer
				double outerPosX = posX + Math.cos(Math.toRadians(rot)) * (outerCircleRadius * scale);
				double outerPosY = posY + Math.sin(Math.toRadians(rot)) * (outerCircleRadius * scale);
				worldRenderer.pos(outerPosX, outerPosY, 0).color(outerColor.getX(), outerColor.getY(), outerColor.getZ(), outerColor.getW()).endVertex();
				
			}
			
			for (double rot = angles.getY(); rot >= angles.getX(); rot -= incr) {
				
				// Inner
				double innerPosX = posX + Math.cos(Math.toRadians(rot)) * innerCircleRadius;
				double innerPosY = posY + Math.sin(Math.toRadians(rot)) * innerCircleRadius;
				worldRenderer.pos(innerPosX, innerPosY, 0).color(innerColor.getX(), innerColor.getY(), innerColor.getZ(), innerColor.getW()).endVertex();
				
			}
			
			// Final line to make all the lines equal size
			double outerPosX = posX + Math.cos(Math.toRadians(angles.getX())) * (outerCircleRadius * scale);
			double outerPosY = posY + Math.sin(Math.toRadians(angles.getX())) * (outerCircleRadius * scale);
			worldRenderer.pos(outerPosX, outerPosY, 0).color(outerColor.getX(), outerColor.getY(), outerColor.getZ(), outerColor.getW()).endVertex();
			
			GL11.glDisable(GL11.GL_LINE_SMOOTH);
			
		}else {
			
			// Render solid slice
			for (double rot = angles.getX(); rot <= angles.getY(); rot += incr) {
				
				// Outer
				double outerPosX = posX + Math.cos(Math.toRadians(rot)) * (outerCircleRadius * scale);
				double outerPosY = posY + Math.sin(Math.toRadians(rot)) * (outerCircleRadius * scale);
				worldRenderer.pos(outerPosX, outerPosY, 0).color(outerColor.getX(), outerColor.getY(), outerColor.getZ(), outerColor.getW()).endVertex();
				
				// Inner
				double innerPosX = posX + Math.cos(Math.toRadians(rot)) * innerCircleRadius;
				double innerPosY = posY + Math.sin(Math.toRadians(rot)) * innerCircleRadius;
				worldRenderer.pos(innerPosX, innerPosY, 0).color(innerColor.getX(), innerColor.getY(), innerColor.getZ(), innerColor.getW()).endVertex();
				
			}
			
		}
		
		tessellator.draw();
		
		// If this is not an outline render the the category is not null then do hover checks, if the mouse is hovering over the category then we draw a white gradient over the solid background
		if (category != null && !outline) {
			try {
				
				// Calculate
				double mouseAngle = Math.toDegrees(Math.atan2(posX - mouseX, posY - mouseY)) + 180; // The angle of the mouse relative to the position of the circle
				
				// Fix it to work with my numbers
				mouseAngle = (360 - mouseAngle) + 90;
				if (mouseAngle < 0)
					mouseAngle += 360;
				else if (mouseAngle > 360)
					mouseAngle -= 360;
				
				// Check if the mouse angle is in the slice angles
				if (mouseAngle >= angles.getX() && mouseAngle <= angles.getY()) {
					
					// Check the distance of the mouse compared to the circles
					boolean isOutsideInner = Math.sqrt(((posY - mouseY)*(posY - mouseY)) + ((posX - mouseX)*(posX - mouseX))) >= innerCircleRadius;
					boolean isInsideOuter = Math.sqrt(((posY - mouseY)*(posY - mouseY)) + ((posX - mouseX)*(posX - mouseX))) <= (outerCircleRadius * scale);
					
					// Draw
					if (isOutsideInner && isInsideOuter) {
						drawSlice(null, mouseX, mouseY, angles, circleInnerSelectionColor, circleOuterSelectionColor);
						lastHoveredCategory = category;
					}else {
						lastHoveredCategory = null;
					}
					
				}
				
			} catch (Exception e) {
				
			}
		}
		
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableTexture2D();
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
		
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		
		// Click
		if (lastHoveredCategory != null) {
			selectedCategory = lastHoveredCategory;
		}
		
	}
	
	// For anybody trying to port this to their own client, the cheat tick loop runs at 64 ticks a second
	@YiffEvents
	public Subscriber<EventCheatTick> onPreTick = e -> {
		if (e.isPost())
			return;
		
		targetOuterCircleRadius = (width * 0.35) * 0.5;
//		targetInnerCircleRadius = targetOuterCircleRadius * 0.175;
		targetInnerCircleRadius = 0;
		if (selectedCategory == null) {
			targetPosX = width * 0.5; // Center the circle
		}else {
			targetPosX = (width * 0.075) + targetOuterCircleRadius; // Push it off to the side
		}
		targetPosY = height / 2;
		
		// Animations
		double animationSpeed = 0.1;
		
		posY = targetPosY;
		
		// Pos x
		if (posX == targetPosX) {}
		else if (posX > targetPosX) {
			posX -= (posX - targetPosX) * animationSpeed;
		}else {
			posX += (targetPosX - posX) * animationSpeed;
		}
		
		// Outer circle
		if (outerCircleRadius == targetOuterCircleRadius) {}
		else if (outerCircleRadius > targetOuterCircleRadius) {
			outerCircleRadius -= (outerCircleRadius - targetOuterCircleRadius) * animationSpeed;
		}else {
			outerCircleRadius += (targetOuterCircleRadius - outerCircleRadius) * animationSpeed;
		}
		
		// Inner circle
		if (innerCircleRadius == targetInnerCircleRadius) {}
		else if (innerCircleRadius > targetInnerCircleRadius) {
			innerCircleRadius -= (innerCircleRadius - targetInnerCircleRadius) * animationSpeed;
		}else {
			innerCircleRadius += (targetInnerCircleRadius - innerCircleRadius) * animationSpeed;
		}
		
		// Category animations
		for (Category category : Category.values()) {
			
			if (!categorySliceScale.containsKey(category)) {
				categorySliceScale.put(category, Double.valueOf(1));
			}
			
			double currentExtend = categorySliceScale.get(category);
			double targetExtend = selectedCategory != null && category == selectedCategory ? selectedScale : 1;
			
			if (currentExtend == targetExtend) {}
			else if (currentExtend > targetExtend) {
				currentExtend -= (currentExtend - targetExtend) * animationSpeed;
			}else {
				currentExtend += (targetExtend - currentExtend) * animationSpeed;
			}
			
			categorySliceScale.replace(category, Double.valueOf(currentExtend));
			
		}
		
		// Box animation
		double boxTarget = selectedCategory == null ? 0 : 1;
		if (boxSize == boxTarget) {}
		else if (boxSize > boxTarget) {
			boxSize -= (boxSize - boxTarget) * animationSpeed;
		}else {
			boxSize += (boxTarget - boxSize) * animationSpeed;
		}
		
	};
	
}