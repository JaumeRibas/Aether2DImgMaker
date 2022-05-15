/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2022 Jaume Ribas

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
	
	@Parameter(names = { "-model" }, description = "The model to generate images from. The currently available models are: 'Aether', 'Spread_Integer_Value' and 'Abelian_sandpile'.")
    public String model = "Aether";
	
	@Parameter(names = { "-grid" }, validateWith = GridValidator.class, converter = GridConverter.class, description = "The type of grid to use. Currently, the only available type is: '{dimension}d_{side}', where {dimension} is a positive integer and {side} is either 'infinite' (for an infinite grid) or a positive integer (for a finite grid shaped as a hypercube of this side) (e.g. '2d_infinite').")
    public GridOptionValue grid = null;

	@Parameter(names = { "-initial-config" }, validateWith = InitialConfigValidator.class, converter = InitialConfigConverter.class, description = "The initial configuration. The currently available configurations are: 'single-source_{value}', for a single source initial configuration (e.g. 'single-source_-1000'), and 'random-region_{side}_{min}_{max}', for an initial configuration consisting of an hypercubic region of side {side} filled with random values ranging form {min} to {max} (e.g. 'random-region_250_-45_60'). Note that outside this region the value will be zero.")
    public InitialConfigOptionValue initialConfiguration = null;

	@Parameter(names = { "-asymmetric" }, description = "Generate images only of an asymmetric section of a symmetric model.")
	public boolean asymmetric = false;
	
	//TODO coordinates option. (all, [0, max], 10, c1+5)
	
	@Parameter(names = { "-v" }, validateWith = CoordinateValidator.class, converter = CoordianteConverter.class, description = "The app generates images from this v-coordinate. It can be either an integer (e.g. '0'), an integer range (e.g. '[0,100]'), another coordinate (e.g. 'x') or another coordinate plus/minus an integer (e.g. 'x+2'). Only applies to models with dimension greater than four.")
	public CoordinateOptionValue v = null;
	
	@Parameter(names = { "-w" }, validateWith = CoordinateValidator.class, converter = CoordianteConverter.class, description = "The app generates images from this w-coordinate. It can be either an integer (e.g. '0'), an integer range (e.g. '[0,100]'), another coordinate (e.g. 'x') or another coordinate plus/minus an integer (e.g. 'x+2'). Only applies to models with dimension greater than three.")
	public CoordinateOptionValue w = null;
	
	@Parameter(names = { "-x" }, validateWith = CoordinateValidator.class, converter = CoordianteConverter.class, description = "The app generates images from this x-coordinate. It can be either an integer (e.g. '0'), an integer range (e.g. '[0,100]'), another coordinate (e.g. 'y') or another coordinate plus/minus an integer (e.g. 'y+2'). Only applies to models with dimension greater than two.")
	public CoordinateOptionValue x = null;
	
	@Parameter(names = { "-y" }, validateWith = CoordinateValidator.class, converter = CoordianteConverter.class, description = "The app generates images from this y-coordinate. It can be either an integer (e.g. '0'), an integer range (e.g. '[0,100]'), another coordinate (e.g. 'x') or another coordinate plus/minus an integer (e.g. 'x+2'). Only applies to models with dimension greater than two.")
	public CoordinateOptionValue y = null;
	
	@Parameter(names = { "-z" }, validateWith = CoordinateValidator.class, converter = CoordianteConverter.class, description = "The app generates images from this z-coordinate. It can be either an integer (e.g. '0'), an integer range (e.g. '[0,100]'), another coordinate (e.g. 'x') or another coordinate plus/minus an integer (e.g. 'x+2'). Only applies to models with dimension greater than two.")
	public CoordinateOptionValue z = null;
	
	@Parameter(names = { "-colormap" }, description = "The colormap to use for the images. The currently available colormaps are: 'Grayscale' and 'Hue'.")
    public String colormap = "Grayscale";
	
	@Parameter(names = { "-min-img-size" }, validateWith = ImgSizeValidator.class, converter = ImgSizeConverter.class, description = "The minimum size of the generated images in pixels. With the format '{width}x{height}' (e.g. '1920x1080'). The images can be bigger if the model is too big to fit using 1:1 pixel to position scale. The aspect ratio is always mantained, if the scaled data is smaller than the image, it is aligned to the bottom left corner. The background is colored in black.")
    public ImgSizeOptionValue minimumImageSize = new ImgSizeOptionValue(ImgMakerConstants.HD_HEIGHT/4, ImgMakerConstants.HD_HEIGHT/4);
	
	@Parameter(names = { "-path" }, description = "The path of the parent folder where the images are created. A subfolder structure is created at this location for organizational purposes.")
    public String path = "./";

	@Parameter(names = { "-restore" }, description = "The path of the backup to restore. Mandatory when no initial configuration is passed and the selected -model requires one.")
    public String backupToRestorePath = null;
	
	@Parameter(names = { "-first-step" }, validateWith = PositiveIntegerValidator.class, description = "The app skips ahead to this step without generating images.")
	public long initialStep = 0;

	@Parameter(names = { "-backup-every" }, validateWith = PositiveIntegerValidator.class, description = "The preferred number of millisencods between automatic backups.")
	public Long millisBetweenBackups;
	
	@Parameter(names = { "-scan1-start" }, description = "The scan1 will start at this coordinate. Only applies to models with dimension greater than two.")
    public Integer xScanInitialIndex = null;
    
	@Parameter(names = { "-scan2-start" }, description = "The scan2 will start at this coordinate. Only applies to models with dimension greater than two.")
    public Integer yScanInitialIndex = null;
    
	@Parameter(names = { "-scan3-start" }, description = "The scan3 will start at this coordinate. Only applies to models with dimension greater than two.")
    public Integer zScanInitialIndex = null;
    
    @Parameter(names = { "-img-generation-mode" }, description = "Option to affect image generation. The currently available modes are: '" + NORMAL + "', '" + SPLIT_PARITY + "', '" + EVEN_ONLY + "' and '" + ODD_ONLY + "'.")
    public String imgGenerationMode = NORMAL;
    
    @Parameter(names = { "-memory-safe" }, description = "Use temporary files, within the -path folder, to store the grid so as to avoid running out of memory. In exchange, processing speed and storage space are sacrificed.")
	public boolean memorySafe = false;
    
    @Parameter(names = "-version", description = "Print the version of the app.")
	public boolean outputVersion;
	
	@Parameter(names = "-help", help = true, description = "Print the list of options.")
	public boolean help;
	
}
