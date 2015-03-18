package ar.edu.itba.it.cg.yart.raytracer.interfaces;

import java.util.List;

import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.raytracer.Bucket;
import ar.edu.itba.it.cg.yart.raytracer.world.World;

public interface RayTracer {
	public ArrayIntegerMatrix render(final World world);
	public void start(final String imageName, final String imageExtension);
	public List<Bucket> getBuckets();
}
