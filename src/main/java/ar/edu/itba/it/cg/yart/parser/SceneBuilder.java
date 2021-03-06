package ar.edu.itba.it.cg.yart.parser;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.edu.itba.it.cg.yart.YartDefaults;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Instance;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.geometry.primitives.MeshBox;
import ar.edu.itba.it.cg.yart.geometry.primitives.Plane;
import ar.edu.itba.it.cg.yart.geometry.primitives.Sphere;
import ar.edu.itba.it.cg.yart.geometry.primitives.mesh.Mesh;
import ar.edu.itba.it.cg.yart.geometry.primitives.mesh.MeshData;
import ar.edu.itba.it.cg.yart.light.AmbientLight;
import ar.edu.itba.it.cg.yart.light.AreaLight;
import ar.edu.itba.it.cg.yart.light.Directional;
import ar.edu.itba.it.cg.yart.light.Light;
import ar.edu.itba.it.cg.yart.light.PointLight;
import ar.edu.itba.it.cg.yart.light.materials.Emissive;
import ar.edu.itba.it.cg.yart.light.materials.Material;
import ar.edu.itba.it.cg.yart.light.materials.Matte;
import ar.edu.itba.it.cg.yart.light.materials.Metal2;
import ar.edu.itba.it.cg.yart.light.materials.Reflective;
import ar.edu.itba.it.cg.yart.light.materials.Transparent;
import ar.edu.itba.it.cg.yart.parser.Identifier.IdentifierType;
import ar.edu.itba.it.cg.yart.parser.Property.PropertyType;
import ar.edu.itba.it.cg.yart.parser.SceneParser.TracerType;
import ar.edu.itba.it.cg.yart.textures.ConstantColor;
import ar.edu.itba.it.cg.yart.textures.ImageTexture;
import ar.edu.itba.it.cg.yart.textures.Texture;
import ar.edu.itba.it.cg.yart.textures.mapping.Mapping;
import ar.edu.itba.it.cg.yart.textures.mapping.RectangularMapping;
import ar.edu.itba.it.cg.yart.textures.mapping.SphericalMapping;
import ar.edu.itba.it.cg.yart.textures.wrappers.ClampWrap;
import ar.edu.itba.it.cg.yart.textures.wrappers.ColorWrap;
import ar.edu.itba.it.cg.yart.textures.wrappers.RepeatWrap;
import ar.edu.itba.it.cg.yart.textures.wrappers.Wrapper;
import ar.edu.itba.it.cg.yart.tracer.Tracer;
import ar.edu.itba.it.cg.yart.tracer.camera.Camera;
import ar.edu.itba.it.cg.yart.tracer.camera.FishEyeCamera;
import ar.edu.itba.it.cg.yart.tracer.camera.PinholeCamera;
import ar.edu.itba.it.cg.yart.tracer.tonemapper.LinearToneMapper;
import ar.edu.itba.it.cg.yart.tracer.tonemapper.OutOfGamutToneMapper;
import ar.edu.itba.it.cg.yart.tracer.tonemapper.ReinhardToneMapper;
import ar.edu.itba.it.cg.yart.transforms.Matrix4d;

public class SceneBuilder {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(YartDefaults.LOG_FILE);

	private Map<MeshData, Mesh> meshes = new HashMap<MeshData, Mesh>();
	private final Tracer raytracer;

	private Map<String, Texture> textures = new HashMap<String, Texture>();
	private Map<String, Material> namedMaterials = new HashMap<String, Material>();
	private Material currentMaterial;
	private AreaLight currentAreaLight;
	private Deque<Matrix4d> transformMatrices = new ArrayDeque<Matrix4d>();
	private Deque<Attribute> attributes = new ArrayDeque<Attribute>();

	private final Sphere referenceSphere = new Sphere();
	private final Plane referencePlane = new Plane();
	private final MeshBox referenceBox = new MeshBox();
	
	private final TracerType tracerType;

	private Path basePath = Paths.get(".").normalize();

