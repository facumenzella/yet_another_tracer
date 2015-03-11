package ar.edu.itba.it.cg.yart;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.exceptions.WrongParametersException;
import ar.edu.itba.it.cg.yart.geometry.Plane;
import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Sphere;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.raytracer.ViewPlane;
import ar.edu.itba.it.cg.yart.raytracer.World;

public class YartApp {
	
	public static void main(String[] args) {
		
		if (args.length < 2) {
			throw new WrongParametersException("You fool!! Introduce a name & extension for the image");
		}
		final String imageName = args[0];
		final String imageExtension = args[1];
		
		World w = new World();
		ViewPlane vp = new ViewPlane(400, 400);
		w.setBackgroundColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
		
		Sphere s1 = new Sphere(new Point3(0, 0, 0), 85);
		Sphere s2 = new Sphere(new Point3(70, 60, 60), 40);
		Plane p1 = new Plane(new Point3(0,40,0), new Vector3d(0, 0.4041, 0.4041));
		
		s1.color = new Color(1.0f, 0.0f, 0.0f);
		s2.color = new Color(1.0f, 1.0f, 0.0f);
		p1.color = new Color(0.0f, 1.0f, 0.5f);
		
		w.addObject(s1);
		w.addObject(s2);
		w.addObject(p1);
		
		long startTime = System.currentTimeMillis();
		ArrayIntegerMatrix matrix = w.render(vp);
		long endTime = System.currentTimeMillis();
		long timeTaken = endTime - startTime;
		
		saveImage(matrix, imageName, imageExtension);
		System.out.println("Finished rendering the scene in " + timeTaken + "ms");
	}
	
	public static void saveImage(final ArrayIntegerMatrix pixels, final String imageName, final String imageExtension) {
		int w = pixels.cols();
		int h = pixels.rows();
		
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				image.setRGB(x, y, pixels.get(x, y));
			}
		}

		StringBuilder nameBuilder = new StringBuilder();
		final String base = "./images/";
		nameBuilder.append(base).append(imageName).append(".").append(imageExtension);
		File outputfile = new File(nameBuilder.toString());
		
		try {
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
