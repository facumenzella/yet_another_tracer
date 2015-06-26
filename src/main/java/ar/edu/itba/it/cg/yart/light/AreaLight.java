package ar.edu.itba.it.cg.yart.light;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.light.materials.Emissive;
import ar.edu.itba.it.cg.yart.light.materials.Material;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class AreaLight implements Light {
	
	private final double ls;
	private final Color color;
	private GeometricObject geometricObject;
	private Material material;
	
	public AreaLight(final double ls, final Color color) {
		super();
		this.ls = ls;
		this.color = color;
	}
	
	public void setShape(final GeometricObject geometricObject) {
		this.geometricObject = geometricObject;
	}
	
	public void setMaterial(final Emissive material) {
		this.material = material;
		if (material != null) {
			material.setLs(ls);
			material.setCe(color);
		}
	}

	@Override
	public Vector3d getDirection(ShadeRec sr) {
		sr.samplePoint = null; // TODO: Sample here
		sr.sampleNormal = null; // TODO: Get normal at samplePoint in geometricObject
		sr.wi = sr.samplePoint.sub(sr.sampleNormal);
		sr.wi.normalizeMe();
		
		return sr.wi;
	}

	@Override
	public Color L(ShadeRec sr) {
		final double r = color.r * ls;
		final double g = color.g * ls;
		final double b = color.b * ls;
		return new Color(r, g, b, color.a);
	}

	@Override
	public boolean inShadow(Ray ray, ShadeRec sr, Stack stack) {
		double t;
		
		final double dx = sr.samplePoint.x - ray.origin.x;
		final double dy = sr.samplePoint.y - ray.origin.y;
		final double dz = sr.samplePoint.z - ray.origin.z;

		final double d = Math.sqrt(dx * dx + dy * dy + dz * dz);

		// TODO check this out
		t = sr.world.getTree().traceShadowHit(ray, d, stack);
		if (t != Double.NEGATIVE_INFINITY) {
			return true;
		}
		return false;
	}

	@Override
	public void shadowsOn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shadowsOff() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean castShadows() {
		// TODO Auto-generated method stub
		return false;
	}

}
