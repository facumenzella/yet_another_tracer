package ar.edu.itba.it.cg.yart.geometry.primitives.mesh;

import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.AABB;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;

public abstract class MeshTriangle extends GeometricObject {

	public Mesh mesh;

	public final int index0;
	public final int index1;
	public final int index2;
	
	public final Point3d p0;
	public final Point3d p1;
	public final Point3d p2;

	protected Vector3d normal;
	protected double area;

	public MeshTriangle(final int index0, final int index1, final int index2,
			final Mesh mesh, final boolean reverse) {
		// triangles should be counter-clockwise
		this.index0 = index0;
		this.index1 = index1;
		this.index2 = index2;
		this.mesh = mesh;
		this.computeNormal(reverse);
		
		p0 = mesh.vertices[index0];
		p1 = mesh.vertices[index1];
		p2 = mesh.vertices[index2];
	}

	private void computeNormal(boolean reverse) {
		normal = (mesh.vertices[index1].sub(mesh.vertices[index0]))
				.cross((mesh.vertices[index2].sub(mesh.vertices[index0])));
		normal = normal.normalizedVector();

		if (reverse)
			normal = normal.inverse();
	}

	protected double interpolateU(final double beta, final double gamma) {
		return (1 - beta - gamma) * mesh.u[index0] + beta
				* mesh.u[index1] + gamma * mesh.u[index2];
	}

	protected double interpolateV(final double beta, final double gamma) {
		return (1 - beta - gamma) * mesh.v[index0] + beta
				* mesh.v[index1] + gamma * mesh.v[index2];
	}

	@Override
	public AABB createBoundingBox() {
		Point3d v0 = mesh.vertices[index0];
		Point3d v1 = mesh.vertices[index1];
		Point3d v2 = mesh.vertices[index2];

		double minX = Math.min(v0.x, Math.min(v1.x, v2.x))  - EPSILON;
		double minY = Math.min(v0.y, Math.min(v1.y, v2.y))  - EPSILON;
		double minZ = Math.min(v0.z, Math.min(v1.z, v2.z))  - EPSILON;
		double maxX = Math.max(v0.x, Math.max(v1.x, v2.x))  + EPSILON;
		double maxY = Math.max(v0.y, Math.max(v1.y, v2.y))  + EPSILON;
		double maxZ = Math.max(v0.z, Math.max(v1.z, v2.z))  + EPSILON;

		return new AABB(new Point3d(minX, maxY, minZ), new Point3d(maxX, minY,
				maxZ));
	}

}
