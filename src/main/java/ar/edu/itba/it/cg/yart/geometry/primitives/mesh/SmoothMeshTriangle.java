package ar.edu.itba.it.cg.yart.geometry.primitives.mesh;

import ar.edu.itba.it.cg.yart.geometry.MutableVector3d;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class SmoothMeshTriangle extends MeshTriangle{
	
	public SmoothMeshTriangle(int index0, int index1, int index2, Mesh mesh, final boolean reverse) {
		super(index0, index1, index2, mesh, reverse);
		updateBoundingBox();
	}

	@Override
	public double hit(Ray ray, ShadeRec sr) {
		if (!getBoundingBox().hit(ray)) {
			return Double.NEGATIVE_INFINITY;
		}
		
		Point3d v0 = mesh.vertices.get(index0);
		Point3d v1 = mesh.vertices.get(index1);
		Point3d v2 = mesh.vertices.get(index2);
		
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
		sr.normal = this.interpolateNormal(beta, gamma);;
		sr.localHitPoint = ray.origin.add(ray.direction.scale(t));
		if (mesh.u != null && mesh.v != null) {
			sr.u = interpolateU(beta, gamma);
			sr.v = interpolateV(beta, gamma);
		}
		
		return t;
	}

	@Override
	public double shadowHit(Ray ray) {
		if (!getBoundingBox().hit(ray)) {
			return Double.NEGATIVE_INFINITY;
		}
		
		Point3d v0 = mesh.vertices.get(index0);
		Point3d v1 = mesh.vertices.get(index1);
		Point3d v2 = mesh.vertices.get(index2);
		
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
		
		return t;
	}
	
	public Vector3d interpolateNormal(final double beta, final double gamma) {
		final Mesh mesh = this.mesh;
		final MutableVector3d n0 = new MutableVector3d(mesh.normals.get(index0).scale(1 - beta - gamma));
		final Vector3d n1 = mesh.normals.get(index1).scale(beta);
		final Vector3d n2 = mesh.normals.get(index1).scale(gamma);
		n0.add(n1);
		n0.add(n2);
		return n0.normalize();
	}

}
