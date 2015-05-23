package ar.edu.itba.it.cg.yart.light;


import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.transforms.Matrix4d;

public class AmbientLight extends AbstractLight {
	
	private double ls;
	private Color color;
	private Vector3d direction;
	
	private Color L;
	
	public AmbientLight(final Color color) {
		this.ls = 1;
		this.color = color;
		this.direction = new Vector3d(0,0,0);
		this.L = this.mL(null);
	}
	
	@Override
	public Vector3d getDirection(final ShadeRec sr) {
		return this.direction;
	}
	
	@Override
	public Color L(final ShadeRec sr) {
		return this.L;
	}
	
	public Color mL(final ShadeRec sr) {
		return color.multiply(ls);
	}

	@Override
	public boolean inShadow(Ray ray, ShadeRec sr, final Stack stack) {
		return false;
	}

	@Override
	public void applyTransformation(Matrix4d matrix) {
		this.direction = this.direction.transformByMatrix(matrix);
	}

}
