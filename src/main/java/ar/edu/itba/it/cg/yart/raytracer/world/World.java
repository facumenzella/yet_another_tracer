package ar.edu.itba.it.cg.yart.raytracer.world;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.nlog2n.YAFKDTree2;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Instance;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.geometry.primitives.Plane;
import ar.edu.itba.it.cg.yart.geometry.primitives.Quadrilateral;
import ar.edu.itba.it.cg.yart.geometry.primitives.Sphere;
import ar.edu.itba.it.cg.yart.light.AmbientLight;
import ar.edu.itba.it.cg.yart.light.Directional;
import ar.edu.itba.it.cg.yart.light.Light;
import ar.edu.itba.it.cg.yart.light.PointLight;
import ar.edu.itba.it.cg.yart.light.materials.Matte;
import ar.edu.itba.it.cg.yart.light.materials.Reflective;
import ar.edu.itba.it.cg.yart.light.materials.Transparent;
import ar.edu.itba.it.cg.yart.textures.ImageTexture;
import ar.edu.itba.it.cg.yart.textures.mapping.RectangularMapping;
import ar.edu.itba.it.cg.yart.textures.mapping.SphericalMapping;
import ar.edu.itba.it.cg.yart.textures.wrappers.ClampWrap;
import ar.edu.itba.it.cg.yart.textures.wrappers.ColorWrap;
import ar.edu.itba.it.cg.yart.textures.wrappers.RepeatWrap;
import ar.edu.itba.it.cg.yart.transforms.Matrix4d;

public class World {

	private Color backgroundColor;
	private List<GeometricObject> objects = new ArrayList<GeometricObject>();
	private List<Light> lights = new ArrayList<Light>();
	private List<Light> castShadowLights = new ArrayList<Light>();
	private List<Light> doNotCastShadowLights = new ArrayList<Light>();
	private AmbientLight ambientLight;
	private YAFKDTree2 kdTree;
	private boolean preprocessed = false;
	
	/**
	 * Creates a sad, empty World.
	 */
	public World() {
		this.ambientLight = new AmbientLight(new Color(0.5, 0.5, 0.5));
	}
	
	public void preprocess() {
		if (!preprocessed) {
//			this.bspTree = new BSPAxisAligned(200, 1000, 0, 1000);
//			this.bspTree.buildTree(objects);
			preprocessed = true;
		}
	}
	
