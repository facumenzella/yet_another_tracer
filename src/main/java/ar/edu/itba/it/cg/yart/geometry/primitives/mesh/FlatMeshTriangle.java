package ar.edu.itba.it.cg.yart.geometry.primitives.mesh;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class FlatMeshTriangle extends MeshTriangle {

	public FlatMeshTriangle(int index0, int index1, int index2,
			final Mesh mesh, final boolean reverse) {
		super(index0, index1, index2, mesh, reverse);
		updateBoundingBox();
	}

	@Override
	public double hit(Ray ray, ShadeRec sr, final double tMax, final Stack stack) {
		if (!getBoundingBox().hit(ray)) {
			return Double.NEGATIVE_INFINITY;
		}

		double a = p0.x - p1.x, b = p0.x - p2.x, c = ray.direction[0], d = p0.x
				- ray.origin.x;
		double e = p0.y - p1.y, f = p0.y - p2.y, g = ray.direction[1], h = p0.y
				- ray.origin.y;
		double i = p0.z - p1.z, j = p0.z - p2.z, k = ray.direction[2], l = p0.z
				- ray.origin.z;

		double m = f * k - g * j, n = h * k - g * l, p = f * l - h * j;
		double q = g * i - e * k, r = e * l - h * i, s = e * j - f * i;

		double inv_denom = 1.0 / (a * m + b * q + c * s);

		double e1 = d * m - b * n - c * p;
		double beta = e1 * inv_denom;

		if (beta < 0.0 || beta > 1.0) {
			return Double.NEGATIVE_INFINITY;
		}

		double e2 = a * n + d * q + c * r;
		double gamma = e2 * inv_denom;

		if (gamma < 0.0 || gamma > 1.0) {
			return Double.NEGATIVE_INFINITY;
		}

		if (beta + gamma > 1.0) {
			return Double.NEGATIVE_INFINITY;
		}

		double e3 = a * p - b * r + d * s;
		double t = e3 * inv_denom;

		if (t < EPSILON) {
			return Double.NEGATIVE_INFINITY;
		}

		sr.normal = normal;
		// ray.origin.add(ray.direction.scale(t));
		double x = ray.origin.x + (ray.direction[0] * t);
		double y = ray.origin.y + (ray.direction[1] * t);
		double z = ray.origin.z + (ray.direction[2] * t);

		sr.localHitPoint = new Point3d(x, y, z);
		if (mesh.u != null && mesh.v != null) {
			sr.u = interpolateU(beta, gamma);
			sr.v = interpolateV(beta, gamma);
		}

		return t;
	}

	@Override
	public double shadowHit(Ray ray, final Stack stack) {
		if (!getBoundingBox().hit(ray)) {
			return Double.NEGATIVE_INFINITY;
		}

		Point3d v0 = mesh.vertices[index0];
		Point3d v1 = mesh.vertices[index1];
		Point3d v2 = mesh.vertices[index2];

		double a = v0.x - v1.x, b = v0.x - v2.x, c = ray.direction[0], d = v0.x
				- ray.origin.x;
		double e = v0.y - v1.y, f = v0.y - v2.y, g = ray.direction[1], h = v0.y
				- ray.origin.y;
		double i = v0.z - v1.z, j = v0.z - v2.z, k = ray.direction[2], l = v0.z
				- ray.origin.z;

		double m = f * k - g * j, n = h * k - g * l, p = f * l - h * j;
		double q = g * i - e * k, r = e * l - h * i, s = e * j - f * i;

		double inv_denom = 1.0 / (a * m + b * q + c * s);

		double e1 = d * m - b * n - c * p;
		double beta = e1 * inv_denom;

		if (beta < 0.0 || beta > 1.0) {
			return Double.NEGATIVE_INFINITY;
		}

		double e2 = a * n + d * q + c * r;
		double gamma = e2 * inv_denom;

		if (gamma < 0.0 || gamma > 1.0) {
			return Double.NEGATIVE_INFINITY;
		}

		if (beta + gamma > 1.0) {
			return Double.NEGATIVE_INFINITY;
		}

		double e3 = a * p - b * r + d * s;
		double t = e3 * inv_denom;

		if (t < EPSILON) {
			return Double.NEGATIVE_INFINITY;
		}

		return t;
	}

}
