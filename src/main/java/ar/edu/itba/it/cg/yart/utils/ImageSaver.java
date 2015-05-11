package ar.edu.itba.it.cg.yart.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;

public class ImageSaver {
	
	public void saveImage(final ArrayIntegerMatrix pixels, final String imageName, final String imageExtension) {
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
			ImageIO.write(image, imageExtension, outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
