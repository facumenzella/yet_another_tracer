package ar.edu.itba.it.cg.yart.textures;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.tracer.ShadeRec;

public class ConstantColor extends Texture{
	
	private Color color;
	
	public ConstantColor(final Color color) {
		this.color = color;
	}

	@Override
	public Color getColor(final ShadeRec sr) {
		return color;
	}
	
	public void setColor(final Color color) {
		this.color = color;
	}

}
