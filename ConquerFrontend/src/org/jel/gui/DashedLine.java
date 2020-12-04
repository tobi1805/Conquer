package org.jel.gui;

import java.awt.Graphics2D;

/**
 * Small wrapper around a line
 */
final class DashedLine {
	private final CityLabel cityA;
	private final CityLabel cityB;

	/**
	 * Construct a new DashedLine between two cities.
	 *
	 * @param a
	 * @param b
	 */
	DashedLine(CityLabel a, CityLabel b) {
		this.cityA = a;
		this.cityB = b;
	}

	/**
	 * Draw the line. You have to set the stroke manually, as otherwise on each
	 * invocation a new one may be constructed==&gt; performance issues.
	 */
	void draw(Graphics2D g) {
		g.drawLine(this.cityA.getPreferredX(), this.cityA.getPreferredY(), this.cityB.getPreferredX(),
				this.cityB.getPreferredY());
	}
}