	public SceneBuilder(final Tracer raytracer, final TracerType tracerType) {
		this.raytracer = raytracer;
		this.tracerType = tracerType;
		transformMatrices.push(new Matrix4d());
	}

	public void setBasePath(final Path path) {
		basePath = path;
	}

	public void attributeBegin(final Attribute attribute) {
		if (attribute != null) {
			transformMatrices.push(new Matrix4d(transformMatrices.peek()));
			attributes.push(attribute);
		}
	}

	public void attributeEnd() {
		transformMatrices.pop();
		attributes.pop();
	}

	public void addIdentifier(final Identifier i) throws SceneParseException {
		if (i == null) {
			return;
		}

		switch (i.getType()) {
		case SHAPE:
			final GeometricObject shape = buildShape(i);
			if (shape != null) {
				raytracer.getWorld().addObject(shape);
			}
			break;
		case MATERIAL:
			currentMaterial = buildMaterial(i);
			break;
		case LIGHT_SOURCE:
			final Light light = buildLight(i);
			if (light != null) {
				raytracer.getWorld().addLight(light);
			}
			break;
		case AREA_LIGHT_SOURCE:
			final AreaLight areaLight = buildAreaLight(i);
			if (areaLight != null) {
				raytracer.getWorld().addLight(areaLight);
			}
			break;
		case NAMED_MATERIAL:
			String args[] = i.getParameters();
			Material ret = namedMaterials.get(args[0]);
			if (ret == null) {
				LOGGER.warn("Named material \"{}\" not defined", args[0]);
			} else {
				currentMaterial = ret;
			}
			break;
		case MAKE_NAMED_MATERIAL:
			addNamedMaterial(i);
			break;
		case TEXTURE:
			buildTexture(i);
			break;
		case IDENTITY:
			transformMatrices.pop();
			transformMatrices.push(new Matrix4d());
			break;
		case TRANSFORM:
			transformMatrices.pop();
			transformMatrices.push(transform(i));
			break;
		case ROTATE:
			Matrix4d next = rotate(i);
			transformMatrices.pop();
			transformMatrices.push(next);
			break;
		case TRANSLATE:
			next = translate(i);
			transformMatrices.pop();
			transformMatrices.push(next);
			break;
		case SCALE:
			next = scale(i);
			transformMatrices.pop();
			transformMatrices.push(next);
		case CAMERA:
			raytracer.setCamera(buildCamera(i));
			break;
		case FILM:
			if (!i.getParameters()[0].equals("fleximage")) {
				LOGGER.warn("Only fleximage Film type is supported");
			}
			raytracer.setResolution(
					i.getInteger("xresolution", YartDefaults.DEFAULT_XRES),
					i.getInteger("yresolution", YartDefaults.DEFAULT_YRES));
			String tonemapper = i.getString("tonemapkernel", "linear");
			if (tonemapper.equals("linear")) {
				raytracer.setToneMapper(new LinearToneMapper());
			}
			else if (tonemapper.equals("reinhard")) {
				raytracer.setToneMapper(new ReinhardToneMapper());
			}
			else if (tonemapper.equals("outofgamut")) {
				raytracer.setToneMapper(new OutOfGamutToneMapper(i.getColor("outofgamut_color", Color.redColor())));
			}
			else {
				LOGGER.warn("Tone Mapping kernel \"{}\" unsupported. Defaulting to linear.", tonemapper);
				raytracer.setToneMapper(new LinearToneMapper());
			}
			raytracer.getToneMapper().setGamma(i.getDouble("gamma", YartDefaults.GAMMA));
			break;
		case LOOKAT:
			double[] params = ParserUtils.parseDoubleArray(i.getParameters());
			raytracer.setViewParameters(new Point3d(params[0], params[1],
					params[2]), new Point3d(params[3], params[4], params[5]),
					new Vector3d(params[6], params[7], params[8]));
			break;
		default:
			break;
		}

		if (attributes.peek() != null) {
			attributes.peek().addIdentifier(i);
		}
	}

