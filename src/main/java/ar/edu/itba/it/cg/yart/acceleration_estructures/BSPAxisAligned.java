package ar.edu.itba.it.cg.yart.acceleration_estructures;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.primitives.BoundingBox;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.raytracer.tracer.Tracer;

public class BSPAxisAligned{

	private Node root;
	private BoundingBox initialBox;
	private final double tMin;
	private final double tMax;
	private final double minZ;
	private final double maxZ;
	private final LeafNode emptyLeafNode;

	private static final int DEPTH = 1;
	protected static final double EPSILON = 0.00001;

	
	public BSPAxisAligned(final double minZ, final double maxZ, final double tMin, final double tMax) {
		this.tMin = tMin;
		this.tMax = tMax;
		this.minZ = minZ;
		this.maxZ = maxZ;
		this.emptyLeafNode = new LeafNode(new ArrayList<GeometricObject>());
	}
	
	public List<GeometricObject> getAllObjects() {
		return this.root.getObjects();
	}
	
	public void buildTree(final List<GeometricObject> objects) {
		this.buildInitialBox();
		final double middleX = (initialBox.p0.x + initialBox.p1.x) / 2 + EPSILON;
		this.root = subdivideXAxis(initialBox, objects, middleX, 0);
	}
	
	private void buildInitialBox() {
		double minX = -Double.MAX_VALUE;
		double maxX = Double.MAX_VALUE;
		double minY = -Double.MAX_VALUE;
		double maxY = Double.MAX_VALUE;
		this.initialBox = new BoundingBox(new Point3(minX, minY, minZ), new Point3(maxX, maxY, maxZ));
	}
	
	private Node subdivideXAxis(final BoundingBox currentBox, final List<GeometricObject> currentObjects, 
			final double middlePoint, final int currentDepth) {
		if (currentObjects.size() == 0) {
			return this.emptyLeafNode;
		}
		
		if (currentDepth == DEPTH) {
			return new LeafNode(currentObjects);
		}		
		
		List<Double> candidates = this.planeCandidatesX(currentObjects);
		final double nextMiddlePoint = this.bestXCandidate(candidates, currentBox, currentObjects) + EPSILON;
		
		final BoundingBox leftBox = new BoundingBox(currentBox.p0, new Point3(middlePoint, currentBox.p1.y, currentBox.p1.z));
		final BoundingBox rightBox = new BoundingBox(new Point3(middlePoint, currentBox.p0.y, currentBox.p0.z), currentBox.p1);
		
		List<GeometricObject> leftObjects = new ArrayList<GeometricObject>();
		List<GeometricObject> rightObjects = new ArrayList<GeometricObject>();
		
		for (GeometricObject o : currentObjects) {
			this.addObjectToBoxes(leftBox, rightBox, o, leftObjects, rightObjects);
		}
		
		XNode xNode = new XNode(currentBox, currentObjects, middlePoint);
		xNode.setLeftNode(subdivideYAxis(leftBox, leftObjects, nextMiddlePoint, currentDepth + 1));
		xNode.setRightNode(subdivideYAxis(rightBox, rightObjects, nextMiddlePoint, currentDepth + 1));
		
		return xNode;
	}
	
	private Node subdivideYAxis(final BoundingBox currentBox, final List<GeometricObject> currentObjects, 
			final double middlePoint, final int currentDepth) {

		if (currentObjects.size() == 0) {
			return this.emptyLeafNode;
		}
		
		if (currentDepth == DEPTH) {
			return new LeafNode(currentObjects);
		}
		
		List<Double> candidates = this.planeCandidatesY(currentObjects);
		final double nextMiddlePoint = this.bestYCandidate(candidates, currentBox, currentObjects) + EPSILON;
		
		final BoundingBox leftBox = new BoundingBox(currentBox.p0, new Point3(currentBox.p1.x, middlePoint, currentBox.p1.z));
		final BoundingBox rightBox = new BoundingBox(new Point3(currentBox.p0.x, middlePoint, currentBox.p0.z), currentBox.p1);
		
		List<GeometricObject> leftObjects = new ArrayList<GeometricObject>();
		List<GeometricObject> rightObjects = new ArrayList<GeometricObject>();
		
		for (GeometricObject o : currentObjects) {
			this.addObjectToBoxes(leftBox, rightBox, o, leftObjects, rightObjects);

		}
		
		YNode yNode = new YNode(currentBox, currentObjects, middlePoint);
		yNode.setLeftNode(subdivideZAxis(leftBox, leftObjects, nextMiddlePoint, currentDepth + 1));
		yNode.setRightNode(subdivideZAxis(rightBox, rightObjects, nextMiddlePoint, currentDepth + 1));
		
		return yNode;
	}
	
