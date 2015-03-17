package ar.edu.itba.it.cg.yart.geometry.primitives;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.light.Material;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public abstract class GeometricObject {

	protected static final double EPSILON = 0.0001;
	public Color color;
	private Material  material;
	
	public GeometricObject() {
		color = new Color(1.0f, 1.0f, 1.0f);
	}
	
	public Material getMaterial() { 
		return material;
	}
	
	public void setMaterial(final Material material) {
		this.material = material;
	}
	public abstract double hit(final Ray ray, final ShadeRec sr);
}