	private Material buildMaterial(Identifier identifier) {
		Material ret = null;

		String type = identifier.getParameters()[0];

		try {
			if (type.equals("null")) {
				ret = ParserUtils.defaultMaterial;
			}
			if (type.equals("matte")) {
				Matte mat = new Matte();
				mat.setCd(getColorOrTexture(identifier, "Kd",
						Color.whiteColor()));
				mat.setKd(1);
				mat.setKa(0.15);
				ret = mat;
			} else if (type.equals("mirror")) {
				Reflective mat = new Reflective();
				mat.setCd(getColorOrTexture(identifier, "Kd",
						Color.blackColor()));
				mat.setCr(getColorOrTexture(identifier, "Kr",
						Color.whiteColor()));
				mat.setKd(0.75);
				mat.setKs(0.0);
				mat.setKa(0.3);
				mat.setExp(0);
				mat.setKr(1);
				ret = mat;
			} else if (type.equals("glass")) {
				double ior = identifier.getDouble("index", 1.5);
				if (ior < 1) {
					ior = 1;
				}

				Transparent mat = new Transparent();
				mat.setCd(Color.blackColor());
				mat.setKs(0.5);
				mat.setExp(1000);
				mat.setCr(getColorOrTexture(identifier, "Kr",
						Color.whiteColor()));
				mat.setKr(0.5);
				mat.setIor(ior);
				mat.setKd(1);
				mat.setKa(0);
				mat.setKt(getColorOrTexture(identifier, "Kt",
						Color.whiteColor()));
				ret = mat;
			}
			else if (type.equals("metal2")) {
				double roughness = identifier.getDouble("uroughness", 0.001);
				
				if (roughness <= 0) {
					LOGGER.warn("Metal roughness must be a number between 0 and 1");
					roughness = 0.001;
				}
				else if (roughness > 1) {
					LOGGER.warn("Metal roughness must be a number between 0 and 1");
					roughness = 1;
				}

				Metal2 mat = new Metal2();
				mat.setFresnel(getColorOrTexture(identifier, "fresnel", new Color(0.5)));
				mat.setRoughness(roughness);
				ret = mat;
			}
			else if (type.equals("glossy")) {
				double uroughness = identifier.getDouble("uroughness", 0.001);
				double vroughness = identifier.getDouble("vroughness", 0.001);
				double ks = identifier.getDouble("ks", 0.5);

				if (!identifier.hasProperty("uroughness")) {
					uroughness = vroughness;
				}
				else if (!identifier.hasProperty("vroughness")) {
					vroughness = uroughness;
				}

				double finalRoughness = Math.max(uroughness, vroughness);

				if (finalRoughness <= 0) {
					LOGGER.warn("Glossy roughness must be a number between 0 and 1");
					finalRoughness = 0.001;
				}
				else if (finalRoughness > 1) {
					LOGGER.warn("Glossy roughness must be a number between 0 and 1");
					finalRoughness = 1;
				}

				if (ks < 0) {
					LOGGER.warn("Ks must be a number between 0 and 1");
					ks = 0;
				}
				else if (ks > 1) {
					LOGGER.warn("Ks must be a number between 0 and 1");
					ks = 1;
				}

				double exponent = 1 / finalRoughness;

				Reflective mat = new Reflective();
				mat.setCd(getColorOrTexture(identifier, "Kd", Color.blackColor()));
				mat.setCr(getColorOrTexture(identifier, "Kd", Color.whiteColor()));
				mat.setKd(1);
				mat.setKs(ks);
				mat.setKa(0.3);
				mat.setExp(exponent);
				mat.setKr(1 - finalRoughness);
				ret = mat;
			}
		} catch (Exception e) {
			LOGGER.warn(e.getMessage());
		}

		return ret;
	}

