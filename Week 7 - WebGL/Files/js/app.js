/**
 * @file app.js
 * @fileoverview The main module of the program, contains several access
 * namespaces denoting which functions, arrays, enums, and variables may be
 * returned for external or global usage. Begun 09/29/2018
 * @author Andrew Eissen
 */
'use strict';

/**
 * @description The primary namespace module, containing a pair of access
 * namespaces that determine whether or not external invocation is permitted or
 * prohibited. Almost all functions, arrays, enums, and variables are included
 * in the <code>inaccessible</code> object namespace, with only the function
 * <code>accessible.init</code> and a handful of enum getters left externally
 * available for invocation by outside functions if needed. These namespaces are
 * supposed to both limit access and simulate the access keywords
 * <code>public</code> and <code>private</code> in Java. As they are prepended
 * to the function signatures and names of arrays in the body of the module,
 * they intentionally bring to mind these keywords and alert the user as to what
 * functions are publicly available and which are private in much the same way.
 * <br />
 * <br />
 * Contained within the <code>inaccessible</code> object namespace are all the
 * application logic functions, utility functions, handlers, and assorted enums
 * and arrays of objects containing and processing the data required to display
 * the <code>canvas</code>-mediated scene. HTML is mostly generated dynamically
 * within this module rather than hardcoded directly into the HTML file itself.
 * This decision was made to permit easier configuration and development changes
 * in the event of an interface design alteration without having to modify both
 * the JS file and the HTML file. Similarly, almost all "magic numbers" are
 * included together in the enums to allow the author to change certain values
 * as needed in a single place rather than in multiple places in the file.
 * <br />
 * <br />
 * The contents of this file were mostly written by the author with inspiration
 * and occasional code taken from a few online tutorial locations. In addition
 * to the Week 7 Chapter 6 reading materials and the author's Project 3 code,
 * <a href="https://www.html5rocks.com/en/tutorials/webgl/webgl_transforms">the
 * WebGL transforms</a> tutorial and the
 * <a href="https://webglfundamentals.org/webgl/lessons/webgl-2d-matrices.html">
 * WebGl 2d matrices</a> tutorial were used as the basic inspiration for some of
 * the WebGl-related functions, providing direction and guidance regarding
 * several somewhat confusing aspects of the API. Additionally, the
 * <a href="https://developer.mozilla.org/en-US/docs/Web/API/WebGL_API/">MDN
 * WebGl documentation</a> and associated examples were also used and consulted
 * to provide direction in certain cases.
 * <br />
 * <br />
 * <pre>
 * Table of contents
 * - Enums
 *   - Utility                    Line 092
 *   - Identifiers                Line 118
 *   - Text                       Line 137
 *   - Colors                     Line 153
 *   - Shaders                    Line 172
 * - Functions
 *   - Utility functions          Line 196
 *   - Assembly functions         Line 242
 *   - Handler functions          Line 482
 *   - Public functions           Line 726
 * </pre>
 *
 * @see {@link //www.html5rocks.com/en/tutorials/webgl/webgl_transforms|Src}
 * @see {@link //webglfundamentals.org/webgl/lessons/webgl-2d-matrices.html|Src}
 * @see {@link //developer.mozilla.org/en-US/docs/Web/API/WebGL_API/|Src}
 * @author Andrew Eissen
 * @module WeekSevenModule
 * @const
 */
