package com.kuai.traffic.model;

import java.util.Date;
import java.util.Timer;

public class Stats {
	private long startTime, prevTime;
	private long ms = 0;
	private long msMin = 1000;
	private long msMax = 0;
	private int fps = 0;
	private int fpsMin = 1000;
	private int fpsMax = 0;
	private int frames = 0;

	Timer timer;

	public Stats() {
		startTime = (new Date()).getTime();
	}

	public void update() {
		startTime = end();
	}

	private long end() {
		long time = (new Date()).getTime();

		ms = time - startTime;
		msMin = Math.min(msMin, ms);
		msMax = Math.max(msMax, ms);

		frames++;

		if (time > prevTime + 1000) {
			fps = (int)Math.round((double)(frames * 1000) / (time - prevTime));
			fpsMin = Math.min(fpsMin, fps);
			fpsMax = Math.max(fpsMax, fps);

			prevTime = time;
			frames = 0;
		}

		return time;
	}
}
