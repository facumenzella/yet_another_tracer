package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Instance;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.AABB;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.geometry.primitives.Plane;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.raytracer.tracer.ColorTracer;
import ar.edu.itba.it.cg.yart.raytracer.tracer.HitTracer;
import ar.edu.itba.it.cg.yart.raytracer.tracer.ShadowTracer;

public class YAFKDTree {

	private static double kKT = 1.5;
	private static double kKI = 1;
	private static int kMAX_DEPTH = 2;
	private double kEPSILON = 0.00001;;
	private double kTMAX = 1000;

	private KDNode root;
	private AABB rootAABB;
	private int depth;
	
	Set<Double> xCandidates;
	Set<Double> yCandidates;
	Set<Double> zCandidates;


	public YAFKDTree(final List<GeometricObject> gObjects) {
		final double size = 200;
		xCandidates = new HashSet<Double>();
		yCandidates = new HashSet<Double>();
		zCandidates = new HashSet<Double>();
		rootAABB = new AABB(new Point3d(-size, -size, -size), new Point3d(size,
				size, size));
		this.root = this.buildKDTree(gObjects, rootAABB, SplitAxis.X, 0);
	}

	private KDNode buildKDTree(final List<GeometricObject> gObjects,
			final AABB box, final SplitAxis axis, final int currentDepth) {
		PlaneCandidate bestCandidate = findPlane(gObjects, box, axis);
		boolean terminate = shouldTerminate(gObjects, bestCandidate.splitPoint.cost);
		if (currentDepth > kMAX_DEPTH || gObjects.isEmpty()
				|| terminate) {
			if (currentDepth > depth) {
				depth = currentDepth;
			}
			return new KDLeafNode(gObjects);
		}

		SplitPoint splitPoint = bestCandidate.splitPoint;
		AABB[] boxes = bestCandidate.boxes;
		List<GeometricObject> leftObjects = new ArrayList<GeometricObject>();
		List<GeometricObject> rightObjects = new ArrayList<GeometricObject>();
		divideObjects(leftObjects, rightObjects, gObjects, boxes[0], boxes[1],
				box);
		final int nextDepth = currentDepth + 1;
		SplitAxis nextAxis = SplitAxis.nextAxis(axis);
		return new KDInternalNode(splitPoint, buildKDTree(leftObjects,
				boxes[0], nextAxis, nextDepth), buildKDTree(rightObjects,
				boxes[1], nextAxis, nextDepth));
	}

	private AABB[] splitAABB(final AABB box, SplitPoint p) {
		AABB[] aabbs = new AABB[2];
		AABB leftBox = null;
		AABB rightBox = null;
		switch (p.axis) {
		case X:
			leftBox = new AABB(box.p0, new Point3d(p.point, box.p1.y, box.p1.z));
			rightBox = new AABB(new Point3d(p.point, box.p0.y, box.p0.z),
					box.p1);
			break;
		case Y:
			leftBox = new AABB(box.p0, new Point3d(box.p1.x, p.point, box.p1.z));
			rightBox = new AABB(new Point3d(box.p0.x, p.point, box.p0.z),
					box.p1);
			break;
		case Z:
			leftBox = new AABB(box.p0, new Point3d(box.p1.x, box.p1.y, p.point));
			rightBox = new AABB(new Point3d(box.p0.x, box.p0.y, p.point),
					box.p1);
			break;
		default:
			System.out.println("Holy shit the impossible happened");
			break;
		}
		aabbs[0] = leftBox;
		aabbs[1] = rightBox;
		return aabbs;
	}

	// find a 'good' plane mother fucker
	private PlaneCandidate findPlane(
			final Collection<GeometricObject> gObjects, final AABB box,
			SplitAxis axis) {
		Set<Double> candidates = null;
		PlaneCandidate best = null;
		switch (axis) {
		case X:
			candidates = planeCandidatesX(gObjects, box);
			best = this.bestCandidate(candidates, box, gObjects, axis);
			xCandidates.add(best.splitPoint.point);
			break;
		case Y:
			candidates = planeCandidatesY(gObjects, box);
			best = this.bestCandidate(candidates, box, gObjects, axis);
			yCandidates.add(best.splitPoint.point);
			break;
		case Z:
			candidates = planeCandidatesZ(gObjects, box);
			best = this.bestCandidate(candidates, box, gObjects, axis);
			zCandidates.add(best.splitPoint.point);
			break;
		default:
			System.out.println("Holy shit the impossible happened");
			break;
		}
		return best;
	}

	public PlaneCandidate bestCandidate(final Set<Double> candidates,
			final AABB box, final Collection<GeometricObject> gObjects,
			SplitAxis axis) {
		double minCost = Double.POSITIVE_INFINITY;
		double bestCandidate = Double.POSITIVE_INFINITY;
		AABB[] bestBoxes = null;
		SplitPoint splitPoint = new SplitPoint();
		splitPoint.axis = axis;
		for (Double p : candidates) {
			splitPoint.point = p;
			AABB[] boxes = splitAABB(box, splitPoint);
			int[] nlnpnr = accurateNLNR(boxes[0], boxes[1], gObjects);
			final double cost = sah(splitPoint, boxes[0], boxes[1], nlnpnr[0],
					nlnpnr[2], nlnpnr[1]);
			if (cost < minCost) {
				minCost = cost;
				bestCandidate = p;
				bestBoxes = boxes;
			}
		}
		splitPoint.cost = minCost;
		splitPoint.point = bestCandidate;
		return new PlaneCandidate(bestBoxes, splitPoint);
	}