	private Node subdivideZAxis(final BoundingBox currentBox, final List<GeometricObject> currentObjects, 
			final double middlePoint, final int currentDepth) {

		if (currentObjects.size() == 0) {
			return this.emptyLeafNode;
		}
		
		if (currentDepth == DEPTH) {
			return new LeafNode(currentObjects);
		}
		
		List<Double> candidates = this.planeCandidatesZ(currentObjects);
		final double nextMiddlePoint = this.bestZCandidate(candidates, currentBox, currentObjects) + EPSILON;
		
		final BoundingBox leftBox = new BoundingBox(currentBox.p0, new Point3(currentBox.p1.x, currentBox.p1.y, middlePoint));
		final BoundingBox rightBox = new BoundingBox(new Point3(currentBox.p0.x, currentBox.p0.y, middlePoint), currentBox.p1);

		List<GeometricObject> leftObjects = new ArrayList<GeometricObject>();
		List<GeometricObject> rightObjects = new ArrayList<GeometricObject>();
		
		for (GeometricObject o : currentObjects) {
			this.addObjectToBoxes(leftBox, rightBox, o, leftObjects, rightObjects);
		}
		
		ZNode zNode = new ZNode(currentBox, currentObjects, middlePoint);
		zNode.setLeftNode(subdivideXAxis(leftBox, leftObjects, nextMiddlePoint, currentDepth + 1));
		zNode.setRightNode(subdivideXAxis(rightBox, rightObjects, nextMiddlePoint, currentDepth + 1));
		
		return zNode;
	}
	
	private void addObjectToBoxes(final BoundingBox leftBox, final BoundingBox rightBox, final GeometricObject o, 
			final List<GeometricObject> leftObjects, final List<GeometricObject> rightObjects) {
		final BoundingBox box = o.getBoundingBox();
		if (box == null) {
			leftObjects.add(o);
			rightObjects.add(o);
		} else {
			if (leftBox.boxIsInside(box)) {
				leftObjects.add(o);
			}
			if (rightBox.boxIsInside(box)){
				rightObjects.add(o);
			}
		}
	}
	
	public List<Double> planeCandidatesX(final List<GeometricObject> objects) {
		List<Double> candidates = new ArrayList<Double>();
		for (GeometricObject o : objects) {
			BoundingBox b = o.getBoundingBox();
			candidates.add(b.p0.x);
			candidates.add(b.p1.x);
		}
		return candidates;
	}
	
	public List<Double> planeCandidatesY(final List<GeometricObject> objects) {
		List<Double> candidates = new ArrayList<Double>();
		for (GeometricObject o : objects) {
			BoundingBox b = o.getBoundingBox();
			candidates.add(b.p0.y);
			candidates.add(b.p1.y);
		}
		return candidates;
	}
	
	public List<Double> planeCandidatesZ(final List<GeometricObject> objects) {
		List<Double> candidates = new ArrayList<Double>();
		for (GeometricObject o : objects) {
			BoundingBox b = o.getBoundingBox();
			candidates.add(b.p0.z);
			candidates.add(b.p1.z);
		}
		return candidates;
	}

	public double planeCostX(final BoundingBox currentBox, final List<GeometricObject> objects, final double middlePoint) {
		double left = 0;
		double right = 0;
		double stradding = 0;
		for (GeometricObject o : objects) {
			BoundingBox b = o.getBoundingBox();
			if (b.p0.x > middlePoint) {
				right ++;
			} else if (b.p1.x < middlePoint) {
				left ++;
			} else {
				stradding++;
			}
		}
		
		final BoundingBox leftBox = new BoundingBox(currentBox.p0, new Point3(middlePoint, currentBox.p1.y, currentBox.p1.z));
		final BoundingBox rightBox = new BoundingBox(new Point3(middlePoint, currentBox.p0.y, currentBox.p0.z), currentBox.p1);
		
		return this.root.getBoundingBox().getSurfaceArea() * 
				( (leftBox.getSurfaceArea() * 
						(left + stradding)) + (rightBox.getSurfaceArea() * (right + stradding)) );
	}
	
