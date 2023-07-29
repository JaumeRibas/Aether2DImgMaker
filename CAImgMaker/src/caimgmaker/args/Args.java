/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2023 Jaume Ribas

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package caimgmaker.args;

import com.beust.jcommander.Parameter;

import caimgmaker.ImgMakerConstants;

public class Args {
	
	public static final String PROGRAM_INVOCATION = "java -jar AetherImgMaker.jar";
	
	//TODO Add infinity
	//I repeat the parameter as a hack so that it is included in the parameters list
	@Parameter(names = "-initial-configuration", order=0, validateWith = InitialConfigValidator.class, converter = InitialConfigConverter.class, description = "(This is the main parameter so its name can be omitted)\nThe initial configuration for the model.\nThe currently available configurations are:\n\n  single-source_{value} or just {value}: for a single source initial configuration of {value} (e.g., 'single-source_-1000' or '-1000')\n\n  random-region_{side}_{min}_{max}: for an initial configuration consisting of an hypercubic region of side {side} filled with random values ranging form {min} to {max} (e.g. 'random-region_250_-45_60').\nNote that outside this region the value will be zero.\n\nExample: " + PROGRAM_INVOCATION + " random-region_250_-45_60")
    public InitialConfigParameterValue initialConfiguration = null;
	@Parameter(validateWith = InitialConfigValidator.class, converter = InitialConfigConverter.class)
    public InitialConfigParameterValue initialConfiguration2 = null;

	@Parameter(names = { "-asymmetric", "-asymm", "-asym" }, description = "Generate images only of an asymmetric section of a symmetric model.\n\nExample: " + PROGRAM_INVOCATION + " 1000 -asymmetric")
	public boolean asymmetric = false;
	
	@Parameter(names = "-backup-every", validateWith = GreaterThanZeroIntegerValidator.class, description = "The preferred number of millisencods between automatic backups. No backups are made by default.")
	public Long millisBetweenBackups;
	
	@Parameter(names = "-colormap", description = "The colormap to use for the images.\nThe currently available colormaps are: Grayscale and Hue.")
    public String colormap = "Grayscale";
	
	@Parameter(names = { "-coordinate-filters", "-coord-filters" }, validateWith = CoordinateFiltersValidatorAndConverter.class, converter = CoordinateFiltersValidatorAndConverter.class, description = "A semicolon separated list of coordinate filters to restrict the image generation to a subregion of the grid. The coordinates are denoted by an 'x' followed by a numeric index between one and the grid's dimension, both included, e.g., x1, x2 and x3.\nThe currently available filters are:\n\n  {coordinate}{=|>|<}{integer}: to target the region where a coordinate is either equal to or greater or less than a certain value, e.g. x2=-5, x1>0, x3<-2\n\n  {coordinate}={+|-}{coordinate}{+|-}{integer}: to target the region where a coordinate is equal to another coordinate, or its opposite, plus/minus a value (optional), e.g. x1=x3, x2=-x4+6\n\nNote that the whole set of filters might need to be surrounded with double quotes in case a '<' or '>' character is used. This is to prevent the shell from interpreting these characters.\n\nExample: " + PROGRAM_INVOCATION + " -100000 -grid 5d -coordinate-filters \"x1=0;x2=1;x3=x4;x4>-1;x4<1921;x5>-1;x5<1081\"")
    public CoordinateFilters coordinateFilters = null;
	
	@Parameter(names = "-first-step", validateWith = NonNegativeIntegerValidator.class, description = "The app skips ahead to this step (or the model's last step plus one, in case the last step is smaller than the first step provided) without generating images.")
	public long firstStep = 0;
	
	@Parameter(names = "-grid", validateWith = GridValidator.class, converter = GridConverter.class, description = "The type of grid to use.\nCurrently, the only available types are:\n\n  {dimension}d: an infinite flat grid of dimension {dimension} (e.g., 3d)\n\n  {dimension}d_{side}: a finite flat grid shaped as a hypercube of dimension {dimension} and side {side} (e.g., 2d_101).")
    public GridParameterValue grid = null;
	
