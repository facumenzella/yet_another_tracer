# Yet another Ray Tracer #
> Because there are not too many. It traces paths as well.
>> By Del Giudice, Menzella & Noriega
## COMPUTACION GRAFICA ##
### ITBA ###

Check the commit: **176895f**

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
Do you want to see a pretty cool Captain Falcon?
```
java -jar yart.jar -i scenes/CaptainFalcon/captainfalcon.lxs -o images/render.png -d 2 -th 4 -bs 32 -pathtracer -s 1000 -tr 10 -g
```
![alt tag](https://cloud.githubusercontent.com/assets/1125252/13339303/74d9d260-dc06-11e5-9139-9e824511ac01.png)

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

##Enviromental Maps##

##Lights##
- Point
- Directional
- Ambient
