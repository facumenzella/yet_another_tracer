package ar.edu.itba.it.cg.yart;

import ar.edu.itba.it.cg.yart.exceptions.WrongParametersException;
import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.raytracer.SimpleRayTracer;
import ar.edu.itba.it.cg.yart.raytracer.interfaces.RayTracer;
import ar.edu.itba.it.cg.yart.raytracer.world.World;
import ar.edu.itba.it.cg.yart.ui.RenderWindow;
import ar.edu.itba.it.cg.yart.utils.ImageSaver;
import ar.edu.itba.it.cg.yart.utils.config.YartConfigProvider;

public class YartApp {

	private final static YartConfigProvider configs = new YartConfigProvider();
	
	public static void main(String[] args) {

		if (args.length < 2) {
			throw new WrongParametersException(
					"You fool!! Introduce a name & extension for the image");
		}
		final String imageName = args[0];
		final String imageExtension = args[1];

		int cores = configs.getCoresQty();
		int alpha = cores * 2;
		// Wanna see something cool?
		// Try 1920 * 1080
		final int hRes = 640;
		final int vRes = 480;
		final double fov = 30;
		final int xBucketSize = hRes / alpha;
		final int yBucketSize = vRes / alpha;
		final double tMax = 1000;
		final double distance = 500;
		final int zoom = 1;
		final int numSamples = 16;
		
		World w = new World("jaja");
		ArrayIntegerMatrix result;

		RayTracer raytracer = new SimpleRayTracer(hRes, vRes, fov, xBucketSize, yBucketSize, tMax, distance, zoom, numSamples, cores);
		raytracer.setWorld(w);

		new RenderWindow(raytracer);

		long start = System.currentTimeMillis();
		result = raytracer.render();
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	
		ImageSaver imageSaver = new ImageSaver();
		imageSaver.saveImage(result, imageName, imageExtension);
	}

}
