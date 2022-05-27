/**
 * 
 */
package cafe.kagu.kagu.utils;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3d;

import net.minecraft.client.Minecraft;

/**
 * @author lavaflowglow
 *
 */
public class RotationUtils {
	
	/**
	 * Calculates the angle from the players eyes needed to look at pos2
	 * @param targetPos The target position
	 * @return A float array where the first index is the yaw and the second is the pitch
	 */
	public static float[] getRotations(Vector3d targetPos) {
		return getRotations(new Vector3d(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight(), Minecraft.getMinecraft().thePlayer.posZ), 
				targetPos);
	}
	
	/**
	 * Calculates the angle from pos1 needed to look at pos2
	 * @param pos1 The first position
	 * @param pos2 The target position
	 * @return A float array where the first index is the yaw and the second is the pitch
	 */
	public static float[] getRotations(Vector3d pos1, Vector3d pos2) {
		// soh cah toa
		double distX = pos2.getX() - pos1.getX();
		double distY = pos2.getY() - pos1.getY();
		double distZ = pos2.getZ() - pos1.getZ();
		double dist = Math.sqrt(distX * distX + distZ * distZ);
		double yaw = Math.toDegrees(Math.atan2(distZ, distX));
		double pitch = Math.toDegrees(Math.atan2(distY, dist));
		return new float[] {(float)yaw - 90, (float)-pitch};
	}
	
}