const WeekSevenModule = (function () {

  // Declare access namespaces
  let accessible, inaccessible;

  // Define access namespaces
  accessible = accessible || {};
  inaccessible = inaccessible || {};

  /**
   * @description This enum is used to consolidate certain often-used values
   * (often called "magic words") that may need to be adjusted by the author
   * over the course of the Week 7 Discussion project's implementation. Rather
   * than sift through the entirety of the file looking for instances of these
   * magic numbers, their values were simply moved to a convenient immutable
   * enum and changed from a single place as needed.
   *
   * @readonly
   * @enum {number}
   */
  inaccessible.Utility = Object.freeze({
    FADE_IN_INTERVAL: 10,
    OPACITY_INCREASE_AMOUNT: 0.015,
    CANVAS_WIDTH: 640,
    CANVAS_HEIGHT: 480,
    ASPECT_RATIO: 4/3,
    DEGREES_PER_SECOND: 45.0,
    VERTEX_COMPONENTS: 2,
    SHAPE_NUMBER_SIDES: 6,
    SHAPE_RADIUS: 0.5,
    SLIDER_MINIMUM_VALUE: 3,
    SLIDER_MAXIMUM_VALUE: 12,
    SLIDER_INITIAL_VALUE: 6,
   });

  /**
   * @description This enum is used to store the DOM element ids used by this
   * file's contents to grab certain dynamically assembled parts of the DOM
   * interface as required. As these string values are unlikely to ever require
   * dynamic adjustment within the file, they were moved to a string enum object
   * of their own. They are required to be immutable as their names are used in
   * the <code>styles.css</code> file to style the DOM elements.
   *
   * @readonly
   * @enum {string}
   */
  inaccessible.Identifiers = Object.freeze({
    CONTAINER_ID: 'wrapper',
    CANVAS_CONTAINER_ID: 'glcanvas-container',
    CANVAS_ID: 'glcanvas',
    SLIDER_CONTAINER_ID: 'slider-container',
    SLIDER_ID: 'slider',
  });

  /**
   * @description This string enum was initially supposed to possess more values
   * as the author had planned on adding a <code>label</code> element alongside
   * the polygon side slider placed below the <code>canvas</code>. However, as
   * the CSS used to style the slider precluded the addition of a
   * <code>label</code> in a logical place, the other strings were removed. Only
   * the error message was retained in the enum.
   *
   * @readonly
   * @enum {string}
   */
  inaccessible.Text = Object.freeze({
    ERROR_TEXT: 'Error: WebGL is not compatible with your current browser.',
  });

  /**
   * @description This enum is used to store arrays of numbers that together
   * compose an rgb color code used by <code>inaccessible.gl.clearColor</code>
   * and <code>inaccessible.gl.uniform4fv</code> to color the background of the
   * <code>canvas</code> scene and the polygon itself. Originally, there were
   * more colors at play as the author was testing the addition of vertex colors
   * via one of the linked tutorials, though these were eventually removed in
   * favor of a flat coloration.
   *
   * @readonly
   * @enum {!Array<number>}
   */
  inaccessible.Colors = Object.freeze({
    GRAY: [0.6, 0.6, 0.6, 0.9], // #B2B2B2
    GOLD: [0.5, 0.5, 0.2, 1.0], // #808033
  });

  /**
   * @description This enum holds the string representations of the shader code
   * written in the OpenGL Shading Language (GLSL) for use in the body of the
   * function <code>inaccessible.handleSceneAnimation</code>. This code was
   * taken and modified from several sources listed above in the module
   * documentation, with much of it coming from the
   * <a href="https://www.html5rocks.com/en/tutorials/webgl/webgl_transforms">
   * HTML5Rocks.com WebGL transforms</a> tutorial the author consulted over the
   * course of the file's construction.
   *
   * @see {@link //www.html5rocks.com/en/tutorials/webgl/webgl_transforms|Src}
   * @readonly
   * @enum {string}
   */
  inaccessible.Shaders = Object.freeze({
    FRAGMENT: `
      #ifdef GL_ES
        precision highp float;
      #endif
      uniform vec4 u_colorVector;
      void main () {
        gl_FragColor = u_colorVector;
      }
    `,
    VERTEX: `
      attribute vec2 a_position;
      uniform vec2 u_scalingVector;
      uniform vec2 u_rotationVector;
      void main () {
        vec2 positionRotated = vec2(
          a_position.x * u_rotationVector.y + a_position.y * u_rotationVector.x,
          a_position.y * u_rotationVector.y - a_position.x * u_rotationVector.x
        );
        gl_Position = vec4(positionRotated * u_scalingVector, 0.0, 1.0);
      }
    `,
  });

  // Utility functions

  /**
   * @description This function is based on the similarly-named fading function
   * available by default in jQuery. As the scene is set to an opacity style
   * value of 0 from the start (namely in its bulk assembly function
   * <code>inaccessible.assembleUserInterface</code>), this function simply
   * increases the element's opacity until it reaches a value of 1, thus giving
   * the impression of the scene fading in from the start. This helps hide the
   * often jerky scene assembly sequence from view for a few milliseconds.
   *
   * @param {string} paramElementId
   * @returns {void}
   */
  inaccessible.fadeIn = function (paramElementId) {

    // Declarations
    let that, container, interval;

    // Definitions
    that = this;
    container = document.getElementById(paramElementId);
    interval = setInterval(function () {
      if (container.style.opacity < 1) {
        container.style.opacity = (parseFloat(container.style.opacity) +
            that.Utility.OPACITY_INCREASE_AMOUNT);
      } else {
        clearInterval(interval);
        return;
      }
    }, this.Utility.FADE_IN_INTERVAL);
  };

  /**
   * @description This function returns a <code>boolean</code> value based on
   * whether or not the inputted object is an array. It is used by
   * <code>inaccessible.assembleElement</code> to determine if inputted
   * argument parameters need to be formatted as arrays.
   *
   * @param {object} paramTarget
   * @returns {boolean}
   */
  inaccessible.isArray = function (paramTarget) {
    return Object.prototype.toString.call(paramTarget) === '[object Array]';
  };

  // Assembly functions

  /**
   * @description This assembly method is used to create and return an array of
   * numbers representing the vertices of the shape to be displayed in the
   * <code>canvas</code> scene. It is called first from within the body of the
   * initialization function, <code>inaccessible.main</code>, to create the
   * first shape, and can be subsequently called by the slider element handler
   * <code>inaccessible.handleSliderChange</code> to create ana array of
   * vertices based on the user-selected number of polygon sides.
   *
   * @param {integer} paramSides
   * @param {number} paramRadius (constant value, set at 0.5)
   * @returns {!Array<number>}
   */
  inaccessible.assembleShapeVertices = function (paramSides, paramRadius) {

    // Declarations
    let angle, vertices, x, y;

    // Smallest possible shape is a triangle
    if (paramSides < 3) {
      paramSides = 3;
    }

    // Definitions
    angle = 2 * Math.PI / paramSides;
    vertices = [];

    for (let i = 0; i < paramSides; i++) {
      x = paramRadius * Math.cos(angle * i);
      y = paramRadius * Math.sin(angle * i);
      vertices.push(x, y);
    }

    // Set as object-global for future use in primative construction process
    this.vertexCount = vertices.length / this.Utility.VERTEX_COMPONENTS;

    // Return array
    return vertices;
  };

  /**
   * @description As its name implies, this function is used to construct an
   * individual instance of an element or object; in this case, it builds a
   * single HTML element that will be returned from the function and appended to
   * the DOM dynamically. It accepts an array of strings denoting the type of
   * element to create and also handles potentially nested element arrays for
   * elements that are to exist inside the outer element tags as inner HTML.
   * <br />
   * <br />
   * This method was borrowed and slightly modified from a StackOverflow thread
   * response found <a href="https://stackoverflow.com/a/2947012">here</a>. Link
   * is provided in jsdoc style below but doesn't work as expected in NetBeans
   * despite being of the proper form.
   *
   * @see {@link https://stackoverflow.com/a/2947012|SO Thread}
   * @param {!Array<string>} paramArray
   * @returns {HTMLElement} element
   */
  inaccessible.assembleElement = function (paramArray) {

    // Declarations
    let element, name, attributes, counter, content;

    // Make sure input argument is a well-formatted array
    if (!this.isArray(paramArray)) {
      return this.assembleElement.call(this,
          Array.prototype.slice.call(arguments));
    }

    // Definitions
    name = paramArray[0];
    attributes = paramArray[1];
    element = document.createElement(name);
    counter = 1;

    // attributes != null -> attributes === undefined || attributes === null
    if (typeof attributes === 'object' && attributes != null &&
        !this.isArray(attributes)) {

      for (let attribute in attributes) {
        element.setAttribute(attribute, attributes[attribute]);
      }
      counter = 2;
    }

    for (let i = counter; i < paramArray.length; i++) {

      // If there's inner HTML to hand, recursively call self
      if (this.isArray(paramArray[i])) {
        content = this.assembleElement(paramArray[i]);

      // Otherwise, treat any remaining array elements as text content
      } else {
         content = document.createTextNode(paramArray[i]);
      }

      // Add to outer parent element
      element.appendChild(content);
    }

    return element;
  };

  /**
   * @description This function is used as a helper function to build the DOM
   * framework containing the wrapper <code>div</code>, the <code>canvas</code>
   * and its container, and the <code>input</code> slider and its container. All
   * necessary element properties are compiled in the associated element object
   * and passed along with the element tag type to the DOM element assembly
   * function, <code>inaccessible.assembleElement</code> in a nested structure.
   * This structure is then returned to the calling function, <code>main</code>.
   *
   * @returns {HTMLElement} element
   */
  inaccessible.assembleUserInterface = function () {

    // Declarations
    let containerConfig ,canvasContainerConfig, canvasConfig,
        sliderContainerConfig, sliderConfig;

    // Config object for wrapper
    containerConfig = {
      id: this.Identifiers.CONTAINER_ID,
      style: 'opacity: 0',
    };

    // Config object for canvas container
    canvasContainerConfig = {
      id: this.Identifiers.CANVAS_CONTAINER_ID,
    };

    // Config object for canvas
    canvasConfig = {
      id: this.Identifiers.CANVAS_ID,
      width: this.Utility.CANVAS_WIDTH,
      height: this.Utility.CANVAS_HEIGHT,
    };

    // Config object for slider container
    sliderContainerConfig = {
      id: this.Identifiers.SLIDER_CONTAINER_ID,
    };

    // Config object for slider
    sliderConfig ={
      type: 'range',
      min: this.Utility.SLIDER_MINIMUM_VALUE,
      max: this.Utility.SLIDER_MAXIMUM_VALUE,
      value: this.Utility.SLIDER_INITIAL_VALUE,
      id: this.Identifiers.SLIDER_ID,
    };

    // Return assembled interface
    return this.assembleElement(
      ['div', containerConfig,
        ['div', canvasContainerConfig,
          ['canvas', canvasConfig, this.Text.ERROR_TEXT],
          ['div', sliderContainerConfig,
            ['input', sliderConfig, '']
        ]
      ],
    ]);
  };

  /**
   * @description This assembly function is used to create the required shaders
   * used to render the object in the scene, namely the fragment shader and the
   * vertex shader. It accepts as a parameter argument an array of objects
   * containing the appropriate gl shader type and the associated template
   * string literal housed in the <code>inaccessible.Shaders</code> enum. A new
   * shader program object is created herein, to which the constructed shaders
   * are attached. Once this process is complete, the shader program is returned
   * to the calling function <code>inaccessible.main</code> where it is applied
   * as an object-global and later used in
   * <code>inaccessible.handleSceneAnimation</code>.
   *
   * @param {!Array<object>} paramShaders
   * @returns {object} shaderProgram
   */
  inaccessible.assembleShaderProgram = function (paramShaders) {

    // Declarations
    let that, shaderProgram, shader;

    // Definitions
    that = this;
    shaderProgram = this.gl.createProgram();

    // Iterate through shaders, build each from string content, & add to program
    paramShaders.forEach(function (shaderSetEntry) {

      // New shader
      shader = that.gl.createShader(shaderSetEntry.type);

      // Configure shader
      that.gl.shaderSource(shader, shaderSetEntry.contents);
      that.gl.compileShader(shader);
      that.gl.attachShader(shaderProgram, shader);
    });

    // Apply assembled program
    this.gl.linkProgram(shaderProgram);

    // Return, later define as object-global for future use
    return shaderProgram;
  };

  /**
   * @description This function constructs a new <code>WebGLBuffer</code>
   * instance and configures it based on an inputted array of vertices built and
   * retrieved via <code>inaccessible.assembleShapeVertices</code>. It is called
   * first from within <code>inaccessible.main</code> with the default initial
   * vertices and will be called by <code>inaccessible.handleSliderChange</code>
   * and provided new vertices based on the user's slider-selected number of
   * shape sides. The constructed vertex buffer is then returned from the
   * function and applied as an object-global property for future use.
   *
   * @param {!Array<number>} vertices
   * @returns {WebGLBuffer} vertexBuffer
   */
  inaccessible.assembleVertexBuffer = function (vertices) {

    // Declarations
    let vertexBuffer, verticesFloatArray;

    // Definitions
    vertexBuffer = this.gl.createBuffer();
    verticesFloatArray = new Float32Array(vertices);

    // Configure buffer
    this.gl.bindBuffer(this.gl.ARRAY_BUFFER, vertexBuffer);
    this.gl.bufferData(this.gl.ARRAY_BUFFER, verticesFloatArray,
        this.gl.STATIC_DRAW);

    // Return, later to define as object-global for possible future use
    return vertexBuffer;
  };

  // Handler functions

  /**
   * @description This function is the primary event listener function attached
   * to the slider <code>input</code> DOM element used by the user to change the
   * number of shape sides. Once a change is detected, this function is run to
   * assemble a new set of vertices based on the number of sides, the array of
   * which is then passed to the vertex buffer assembly function,
   * <code>inaccessible.assembleVertexBuffer</code>, prior to repainting and
   * rendering the scene.
   * <br />
   * <br />
   * In case the user changes the side count of the shape multiple times in one
   * program run, the values of preassembled vertex arrays are cached in an
   * object-global object called <code>inaccessible.preassembledVertices</code>
   * to ensure that the same vertices are not recreated every time. Naturally in
   * such a small program as this wherein only a few possible shapes are
   * allowed, this is not likely saving much computational power, but if we had
   * shapes with many sides and many vertices to calculate, caching preassembled
   * vertex sets and simply retrieving them as needed would certainly help save
   * some time.
   *
   * @returns {void}
   */
  inaccessible.handleSliderChange = function () {

    // Declarations
    let value, vertices, preassembledVertices;

    // Definitions
    preassembledVertices = this.assembledVertexSets;
    value = document.getElementById(this.Identifiers.SLIDER_ID).value;

    // If vertices of that polygon type have already been created and cached...
    if (value in preassembledVertices) {

      // Use the cached set instead of rebuilding its contents again
      vertices = preassembledVertices[value];

      // Redefine vertexCount as needed
      this.vertexCount = vertices.length / this.Utility.VERTEX_COMPONENTS;

    // Otherwise, if the user has not viewed this polygon type yet...
    } else {

      // Build the vertex set and...
      vertices = this.assembleShapeVertices(value, this.Utility.SHAPE_RADIUS);

      // Add it to the vertex set cache in case the user comes back to it
      preassembledVertices[value] = vertices;
    }

    // Build new vertex buffer based on new vertices
    this.vertexBuffer = this.assembleVertexBuffer(vertices);

    // Render the animation
    this.handleSceneAnimation();
  };

  /**
   * @description This function is based on the similar function found in the
   * author's Project 3 submission, as well as that found in the Project 3
   * template file entitled <code>modeling-starter.doFrame</code>. It is called
   * with every frame rendering via <code>window.requestAnimationFrame</code>.
   * Based on the current time and the number of degrees the object may be
   * rotated every second, the current angle of the object is updated and the
   * new time reapplied to the old time variable for use in the next frame.
   *
   * @returns {void}
   */
  inaccessible.handleFrame = function () {

    // Declarations
    let currentTime, angleDelta;

    // Definitions
    currentTime = Date.now();
    angleDelta = ((currentTime - this.previousTime) / 1000.0) *
        this.Utility.DEGREES_PER_SECOND;

    // Adjust current object positioning angle
    this.currentAngle = (this.currentAngle + angleDelta) % 360;

    // Apply new time to old time variable
    this.previousTime = currentTime;

    // Render next frame
    this.handleSceneAnimation();
  };

  /**
   * @description This function is called with every frame of the animation as
   * needed, and is used to recalculate the transformations needed to move the
   * object about its midpoint based on its previous position.
   * <br />
   * <br />
   * First, the scene is cleared of content and recolored to the standard gray
   * background, and the <code>radians</code> value is recalculated based on the
   * current angle as redefined in <code>inaccessible.handleFrame</code>. Once
   * this is complete, the current rotation transformation of the frame is
   * reset and recalculated based on the <code>radians</code> value and
   * reapplied via the saved, preassembled shader program to which are attached
   * the fragment and vertex shaders. The primatives are drawn at the end and
   * <code>inaccessible.handleFrame</code> invoked via
   * <code>window.requestAnimationFrame</code>
   *
   * @returns {void}
   */
  inaccessible.handleSceneAnimation = function () {

    // Declarations
    let scale, color, rotate, position, radians;

    // Clear the scene
    this.gl.clear(this.gl.COLOR_BUFFER_BIT);

    // Set background to a gray
    this.gl.clearColor(...this.Colors.GRAY);

    // Translate currently calculated angle (via handleFrame) into radians
    radians = this.currentAngle * Math.PI / 180.0;

    // Redefine the current rotation array elements based on calculated radians
    this.currentRotation[0] = Math.sin(radians);
    this.currentRotation[1] = Math.cos(radians);

    // Apply saved shader program
    this.gl.useProgram(this.shaderProgram);

    // Define transforms (and object color) for this frame
    rotate = this.gl.getUniformLocation(this.shaderProgram, 'u_rotationVector');
    scale = this.gl.getUniformLocation(this.shaderProgram, 'u_scalingVector');
    color = this.gl.getUniformLocation(this.shaderProgram, 'u_colorVector');

    // Apply current transforms (color could probably be removed?)
    this.gl.uniform2fv(rotate, this.currentRotation);
    this.gl.uniform2fv(scale, this.currentScale);
    this.gl.uniform4fv(color, this.Colors.GOLD);

    // Return, assign the location of vertex attribute variable a_position
    position = this.gl.getAttribLocation(this.shaderProgram, 'a_position');

    // Enable individual attributes, allowing other WebGL methods access
    this.gl.enableVertexAttribArray(position);

    // Bind buffer to vertex position in specification of layout
    this.gl.vertexAttribPointer(position, this.Utility.VERTEX_COMPONENTS,
        this.gl.FLOAT, false, 0, 0);

    // Build primatives from vertices -> drawArrays(mode, first, total)
    this.gl.drawArrays(this.gl.TRIANGLE_FAN, 0, this.vertexCount);

    // Update animation prior to repaint
    window.requestAnimationFrame(this.handleFrame.bind(this));
  };

  /**
   * @description This function is the primary function of the program, used to
   * initialize the transformation values and assemble all the required DOM
   * elements and shaders required to display and animate the polygon scene
   * object in the <code>canvas</code>.
   * <br />
   * <br />
   * Apart from being used as a pseudo-constructor to define some important
   * object-globals to be used in other functions as required, this function is
   * used to call the various assembly functions that return everything from
   * assembled DOM elements to shader programs and vertex buffers. As these are
   * returned from their assembly functions, <code>main</code> applies them as
   * either object-globals for use in other handlers or as local variables to be
   * passed to other assembly functions further in the function body. Once all
   * elements required to run and display the scene are assembled and properly
   * initialized, the function calls the <code>inaccessible.fadeIn</code>
   * function and begins the animation.
   *
   * @returns {void}
   */
  inaccessible.main = function () {

    // Declarations
    let that, userInterface, vertices, shaderSet;

    // Initial namespace-global definitions for default transforms
    this.currentScale = [1.0, this.Utility.ASPECT_RATIO];
    this.currentRotation = [0, 1];
    this.currentAngle = 0.0;
    this.previousTime = 0.0;

    // Initialize the object holding preassembled polygon vertex sets
    this.assembledVertexSets = {};

    // Preserve scope context
    that = this;

    // Build DOM interface
    userInterface = this.assembleUserInterface();

    // Populate DOM with assembled interface
    document.body.appendChild(userInterface);

    // Set slider event listener
    document.getElementById(this.Identifiers.SLIDER_ID)
        .addEventListener('change', function () {
      that.handleSliderChange();
    }, false);

    // Grab canvas DOM element
    this.glCanvas = document.getElementById('glcanvas');

    // Define gl from canvas context
    this.gl = this.glCanvas.getContext('webgl');

    // Build vertices (initial shape)
    vertices = this.assembleShapeVertices(this.Utility.SHAPE_NUMBER_SIDES,
        this.Utility.SHAPE_RADIUS);

    // Define shaders
    shaderSet = [
      {
        type: this.gl.VERTEX_SHADER,
        contents: this.Shaders.VERTEX,
      },
      {
        type: this.gl.FRAGMENT_SHADER,
        contents: this.Shaders.FRAGMENT,
      }
    ];

    // Build the vertex shader and fragment shader from array
    this.shaderProgram = this.assembleShaderProgram(shaderSet);

    // Build vertex buffer using initial shape vertices
    this.vertexBuffer = this.assembleVertexBuffer(vertices);

    // Define viewport before animation initialization (needed?)
    this.gl.viewport(0, 0, this.Utility.CANVAS_WIDTH,
        this.Utility.CANVAS_HEIGHT);

    // Once everything is handled, fade in on scene prior to animation
    this.fadeIn(this.Identifiers.CONTAINER_ID);

    // Begin first animation
    this.handleSceneAnimation();
  };

  // Public functions

  /**
   * @description External getter for immutable <code>Utility</code> values
   *
   * @returns {enum} inaccessible.Utility
   */
  accessible.getUtility = function () {
    return inaccessible.Utility;
  };

  /**
   * @description External getter for immutable <code>Identifiers</code> values
   *
   * @returns {enum} inaccessible.Identifiers
   */
  accessible.getIdentifiers = function () {
    return inaccessible.Identifiers;
  };

  /**
   * @description External getter for immutable <code>Text</code> values
   *
   * @returns {enum} inaccessible.Text
   */
  accessible.getText = function () {
    return inaccessible.Text;
  };

  /**
   * @description External getter for immutable <code>Colors</code> values
   *
   * @returns {enum} inaccessible.Colors
   */
  accessible.getColors = function () {
    return inaccessible.Colors;
  };

  /**
   * @description External getter for immutable <code>Shader</code> values
   *
   * @returns {enum} inaccessible.Shaders
   */
  accessible.getShaders = function () {
    return inaccessible.Shaders;
  };

  /**
   * @description The main function of the <code>accessible</code> access scope
   * object namespace, <code>init</code> is called on the completion of the
   * loading of the HTML <code>body</code> element. This method is the only
   * non-getter accessible function of the <code>ProjectThreeModule</code>
   * module, and simply calls <code>inaccessible.main</code> to get the program
   * started.
   *
   * @returns {void}
   */
  accessible.init = function () {
    inaccessible.main();
  };

  // Return external-facing namespace object
  return accessible;
})();