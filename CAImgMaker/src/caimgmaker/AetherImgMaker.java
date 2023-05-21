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
package caimgmaker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import org.apache.commons.io.FileUtils;

import com.beust.jcommander.JCommander;
import caimgmaker.args.Args;
import caimgmaker.args.CoordinateFilters;
import caimgmaker.args.GridOptionValue;
import caimgmaker.args.ImageGenerationMode;
import caimgmaker.args.InitialConfigOptionValue.InitialConfigType;
import caimgmaker.colormap.ColorMapper;
import caimgmaker.colormap.GrayscaleMapper;
import caimgmaker.colormap.HueMapper;
import cellularautomata.PartialCoordinates;
import cellularautomata.automata.IntAbelianSandpileSingleSource2D;
import cellularautomata.automata.aether.LongAether1D;
import cellularautomata.automata.aether.LongAether2D;
import cellularautomata.automata.aether.LongAether2DTopplingAlternationViolations;
import cellularautomata.automata.aether.IntAether2DRandomConfiguration;
import cellularautomata.automata.aether.IntAether2DTopplingAlternationViolations;
import cellularautomata.automata.aether.LongAether3D;
import cellularautomata.automata.aether.LongAether3DCubicGrid;
import cellularautomata.automata.aether.LongAether4D;
import cellularautomata.automata.aether.LongAether5D;
import cellularautomata.automata.aether.BigIntAether2D;
import cellularautomata.automata.aether.BigIntAether2DTopplingAlternationViolations;
import cellularautomata.automata.aether.BigIntAether3D;
import cellularautomata.automata.aether.BigIntAether3DCubicGrid;
import cellularautomata.automata.aether.BigIntAether4D;
import cellularautomata.automata.aether.FileBackedAether1D;
import cellularautomata.automata.aether.FileBackedAether2D;
import cellularautomata.automata.aether.FileBackedAether3D;
import cellularautomata.automata.aether.FileBackedAether4D;
import cellularautomata.automata.aether.FileBackedAether5D;
import cellularautomata.automata.aether.IntAether2D;
import cellularautomata.automata.aether.IntAether3D;
import cellularautomata.automata.aether.IntAether3DRandomConfiguration;
import cellularautomata.automata.aether.IntAether4D;
import cellularautomata.automata.aether.IntAether5D;
import cellularautomata.automata.nearaether.SimpleBigIntNearAether3_3D;
import cellularautomata.automata.nearaether.IntNearAether1_3D;
import cellularautomata.automata.nearaether.IntNearAether2_3D;
import cellularautomata.automata.siv.IntSpreadIntegerValue2D;
import cellularautomata.automata.siv.IntSpreadIntegerValue;
import cellularautomata.automata.siv.LongSpreadIntegerValue1D;
import cellularautomata.automata.siv.LongSpreadIntegerValue2D;
import cellularautomata.automata.siv.LongSpreadIntegerValue3D;
import cellularautomata.automata.siv.LongSpreadIntegerValue4D;
import cellularautomata.model.IntModel;
import cellularautomata.model.Model;
import cellularautomata.model.SymmetricModel;
import cellularautomata.model2d.BooleanModel2D;
import cellularautomata.model2d.IntModel2D;
import cellularautomata.model2d.IntModelAs2D;
import cellularautomata.model2d.LongModel2D;
import cellularautomata.model2d.NumericModel2D;
import cellularautomata.model3d.IntModel3D;
import cellularautomata.model3d.IntModelAs3D;
import cellularautomata.model3d.LongModel3D;
import cellularautomata.model3d.Model3D;
import cellularautomata.model3d.ModelAs3D;
import cellularautomata.model3d.NumericModel3D;
import cellularautomata.numbers.BigInt;

public class AetherImgMaker {
	
	private static final String USE_HELP_MESSAGE = "Use -help to view the list of available options and their accepted values.";
	private static final String GRID_NOT_SUPPORTED_MESSAGE_FORMAT = "The %s model is currently not supported with this type of grid.%n";
	private static final String GRID_TYPE_NEEDED_IN_ORDER_TO_RESTORE_MESSAGE_FORMAT = "You need to specify the grid type of the backup you are trying to restore.%n";
	private static final String INITIAL_CONFIG_NOT_SUPPORTED_MESSAGE_FORMAT = "The %s model is currently not supported with the selected initial configuration.%n";
	private static final String SINGLE_SOURCE_OUT_OF_RANGE_MESSAGE_FORMAT = "The single source value is out of the currently supported range for this model: [%d, %d].%n";
	private static final String MIN_MAX_OUT_OF_RANGE_MESSAGE_FORMAT = "The min/max values are out of the currently supported range for this model: [%d, %d].%n";
	private static final String INITIAL_CONFIG_NEEDED_MESSAGE_FORMAT = "The %s model needs and initial configuration.%n";
	private static final String UNSUPPORTED_DIMENSION_MESSAGE_FORMAT = "Currently it is only supported to generate images from a model section with dimension two or three (found %d). Use the -coordinate-filters option or a -grid with two or three dimensions.%n";
	private static final String UNKNOWN_IMG_GEN_MODE_MESSAGE = "Unrecognized image generation mode.";
	private static final String UNSUPPORTED_MODEL_SECTION_MESSAGE_FORMAT = "It is currently not supported to generate images form a model section of type %s.%n";
	private static final String MEMORY_SAFE_NOT_SUPPORTED_FOR_THIS_MODEL_MESSAGE_FORMAT = "The %s model is currently not supported with the -memory-safe option.%n";
	private static final String MEMORY_SAFE_NOT_SUPPORTED_FOR_THIS_INITIAL_CONFIG_MESSAGE_FORMAT = "The %s model is currently not supported with the -memory-safe option and the selected initial configuration.%n";
	private static final String IMG_GEN_MODE_NOT_SUPPORTED_MESSAGE_FORMAT = "The selected image generation mode is currently not supported for the %s model.%n";
	private static final String MEMORY_SAFE_NOT_SUPPORTED_FOR_THIS_CONFIG_MESSAGE_FORMAT = "The -memory-safe option is currently not supported together with the given configuration.%n";

	private static ImageGenerationMode imgGenerationMode;
	
