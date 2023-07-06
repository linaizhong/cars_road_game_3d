package com.kuai.traffic.model.road;

import java.awt.Color;
import java.awt.Polygon;

import com.kuai.traffic.model.TrafficColors;
import com.kuai.traffic.model.TrafficRender;

public class HorizontalRender extends TrafficRender {
	private static final long serialVersionUID = 1L;

	public HorizontalRender() {
		super();
	}

	public void polygon(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4,
			Color color) {
		log.info("TrafficRender:polygon: (" + x1 + ", " + y1 + "; " + x2 + ", " + y2 + "; " + x3 + ", " + y3 + "; " + x4
				+ ", " + y4 + "; " + ")");

		g2d.setColor(color);
		int xPoly[] = { (int) x1, (int) x2, (int) x3, (int) x4 };
		int yPoly[] = { (int) y1, (int) y2, (int) y3, (int) y4 };
		Polygon p = new Polygon(xPoly, yPoly, xPoly.length);
		g2d.fillPolygon(p);
	}

	public void segment(int width, int lanes, double x1, double y1, double w1, double x2, double y2, double w2,
			double fog, TrafficColors colors) {
		log.info("TrafficRender:segment: (" + width + ", " + lanes + ", " + x1 + ", " + y1 + ", " + w1 + ", " + x2
				+ ", " + y2 + ", " + w2 + ", " + fog + ")");

		double r1 = rumbleWidth(w1, lanes), 
				r2 = rumbleWidth(w2, lanes), 
				l1 = laneMarkerWidth(w1, lanes),
				l2 = laneMarkerWidth(w2, lanes), 
				lanew1, lanew2, 
				lanex1, lanex2, 
				lane;

		g2d.setColor(colors.getGrass());
		g2d.fillRect(0, (int) y2, width, (int) (y1 - y2));

		polygon(x1 - w1 - r1, y1, x1 - w1, y1, x2 - w2, y2, x2 - w2 - r2, y2, colors.getRumble());
		polygon(x1 + w1 + r1, y1, x1 + w1, y1, x2 + w2, y2, x2 + w2 + r2, y2, colors.getRumble());
		polygon(x1 - w1, y1, x1 + w1, y1, x2 + w2, y2, x2 - w2, y2, colors.getRoad());

		if (colors.getLane() != null) {
			lanew1 = w1 * 2 / lanes;
			lanew2 = w2 * 2 / lanes;
			lanex1 = x1 - w1 + lanew1;
			lanex2 = x2 - w2 + lanew2;
			for (lane = 1; lane < lanes; lanex1 += lanew1, lanex2 += lanew2, lane++)
				polygon((lanex1 - l1 / 2), y1, (lanex1 + l1 / 2), y1, (lanex2 + l2 / 2), y2, (lanex2 - l2 / 2), y2,
						colors.getLane());
		}

//		fog(0, (int) y1, (int) width, (int) (y2 - y1), fog);
	}
}