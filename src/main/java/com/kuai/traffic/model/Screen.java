package com.kuai.traffic.model;

public class Screen {
	private double x;
	private double y;
	private double z;
	private double w;
	private double scale;

	public Screen() {
	}

	public Screen(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Screen(double x, double y, double z, double w) {
		this(x, y, z);
		this.w = w;
	}

	public Screen(double x, double y, double z, double w, double s) {
		this(x, y, z, w);
		this.scale = s;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public double getW() {
		return w;
	}

	public void setW(double w) {
		this.w = w;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	@Override
	public String toString() {
		return "Screen [x=" + x + ", y=" + y + ", z=" + z + ", w=" + w + ", scale=" + scale + "]";
	}
}
