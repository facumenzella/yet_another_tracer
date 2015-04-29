package ar.edu.itba.it.cg.yart.light;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.raytracer.tracer.HitTracer;
import ar.edu.itba.it.cg.yart.raytracer.tracer.SimpleHitTracer;

public class Directional extends LightAbstract{
	
	private final double ls;
	private final Color color;
	private final Vector3d dir;
	private final HitTracer tracer;
	
	private Color L;
	
	public Directional(final double ls, final Color color, final Vector3d direction) {
		super();
		this.ls = ls;
		this.color = color;
		this.dir = direction.normalizedVector();
		this.tracer = new SimpleHitTracer();
		this.L = this.mL(null);
	}
	
	@Override
	public Vector3d getDirection(ShadeRec sr) {
		return dir;
	}

	@Override
	public Color L(final ShadeRec sr) {
		return this.L;
	}
	
	public Color mL(final ShadeRec sr) {
		return color.multiply(ls);
	}
	
	@Override
	public boolean inShadow(Ray ray, ShadeRec sr) {
		double t;		
		t = sr.world.getTree().traceRayHit(ray, tracer);
		if(t != Double.NEGATIVE_INFINITY) {
				return true;
		}
		return false;
	}

}
