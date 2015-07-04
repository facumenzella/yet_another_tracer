package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.nlog2n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.edu.itba.it.cg.yart.YartDefaults;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.KDInternalNode;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.KDLeafNode;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.KDNodeAbstract;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.PlaneCandidate;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.SplitPoint;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack.StackElement;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.AABB;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.tracer.AbstractTracer;
import ar.edu.itba.it.cg.yart.tracer.Ray;
import ar.edu.itba.it.cg.yart.tracer.ShadeRec;
import ar.edu.itba.it.cg.yart.tracer.strategy.TracerStrategy;
import ar.edu.itba.it.cg.yart.transforms.Matrix4d;

// This has O(N log N) or at least we hope it does

public class YAFKDTree {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(YartDefaults.LOG_FILE);

	private static int kMAX_DEPTH = 60;

	public static double kEPSILON = 0.00001;

	private KDNodeAbstract root;
	private AABB rootAABB;

	private static AABB buildInfiniteRootAABB(
			final List<GeometricObject> objects) {
		double minX = -Double.MAX_VALUE;
		double minY = minX, minZ = minX;
		double maxX = Double.MAX_VALUE;
		double maxY = maxX, maxZ = maxX;

		return new AABB(new Point3d(minX, maxY, minZ), new Point3d(maxX, minY,
				maxZ));
	}

	private static AABB buildRootAABB(final List<GeometricObject> objects) {
		boolean allFinite = true;
		for (GeometricObject geometricObject : objects) {
			if (!geometricObject.isFinite()) {
				allFinite = false;
			}
		}

		if (!allFinite) {
			return YAFKDTree.buildInfiniteRootAABB(objects);
		}

		double minX = Double.MAX_VALUE;
		double minY = minX, minZ = minX;
		double maxX = -Double.MAX_VALUE;
		double maxY = maxX, maxZ = maxX;

		for (GeometricObject g : objects) {
			AABB b = g.getBoundingBox();
			minX = Math.min(minX, b.p0.x - kEPSILON);
			minZ = Math.min(minZ, b.p0.z - kEPSILON);
			minY = Math.min(minY, b.p1.y - kEPSILON);
			maxX = Math.max(maxX, b.p1.x + kEPSILON);
			maxY = Math.max(maxY, b.p0.y + kEPSILON);
			maxZ = Math.max(maxZ, b.p1.z + kEPSILON);
		}
		return new AABB(new Point3d(minX, maxY, minZ), new Point3d(maxX, minY,
				maxZ));
	}

	public static YAFKDTree build(final List<GeometricObject> gObjects) {
		final AABB aabb = YAFKDTree.buildRootAABB(gObjects);
		return YAFKDTree.build(gObjects, aabb);
	}

	public static YAFKDTree build(final List<GeometricObject> gObjects,
			final AABB aabb) {
		final long start = System.currentTimeMillis();
		YAFKDTree tree = new YAFKDTree();

		final List<Event> eventList = new ArrayList<>();
		for (final GeometricObject obj : gObjects) {
			eventList.addAll(Event.generateEvents(obj, aabb));
		}
		tree.rootAABB = aabb;
		Event[] events = eventList.toArray(new Event[eventList.size()]);
		Arrays.sort(events);

		tree.root = buildTree(tree.rootAABB, gObjects, events);

		LOGGER.info("Tree built in {}ms. Initials: {}. ",
				(System.currentTimeMillis() - start), gObjects.size());
		return tree;
	}

	private static KDNodeAbstract buildTree(final AABB rootBox,
			final List<GeometricObject> gObjects, final Event[] events) {
		return buildKDNodeAbstract(gObjects, rootBox, 0, events, rootBox,
				new HashSet<PlaneCandidate>());
	}

