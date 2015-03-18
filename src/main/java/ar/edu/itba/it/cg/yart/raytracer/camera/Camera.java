package ar.edu.itba.it.cg.yart.raytracer.camera;

import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.raytracer.Bucket;
import ar.edu.itba.it.cg.yart.raytracer.ViewPlane;
import ar.edu.itba.it.cg.yart.raytracer.world.World;

public interface Camera {
	public void renderScene(final Bucket bucket, final World world,
			final ArrayIntegerMatrix result, final ViewPlane viewPlane);
}
