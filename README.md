# AetherImgMaker
Console app to generate images from the [Aether](https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition) and [Sunflower](https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Sunflower-Cellular-Automaton-Definition) models in different dimensions and initial configurations.

## Download

The runnable jar as well as the source code can be downloaded from [here](https://github.com/JaumeRibas/Aether2DImgMaker/releases/).

## Requirements

To run this app you need to have Java installed.

## How to run

To run the app open a terminal and use `java -jar`. For instance, if the .jar file is in the current directory, use `java -jar AetherImgMaker.jar [parameters]`, where `[parameters]` is a space separated list of the parameters and values to use. 

Parameter names are prefixed with a hyphen, e.g., `-help`. Most parameters require a value that must come right after the parameter name, e.g., `-colormap grayscale`. The parameter `-initial-configuration` is the main parameter and it can be used omitting its name. Use the `-help` parameter to view a list of available parameters and their accepted values. 

The `Ctrl`+`C` command can be used to halt the execution.

**Beware that in some cases the app can generate a high number of images in a relatively short time.**

[This other tool](https://github.com/JaumeRibas/image-sequence-viewer) can be used to view the generated images.

## Examples

* Print the list of available parameters:

```
java -jar AetherImgMaker.jar -help
```

* Generate images from the Aether model on a square grid with a single source initial configuration of 1,000,000,000:

```
java -jar AetherImgMaker.jar 1000000000 -colormap hue
```

* Generate images from the Aether model on a square grid with a random initial configuration and using a grayscale colormap (the default one):

```
java -jar AetherImgMaker.jar random-region_270_-100000_100000
```

* Generate images from the Sunflower model on a square grid with a single source of 20,000,000:

```
java -jar AetherImgMaker.jar -model sunflower 20000000
```

* Generate images from the asymmetric section of the Aether model on a 3D grid with a single source of -1,925 and splitting the even and odd positions into sepate images:

```
java -jar AetherImgMaker.jar -grid 3d -1925 -split-by-coordinate-parity -colormap hue -asymmetric
```
