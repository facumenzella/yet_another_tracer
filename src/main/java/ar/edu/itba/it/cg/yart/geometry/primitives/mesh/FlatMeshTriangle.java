package ar.edu.itba.it.cg.yart.geometry.primitives.mesh;

import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.primitives.BoundingBox;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class FlatMeshTriangle extends MeshTriangle {

	public FlatMeshTriangle(int index0, int index1, int index2) {
		super(index0, index1, index2);
	}

	@Override
	public BoundingBox createBoundingBox() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double hit(Ray ray, ShadeRec sr) {
		Point3 v0 = mesh.vertices.get(index0);
		Point3 v1 = mesh.vertices.get(index1);
		Point3 v2 = mesh.vertices.get(index2);
		
		double a = v0.x - v1.x, b = v0.x - v2.x, c = ray.direction.x, d = v0.x - ray.origin.x; 
		double e = v0.y - v1.y, f = v0.y - v2.y, g = ray.direction.y, h = v0.y - ray.origin.y;
		double i = v0.z - v1.z, j = v0.z - v2.z, k = ray.direction.z, l = v0.z - ray.origin.z;
			
		double m = f * k - g * j, n = h * k - g * l, p = f * l - h * j;
		double q = g * i - e * k, r = e * l - h * i, s = e * j - f * i;
		
		double inv_denom  = 1.0 / (a * m + b * q + c * s);
		
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

		sr.normal = normal; // for flat shading
		sr.localHitPoint = ray.origin.add(ray.direction.scale(t));

		return t;
	}

	@Override
	public double shadowHit(Ray ray) {
		return Double.NEGATIVE_INFINITY;
	}

}
