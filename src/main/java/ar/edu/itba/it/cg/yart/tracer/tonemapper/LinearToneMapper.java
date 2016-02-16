package ar.edu.itba.it.cg.yart.tracer.tonemapper;

import ar.edu.itba.it.cg.yart.color.Color;

public class LinearToneMapper extends AbstractToneMapper {

	@Override
	public Color mapMe(Color color) {
		double max = Math.max(color.r, Math.max(color.g, color.b));
		if (max > 1.0) {
			color.r /= max;
			color.g /= max;
			color.b /= max;
		}
		
		return super.mapMe(color);
	}

}
