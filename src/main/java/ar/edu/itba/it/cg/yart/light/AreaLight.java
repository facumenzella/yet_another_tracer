package ar.edu.itba.it.cg.yart.light;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.light.materials.Emissive;
import ar.edu.itba.it.cg.yart.tracer.Ray;
import ar.edu.itba.it.cg.yart.tracer.ShadeRec;

public class AreaLight implements Light {
	
	private final double ls;
	private final Color color;
	private GeometricObject geometricObject;
	private Emissive material;
	private final int samples;
	
	public AreaLight(final double ls, final Color color, final int samples) {
		super();
		this.ls = ls;
		this.color = color;
		this.samples = samples;
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
	
	public int getSamplesNumber() {
		return samples;
	}

	@Override
	public Vector3d getDirection(ShadeRec sr) {
		sr.sample = geometricObject.getSample();
		sr.wi = sr.sample.point.sub(sr.hitPoint).normalizeMe();
		return sr.wi;
	}

	@Override
	public Color L(ShadeRec sr) {
		double ndotd = sr.sample.normal.inverse().dot(sr.wi);
		
		if (ndotd > 0.0) {
			return material.getCe();
		}
		else {
			return Color.BLACK;
		}
	}
	
	public double G(ShadeRec sr) {
		final double ndotd = sr.sample.normal.inverse().dot(sr.wi);
		final double d2 = sr.sample.point.distanceSquared(sr.hitPoint);
		
		return ndotd / d2;
	}
	
	public double pdf(ShadeRec sr) {
		return geometricObject.pdf();
	}

	@Override
	public boolean inShadow(Ray ray, ShadeRec sr, Stack stack) {
		double t;
		
		final double dx = sr.sample.point.x - ray.origin.x;
		final double dy = sr.sample.point.y - ray.origin.y;
		final double dz = sr.sample.point.z - ray.origin.z;

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
		return true;
	}

}
