package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.nlog2n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.edu.itba.it.cg.yart.YartConstants;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.KDInternalNode;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.KDLeafNode;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.KDNode;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.PlaneCandidate;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.SplitAxis;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.SplitPoint;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack.StackElement;
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
	
	private static final Logger LOGGER = LoggerFactory.getLogger(YartConstants.LOG_FILE);

	private static double kKT = 1;
	private static double kKI = 1.5;
	private static int kMAX_DEPTH = 60;

	public static double kEPSILON = 0.00001;
	private double kTMAX = 1000;
	private static double kLAMBDA = .8;

	private static int leafs;

	private KDNode root;
	private AABB rootAABB;

	public static KDLeafNode emptyLeaf = new KDLeafNode(null);

	private static AABB buildRootAABB(final List<GeometricObject> objects) {
		double minX = Double.MAX_VALUE;
		double minY = minX, minZ = minX;
		double maxX = -Double.MAX_VALUE;
		double maxY = maxX, maxZ = maxX;

		for (GeometricObject g : objects) {
			AABB b = g.getBoundingBox();
			if (b != null && g.isFinite()) {
				minX = Math.min(minX, b.p0.x - kEPSILON);
				minZ = Math.min(minZ, b.p0.z - kEPSILON);
				minY = Math.min(minY, b.p1.y - kEPSILON);
				maxX = Math.max(maxX, b.p1.x + kEPSILON);
				maxY = Math.max(maxY, b.p0.y + kEPSILON);
				maxZ = Math.max(maxZ, b.p1.z + kEPSILON);
			}
		}
		return new AABB(new Point3d(minX, maxY, minZ), new Point3d(maxX, minY,
				maxZ));
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
		
		LOGGER.info("Tree built in {}ms. Initials: {}. Leaves: {}. ", (System.currentTimeMillis() - start), gObjects.size(), leafs);
		return tree;
	}

	private static KDNode buildTree(final AABB rootBox,
			final List<GeometricObject> gObjects, final Event[] events) {
		return buildKDNode(gObjects, rootBox, 0, events, rootBox,
				new HashSet<PlaneCandidate>());
	}

	private static KDNode buildKDNode(final List<GeometricObject> gObjects,
			final AABB box, final int currentDepth, Event[] events,
			final AABB rootAABB, final Set<PlaneCandidate> prevs) {
		final int size = gObjects.size();

		PlaneCandidate bestCandidate = findPlane(size, box, events, rootAABB,
				prevs);
		boolean terminate = bestCandidate.cost > (kKI * size);

		if (size == 0 || currentDepth >= kMAX_DEPTH || bestCandidate == null
				|| terminate || prevs.contains(bestCandidate)) {
			leafs += gObjects.size();
			return new KDLeafNode(gObjects);
		}

		// We divide the objects
		SplitPoint splitPoint = bestCandidate.splitPoint;
		AABB[] boxes = bestCandidate.boxes;

		// We classify the objects with the events (left, right, both)
		ClassifiedObjects classifiedObjects = classify(gObjects, events,
				bestCandidate);
		// We classify the events in left and right
		final ClassifiedEvents classifiedEvents = splice(events,
				classifiedObjects, boxes[0], boxes[1]);

		final Event[] ebl = classifiedEvents.ebl.toArray(new Event[0]);
		final Event[] ebr = classifiedEvents.ebr.toArray(new Event[0]);

		Arrays.sort(ebl);
		Arrays.sort(ebr);

		final List<Event> el = mergeEvents(Arrays.asList(ebl),
				classifiedEvents.elo);
		final List<Event> er = mergeEvents(Arrays.asList(ebr),
				classifiedEvents.ero);

		final Set<PlaneCandidate> prevsPlus = new HashSet<PlaneCandidate>();
		prevsPlus.add(bestCandidate);

		final int nextDepth = currentDepth + 1;

		return new KDInternalNode(splitPoint, buildKDNode(classifiedObjects.tl,
				boxes[0], nextDepth, el.toArray(new Event[] {}), rootAABB,
				prevsPlus), buildKDNode(classifiedObjects.tr, boxes[1],
				nextDepth, er.toArray(new Event[] {}), rootAABB, prevsPlus));
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
			final AABB box, final Event[] events, final AABB rootAABB,
			Set<PlaneCandidate> prevs) {

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

			while (i < eventsQty && events[i].axis.value == e.axis.value
					&& events[i].point == e.point
					&& events[i].type == EventType.END) {
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

			while (i < eventsQty && events[i].axis.value == e.axis.value
					&& events[i].point == e.point
					&& events[i].type == EventType.START) {
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
			while (i < eventsQty && events[i].axis.value == e.axis.value
					&& events[i].point == e.point
					&& events[i].type == EventType.PLANAR) {
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

			if (minCost > candidate.cost) {
				minCost = candidate.cost;
				bestCandidate.splitPoint = candidate.splitPoint;
				bestCandidate.boxes = candidate.boxes;
			}
		}
		bestCandidate.cost = minCost;
		return bestCandidate;
	}

	public static PerfectSplits perfectSplits(final GeometricObject object,
			final AABB box) {
		double[] xs = new double[2];
		double[] ys = new double[2];
		double[] zs = new double[2];
		AABB b = object.getBoundingBox();
		if (b != null) {
			b = b.clip(box);
			// we first find the perfect xs
			xs[0] = b.p0.x;
			xs[1] = b.p1.x;
			// then the ys
			ys[0] = b.p1.y;
			ys[1] = b.p0.y;
			// finally the zs
			zs[0] = b.p0.z;
			zs[1] = b.p1.z;
		}
		return new PerfectSplits(xs, ys, zs);
	}

	private static PlaneCandidate sah(final SplitPoint p, final AABB box,
			final double nl, final double nr, final double np,
			final AABB rootAABB) {
		AABB boxes[] = splitAABB(box, p);
		final AABB boxL = boxes[0];
		final AABB boxR = boxes[1];

		double area = rootAABB.surfaceArea;

		final double pl = boxL.surfaceArea / area;
		final double pr = boxR.surfaceArea / area;

		final double cl = ((nr == 0 || nl == 0) ? kLAMBDA : 1)
				* (kKT + kKI * ((pl * nl + np) + (pr * nr)));
		final double cr = ((nr == 0 || nl == 0) ? kLAMBDA : 1)
				* (kKT + kKI * ((pl * nl) + (pr * nr + np)));

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
		} else if (object.isFinite()) {
			SplitPoint splitPointStart = new SplitPoint();
			splitPointStart.axis = axis;
			splitPointStart.point = min;
			SplitPoint splitPointEnd = new SplitPoint();
			splitPointEnd.axis = axis;
			splitPointEnd.point = max;

			eventList.add(new Event(EventType.START, object, splitPointStart));
			eventList.add(new Event(EventType.END, object, splitPointEnd));
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
		} else if (object.isFinite()) {
			SplitPoint splitPointStart = new SplitPoint();
			splitPointStart.axis = axis;
			splitPointStart.point = min;
			SplitPoint splitPointEnd = new SplitPoint();
			splitPointEnd.axis = axis;
			splitPointEnd.point = max;

			eventList.add(new Event(EventType.START, object, splitPointStart));
			eventList.add(new Event(EventType.END, object, splitPointEnd));
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
		} else if (object.isFinite()) {
			SplitPoint splitPointStart = new SplitPoint();
			splitPointStart.axis = axis;
			splitPointStart.point = min;
			SplitPoint splitPointEnd = new SplitPoint();
			splitPointEnd.axis = axis;
			splitPointEnd.point = max;

			eventList.add(new Event(EventType.START, object, splitPointStart));
			eventList.add(new Event(EventType.END, object, splitPointEnd));
		}

		return eventList;

	}

	// Here we trace rays. Work for kids
	public Color traceRay(final Ray ray, final ShadeRec sr, final Stack stack) {
		double tNear = 0;
		double tFar = kTMAX;
		KDNode node = null;

		int top = stack.index;
		stack.push(root, tNear, tFar);

		while (true) {
			StackElement e = stack.pop();
			node = e.node;
			tNear = e.min;
			tFar = e.max;
			while (!node.isLeaf()) {
				double dir[] = { ray.direction.x, ray.direction.y,
						ray.direction.z };
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
				if (t > tFar || t < kEPSILON) {
					// its on the near node
					node = near;
				} else {
					if (t < tNear) {
						// its on the far node
						node = far;
					} else {
						// the ray might hit in both nodes, so we split the ray
						stack.push(far, t, tFar);
						node = near;
						tFar = t;
					}
				}
			}
			KDLeafNode leaf = (KDLeafNode) node;
			List<GeometricObject> objects = leaf.gObjects;
			if (ray.depth < AbstractTracer.MAX_DEPTH && objects != null) {
				Vector3d normal = null;
				Point3d localHitPoint = null;
				double tMin = tFar;
				double u = 0, v = 0;
				for (int i = 0; i < objects.size(); i++) {
					GeometricObject object = objects.get(i);
					double t = object.hit(ray, sr, stack);
					if (t != Double.NEGATIVE_INFINITY && t < tMin) {
						sr.hitObject = true;
						sr.material = object.getMaterial();
						sr.hitPoint = sr.localHitPoint
								.transformByMatrix(object.matrix);
						normal = sr.normal;
						u = sr.u;
						v = sr.v;
						localHitPoint = sr.localHitPoint;
						tMin = t;
					}
				}
				Color color = null;
				if (sr.hitObject) {
					sr.depth = ray.depth;
					sr.t = tMin;
					sr.normal = normal;
					sr.localHitPoint = localHitPoint;
					sr.ray = ray;
					sr.u = u;
					sr.v = v;
					color = sr.material.shade(sr, stack);
					stack.index = top;
					return color;
				}
			}

			// If stack is empty
			if (stack.peek() == top) {
				return sr.world.getBackgroundColor();
			}
		}
	}

	public double traceShadowHit(final Ray ray, final Stack stack) {
		double tNear = 0;
		double tFar = kTMAX;
		KDNode node = null;

		int top = stack.index;
		stack.push(root, tNear, tFar);

		while (true) {
			StackElement e = stack.pop();
			node = e.node;
			tNear = e.min;
			tFar = e.max;
			while (!node.isLeaf()) {
				double dir[] = { ray.direction.x, ray.direction.y,
						ray.direction.z };
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
				if (t > tFar || t < kEPSILON) {
					// its on the near node
					node = near;
				} else {
					if (t < tNear) {
						// its on the far node
						node = far;
					} else {
						// the ray might hit in both nodes, so we split the ray
						stack.push(far, t, tFar);
						node = near;
						tFar = t;
					}
				}
			}
			KDLeafNode leaf = (KDLeafNode) node;
			List<GeometricObject> objects = leaf.gObjects;
			if (ray.depth < AbstractTracer.MAX_DEPTH && objects != null) {
				double tMin = tFar;
				boolean hit = false;
				for (int i = 0; i < objects.size(); i++) {
					GeometricObject object = objects.get(i);
					double t = object.shadowHit(ray, stack);
					if (t != Double.NEGATIVE_INFINITY && t < tMin) {
						tMin = t;
						hit = true;
					}
				}
				if (hit) {
					stack.index = top;
					return tMin;
				}
			}
			// If stack is empty
			if (stack.peek() == top) {
				return Double.NEGATIVE_INFINITY;
			}
		}
	}

	public double traceRayHit(final Ray ray, final ShadeRec sr,
			final Stack stack) {
		double tNear = 0;
		double tFar = kTMAX;
		KDNode node = null;

		int top = stack.index;
		stack.push(root, tNear, tFar);

		while (true) {
			StackElement e = stack.pop();
			node = e.node;
			tNear = e.min;
			tFar = e.max;
			while (!node.isLeaf()) {
				double dir[] = { ray.direction.x, ray.direction.y,
						ray.direction.z };
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
				if (t > tFar || t < kEPSILON) {
					// its on the near node
					node = near;
				} else {
					if (t < tNear) {
						// its on the far node
						node = far;
					} else {
						// the ray might hit in both nodes, so we split the ray
						stack.push(far, t, tFar);
						node = near;
						tFar = t;
					}
				}
			}
			KDLeafNode leaf = (KDLeafNode) node;
			List<GeometricObject> objects = leaf.gObjects;
			if (ray.depth < AbstractTracer.MAX_DEPTH && objects != null) {
				double tMin = tFar;
				boolean hit = false;
				Vector3d normal = null;
				Point3d localHitPoint = null;
				GeometricObject object = null;
				double u = 0, v = 0;
				for (int i = 0; i < objects.size(); i++) {
					object = objects.get(i);
					double t = object.hit(ray, sr, stack);
					if (t != Double.NEGATIVE_INFINITY && t < tMin) {
						tMin = t;
						normal = sr.normal;
						localHitPoint = sr.localHitPoint;
						u = sr.u;
						v = sr.v;
						hit = true;
					}
				}
				if (hit) {
					sr.normal = normal;
					sr.localHitPoint = localHitPoint;
					stack.index = top;
					sr.u = u;
					sr.v = v;
					return tMin;
				}
			}
			// If stack is empty
			if (stack.peek() == top) {
				return Double.NEGATIVE_INFINITY;
			}
		}
	}

	public static List<Event> mergeEvents(final Collection<Event> e1,
			final Collection<Event> elo) {
		final Iterator<Event> it1 = e1.iterator();
		final Iterator<Event> it2 = elo.iterator();
		final List<Event> merged = new ArrayList<>();

		if (!it1.hasNext()) {
			merged.addAll(elo);
			return merged;
		}

		if (!it2.hasNext()) {
			merged.addAll(e1);
			return merged;
		}

		Event ev1 = it1.next();
		Event ev2 = it2.next();

		do {
			if (ev1.compareTo(ev2) < 0) {
				merged.add(ev1);
				if (it1.hasNext()) {
					ev1 = it1.next();
				} else {
					merged.add(ev2);
					break;
				}
			} else {
				merged.add(ev2);
				if (it2.hasNext()) {
					ev2 = it2.next();
				} else {
					merged.add(ev1);
					break;
				}
			}
		} while (it1.hasNext() && it2.hasNext());

		while (it1.hasNext()) {
			merged.add(it1.next());
		}

		while (it2.hasNext()) {
			merged.add(it2.next());
		}

		return merged;
	}

	public static class Event implements Comparable<Event> {
		public final EventType type;
		public final GeometricObject object;
		public final double point;
		public final SplitAxis axis;
		public final SplitPoint splitPoint;

		public Event(final EventType type, final GeometricObject object,
				final SplitPoint splitPoint) {
			this.type = type;
			this.object = object;
			this.point = splitPoint.point;
			this.axis = splitPoint.axis;
			this.splitPoint = splitPoint;
		}

		@Override
		public int compareTo(Event o) {
			final double first = point - o.point;
			if (first < 0) {
				return -1;
			}
			if (first == 0) {
				if (axis == o.axis) {
					return type.value - o.type.value;
				}
				return axis.value - o.axis.value;
			}
			return 1;
		}

		@Override
		public String toString() {
			return "Event [axis=" + axis + ", type=" + type + ", position="
					+ point + object + "]";
		}
	}

	public static ClassifiedEvents splice(final Event[] events,
			final ClassifiedObjects tc, final AABB leftBox, final AABB rightBox) {
		final List<Event> elo = new ArrayList<Event>(); // left only
		final List<Event> ero = new ArrayList<Event>(); // right only
		final List<Event> ebl = new ArrayList<Event>(); // events overlapping
														// left
		final List<Event> ebr = new ArrayList<Event>(); // events overlapping
														// right

		for (int i = 0; i < events.length; i++) {
			Event e = events[i];
			switch (tc.sides.get(e.object)) {
			// events for “both sides”(3) triangles get discarded
			case 1:
				elo.add(e);
				break;
			case 2:
				ero.add(e);
				break;
			}
		}

		for (final Entry<GeometricObject, Integer> entry : tc.sides.entrySet()) {
			if (entry.getValue() == 3) {
				final GeometricObject obj = entry.getKey();
				ebl.addAll(YAFKDTree2.generateEvents(obj, leftBox));
				ebr.addAll(YAFKDTree2.generateEvents(obj, rightBox));
			}
		}

		return new ClassifiedEvents(elo, ero, ebl, ebr);
	}

	public static class ClassifiedEvents {

		public final List<Event> elo;
		public final List<Event> ero;
		public final List<Event> ebl;
		public final List<Event> ebr;

		private ClassifiedEvents(final List<Event> elo, final List<Event> ero,
				final List<Event> ebl, final List<Event> ebr) {
			this.elo = elo;
			this.ero = ero;
			this.ebl = ebl;
			this.ebr = ebr;
		}
	}

	public static ClassifiedObjects classify(List<GeometricObject> gObjects,
			final Event[] events, final PlaneCandidate candidate) {
		// 1: Left
		// 2: Right
		// 3: Both
		Map<GeometricObject, Integer> sides = new HashMap<GeometricObject, Integer>();

		for (final GeometricObject o : gObjects) {
			sides.put(o, 3);
		}

		for (final Event e : events) {
			if (e.type == EventType.END && e.axis == candidate.splitPoint.axis
					&& e.point <= candidate.splitPoint.point) {
				sides.put(e.object, 1);
			} else if (e.type == EventType.START
					&& e.axis == candidate.splitPoint.axis
					&& e.point >= candidate.splitPoint.point) {
				sides.put(e.object, 2);
			} else if (e.type == EventType.PLANAR
					&& e.axis == candidate.splitPoint.axis) {
				if (e.point < candidate.splitPoint.point
						|| (e.point == candidate.splitPoint.point && candidate.left)) {
					sides.put(e.object, 1);
				} else if (e.point > candidate.splitPoint.point
						|| (e.point == candidate.splitPoint.point && !candidate.left)) {
					sides.put(e.object, 2);
				}

			}
		}

		final List<GeometricObject> tl = new ArrayList<GeometricObject>();
		final List<GeometricObject> tr = new ArrayList<GeometricObject>();

		for (final Entry<GeometricObject, Integer> e : sides.entrySet()) {
			switch (e.getValue()) {
			case 1:
				tl.add(e.getKey());
				break;
			case 2:
				tr.add(e.getKey());
				break;
			case 3:
				tl.add(e.getKey());
				tr.add(e.getKey());
				break;
			}
		}

		return new ClassifiedObjects(tl, tr, sides);
	}

	public static class ClassifiedObjects {

		final List<GeometricObject> tl, tr;
		final Map<GeometricObject, Integer> sides;

		public ClassifiedObjects(final List<GeometricObject> tl,
				final List<GeometricObject> tr,
				final Map<GeometricObject, Integer> sides) {
			this.tl = tl;
			this.tr = tr;
			this.sides = sides;
		}

	}
}
