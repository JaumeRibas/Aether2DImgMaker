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

	public static final String NORMAL = "normal";
	public static final String SPLIT_PARITY = "split-parity";
	public static final String EVEN_ONLY = "even-only";
	public static final String ODD_ONLY = "odd-only";
	//TODO missing modes
	
	@Parameter(names = "-model", description = "The model to generate images from. The currently available models are: 'Aether', 'Spread_Integer_Value' and 'Abelian_sandpile'.")
    public String model = "Aether";
	
	@Parameter(names = "-grid", validateWith = GridValidator.class, converter = GridConverter.class, description = "The type of grid to use. Currently, the only available types are: '{dimension}d', a infinite flat grid of dimension {dimension} (e.g. '3d'); and'{dimension}d_{side}', a finite flat grid shaped as a hypercube of dimension {dimension} and side {side} (e.g. '2d_101').")
    public GridOptionValue grid = null;

	//TODO make single source the default
	@Parameter(names = { "-initial-configuration", "-initial-config", "-init-configuration", "-init-config" }, validateWith = InitialConfigValidator.class, converter = InitialConfigConverter.class, description = "The initial configuration. The currently available configurations are: 'single-source_{value}', for a single source initial configuration (e.g. 'single-source_-1000'), and 'random-region_{side}_{min}_{max}', for an initial configuration consisting of an hypercubic region of side {side} filled with random values ranging form {min} to {max} (e.g. 'random-region_250_-45_60'). Note that outside this region the value will be zero.")
    public InitialConfigOptionValue initialConfiguration = null;

	@Parameter(names = { "-asymmetric", "-asymm", "-asym" }, description = "Generate images only of an asymmetric section of a symmetric model.")
	public boolean asymmetric = false;
	
	@Parameter(names = { "-coordinate-filters", "-coord-filters" }, validateWith = CoordinateFiltersValidatorAndConverter.class, converter = CoordinateFiltersValidatorAndConverter.class, description = "A semicolon separated list of coordinate filters to restrict the image generation to a subregion of the grid. The coordinates are denoted by an 'x' followed by a numeric index between one and the grid's dimension, both included, e.g. x1, x2 and x3. The currently available filters are: '{coordinate}{=|>|<}{integer}', to target the region where a coordinate is either equal to or greater or less than a certain value, e.g. 'x2=-5', 'x1>0', 'x3<-2'; and '{coordinate}={+|-}{coordinate}{+|-}{integer}' to target the region where a coordinate is equal to another coordinate, or its opposite, plus/minus a value (optional), e.g. 'x1=x3', 'x2=-x4+6'. Note that the whole set of filters might need to be surrounded with double quotes in case a '<' or '>' character is used, e.g. -coordinate-filters \"x1<5;x2>3;x3=0\". This is to prevent the shell from interpreting these characters.")
    public CoordinateFilters coordinateFilters = null;
	
	@Parameter(names = "-colormap", description = "The colormap to use for the images. The currently available colormaps are: 'Grayscale' and 'Hue'.")
    public String colormap = "Grayscale";
	
	@Parameter(names = { "-minimum-image-size", "-min-image-size", "-minimum-img-size", "-min-img-size" }, validateWith = ImgSizeValidator.class, converter = ImgSizeConverter.class, description = "The minimum size of the generated images in pixels. With the format '{width}x{height}' (e.g. '1920x1080'). The images can be bigger if the model is too big to fit using 1:1 pixel to position scale. The aspect ratio is always mantained, if the scaled data is smaller than the image, it is aligned to the bottom left corner. The background is colored in black.")
    public ImgSizeOptionValue minimumImageSize = new ImgSizeOptionValue(ImgMakerConstants.HD_HEIGHT/4, ImgMakerConstants.HD_HEIGHT/4);
	
	@Parameter(names = "-path", description = "The path of the parent folder where the images are created. By default, a subfolder structure is created at this location for organizational purposes. This can be prevented using the -no-folders option.")
    public String path = "./";

	@Parameter(names = "-restore", description = "The path of the backup to restore. Mandatory when no initial configuration is passed and the selected -model requires one.")
    public String backupToRestorePath = null;
	
	@Parameter(names = "-first-step", validateWith = NonNegativeIntegerValidator.class, description = "The app skips ahead to this step (or the model's last step plus one, in case the last step is smaller than the first step provided) without generating images.")
	public long firstStep = 0;
	
	@Parameter(names = "-step-leap", validateWith = GreaterThanZeroIntegerValidator.class, description = "The app will generate images at the steps multiple of this number. If this number is greater than one, the app will also generate an image at the last step plus one of the model.")
	public int steapLeap = 1;

	@Parameter(names = "-backup-every", validateWith = GreaterThanZeroIntegerValidator.class, description = "The preferred number of millisencods between automatic backups.")
	public Long millisBetweenBackups;
	
	@Parameter(names = "-scan1-start", description = "The scan1 will start at this coordinate. Only applies to model sections with dimension three (see -grid and -coordinate-filters options).")
    public Integer xScanInitialIndex = null;
    
	@Parameter(names = "-scan2-start", description = "The scan2 will start at this coordinate. Only applies to model sections with dimension three (see -grid and -coordinate-filters options).")
    public Integer yScanInitialIndex = null;
    
	@Parameter(names = "-scan3-start", description = "The scan3 will start at this coordinate. Only applies to model sections with dimension three (see -grid and -coordinate-filters options).")
    public Integer zScanInitialIndex = null;
    
    @Parameter(names = { "-image-generation-mode", "-img-generation-mode", "-image-gen-mode", "-img-gen-mode" }, description = "Option to affect image generation. The currently available modes are: '" + NORMAL + "', '" + SPLIT_PARITY + "', '" + EVEN_ONLY + "' and '" + ODD_ONLY + "'.")
    public String imgGenerationMode = NORMAL;
    
    @Parameter(names = "-memory-safe", description = "Use temporary files, within the -path folder, to store the grid so as to avoid running out of memory. In exchange, processing speed and storage space are sacrificed.")
	public boolean memorySafe = false;
    
    @Parameter(names = { "-version", "-v" }, description = "Print the version of the app.")
	public boolean outputVersion;
	
	@Parameter(names = "-no-folders", description = "Do not create a subfolder structure at the provided -path.")
	public boolean noFolders;
	
	@Parameter(names = "-help", help = true, description = "Print the list of options.")
	public boolean help;
	
}
