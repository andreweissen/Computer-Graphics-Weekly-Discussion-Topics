### CMSC 405 Weekly Discussion Topics ###

#### Overview ####

This repository contains the code and associated screenshots/gifs of the weekly CMSC 405 discussion topic projects. All topics received grades of 100% in accordance with the related rubric requirements.

#### Week 3 ####

The first major weekly discussion topic involved the creation of an animated SVG image. The author created a moving analog clock animation whose hands move at the same fractional rates relative to each other as those of a standard analog clock, though at an increased speed.

#### Week 5 ####

The fifth week's discussion topic involved the modification of the `FourLights.java` file found [here](http://math.hws.edu/graphicsbook/source/jogl/FourLights.java) in the creation of a unique Java OpenGL scene. Rather than work within that file to create a set of unique scene objects and light sources, the author saw fit to rewrite and reorganize the file's contents in accordance with the [single responsibility principle](https://en.wikipedia.org/wiki/Single_responsibility_principle) prior to scene modification, replacing the scene's teapot element with a [truncated icosahedron](https://en.wikipedia.org/wiki/Truncated_icosahedron).

This file imports the required `Camera.java` file found [here](http://math.hws.edu/graphicsbook/source/jogl/Camera.java). This file, untouched or modified by the author, must be included for this file to work as intended, with the included import commented out or adjusted as needed based on `Camera`'s location relative to this file.

#### Week 7 ####

The final weekly discussion topic project involved the use of the WebGL JavaScript API to create a simple animated `canvas` scene styled with some author-assembled CSS. The author's scene, including a simple polygon of indeterminate side count rotating at a rate of 45 degrees/second, includes a slider element permitting users to adjust the polygon's number of sides in real time, rendering all regular polygon types from triangle to dodecagon. This was accomplished by having the included vertex assembly function calculate and cache a vertex set for a given side count and polygon radius, the array of which is then used to create and configure a new `WebGLBuffer` instance.