	public void buildTestWorld() {
		
		BufferedImage blackAndWhiteImage = null;
		try {
			blackAndWhiteImage = ImageIO.read(new File("/Users/fmenzella/Desktop/Facundo/soja_woodcut_0.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedImage billiardBallImage = null;
		try {
			billiardBallImage = ImageIO.read(new File("/Users/fmenzella/Desktop/dgZs7GeF.jpeg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BufferedImage earthMapImage = null;
		try {
			earthMapImage = ImageIO.read(new File("/Users/fmenzella/Desktop/Facundo/soja_woodcut_0.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BufferedImage earthSpecImage = null;
		try {
			earthSpecImage = ImageIO.read(new File("/Users/fmenzella/Desktop/dgZs7GeF.jpeg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BufferedImage menzellaImage = null;
		try {
			menzellaImage = ImageIO.read(new File("/Users/fmenzella/Desktop/Facundo/soja_woodcut_0.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		final ImageTexture earthMirrorReflection = new ImageTexture(earthSpecImage, new RectangularMapping(), new ClampWrap());
		final ImageTexture earthMirrorTexture = new ImageTexture(earthMapImage, new RectangularMapping(), new ClampWrap());
		

		final ImageTexture floorTexture = new ImageTexture(blackAndWhiteImage, new RectangularMapping(), new RepeatWrap());
		final ImageTexture billiardBallTexture = new ImageTexture(billiardBallImage, new SphericalMapping(), new ColorWrap());
		
		final ImageTexture menzellaTexture = new ImageTexture(menzellaImage, new RectangularMapping(), new ClampWrap());
		
		final Instance earthMirror = new Instance(new Quadrilateral());
		final Matrix4d earthMirrorTrans = Matrix4d.scaleMatrix(200,100,1).rotateZ(90).rotateY(100).transform(-10,-100,-30);
		earthMirror.applyTransformation(earthMirrorTrans);
		final Reflective r1m = new Reflective().setCd(earthMirrorTexture).setKa(0).setKd(1)
				.setKs(0).setExp(0).setCr(Color.whiteColor()).setKr(earthMirrorReflection);
		earthMirror.setMaterial(r1m);
		
		final Instance menzella = new Instance(new Quadrilateral());
		final Matrix4d menzellaTrans = Matrix4d.scaleMatrix(85,88.4,1).rotateX(0).transform(-60,-30,250);
		menzella.applyTransformation(menzellaTrans);
		final Matte menzellaMaterial = new Matte().setCd(menzellaTexture).setKa(0).setKd(1);
		menzella.setMaterial(menzellaMaterial);
		
		
		
		setBackgroundColor(Color.blackColor());

		final Instance billiardBall = new Instance(new Sphere());
		final Matrix4d ballTrans = Matrix4d.scaleMatrix(10, 10, 10).rotateY(0).transform(40,40,-20);
		billiardBall.applyTransformation(ballTrans);
		Reflective s1m = new Reflective();
		s1m.setCd(billiardBallTexture);
		s1m.setKd(1);
		s1m.setKa(0);
		s1m.setKs(0.2);
		s1m.setExp(200);
		s1m.setCr(Color.whiteColor());
		s1m.setKr(.05);
		billiardBall.setMaterial(s1m);
		
		final Instance waterGlass = new Instance(new Sphere());
		final Matrix4d waterGlassTrans = Matrix4d.scaleMatrix(5, 5, 5).transform(70,-25,-25);
		waterGlass.applyTransformation(waterGlassTrans);
		Transparent waterGlassMaterial = new Transparent()
		.setCd(Color.blackColor())
		.setKa(0.2)
		.setKd(0.5)
		.setKs(0.7)
		.setExp(100)
		.setCr(Color.whiteColor())
		.setKr(0.1)
		.setIor(1.33)
		.setKt(new Color(0.9, 0.9, 0.9));
		waterGlass.setMaterial(waterGlassMaterial);
		
		final Instance floor = new Instance(new Plane());
		floor.applyTransformation(Matrix4d.scaleMatrix(100, 100, 1).rotateX(0).transform(0, 0, -30));
		Matte floorM = new Matte();
		floorM.setCd(floorTexture);
		floorM.setKd(0.50);
		floorM.setKa(0.15);
		floor.setMaterial(floorM);
		
		final Directional light1 = new Directional(2.0,Color.whiteColor(),new Vector3d(-2,7,3));

		final PointLight light2 = new PointLight(2,Color.whiteColor(), new Vector3d(60, 70, 70));
//		final Matrix4d light2T = Matrix4d.transformMatrix(60, 30, 40);
//		light2.applyTransformation(light2T);
		
//		addLight(light1);
		addLight(light2);
//		light1.shadowsOff();
//		light2.shadowsOff();
				

		addObject(earthMirror);
		addObject(billiardBall);
//		addObject(menzella);
		addObject(waterGlass);
		addObject(floor);

		this.kdTree = YAFKDTree2.build(this.objects, 30);
	}
	
	
	public AmbientLight getAmbientLight() {
		return ambientLight;
	}
	
	public void setAmbientLight(final AmbientLight ambientLight) {
		this.ambientLight = ambientLight;
	}
	
	public void setBackgroundColor(final Color color) {
		backgroundColor = new Color(color.r, color.g, color.b, color.a);
	}
	
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	
	public void addObjects(final List<GeometricObject> objects) {
		this.objects.clear();
		this.objects.addAll(objects);
		preprocessed = false;
		this.kdTree = YAFKDTree2.build(this.objects, Double.POSITIVE_INFINITY);
	}
	
	public void addObject(final GeometricObject object) {
		objects.add(object);
		preprocessed = false;
	}
	
	public List<GeometricObject> getObjects() {
		return objects;
	}
	
	public YAFKDTree2 getTree() {
		return kdTree;
	}
	
	public void addLight(final Light light) {
		if (light instanceof AmbientLight) {
			setAmbientLight((AmbientLight) light);
		} else {
			lights.add(light);
			if (light.castShadows()) {
				castShadowLights.add(light);
			} else {
				doNotCastShadowLights.add(light);
			}
		}
	}
	
	public List<Light> getLights() {
		return lights;
	}
	
	public List<Light> getCastShadowLights() {
		return castShadowLights;
	}
	
	public List<Light> getDoNotCastShadowLights() {
		return doNotCastShadowLights;
	}

}
