package ar.edu.itba.it.cg.yart.tracer.tonemapper;

import ar.edu.itba.it.cg.yart.YartDefaults;
import ar.edu.itba.it.cg.yart.color.Color;

public abstract class AbstractToneMapper implements ToneMapper {

	private double gamma = YartDefaults.GAMMA;
	private double invGamma = YartDefaults.INV_GAMMA;

	@Override
	public Color map(final Color color) {
		return mapMe(new Color(color));
	}

	@Override
	public Color mapMe(Color color) {
		color.r = Math.pow(color.r, invGamma);
		color.g = Math.pow(color.g, invGamma);
		color.b = Math.pow(color.b, invGamma);

		return color;
	}

	@Override
	public void setGamma(final double gamma) {
		if (gamma > 0) {
			this.gamma = gamma;
			this.invGamma = 1 / gamma;
		}
		else {
			setGamma(1);
		}
	}

	@Override
	public double getGamma() {
		return gamma;
	}

	@Override
	public double getInvGamma() {
		return invGamma;
	}
}
