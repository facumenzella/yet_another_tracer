package ar.edu.itba.it.cg.yart.textures.wrappers;

import ar.edu.itba.it.cg.yart.color.Color;

public class RepeatWrap extends Wrapper {
	
	
	@Override
	public Color wrap(final double u, final double v) {
		int row = ((int) u )%hres;
		int column = ((vres - 1) - (int) v )%vres;
		if(row < 0) {
			row = hres + row;
		}
		if (column < 0) {
			column = vres + column;
		}
		return getColor(row, column);
	}
	
}