	private Set<Double> planeCandidatesX(
			final Collection<GeometricObject> gObjects, final AABB box) {
		Set<Double> candidates = new HashSet<Double>();
		for (GeometricObject o : gObjects) {
			AABB b = o.getBoundingBox();
			if (b != null) {
				b = b.clip(box);
				if (b.p0.x >= box.p0.x && !xCandidates.contains(b.p0.x)) {
					candidates.add(b.p0.x);
				}
				if (b.p1.x <= box.p1.x && !xCandidates.contains(b.p1.x)) {
					candidates.add(b.p1.x);
				}
			} else {
				Plane plane = (Plane) ((Instance) o).object;
				if (SplitAxis.X.isParalel(plane.normal)) {
					candidates.add(plane.p.x);
				}
			}
		}
		return candidates;
	}

	private Set<Double> planeCandidatesY(
			final Collection<GeometricObject> gObjects, final AABB box) {
		Set<Double> candidates = new HashSet<Double>();
		for (GeometricObject o : gObjects) {
			AABB b = o.getBoundingBox();
			if (b != null) {
				b = b.clip(box);
				if (b.p0.y >= box.p0.y && !yCandidates.contains(b.p0.y)) {
					candidates.add(b.p0.y);
				}
				if (b.p1.y <= box.p1.y && !xCandidates.contains(b.p1.y)) {
					candidates.add(b.p1.y);
				}
			} else {
				Plane plane = (Plane) ((Instance) o).object;
				if (SplitAxis.Y.isParalel(plane.normal)) {
					candidates.add(plane.p.y);
				}
			}
		}
		return candidates;
	}

	private Set<Double> planeCandidatesZ(
			final Collection<GeometricObject> gObjects, final AABB box) {
		Set<Double> candidates = new HashSet<Double>();
		for (GeometricObject o : gObjects) {
			AABB b = o.getBoundingBox();
			if (b != null) {
				b = b.clip(box);
				if (b.p0.z >= box.p0.z && !zCandidates.contains(b.p0.z)) {
					candidates.add(b.p0.z);
				}
				if (b.p1.z <= box.p1.z && !zCandidates.contains(b.p0.z)) {
					candidates.add(b.p1.z);
				}
			} else {
				Plane plane = (Plane) ((Instance) o).object;
				if (SplitAxis.Z.isParalel(plane.normal)) {
					candidates.add(plane.p.z);
				}
			}
		}
		return candidates;
	}

	private static double costOfSubdividing(final double pL, final double pR,
			final double nL, final double nR) {
		final double lambda = (nL == 0 || nR == 0) ? .8 : 1;
		return lambda * (kKT + kKI * ((pL * nL) + (pR * nR)));
	}

	private static double costAsLeaf(final double n) {
		return kKI * n;
	}

	private static double probabilityOfHittingSubBox(final AABB subBox,
			final AABB box) {
		return subBox.surfaceArea / box.surfaceArea;
	}

	private static int[] accurateNLNR(final AABB leftBox, final AABB rightBox,
			final Collection<GeometricObject> gObjects) {
		int[] nlnpnr = new int[3];
		for (GeometricObject o : gObjects) {
			final AABB box = o.getBoundingBox();
			if (box == null) {
				nlnpnr[1] = nlnpnr[1] + 1;
			} else {
				if (leftBox.intersectsBox(box)) {
					nlnpnr[0] = nlnpnr[0] + 1;
				} else if (rightBox.intersectsBox(box)) {
					nlnpnr[2] = nlnpnr[2] + 1;
				} else {
					nlnpnr[0] = nlnpnr[0] + 1;
					nlnpnr[2] = nlnpnr[2] + 1;
				}
			}
		}
		return nlnpnr;
	}

	private double sah(final SplitPoint p, final AABB boxL, final AABB boxR,
			final double nl, final double nr, final double np) {
		final double pl = probabilityOfHittingSubBox(boxL, rootAABB);
		final double pr = probabilityOfHittingSubBox(boxR, rootAABB);

		final double cl = costOfSubdividing(pl, pr, nl + np, nr);
		final double cr = costOfSubdividing(pl, pr, nl, nr + np);

		return (cl < cr) ? cl : cr;
	}

	private static boolean shouldTerminate(
			final Collection<GeometricObject> objects, final double cost) {
		return cost > costAsLeaf(objects.size());
	}

