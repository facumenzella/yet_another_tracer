package ar.edu.itba.it.cg.yart.geometry.primitives.mesh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.nlog2n.YAFKDTree2;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.AABB;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.light.materials.Material;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.raytracer.tracer.HitTracer;
import ar.edu.itba.it.cg.yart.raytracer.tracer.ShadowTracer;
import ar.edu.itba.it.cg.yart.raytracer.tracer.SimpleHitTracer;
import ar.edu.itba.it.cg.yart.raytracer.tracer.SimpleShadowTracer;

public class Mesh extends GeometricObject {

	public List<Point3d> vertices;
	public double[] u;
	public double[] v;
	public List<Integer> indices;
	public List<Vector3d> normals; // average normal at each vertex
	public Map<Integer, List<MeshTriangle>> faces; // the faces shared by each
													// vertex, we need it for
													// smooth mesh
	public List<GeometricObject> triangles;

	public int numVertices;
	public int numTriangles;
	private YAFKDTree2 kdTree;

	private double minX = Double.POSITIVE_INFINITY;
	private double maxX = Double.NEGATIVE_INFINITY;
	private double minY = Double.POSITIVE_INFINITY;
	private double maxY = Double.NEGATIVE_INFINITY;
	private double minZ = Double.POSITIVE_INFINITY;
	private double maxZ = Double.NEGATIVE_INFINITY;
	private boolean needsSmoothing;

	int verticesAmount;
	int trianglesAmount;

	private HitTracer hitTracer;
	private ShadowTracer shadowTracer;

	public Mesh(final List<Point3d> vertices, final List<Vector3d> normals,
			final List<Integer> indices, final double[] u,
			final double[] v, final boolean needsSmoothing) {
		this.hitTracer = new SimpleHitTracer();
		this.shadowTracer = new SimpleShadowTracer();

		this.u = u;
		this.v = v;
		
		this.vertices = vertices;
		this.normals = normals;
		this.indices = indices;
		this.needsSmoothing = needsSmoothing;
		this.triangles = new ArrayList<GeometricObject>();
		this.faces = new HashMap<Integer, List<MeshTriangle>>();

		final int iterations = this.indices.size() / 3;
		for (int i = 0; i < iterations; i++) {
			Integer v1 = indices.get(i * 3 + 2);
			Integer v2 = indices.get(i * 3 + 1);
			Integer v3 = indices.get(i * 3);

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

		this.finish();
	}

	private void finish() {
		this.numTriangles = triangles.size();
		this.numVertices = vertices.size();
		this.updateBoundingBox();
		double max = Math.max(
				maxZ,
				Math.max(maxY,
						Math.max(maxX, Math.max(minZ, Math.max(minX, minY)))));
		kdTree = YAFKDTree2.build(this.triangles, max);

		if (needsSmoothing) {
			this.computeMeshNormals();
		}
	}

	private void addTriangle(final MeshTriangle t) {
		AABB b = t.getBoundingBox();

		minX = Math.min(minX, b.p0.x);
		minZ = Math.min(minZ, b.p0.z);
		minY = Math.min(minY, b.p0.y);
		maxX = Math.max(maxX, b.p1.x);
		maxY = Math.max(maxY, b.p1.y);
		maxZ = Math.max(maxZ, b.p1.z);

		this.triangles.add(t);
	}

	private void computeMeshNormals() {
		this.normals = new ArrayList<Vector3d>(this.vertices.size());
		for (int i = 0; i < this.vertices.size(); i++) {
			Vector3d normal = new Vector3d(0, 0, 0);
			List<MeshTriangle> ts = this.faces.get(i);
			if (ts != null) {
				for (MeshTriangle meshTriangle : ts) {
					Vector3d n = meshTriangle.normal;
					normal = normal.add(n);
				}
			}
			this.normals.add(i, normal);
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
	public AABB createBoundingBox() {
		return new AABB(new Point3d(minX, maxY, minZ), new Point3d(maxX, minY,
				maxZ));
	}

	@Override
	public double hit(Ray ray, ShadeRec sr) {
//		if (!getBoundingBox().hit(ray)) {
//			return Double.NEGATIVE_INFINITY;
//		}
		return kdTree.traceRayHit(ray, this.hitTracer, sr);
	}

	@Override
	public double shadowHit(Ray ray) {
//		if (!getBoundingBox().hit(ray)) {
//		return Double.NEGATIVE_INFINITY;
//	}
		return kdTree.traceShadowHit(ray, this.shadowTracer);
	}

}
