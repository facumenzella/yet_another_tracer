package ar.edu.itba.it.cg.yart.geometry;

import ar.edu.itba.it.cg.yart.geometry.primitives.BoundingBox;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.transforms.Matrix4d;

public class Instance extends GeometricObject{

	private GeometricObject object; // object to be transformed
	public boolean transformTexture; // do we transform the texture

	public Instance(final GeometricObject object) {
		this.object = object;
		this.matrix = new Matrix4d();
		this.invMatrix = this.matrix.inverse();
		this.transposedInvMatrix = this.invMatrix.transpose();
	}
	
	@Override
	public BoundingBox createBoundingBox() {
		BoundingBox boundingBox = object.createBoundingBox();
		boundingBox.applyTransformation(matrix);
		return boundingBox;
	}

	@Override
	public double hit(Ray ray, ShadeRec sr) {
		Ray invRay = new Ray(ray.origin);
		// apply the inverse set of transformations to the ray to produce an inverse transformed ray
		invRay.origin = ray.origin.transformByMatrix(invMatrix);
		invRay.direction = ray.direction.transformByMatrix(invMatrix);
		
		final double t = object.hit(invRay, sr);
		if (t != Double.NEGATIVE_INFINITY) {
			sr.normal = sr.normal.transformByMatrix(transposedInvMatrix).normalizedVector();
		}
		
		if (object.material != null) {
			this.material = object.material;
		}
		
		if (!transformTexture) {
			sr.localHitPoint = ray.origin.add(ray.direction.scale(t));
		}
		return t;
	}

	@Override
	public double shadowHit(Ray ray) {
		Ray invRay = new Ray(ray.origin);
		// apply the inverse set of transformations to the ray to produce an inverse transformed ray
		invRay.origin = ray.origin.transformByMatrix(invMatrix);
		invRay.direction = ray.direction.transformByMatrix(invMatrix);
		
		final double t = object.shadowHit(invRay);
		return t;
	}

}