	private static KDNodeAbstract buildKDNodeAbstract(
			final List<GeometricObject> gObjects, final AABB box,
			final int currentDepth, Event[] events, final AABB rootAABB,
			final Set<PlaneCandidate> prevs) {
		final double kKI = 1.5;
		final int size = gObjects.size();

		PlaneCandidate bestCandidate = findPlane(size, box, events, rootAABB);
		if (bestCandidate == null) {
			return new KDLeafNode(gObjects);
		}
		boolean terminate = bestCandidate.cost > (kKI * size);
		if (size == 0 || currentDepth >= kMAX_DEPTH || bestCandidate == null
				|| terminate || prevs.contains(bestCandidate)) {
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

		final Set<PlaneCandidate> prevsPlus = new HashSet<PlaneCandidate>();
		prevsPlus.add(bestCandidate);

		final int nextDepth = currentDepth + 1;

		return new KDInternalNode(splitPoint, buildKDNodeAbstract(
				classifiedObjects.tl, boxes[0], nextDepth,
				el.toArray(new Event[] {}), rootAABB, prevsPlus),
				buildKDNodeAbstract(classifiedObjects.tr, boxes[1], nextDepth,
						er.toArray(new Event[] {}), rootAABB, prevsPlus));
	}

	private static AABB[] splitAABB(final AABB box, SplitPoint p) {
		AABB[] aabbs = new AABB[2];
		AABB leftBox = null;
		AABB rightBox = null;
		switch (p.axis) {
		case 0:
			leftBox = new AABB(box.p0, new Point3d(p.point, box.p1.y, box.p1.z));
			rightBox = new AABB(new Point3d(p.point, box.p0.y, box.p0.z),
					box.p1);
			break;
		case 1:
			leftBox = new AABB(new Point3d(box.p0.x, p.point, box.p0.z), box.p1);
			rightBox = new AABB(box.p0,
					new Point3d(box.p1.x, p.point, box.p1.z));
			break;
		case 2:
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
		SplitPoint splitPoint = null;
		AABB boxes[] = null;
		boolean left = false;

		int nLX = 0, nPX = 0, nRX = size;
		int nLY = 0, nPY = 0, nRY = size;
		int nLZ = 0, nPZ = 0, nRZ = size;

		for (int i = 0; i < eventsQty; i++) {

			int pSTARTX = 0, pENDX = 0, pPLANARX = 0;
			int pSTARTY = 0, pENDY = 0, pPLANARY = 0;
			int pSTARTZ = 0, pENDZ = 0, pPLANARZ = 0;

			Event e = events[i];

			// END(0), PLANAR(1), START(2);
			while (i < eventsQty && events[i].axis == e.axis
					&& events[i].point == e.point && events[i].type == 0) {
				i++;
				switch (e.splitPoint.axis) {
				case 0:
					pENDX++;
					break;
				case 1:
					pENDY++;
					break;
				case 2:
					pENDZ++;
					break;
				default:
					System.out.println("Holy shit the impossible happened");
				}
			}

			while (i < eventsQty && events[i].axis == e.axis
					&& events[i].point == e.point && events[i].type == 1) {
				i++;
				switch (e.splitPoint.axis) {
				case 0:
					pPLANARX++;
					break;
				case 1:
					pPLANARY++;
					break;
				case 2:
					pPLANARZ++;
					break;
				default:
					System.out.println("Holy shit the impossible happened");
				}
			}

			while (i < eventsQty && events[i].axis == e.axis
					&& events[i].point == e.point && events[i].type == 2) {
				i++;
				switch (e.splitPoint.axis) {
				case 0:
					pSTARTX++;
					break;
				case 1:
					pSTARTY++;
					break;
				case 2:
					pSTARTZ++;
					break;
				default:
					System.out.println("Holy shit the impossible happened");
				}
			}

			PlaneCandidate candidate = null;
			switch (e.splitPoint.axis) {
			case 0:
				nPX = pPLANARX;
				nRX -= pPLANARX;
				nRX -= pENDX;
				candidate = sah(e.splitPoint, box, nLX, nRX, nPX, rootAABB);
				nPX = 0;
				nLX += pPLANARX;
				nLX += pSTARTX;
				break;
			case 1:
				nPY = pPLANARY;
				nRY -= pPLANARY;
				nRY -= pENDY;
				candidate = sah(e.splitPoint, box, nLY, nRY, nPY, rootAABB);
				nPY = 0;
				nLY += pPLANARY;
				nLY += pSTARTY;
				break;
			case 2:
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
				splitPoint = candidate.splitPoint;
				boxes = candidate.boxes;
				left = candidate.left;
			}
		}
		if (minCost != Double.MAX_VALUE) {
			return new PlaneCandidate(boxes, splitPoint, minCost, left);
		}
		return null;
	}

	private static PlaneCandidate sah(final SplitPoint p, final AABB box,
			final double nl, final double nr, final double np,
			final AABB rootAABB) {
		final double kKT = 1;
		final double kKI = 1.5;
		AABB boxes[] = splitAABB(box, p);
		final AABB boxL = boxes[0];
		final AABB boxR = boxes[1];

		double area = rootAABB.surfaceArea;

		final double pl = boxL.surfaceArea / area;
		final double pr = boxR.surfaceArea / area;

		final double cl = (kKT + kKI * ((pl * nl + np) + (pr * nr)));
		final double cr = (kKT + kKI * ((pl * nl) + (pr * nr + np)));

		double cost = (cl < cr) ? cl : cr;
		return new PlaneCandidate(boxes, p, cost, (cl < cr) ? true : false);
	}

	// Here we trace rays. Work for kids
	public Color traceRay(final Ray ray, final ShadeRec sr, final double tMax,
			final Stack stack, final TracerStrategy strategy) {
		if (!rootAABB.hit(ray) || ray.depth > AbstractTracer.HOPS) {
			return sr.world.backgroundColor;
		}

		double tNear = 0;
		double tFar = tMax;
		KDNodeAbstract node = null;

		int top = stack.index;
		stack.push(root, tNear, tFar);

		double dir[] = ray.direction;
		double origin[] = { ray.origin.x, ray.origin.y, ray.origin.z };

		while (true) {
			--stack.index;
			StackElement e = stack.stack[stack.index];

			node = e.node;
			tNear = e.min;
			tFar = e.max;
			while (node.gObjects == null) {

				KDInternalNode internalNode = (KDInternalNode) node;

				KDNodeAbstract near = null, far = null;
				final double splitPoint = internalNode.splitPoint.point;
				final double rayDirAxis = dir[internalNode.splitPoint.axis];
				final double rayOriginAxis = origin[internalNode.splitPoint.axis];

				final double diff = splitPoint - rayOriginAxis;

				if (rayOriginAxis < splitPoint) {
					near = internalNode.left;
					far = internalNode.right;
				} else if (rayOriginAxis > splitPoint) {
					near = internalNode.right;
					far = internalNode.left;
				} else if (rayDirAxis < 0) {
					near = internalNode.left;
					far = internalNode.right;
				} else {
					near = internalNode.right;
					far = internalNode.left;
				}

				final double t = diff / rayDirAxis;

				if (t > tFar || t < 0.0) {
					node = near;
				} else {
					if (t <= tNear) {
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
			if (objects != null) {
				Vector3d normal = null;
				Point3d localHitPoint = null;
				double tMin = tFar;
				double u = 0, v = 0;
				Matrix4d m = null;
				for (GeometricObject object : objects) {
					double t = object.hit(ray, sr, tMin, stack);
					if (t != Double.NEGATIVE_INFINITY && t < tMin) {
						sr.hitObject = true;
						sr.material = object.getMaterial();
						m = object.matrix;

						normal = sr.normal;
						u = sr.u;
						v = sr.v;
						localHitPoint = sr.localHitPoint;
						tMin = t;
					}
				}
				Color color = null;
				if (sr.hitObject) {
					sr.t = tMin;
					sr.normal = normal;
					sr.localHitPoint = localHitPoint;
					sr.ray = ray;
					if (m == null) {
						System.out.println("no matrix");
					}
					final double dx = (m.m00 * sr.localHitPoint.x)
							+ (m.m01 * sr.localHitPoint.y)
							+ (m.m02 * sr.localHitPoint.z) + m.m03;
					final double dy = (m.m10 * sr.localHitPoint.x)
							+ (m.m11 * sr.localHitPoint.y)
							+ (m.m12 * sr.localHitPoint.z) + m.m13;
					final double dz = (m.m20 * sr.localHitPoint.x)
							+ (m.m21 * sr.localHitPoint.y)
							+ (m.m22 * sr.localHitPoint.z) + m.m23;
					sr.hitPoint = new Point3d(dx, dy, dz);
					sr.depth = ray.depth;
					sr.u = u;
					sr.v = v;
					color = strategy.shade(sr.material, sr, stack);
					stack.index = top;
					return color;
				}
			}

			// If stack is empty
			if (stack.peek() == top) {
				return sr.world.backgroundColor;
			}
		}
	}

	public double traceShadowHit(final Ray ray, final double tMax,
			final Stack stack) {
		if (!rootAABB.hit(ray) || ray.depth > AbstractTracer.HOPS) {
			return Double.NEGATIVE_INFINITY;
		}

		double tNear = 0;
		double tFar = tMax;
		KDNodeAbstract node = null;

		int top = stack.index;
		stack.push(root, tNear, tFar);

		double dir[] = ray.direction;
		double origin[] = { ray.origin.x, ray.origin.y, ray.origin.z };

		while (true) {
			--stack.index;
			StackElement e = stack.stack[stack.index];
			node = e.node;
			tNear = e.min;
			tFar = e.max;
			while (node.gObjects == null) {

				KDInternalNode internalNode = (KDInternalNode) node;

				KDNodeAbstract near = null, far = null;
				final double splitPoint = internalNode.splitPoint.point;
				final double rayDirAxis = dir[internalNode.splitPoint.axis];
				final double rayOriginAxis = origin[internalNode.splitPoint.axis];

				final double diff = splitPoint - rayOriginAxis;

				if (rayOriginAxis < splitPoint) {
					near = internalNode.left;
					far = internalNode.right;
				} else if (rayOriginAxis > splitPoint) {
					near = internalNode.right;
					far = internalNode.left;
				} else if (rayDirAxis < 0) {
					near = internalNode.left;
					far = internalNode.right;
				} else {
					near = internalNode.right;
					far = internalNode.left;
				}

				final double t = diff / rayDirAxis;

				if (t > tFar || t < 0.0) {
					node = near;
				} else {
					if (t <= tNear) {
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
			if (objects != null) {
				double tMin = tFar;
				boolean hit = false;
				for (GeometricObject object : objects) {
					if (object.isCastsShadows()) {
						double t = object.shadowHit(ray, tMin, stack);
						if (t != Double.NEGATIVE_INFINITY && t < tMin) {
							tMin = t;
							hit = true;
						}
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
			final double tMax, final Stack stack) {
		if (!rootAABB.hit(ray) || ray.depth > AbstractTracer.HOPS) {
			return Double.NEGATIVE_INFINITY;
		}
		double tNear = 0;
		double tFar = tMax;
		KDNodeAbstract node = null;

		int top = stack.index;
		stack.push(root, tNear, tFar);

		double dir[] = ray.direction;
		double origin[] = { ray.origin.x, ray.origin.y, ray.origin.z };

		while (true) {
			--stack.index;
			StackElement e = stack.stack[stack.index];
			node = e.node;
			tNear = e.min;
			tFar = e.max;
			while (node.gObjects == null) {
				KDInternalNode internalNode = (KDInternalNode) node;

				KDNodeAbstract near = null, far = null;
				final double splitPoint = internalNode.splitPoint.point;
				final double rayDirAxis = dir[internalNode.splitPoint.axis];
				final double rayOriginAxis = origin[internalNode.splitPoint.axis];

				final double diff = splitPoint - rayOriginAxis;

				if (rayOriginAxis < splitPoint) {
					near = internalNode.left;
					far = internalNode.right;
				} else if (rayOriginAxis > splitPoint) {
					near = internalNode.right;
					far = internalNode.left;
				} else if (rayDirAxis < 0) {
					near = internalNode.left;
					far = internalNode.right;
				} else {
					near = internalNode.right;
					far = internalNode.left;
				}

				final double t = diff / rayDirAxis;

				if (t > tFar || t < 0.0) {
					node = near;
				} else {
					if (t <= tNear) {
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
			if (objects != null) {
				double tMin = tFar;
				boolean hit = false;
				Vector3d normal = null;
				Point3d localHitPoint = null;
				double u = 0, v = 0;
				for (GeometricObject object : objects) {
					double t = object.hit(ray, sr, tMin, stack);
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

}