	public static void main(String[] rawArgs) throws Exception {
//		String debugArgs = "92233720368547758079999 -path D:/data/test";//debug
//		debugArgs = "-help";//debug
//		rawArgs = debugArgs.split(" ");//debug
		try {
			Args args = new Args();
			JCommander jcommander = JCommander.newBuilder()
					.programName("java -jar AetherImgMaker.jar")
					.addObject(args)
					.build();
			jcommander.parse(rawArgs);
			imgGenerationMode = ImageGenerationMode.getByName(args.imgGenerationMode);
			if (args.help) {
				jcommander.usage();
				return;
			}
			if (!mergeInitialConfigOptions(args)) {
				System.out.println(USE_HELP_MESSAGE);
				return;
			}
			if (args.outputVersion) {
				System.out.println("0.7.0");
				return;
			}
			Model model = getModel(args);
			if (model == null) {
				System.out.println(USE_HELP_MESSAGE);
				return;
			}
			String path = args.path;
			if (!args.noFolders) {
				path += "/" + model.getSubfolderPath();
			}
			String backupsPath = path + "/backups";
			evolveModelToFirstStep(model, args, backupsPath);
			Model modelSection = getModelSection(model, args);
			if (modelSection == null) {
				System.out.println(USE_HELP_MESSAGE);
				return;
			}
			if (args.backupToRestorePath == null)
				FileUtils.writeStringToFile(
						new File(path + "/options.txt"), 
						new Timestamp(System.currentTimeMillis()).toString() + "\t" + String.join(" ", rawArgs) + System.lineSeparator(), 
						Charset.forName("UTF8"), 
						true);
			boolean success = generateImages(modelSection, args, backupsPath);
			if(!success) {
				System.out.println(USE_HELP_MESSAGE);
			}
		} catch (Exception ex) {
			String message = ex.getMessage();
			if (message == null) {
				System.out.println("Unexpected error.");
				ex.printStackTrace();
			} else if (message.startsWith("For input string")) {
				System.out.println("One or more unrecognized options found.");
			} else {
				System.out.println(message);
			}
			System.out.println(USE_HELP_MESSAGE);
			//throw ex;//debug
		}
	}
	
	private static boolean mergeInitialConfigOptions(Args args) {
		boolean succeeded = true;
		if (args.initialConfiguration2 != null) {
			if (args.initialConfiguration != null) {
				System.out.println("Can only specify one initial configuration.");
				succeeded = false;
			} else {
				args.initialConfiguration = args.initialConfiguration2;
			}
		}
		return succeeded;
	}
	
