package ar.edu.itba.it.cg.yart.raytracer.shade;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.light.materials.Material;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class RayTracerShader implements Shader{

	@Override
	public Color shade(final Material material, final ShadeRec sr, final Stack stack) {
		return material.shade(sr, stack);
	}

}
