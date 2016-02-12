package ar.edu.itba.it.cg.yart.light.materials;

import java.util.List;

import ar.edu.itba.it.cg.yart.YartDefaults;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.light.AreaLight;
import ar.edu.itba.it.cg.yart.light.Light;
import ar.edu.itba.it.cg.yart.light.brdf.CookTorrance;
import ar.edu.itba.it.cg.yart.light.brdf.PDF;
import ar.edu.itba.it.cg.yart.textures.Texture;
import ar.edu.itba.it.cg.yart.tracer.Ray;
import ar.edu.itba.it.cg.yart.tracer.ShadeRec;
import ar.edu.itba.it.cg.yart.tracer.strategy.PathTracingStrategy;
import ar.edu.itba.it.cg.yart.tracer.strategy.TracerStrategy;

public class Metal2 extends MaterialAbstract {
    
    private double tMax = YartDefaults.DEFAULT_RAY_DEPTH;
    private CookTorrance specularBRDF = new CookTorrance();
    private final TracerStrategy shader = new PathTracingStrategy();
   
    private Color specularShade(ShadeRec sr, Stack stack) {
            final double dx = -sr.ray.direction[0];
            final double dy = -sr.ray.direction[1];
            final double dz = -sr.ray.direction[2];

            final Vector3d wo = new Vector3d(dx, dy, dz);
            final Color colorL = Color.whiteColor();
            final Color a = sr.world.getAmbientLight().L(sr);
           
            Color specular = Color.blackColor();
            specular.r = colorL.r*a.r;
            specular.g = colorL.g*a.g;
            specular.b = colorL.b*a.b;
           
            final List<Light> castShadowLights = sr.world.getCastShadowLights();
           
            for (final AreaLight light : sr.world.getAreaLights()) {
                    double pdfAndSamples = light.pdf(sr) * light.getSamplesNumber();
                    for (int i = 0; i < light.getSamplesNumber(); i++) {
                            final Vector3d wi = light.getDirection(sr);
                            double ndotwi = sr.normal.dot(wi);
   
                            if (ndotwi > 0.0) {
                                    boolean inShadow = false;
   
                                    Ray shadowRay = new Ray(sr.hitPoint, wi);
                                    inShadow = light.inShadow(shadowRay, sr, stack);
                                    if (!inShadow) {
                                            final Color aux = specularBRDF.f(sr, wo, wi);
                                            //final Color si = specular.f(sr, wo, wi);
   
                                           
                                            final Color li = light.L(sr);
                                            final double g = light.G(sr);
                                            final double factor = ndotwi * g / pdfAndSamples;
                                           
                                            aux.r *= li.r * factor;
                                            aux.g *= li.g * factor;
                                            aux.b *= li.b * factor;
   
                                            specular.r += aux.r;
                                            specular.g += aux.g;
                                            specular.b += aux.b;
                                    }
                            }
                    }
            }

            for (final Light light : castShadowLights) {
                    final Vector3d wi = light.getDirection(sr);
                    double ndotwi = sr.normal.dot(wi);

                    if (ndotwi > 0.0) {
                            boolean inShadow = false;

                            Ray shadowRay = new Ray(sr.hitPoint, wi);
                            inShadow = light.inShadow(shadowRay, sr, stack);
                            if (!inShadow) {
                                    final Color aux = specularBRDF.f(sr, wo, wi);
                                    //final Color si = specular.f(sr, wo, wi);

                                   
                                    final Color li = light.L(sr);
                                   
                                    aux.r *= li.r * ndotwi;
                                    aux.g *= li.g * ndotwi;
                                    aux.b *= li.b * ndotwi;

                                    specular.r += aux.r;
                                    specular.g += aux.g;
                                    specular.b += aux.b;
                            }
                    }
            }
            return specular;
    }
   
    @Override
    public Color shade(ShadeRec sr, Stack stack) {
            return specularShade(sr, stack);
    }

    @Override
    public Color globalShade(ShadeRec sr, Stack stack) {
            final double dx = -sr.ray.direction[0];
            final double dy = -sr.ray.direction[1];
            final double dz = -sr.ray.direction[2];
           
            Vector3d wi = new Vector3d(0, 0, 0);
            PDF pdf = new PDF();
           
            Color colorL = this.shade(sr, stack);
            final Vector3d wo = new Vector3d(dx, dy, dz);
            ShadeRec sRec1 = new ShadeRec(sr.world);
            final Color f1 = specularBRDF.sample_f(sr, wo, wi, pdf);
            final double ndotwi1 = sr.normal.dot(wi);

            final Ray reflectedRay1 = new Ray(sr.hitPoint, wi);
            reflectedRay1.depth = sr.ray.depth + 1;
            Color reflectedColor1 = sr.world.getTree().traceRay(reflectedRay1,
                            sRec1, tMax, stack, shader);
           
            final double gain = 1;
            final double factor1 = ndotwi1 * gain / pdf.pdf;
           
            colorL.r += reflectedColor1.r * f1.r * factor1;
            colorL.g += reflectedColor1.g * f1.g * factor1;
            colorL.b += reflectedColor1.b * f1.b * factor1;
           
            return colorL;
    }      
   
    public Metal2 setFresnel(final Texture fresnel) {
            specularBRDF.setFresnel(fresnel);
            return this;
    }
   
    public Metal2 setRoughness(final double roughness) {           
            specularBRDF.setRoughness(roughness);
            return this;
    }


}