	private Texture getColorOrTexture(Identifier identifier, String property,
			Color defaultColor) {
		Texture ret;
		PropertyType type = identifier.getPropertyType(property);

		if (!identifier.hasProperty(property)) {
			return new ConstantColor(defaultColor);
		}

		if (type == PropertyType.COLOR) {
			Color color = identifier.getColor(property, defaultColor);
			ret = new ConstantColor(color);
		} else if (type == PropertyType.TEXTURE) {
			String textureName = identifier.getString(property, null);
			if (!textures.containsKey(textureName)) {
				LOGGER.warn("Texture \"{}\" not found. Using default color",
						textureName);
				ret = new ConstantColor(defaultColor);
			} else {
				ret = textures.get(textureName);
			}
		} else {
			throw new ClassCastException("Property " + property
					+ " must be either a COLOR or TEXTURE. Found " + type + ".");
		}

		return ret;
	}

	private Texture buildTexture(final Identifier identifier) {
		Texture ret = null;
		final String[] args = identifier.getParameters();

		final String name = args[0];
		final String color = args[1];
		final String type = args[2];

		if (!color.equalsIgnoreCase("color")) {
			LOGGER.warn(
					"Texture option \"{}\" unsupported. Defaulting to \"color\"",
					color);
		}

		try {
			if (type.equalsIgnoreCase("imagemap")) {
				String filename = identifier.getString("filename");
				BufferedImage image = ImageIO.read(basePath.resolve(filename)
						.toFile());
				Mapping mapping = null;
				Wrapper wrapper = null;
				String mappingStr = identifier.getString("mapping", "uv");
				String wrapperStr = identifier.getString("wrap");

				// Load mapping
				if (mappingStr.equalsIgnoreCase("uv")) {
					mapping = null;
				} else if (mappingStr.equalsIgnoreCase("spherical")) {
					mapping = new SphericalMapping();
				} else if (mappingStr.equalsIgnoreCase("planar")) {
					mapping = new RectangularMapping();
				} else {
					LOGGER.warn(
							"Mapping type \"{}\" unrecognized. Defaulting to \"uv\"",
							mapping);
					mapping = null;
				}

				// Load wrap type
				if (wrapperStr.equalsIgnoreCase("repeat")) {
					wrapper = new RepeatWrap();
				} else if (wrapperStr.equalsIgnoreCase("black")) {
					wrapper = new ColorWrap();
				} else if (wrapperStr.equalsIgnoreCase("clamp")) {
					wrapper = new ClampWrap();
				}

				ret = new ImageTexture(image, mapping, wrapper);
			} else {
				LOGGER.warn("Texture type \"{}\" unsupported", type);
			}
		} catch (Exception e) {
			LOGGER.warn("Couldn't load texture \"{}\": {}", name,
					e.getMessage());
		}

		if (ret != null) {
			textures.put(name, ret);
		}

		return ret;
	}

