package ar.edu.itba.it.cg.yart.geometry.primitives.mesh;

import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.BoundingBox;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;

public abstract class MeshTriangle extends GeometricObject {
	
	public Mesh mesh;
	
	public int index0;
	public int index1;
	public int index2;
	
	protected Vector3d normal;
	protected double area;
	
	public MeshTriangle(final int index0, final int index1, final int index2, final Mesh mesh, final boolean reverse) {
		// triangles should be counter-sclockwise
		this.index0 = index0;
		this.index1 = index1;
		this.index2 = index2;
		this.mesh = mesh;
		this.computeNormal(reverse);
	}
	
	private void computeNormal(boolean reverse) {
		normal = (mesh.vertices.get(index1).sub(mesh.vertices.get(index0))).cross((mesh.vertices.get(index2).sub(mesh.vertices.get(index0))));
		normal = normal.normalized;
		
		if (reverse)
			normal = normal.inverse();
	}
	
	@Override
	public BoundingBox createBoundingBox() {
		Point3 v0 = mesh.vertices.get(index0);
		Point3 v1 = mesh.vertices.get(index1);
		Point3 v2 = mesh.vertices.get(index2);
		
		double minX = Math.min(v0.x - EPSILON, Math.min(v1.x, v2.x));
		double minY = Math.min(v0.y - EPSILON, Math.min(v1.y, v2.y));
		double minZ = Math.min(v0.z - EPSILON, Math.min(v1.z, v2.z));
		double maxX = Math.max(v0.x + EPSILON, Math.max(v1.x, v2.x));
		double maxY = Math.max(v0.y + EPSILON, Math.max(v1.y, v2.y));
		double maxZ = Math.max(v0.z + EPSILON, Math.max(v1.z, v2.z));

		return new BoundingBox(new Point3(minX, minY, minZ), new Point3(maxX, maxY, maxZ));
	}

}
