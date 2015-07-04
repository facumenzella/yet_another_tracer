package ar.edu.itba.it.cg.yart.tracer.tonemapper;

import ar.edu.itba.it.cg.yart.color.Color;

public interface ToneMapper {
	/**
	 * Applies a Tone mapping operator to the given color. The given color is modified.
	 * @param color The color to apply the operator to.
	 * @return The same Color instance
	 */
	public Color mapColor(final Color color);
}
