package ar.edu.itba.it.cg.yart.geometry.primitives.mesh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.nlog2n.YAFKDTree;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.AABB;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.light.Sample;
import ar.edu.itba.it.cg.yart.light.materials.Material;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class Mesh extends GeometricObject {

	public Point3d[] vertices;
	public double[] u;
	public double[] v;
	public int[] indices;
	public Vector3d[] normals; // average normal at each vertex
	public Map<Integer, List<MeshTriangle>> faces; // the faces shared by each
													// vertex, we need it for
													// smooth mesh
	public List<GeometricObject> triangles;
	
	public int numVertices;
	public int numTriangles;
	private YAFKDTree kdTree;

	private double minX = Double.POSITIVE_INFINITY;
	private double maxX = Double.NEGATIVE_INFINITY;
	private double minY = Double.POSITIVE_INFINITY;
	private double maxY = Double.NEGATIVE_INFINITY;
	private double minZ = Double.POSITIVE_INFINITY;
	private double maxZ = Double.NEGATIVE_INFINITY;
	private double totalArea = 0;
	private double invArea = 0;
	private boolean needsSmoothing;

	int verticesAmount;
	int trianglesAmount;
	
	private boolean preprocessed = false;

	public Mesh(final Point3d[] vertices, final Vector3d[] normals,
			final int[] indices, final double[] u, final double[] v,
			final boolean needsSmoothing) {
		this.u = u;
		this.v = v;

		this.vertices = vertices;
		this.normals = normals;
		this.indices = indices;
		this.needsSmoothing = needsSmoothing;
		this.triangles = new ArrayList<GeometricObject>();
		this.faces = new HashMap<Integer, List<MeshTriangle>>();

		final int iterations = indices.length / 3;
		for (int i = 0; i < iterations; i++) {
			Integer v1 = indices[i * 3 + 2];
			Integer v2 = indices[i * 3 + 1];
			Integer v3 = indices[i * 3];

			List<MeshTriangle> facesV1 = this.faces.get(v1);
			List<MeshTriangle> facesV2 = this.faces.get(v2);
			List<MeshTriangle> facesV3 = this.faces.get(v3);

			if (facesV1 == null) {
				facesV1 = new ArrayList<MeshTriangle>();
				this.faces.put(v1, facesV1);
			}
			if (facesV2 == null) {
				facesV2 = new ArrayList<MeshTriangle>();
				this.faces.put(v2, facesV2);
			}
			if (facesV3 == null) {
				facesV3 = new ArrayList<MeshTriangle>();
				this.faces.put(v3, facesV3);
			}
			MeshTriangle t;
			if (needsSmoothing) {
				t = new SmoothMeshTriangle(v1, v2, v3, this, true);
			} else {
				t = new FlatMeshTriangle(v1, v2, v3, this, true);
			}

			facesV1.add(t);
			facesV2.add(t);
			facesV3.add(t);

			this.addTriangle(t);
		}
	}

	public void preprocess() {
		if (!preprocessed) {
			numTriangles += triangles.size();
			this.numVertices = vertices.length;
			this.updateBoundingBox();
			
			kdTree = YAFKDTree.build(this.triangles, this.getBoundingBox());

			if (needsSmoothing) {
				this.computeMeshNormals();
			}
			
			preprocessed = true;
		}
	}

	private void addTriangle(final MeshTriangle t) {
		AABB b = t.getBoundingBox();

		minX = Math.min(minX, b.p0.x);
		minZ = Math.min(minZ, b.p0.z);
		minY = Math.min(minY, b.p1.y);
		maxX = Math.max(maxX, b.p1.x);
		maxY = Math.max(maxY, b.p0.y);
		maxZ = Math.max(maxZ, b.p1.z);
		
		totalArea += t.area;
		invArea = 1 / totalArea;

		this.triangles.add(t);
	}
	
	@Override
	public double pdf() {
		return invArea;
	}

	private void computeMeshNormals() {
		if (normals == null) {
			normals = new Vector3d[vertices.length];
			for (int i = 0; i < vertices.length; i++) {
				Vector3d normal = new Vector3d(0, 0, 0);
				List<MeshTriangle> ts = this.faces.get(i);
				if (ts != null) {
					for (MeshTriangle meshTriangle : ts) {
						Vector3d n = meshTriangle.normal;
						normal = normal.add(n);
					}
				}
				normals[i] = normal;
			}
		}
	}

	@Override
	public void setMaterial(Material material) {
		super.setMaterial(material);

		for (GeometricObject t : triangles) {
			t.setMaterial(material);
		}
	}
	
	@Override
	public Sample getSample() {
		return triangles.get(ThreadLocalRandom.current().nextInt(triangles.size())).getSample();
	}

	@Override
	public AABB createBoundingBox() {
		return new AABB(new Point3d(minX, maxY, minZ), new Point3d(maxX, minY,
				maxZ));
	}

	@Override
	public double hit(Ray ray, ShadeRec sr, final double tMax, final Stack stack) {
		return kdTree.traceRayHit(ray, sr, tMax, stack);
	}

	@Override
	public double shadowHit(Ray ray, final double tMax,final Stack stack) {
		return kdTree.traceShadowHit(ray, tMax, stack);
	}

}
