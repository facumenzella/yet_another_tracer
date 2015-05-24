package ar.edu.itba.it.cg.yart.geometry;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.geometry.primitives.AABB;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.transforms.Matrix4d;

public class Instance extends GeometricObject{

	public GeometricObject object; // object to be transformed
	public boolean transformTexture; // do we transform the texture

	public Instance(final GeometricObject object) {
		this.object = object;
		this.matrix = new Matrix4d();
		this.invMatrix = this.matrix.inverse();
		this.transposedInvMatrix = this.invMatrix.transpose();
		updateBoundingBox();
	}
	
	@Override
	public AABB createBoundingBox() {
		AABB boundingBox = object.createBoundingBox();
		if (boundingBox != null) {
			boundingBox.applyTransformation(matrix);
		}
		return boundingBox;
	}

	@Override
	public double hit(Ray ray, ShadeRec sr, final Stack stack) {
		Ray invRay = new Ray(ray.origin);
		// apply the inverse set of transformations to the ray to produce an inverse transformed ray
		invRay.origin = ray.origin.transformByMatrix(invMatrix);
		
		final double dx = (invMatrix.m00 * ray.direction[0]) + (invMatrix.m01 * ray.direction[1]) + (invMatrix.m02 * ray.direction[2]);
		final double dy = (invMatrix.m10 * ray.direction[0]) + (invMatrix.m11 * ray.direction[1]) + (invMatrix.m12 * ray.direction[2]);
		final double dz = (invMatrix.m20 * ray.direction[0]) + (invMatrix.m21 * ray.direction[1]) + (invMatrix.m22 * ray.direction[2]);
		
		double d[] = new double[3];
		d[0] = dx;
		d[1] = dy;
		d[2] = dz;
		
		invRay.direction = d;
		
		final double t = object.hit(invRay, sr, stack);
		if (t != Double.NEGATIVE_INFINITY) {
			sr.normal = sr.normal.transformByMatrix(transposedInvMatrix).normalizedVector();
		}
		
//		if (!transformTexture) {
//			sr.localHitPoint = ray.origin.add(ray.direction.scale(t));
//		}

		return t;
	}

	@Override
	public double shadowHit(Ray ray, final Stack stack) {
		Ray invRay = new Ray(ray.origin);
		// apply the inverse set of transformations to the ray to produce an inverse transformed ray
		invRay.origin = ray.origin.transformByMatrix(invMatrix);
		final double dx = (invMatrix.m00 * ray.direction[0]) + (invMatrix.m01 * ray.direction[1]) + (invMatrix.m02 * ray.direction[2]);
		final double dy = (invMatrix.m10 * ray.direction[0]) + (invMatrix.m11 * ray.direction[1]) + (invMatrix.m12 * ray.direction[2]);
		final double dz = (invMatrix.m20 * ray.direction[0]) + (invMatrix.m21 * ray.direction[1]) + (invMatrix.m22 * ray.direction[2]);
		
		double d[] = new double[3];
		d[0] = dx;
		d[1] = dy;
		d[2] = dz;
		
		invRay.direction = d;
		final double t = object.shadowHit(invRay, stack);
		return t;
	}
	
	@Override
	public void applyTransformation(Matrix4d matrix) {
		super.applyTransformation(matrix);
		updateBoundingBox();
	}

	@Override
	public boolean isFinite(){
		return this.object.isFinite();
	}
	
	@Override
	public String toString() {
		return this.object.getClass().toString();
	}
}
