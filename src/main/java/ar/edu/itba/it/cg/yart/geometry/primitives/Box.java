package ar.edu.itba.it.cg.yart.geometry.primitives;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.light.materials.Material;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class Box extends GeometricObject {

	private Point3d topCorner;
	private Point3d bottomCorner;
	private double width;
	private double height;
	private double depth;
	private List<GeometricObject> faces = new ArrayList<GeometricObject>();
	
	public Box() {
		initialize(1, 1, 1);
	}
	
	public Box(final double width, final double height, final double depth) {
		initialize(width, height, depth);
	}

	private void initialize(final double width, final double height,
			final double depth) {
		this.topCorner = new Point3d(width, height, depth);
		this.bottomCorner = new Point3d(-width, -height, -depth);

		final Vector3d widthTopVector = new Vector3d(-2 * width, 0, 0);
		final Vector3d heightTopVector = new Vector3d(0,-2 * height, 0);
		final Vector3d depthTopVector = new Vector3d(0, 0, -2 * depth);
		
		final Vector3d widthBottomVector = new Vector3d(2 * width, 0, 0);
		final Vector3d heightBottomVector = new Vector3d(0,2 * height, 0);
		final Vector3d depthBottomVector = new Vector3d(0, 0,2 * depth);
		
		createFaces(topCorner, widthTopVector, heightTopVector, depthTopVector);
		createFaces(bottomCorner, widthBottomVector, heightBottomVector, depthBottomVector);
		updateBoundingBox();

	}

	private void createFaces(final Point3d corner, final Vector3d width,
			final Vector3d height, final Vector3d depth) {
		final Quadrilateral face1 = new Quadrilateral(corner, width, height);
		final Quadrilateral face2 = new Quadrilateral(corner, width, depth);
		final Quadrilateral face3 = new Quadrilateral(corner, height, depth);
		faces.add(face1);
		faces.add(face2);
		faces.add(face3);
	}
	
	@Override
	public void setMaterial(final Material material) {
		super.setMaterial(material);
		for(final GeometricObject face : faces) {
			face.setMaterial(material);
		}
	}

	@Override
	public BoundingBox createBoundingBox() {
		return new BoundingBox(topCorner, bottomCorner);
	}

	@Override
	public double hit(Ray ray, ShadeRec sr) {
		double tMax = Double.MAX_VALUE;
		Vector3d normal = null;
		Point3d localHitPoint = null;
		double tMin = tMax;
		for(final GeometricObject face: faces) {
			double t = face.hit(ray, sr);
			if (t != Double.NEGATIVE_INFINITY && t < tMin) {
				sr.hitObject = true;
				sr.material = face.getMaterial();
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
	public double shadowHit(Ray ray) {
		double tMax = Double.MAX_VALUE;
		boolean hitObject = false;
		double tMin = tMax;
		for(final GeometricObject face: faces) {
			double t = face.shadowHit(ray);
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
