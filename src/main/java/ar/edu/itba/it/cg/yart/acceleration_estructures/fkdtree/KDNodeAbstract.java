package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree;

import java.util.List;

import ar.edu.itba.it.cg.yart.geometry.primitives.AABB;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;


public abstract class KDNodeAbstract {
	
		public List<GeometricObject> gObjects;
		public AABB box;
		public abstract boolean isLeaf();
}
