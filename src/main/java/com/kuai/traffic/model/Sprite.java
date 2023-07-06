package com.kuai.traffic.model;

import java.awt.Rectangle;

public class Sprite {
	private int index;
	private Rectangle source;
	private double offset;

	public Sprite() {

	}

	public Sprite(int ind, Rectangle rect, double off) {
		this.index = ind;
		this.source = rect;
		this.offset = off;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Rectangle getSource() {
		return source;
	}

	public void setSource(Rectangle source) {
		this.source = source;
	}

	public double getOffset() {
		return offset;
	}

	public void setOffset(double offset) {
		this.offset = offset;
	}

	@Override
	public String toString() {
		return "Sprite [index=" + index + ", source=" + source + ", offset=" + offset + "]";
	}
}
