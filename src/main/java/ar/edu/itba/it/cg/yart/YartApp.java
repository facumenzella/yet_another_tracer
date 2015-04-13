package ar.edu.itba.it.cg.yart;

import java.io.IOException;
import java.text.ParseException;

import ar.edu.itba.it.cg.yart.exceptions.WrongParametersException;
import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.parser.SceneParser;
import ar.edu.itba.it.cg.yart.raytracer.SimpleRayTracer;
import ar.edu.itba.it.cg.yart.raytracer.interfaces.RayTracer;
import ar.edu.itba.it.cg.yart.raytracer.world.World;
<<<<<<< HEAD
import ar.edu.itba.it.cg.yart.ui.RenderWindow;
import ar.edu.itba.it.cg.yart.utils.ImageSaver;
import ar.edu.itba.it.cg.yart.utils.config.YartConfigProvider;
=======
>>>>>>> First attempt at file parser.

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
		// Wanna see something cool?
		// Try 1920 * 1080
		final int xBucketSize = 128;
		final int yBucketSize = 128;
		final double tMax = 1000;
		final double distance = 500;
		final int zoom = 1;
		final int numSamples = 16;
		
		World w = new World("jaja");
		ArrayIntegerMatrix result;

		RayTracer raytracer = new SimpleRayTracer(xBucketSize, yBucketSize, tMax, distance, zoom, numSamples, cores);
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
