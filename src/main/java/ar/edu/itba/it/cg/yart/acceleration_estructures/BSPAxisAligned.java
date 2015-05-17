package ar.edu.itba.it.cg.yart.acceleration_estructures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.BoundingBox;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.raytracer.tracer.ColorTracer;
import ar.edu.itba.it.cg.yart.raytracer.tracer.HitTracer;
import ar.edu.itba.it.cg.yart.raytracer.tracer.ShadowTracer;

public class BSPAxisAligned {

	private Node root;
	private BoundingBox initialBox;
	private final double tMin;
	private final double tMax;
	private final double minZ;
	private final double maxZ;
	private final double minY;
	private final double maxY;
	private final double minX;
	private final double maxX;
	
	private final LeafNode emptyLeafNode;

	private static final int DEPTH = 10;
	protected static final double EPSILON = 0.00001;

	public BSPAxisAligned(final double minX, final double maxX, final double minY, final double maxY, 
			final double minZ, final double maxZ,
			final double tMin, final double tMax) {
		this.tMin = tMin;
		this.tMax = tMax;
		this.minZ = minZ;
		this.maxZ = maxZ;
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		this.emptyLeafNode = new LeafNode(new ArrayList<GeometricObject>());
		this.buildInitialBox();
	}
	
	public BSPAxisAligned(final double minZ, final double maxZ,
			final double tMin, final double tMax) {
		this(-1000, 1000, -1000, 1000, minZ, maxZ, tMin, tMax);
	}

	public void buildTree(final List<GeometricObject> objects) {
		this.root = subdivideXAxis(initialBox, objects, 0);
	}

	private void buildInitialBox() {
		this.initialBox = new BoundingBox(new Point3d(minX, minY, minZ),
				new Point3d(maxX, maxY, maxZ));
	}

	private Node subdivideXAxis(final BoundingBox currentBox,
			final List<GeometricObject> currentObjects, final int currentDepth) {
		if (currentObjects.size() == 0) {
			return this.emptyLeafNode;
		}

		if (currentDepth == DEPTH) {
			return new LeafNode(currentObjects);
		}
		
		Set<Double> candidates = this.planeCandidatesX(currentObjects);
		final double splittingPoint = this.bestXCandidate(candidates,
				currentBox, currentObjects);
		
		final double leafCost = this.leafCost(currentBox, currentObjects);
		final double splittingCost = this.planeCostX(currentBox, currentObjects, splittingPoint);
		
		if (splittingCost / leafCost > 1) {
			return new LeafNode(currentObjects);
		}

		final BoundingBox leftBox = new BoundingBox(currentBox.p0, new Point3d(
				splittingPoint, currentBox.p1.y, currentBox.p1.z));
		final BoundingBox rightBox = new BoundingBox(new Point3d(splittingPoint,
				currentBox.p0.y, currentBox.p0.z), currentBox.p1);

		List<GeometricObject> leftObjects = new ArrayList<GeometricObject>();
		List<GeometricObject> rightObjects = new ArrayList<GeometricObject>();

		for (GeometricObject o : currentObjects) {
			this.addObjectToBoxes(leftBox, rightBox, o, leftObjects,
					rightObjects);
		}

		XNode xNode = new XNode(currentBox, currentObjects, splittingPoint);
		xNode.setLeftNode(subdivideYAxis(leftBox, leftObjects, currentDepth + 1));
		xNode.setRightNode(subdivideYAxis(rightBox, rightObjects, currentDepth + 1));

		return xNode;
	}

