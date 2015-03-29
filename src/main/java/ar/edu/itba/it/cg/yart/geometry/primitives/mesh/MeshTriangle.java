package ar.edu.itba.it.cg.yart.geometry.primitives.mesh;

import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;

public abstract class MeshTriangle extends GeometricObject {
	
	public Mesh mesh;
	
	public int index0;
	public int index1;
	public int index2;
	
	protected Vector3d normal;
	protected double area;
	
	public MeshTriangle(final int index0, final int index1, final int index2) {
		this.index0 = index0;
		this.index1 = index1;
		this.index2 = index2;
	}
	
	protected void computeNormal(boolean reverse) {
		normal = (mesh.vertices.get(index1).sub(mesh.vertices.get(index0))).cross((mesh.vertices.get(index2).sub(mesh.vertices.get(index0))));
		normal = normal.normalized;
		
		if (reverse)
			normal = normal.inverse();
	}

}
