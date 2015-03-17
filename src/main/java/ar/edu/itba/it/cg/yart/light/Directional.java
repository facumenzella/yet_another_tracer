package ar.edu.itba.it.cg.yart.light;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class Directional extends LightAbstract{
	
	private final double ls;
	private final Color color;
	private final Vector3d dir;
	


	public Directional(final double ls, final Color color, final Vector3d direction) {
		this.ls = ls;
		this.color = color;
		this.dir = direction;
	}
	@Override
	public Vector3d getDirection(ShadeRec sr) {
		return dir.normalizedVector();
	}

	@Override
	public Color L(ShadeRec sr) {
		return color.multiply(ls);
	}

}
