package ar.edu.itba.it.cg.yart.geometry;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.geometry.primitives.AABB;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.light.Sample;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.transforms.Matrix4d;

public class Instance extends GeometricObject {

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
	public double hit(Ray ray, ShadeRec sr, final double tMax, final Stack stack) {
		Ray invRay = new Ray();
		// apply the inverse set of transformations to the ray to produce an
		// inverse transformed ray
		final double px = (invMatrix.m00 * ray.origin.x) + (invMatrix.m01 * ray.origin.y) + (invMatrix.m02 * ray.origin.z) + invMatrix.m03;
		final double py = (invMatrix.m10 * ray.origin.x) + (invMatrix.m11 * ray.origin.y) + (invMatrix.m12 * ray.origin.z) + invMatrix.m13;
		final double pz = (invMatrix.m20 * ray.origin.x) + (invMatrix.m21 * ray.origin.y) + (invMatrix.m22 * ray.origin.z) + invMatrix.m23;
		invRay.origin = new Point3d(px, py, pz);

		final double dx = (invMatrix.m00 * ray.direction[0])
				+ (invMatrix.m01 * ray.direction[1])
				+ (invMatrix.m02 * ray.direction[2]);
		final double dy = (invMatrix.m10 * ray.direction[0])
				+ (invMatrix.m11 * ray.direction[1])
				+ (invMatrix.m12 * ray.direction[2]);
		final double dz = (invMatrix.m20 * ray.direction[0])
				+ (invMatrix.m21 * ray.direction[1])
				+ (invMatrix.m22 * ray.direction[2]);

		double d[] = new double[3];
		d[0] = dx;
		d[1] = dy;
		d[2] = dz;

		invRay.direction = d;

		final double t = object.hit(invRay, sr, tMax, stack);
		if (t != Double.NEGATIVE_INFINITY) {
			final double nx = (matrix.m00 * sr.normal.x) + (matrix.m01 * sr.normal.y)
					+ (matrix.m02 * sr.normal.z);
			final double ny = (matrix.m10 * sr.normal.x) + (matrix.m11 * sr.normal.y)
					+ (matrix.m12 * sr.normal.z);
			final double nz = (matrix.m20 * sr.normal.x) + (matrix.m21 * sr.normal.y)
					+ (matrix.m22 * sr.normal.z);
			final double length = Math.sqrt(nx*nx + ny*ny + nz*nz);
			
			sr.normal = new Vector3d(nx / length, ny / length, nz / length);
		}

		return t;
	}

	@Override
	public double shadowHit(Ray ray, final double tMax,final Stack stack) {
		Ray invRay = new Ray();
		// apply the inverse set of transformations to the ray to produce an
		// inverse transformed ray
		final double px = (invMatrix.m00 * ray.origin.x) + (invMatrix.m01 * ray.origin.y) + (invMatrix.m02 * ray.origin.z) + invMatrix.m03;
		final double py = (invMatrix.m10 * ray.origin.x) + (invMatrix.m11 * ray.origin.y) + (invMatrix.m12 * ray.origin.z) + invMatrix.m13;
		final double pz = (invMatrix.m20 * ray.origin.x) + (invMatrix.m21 * ray.origin.y) + (invMatrix.m22 * ray.origin.z) + invMatrix.m23;
		invRay.origin = new Point3d(px, py, pz);
		final double dx = (invMatrix.m00 * ray.direction[0])
				+ (invMatrix.m01 * ray.direction[1])
				+ (invMatrix.m02 * ray.direction[2]);
		final double dy = (invMatrix.m10 * ray.direction[0])
				+ (invMatrix.m11 * ray.direction[1])
				+ (invMatrix.m12 * ray.direction[2]);
		final double dz = (invMatrix.m20 * ray.direction[0])
				+ (invMatrix.m21 * ray.direction[1])
				+ (invMatrix.m22 * ray.direction[2]);

		double d[] = new double[3];
		d[0] = dx;
		d[1] = dy;
		d[2] = dz;

		invRay.direction = d;
		final double t = object.shadowHit(invRay, tMax, stack);
		return t;
	}
	
	@Override
	public Sample getSample() {
		Sample ret = object.getSample();
		ret.point = ret.point.transformByMatrix(matrix);
		ret.normal = ret.normal.transformByMatrix(matrix).normalizeMe();
		return ret;
	}
	
	@Override
	public double pdf() {
		return object.pdf() / getBoundingBox().surfaceArea;
	}

	@Override
	public void setCastsShadows(boolean castsShadows) {
		object.setCastsShadows(castsShadows);
	}
	
	@Override
	public boolean isCastsShadows() {
		return object.isCastsShadows();
	}
	
	@Override
	public void applyTransformation(Matrix4d matrix) {
		super.applyTransformation(matrix);
		updateBoundingBox();
	}

	@Override
	public boolean isFinite() {
		return this.object.isFinite();
	}

	@Override
	public String toString() {
		return this.object.getClass().toString();
	}
}
