package ar.edu.itba.it.cg.yart.geometry.primitives.mesh;

import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.AABB;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;

public abstract class MeshTriangle extends GeometricObject {

	public Mesh mesh;

	public int index0;
	public int index1;
	public int index2;

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
	}

	private void computeNormal(boolean reverse) {
		normal = (mesh.vertices.get(index1).sub(mesh.vertices.get(index0)))
				.cross((mesh.vertices.get(index2).sub(mesh.vertices.get(index0))));
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
		Point3d v0 = mesh.vertices.get(index0);
		Point3d v1 = mesh.vertices.get(index1);
		Point3d v2 = mesh.vertices.get(index2);

		double minX = Math.min(v0.x - EPSILON, Math.min(v1.x, v2.x));
		double minY = Math.min(v0.y - EPSILON, Math.min(v1.y, v2.y));
		double minZ = Math.min(v0.z - EPSILON, Math.min(v1.z, v2.z));
		double maxX = Math.max(v0.x + EPSILON, Math.max(v1.x, v2.x));
		double maxY = Math.max(v0.y + EPSILON, Math.max(v1.y, v2.y));
		double maxZ = Math.max(v0.z + EPSILON, Math.max(v1.z, v2.z));

		return new AABB(new Point3d(minX, maxY, minZ), new Point3d(maxX, minY,
				maxZ));
	}

}
