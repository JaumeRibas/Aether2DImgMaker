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
import com.beust.jcommander.Parameters;

import caimgmaker.ImgMakerConstants;

@Parameters(resourceBundle = "MessagesBundle")
public class Args {
	
	public static final String PROGRAM_INVOCATION = "java -jar AetherImgMaker.jar";

	public static final String INITIAL_CONFIGURATION = "-initial-configuration";
	//TODO Add infinity
	//I repeat the parameter as a hack so that it is included in the parameters list
	@Parameter(names = INITIAL_CONFIGURATION, order=0, validateWith = InitialConfigValidator.class, converter = InitialConfigConverter.class, descriptionKey = "initial-config-description")
    public InitialConfigParameterValue initialConfiguration = null;
	@Parameter(validateWith = InitialConfigValidator.class, converter = InitialConfigConverter.class)
    public InitialConfigParameterValue initialConfiguration2 = null;

	public static final String ASYMMETRIC = "-asymmetric";
	@Parameter(names = { ASYMMETRIC, "-asymm", "-asym" }, descriptionKey = "asymmetric-description")
	public boolean asymmetric = false;
	
	public static final String BACKUP_EVERY = "-backup-every";
	@Parameter(names = BACKUP_EVERY, validateWith = GreaterThanZeroIntegerValidator.class, descriptionKey = "backup-every-description")
	public Long millisBetweenBackups;
	
	public static final String COLORMAP = "-colormap";
	@Parameter(names = COLORMAP, descriptionKey = "colormap-description")
    public String colormap = "Grayscale";
	
	public static final String COORDIANTE_FILTERS = "-coordinate-filters";
	@Parameter(names = { COORDIANTE_FILTERS, "-coord-filters" }, validateWith = CoordinateFiltersValidatorAndConverter.class, converter = CoordinateFiltersValidatorAndConverter.class, descriptionKey = "coordinate-filters-description")
    public CoordinateFilters coordinateFilters = null;
	
	public static final String FIRST_STEP = "-first-step";
	@Parameter(names = FIRST_STEP, validateWith = NonNegativeIntegerValidator.class, descriptionKey = "first-step-description")
	public long firstStep = 0;
	
	public static final String GRID = "-grid";
	@Parameter(names = GRID, validateWith = GridValidator.class, converter = GridConverter.class, descriptionKey = "grid-description")
    public GridParameterValue grid = null;
	
	public static final String HELP = "-help";
	@Parameter(names = HELP, help = true, descriptionKey = "help-description")
	public boolean help;
	
	public static final String IMAGE_GENERATION_MODE = "-image-generation-mode";
	@Parameter(names = { IMAGE_GENERATION_MODE, "-img-generation-mode", "-image-gen-mode", "-img-gen-mode" }, validateWith = ImageGenerationModeValidator.class, converter = ImageGenerationModeConverter.class, descriptionKey = "image-generation-mode-description")
    public ImageGenerationMode imgGenerationMode = ImageGenerationMode.NORMAL;
	
	public static final String IMAGE_NAME = "-image-name";
	@Parameter(names = { IMAGE_NAME, "-img-name" }, validateWith = ImgNameValidator.class, descriptionKey = "image-name-description")
    public String imgName = null;
    
	public static final String MEMORY_SAFE = "-memory-safe";
	@Parameter(names = MEMORY_SAFE, descriptionKey = "memory-safe-description")
	public boolean memorySafe = false;
    
	public static final String MINIMUM_IMAGE_SIZE = "-minimum-image-size";
	@Parameter(names = { MINIMUM_IMAGE_SIZE, "-min-image-size", "-minimum-img-size", "-min-img-size" }, validateWith = ImgSizeValidator.class, converter = ImgSizeConverter.class, descriptionKey = "minimum-image-size-description")
    public ImgSizeParameterValue minimumImageSize = new ImgSizeParameterValue(ImgMakerConstants.HD_HEIGHT/4, ImgMakerConstants.HD_HEIGHT/4);
	
	public static final String MODEL = "-model";
	@Parameter(names = MODEL, descriptionKey = "model-description")
    public String model = "Aether";
	
	public static final String NO_FOLDERS = "-no-folders";
	@Parameter(names = { NO_FOLDERS, "-no-folder" }, descriptionKey = "no-folders-description")
	public boolean noFolders;
	
	public static final String PATH = "-path";
	@Parameter(names = PATH, validateWith = PathValidator.class, descriptionKey = "path-description")
    public String path = "./";

	public static final String RESTORE = "-restore";
	@Parameter(names = RESTORE, descriptionKey = "restore-description")
    public String backupToRestorePath = null;
	
	public static final String SCAN1_START = "-scan1-start";
	@Parameter(names = SCAN1_START, descriptionKey = "scan1-start-description")
    public Integer xScanInitialIndex = null;
    
	public static final String SCAN2_START = "-scan2-start";
	@Parameter(names = SCAN2_START, descriptionKey = "scan2-start-description")
    public Integer yScanInitialIndex = null;
    
	public static final String SCAN3_START = "-scan3-start";
	@Parameter(names = SCAN3_START, descriptionKey = "scan3-start-description")
    public Integer zScanInitialIndex = null;
    
	public static final String STEP_LEAP = "-step-leap";
	@Parameter(names = STEP_LEAP, validateWith = GreaterThanZeroIntegerValidator.class, descriptionKey = "step-leap-description")
	public int steapLeap = 1;

	public static final String VERSION = "-version";
	@Parameter(names = { VERSION, "-v" }, descriptionKey = "version-description")
	public boolean outputVersion;	
	
}
