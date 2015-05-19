package ar.edu.itba.it.cg.yart.textures.wrappers;

import ar.edu.itba.it.cg.yart.color.Color;

public class ColorWrap extends Wrapper{

	private Color color = Color.blackColor();
	
	public ColorWrap() {
		this(Color.blackColor());
	}
	
	public ColorWrap(final Color color) {
		this.color = color;
	}
	@Override
	public Color wrap(final double u, final double v) {
		
		int row = ((int) u);
		int column = ((vres - 1) - (int) v);
		if(row < 0 || column < 0 || column >= vres || row >= hres) {
			return color;
		}
		
		return getColor(row, column);
	}

}