	@Parameter(names = "-help", help = true, description = "Print the list of parameters.")
	public boolean help;
	
	@Parameter(names = { "-image-generation-mode", "-img-generation-mode", "-image-gen-mode", "-img-gen-mode" }, validateWith = ImageGenerationModeValidator.class, converter = ImageGenerationModeConverter.class, description = "Parameter to affect image generation.\nThe currently available modes are:\n\n  normal\n\n  split-coordinate-parity: split the even and odd coordinates of the grid into sepate images\n\n  even-coordinates-only: generate images only of the even coordinates of the grid\n\n  odd-coordinates-only: generate images only of the odd coordinates of the grid\n\n  toppling-alternation-compliance (only available for the Aether model with single source initial configuration): color positions based on whether they keep the original toppling alternation phase between von Neumann neighbors")
    public ImageGenerationMode imgGenerationMode = ImageGenerationMode.NORMAL;
	
	@Parameter(names = { "-image-name", "-img-name" }, validateWith = ImgNameValidator.class, description = "The name of the generated images.\nThe step index will be appended to this string.\nBy default, the model name followed by an underscore is used.")
    public String imgName = null;
    
	@Parameter(names = "-memory-safe", description = "Use temporary files, within the -path folder, to store the grid so as to avoid running out of memory. In exchange, processing speed and storage space are sacrificed.")
	public boolean memorySafe = false;
    
	@Parameter(names = { "-minimum-image-size", "-min-image-size", "-minimum-img-size", "-min-img-size" }, validateWith = ImgSizeValidator.class, converter = ImgSizeConverter.class, description = "The minimum size of the generated images in pixels, with the format {width}x{height} (e.g., 1920x1080).\nThe images can be bigger if the model is too big to fit using 1:1 pixel to position scale. The aspect ratio is always mantained. If the scaled grid region is smaller than the image, it is aligned to the bottom left corner and the background colored in black.")
    public ImgSizeParameterValue minimumImageSize = new ImgSizeParameterValue(ImgMakerConstants.HD_HEIGHT/4, ImgMakerConstants.HD_HEIGHT/4);
	
	@Parameter(names = "-model", description = "The model to generate images from.\nThe currently available models are:\n\n  Aether: https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition\n\n  Spread_Integer_Value, SIV: https://github.com/JaumeRibas/Aether2DImgMaker/wiki/SIV-Cellular-Automaton-Definition\n\n  Abelian_sandpile: https://en.wikipedia.org/wiki/Abelian_sandpile_model")
    public String model = "Aether";
	
	@Parameter(names = { "-no-folders", "-no-folder" }, description = "Do not create a subfolder structure at the -path apart from numbered folders.")
	public boolean noFolders;
	
	@Parameter(names = "-path", validateWith = PathValidator.class, description = "The path of the parent folder where the images are created. By default, a subfolder structure is created at this location for organizational purposes. This can be prevented using the -no-folders parameter.")
    public String path = "./";

	@Parameter(names = "-restore", description = "The path of the backup to restore. Mandatory when no initial configuration is passed and the selected -model requires one.")
    public String backupToRestorePath = null;
	
	@Parameter(names = "-scan1-start", description = "The scan1 will start at this coordinate. Only applies to model sections with dimension three (see -grid and -coordinate-filters parameters).")
    public Integer xScanInitialIndex = null;
    
	@Parameter(names = "-scan2-start", description = "The scan2 will start at this coordinate. Only applies to model sections with dimension three (see -grid and -coordinate-filters parameters).")
    public Integer yScanInitialIndex = null;
    
	@Parameter(names = "-scan3-start", description = "The scan3 will start at this coordinate. Only applies to model sections with dimension three (see -grid and -coordinate-filters parameters).")
    public Integer zScanInitialIndex = null;
    
	@Parameter(names = "-step-leap", validateWith = GreaterThanZeroIntegerValidator.class, description = "The app will generate images at the steps multiple of this number. If this number is greater than one, the app will also generate an image at the last step plus one of the model.")
	public int steapLeap = 1;

	@Parameter(names = { "-version", "-v" }, description = "Print the version of the app.")
	public boolean outputVersion;	
	
}