	private GeometricObject buildShape(Identifier identifier)
			throws SceneParseException {
		Instance instance = null;
		Matrix4d localMatrix = new Matrix4d();

		String strType = identifier.getParameters()[0];

		try {
			if (strType.equals("sphere")) {
				double radius = identifier.getDouble("radius", 1.0);
				instance = new Instance(referenceSphere);
				localMatrix = localMatrix.scale(radius, radius, radius);
			} else if (strType.equals("plane")) {
				if (currentAreaLight != null) {
					LOGGER.warn("Infinite planes Area Light is currently unsupported");
					currentAreaLight = null;
				}
				Vector3d normal = identifier.getNormal("n",
						new Vector3d(0, 0, 1)).normalizedVector();
				if (normal.x == 0 && normal.y == 0 && normal.z == 0) {
					LOGGER.warn("Plane normal cannot have zero length");
					normal = new Vector3d(0, 0, 1);
				}
				double rX = Math.toDegrees(Math.atan2(normal.y, normal.z));
				double rY = Math.toDegrees(Math.atan2(normal.x, normal.z));
				double rZ = Math.toDegrees(Math.atan2(normal.y, normal.x));
				localMatrix = localMatrix.rotateX(rX).rotateY(rY).rotateZ(rZ);
				instance = new Instance(referencePlane);
			} else if (strType.equals("box")) {
				double width = identifier.getDouble("width", 1);
				double height = identifier.getDouble("height", 1);
				double depth = identifier.getDouble("depth", 1);

				localMatrix = localMatrix.scale(width, height, depth);
				instance = new Instance(referenceBox);
			} else if (strType.equals("mesh") || strType.equals("trianglemesh")) {
				int[] triindices = identifier.getIntegers("triindices", null);
				if (triindices == null) {
					triindices = identifier.getIntegers("indices", null);
				}
				if (triindices == null) {
					throw new PropertyNotFoundException("triindices",
							IdentifierType.SHAPE, PropertyType.INTEGER);
				}
				final Point3d[] vertices = identifier.getPoints("P");
				final Vector3d[] normals = identifier.getNormals("N", null);
				final double[] uvList = identifier.getDoubles("uv", null);
				final double[] uList;
				final double[] vList;

				final MeshData meshData = new MeshData(triindices, vertices,
						normals, uvList);

				Mesh mesh = null;

				if (meshes.containsKey(meshData)) {
					mesh = meshes.get(meshData);
				} else {
					// Load UV mapMe
					if (uvList != null && uvList.length > 1) {
						int items = (int) Math.ceil(uvList.length / 2);
						uList = new double[items];
						vList = new double[items];
						for (int i = 0; i < uList.length; i++) {
							uList[i] = uvList[i * 2 + 1];
							vList[i] = uvList[i * 2];
						}
					} else {
						uList = null;
						vList = null;
					}

					mesh = new Mesh(vertices, normals, triindices, uList,
							vList, true);
					meshes.put(meshData, mesh);
				}
				instance = new Instance(mesh);
			}
		} catch (Exception e) {
			LOGGER.warn(e.getMessage());
		}

		// If there is no material set, use the default one
		if (currentMaterial == null) {
			currentMaterial = ParserUtils.defaultMaterial;
		}

		if (instance != null) {
			// If there is an Area Light set, use an Emissive material and set
			// this object as its shape
			if (currentAreaLight != null) {
				final Emissive emissiveMaterial = new Emissive(currentMaterial);
				currentAreaLight.setMaterial(emissiveMaterial);
				currentAreaLight.setShape(instance);
				currentAreaLight = null;
				instance.setMaterial(emissiveMaterial);
				instance.setCastsShadows(false);
				instance.generateSamples(1000);
			} else {
				instance.setMaterial(currentMaterial);
			}
			Matrix4d matrix = transformMatrices.peek();
			instance.applyTransformation(localMatrix.leftMultiply(matrix));
			currentMaterial = null;
		}

		return instance;
	}