	public double planeCostY(final BoundingBox currentBox, final List<GeometricObject> objects, final double middlePoint) {
		double under = 0;
		double over = 0;
		double stradding = 0;
		for (GeometricObject o : objects) {
			BoundingBox b = o.getBoundingBox();
			if (b.p0.y > middlePoint) {
				over ++;
			} else if (b.p1.x < middlePoint) {
				under ++;
			} else {
				stradding++;
			}
		}
		
		final BoundingBox leftBox = new BoundingBox(currentBox.p0, new Point3(currentBox.p1.x, middlePoint, currentBox.p1.z));
		final BoundingBox rightBox = new BoundingBox(new Point3(currentBox.p0.x, middlePoint, currentBox.p0.z), currentBox.p1);
		
		return this.root.getBoundingBox().getSurfaceArea() * 
				( (leftBox.getSurfaceArea() * 
						(under + stradding)) + (rightBox.getSurfaceArea() * (over + stradding)) );
	}
	
	public double planeCostZ(final BoundingBox currentBox, final List<GeometricObject> objects, final double middlePoint) {
		double near = 0;
		double far = 0;
		double stradding = 0;
		for (GeometricObject o : objects) {
			BoundingBox b = o.getBoundingBox();
			if (b.p0.z > middlePoint) {
				near ++;
			} else if (b.p1.x < middlePoint) {
				far ++;
			} else {
				stradding++;
			}
		}
		
		final BoundingBox leftBox = new BoundingBox(currentBox.p0, new Point3(currentBox.p1.x, currentBox.p1.y, middlePoint));
		final BoundingBox rightBox = new BoundingBox(new Point3(currentBox.p0.x, currentBox.p0.y, middlePoint), currentBox.p1);
		
		return this.root.getBoundingBox().getSurfaceArea() * 
				( (leftBox.getSurfaceArea() * 
						(near + stradding)) + (rightBox.getSurfaceArea() * (far + stradding)) );
	}
	
	public double bestXCandidate(final List<Double> candidates, final BoundingBox currentBox, 
			final List<GeometricObject> objects) {
		double minCost = Double.POSITIVE_INFINITY;
		for (Double d : candidates) {
			final double cost = this.planeCostX(currentBox, objects, d);
			if (cost < minCost) {
				minCost = cost;
			}
		}
		return minCost;
	}
	
	public double bestYCandidate(final List<Double> candidates, final BoundingBox currentBox, 
			final List<GeometricObject> objects) {
		double minCost = Double.POSITIVE_INFINITY;
		for (Double d : candidates) {
			final double cost = this.planeCostY(currentBox, objects, d);
			if (cost < minCost) {
				minCost = cost;
			}
		}
		return minCost;
	}
	
	public double bestZCandidate(final List<Double> candidates, final BoundingBox currentBox, 
			final List<GeometricObject> objects) {
		double minCost = Double.POSITIVE_INFINITY;
		for (Double d : candidates) {
			final double cost = this.planeCostZ(currentBox, objects, d);
			if (cost < minCost) {
				minCost = cost;
			}
		}
		return minCost;
	}
	
	public Color traceRay(final Ray ray, final Tracer tracer, final ShadeRec sr) {
//		Point3 origin = new Point3(0, 0, 200);
//		Point3 hitP = new Point3(-45,-10,20);
//		Ray aRay = new Ray(origin, hitP.sub(origin));
		return p_traceObjectsForRay(ray, root, tMin, tMax, tracer, sr);
	}

	private Color p_traceObjectsForRay(final Ray ray, final Node node, 
			final double min, final double max, final Tracer tracer, final ShadeRec sr) {
		
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
				Color nearColor = p_traceObjectsForRay(ray, near, min, t, tracer, sr);
				if (tracer.hitObject()) {
					return nearColor;
				}
				return p_traceObjectsForRay(ray, far, t, max, tracer, sr);
			}
		}
	}
	
}
