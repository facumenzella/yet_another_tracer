package ar.edu.itba.it.cg.yart.textures.wrappers;

import ar.edu.itba.it.cg.yart.color.Color;

public class ClampWrap extends Wrapper{

	@Override
	public Color wrap(final double u, final double v) {
		int row = ((int) u);
		int column = ((vres - 1) - (int) v);
		if(row < 0) {
			row = 0;
		} else if (row >= hres) {
			row = hres-1;
		}
		
		if(column < 0) {
			column = 0;
		} else if (column >= vres) {
			column = vres-1;
		}
		
		return getColor(row, column);
	}

}
