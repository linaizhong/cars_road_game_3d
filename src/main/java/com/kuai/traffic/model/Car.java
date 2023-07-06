package com.kuai.traffic.model;

public class Car {
	private double offset;
	private double z;
	private Sprite sprite;
	private double speed;
	private double percent;

	public double getOffset() {
		return offset;
	}

	public void setOffset(double offset) {
		this.offset = offset;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getPercent() {
		return percent;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	@Override
	public String toString() {
		return "Car [offset=" + offset + ", z=" + z + ", sprite=" + sprite + ", speed=" + speed + ", percent=" + percent
				+ "]";
	}
}