	private Node subdivideYAxis(final BoundingBox currentBox,
			final List<GeometricObject> currentObjects, final int currentDepth) {

		if (currentObjects.size() == 0) {
			return this.emptyLeafNode;
		}

		if (currentDepth == DEPTH) {
			return new LeafNode(currentObjects);
		}

		Set<Double> candidates = this.planeCandidatesY(currentObjects);
		final double splittingPoint = this.bestYCandidate(candidates,
				currentBox, currentObjects);

		final double leafCost = this.leafCost(currentBox, currentObjects);
		final double splittingCost = this.planeCostX(currentBox, currentObjects, splittingPoint);
		
		if (splittingCost / leafCost > 1) {
			return new LeafNode(currentObjects);
		}
		
		final BoundingBox leftBox = new BoundingBox(currentBox.p0, new Point3d(
				currentBox.p1.x, splittingPoint, currentBox.p1.z));
		final BoundingBox rightBox = new BoundingBox(new Point3d(
				currentBox.p0.x, splittingPoint, currentBox.p0.z), currentBox.p1);

		List<GeometricObject> leftObjects = new ArrayList<GeometricObject>();
		List<GeometricObject> rightObjects = new ArrayList<GeometricObject>();

		for (GeometricObject o : currentObjects) {
			this.addObjectToBoxes(leftBox, rightBox, o, leftObjects,
					rightObjects);

		}

		YNode yNode = new YNode(currentBox, currentObjects, splittingPoint);
		yNode.setLeftNode(subdivideZAxis(leftBox, leftObjects, currentDepth + 1));
		yNode.setRightNode(subdivideZAxis(rightBox, rightObjects, currentDepth + 1));

		return yNode;
	}

	private Node subdivideZAxis(final BoundingBox currentBox,
			final List<GeometricObject> currentObjects, final int currentDepth) {

		if (currentObjects.size() == 0) {
			return this.emptyLeafNode;
		}

		if (currentDepth == DEPTH) {
			return new LeafNode(currentObjects);
		}

		Set<Double> candidates = this.planeCandidatesZ(currentObjects);
		final double splittingPoint = this.bestZCandidate(candidates,
				currentBox, currentObjects);

		final double leafCost = this.leafCost(currentBox, currentObjects);
		final double splittingCost = this.planeCostX(currentBox, currentObjects, splittingPoint);
		
		if (splittingCost / leafCost > 1) {
			return new LeafNode(currentObjects);
		}
		
		final BoundingBox leftBox = new BoundingBox(currentBox.p0, new Point3d(
				currentBox.p1.x, currentBox.p1.y, splittingPoint));
		final BoundingBox rightBox = new BoundingBox(new Point3d(
				currentBox.p0.x, currentBox.p0.y, splittingPoint), currentBox.p1);

		List<GeometricObject> leftObjects = new ArrayList<GeometricObject>();
		List<GeometricObject> rightObjects = new ArrayList<GeometricObject>();

		for (GeometricObject o : currentObjects) {
			this.addObjectToBoxes(leftBox, rightBox, o, leftObjects,
					rightObjects);
		}

		ZNode zNode = new ZNode(currentBox, currentObjects, splittingPoint);
		zNode.setLeftNode(subdivideXAxis(leftBox, leftObjects, currentDepth + 1));
		zNode.setRightNode(subdivideXAxis(rightBox, rightObjects, currentDepth + 1));

		return zNode;
	}

	private void addObjectToBoxes(final BoundingBox leftBox,
			final BoundingBox rightBox, final GeometricObject o,
			final List<GeometricObject> leftObjects,
			final List<GeometricObject> rightObjects) {
		final BoundingBox box = o.getBoundingBox();
		if (box == null) {
			leftObjects.add(o);
			rightObjects.add(o);
		} else {
			if (leftBox.boxIsInside(box)) {
				leftObjects.add(o);
			}
			if (rightBox.boxIsInside(box)) {
				rightObjects.add(o);
			}
		}
	}

	public Set<Double> planeCandidatesX(final List<GeometricObject> objects) {
		Set<Double> candidates = new HashSet<Double>();
		for (GeometricObject o : objects) {
			BoundingBox b = o.getBoundingBox();
			if (b != null) {
				candidates.add(b.p0.x);
				candidates.add(b.p1.x);
			}
		}
		return candidates;
	}

	public Set<Double> planeCandidatesY(final List<GeometricObject> objects) {
		Set<Double> candidates = new HashSet<Double>();
		for (GeometricObject o : objects) {
			BoundingBox b = o.getBoundingBox();
			if (b != null) {
				candidates.add(b.p0.y);
				candidates.add(b.p1.y);
			}
		}
		return candidates;
	}

	public Set<Double> planeCandidatesZ(final List<GeometricObject> objects) {
		Set<Double> candidates = new HashSet<Double>();
		for (GeometricObject o : objects) {
			BoundingBox b = o.getBoundingBox();
			if (b != null) {
				candidates.add(b.p0.z);
				candidates.add(b.p1.z);
			}
		}
		return candidates;
	}

