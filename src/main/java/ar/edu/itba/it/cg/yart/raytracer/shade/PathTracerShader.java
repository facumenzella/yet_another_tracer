package ar.edu.itba.it.cg.yart.raytracer.shade;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.light.materials.Material;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.samplers.SamplerAbstract;

public class PathTracerShader implements Shader {

	@Override
	public Color shade(Material material, ShadeRec sr, Stack stack) {
		Color colorL = material.globalShade(sr, stack);
		for (int i = 1; i < SamplerAbstract.SAMPLES; i++) {
			final Color c = material.globalShade(sr, stack);
			colorL.r += c.r;
			colorL.g += c.g;
			colorL.b += c.b;
		}
		colorL.r /= SamplerAbstract.SAMPLES;
		colorL.g /= SamplerAbstract.SAMPLES;
		colorL.b /= SamplerAbstract.SAMPLES;
		return colorL;
	}

}
