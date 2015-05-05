package ar.edu.itba.it.cg.yart.geometry.primitives;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.light.materials.Material;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public abstract class GeometricObject {

	protected static final double EPSILON = 0.0001;
	public Color color;
	private Material  material;
	private BoundingBox boundingBox;	
	
	public GeometricObject() {
		color = new Color(1.0f, 1.0f, 1.0f);
	}
	
	public Material getMaterial() { 
		return material;
	}
	
	public void setMaterial(final Material material) {
		this.material = material;
	}
	
	public BoundingBox getBoundingBox() {
		return boundingBox;
	}
	
	protected void updateBoundingBox() {
		this.boundingBox = createBoundingBox();
	}
	
	public abstract BoundingBox createBoundingBox();
	public abstract double hit(final Ray ray, final ShadeRec sr);
	public abstract double shadowHit(final Ray ray);
}
