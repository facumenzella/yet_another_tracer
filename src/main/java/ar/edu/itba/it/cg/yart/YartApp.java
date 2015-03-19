package ar.edu.itba.it.cg.yart;

import ar.edu.itba.it.cg.yart.exceptions.WrongParametersException;
import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.raytracer.SimpleRayTracer;
import ar.edu.itba.it.cg.yart.raytracer.world.World;
import ar.edu.itba.it.cg.yart.ui.RenderWindow;
import ar.edu.itba.it.cg.yart.utils.ImageSaver;

public class YartApp {

	public static void main(String[] args) {

		if (args.length < 2) {
			throw new WrongParametersException(
					"You fool!! Introduce a name & extension for the image");
		}
		final String imageName = args[0];
		final String imageExtension = args[1];

		World w = new World("jaja");
		ArrayIntegerMatrix result;
		
		SimpleRayTracer raytracer = new SimpleRayTracer(640, 480, 256);
		new RenderWindow(raytracer);

		long start = System.currentTimeMillis();
		result = raytracer.render(w);
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	
		ImageSaver imageSaver = new ImageSaver();
		imageSaver.saveImage(result, imageName, imageExtension);
	}

}
