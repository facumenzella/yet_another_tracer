# Yet another Ray Tracer #
> Because there are not too many.
>> By Del Giudice, Menzella & Noriega
## COMPUTACION GRAFICA ##
### ITBA ###

Check the commit: **c824fcb**

You may be wondering how do I run this app?
It's easy:

```
java -jar yartapp [commands]
 -aa,--antialiasing <arg>   Number of antialiasing samples. Must be a
                            positive number
 -b,--benchmark <arg>       Number of benchmark runs. Must be a positive
                            number
 -bs,--bucketsize <arg>     Bucket size to be used
 -d,--raydepth <arg>        Ray depth. Must be a positive number
 -g,--gui                   Display render progress in a window
 -h,--help                  Prints this help
 -hb,--heart-beat           In normal, non-gui mode, display a character
                            each time a bucket has finished
 -i,--input <arg>           Input scene file
 -o,--output <arg>          Output file's name
 -pathtracer                Enable Path Tracing
 -s,--samples <arg>         Number of samples for pathtracing. Must be a
                            positive number
 -t,--time                  Print render time and triangle count in output
                            image
 -th,--threads <arg>        Number of threads to be used
 -tr,--trace-depth <arg>    Ray hops. Must be a positive integer number
```
For example:
Do you want to see something cool?
```
java -jar yart.jar -o render.png -i scenes/Yart.lxs -d 4 4 -aa 1 -g
```
Do you want to see a fancy Mario?
```
java -jar yart.jar -o render.png -i scenes/Benchmarks/MirrorLowReflectivity.lxs -d 4 4 -aa 1 -g
```


##Acceleration structures##
- SAH KDTree

##Geometric Objects##
- Mesh
- MeshBox

##Primitives##
- Sphere
- Triangle
- Plane
- Disc

##Materials##
- Matte
- Mirror
- Glass
- Metal2

##Lights##
- Point
- Directional
- Ambient