	private void divideObjects(final Collection<GeometricObject> leftObjects,
			final Collection<GeometricObject> rightObjects,
			final Collection<GeometricObject> objects, final AABB leftBox,
			final AABB rightBox, final AABB box) {
		for (GeometricObject o : objects) {
			AABB oBox = o.getBoundingBox();
			if (oBox == null) {
				leftObjects.add(o);
				rightObjects.add(o);
			} else {
				if (oBox.intersectsBox(leftBox)) {
					leftObjects.add(o);
				} else if (oBox.intersectsBox(rightBox)) {
					rightObjects.add(o);
				} else {
					leftObjects.add(o);
					rightObjects.add(o);
				}
			}
		}
		if (leftObjects.size() + rightObjects.size() < objects.size()) {
			System.out.println("shit");
		}
	}

	public Color traceRay(final Ray ray, final ColorTracer tracer,
			final ShadeRec sr) {
		return p_traceObjectsForRay(ray, root, 0, 1000, tracer, sr);
	}

	private Color p_traceObjectsForRay(final Ray ray, final KDNode node,
			final double min, final double max, final ColorTracer tracer,
			final ShadeRec sr) {
				
		if (node.isLeaf()) {
			return tracer.traceRay(ray, node.gObjects, sr, max);
		}
		
		double dir[] = {ray.direction.x, ray.direction.y, ray.direction.z};
		double origin[] = {ray.direction.x, ray.direction.y, ray.direction.z};

		KDInternalNode internalNode = (KDInternalNode)node;
		
		KDNode near = null,far = null;
		final double splitPoint = internalNode.splitPoint.point;
		final double rayDirAxis = dir[internalNode.splitPoint.axis.value];
		final double rayOriginAxis = origin[internalNode.splitPoint.axis.value];
		
		if (rayOriginAxis < splitPoint) {
			near = internalNode.left;
			far = internalNode.right;
		} else if (rayOriginAxis > splitPoint){
			near = internalNode.right;
			far = internalNode.left;
		} else if (rayDirAxis < 0) {
			near = internalNode.left;
			far = internalNode.right;
		} else {
			near = internalNode.right;
			far = internalNode.left;
		}
		
		final double t =  (splitPoint - rayOriginAxis) / rayDirAxis;

		// This is madness !!
		if (t > max || t < kEPSILON) {
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

	public double traceShadowHit(final Ray ray, final ShadowTracer tracer) {
		return p_traceObjectsForShadowHit(ray, root, kEPSILON, kTMAX, tracer);
	}
	
	private double p_traceObjectsForShadowHit(final Ray ray, final KDNode node,
			final double min, final double max, final ShadowTracer tracer) {

		if (node.isLeaf()) {
			return tracer.traceShadowHit(ray, node.gObjects, max);
		}
		
		double dir[] = {ray.direction.x, ray.direction.y, ray.direction.z};
		double origin[] = {ray.direction.x, ray.direction.y, ray.direction.z};

		KDInternalNode internalNode = (KDInternalNode)node;
		
		KDNode near = null,far = null;
		final double splitPoint = internalNode.splitPoint.point;
		final double rayDirAxis = dir[internalNode.splitPoint.axis.value];
		final double rayOriginAxis = origin[internalNode.splitPoint.axis.value];
		
		if (rayOriginAxis < splitPoint) {
			near = internalNode.left;
			far = internalNode.right;
		} else if (rayOriginAxis > splitPoint){
			near = internalNode.right;
			far = internalNode.left;
		} else if (rayDirAxis < 0) {
			near = internalNode.left;
			far = internalNode.right;
		} else {
			near = internalNode.right;
			far = internalNode.left;
		}
		
		final double t =  (splitPoint - rayOriginAxis) / rayDirAxis;

		// This is madness !!
		if (t > max || t < kEPSILON) {
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

	public double traceRayHit(final Ray ray, final HitTracer tracer, final ShadeRec sr) {
		return p_traceObjectsForRayHit(ray, root, kEPSILON, kTMAX, tracer, sr);
	}

	private double p_traceObjectsForRayHit(final Ray ray, final KDNode node,
			final double min, final double max, final HitTracer tracer,
			final ShadeRec sr) {

		if (node.isLeaf()) {
			return tracer.traceRayHit(ray, node.gObjects, sr, max);
		}
		
		double dir[] = {ray.direction.x, ray.direction.y, ray.direction.z};
		double origin[] = {ray.direction.x, ray.direction.y, ray.direction.z};

		KDInternalNode internalNode = (KDInternalNode)node;
		
		KDNode near = null,far = null;
		final double splitPoint = internalNode.splitPoint.point;
		final double rayDirAxis = dir[internalNode.splitPoint.axis.value];
		final double rayOriginAxis = origin[internalNode.splitPoint.axis.value];
		
		if (rayOriginAxis < splitPoint) {
			near = internalNode.left;
			far = internalNode.right;
		} else if (rayOriginAxis > splitPoint){
			near = internalNode.right;
			far = internalNode.left;
		} else if (rayDirAxis < 0) {
			near = internalNode.left;
			far = internalNode.right;
		} else {
			near = internalNode.right;
			far = internalNode.left;
		}
		
		final double t =  (splitPoint - rayOriginAxis) / rayDirAxis;

		// This is madness !!
		if (t > max || t < kEPSILON) {
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
