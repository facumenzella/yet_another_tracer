package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.nlog2n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.KDInternalNode;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.KDLeafNode;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.KDNode;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.PlaneCandidate;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.SplitAxis;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.SplitPoint;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.AABB;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.raytracer.tracer.AbstractTracer;

// This has O(N log N) or at least we hope it does

public class YAFKDTree2 {

	private static double kKT = 1.5;
	private static double kKI = 1;
	private static int kMAX_DEPTH = 3;

	private static int kMIN_DEPTH = 10;
	private static double kEPSILON = 0.00001;
	private double kTMAX = 1000;
	private static double kLAMBDA = .8;

	private static int leafs;

	private KDNode root;
	private AABB rootAABB;

	public static KDLeafNode emptyLeaf = new KDLeafNode(null);

	private static AABB buildRootAABB(final List<GeometricObject> objects) {
		double minX = -Double.MAX_VALUE;
		double minY = minX, minZ = minX;
		double maxX = Double.MAX_VALUE;
		double maxY = maxX, maxZ = maxX;
		
		for (GeometricObject g : objects) {
			AABB b = g.getBoundingBox();
			if (b != null) {
				minX = Math.min(minX, b.p0.x);
				minZ = Math.min(minZ, b.p0.z);
				minY = Math.min(minY, b.p1.y);
				maxX = Math.max(maxX, b.p1.x);
				maxY = Math.max(maxY, b.p0.y);
				maxZ = Math.max(maxZ, b.p1.z);
			}	
		}
		return new AABB(new Point3d(minX, maxY, minZ), new Point3d(maxX, minY, maxZ));
	}
	
	public static YAFKDTree2 build(final List<GeometricObject> gObjects) {
		final AABB aabb = YAFKDTree2.buildRootAABB(gObjects);
		return YAFKDTree2.build(gObjects, aabb);
	}
	
	public static YAFKDTree2 build(final List<GeometricObject> gObjects,
			final AABB aabb) {
		final long start = System.currentTimeMillis();
		YAFKDTree2 tree = new YAFKDTree2();

		final List<Event> eventList = new ArrayList<>();
		for (final GeometricObject obj : gObjects) {
			eventList.addAll(generateEvents(obj, aabb));
		}
		tree.rootAABB = aabb;
		Event[] events = eventList.toArray(new Event[eventList.size()]);
		Arrays.sort(events);
		
		tree.root = buildTree(tree.rootAABB, gObjects, events);
		
		System.out.println("Tree built in "
				+ (System.currentTimeMillis() - start)
				+ " milliseconds. \n initials" + gObjects.size() + "leafs: "
				+ leafs);
		return tree;
	}

	private static KDNode buildTree(final AABB rootBox,
			final List<GeometricObject> gObjects, final Event[] events) {
		return buildKDNode(gObjects, rootBox, SplitAxis.X, 0, events,
				new HashSet<PlaneCandidate>(), rootBox);
	}

	private static KDNode buildKDNode(final List<GeometricObject> gObjects,
			final AABB box, final SplitAxis axis, final int currentDepth,
			Event[] events, Set<PlaneCandidate> prevs, final AABB rootAABB) {
		final int size = gObjects.size();
		if (size == 0) {
			return emptyLeaf;
		}
		PlaneCandidate bestCandidate = findPlane(size, box, events, rootAABB);
		boolean terminate = bestCandidate.cost > (kKI * size);

		if (currentDepth >= kMAX_DEPTH || (terminate && currentDepth > kMIN_DEPTH)
				|| prevs.contains(bestCandidate)) {
			leafs += gObjects.size();
			return new KDLeafNode(gObjects);
		}

		// We divide the objects
		SplitPoint splitPoint = bestCandidate.splitPoint;
		AABB[] boxes = bestCandidate.boxes;

		// We classify the objects with the events (left, right, both)
		ClassifiedObjects classifiedObjects = ClassifiedObjects.classify(
				gObjects, events, bestCandidate);
		// We classify the events in left and right
		final ClassifiedEvents classifiedEvents = ClassifiedEvents.splice(
				events, classifiedObjects, boxes[0], boxes[1]);

		final Event[] ebl = classifiedEvents.ebl.toArray(new Event[0]);
		final Event[] ebr = classifiedEvents.ebr.toArray(new Event[0]);

		Arrays.sort(ebl);
		Arrays.sort(ebr);

		final List<Event> el = Event.mergeEvents(Arrays.asList(ebl),
				classifiedEvents.elo);
		final List<Event> er = Event.mergeEvents(Arrays.asList(ebr),
				classifiedEvents.ero);

		final int nextDepth = currentDepth + 1;
		SplitAxis nextAxis = SplitAxis.nextAxis(axis);

		final Set<PlaneCandidate> newSplits = new HashSet<>(prevs);
		newSplits.add(bestCandidate);

		return new KDInternalNode(splitPoint, buildKDNode(classifiedObjects.tl,
				boxes[0], nextAxis, nextDepth, el.toArray(new Event[] {}),
				newSplits, rootAABB), buildKDNode(classifiedObjects.tr, boxes[1],
				nextAxis, nextDepth, er.toArray(new Event[] {}), newSplits, rootAABB));
	}

