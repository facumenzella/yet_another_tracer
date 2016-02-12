package ar.edu.itba.it.cg.yart.textures;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.tracer.ShadeRec;

public class ConstantColor extends Texture {
	
	private Color color;
	private ConstantColor complement;

	/**
	 * Creates a new ConstantColor texture using the specified color.
	 * @param color The Color that is returned when sampling this texture
	 */
	public ConstantColor(final Color color) {
		setColor(color);
	}

	/**
	 * Creates a complement ConstantColor texture
	 * @param other
	 */
	private ConstantColor(final ConstantColor other) {
		complement = other;
		color = other.color.complement();
	}

	@Override
	public Color getColor(final ShadeRec sr) {
		return color;
	}

	@Override
	public Texture complement() {
		return complement;
	}

	public void setColor(final Color color) {
		this.color = color;
		complement = new ConstantColor(this);
	}

}
