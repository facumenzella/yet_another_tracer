package ar.edu.itba.it.cg.yart.tracer.tonemapper;

import ar.edu.itba.it.cg.yart.color.Color;

public class ReinhardToneMapper implements ToneMapper {

	@Override
	public Color mapColor(Color color) {
		color.r /= 1 + color.r;
		color.g /= 1 + color.g;
		color.b /= 1 + color.b;
		
		return color;
	}

}