	private static AABB[] splitAABB(final AABB box, SplitPoint p) {
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
			leftBox = new AABB(new Point3d(box.p0.x, p.point, box.p0.z), box.p1);
			rightBox = new AABB(box.p0,
					new Point3d(box.p1.x, p.point, box.p1.z));
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

	// find a 'good' plane mother fucker!!
	private static PlaneCandidate findPlane(final int objectsSize,
			final AABB box, final Event[] events, final AABB rootAABB) {

		final int size = objectsSize;
		final int eventsQty = events.length;
		double minCost = Double.MAX_VALUE;
		PlaneCandidate bestCandidate = new PlaneCandidate();

		int nLX = 0, nPX = 0, nRX = size;
		int nLY = 0, nPY = 0, nRY = size;
		int nLZ = 0, nPZ = 0, nRZ = size;

		for (int i = 0; i < eventsQty; i++) {

			int pSTARTX = 0, pENDX = 0, pPLANARX = 0;
			int pSTARTY = 0, pENDY = 0, pPLANARY = 0;
			int pSTARTZ = 0, pENDZ = 0, pPLANARZ = 0;

			Event e = events[i];
			Event ei;

			while (i < eventsQty && (ei = events[i]).axis.value == e.axis.value
					&& Math.abs(ei.point - e.point) < kEPSILON && ei.type == EventType.END) {
				i++;
				switch (e.splitPoint.axis) {
				case X:
					pENDX++;
					break;
				case Y:
					pENDY++;
					break;
				case Z:
					pENDZ++;
					break;
				default:
					System.out.println("Holy shit the impossible happened");
				}
			}
			while (i < eventsQty && (ei = events[i]).axis.value == e.axis.value
					&& Math.abs(ei.point - e.point) < kEPSILON && ei.type == EventType.START) {
				i++;
				switch (e.splitPoint.axis) {
				case X:
					pSTARTX++;
					break;
				case Y:
					pSTARTY++;
					break;
				case Z:
					pSTARTZ++;
					break;
				default:
					System.out.println("Holy shit the impossible happened");
				}
			}
			while (i < eventsQty && (ei = events[i]).axis.value == e.axis.value
					&& Math.abs(ei.point - e.point) < kEPSILON && ei.type == EventType.PLANAR) {
				i++;
				switch (e.splitPoint.axis) {
				case X:
					pPLANARX++;
					break;
				case Y:
					pPLANARY++;
					break;
				case Z:
					pPLANARZ++;
					break;
				default:
					System.out.println("Holy shit the impossible happened");
				}
			}

			PlaneCandidate candidate = null;
			switch (e.splitPoint.axis) {
			case X:
				nPX = pPLANARX;
				nRX -= pPLANARX;
				nRX -= pENDX;
				candidate = sah(e.splitPoint, box, nLX, nRX, nPX, rootAABB);
				nPX = 0;
				nLX += pPLANARX;
				nLX += pSTARTX;
				break;
			case Y:
				nPY = pPLANARY;
				nRY -= pPLANARY;
				nRY -= pENDY;
				candidate = sah(e.splitPoint, box, nLY, nRY, nPY, rootAABB);
				nPY = 0;
				nLY += pPLANARY;
				nLY += pSTARTY;
				break;
			case Z:
				nPZ = pPLANARZ;
				nRZ -= pPLANARZ;
				nRZ -= pENDZ;
				candidate = sah(e.splitPoint, box, nLZ, nRZ, nPZ, rootAABB);
				nPZ = 0;
				nLZ += pPLANARZ;
				nLZ += pSTARTZ;
				break;
			default:
				System.out.println("Holy shit the impossible happened");
			}

			if ( Double.isNaN(candidate.cost) || minCost > candidate.cost ) {
				bestCandidate.cost = candidate.cost;
				bestCandidate.splitPoint = e.splitPoint;
				bestCandidate.boxes = candidate.boxes;
			}
		}
		return bestCandidate;
	}

	public static PerfectSplits perfectSplits(final GeometricObject object,
			final AABB box) {
		// we first find the perfect xs
		double[] xs = new double[2];
		double[] ys = new double[2];
		double[] zs = new double[2];
		AABB b = object.getBoundingBox();
		if (b != null) {
			b = b.clip(box);
			xs[0] = b.p0.x;
			xs[1] = b.p1.x;
		}
		// then the ys
		if (b != null) {
			b = b.clip(box);
			ys[0] = b.p0.y;
			ys[1] = b.p1.y;
		}
		// finally the zs
		if (b != null) {
			b = b.clip(box);
			zs[0] = b.p0.z;
			zs[0] = b.p1.z;
		} 

		return new PerfectSplits(xs, ys, zs);
	}

	private static PlaneCandidate sah(final SplitPoint p, final AABB box,
			final double nl, final double nr, final double np, final AABB rootAABB) {
		AABB boxes[] = splitAABB(box, p);
		final AABB boxL = boxes[0];
		final AABB boxR = boxes[1];

		double area = (Double.isInfinite(rootAABB.surfaceArea)) ? 10000 : rootAABB.surfaceArea;
		
		final double pl = boxL.surfaceArea / area;
		final double pr = boxR.surfaceArea / area;

		final double cl = kLAMBDA * (kKT + kKI * ((pl * nl + np) + (pr * nr)));
		final double cr = kLAMBDA * (kKT + kKI * ((pl * nl) + (pr * nr + np)));

		double cost = (cl < cr) ? cl : cr;
		return new PlaneCandidate(boxes, p, cost, (cl < cr) ? true : false);
	}

	// Here comes the shit
	public static List<Event> generateEvents(final GeometricObject object,
			final AABB box) {
		final List<Event> eventList = new ArrayList<Event>();
		PerfectSplits perfectSplits = perfectSplits(object, box);

		// 3 because we have 3 dimensions
		// first x
		SplitAxis axis = SplitAxis.X;
		double[] perfects = null;
		perfects = perfectSplits.perfectXs;
		double min = perfects[0];
		double max = perfects[1];

		// if they are the same, they are planar
		if (max - min < kEPSILON) {
			SplitPoint splitPoint = new SplitPoint();
			splitPoint.axis = axis;
			splitPoint.point = min;
			eventList.add(new Event(EventType.PLANAR, object, splitPoint));
		} else {
			SplitPoint splitPointStart = new SplitPoint();
			splitPointStart.axis = axis;
			splitPointStart.point = min;
			SplitPoint splitPointEnd = new SplitPoint();
			splitPointEnd.axis = axis;
			splitPointEnd.point = max;

			eventList.add(new Event(EventType.START, object, splitPointEnd));
			eventList.add(new Event(EventType.END, object, splitPointStart));
		}

		// then y
		perfects = null;
		axis = SplitAxis.Y;
		perfects = perfectSplits.perfectYs;

		min = perfects[0];
		max = perfects[1];

		// if they are the same, they are planar
		if (max - min < kEPSILON) {
			SplitPoint splitPoint = new SplitPoint();
			splitPoint.axis = axis;
			splitPoint.point = min;
			eventList.add(new Event(EventType.PLANAR, object, splitPoint));
		} else {
			SplitPoint splitPointStart = new SplitPoint();
			splitPointStart.axis = axis;
			splitPointStart.point = min;
			SplitPoint splitPointEnd = new SplitPoint();
			splitPointEnd.axis = axis;
			splitPointEnd.point = max;

			eventList.add(new Event(EventType.START, object, splitPointEnd));
			eventList.add(new Event(EventType.END, object, splitPointStart));
		}

		// finally z
		perfects = null;
		axis = SplitAxis.Z;
		perfects = perfectSplits.perfectZs;

		min = perfects[0];
		max = perfects[1];

		// if they are the same, they are planar
		if (max - min < kEPSILON) {
			SplitPoint splitPoint = new SplitPoint();
			splitPoint.axis = axis;
			splitPoint.point = min;
			eventList.add(new Event(EventType.PLANAR, object, splitPoint));
		} else {
			SplitPoint splitPointStart = new SplitPoint();
			splitPointStart.axis = axis;
			splitPointStart.point = min;
			SplitPoint splitPointEnd = new SplitPoint();
			splitPointEnd.axis = axis;
			splitPointEnd.point = max;

			eventList.add(new Event(EventType.START, object, splitPointEnd));
			eventList.add(new Event(EventType.END, object, splitPointStart));
		}

		return eventList;

	}

	// Here we trace rays. Work for kids
	public Color traceRay(final Ray ray, final ShadeRec sr) {
		return p_traceObjectsForRay(ray, root, 0, 1000, sr);
	}

	private Color p_traceObjectsForRay(final Ray ray, final KDNode node,
			final double min, final double max, final ShadeRec sr) {

		if (node.isLeaf()) {
			KDLeafNode leaf = (KDLeafNode)node;
			List<GeometricObject> objects = leaf.gObjects;
			if (ray.depth >= AbstractTracer.MAX_DEPTH || objects == null || objects.size() == 0) {
				return Color.blackColor();
			}
			Vector3d normal = null;
			Point3d localHitPoint = null;
			double tMin = max;
			for (int i = 0; i < objects.size(); i++) {
				GeometricObject object = objects.get(i);
				double t = object.hit(ray, sr);
				if (t != Double.NEGATIVE_INFINITY && t < tMin) {
					sr.hitObject = true;
					sr.material = object.getMaterial();
					sr.hitPoint = sr.localHitPoint.transformByMatrix(object.matrix);
					normal = sr.normal;
					localHitPoint = sr.localHitPoint;
					tMin = t;
				}
			}

			if (sr.hitObject) {
				sr.depth = ray.depth;
				sr.t = tMin;
				sr.normal = normal;
				sr.localHitPoint = localHitPoint;
				sr.ray = ray;
				return sr.material.shade(sr);
			}
			
			return sr.world.getBackgroundColor();
		}

		double dir[] = { ray.direction.x, ray.direction.y, ray.direction.z };
		double origin[] = { ray.origin.x, ray.origin.y, ray.origin.z };

		KDInternalNode internalNode = (KDInternalNode) node;

		KDNode near = null, far = null;
		final double splitPoint = internalNode.splitPoint.point;
		final double rayDirAxis = dir[internalNode.splitPoint.axis.value];
		final double rayOriginAxis = origin[internalNode.splitPoint.axis.value];

		final double diff = splitPoint - rayOriginAxis;

		if (diff > 0) {
			near = internalNode.left;
			far = internalNode.right;
		} else {
			near = internalNode.right;
			far = internalNode.left;
		}

		final double t = diff / rayDirAxis;

		// This is madness !!
		if (t > max || t < kEPSILON) {
			// its on the near node
			return p_traceObjectsForRay(ray, near, min, max, sr);
		} else {
			if (t < min) {
				// its on the far node
				return p_traceObjectsForRay(ray, far, min, max, sr);
			} else {
				// the ray might hit in both nodes, so we split the ray
				Color nearColor = p_traceObjectsForRay(ray, near, min, t, sr);
				if (sr.hitObject) {
					return nearColor;
				}
				return p_traceObjectsForRay(ray, far, t, max, sr);
			}
		}
	}

	public double traceShadowHit(final Ray ray) {
		return p_traceObjectsForShadowHit(ray, root, kEPSILON, kTMAX);
	}

	private double p_traceObjectsForShadowHit(final Ray ray, final KDNode node,
			final double min, final double max) {

		if (node.isLeaf()) {
			KDLeafNode leaf = (KDLeafNode)node;
			List<GeometricObject> objects = leaf.gObjects;
			if (ray.depth >= AbstractTracer.MAX_DEPTH || objects == null || objects.size() == 0) {
				return Double.NEGATIVE_INFINITY;
			}
			double tMin = max;
			boolean hit = false;
			for (int i = 0; i < objects.size(); i++) {
				GeometricObject object = objects.get(i);
				double t = object.shadowHit(ray);
				if (t != Double.NEGATIVE_INFINITY && t < tMin) {
					tMin = t;
					hit = true;
				}
			}
			if (hit) {
				return tMin;
			}
			return Double.NEGATIVE_INFINITY;
		}

		double dir[] = { ray.direction.x, ray.direction.y, ray.direction.z };
		double origin[] = { ray.origin.x, ray.origin.y, ray.origin.z };

		KDInternalNode internalNode = (KDInternalNode) node;

		KDNode near = null, far = null;
		final double splitPoint = internalNode.splitPoint.point;
		final double rayDirAxis = dir[internalNode.splitPoint.axis.value];
		final double rayOriginAxis = origin[internalNode.splitPoint.axis.value];

		final double diff = splitPoint - rayOriginAxis;

		if (diff > 0) {
			near = internalNode.left;
			far = internalNode.right;
		} else {
			near = internalNode.right;
			far = internalNode.left;
		}

		final double t = diff / rayDirAxis;

		// This is madness !!
		if (t > max || t < kEPSILON) {
			// its on the near node
			return p_traceObjectsForShadowHit(ray, near, min, max);
		} else {
			if (t < min) {
				// its on the far node
				return p_traceObjectsForShadowHit(ray, far, min, max);
			} else {
				// the ray might hit in both nodes, so we split the ray
				double hit = p_traceObjectsForShadowHit(ray, near, min, t);
				if (t != Double.NEGATIVE_INFINITY) {
					return hit;
				}
				return p_traceObjectsForShadowHit(ray, far, t, max);
			}
		}
	}

	public double traceRayHit(final Ray ray, final ShadeRec sr) {
		return p_traceObjectsForRayHit(ray, root, kEPSILON, kTMAX, sr);
	}

	private double p_traceObjectsForRayHit(final Ray ray, final KDNode node,
			final double min, final double max, final ShadeRec sr) {

		if (node.isLeaf()) {
			KDLeafNode leaf = (KDLeafNode)node;
			List<GeometricObject> objects = leaf.gObjects;
			if (objects == null || objects.size() == 0) {
				return Double.NEGATIVE_INFINITY;
			}
			
			double tMin = max;
			boolean hit = false;
			Vector3d normal = null;
			Point3d localHitPoint = null;
			GeometricObject object = null;
			for (int i = 0; i < objects.size(); i++) {
				object = objects.get(i);
				double t = object.hit(ray, sr);
				if (t != Double.NEGATIVE_INFINITY && t < tMin) {
					tMin = t;
					normal = sr.normal;
					localHitPoint = sr.localHitPoint;
					hit = true;
				}
			}
			if (hit) {
				sr.normal = normal;
				sr.localHitPoint = localHitPoint;
				return tMin;
			}
			return Double.NEGATIVE_INFINITY;
		}

		double dir[] = { ray.direction.x, ray.direction.y, ray.direction.z };
		double origin[] = { ray.origin.x, ray.origin.y, ray.origin.z };

		KDInternalNode internalNode = (KDInternalNode) node;

		KDNode near = null, far = null;
		final double splitPoint = internalNode.splitPoint.point;
		final double rayDirAxis = dir[internalNode.splitPoint.axis.value];
		final double rayOriginAxis = origin[internalNode.splitPoint.axis.value];

		final double diff = splitPoint - rayOriginAxis;

		if (diff > 0) {
			near = internalNode.left;
			far = internalNode.right;
		} else {
			near = internalNode.right;
			far = internalNode.left;
		}

		final double t = diff / rayDirAxis;

		// This is madness !!
		if (t > max || t < kEPSILON) {
			// its on the near node
			return p_traceObjectsForRayHit(ray, near, min, max, sr);
		} else {
			if (t < min) {
				// its on the far node
				return p_traceObjectsForRayHit(ray, far, min, max, sr);
			} else {
				// the ray might hit in both nodes, so we split the ray
				double distance = p_traceObjectsForRayHit(ray, near, min, t, sr);
				if (distance != Double.NEGATIVE_INFINITY) {
					return distance;
				}
				return p_traceObjectsForRayHit(ray, far, t, max, sr);
			}
		}
	}

}
