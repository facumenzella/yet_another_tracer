package ar.edu.itba.it.cg.yart.geometry.primitives;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.light.materials.Material;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.transforms.Matrix4d;
import ar.edu.itba.it.cg.yart.transforms.Transformable;

public abstract class GeometricObject implements Transformable{

	protected double EPSILON = 0.00001;
	public Color color;
	public Material  material;
	private AABB boundingBox;	
	
	public Matrix4d matrix;
	public Matrix4d invMatrix;
	public Matrix4d transposedInvMatrix;
	
	public GeometricObject() {
		color = new Color(1.0f, 1.0f, 1.0f);
		this.matrix = new Matrix4d();
		this.invMatrix = this.matrix.inverse();
		this.transposedInvMatrix = this.invMatrix.transpose();
	}
	
	public Material getMaterial() { 
		return material;
	}
	
	public void setMaterial(final Material material) {
		this.material = material;
	}
	
	public AABB getBoundingBox() {
		return boundingBox;
	}
	
	protected void updateBoundingBox() {
		this.boundingBox = createBoundingBox();
	}
	
	@Override
	public void applyTransformation(Matrix4d matrix) {
		this.matrix = this.matrix.leftMultiply(matrix);
		this.invMatrix = this.matrix.inverse();
		this.transposedInvMatrix = this.invMatrix.transpose();
	}
	
	public abstract AABB createBoundingBox();
	public abstract double hit(final Ray ray, final ShadeRec sr, final Stack stack);
	public abstract double shadowHit(final Ray ray, final Stack stack);
}