	private Light buildLight(Identifier identifier) {
		Light ret = null;

		try {
			String type = identifier.getParameters()[0];
			double gain = identifier.getDouble("gain", 1.0f)
					* YartDefaults.LIGHT_GAIN_MULTIPLIER;
			if (type.equals("point")) {
				Color l = identifier.getColor("l", Color.WHITE);
				double power = identifier.getDouble("power", 100);
				double efficacy = identifier.getDouble("efficacy", 17);
				if (power != 0 || efficacy != 0) {
					gain *= power * (efficacy / 100);
				}
				
				if (tracerType == TracerType.RAY_TRACER) {
					// In Ray Tracing, create a Point Light as always
					Point3d from = identifier
							.getPoint("from", new Point3d(0, 0, 0));
					
					PointLight light = new PointLight(gain, l,
							new Vector3d(0, 0, 0));
					light.applyTransformation(new Matrix4d().transform(from.x,
							from.y, from.z).leftMultiply(transformMatrices.peek()));
					ret = light;
				}
				else if (tracerType == TracerType.PATH_TRACER) {
					// In Path Tracing, create a similar Spherical Area Light instead
					final Instance pointLightInstance = new Instance(referenceSphere);
					final AreaLight areaLight = new AreaLight(gain, l, 1);
					final Emissive emissive = new Emissive(ParserUtils.defaultMaterial);
					pointLightInstance.applyTransformation(new Matrix4d().scale(0.05, 0.05, 0.05).leftMultiply(transformMatrices.peek()));
					pointLightInstance.setMaterial(emissive);
					pointLightInstance.setCastsShadows(false);
					pointLightInstance.generateSamples(1000);
					areaLight.setMaterial(emissive);
					areaLight.setShape(pointLightInstance);
					raytracer.getWorld().addObject(pointLightInstance);
					ret = areaLight;
				}
			} else if (type.equals("distant")) {
				Color l = identifier.getColor("l", Color.whiteColor());
				Point3d from = identifier
						.getPoint("from", new Point3d(0, 0, 0));
				Point3d to = identifier.getPoint("to", new Point3d(0, 0, 1));

				if (from.equals(to)) {
					LOGGER.warn("TO and FROM points can't be equal. Using default values");
					from = new Point3d(0, 0, 0);
					to = new Point3d(0, 0, 1);
				}

				Vector3d result = from.sub(to).transformByMatrix(
						new Matrix4d().leftMultiply(transformMatrices.peek()));
				Directional light = new Directional(gain, l, result);
				ret = light;
			} else if (type.equals("infinite")) {
				AmbientLight light = null;
				if (identifier.hasProperty("mapname")) {
					String filename = identifier.getString("mapname");
					try {
						String mapping = identifier.getString("mapping", null);
						if (mapping != null && !mapping.equalsIgnoreCase(mapping)) {
							LOGGER.warn("Environment mapping type \"{}\" unsupported. Only \"latlong\" is supported.",
							mapping);
						}
						BufferedImage environmentMap = ImageIO.read(basePath.resolve(filename).toFile());
						light = new AmbientLight(environmentMap);
					}
					catch (IOException e) {
						LOGGER.warn("Couldn't load environment map \"{}\": {}", filename, e.getMessage());
					}
				}
				if (light == null) {
					Color l = identifier.getColor("l", Color.whiteColor());
					light = new AmbientLight(gain, l);
				}
				ret = light;
			} else if (type.equals("area")) {
				ret = buildAreaLight(identifier);
			}
		} catch (Exception e) {
			LOGGER.warn(e.getMessage());
		}

		return ret;
	}

	private AreaLight buildAreaLight(final Identifier identifier) {
		AreaLight ret = null;
		try {
			double gain = identifier.getDouble("gain", 1.0f)
					* YartDefaults.LIGHT_GAIN_MULTIPLIER;
			if (identifier.getParameters()[0].equals("area")) {
				double power = identifier.getDouble("power", 100);
				double efficacy = identifier.getDouble("efficacy", 17);
				if (power != 0 || efficacy != 0) {
					gain *= power * (efficacy / 100);
				}
				ret = new AreaLight(gain,
						identifier.getColor("l", Color.WHITE),
						identifier.getInteger("nsamples", 1));
			} else {
				LOGGER.warn("AreaLightSource only accepts \"area\" as parameter");
			}
		} catch (Exception e) {
			LOGGER.warn(e.getMessage());
		}
		currentAreaLight = ret;
		return ret;
	}

	private void addNamedMaterial(Identifier identifier) {
		String[] idArgs = identifier.getParameters();
		try {
			// Wrap the given identifier in a convenient Material one
			String[] args = { identifier.getString("type", "matte") };
			Identifier i = new Identifier(IdentifierType.MATERIAL, args);
			i.addProperties(identifier.getProperties());
			Material ret = buildMaterial(i);
			if (ret != null) {
				namedMaterials.put(idArgs[0], ret);
			} else {
				LOGGER.warn("Couldn't create named material \"{}\"", idArgs[0]);
			}
		} catch (Exception e) {
			LOGGER.warn(e.getMessage());
		}
	}

