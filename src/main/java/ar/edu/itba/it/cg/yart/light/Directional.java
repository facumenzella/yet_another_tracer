package ar.edu.itba.it.cg.yart.light;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class Directional extends LightAbstract{
	
	private final double ls;
	private final Color color;
	private final Color shadeColor;
	private final Vector3d dir;
	
	public Directional(final double ls, final Color color, final Vector3d direction) {
		super();
		this.ls = ls;
		this.color = color;
		this.shadeColor = color.multiply(ls);
		this.dir = direction.normalizedVector();
	}
	@Override
	public Vector3d getDirection(ShadeRec sr) {
		return dir;
	}

	@Override
	public Color L(ShadeRec sr) {
		return this.shadeColor;
	}
	@Override
	public boolean inShadow(Ray ray, ShadeRec sr) {
		double t;		
		for(final GeometricObject object : sr.world.getObjects()) {
			t = object.shadowHit(ray);
			if(t != Double.NEGATIVE_INFINITY) {
				return true;
			}
		}
		return false;
	}

}
