package ar.edu.itba.it.cg.yart.geometry.primitives;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.geometry.Instance;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class Box extends GeometricObject {

	private Point3d topCorner;
	private Point3d bottomCorner;

	private List<GeometricObject> faces = new ArrayList<GeometricObject>();

	public Box() {
		this(1, 1, 1);
	}

	public Box(final double width, final double height, final double depth) {
		this.topCorner = new Point3d(width, height, depth);
		this.bottomCorner = new Point3d(-width, -height, -depth);
		initialize(width, height, depth);
	}

	private void initialize(final double width, final double height,
			final double depth) {

		final Vector3d widthTopVector = new Vector3d(-2 * width, 0, 0);
		final Vector3d heightTopVector = new Vector3d(0, -2 * height, 0);
		final Vector3d depthTopVector = new Vector3d(0, 0, -2 * depth);

		final Quadrilateral frontFace = new Quadrilateral(topCorner,
				widthTopVector, heightTopVector);
		final Quadrilateral topFace = new Quadrilateral(topCorner,
				depthTopVector, widthTopVector);
		final Quadrilateral rightFace = new Quadrilateral(topCorner,
				heightTopVector, depthTopVector);

		final Vector3d widthBottomVector = new Vector3d(2 * width, 0, 0);
		final Vector3d heightBottomVector = new Vector3d(0, 2 * height, 0);
		final Vector3d depthBottomVector = new Vector3d(0, 0, 2 * depth);

		final Quadrilateral backFace = new Quadrilateral(bottomCorner,
				heightBottomVector, widthBottomVector);
		final Quadrilateral bottomFace = new Quadrilateral(bottomCorner,
				widthBottomVector, depthBottomVector);
		final Quadrilateral leftFace = new Quadrilateral(bottomCorner,
				depthBottomVector, heightBottomVector);
		
		faces.add(new Instance(frontFace));
		faces.add(new Instance(topFace));
		faces.add(new Instance(rightFace));
		faces.add(new Instance(backFace));
		faces.add(new Instance(bottomFace));
		faces.add(new Instance(leftFace));

		updateBoundingBox();

	}

	@Override
	public AABB createBoundingBox() {
		return new AABB(topCorner, bottomCorner);
	}

	@Override
	public double hit(Ray ray, ShadeRec sr, final double t_max, final Stack stack) {
		double tMax = Double.MAX_VALUE;
		Vector3d normal = null;
		Point3d localHitPoint = null;
		double tMin = tMax;
		for (final GeometricObject face : faces) {
			double t = face.hit(ray, sr, t_max, stack);
			if (t != Double.NEGATIVE_INFINITY && t < tMin) {
				sr.hitObject = true;
				sr.material = getMaterial();
				sr.hitPoint = sr.localHitPoint.transformByMatrix(face.matrix);
				normal = sr.normal;
				localHitPoint = sr.localHitPoint;
				tMin = t;
			}
		}

		if (sr.hitObject) {
			sr.depth = ray.depth;
			sr.t = tMin;
			sr.normal = normal;
			sr.localHitPoint = localHitPoint;
			sr.ray = ray;
			return tMin;
		}
		return Double.NEGATIVE_INFINITY;

	}

	@Override
	public double shadowHit(Ray ray, final double t_max,final Stack stack) {
		double tMax = Double.MAX_VALUE;
		boolean hitObject = false;
		double tMin = tMax;
		for (final GeometricObject face : faces) {
			double t = face.shadowHit(ray, t_max, stack);
			if (t != Double.NEGATIVE_INFINITY && t < tMin) {
				hitObject = true;
				tMin = t;
			}
		}

		if (hitObject) {
			return tMin;
		}
		return Double.NEGATIVE_INFINITY;
	}

}