	private Camera buildCamera(Identifier identifier) {
		Camera ret = null;
		final String type = identifier.getParameters()[0];

		try {
			double[] defaults = { -1, 1, -1, 1 };
			double[] screenWindow = identifier.getDoubles("screenwindow",
					defaults);
			double fov = identifier.getDouble("fov",
					YartDefaults.DEFAULT_FOV);
			
			if (screenWindow.length < 4) {
				LOGGER.warn("Screen Window needs 4 floats. Using default value");
				screenWindow = defaults;
			}
			
			if (type.equals("perspective")) {
				PinholeCamera cam = new PinholeCamera(
						YartDefaults.DEFAULT_EYE,
						YartDefaults.DEFAULT_LOOKAT, YartDefaults.DEFAULT_UP,
						500, 1, YartDefaults.DEFAULT_RAY_DEPTH, tracerType.getStrategy());
				
				cam.setScreenWindow(screenWindow[0], screenWindow[1],
						screenWindow[2], screenWindow[3]);
				cam.setFov(fov);
				cam.setLensRadius(identifier.getDouble("lensradius",
						YartDefaults.DEFAULT_LENS_RADIUS));
				cam.setFocalDistance(identifier.getDouble("focaldistance",
						YartDefaults.DEFAULT_FOCAL_DISTANCE));
				ret = cam;
			} else if (type.equals("fisheye")) {
				FishEyeCamera cam = new FishEyeCamera(
						YartDefaults.DEFAULT_EYE,
						YartDefaults.DEFAULT_LOOKAT, YartDefaults.DEFAULT_UP,
						1, YartDefaults.DEFAULT_RAY_DEPTH, fov, tracerType.getStrategy());
				cam.setFov(fov);
				cam.setScreenWindow(screenWindow[0], screenWindow[1],
						screenWindow[2], screenWindow[3]);
				ret = cam;
			} else {
				LOGGER.warn("Camera type \"{}\" unsupported", type);
			}
		} catch (Exception e) {
			LOGGER.warn(e.getMessage());
		}

		return ret;
	}

	private Matrix4d transform(Identifier identifier) {
		double[] array = ParserUtils.parseDoubleArray(identifier
				.getParameters());
		final double m00 = array[0];
		final double m10 = array[1];
		final double m20 = array[2];
		final double m30 = array[3];

		final double m01 = array[4];
		final double m11 = array[5];
		final double m21 = array[6];
		final double m31 = array[7];

		final double m02 = array[8];
		final double m12 = array[9];
		final double m22 = array[10];
		final double m32 = array[11];

		final double m03 = array[12];
		final double m13 = array[13];
		final double m23 = array[14];
		final double m33 = array[15];

		return new Matrix4d(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21,
				m22, m23, m30, m31, m32, m33);
	}

	private Matrix4d rotate(Identifier identifier) {
		Matrix4d current = transformMatrices.peek();
		double[] params = ParserUtils.parseDoubleArray(identifier
				.getParameters());

		double degrees = params[0];
		int rotateX = (int) params[1];
		int rotateY = (int) params[2];
		int rotateZ = (int) params[3];

		if (rotateX != 0) {
			current = current.rotateX(degrees);
		}

		if (rotateY != 0) {
			current = current.rotateY(degrees);
		}

		if (rotateZ != 0) {
			current = current.rotateZ(degrees);
		}

		return current;
	}

	private Matrix4d translate(Identifier identifier) {
		Matrix4d current = transformMatrices.peek();

		double[] params = ParserUtils.parseDoubleArray(identifier
				.getParameters());

		double translateX = params[0];
		double translateY = params[1];
		double translateZ = params[2];

		return current.transform(translateX, translateY, translateZ);
	}

	private Matrix4d scale(Identifier identifier) {
		Matrix4d current = transformMatrices.peek();

		double[] params = ParserUtils.parseDoubleArray(identifier
				.getParameters());

		double scaleX = params[0];
		double scaleY = params[1];
		double scaleZ = params[2];

		return current.scale(scaleX, scaleY, scaleZ);
	}
}