	public double planeCostX(final BoundingBox currentBox,
			final List<GeometricObject> objects, final double middlePoint) {
		double left = 0;
		double right = 0;
		double stradding = 0;
		for (GeometricObject o : objects) {
			BoundingBox b = o.getBoundingBox();
			if (b != null) {
				if (b.p0.x > middlePoint) {
					right++;
				} else if (b.p1.x < middlePoint) {
					left++;
				} else {
					stradding++;
				}
			} else {
				stradding++;
			}
		}

		final BoundingBox leftBox = new BoundingBox(currentBox.p0, new Point3d(
				middlePoint, currentBox.p1.y, currentBox.p1.z));
		final BoundingBox rightBox = new BoundingBox(new Point3d(middlePoint,
				currentBox.p0.y, currentBox.p0.z), currentBox.p1);

		return (1 / this.initialBox.getSurfaceArea())
				* ((leftBox.getSurfaceArea() * (left + stradding)) + (rightBox
						.getSurfaceArea() * (right + stradding)));
	}

	public double planeCostY(final BoundingBox currentBox,
			final List<GeometricObject> objects, final double middlePoint) {
		double under = 0;
		double over = 0;
		double stradding = 0;
		for (GeometricObject o : objects) {
			BoundingBox b = o.getBoundingBox();
			if (b != null) {
				if (b.p0.y > middlePoint) {
					over++;
				} else if (b.p1.x < middlePoint) {
					under++;
				} else {
					stradding++;
				}
			} else {
				stradding++;
			}
		}

		final BoundingBox leftBox = new BoundingBox(currentBox.p0, new Point3d(
				currentBox.p1.x, middlePoint, currentBox.p1.z));
		final BoundingBox rightBox = new BoundingBox(new Point3d(
				currentBox.p0.x, middlePoint, currentBox.p0.z), currentBox.p1);

		return (1 / this.initialBox.getSurfaceArea())
				* ((leftBox.getSurfaceArea() * (under + stradding)) + (rightBox
						.getSurfaceArea() * (over + stradding)));
	}

	public double planeCostZ(final BoundingBox currentBox,
			final List<GeometricObject> objects, final double middlePoint) {
		double near = 0;
		double far = 0;
		double stradding = 0;
		for (GeometricObject o : objects) {
			BoundingBox b = o.getBoundingBox();
			if (b != null) {
				if (b.p0.z > middlePoint) {
					near++;
				} else if (b.p1.x < middlePoint) {
					far++;
				} else {
					stradding++;
				}
			} else {
				stradding++;
			}
		}

		final BoundingBox leftBox = new BoundingBox(currentBox.p0, new Point3d(
				currentBox.p1.x, currentBox.p1.y, middlePoint));
		final BoundingBox rightBox = new BoundingBox(new Point3d(
				currentBox.p0.x, currentBox.p0.y, middlePoint), currentBox.p1);

		return (1 / this.initialBox.getSurfaceArea())
				* ((leftBox.getSurfaceArea() * (near + stradding)) + (rightBox
						.getSurfaceArea() * (far + stradding)));
	}
	
	public double leafCost(final BoundingBox currentBox, final List<GeometricObject> objects) {
		return (1 / this.initialBox.getSurfaceArea()) * (currentBox.getSurfaceArea() * objects.size());
	}

	public double bestXCandidate(final Set<Double> candidates,
			final BoundingBox currentBox, final List<GeometricObject> objects) {
		double minCost = Double.POSITIVE_INFINITY;
		double bestCandidate = Double.POSITIVE_INFINITY;
		for (Double d : candidates) {
			final double cost = this.planeCostX(currentBox, objects, d);
			if (cost < minCost) {
				minCost = cost;
				bestCandidate = d;
			}
		}
		return bestCandidate;
	}

	public double bestYCandidate(final Set<Double> candidates,
			final BoundingBox currentBox, final List<GeometricObject> objects) {
		double minCost = Double.POSITIVE_INFINITY;
		double bestCandidate = Double.POSITIVE_INFINITY;
		for (Double d : candidates) {
			final double cost = this.planeCostY(currentBox, objects, d);
			if (cost < minCost) {
				minCost = cost;
				bestCandidate = d;
			}
		}
		return bestCandidate;
	}

