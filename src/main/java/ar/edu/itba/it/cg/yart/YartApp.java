package ar.edu.itba.it.cg.yart;

import ar.edu.itba.it.cg.yart.exceptions.WrongParametersException;
import ar.edu.itba.it.cg.yart.raytracer.SimpleRayTracer;
import ar.edu.itba.it.cg.yart.raytracer.world.World.Scenario;

public class YartApp {
	
	public static void main(String[] args) {
		
		if (args.length < 2) {
			throw new WrongParametersException("You fool!! Introduce a name & extension for the image");
		}
		final String imageName = args[0];
		final String imageExtension = args[1];

		SimpleRayTracer.buildScenario(Scenario.SPHERE_WORLD_1).start(imageName, imageExtension);
	}
	
	
}
