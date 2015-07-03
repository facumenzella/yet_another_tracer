package ar.edu.itba.it.cg.yart.tracer.strategy;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.light.materials.Material;
import ar.edu.itba.it.cg.yart.tracer.ShadeRec;

public class PathTracingStrategy implements TracerStrategy {

	@Override
	public Color shade(Material material, ShadeRec sr, Stack stack) {
		return material.globalShade(sr, stack);
	}

}