	public double bestZCandidate(final Set<Double> candidates,
			final BoundingBox currentBox, final List<GeometricObject> objects) {
		double minCost = Double.POSITIVE_INFINITY;
		double bestCandidate = Double.POSITIVE_INFINITY;
		for (Double d : candidates) {
			final double cost = this.planeCostZ(currentBox, objects, d);
			if (cost < minCost) {
				minCost = cost;
				bestCandidate = d;
			} 
		}
		return bestCandidate;
	}
	
	public double traceShadowHit(final Ray ray, final ShadowTracer tracer) {
		// Point3 origin = new Point3(0, 0, 200);
		// Point3 hitP = new Point3(-45,-10,20);
		// Ray aRay = new Ray(origin, hitP.sub(origin));
		return p_traceObjectsForShadowHit(ray, root, tMin, tMax, tracer);
	}
	
	private double p_traceObjectsForShadowHit(final Ray ray, final Node node,
			final double min, final double max, final ShadowTracer tracer) {

		if (node.isLeaf()) {
			return tracer.traceShadowHit(ray, node.getObjects(), max);
		}
		final double t = node.distanceToSplittingPlane(ray);
		final Node near = node.nearNode(ray);
		final Node far = node.farNode(near);

		// This is madness !!
		if (t > max || t < 0) {
			// its on the near node
			return p_traceObjectsForShadowHit(ray, near, min, max, tracer);
		} else {
			if (t < min) {
				// its on the far node
				return p_traceObjectsForShadowHit(ray, far, min, max, tracer);
			} else {
				// the ray might hit in both nodes, so we split the ray
				double hit = p_traceObjectsForShadowHit(ray, near, min, t, tracer);
				if (t != Double.NEGATIVE_INFINITY) {
					return hit;
				}
				return p_traceObjectsForShadowHit(ray, far, t, max, tracer);
			}
		}
	}

	public Color traceRay(final Ray ray, final ColorTracer tracer, final ShadeRec sr) {
		return p_traceObjectsForRay(ray, root, tMin, tMax, tracer, sr);
	}

	private Color p_traceObjectsForRay(final Ray ray, final Node node,
			final double min, final double max, final ColorTracer tracer,
			final ShadeRec sr) {

		if (node == emptyLeafNode) {
			return sr.world.getBackgroundColor();
		}
		
		if (node.isLeaf()) {
			return tracer.traceRay(ray, node.getObjects(), sr, max);
		}
		final double t = node.distanceToSplittingPlane(ray);
		final Node near = node.nearNode(ray);
		final Node far = node.farNode(near);

		// This is madness !!
		if (t > max || t < 0) {
			// its on the near node
			return p_traceObjectsForRay(ray, near, min, max, tracer, sr);
		} else {
			if (t < min) {
				// its on the far node
				return p_traceObjectsForRay(ray, far, min, max, tracer, sr);
			} else {
				// the ray might hit in both nodes, so we split the ray
				Color nearColor = p_traceObjectsForRay(ray, near, min, t,
						tracer, sr);
				if (sr.hitObject) {
					return nearColor;
				}
				return p_traceObjectsForRay(ray, far, t, max, tracer, sr);
			}
		}
	}

	public double traceRayHit(final Ray ray, final HitTracer tracer, final ShadeRec sr) {
		return p_traceObjectsForRayHit(ray, root, tMin, tMax, tracer, sr);
	}

	private double p_traceObjectsForRayHit(final Ray ray, final Node node,
			final double min, final double max, final HitTracer tracer,
			final ShadeRec sr) {

		if (node == emptyLeafNode) {
			return Double.NEGATIVE_INFINITY;
		}
		
		if (node.isLeaf()) {
			return tracer.traceRayHit(ray, node.getObjects(), sr, max);
		}
		final double t = node.distanceToSplittingPlane(ray);
		final Node near = node.nearNode(ray);
		final Node far = node.farNode(near);

		// This is madness !!
		if (t > max || t < 0) {
			// its on the near node
			return p_traceObjectsForRayHit(ray, near, min, max, tracer, sr);
		} else {
			if (t < min) {
				// its on the far node
				return p_traceObjectsForRayHit(ray, far, min, max, tracer, sr);
			} else {
				// the ray might hit in both nodes, so we split the ray
				double distance = p_traceObjectsForRayHit(ray, near, min, t,
						tracer, sr);
				if (distance != Double.NEGATIVE_INFINITY) {
					return distance;
				}
				return p_traceObjectsForRayHit(ray, far, t, max, tracer, sr);
			}
		}
	}

}
