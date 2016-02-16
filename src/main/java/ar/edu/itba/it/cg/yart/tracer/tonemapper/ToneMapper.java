package ar.edu.itba.it.cg.yart.tracer.tonemapper;

import ar.edu.itba.it.cg.yart.color.Color;

public interface ToneMapper {

	/**
	 * Applies a Tone mapping operator to the given color. The given color is modified.
	 * @param color The color to apply the operator to.
	 * @return The same Color instance
	 */
	public Color mapMe(final Color color);

	/**
	 * Applies a Tone mapping operator to the given color. The given color is left untouched.
	 * @param color The color to apply the operator to.
	 * @return A new Color with this Tone mapping operator applied
	 */
	public Color map(final Color color);

	/**
	 * Sets the Gamma for this Tone mapper. All Colors will be gamma-corrected by the inverse.
	 * @param gamma The gamma
	 */
	public void setGamma(final double gamma);

	/**
	 * Retrieves the Gamma for this Tone mapper.
	 * @return The gamma
	 */
	public double getGamma();

	/**
	 * Returns the inverse Gamma value for this Tone mapper
	 * @return The inverse Gamma
	 */
	public double getInvGamma();
}