	private static boolean generateImages(Model model, Args args, String backupsPath) throws Exception {
		ColorMapper colorMapper = getColorMapper(args);
		if (colorMapper == null)
			return false;
		String imagesPath = args.path;
		if (!args.noFolders) {
			imagesPath += "/" + model.getSubfolderPath();
			if (args.steapLeap > 1) {
				imagesPath += "/step-leap=" + args.steapLeap;
			}
			imagesPath += "/img/" + colorMapper.getColormapName();
		}
		ImgMaker imgMaker = null;
		if (args.millisBetweenBackups == null) {
			imgMaker = new ImgMaker();
		} else {
			imgMaker = new ImgMaker(args.millisBetweenBackups);
		}
		boolean error = false;
		int dimension = model.getGridDimension();
		switch (dimension) {
			case 2:
				if (model instanceof BooleanModel2D) {
					BooleanModel2D castedModel = (BooleanModel2D)model;
					switch (imgGenerationMode) {
						case TOPPLING_ALTERNATION_VIOLATIONS:	
						case NORMAL: imgMaker.createImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap);
							break;
						case SPLIT_BY_COORDINATE_PARITY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, false, false);
							break;
						case EVEN_COORDINATES_ONLY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, false, true);
							break;
						case ODD_COORDINATES_ONLY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, true, false);
							break;
						default: 
							System.out.println(UNKNOWN_IMG_GEN_MODE_MESSAGE);
							error = true;
					}				
				} else if (model instanceof IntModel2D) {
					IntModel2D castedModel = (IntModel2D)model;
					switch (imgGenerationMode) {
						case TOPPLING_ALTERNATION_VIOLATIONS:	
						case NORMAL: imgMaker.createImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap);
							break;
						case SPLIT_BY_COORDINATE_PARITY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, false, false);
							break;
						case EVEN_COORDINATES_ONLY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, false, true);
							break;
						case ODD_COORDINATES_ONLY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, true, false);
							break;
						default: 
							System.out.println(UNKNOWN_IMG_GEN_MODE_MESSAGE);
							error = true;
					}				
				} else if (model instanceof LongModel2D) {
					LongModel2D castedModel = (LongModel2D)model;
					switch (imgGenerationMode) {
						case TOPPLING_ALTERNATION_VIOLATIONS:	
						case NORMAL: imgMaker.createImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap);
							break;
						case SPLIT_BY_COORDINATE_PARITY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, false, false);
							break;
						case EVEN_COORDINATES_ONLY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, false, true);
							break;
						case ODD_COORDINATES_ONLY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, true, false);
							break;
						default: 
							System.out.println(UNKNOWN_IMG_GEN_MODE_MESSAGE);
							error = true;
					}	
				} else if (model instanceof NumericModel2D) {
					@SuppressWarnings("unchecked")
					NumericModel2D<BigInt> castedModel = (NumericModel2D<BigInt>)model;
					switch (imgGenerationMode) {
						case TOPPLING_ALTERNATION_VIOLATIONS:	
						case NORMAL: imgMaker.createImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap);
							break;
						case SPLIT_BY_COORDINATE_PARITY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, false, false);
							break;
						case EVEN_COORDINATES_ONLY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, false, true);
							break;
						case ODD_COORDINATES_ONLY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, true, false);
							break;
						default: 
							System.out.println(UNKNOWN_IMG_GEN_MODE_MESSAGE);
							error = true;
					}
				} else if (model instanceof IntModel) {
					IntModel2D castedModel = new IntModelAs2D((IntModel)model);
					switch (imgGenerationMode) {
						case TOPPLING_ALTERNATION_VIOLATIONS:	
						case NORMAL: imgMaker.createImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap);
							break;
						case SPLIT_BY_COORDINATE_PARITY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, false, false);
							break;
						case EVEN_COORDINATES_ONLY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, false, true);
							break;
						case ODD_COORDINATES_ONLY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, true, false);
							break;
						default: 
							System.out.println(UNKNOWN_IMG_GEN_MODE_MESSAGE);
							error = true;
					}
				} else {
					System.out.printf(UNSUPPORTED_MODEL_SECTION_MESSAGE_FORMAT, model.getClass().getName());
					error = true;
				}
				break;
			case 3:
				Model3D model3d = model instanceof Model3D ? (Model3D) model : new ModelAs3D<Model>(model);
				if (args.xScanInitialIndex == null) {
					args.xScanInitialIndex = model3d.getMaxX();
				}
				if (args.yScanInitialIndex == null) {
					args.yScanInitialIndex = model3d.getMaxY();
				}
				int maxZ = model3d.getMaxZ();
				int minZ = model3d.getMinZ();
				if (args.zScanInitialIndex == null) {
					args.zScanInitialIndex = maxZ;
				}
				int crossSectionZ = 0;
				if (crossSectionZ > maxZ || crossSectionZ < minZ) {
					crossSectionZ = minZ + (maxZ - minZ + 1)/2;
				}
				int[] scanCoords = new int[] { args.xScanInitialIndex, args.yScanInitialIndex, args.zScanInitialIndex};
				if (model instanceof IntModel3D) {
					IntModel3D castedModel = (IntModel3D)model;
					switch (imgGenerationMode) {
						case TOPPLING_ALTERNATION_VIOLATIONS:	
						case NORMAL: imgMaker.createScanningAndZCrossSectionImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap);
							break;
						case SPLIT_BY_COORDINATE_PARITY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, false, false);
							break;
						case EVEN_COORDINATES_ONLY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, false, true);
							break;
						case ODD_COORDINATES_ONLY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, true, false);
							break;
						default: 
							System.out.println(UNKNOWN_IMG_GEN_MODE_MESSAGE);
							error = true;
					}			
				} else if (model instanceof LongModel3D) {
					LongModel3D castedModel = (LongModel3D)model;
					switch (imgGenerationMode) {
						case TOPPLING_ALTERNATION_VIOLATIONS:	
						case NORMAL: imgMaker.createScanningAndZCrossSectionImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap);
							break;
						case SPLIT_BY_COORDINATE_PARITY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, false, false);
							break;
						case EVEN_COORDINATES_ONLY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, false, true);
							break;
						case ODD_COORDINATES_ONLY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, true, false);
							break;
						default: 
							System.out.println(UNKNOWN_IMG_GEN_MODE_MESSAGE);
							error = true;
					}
				} else if (model instanceof NumericModel3D) {
					@SuppressWarnings("unchecked")
					NumericModel3D<BigInt> castedModel = (NumericModel3D<BigInt>)model;
					switch (imgGenerationMode) {
						case TOPPLING_ALTERNATION_VIOLATIONS:	
						case NORMAL: imgMaker.createScanningAndZCrossSectionImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap);
							break;
						case SPLIT_BY_COORDINATE_PARITY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, false, false);
							break;
						case EVEN_COORDINATES_ONLY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, false, true);
							break;
						case ODD_COORDINATES_ONLY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, true, false);
							break;
						default: 
							System.out.println(UNKNOWN_IMG_GEN_MODE_MESSAGE);
							error = true;
					}
				} else if (model instanceof IntModel) {
					IntModel3D castedModel = new IntModelAs3D((IntModel)model);
					switch (imgGenerationMode) {
						case TOPPLING_ALTERNATION_VIOLATIONS:	
						case NORMAL: imgMaker.createScanningAndZCrossSectionImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap);
							break;
						case SPLIT_BY_COORDINATE_PARITY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, false, false);
							break;
						case EVEN_COORDINATES_ONLY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, false, true);
							break;
						case ODD_COORDINATES_ONLY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, args.steapLeap, true, false);
							break;
						default: 
							System.out.println(UNKNOWN_IMG_GEN_MODE_MESSAGE);
							error = true;
					}
				} else {
					System.out.printf(UNSUPPORTED_MODEL_SECTION_MESSAGE_FORMAT, model.getClass().getName());
					error = true;
				}
				break;
			default:
				System.out.printf(UNSUPPORTED_DIMENSION_MESSAGE_FORMAT, dimension);
				error = true;						
		}
		return !error;
	}

	private static void evolveModelToFirstStep(Model model, Args args, String backupsPath) throws Exception {
		long step = model.getStep();
		if (args.firstStep > step) {
			System.out.println("Evolving model to step " + args.firstStep + ".");
			Boolean changed;
			if (args.millisBetweenBackups != null) {
				long millis = System.currentTimeMillis();
				do {
					System.out.println("Step: " + step);
					changed = model.nextStep();
					step++;
					if (System.currentTimeMillis() - millis >= args.millisBetweenBackups) {
						String backupName = model.getClass().getSimpleName() + "_" + step;
						System.out.println("Backing up instance at '" + backupsPath + "/" + backupName + "'.");
						model.backUp(backupsPath, backupName);		
						System.out.println("Backing up finished.");
						millis = System.currentTimeMillis();
					}
				} while ((changed == null || changed) && step < args.firstStep);
			} else {
				do {
					System.out.println("Step: " + step);
					changed = model.nextStep();
					step++;
				} while ((changed == null || changed) && step < args.firstStep);
			}
		}		
	}
	
	private static final String INVALID_COORD_INDEX_MESSAGE_FORMAT = "Invalid coordinate in filter. The coordinate index must be between one and the dimension (%d), both included.%n";

	private static Model getModelSection(Model model, Args args) {
		//asymmetric section
		if (args.asymmetric && model instanceof SymmetricModel) {
			model = ((SymmetricModel)model).asymmetricSection();
		}
		CoordinateFilters filters = args.coordinateFilters;
		if (filters != null) {
			List<Integer> absoluteFilterCoords = new ArrayList<Integer>(filters.absoluteFilters.keySet());
			int absoluteFilterCount = filters.absoluteFilters.size();
			List<Integer> minMaxFilterCoords = new ArrayList<Integer>(filters.minMaxFilters.keySet());
			int minMaxFilterCount = filters.minMaxFilters.size();
			//validate the filters against the model's dimension
			int dimension = model.getGridDimension();
			if (absoluteFilterCount != 0 && absoluteFilterCoords.get(absoluteFilterCount - 1) >= dimension 
					|| minMaxFilterCount != 0 && minMaxFilterCoords.get(minMaxFilterCount - 1) >= dimension) {
				System.out.printf(INVALID_COORD_INDEX_MESSAGE_FORMAT, dimension);
				return null;
			}
			int relativeFilterGroupCount = filters.relativeFilterGroups.size();
			List<List<Integer>> relativeFilterGroupsCoords = new ArrayList<List<Integer>>(relativeFilterGroupCount);
			for (int i = 0; i != relativeFilterGroupCount; i++) {
				SortedMap<Integer, int[]> group = filters.relativeFilterGroups.get(i);
				List<Integer> groupCoords = new ArrayList<Integer>(group.keySet());
				if (groupCoords.get(group.size() - 1) >= dimension) {
					System.out.printf(INVALID_COORD_INDEX_MESSAGE_FORMAT, dimension);
					return null;
				}
				relativeFilterGroupsCoords.add(groupCoords);
			}
			//orthogonal cross sections
			for (int i = absoluteFilterCount - 1; i != -1; i--) {
				int coord = absoluteFilterCoords.get(i);
				int value = filters.absoluteFilters.get(coord);
				model = model.crossSection(coord, value);
				removeCoordinateFromFilters(coord, minMaxFilterCoords, filters.minMaxFilters);
				for (int j = 0; j != relativeFilterGroupCount; j++) {
					SortedMap<Integer, int[]> group = filters.relativeFilterGroups.get(j);
					List<Integer> groupCoords = relativeFilterGroupsCoords.get(j);
					removeCoordinateFromFilters(coord, groupCoords, group);
				}
			}		
			//diagonal cross sections
			for (int i = relativeFilterGroupCount - 1; i != -1; i--) {
				SortedMap<Integer, int[]> group = filters.relativeFilterGroups.get(i);
				List<Integer> groupCoords = relativeFilterGroupsCoords.get(i);
				int referenceCoord = groupCoords.get(0);
				for (int j = group.size() - 1; j != 0; j--) {
					int coord = groupCoords.get(j);
					int[] filter = group.get(coord);
					model = model.diagonalCrossSection(referenceCoord, coord, filter[0] == 1, filter[1]);
					removeCoordinateFromFilters(coord, minMaxFilterCoords, filters.minMaxFilters);
					for (int k = 0; k != i; k++) {
						SortedMap<Integer, int[]> otherGroup = filters.relativeFilterGroups.get(k);
						List<Integer> otherGroupCoords = relativeFilterGroupsCoords.get(k);
						removeCoordinateFromFilters(coord, otherGroupCoords, otherGroup);
					}
				}
			}
			//subsection
			if (minMaxFilterCount != 0) {
				int newDimension = model.getGridDimension();
				Integer[] minCoordinates = new Integer[newDimension];
				Integer[] maxCoordinates = new Integer[newDimension];
				for (int i = 0; i != minMaxFilterCount; i++) {
					int coord = minMaxFilterCoords.get(i);
					Integer[] minAndMax = filters.minMaxFilters.get(coord);
					minCoordinates[coord] = minAndMax[0];
					maxCoordinates[coord] = minAndMax[1];
				}
				model = model.subsection(new PartialCoordinates(minCoordinates), new PartialCoordinates(maxCoordinates));
			}
		}
		return model;
	}
	
	private static <Filter_Type> void removeCoordinateFromFilters(int removedCoordinate, List<Integer> coordinates, SortedMap<Integer, Filter_Type> filters) {
		int coordinateCount = coordinates.size();
		int i = 0;
		int coordinate = -1;
		while (i != coordinateCount && (coordinate = coordinates.get(i)) < removedCoordinate) {
			i++;
		}
		if (i != coordinateCount) {
			Filter_Type filter = filters.get(coordinate);
			int newCoordinate = coordinate - 1;
			filters.put(newCoordinate, filter);
			filters.remove(coordinate);
			coordinates.set(i, newCoordinate);
			for (i++; i != coordinateCount; i++) {
				coordinate = coordinates.get(i);
				filter = filters.get(coordinate);
				newCoordinate = coordinate - 1;
				filters.put(newCoordinate, filter);
				filters.remove(coordinate);
				coordinates.set(i, newCoordinate);
			}
		}
	}

	private static ColorMapper getColorMapper(Args args) {
		ColorMapper colorMapper = null;
		String lowerCaseColorMapName = args.colormap.toLowerCase();
		switch (lowerCaseColorMapName) {
			case "grayscale":
				colorMapper = new GrayscaleMapper(0);
				break;
			case "hue":
				colorMapper = new HueMapper();
				break;
			default:
				System.out.println("Color map '" + args.colormap + "' is not recognized.");
		}
		return colorMapper;
	}
	
	private static Model getModel(Args args) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		String lowerCaseModelName = args.model.toLowerCase();
		switch (lowerCaseModelName) {
			case "ae":
			case "aether":
				model = getAetherModel(args);
				break;
			case "siv":
			case "spread_integer_value":
				model = getSivModel(args);
				break;
			case "as":
			case "abelian_sandpile":
				model = getAbelianSandpileModel(args);
				break;
			case "nearae1":
			case "nearaether1":
				model = getNearAether1Model(args);
				break;
			case "nearae2":
			case "nearaether2":
				model = getNearAether2Model(args);
				break;
			case "nearae3":
			case "nearaether3":
				model = getNearAether3Model(args);
				break;
			default:
				System.out.println("The model '" + args.model + "' is not recognized.");
		}
		return model;
	}
	
	private static Model getSivModel(Args args) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.memorySafe) {
			System.out.printf(MEMORY_SAFE_NOT_SUPPORTED_FOR_THIS_MODEL_MESSAGE_FORMAT, args.model);
		} else if (args.initialConfiguration == null && args.backupToRestorePath == null) {
			System.out.printf(INITIAL_CONFIG_NEEDED_MESSAGE_FORMAT, args.model);
		} else if (args.backupToRestorePath != null && args.grid == null) {
			System.out.printf(GRID_TYPE_NEEDED_IN_ORDER_TO_RESTORE_MESSAGE_FORMAT);
		} else if (imgGenerationMode == ImageGenerationMode.TOPPLING_ALTERNATION_VIOLATIONS) {
			System.out.printf(IMG_GEN_MODE_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
		} else {
			if (args.grid == null) {
				args.grid = new GridOptionValue(2);//default to 2D
			}
			switch (args.grid.dimension) {
				case 1:
					if (args.grid.side == null) {
						if (args.backupToRestorePath == null) {
							if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
								if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Long.MAX_VALUE)) <= 0
										&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Long.MIN_VALUE)) >= 0) {
									model = new LongSpreadIntegerValue1D(args.initialConfiguration.singleSource.longValue(), 0); //TODO support background value?
								} else {
									System.out.printf(SINGLE_SOURCE_OUT_OF_RANGE_MESSAGE_FORMAT, Long.MIN_VALUE, Long.MAX_VALUE);
								}								
							} else {
								System.out.printf(INITIAL_CONFIG_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
							}
						} else {
							model = new LongSpreadIntegerValue1D(args.backupToRestorePath);
						}
					} else {
						System.out.printf(GRID_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
					}
					break;
				case 2:
					if (args.grid.side == null) {
						if (args.backupToRestorePath == null) {
							if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
								if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Integer.MAX_VALUE)) <= 0
										&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Integer.MIN_VALUE)) >= 0) {
									model = new IntSpreadIntegerValue2D(args.initialConfiguration.singleSource.intValue(), 0);
								} else if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Long.MAX_VALUE)) <= 0
										&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Long.MIN_VALUE)) >= 0) {
									model = new LongSpreadIntegerValue2D(args.initialConfiguration.singleSource.longValue(), 0);
								} else {
									System.out.printf(SINGLE_SOURCE_OUT_OF_RANGE_MESSAGE_FORMAT, Long.MIN_VALUE, Long.MAX_VALUE);
								}
							} else {
								System.out.printf(INITIAL_CONFIG_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
							}
						} else {
							try {
								model = new IntSpreadIntegerValue2D(args.backupToRestorePath);
							} catch (Exception ex) {
								model = new LongSpreadIntegerValue2D(args.backupToRestorePath);
							}
						}
					} else {
						System.out.printf(GRID_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
					}
					break;
				case 3:
					if (args.grid.side == null) {
						if (args.backupToRestorePath == null) {
							if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
								if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Long.MAX_VALUE)) <= 0
										&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Long.MIN_VALUE)) >= 0) {
									model = new LongSpreadIntegerValue3D(args.initialConfiguration.singleSource.longValue());
								} else {
									System.out.printf(SINGLE_SOURCE_OUT_OF_RANGE_MESSAGE_FORMAT, Long.MIN_VALUE, Long.MAX_VALUE);
								}
							} else {
								System.out.printf(INITIAL_CONFIG_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
							}
						} else {
							model = new LongSpreadIntegerValue3D(args.backupToRestorePath);
						}
					} else {
						System.out.printf(GRID_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
					}
					break;
				case 4:
					if (args.grid.side == null) {
						if (args.backupToRestorePath == null) {
							if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
								if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Long.MAX_VALUE)) <= 0
										&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Long.MIN_VALUE)) >= 0) {
									model = new LongSpreadIntegerValue4D(args.initialConfiguration.singleSource.longValue());
								} else {
									System.out.printf(SINGLE_SOURCE_OUT_OF_RANGE_MESSAGE_FORMAT, Long.MIN_VALUE, Long.MAX_VALUE);
								}
							} else {
								System.out.printf(INITIAL_CONFIG_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
							}
						} else {
							model = new LongSpreadIntegerValue4D(args.backupToRestorePath);
						}
					} else {
						System.out.printf(GRID_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
					}
					break;
				default:
					if (args.grid.side == null) {
						if (args.backupToRestorePath == null) {
							if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
								if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Integer.MAX_VALUE)) <= 0
										&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Integer.MIN_VALUE)) >= 0) {
									model = new IntSpreadIntegerValue(args.grid.dimension, args.initialConfiguration.singleSource.intValue(), 0);
								} else {
									System.out.printf(SINGLE_SOURCE_OUT_OF_RANGE_MESSAGE_FORMAT, Integer.MIN_VALUE, Integer.MAX_VALUE);
								}
							} else {
								System.out.printf(INITIAL_CONFIG_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
							}
						} else {
							model = new IntSpreadIntegerValue(args.backupToRestorePath);
						}
					} else {
						System.out.printf(GRID_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
					}
					break;
			}
		}
		return model;
	}

	private static Model getAbelianSandpileModel(Args args) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.memorySafe) {
			System.out.printf(MEMORY_SAFE_NOT_SUPPORTED_FOR_THIS_MODEL_MESSAGE_FORMAT, args.model);
		} else if (args.initialConfiguration == null && args.backupToRestorePath == null) {
			System.out.printf(INITIAL_CONFIG_NEEDED_MESSAGE_FORMAT, args.model);
//		} else if (args.backupToRestorePath != null && args.grid == null) { //uncomment if more grid types become supported
//			System.out.printf(gridTypeNeededToRestoreMessageFormat);
		} else if (imgGenerationMode == ImageGenerationMode.TOPPLING_ALTERNATION_VIOLATIONS) {
			System.out.printf(IMG_GEN_MODE_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
		} else {
			if (args.grid == null) {
				args.grid = new GridOptionValue(2);//default to 2D
			}
			switch (args.grid.dimension) {
				case 2:
					if (args.grid.side == null) {
						if (args.backupToRestorePath == null) {
							if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
								if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Integer.MAX_VALUE)) <= 0
										&& args.initialConfiguration.singleSource.compareTo(BigInt.ZERO) >= 0) {
									model = new IntAbelianSandpileSingleSource2D(args.initialConfiguration.singleSource.intValue());
								} else {
									System.out.printf(SINGLE_SOURCE_OUT_OF_RANGE_MESSAGE_FORMAT, 0, Integer.MAX_VALUE);
								}
							} else {
								System.out.printf(INITIAL_CONFIG_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
							}
						} else {
							model = new IntAbelianSandpileSingleSource2D(args.backupToRestorePath);
						}
					} else {
						System.out.printf(GRID_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
					}
					break;
				default:
					System.out.printf(GRID_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
			}
		}
		return model;
	}
	
	private static Model getAetherModel(Args args) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.initialConfiguration == null && args.backupToRestorePath == null) {
			System.out.printf(INITIAL_CONFIG_NEEDED_MESSAGE_FORMAT, args.model);
		} else if (args.backupToRestorePath != null && args.grid == null) {
			System.out.printf(GRID_TYPE_NEEDED_IN_ORDER_TO_RESTORE_MESSAGE_FORMAT);
		} else if (args.memorySafe && args.initialConfiguration.type != InitialConfigType.SINGLE_SOURCE) {
			System.out.printf(MEMORY_SAFE_NOT_SUPPORTED_FOR_THIS_INITIAL_CONFIG_MESSAGE_FORMAT, args.model);
		} else {
			if (args.grid == null) {
				args.grid = new GridOptionValue(2);//default to 2D
			}
			switch (args.grid.dimension) {
				case 1:
					if (args.grid.side == null) {
						if (args.backupToRestorePath == null) {
							if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
								if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether1D.MAX_INITIAL_VALUE)) <= 0
										&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether1D.MIN_INITIAL_VALUE)) >= 0) {
									if (args.memorySafe) {
										model = new FileBackedAether1D(args.initialConfiguration.singleSource.longValue(), args.path);
									} else {
										model = new LongAether1D(args.initialConfiguration.singleSource.longValue());
									}
								} else {
									System.out.printf(SINGLE_SOURCE_OUT_OF_RANGE_MESSAGE_FORMAT, LongAether1D.MIN_INITIAL_VALUE, LongAether1D.MAX_INITIAL_VALUE);
								}								
							} else {
								System.out.printf(INITIAL_CONFIG_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
							}
						} else {
							if (args.memorySafe) {
								model = new FileBackedAether1D(args.backupToRestorePath, args.path);
							} else {
								model = new LongAether1D(args.backupToRestorePath);
							}
						}
					} else {
						System.out.printf(GRID_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
					}
					break;
				case 2:
					if (args.grid.side == null) {
						if (args.backupToRestorePath == null) {
							if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
								if (imgGenerationMode == ImageGenerationMode.TOPPLING_ALTERNATION_VIOLATIONS) {
									if (args.memorySafe) {
										System.out.printf(MEMORY_SAFE_NOT_SUPPORTED_FOR_THIS_CONFIG_MESSAGE_FORMAT);
									} else {
										if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether2DTopplingAlternationViolations.MAX_INITIAL_VALUE)) <= 0
												&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether2DTopplingAlternationViolations.MIN_INITIAL_VALUE)) >= 0) {
											model = new IntAether2DTopplingAlternationViolations(args.initialConfiguration.singleSource.intValue());
										} else if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether2DTopplingAlternationViolations.MAX_INITIAL_VALUE)) <= 0
												&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether2DTopplingAlternationViolations.MIN_INITIAL_VALUE)) >= 0) {
											model = new LongAether2DTopplingAlternationViolations(args.initialConfiguration.singleSource.longValue());
										} else {
											model = new BigIntAether2DTopplingAlternationViolations(args.initialConfiguration.singleSource);
										}
									}
								} else {
									if (args.memorySafe) {
										if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedAether2D.MAX_INITIAL_VALUE)) <= 0
												&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedAether2D.MIN_INITIAL_VALUE)) >= 0) {
											model = new FileBackedAether2D(args.initialConfiguration.singleSource.longValue(), args.path);
										} else {
											System.out.printf(SINGLE_SOURCE_OUT_OF_RANGE_MESSAGE_FORMAT, FileBackedAether2D.MIN_INITIAL_VALUE, FileBackedAether2D.MAX_INITIAL_VALUE);
										}
									} else {
										if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether2D.MAX_INITIAL_VALUE)) <= 0
												&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether2D.MIN_INITIAL_VALUE)) >= 0) {
											model = new IntAether2D(args.initialConfiguration.singleSource.intValue());
										} else if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether2D.MAX_INITIAL_VALUE)) <= 0
												&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether2D.MIN_INITIAL_VALUE)) >= 0) {
											model = new LongAether2D(args.initialConfiguration.singleSource.longValue());
										} else {
											model = new BigIntAether2D(args.initialConfiguration.singleSource);
										}
									}
								}
							} else {
								//TODO output error in case of -memory-safe
								if (args.initialConfiguration.min.compareTo(BigInt.valueOf(Integer.MAX_VALUE)) <= 0
										&& args.initialConfiguration.min.compareTo(BigInt.valueOf(Integer.MIN_VALUE)) >= 0
										&& args.initialConfiguration.max.compareTo(BigInt.valueOf(Integer.MAX_VALUE)) <= 0
										&& args.initialConfiguration.max.compareTo(BigInt.valueOf(Integer.MIN_VALUE)) >= 0) {
									model = new IntAether2DRandomConfiguration(args.initialConfiguration.side, args.initialConfiguration.min.intValue(), args.initialConfiguration.max.intValue());
								} else {
									System.out.printf(MIN_MAX_OUT_OF_RANGE_MESSAGE_FORMAT, Integer.MIN_VALUE, Integer.MAX_VALUE);
								}
							}
						} else {
							boolean successfullyRestored = true;
							if (args.memorySafe) {
								model = new FileBackedAether2D(args.backupToRestorePath, args.path);
							} else if (imgGenerationMode == ImageGenerationMode.TOPPLING_ALTERNATION_VIOLATIONS) {
								try {
									model = new IntAether2DTopplingAlternationViolations(args.backupToRestorePath);							
								} catch (Exception ex1) {
									try {
										model = new LongAether2DTopplingAlternationViolations(args.backupToRestorePath);							
									} catch (Exception ex2) {
										try {
											model = new BigIntAether2DTopplingAlternationViolations(args.backupToRestorePath);							
										} catch (Exception ex3) {
											successfullyRestored = false;
										}						
									}						
								}
							} else {	
								try {
									model = new IntAether2D(args.backupToRestorePath);							
								} catch (Exception ex1) {
									try {
										model = new LongAether2D(args.backupToRestorePath);							
									} catch (Exception ex2) {
										try {
											model = new BigIntAether2D(args.backupToRestorePath);							
										} catch (Exception ex3) {
											try {
												model = new IntAether2DRandomConfiguration(args.backupToRestorePath);			
											} catch (Exception ex4) {
												successfullyRestored = false;					
											}
										}						
									}						
								}
							}
							if (!successfullyRestored) {
								//TODO output error			
							}
						}
					} else {
						System.out.printf(GRID_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
					}
					break;
				case 3:
					if (args.grid.side == null) {
						if (args.backupToRestorePath == null) {
							if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
								if (args.memorySafe) {
									if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedAether3D.MAX_INITIAL_VALUE)) <= 0
											&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedAether3D.MIN_INITIAL_VALUE)) >= 0) {
										model = new FileBackedAether3D(args.initialConfiguration.singleSource.longValue(), args.path);
									} else {
										System.out.printf(SINGLE_SOURCE_OUT_OF_RANGE_MESSAGE_FORMAT, FileBackedAether3D.MIN_INITIAL_VALUE, FileBackedAether3D.MAX_INITIAL_VALUE);
									}
								} else {
									if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether3D.MAX_INITIAL_VALUE)) <= 0
											&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether3D.MIN_INITIAL_VALUE)) >= 0) {
										model = new IntAether3D(args.initialConfiguration.singleSource.intValue());
									} else if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether3D.MAX_INITIAL_VALUE)) <= 0
											&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether3D.MIN_INITIAL_VALUE)) >= 0) {
										model = new LongAether3D(args.initialConfiguration.singleSource.longValue());
									} else {
										model = new BigIntAether3D(args.initialConfiguration.singleSource);
									}
								}
							} else {
								if (args.initialConfiguration.min.compareTo(BigInt.valueOf(Integer.MAX_VALUE)) <= 0
										&& args.initialConfiguration.min.compareTo(BigInt.valueOf(Integer.MIN_VALUE)) >= 0
										&& args.initialConfiguration.max.compareTo(BigInt.valueOf(Integer.MAX_VALUE)) <= 0
										&& args.initialConfiguration.max.compareTo(BigInt.valueOf(Integer.MIN_VALUE)) >= 0) {
									model = new IntAether3DRandomConfiguration(args.initialConfiguration.side, args.initialConfiguration.min.intValue(), args.initialConfiguration.max.intValue());
								} else {
									System.out.printf(MIN_MAX_OUT_OF_RANGE_MESSAGE_FORMAT, Integer.MIN_VALUE, Integer.MAX_VALUE);
								}
							}
						} else {
							if (args.memorySafe) {
								model = new FileBackedAether3D(args.backupToRestorePath, args.path);
							} else {	
								try {
									model = new IntAether3D(args.backupToRestorePath);							
								} catch (Exception ex1) {
									try {
										model = new LongAether3D(args.backupToRestorePath);							
									} catch (Exception ex2) {
										try {
											model = new BigIntAether3D(args.backupToRestorePath);							
										} catch (Exception ex3) {
											model = new IntAether3DRandomConfiguration(args.backupToRestorePath);							
										}						
									}						
								}
							}
						}
					} else {
						if (args.backupToRestorePath == null) {
							if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) { 
								if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether3DCubicGrid.MAX_INITIAL_VALUE)) <= 0
										&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether3DCubicGrid.MIN_INITIAL_VALUE)) >= 0) {
									model = new LongAether3DCubicGrid(args.grid.side, args.initialConfiguration.singleSource.longValue());
								} else {
									model = new BigIntAether3DCubicGrid(args.grid.side, args.initialConfiguration.singleSource);
								}
							} else {
								System.out.printf(INITIAL_CONFIG_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
							}
						} else {
							try {
								model = new LongAether3DCubicGrid(args.backupToRestorePath);							
							} catch (Exception ex1) {
								model = new BigIntAether3DCubicGrid(args.backupToRestorePath);			
							}
						}
					}
					break;
				case 4:
					if (args.grid.side == null) {
						if (args.backupToRestorePath == null) {
							if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
								//TODO use memory safe implementations depending on asymmetric and single source options and available heap space?
								//long heapFreeSize = Runtime.getRuntime().freeMemory();
								if (args.memorySafe) {
									if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedAether4D.MAX_INITIAL_VALUE)) <= 0
											&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedAether4D.MIN_INITIAL_VALUE)) >= 0) {
										model = new FileBackedAether4D(args.initialConfiguration.singleSource.longValue(), args.path);
									} else {
										System.out.printf(SINGLE_SOURCE_OUT_OF_RANGE_MESSAGE_FORMAT, FileBackedAether4D.MIN_INITIAL_VALUE, FileBackedAether4D.MAX_INITIAL_VALUE);
									}
								} else {
									if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether4D.MAX_INITIAL_VALUE)) <= 0
											&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether4D.MIN_INITIAL_VALUE)) >= 0) {
										model = new IntAether4D(args.initialConfiguration.singleSource.intValue());
									} else if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether4D.MAX_INITIAL_VALUE)) <= 0
											&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether4D.MIN_INITIAL_VALUE)) >= 0) {
										model = new LongAether4D(args.initialConfiguration.singleSource.longValue());
									} else {
										model = new BigIntAether4D(args.initialConfiguration.singleSource);
									}		
								}						
							} else {
								System.out.printf(INITIAL_CONFIG_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
							}
						} else {
							if (args.memorySafe) {
								model = new FileBackedAether4D(args.backupToRestorePath, args.path);
							} else {	
								try {
									model = new LongAether4D(args.backupToRestorePath);							
								} catch (Exception ex1) {
									model = new BigIntAether4D(args.backupToRestorePath);			
								}
							}
						}
					} else {
						System.out.printf(GRID_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
					}
					break;
				case 5:
					if (args.grid.side == null) {
						if (args.backupToRestorePath == null) {
							if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
								//TODO use memory safe implementations depending on asymmetric and single source options and available heap space?
								//long heapFreeSize = Runtime.getRuntime().freeMemory();
								if (args.memorySafe) {
									if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedAether5D.MAX_INITIAL_VALUE)) <= 0
											&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedAether5D.MIN_INITIAL_VALUE)) >= 0) {
										model = new FileBackedAether5D(args.initialConfiguration.singleSource.longValue(), args.path);
									} else {
										System.out.printf(SINGLE_SOURCE_OUT_OF_RANGE_MESSAGE_FORMAT, FileBackedAether5D.MIN_INITIAL_VALUE, FileBackedAether5D.MAX_INITIAL_VALUE);
									}
								} else {
									if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether5D.MAX_INITIAL_VALUE)) <= 0
											&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether5D.MIN_INITIAL_VALUE)) >= 0) {
										model = new IntAether5D(args.initialConfiguration.singleSource.intValue());
									} else if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether5D.MAX_INITIAL_VALUE)) <= 0
											&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether5D.MIN_INITIAL_VALUE)) >= 0) {
										model = new LongAether5D(args.initialConfiguration.singleSource.longValue());
									} else {
										System.out.printf(SINGLE_SOURCE_OUT_OF_RANGE_MESSAGE_FORMAT, LongAether5D.MIN_INITIAL_VALUE, LongAether5D.MAX_INITIAL_VALUE);
									}
								}
							} else {
								System.out.printf(INITIAL_CONFIG_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
							}
						} else {
							if (args.memorySafe) {
								model = new FileBackedAether5D(args.backupToRestorePath, args.path);
							} else {	
								try {
									model = new IntAether5D(args.backupToRestorePath);							
								} catch (Exception ex1) {
									model = new LongAether5D(args.backupToRestorePath);			
								}
							}
						}
					} else {
						System.out.printf(GRID_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
					}
					break;
				default:
					System.out.printf(GRID_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
			}
		}
		return model;
	}

	private static Model getNearAether1Model(Args args) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.memorySafe) {
			System.out.printf(MEMORY_SAFE_NOT_SUPPORTED_FOR_THIS_MODEL_MESSAGE_FORMAT, args.model);
		} else if (args.initialConfiguration == null && args.backupToRestorePath == null) {
			System.out.printf(INITIAL_CONFIG_NEEDED_MESSAGE_FORMAT, args.model);
//		} else if (args.backupToRestorePath != null && args.grid == null) { //uncomment if more grid types become supported
//			System.out.printf(gridTypeNeededToRestoreMessageFormat);
		} else if (imgGenerationMode == ImageGenerationMode.TOPPLING_ALTERNATION_VIOLATIONS) {
			System.out.printf(IMG_GEN_MODE_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
		} else {
			if (args.grid == null) {
				args.grid = new GridOptionValue(3);//default
			}
			switch (args.grid.dimension) {
				case 3:
					if (args.grid.side == null) {
						if (args.backupToRestorePath == null) {
							if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
								if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntNearAether1_3D.MAX_INITIAL_VALUE)) <= 0
										&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntNearAether1_3D.MIN_INITIAL_VALUE)) >= 0) {
									model = new IntNearAether1_3D(args.initialConfiguration.singleSource.intValue());
								} else {
									System.out.printf(SINGLE_SOURCE_OUT_OF_RANGE_MESSAGE_FORMAT, IntNearAether1_3D.MIN_INITIAL_VALUE, IntNearAether1_3D.MAX_INITIAL_VALUE);
								}
							} else {
								System.out.printf(INITIAL_CONFIG_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
							}
						} else {
							model = new IntNearAether1_3D(args.backupToRestorePath);
						}
					} else {
						System.out.printf(GRID_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
					}
					break;
				default:
					System.out.printf(GRID_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
			}
		}
		return model;
	}
	
	private static Model getNearAether2Model(Args args) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.memorySafe) {
			System.out.printf(MEMORY_SAFE_NOT_SUPPORTED_FOR_THIS_MODEL_MESSAGE_FORMAT, args.model);
		} else if (args.memorySafe) {
			System.out.printf(MEMORY_SAFE_NOT_SUPPORTED_FOR_THIS_MODEL_MESSAGE_FORMAT, args.model);
		} else if (args.initialConfiguration == null && args.backupToRestorePath == null) {
			System.out.printf(INITIAL_CONFIG_NEEDED_MESSAGE_FORMAT, args.model);
//		} else if (args.backupToRestorePath != null && args.grid == null) { //uncomment if more grid types become supported
//			System.out.printf(gridTypeNeededToRestoreMessageFormat);
		} else if (imgGenerationMode == ImageGenerationMode.TOPPLING_ALTERNATION_VIOLATIONS) {
			System.out.printf(IMG_GEN_MODE_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
		} else {
			if (args.grid == null) {
				args.grid = new GridOptionValue(3);//default
			}
			switch (args.grid.dimension) {
				case 3:
					if (args.grid.side == null) {
						if (args.backupToRestorePath == null) {
							if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
								if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntNearAether2_3D.MAX_INITIAL_VALUE)) <= 0
										&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntNearAether2_3D.MIN_INITIAL_VALUE)) >= 0) {
									model = new IntNearAether2_3D(args.initialConfiguration.singleSource.intValue());
								} else {
									System.out.printf(SINGLE_SOURCE_OUT_OF_RANGE_MESSAGE_FORMAT, IntNearAether1_3D.MIN_INITIAL_VALUE, IntNearAether1_3D.MAX_INITIAL_VALUE);
								}
							} else {
								System.out.printf(INITIAL_CONFIG_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
							}
						} else {
							model = new IntNearAether2_3D(args.backupToRestorePath);
						}
					} else {
						System.out.printf(GRID_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
					}
					break;
				default:
					System.out.printf(GRID_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
			}
		}
		return model;
	}
	
	private static Model getNearAether3Model(Args args) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.memorySafe) {
			System.out.printf(MEMORY_SAFE_NOT_SUPPORTED_FOR_THIS_MODEL_MESSAGE_FORMAT, args.model);
		} else if (args.initialConfiguration == null && args.backupToRestorePath == null) {
			System.out.printf(INITIAL_CONFIG_NEEDED_MESSAGE_FORMAT, args.model);
//		} else if (args.backupToRestorePath != null && args.grid == null) { //uncomment if more grid types become supported
//			System.out.printf(gridTypeNeededToRestoreMessageFormat);
		} else if (imgGenerationMode == ImageGenerationMode.TOPPLING_ALTERNATION_VIOLATIONS) {
			System.out.printf(IMG_GEN_MODE_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
		} else {
			if (args.grid == null) {
				args.grid = new GridOptionValue(3);//default
			}
			switch (args.grid.dimension) {
				case 3:
					if (args.grid.side == null) {
						if (args.backupToRestorePath == null) {
							if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
								model = new SimpleBigIntNearAether3_3D(args.initialConfiguration.singleSource);
							} else {
								System.out.printf(INITIAL_CONFIG_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
							}
						} else {
							model = new SimpleBigIntNearAether3_3D(args.backupToRestorePath);
						}
					} else {
						System.out.printf(GRID_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
					}
					break;
				default:
					System.out.printf(GRID_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
			}
		}
		return model;
	}
	
}
