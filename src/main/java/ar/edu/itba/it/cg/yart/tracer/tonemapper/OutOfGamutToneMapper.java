package ar.edu.itba.it.cg.yart.tracer.tonemapper;

import ar.edu.itba.it.cg.yart.color.Color;

public class OutOfGamutToneMapper extends AbstractToneMapper {
	
	private final Color tone;
	
	public OutOfGamutToneMapper(final Color tone) {
		this.tone = tone;
	}

	@Override
	public Color mapMe(Color color) {
		if (color.r > 1 || color.g > 1 || color.b > 1) {
			return tone;
		}
		return super.mapMe(color);
	}
}
