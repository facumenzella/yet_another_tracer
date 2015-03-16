package ar.edu.itba.it.cg.yart.raytracer;

import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.raytracer.interfaces.RayTracer;
import ar.edu.itba.it.cg.yart.raytracer.world.World;
import ar.edu.itba.it.cg.yart.raytracer.world.World.Scenario;
import ar.edu.itba.it.cg.yart.utils.ImageSaver;

public class SimpleRayTracer implements RayTracer {

	private final World world;
	private final ViewPlane vp;
	
	private SimpleRayTracer(final Scenario scenario, final int hRes, final int vRes) {
		// TODO : change how we create the world
		this.vp = new ViewPlane(hRes, vRes);
		switch (scenario) {
		case SPHERE_WORLD_1:
			this.world = World.spheresWorld(vp);
			break;
		default:
			this.world = World.spheresWorld(vp);
			break;
		}
	}
	
	@Override
	public void start(final String imageName, final String imageExtension) {
		long startTime = System.currentTimeMillis();
		ArrayIntegerMatrix matrix = this.world.render();
		long endTime = System.currentTimeMillis();
		long timeTaken = endTime - startTime;
		System.out.println("Finished rendering the scene in " + timeTaken + "ms");

		ImageSaver imageSaver = new ImageSaver();
		imageSaver.saveImage(matrix, imageName, imageExtension);
	}
	
	public static RayTracer buildScenario(final Scenario scenario) {
		// TODO : fix hRes, vRes from file
		final RayTracer simpleRayTracer = new SimpleRayTracer(scenario, 1024, 1024);
		return simpleRayTracer;
	}
	
}
