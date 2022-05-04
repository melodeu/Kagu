package xyz.yiffur.yiffur.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlyphUtils {

	/**
	 * @param font The font to use
	 * @return A glyphMap which contains a buffered image and a map of chars to
	 *         glyphs
	 */
	public static GlyphMap genereateGlyphBufferedImageFromFont(Font font, boolean antiAliasing) {
		GlyphMap glyphMap = new GlyphMap();
		glyphMap.setAntiAliased(antiAliasing);
		
		// Get all the characters that the font contains
		char[] chars;
		{
			// Search
			List<Character> characters = new ArrayList<Character>();
			for (int i = Character.MIN_VALUE; i <= Character.MAX_VALUE; i++) {
				if (font.canDisplay((char) i)) {
					characters.add((char) i);
				}
			}
			
			// Add
			chars = new char[characters.size()];
			int index = 0;
			for (char c : characters) {
				chars[index] = c;
				index++;
			}
		}
		
		// Used to measure character sizes
		AffineTransform affineTransform = new AffineTransform();
		FontRenderContext fontRenderContext = new FontRenderContext(affineTransform, antiAliasing, font.getSize2D() % 1 != 0);
		
		// Calculate max width and height
		double maxCharWidth = 0;
		double maxCharHeight = 0;
		double maxDescent = 0;
		{
			
			// Find maxes
			for (char c : chars) {
				Rectangle2D bounds = font.getStringBounds(String.valueOf(c), fontRenderContext);
				if (bounds.getWidth() > maxCharWidth) {
					maxCharWidth = bounds.getWidth();
				}
				if (bounds.getHeight() > maxCharHeight) {
					maxCharHeight = bounds.getHeight();
				}
			}
			
			// Account for descent and ascent
			Graphics2D tempGraphics = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB).createGraphics();
			tempGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAliasing ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
			tempGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAliasing ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
//			glyphMap.setFontHeight((float) Math.floor(maxCharHeight)); // Needed because descent and ascent shouldn't count towards font height
			glyphMap.setFontHeight((float) font.getSize()); // For some reason this works better? It shouldn't but if it works it works
			maxDescent = Math.abs(tempGraphics.getFontMetrics().getMaxDescent());
			maxCharHeight += Math.abs(tempGraphics.getFontMetrics(font).getMaxDescent());
		}
		
		// Padding
		maxCharHeight += 2;
		maxCharWidth += 2;
		
		// Create a buffered image, it's just a long line of characters, no fancy squares or rectangles. This is the easiest and fastest way to do it
		BufferedImage bufferedImage = new BufferedImage((int)Math.ceil(maxCharWidth * (chars.length - 1)), (int)Math.ceil(maxCharHeight), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = bufferedImage.createGraphics();
		graphics.setFont(font);
		
		// Clear the background of the buffered image
		graphics.setBackground(new Color(0, 0, 0, 0));
		graphics.clearRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
		
		
		// Draw each character
		graphics.setColor(Color.WHITE);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAliasing ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAliasing ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		{
			int x = 0;
			int y = 0;
			for (char c : chars) {
				Rectangle2D charBounds = font.getStringBounds(String.valueOf(c), fontRenderContext);
				y = (int)Math.ceil(charBounds.getHeight());
				graphics.drawString(String.valueOf(c), x, y);
				
				// Glyph, accounts for
				Glyph glyph = new Glyph((int) charBounds.getWidth(), (int) maxCharHeight,
						(double) x / (double) bufferedImage.getWidth(),
						(bufferedImage.getHeight() - charBounds.getHeight()) / bufferedImage.getHeight(),
						charBounds.getWidth() / bufferedImage.getWidth(),
						1, (int) 0);
				glyphMap.getMapping().put(c, glyph);
				
				x += maxCharWidth;
			}
		}
		
		// Set buffered image and return glyph map
		glyphMap.setBufferedImage(bufferedImage);
		return glyphMap;
	}
	
	/**
	 * @author lavaflowglow
	 *
	 */
	public static class GlyphMap {
		private boolean isAntiAliased = false;
		private BufferedImage bufferedImage;
		private Map<Character, Glyph> mapping = new HashMap<Character, Glyph>();
		private float fontHeight = 0;

		/**
		 * @return the isAntiAliased
		 */
		public boolean isAntiAliased() {
			return isAntiAliased;
		}

		/**
		 * @param isAntiAliased the isAntiAliased to set
		 */
		public void setAntiAliased(boolean isAntiAliased) {
			this.isAntiAliased = isAntiAliased;
		}

		/**
		 * @return the bufferedImage
		 */
		public BufferedImage getBufferedImage() {
			return bufferedImage;
		}

		/**
		 * @param bufferedImage the bufferedImage to set
		 */
		public void setBufferedImage(BufferedImage bufferedImage) {
			this.bufferedImage = bufferedImage;
		}

		/**
		 * @return the mapping
		 */
		public Map<Character, Glyph> getMapping() {
			return mapping;
		}

		/**
		 * @param mapping the mapping to set
		 */
		public void setMapping(Map<Character, Glyph> mapping) {
			this.mapping = mapping;
		}

		/**
		 * @return the fontHeight
		 */
		public float getFontHeight() {
			return fontHeight;
		}

		/**
		 * @param fontHeight the fontHeight to set
		 */
		public void setFontHeight(float fontHeight) {
			this.fontHeight = fontHeight;
		}

	}

}