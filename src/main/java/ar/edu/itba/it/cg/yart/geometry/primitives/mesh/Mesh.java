package ar.edu.itba.it.cg.yart.geometry.primitives.mesh;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.BoundingBox;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.light.materials.Material;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.raytracer.tracer.SimpleTracer;
import ar.edu.itba.it.cg.yart.raytracer.tracer.Tracer;

public class Mesh extends GeometricObject {
	
	public List<Point3> vertices;
	public List<Integer> indices;
	public List<Vector3d> normals;
	public List<List<Integer>> faces;
	public List<GeometricObject> triangles;
	public int numVertices;
	public int numTriangles;
	Tracer tracer;
	
	int verticesAmount;
	int trianglesAmount;
	
	public Mesh() {
		vertices = new ArrayList<Point3>();
		indices = new ArrayList<Integer>();
		normals = new ArrayList<Vector3d>();
		faces = new ArrayList<List<Integer>>();
		triangles = new ArrayList<GeometricObject>();
		tracer = new SimpleTracer();
		
		vertices.add(new Point3(-50, 50, -50));
		vertices.add(new Point3(-50, 0, 50));
		vertices.add(new Point3(50, 50, 50));
		vertices.add(new Point3(50, 0, -50));
		
		MeshTriangle t1 = new FlatMeshTriangle(0, 2, 3);
		MeshTriangle t2 = new FlatMeshTriangle(0, 1, 2);
		t1.mesh = this;
		t1.computeNormal(false);
		t2.mesh = this;
		t2.computeNormal(false);
		triangles.add(t1);
		triangles.add(t2);
		
		this.numVertices = vertices.size();
		this.numTriangles = triangles.size();
	}
	
	@Override
	public void setMaterial(Material material) {
		super.setMaterial(material);
		
		for (GeometricObject t : triangles) {
			t.setMaterial(material);
		}
	}

	@Override
	public BoundingBox createBoundingBox() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double hit(Ray ray, ShadeRec sr) {
		return 0;
		//return triangles.get(0).hit(ray, sr);
	}

	@Override
	public double shadowHit(Ray ray) {
		return Double.NEGATIVE_INFINITY;
	}

}
