package ar.edu.itba.it.cg.yart.raytracer.shade;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.light.materials.Material;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class PathTracerShader implements Shader {

	@Override
	public Color shade(Material material, ShadeRec sr, Stack stack) {
		return material.globalShade(sr, stack);
	}

}
