# AetherImgMaker
Console app to generate images from the [Aether](https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition) and [SIV](https://github.com/JaumeRibas/Aether2DImgMaker/wiki/SIV-Cellular-Automaton-Definition) models in different dimensions and initial configurations.

## Download

The runnable jar as well as the source code can be downloaded from [here](https://github.com/JaumeRibas/Aether2DImgMaker/releases/).

## Requirements

To run this app you need to have Java (version 8 or higher) installed.

## How to run

To run the app open a terminal and use `java -jar`. For instance, if the .jar file is in the current directory, use `java -jar AetherImgMaker.jar [options]`, where `[options]` is a space separated list of the options and values to use. 

Option names are prefixed with a hyphen, e.g. `-help`. Most options require a value that must come right after the option name, e.g. `-colormap grayscale`. Use the `-help` option to view a list of available options and their accepted values. 

The `Ctrl`+`C` command can be used to halt the execution.

**Beware that in some cases the app can generate a high number of images in a relatively short time.**

## Examples

* Print the list of available options:

`java -jar AetherImgMaker.jar -help`

* Generate images from the Aether model on a square grid with a single source initial configuration of 1,000,000,000:

`java -jar AetherImgMaker.jar -initial-config single-source_1000000000 -colormap hue`

* Generate images from the Aether model on a square grid with a random initial configuration and using a grayscale colormap:

`java -jar AetherImgMaker.jar -initial-config random-region_270_-100000_100000`

* Generate images from the SIV model on a square grid with a single source of 20,000,000:

`java -jar AetherImgMaker.jar -model siv -initial-config single-source_20000000`

* Generate images from the asymmetric section of the Aether model on a 3D grid with a single source of -1,925 and splitting the even and odd positions into sepate images:

`java -jar AetherImgMaker.jar -grid 3d_infinite -initial-config single-source_-1925 -img-generation-mode split-parity -colormap hue -asymmetric`
