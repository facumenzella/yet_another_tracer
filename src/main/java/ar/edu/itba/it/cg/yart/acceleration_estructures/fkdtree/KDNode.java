package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree;

import java.util.List;

import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;


public abstract class KDNode {
	
		public List<GeometricObject> gObjects;
		
		public abstract boolean isLeaf();
}
