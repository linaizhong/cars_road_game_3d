package com.kuai.traffic.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Date;

import com.kuai.traffic.model.Project;

public class Util {
	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

	public static long timestamp() {
		return (new Date()).getTime();
	}

	public static int toInt(Integer obj, int def) {
		if (obj != null) {
			return obj;
		}

		return Util.toInt(def, 0);
	}

	public static double toDouble(Double obj, double def) {
		if (obj != null) {
			return obj;
		}

		return Util.toDouble(obj, 0.0);
	}

	public static double increase(double start, double increment, double max) {
		double result = start + increment;
		while (result >= max)
			result -= max;
		while (result < 0)
			result += max;
		return result;
	}

	public static void project(Project p, double cameraX, double cameraY, double cameraZ, double cameraDepth, int width,
			int height, double roadWidth) {
		p.getCamera().setX((p.getWorld().getX()) - cameraX);
		p.getCamera().setY((p.getWorld().getY()) - cameraY);
		p.getCamera().setZ((p.getWorld().getZ()) - cameraZ);
		p.getScreen().setScale(cameraDepth / p.getCamera().getZ());
		p.getScreen().setX(Math.round((width / 2) + (p.getScreen().getScale() * p.getCamera().getX() * width / 2)));
		p.getScreen().setY(Math.round((height / 2) - (p.getScreen().getScale() * p.getCamera().getY() * height / 2)));
		p.getScreen().setW(Math.round((p.getScreen().getScale() * roadWidth * width / 2)));
	}
	
	public static void project1(Project p, double cameraX, double cameraY, double cameraZ, double cameraDepth, int width,
			int height, double roadWidth) {
		p.getCamera().setX((p.getWorld().getX()) - cameraX);
		p.getCamera().setY((p.getWorld().getY()) - cameraY);
		p.getCamera().setZ((p.getWorld().getZ()) - cameraZ);
		p.getScreen().setScale(cameraDepth / p.getCamera().getZ());
		p.getScreen().setX(Math.round((width / 2) - (p.getScreen().getScale() * p.getCamera().getX() * width / 2)));
		p.getScreen().setY(Math.round((height / 2) + (p.getScreen().getScale() * p.getCamera().getY() * height / 2)));
		p.getScreen().setW(Math.round((p.getScreen().getScale() * roadWidth * height / 2)));

	}

	public static boolean overlap(double x1, double w1, double x2, double w2) {
		return overlap(x1, w1, x2, w2, 1);
	}

	public static boolean overlap(double x1, double w1, double x2, double w2, double percent) {
		double half = percent / 2;
		double min1 = x1 - (w1 * half);
		double max1 = x1 + (w1 * half);
		double min2 = x2 - (w2 * half);
		double max2 = x2 + (w2 * half);

		return !((max1 < min2) || (min1 > max2));
	}

	public static double easeIn(double a, double b, double percent) {
		double ret = a + (b - a) * Math.pow(percent, 2);
		return ret;
	}

	public static double easeInOut(double a, double b, double percent) {
		double ret = a + (b - a) * ((-Math.cos(percent * Math.PI) / 2) + 0.5);
		return ret;
	}

	public static Rectangle randomChoice(Rectangle[] options) {
		return options[Util.randomInt(0, options.length - 1)];
	}

	public static double randomChoice(double[] options) {
		return options[Util.randomInt(0, options.length - 1)];
	}

	public static int randomChoice(int[] options) {
		return options[Util.randomInt(0, options.length - 1)];
	}

	public static int randomInt(int min, int max) {
		return (int) Math.round(Util.interpolate(min, max, Math.random()));
	}

	public static double interpolate(double a, double b, double percent) {
		double interpolate = a + ((b - a) * percent);
		return interpolate;
	}

	public static int limit(int value, int min, int max) {
		return Math.max(min, Math.min(value, max));
	}

	public static double percentRemaining(int n, int total) {
		return (n % total) / (double)total;
	}

	public static double accelerate(double v, double accel, double dt) {
		return v + (accel * dt);
	}

	public static double easeOut(double a, double b, double percent) {
		double ret = a + (b - a) * (1 - Math.pow(1 - percent, 2));
		return ret;
	}

	public static double exponentialFog(int distance, int density) {
		return 1 / (Math.pow(Math.E, (distance * distance * density)));
	}
}
