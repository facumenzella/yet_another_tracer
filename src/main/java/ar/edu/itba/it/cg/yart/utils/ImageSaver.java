package ar.edu.itba.it.cg.yart.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.raytracer.RenderResult;

public class ImageSaver {
	
	public static void saveImage(final ArrayIntegerMatrix pixels, final String imageName, final String imageExtension, final RenderResult results) {
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
		
		printRenderTime(image, results);
		
		try {
			ImageIO.write(image, imageExtension, outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveImage(final ArrayIntegerMatrix pixels, final String imageName, final String imageExtension) {
		saveImage(pixels, imageName, imageExtension, null);
	}
	
	public static void printRenderTime(final BufferedImage image, final RenderResult results) {
		int w = image.getWidth();
		int h = image.getHeight();
		
		if (results != null && results.isDisplayRenderTime()) {
			String key = "Render time: " + results.getRenderTime() + "ms";
			Graphics graphics = image.getGraphics();
			graphics.setColor(new Color(0.1f, 0.1f, 0.1f, 0.75f));
			graphics.fillRect(0, h - 30, w, 30);
	        graphics.setColor(Color.WHITE);
	        graphics.setFont(new Font("Arial", Font.PLAIN, 16));
	        graphics.drawString(key + " | " + results.getTriangles() + " triangles", 10, h - 10);
		}
	}
}
