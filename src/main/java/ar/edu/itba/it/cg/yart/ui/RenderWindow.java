package ar.edu.itba.it.cg.yart.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ar.edu.itba.it.cg.yart.parser.SceneParseException;
import ar.edu.itba.it.cg.yart.parser.SceneParser;
import ar.edu.itba.it.cg.yart.tracer.RenderResult;
import ar.edu.itba.it.cg.yart.tracer.Tracer;
import ar.edu.itba.it.cg.yart.tracer.Tracer.TracerCallbacks;
import ar.edu.itba.it.cg.yart.tracer.buckets.Bucket;
import ar.edu.itba.it.cg.yart.utils.ImageSaver;

public class RenderWindow extends JFrame implements TracerCallbacks {

	private static final long serialVersionUID = 1L;
	
	private static final int MIN_WIDTH = 320;
	private static final int MIN_HEIGHT = 240;
	private static final int MAX_WIDTH = 1024;
	private static final int MAX_HEIGHT = 768;
	
	private final JScrollPane scrollPane;
	private final RenderImageResult resultPanel;
	private final StatusPanel statusPanel;
	
	private BufferedImage bi;
	private Tracer raytracer;
	
	public RenderWindow(Tracer raytracer) {
		int width = raytracer.getHorizontalRes();
		int height = raytracer.getVerticalRes();
		
		this.raytracer = raytracer;
		
		raytracer.setCallbacks(this);
		
		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setHgap(0);
		borderLayout.setVgap(0);
		
		setLayout(borderLayout);
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		
		resultPanel = new RenderImageResult();
		panel.add(resultPanel);
		
		scrollPane = new JScrollPane(panel);
		scrollPane.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		
		statusPanel = new StatusPanel();
		
		setImageSize(width, height);
		
		add(scrollPane, BorderLayout.CENTER);
		//add(statusPanel, BorderLayout.LINE_START);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		setMaximumSize(new Dimension(MAX_WIDTH, MAX_HEIGHT));
		
		pack();
		setLocationRelativeTo(null);
		
		setVisible(true);
	}
	
	public void loadScene(final String sceneFile) {
		RenderResult renderResult = new RenderResult();
		SceneParser sceneParser = new SceneParser(sceneFile, raytracer);
		try {
			renderResult.startSceneLoading();
			sceneParser.parse();
			renderResult.finishSceneLoading();
			statusPanel.setLoadingTime(renderResult.getSceneLoadingTime());
		} catch (SceneParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void setImageSize(final int width, final int height) {
		int w = width;
		int h = height;
		
		bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		if (width > MAX_WIDTH) {
			w = MAX_WIDTH;
		}
		else if (width < MIN_WIDTH) {
			w = MIN_WIDTH;
		}
		
		if (height > MAX_HEIGHT) {
			h = MAX_HEIGHT;
		}
		else if (height < MIN_HEIGHT) {
			h = MIN_HEIGHT;
		}
		
		w += 10;
		h += 10;
		
		scrollPane.setPreferredSize(new Dimension(w, h));
		resultPanel.setBufferedImage(bi);
		pack();
	}
	
	@Override
	public void onBucketStarted(Bucket bucket) {
		Graphics graphics = bi.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.drawRect(bucket.getX(), bucket.getY(), bucket.getWidth() - 1, bucket.getHeight() - 1);
		resultPanel.repaint(bucket.getX(), bucket.getY(), bucket.getWidth(), bucket.getHeight());
	}

	@Override
	public void onBucketFinished(final Bucket bucket, final RenderResult result) {
		final int xStart = bucket.getX();
		final int xFinish = bucket.getX() + bucket.getWidth();
		final int yStart = bucket.getY();
		final int yFinish = bucket.getY() + bucket.getHeight();
		
		for (int y = yStart; y < yFinish; y++) {
			for (int x = xStart; x < xFinish; x++) {
				bi.setRGB(x, y, result.getPixels().get(x, y));
			}
		}
		
		resultPanel.repaint(bucket.getX(), bucket.getY(), bucket.getWidth(), bucket.getHeight());
	}

	@Override
	public void onRenderFinished(RenderResult result) {
		ImageSaver.printRenderTime(bi, result);
		resultPanel.repaint();
		statusPanel.setLoadingTime(result.getSceneLoadingTime());
		statusPanel.setPreprocessingTime(result.getPreprocessingTime());
		statusPanel.setRenderTime(result.getRenderTime());
	}

}
