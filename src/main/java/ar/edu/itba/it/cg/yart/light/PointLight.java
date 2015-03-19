package ar.edu.itba.it.cg.yart.light;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class PointLight extends LightAbstract {
	
	private final double ls;
	private final Color color;
	private final Vector3d location;
	
	public PointLight(final double ls, final Color color, final Vector3d location) {
		this.ls = ls;
		this.color = color;
		this.location = location;
	}
	
	@Override
	public Vector3d getDirection(final ShadeRec sr) {
		return location.sub(sr.hitPoint).normalizedVector();
	}
	
	@Override
	public Color L(final ShadeRec sr) {
		return color.multiply(ls);
		
	}
}
