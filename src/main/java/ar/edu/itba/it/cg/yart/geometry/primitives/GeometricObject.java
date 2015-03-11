package ar.edu.itba.it.cg.yart.geometry.primitives;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.raytracer.Ray;

public abstract class GeometricObject {

	protected static final double EPSILON = 0.0001;
	public Color color;
	
	public GeometricObject() {
		color = new Color(1.0f, 1.0f, 1.0f);
	}
	
	public abstract double hit(final Ray ray);
}
