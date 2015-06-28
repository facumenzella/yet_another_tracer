package ar.edu.itba.it.cg.yart.raytracer.world;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.nlog2n.YAFKDTree;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Instance;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.geometry.primitives.Plane;
import ar.edu.itba.it.cg.yart.geometry.primitives.Quadrilateral;
import ar.edu.itba.it.cg.yart.geometry.primitives.Sphere;
import ar.edu.itba.it.cg.yart.geometry.primitives.mesh.Mesh;
import ar.edu.itba.it.cg.yart.light.AmbientLight;
import ar.edu.itba.it.cg.yart.light.AreaLight;
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

	public Color backgroundColor;
	private List<GeometricObject> objects = new ArrayList<GeometricObject>();
	private List<Mesh> meshes = new ArrayList<Mesh>();
	private List<Light> lights = new ArrayList<Light>();
	private List<Light> castShadowLights = new ArrayList<Light>();
	private List<Light> doNotCastShadowLights = new ArrayList<Light>();
	private List<AreaLight> areaLights = new ArrayList<AreaLight>();
	private AmbientLight ambientLight;
	private YAFKDTree kdTree;
	private boolean preprocessed = false;
	
	/**
	 * Creates a sad, empty World.
	 */
	public World() {
		this.ambientLight = new AmbientLight(Color.blackColor());
		setBackgroundColor(Color.blackColor());
	}
	
	public int getTriangleCount() {
		int acc = 0;
		for (Mesh m : meshes) {
			acc += m.numTriangles;
		}
		return acc;
	}
	
	public void preprocess() {
		if (!preprocessed) {
			this.kdTree = YAFKDTree.build(this.objects);
			preprocessed = true;
			
			for (Mesh m : meshes) {
				m.preprocess();
			}
		}
	}
	
	public void buildTestWorld() {
		
		BufferedImage blackAndWhiteImage = null;
		try {
			blackAndWhiteImage = ImageIO.read(new File("./images/BlackAndWhite.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedImage billiardBallImage = null;
		try {
			billiardBallImage = ImageIO.read(new File("./images/BilliardBall.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BufferedImage earthMapImage = null;
		try {
			earthMapImage = ImageIO.read(new File("./images/earthmap.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BufferedImage earthSpecImage = null;
		try {
			earthSpecImage = ImageIO.read(new File("./images/earthspec.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BufferedImage menzellaImage = null;
		try {
			menzellaImage = ImageIO.read(new File("./images/menzella.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		final ImageTexture earthMirrorReflection = new ImageTexture(earthSpecImage, new RectangularMapping(), new ClampWrap());
		final ImageTexture earthMirrorTexture = new ImageTexture(earthMapImage, new RectangularMapping(), new ClampWrap());
		

		final ImageTexture floorTexture = new ImageTexture(blackAndWhiteImage, new RectangularMapping(), new RepeatWrap());
		final ImageTexture billiardBallTexture = new ImageTexture(billiardBallImage, new SphericalMapping(), new ColorWrap());
		
		final ImageTexture menzellaTexture = new ImageTexture(menzellaImage, new RectangularMapping(), new ClampWrap());
		
		final Instance earthMirror = new Instance(new Quadrilateral());
		final Matrix4d earthMirrorTrans = Matrix4d.scaleMatrix(200,100,1).rotateZ(90).rotateY(90).transform(-10,-100,-30);
		earthMirror.applyTransformation(earthMirrorTrans);
		final Transparent r1m = new Transparent().setCd(earthMirrorTexture).setKa(0).setKd(1)
				.setKs(0).setExp(0).setCr(Color.whiteColor()).setKr(0.5).setIor(1.1).setKt(0.5);
		earthMirror.setMaterial(r1m);
		
		final Instance menzella = new Instance(new Quadrilateral());
		final Matrix4d menzellaTrans = Matrix4d.scaleMatrix(85,88.4,1).rotateX(0).transform(-60,-30,250);
		menzella.applyTransformation(menzellaTrans);
		final Matte menzellaMaterial = new Matte().setCd(menzellaTexture).setKa(0).setKd(1);
		menzella.setMaterial(menzellaMaterial);
		
		
		
		setBackgroundColor(Color.blackColor());

		final Instance billiardBall = new Instance(new Sphere());
		final Matrix4d ballTrans = Matrix4d.scaleMatrix(20, 20, 20).rotateY(0).transform(40,40,-10);
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
		final Matrix4d waterGlassTrans = Matrix4d.scaleMatrix(5, 5, 5).transform(100,-25,-25);
		waterGlass.applyTransformation(waterGlassTrans);
		Transparent waterGlassMaterial = new Transparent()
		.setCd(Color.redColor())
		.setKa(0)
		.setKd(0)
		.setKs(0.5)
		.setExp(100)
		.setCr(Color.whiteColor())
		.setKr(0.5)
		.setIor(1.33)
		.setKt(0.9);
		waterGlass.setMaterial(waterGlassMaterial);
		
		final Instance floor = new Instance(new Plane());
		floor.applyTransformation(Matrix4d.scaleMatrix(100, 100, 1).rotateX(0).transform(0, 0, -30));
		Matte floorM = new Matte();
		floorM.setCd(floorTexture);
		floorM.setKd(0.50);
		floorM.setKa(0.15);
		floor.setMaterial(floorM);
		
		final Directional light1 = new Directional(2.0,Color.whiteColor(),new Vector3d(-2,7,3));

		final PointLight light2 = new PointLight(2,Color.whiteColor(), new Vector3d(60, 70, 80));
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
	
//	public Color getBackgroundColor() {
//		return ambientLight.getColor();
//	}
	
	public void addObjects(final List<GeometricObject> objects) {
		for (GeometricObject o : objects) {
			addObject(o);
		}
		preprocessed = false;
	}
	
	public void addObject(final GeometricObject object) {
		Instance result;
		if (object instanceof Instance) {
			result = (Instance) object;
		}
		else {
			result = new Instance(object);
		}
		addObject(result);
	}
	
	public void addObject(final Instance instance) {
		if (instance != null && instance.object != null) {
			if (instance.object instanceof Mesh) {
				meshes.add((Mesh) instance.object);
			}
			
			objects.add(instance);
			preprocessed = false;
		}
	}
	
	public List<GeometricObject> getObjects() {
		return objects;
	}
	
	public YAFKDTree getTree() {
		return kdTree;
	}
	
	public void addLight(final Light light) {
		if (light instanceof AmbientLight) {
			setAmbientLight((AmbientLight) light);
		}
		else if (light instanceof AreaLight) {
			addAreaLight((AreaLight) light);
		} else {
			lights.add(light);
			if (light.castShadows()) {
				castShadowLights.add(light);
			} else {
				doNotCastShadowLights.add(light);
			}
		}
	}
	
	public void addAreaLight(final AreaLight light) {
		if (light != null) {
			areaLights.add(light);
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
	
	public List<AreaLight> getAreaLights() {
		return areaLights;
	}

}
