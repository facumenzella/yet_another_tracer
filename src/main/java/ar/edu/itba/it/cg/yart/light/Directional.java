package ar.edu.itba.it.cg.yart.light;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.transforms.Matrix4d;

public class Directional extends AbstractLight{
	
	private final double ls;
	private final Color color;
	private Vector3d dir;
		
	public Directional(final double ls, final Color color, final Vector3d direction) {
		super();
		this.ls = ls;
		this.color = color;
		this.dir = direction.normalizedVector();
	}
	
	@Override
	public Vector3d getDirection(ShadeRec sr) {
		return dir;
	}

	@Override
	public Color L(final ShadeRec sr) {
		final double r = color.r * ls;
		final double g = color.g * ls;
		final double b = color.b * ls;
		return new Color(r, g, b, color.a);
	}
	
	
	@Override
	public boolean inShadow(Ray ray, ShadeRec sr, final Stack stack) {
		double t;		
		// TODO check this out
		t = sr.world.getTree().traceShadowHit(ray, Double.POSITIVE_INFINITY, stack);
		if(t != Double.NEGATIVE_INFINITY) {
				return true;
		}
		return false;
	}
	@Override
	public void applyTransformation(Matrix4d matrix) {
		this.dir = this.dir.transformByMatrix(matrix);
	}

}
