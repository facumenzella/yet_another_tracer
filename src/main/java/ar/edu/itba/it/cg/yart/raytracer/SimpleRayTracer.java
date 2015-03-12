package ar.edu.itba.it.cg.yart.raytracer;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.raytracer.interfaces.RayTracer;
import ar.edu.itba.it.cg.yart.raytracer.world.World;
import ar.edu.itba.it.cg.yart.utils.ImageSaver;

public class SimpleRayTracer implements RayTracer {

	private final World world;
	private final ViewPlane vp;
	
	private SimpleRayTracer(final int hRes, final int vRes) {
		this.vp = new ViewPlane(hRes, vRes);
		this.world = World.spheresWorld(vp);
		this.world.setBackgroundColor(Color.whiteColor());
	}
	
	@Override
	public void start(final String imageName, final String imageExtension) {
		long startTime = System.currentTimeMillis();
		ArrayIntegerMatrix matrix = this.world.render();
		long endTime = System.currentTimeMillis();
		long timeTaken = endTime - startTime;
		
		ImageSaver.saveImage(matrix, imageName, imageExtension);
		System.out.println("Finished rendering the scene in " + timeTaken + "ms");
	}
	
	public static RayTracer scenario1() {
		final RayTracer simpleRayTracer = new SimpleRayTracer(400, 400);
		return simpleRayTracer;
	}
	
	
}
