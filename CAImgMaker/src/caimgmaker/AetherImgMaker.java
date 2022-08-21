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
package caimgmaker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import org.apache.commons.io.FileUtils;

import com.beust.jcommander.JCommander;
import caimgmaker.args.Args;
import caimgmaker.args.GridOptionValue;
import caimgmaker.args.InitialConfigOptionValue.InitialConfigType;
import caimgmaker.colormap.ColorMapper;
import caimgmaker.colormap.GrayscaleMapper;
import caimgmaker.colormap.HueMapper;
import cellularautomata.automata.AbelianSandpileSingleSource2D;
import cellularautomata.automata.aether.Aether1D;
import cellularautomata.automata.aether.Aether2D;
import cellularautomata.automata.aether.IntAether2DRandomConfiguration;
import cellularautomata.automata.aether.Aether3D;
import cellularautomata.automata.aether.Aether3DCubicGrid;
import cellularautomata.automata.aether.Aether4D;
import cellularautomata.automata.aether.Aether5D;
import cellularautomata.automata.aether.BigIntAether2D;
import cellularautomata.automata.aether.BigIntAether3D;
import cellularautomata.automata.aether.BigIntAether3DCubicGrid;
import cellularautomata.automata.aether.BigIntAether4D;
import cellularautomata.automata.aether.FileBackedAether1D;
import cellularautomata.automata.aether.FileBackedAether2D;
import cellularautomata.automata.aether.FileBackedAether3D;
import cellularautomata.automata.aether.FileBackedAether4D;
import cellularautomata.automata.aether.FileBackedAether5D;
import cellularautomata.automata.aether.IntAether3D;
import cellularautomata.automata.aether.IntAether3DRandomConfiguration;
import cellularautomata.automata.aether.IntAether4D;
import cellularautomata.automata.aether.IntAether5D;
import cellularautomata.automata.nearaether.BigIntNearAether3Simple3D;
import cellularautomata.automata.nearaether.IntNearAether1_3D;
import cellularautomata.automata.nearaether.IntNearAether2_3D;
import cellularautomata.automata.siv.IntSpreadIntegerValue2D;
import cellularautomata.automata.siv.SpreadIntegerValue1D;
import cellularautomata.automata.siv.SpreadIntegerValue2D;
import cellularautomata.automata.siv.SpreadIntegerValue3D;
import cellularautomata.automata.siv.SpreadIntegerValue4D;
import cellularautomata.model.Model;
import cellularautomata.model.SymmetricModel;
import cellularautomata.model1d.Model1D;
import cellularautomata.model2d.IntModel2D;
import cellularautomata.model2d.LongModel2D;
import cellularautomata.model2d.Model2D;
import cellularautomata.model2d.NumericModel2D;
import cellularautomata.model3d.IntModel3D;
import cellularautomata.model3d.LongModel3D;
import cellularautomata.model3d.Model3D;
import cellularautomata.model3d.NumericModel3D;
import cellularautomata.model4d.Model4D;
import cellularautomata.numbers.BigInt;

public class AetherImgMaker {
	
	private static final String gridNotSupportedMessageFormat = "The %s model is currently not supported with this type of grid.%n";
	private static final String gridTypeNeededToRestoreMessageFormat = "You need to specify the grid type of the backup you are trying to restore.%n";
	private static final String initialConfigNotSupportedMessageFormat = "The %s model is currently not supported with the selected initial configuration.%n";
	private static final String singleSourceOutOfRangeMessageFormat = "The single source value is out of the currently supported range for this model: [%d, %d].%n";
	private static final String minAndMaxOutOfRangeMessageFormat = "The min/max values are out of the currently supported range for this model: [%d, %d].%n";
	private static final String initialConfigNeededMessageFormat = "The %s model needs and initial configuration.%n";
	private static final String unsupportedDimensionCount = "Currently it is only supported to generate images from a model section with dimension two or three. Use any of the coordinate options (-v -w -x -y -z) or a -grid with two or three dimensions.";
	private static final String unknownImgGenMode = "Unrecognized image generation mode.";
	private static final String unsupportedModelSectionMessageFormat = "It is currently not supported to generate images form a model section of type %s.%n";
	private static final String memorySafeNotSupportedForThisModelMessageFormat = "The %s model is currently not supported with the -memory-safe option.%n";
	private static final String memorySafeNotSupportedForThisInitialConfigMessageFormat = "The %s model is currently not supported with the -memory-safe option and the selected initial configuration.%n";

	public static void main(String[] rawArgs) throws Exception {
//		String debugArgs = "-initial-config single-source_92233720368547758079999 -path D:/data/test";//debug
//		debugArgs = "-help";//debug
//		rawArgs = debugArgs.split(" ");//debug
		final String useHelpMessage = "Use -help to view the list of available options and their accepted values.";
		try {
			Args args = new Args();
			JCommander jcommander = JCommander.newBuilder()
			.addObject(args)
			.build();
			jcommander.parse(rawArgs);
			if (args.help) {
				jcommander.usage();
				return;
			}
			if (args.outputVersion) {
				System.out.println("0.5.4");
				return;
			}
			Model model = getModel(args);
			if (model == null) {
				System.out.println(useHelpMessage);
				return;
			}
			String path = args.path + "/" + model.getSubfolderPath();
			String backupsPath = path + "/backups";
			evolveModelToInitialStep(model, args, backupsPath);
			Model modelSection = getModelSection(model, args);
			if (modelSection == null) {
				System.out.println(useHelpMessage);
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
				System.out.println(useHelpMessage);
			}
		} catch (Exception ex) {
			String message = ex.getMessage();
			if (message == null) {
				System.out.println("Unexpected error.");
				ex.printStackTrace();
			} else if (message.contains("main parameter")) {
				System.out.println("One or more unrecognized options found.");
			} else {
				System.out.println(message);
			}
			System.out.println(useHelpMessage);
		}
	}
	
	private static boolean generateImages(Model model, Args args, String backupsPath) throws Exception {
		boolean error = false;
		final String path = args.path + "/" + model.getSubfolderPath();
		final String imagesPath = path + "/img";
		ColorMapper colorMapper = getColorMapper(args);
		if (colorMapper == null)
			return false;
		ImgMaker imgMaker = null;
		if (args.millisBetweenBackups == null) {
			imgMaker = new ImgMaker();
		} else {
			imgMaker = new ImgMaker(args.millisBetweenBackups);
		}
		if (model instanceof Model1D) {
			System.out.println(unsupportedDimensionCount);
		} else if (model instanceof Model2D) {
			if (model instanceof IntModel2D) {
				IntModel2D castedModel = (IntModel2D)model;
				switch (args.imgGenerationMode) {
					case Args.NORMAL: imgMaker.createImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath);
						break;
					case Args.SPLIT_PARITY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, false, false);
						break;
					case Args.EVEN_ONLY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, false, true);
						break;
					case Args.ODD_ONLY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, true, false);
						break;
					default: 
						System.out.println(unknownImgGenMode);
						error = true;
				}				
			} else if (model instanceof LongModel2D) {
				LongModel2D castedModel = (LongModel2D)model;
				switch (args.imgGenerationMode) {
					case Args.NORMAL: imgMaker.createImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath);
						break;
					case Args.SPLIT_PARITY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, false, false);
						break;
					case Args.EVEN_ONLY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, false, true);
						break;
					case Args.ODD_ONLY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, true, false);
						break;
					default: 
						System.out.println(unknownImgGenMode);
						error = true;
				}	
			} else if (model instanceof NumericModel2D) {
				@SuppressWarnings("unchecked")
				NumericModel2D<BigInt> castedModel = (NumericModel2D<BigInt>)model;
				switch (args.imgGenerationMode) {
					case Args.NORMAL: imgMaker.createImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath);
						break;
					case Args.SPLIT_PARITY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, false, false);
						break;
					case Args.EVEN_ONLY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, false, true);
						break;
					case Args.ODD_ONLY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, true, false);
						break;
					default: 
						System.out.println(unknownImgGenMode);
						error = true;
				}
			} else {
				System.out.printf(unsupportedModelSectionMessageFormat, model.getClass().getName());
				error = true;
			}
		} else if (model instanceof Model3D) {
			Model3D model3d = (Model3D) model;
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
				switch (args.imgGenerationMode) {
					case Args.NORMAL: imgMaker.createScanningAndZCrossSectionImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath);
						break;
					case Args.SPLIT_PARITY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, false, false);
						break;
					case Args.EVEN_ONLY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, false, true);
						break;
					case Args.ODD_ONLY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, true, false);
						break;
					default: 
						System.out.println(unknownImgGenMode);
						error = true;
				}			
			} else if (model instanceof LongModel3D) {
				LongModel3D castedModel = (LongModel3D)model;
					switch (args.imgGenerationMode) {
					case Args.NORMAL: imgMaker.createScanningAndZCrossSectionImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath);
						break;
					case Args.SPLIT_PARITY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, false, false);
						break;
					case Args.EVEN_ONLY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, false, true);
						break;
					case Args.ODD_ONLY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, true, false);
						break;
					default: 
						System.out.println(unknownImgGenMode);
						error = true;
				}
			} else if (model instanceof NumericModel3D) {
				@SuppressWarnings("unchecked")
				NumericModel3D<BigInt> castedModel = (NumericModel3D<BigInt>)model;
				switch (args.imgGenerationMode) {
					case Args.NORMAL: imgMaker.createScanningAndZCrossSectionImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath);
						break;
					case Args.SPLIT_PARITY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, false, false);
						break;
					case Args.EVEN_ONLY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, false, true);
						break;
					case Args.ODD_ONLY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, backupsPath, true, false);
						break;
					default: 
						System.out.println(unknownImgGenMode);
						error = true;
				}
			} else {
				System.out.printf(unsupportedModelSectionMessageFormat, model.getClass().getName());
				error = true;
			}
		} else {
			System.out.println(unsupportedDimensionCount);
			error = true;
		}
		return !error;
	}

	private static void evolveModelToInitialStep(Model model, Args args, String backupsPath) throws Exception {
		if (args.initialStep > model.getStep()) {
			System.out.println("Evolving model to step " + args.initialStep + ".");
			if (args.millisBetweenBackups != null) {
				long millis = System.currentTimeMillis();
				do {
					model.nextStep();
					System.out.println("Step: " + model.getStep());
					if (System.currentTimeMillis() - millis >= args.millisBetweenBackups) {
						String backupName = model.getClass().getSimpleName() + "_" + model.getStep();
						System.out.println("Backing up instance at '" + backupsPath + "/" + backupName + "'.");
						model.backUp(backupsPath, backupName);		
						System.out.println("Backing up finished.");
						millis = System.currentTimeMillis();
					}
				} while (model.getStep() < args.initialStep);
			} else {
				do {
					model.nextStep();
					System.out.println("Step: " + model.getStep());
				} while (model.getStep() < args.initialStep);
			}
		}		
	}
	
	private static Integer getCoordIndex(String coord) {
		Integer index = null;
		if (coord != null) {
			switch (coord) {
			case "v":
				index = 4;
				break;
			case "w":
				index = 3;
				break;
			case "x":
				index = 2;
				break;
			case "y":
				index = 1;
				break;
			case "z":
				index = 0;
				break;
			}
		}
		return index;
	}
	
	private static Model getModelSection(Model model, Args args) {
		if (args.asymmetric && model instanceof SymmetricModel) {
			model = ((SymmetricModel)model).asymmetricSection();
		}	
		final String onlyRangesOnAllCoordsAreSupported = "Currently it is only supported to use coordinate ranges if they are used in all coordinates. Try passing ranges in all coordinates";
		final String coordRelativeToItselfError = "A coordinate cannot be set relative to itself.";
		Integer vCoord = null, wCoord = null, xCoord = null, yCoord = null, zCoord = null;
		int[] vRange = null, wRange = null, xRange = null, yRange = null, zRange = null;
		Integer vRefCoord = null, wRefCoord = null, xRefCoord = null, yRefCoord = null, zRefCoord = null;
		Integer vOffset = null, wOffset = null, xOffset = null, yOffset = null, zOffset = null;
		if (args.v != null) {
			vCoord = args.v.coordinate;
			vRange = args.v.range;
			vRefCoord = getCoordIndex(args.v.referenceCoordinate);
			vOffset = args.v.offset;
			if (vRefCoord != null && vRefCoord == 4) {
				if (vOffset == 0) {
					vRefCoord = null;
					vOffset = null;
				} else {
					System.out.println(coordRelativeToItselfError);
				}
			}
		}
		if (args.w != null) {
			wCoord = args.w.coordinate;
			wRange = args.w.range;
			wRefCoord = getCoordIndex(args.w.referenceCoordinate);
			wOffset = args.w.offset;
			if (wRefCoord != null && wRefCoord == 3) {
				if (wOffset == 0) {
					wRefCoord = null;
					wOffset = null;
				} else {
					System.out.println(coordRelativeToItselfError);
				}
			}
		}
		if (args.x != null) {
			xCoord = args.x.coordinate;
			xRange = args.x.range;
			xRefCoord = getCoordIndex(args.x.referenceCoordinate);
			xOffset = args.x.offset;
			if (xRefCoord != null && xRefCoord == 2) {
				if (xOffset == 0) {
					xRefCoord = null;
					xOffset = null;
				} else {
					System.out.println(coordRelativeToItselfError);
				}
			}
		}
		if (args.y != null) {
			yCoord = args.y.coordinate;
			yRange = args.y.range;
			yRefCoord = getCoordIndex(args.y.referenceCoordinate);
			yOffset = args.y.offset;
			if (yRefCoord != null && yRefCoord == 1) {
				if (yOffset == 0) {
					yRefCoord = null;
					yOffset = null;
				} else {
					System.out.println(coordRelativeToItselfError);
				}
			}
		}
		if (args.z != null) {
			zCoord = args.z.coordinate;
			zRange = args.z.range;
			zRefCoord = getCoordIndex(args.z.referenceCoordinate);
			zOffset = args.z.offset;
			if (zRefCoord != null && zRefCoord == 0) {
				if (zOffset == 0) {
					zRefCoord = null;
					zOffset = null;
				} else {
					System.out.println(coordRelativeToItselfError);
				}
			}
		}
		//TODO diagonal cross sections (refCoords and offsets)
//		if (model instanceof Model5D) {
//			
//		}
		if (model instanceof Model4D) {
			Model4D model4d = (Model4D)model;
			if (wCoord != null) {
				model = model4d.crossSectionAtW(wCoord);
			} else if (xCoord != null) {
				model = model4d.crossSectionAtX(xCoord);
				//relocate args for cascading
				xCoord = wCoord; xRange = wRange; //xRefCoord = wRefCoord; xOffset = wOffset; //not possible with refcoord? review
			} else if (yCoord != null) {
				model = model4d.crossSectionAtY(yCoord);
				//relocate args for cascading
				yCoord = xCoord; yRange = xRange;
				xCoord = wCoord; xRange = wRange;
			} else if (zCoord != null) {
				model = model4d.crossSectionAtZ(zCoord);
				//relocate args for cascading
				zCoord = yCoord; zRange = yRange;
				yCoord = xCoord; yRange = xRange;
				xCoord = wCoord; xRange = wRange;
			} /*else if (xRefCoord != null) {
				switch (xRefCoord) {
				case "y":
					break;
				case "z":
					break;
				default:
					
				}
			}*/ else if (wRange != null && xRange != null && yRange != null && zRange != null) {
				model = model4d.subsection(wRange[0], wRange[1], xRange[0], xRange[1], yRange[0], yRange[1], zRange[0], zRange[1]);
			} else if (wRange != null || xRange != null || yRange != null || zRange != null) {
				System.out.println(onlyRangesOnAllCoordsAreSupported);
				model = null;
			}
		}
		if (model instanceof Model3D) {
			Model3D model3d = (Model3D)model;
			if (xCoord != null) {
				model = model3d.crossSectionAtX(xCoord);
				//relocate args for cascading
				xCoord = yCoord; xRange = yRange; //xRefCoord = yRefCoord; xOffset = yOffset; //not possible with refcoord. review
				yCoord = zCoord; yRange = zRange; //yRefCoord = zRefCoord; yOffset = zOffset;
			} else if (yCoord != null) {
				model = model3d.crossSectionAtY(yCoord);
				//relocate args for cascading
				yCoord = zCoord; yRange = zRange; //yRefCoord = zRefCoord; yOffset = zOffset;
			} else if (zCoord != null) {
				model = model3d.crossSectionAtZ(zCoord);
			} /*else if (xRefCoord != null) {
				switch (xRefCoord) {
				case "y":
					break;
				case "z":
					break;
				default:
					
				}
			}*/ else if (xRange != null && yRange != null && zRange != null) {
				model = model3d.subsection(xRange[0], xRange[1], yRange[0], yRange[1], zRange[0], zRange[1]);
			} else if (xRange != null || yRange != null || zRange != null) {
				System.out.println(onlyRangesOnAllCoordsAreSupported);
				model = null;
			}
		}
		if (model instanceof Model2D) {			
			if (xRange != null && yRange != null) {
				model = ((Model2D)model).subsection(xRange[0], xRange[1], yRange[0], yRange[1]);
			} else if (xRange != null || yRange != null) {
				System.out.println(onlyRangesOnAllCoordsAreSupported);
				model = null;
			} else if (xCoord != null || yCoord != null || xRefCoord != null || yRefCoord != null) {
				System.out.println(unsupportedDimensionCount);
				model = null;
			}
		}
		return model;
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
				model = getSIVModel(args);
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
	
	private static Model getSIVModel(Args args) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.memorySafe) {
			System.out.printf(memorySafeNotSupportedForThisModelMessageFormat, args.model);
		} else if (args.initialConfiguration == null && args.backupToRestorePath == null) {
			System.out.printf(initialConfigNeededMessageFormat, args.model);
		} else if (args.backupToRestorePath != null && args.grid == null) {
			System.out.printf(gridTypeNeededToRestoreMessageFormat);
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
									model = new SpreadIntegerValue1D(args.initialConfiguration.singleSource.longValue(), 0); //TODO support background value?
								} else {
									System.out.printf(singleSourceOutOfRangeMessageFormat, Long.MIN_VALUE, Long.MAX_VALUE);
								}								
							} else {
								System.out.printf(initialConfigNotSupportedMessageFormat, args.model);
							}
						} else {
							model = new SpreadIntegerValue1D(args.backupToRestorePath);
						}
					} else {
						System.out.printf(gridNotSupportedMessageFormat, args.model);
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
									model = new SpreadIntegerValue2D(args.initialConfiguration.singleSource.longValue(), 0);
								} else {
									System.out.printf(singleSourceOutOfRangeMessageFormat, Long.MIN_VALUE, Long.MAX_VALUE);
								}
							} else {
								System.out.printf(initialConfigNotSupportedMessageFormat, args.model);
							}
						} else {
							try {
								model = new IntSpreadIntegerValue2D(args.backupToRestorePath);
							} catch (Exception ex) {
								model = new SpreadIntegerValue2D(args.backupToRestorePath);
							}
						}
					} else {
						System.out.printf(gridNotSupportedMessageFormat, args.model);
					}
					break;
				case 3:
					if (args.grid.side == null) {
						if (args.backupToRestorePath == null) {
							if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
								if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Long.MAX_VALUE)) <= 0
										&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Long.MIN_VALUE)) >= 0) {
									model = new SpreadIntegerValue3D(args.initialConfiguration.singleSource.longValue());
								} else {
									System.out.printf(singleSourceOutOfRangeMessageFormat, Long.MIN_VALUE, Long.MAX_VALUE);
								}
							} else {
								System.out.printf(initialConfigNotSupportedMessageFormat, args.model);
							}
						} else {
							model = new SpreadIntegerValue3D(args.backupToRestorePath);
						}
					} else {
						System.out.printf(gridNotSupportedMessageFormat, args.model);
					}
					break;
				case 4:
					if (args.grid.side == null) {
						if (args.backupToRestorePath == null) {
							if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
								if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Long.MAX_VALUE)) <= 0
										&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Long.MIN_VALUE)) >= 0) {
									model = new SpreadIntegerValue4D(args.initialConfiguration.singleSource.longValue());
								} else {
									System.out.printf(singleSourceOutOfRangeMessageFormat, Long.MIN_VALUE, Long.MAX_VALUE);
								}
							} else {
								System.out.printf(initialConfigNotSupportedMessageFormat, args.model);
							}
						} else {
							model = new SpreadIntegerValue4D(args.backupToRestorePath);
						}
					} else {
						System.out.printf(gridNotSupportedMessageFormat, args.model);
					}
					break;
				default:
					System.out.printf(gridNotSupportedMessageFormat, args.model);
			}
		}
		return model;
	}

	private static Model getAbelianSandpileModel(Args args) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.memorySafe) {
			System.out.printf(memorySafeNotSupportedForThisModelMessageFormat, args.model);
		} else if (args.initialConfiguration == null && args.backupToRestorePath == null) {
			System.out.printf(initialConfigNeededMessageFormat, args.model);
//		} else if (args.backupToRestorePath != null && args.grid == null) { //uncomment if more grid types become supported
//			System.out.printf(gridTypeNeededToRestoreMessageFormat);
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
									model = new AbelianSandpileSingleSource2D(args.initialConfiguration.singleSource.intValue());
								} else {
									System.out.printf(singleSourceOutOfRangeMessageFormat, 0, Integer.MAX_VALUE);
								}
							} else {
								System.out.printf(initialConfigNotSupportedMessageFormat, args.model);
							}
						} else {
							model = new AbelianSandpileSingleSource2D(args.backupToRestorePath);
						}
					} else {
						System.out.printf(gridNotSupportedMessageFormat, args.model);
					}
					break;
				default:
					System.out.printf(gridNotSupportedMessageFormat, args.model);
			}
		}
		return model;
	}
	
	private static Model getAetherModel(Args args) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.initialConfiguration == null && args.backupToRestorePath == null) {
			System.out.printf(initialConfigNeededMessageFormat, args.model);
		} else if (args.backupToRestorePath != null && args.grid == null) {
			System.out.printf(gridTypeNeededToRestoreMessageFormat);
		} else if (args.memorySafe && args.initialConfiguration.type != InitialConfigType.SINGLE_SOURCE) {
			System.out.printf(memorySafeNotSupportedForThisInitialConfigMessageFormat, args.model);
		} else {
			if (args.grid == null) {
				args.grid = new GridOptionValue(2);//default to 2D
			}
			switch (args.grid.dimension) {
				case 1:
					if (args.grid.side == null) {
						if (args.backupToRestorePath == null) {
							if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
								if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Aether1D.MAX_INITIAL_VALUE)) <= 0
										&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Aether1D.MIN_INITIAL_VALUE)) >= 0) {
									if (args.memorySafe) {
										model = new FileBackedAether1D(args.initialConfiguration.singleSource.longValue(), args.path);
									} else {
										model = new Aether1D(args.initialConfiguration.singleSource.longValue());
									}
								} else {
									System.out.printf(singleSourceOutOfRangeMessageFormat, Aether1D.MIN_INITIAL_VALUE, Aether1D.MAX_INITIAL_VALUE);
								}								
							} else {
								System.out.printf(initialConfigNotSupportedMessageFormat, args.model);
							}
						} else {
							if (args.memorySafe) {
								model = new FileBackedAether1D(args.backupToRestorePath, args.path);
							} else {
								model = new Aether1D(args.backupToRestorePath);
							}
						}
					} else {
						System.out.printf(gridNotSupportedMessageFormat, args.model);
					}
					break;
				case 2:
					if (args.grid.side == null) {
						if (args.backupToRestorePath == null) {
							if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
								if (args.memorySafe) {
									if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedAether2D.MAX_INITIAL_VALUE)) <= 0
											&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedAether2D.MIN_INITIAL_VALUE)) >= 0) {
										model = new FileBackedAether2D(args.initialConfiguration.singleSource.longValue(), args.path);
									} else {
										System.out.printf(singleSourceOutOfRangeMessageFormat, Aether2D.MIN_INITIAL_VALUE, Aether2D.MAX_INITIAL_VALUE);
									}	
								} else {
									if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Aether2D.MAX_INITIAL_VALUE)) <= 0
											&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Aether2D.MIN_INITIAL_VALUE)) >= 0) {
										model = new Aether2D(args.initialConfiguration.singleSource.longValue());
									} else {
										model = new BigIntAether2D(args.initialConfiguration.singleSource);
									}
								}								
							} else {
								if (args.initialConfiguration.min.compareTo(BigInt.valueOf(Integer.MAX_VALUE)) <= 0
										&& args.initialConfiguration.min.compareTo(BigInt.valueOf(Integer.MIN_VALUE)) >= 0
										&& args.initialConfiguration.max.compareTo(BigInt.valueOf(Integer.MAX_VALUE)) <= 0
										&& args.initialConfiguration.max.compareTo(BigInt.valueOf(Integer.MIN_VALUE)) >= 0) {
									model = new IntAether2DRandomConfiguration(args.initialConfiguration.side, args.initialConfiguration.min.intValue(), args.initialConfiguration.max.intValue());
								} else {
									System.out.printf(minAndMaxOutOfRangeMessageFormat, Integer.MIN_VALUE, Integer.MAX_VALUE);
								}
							}
						} else {
							if (args.memorySafe) {
								model = new FileBackedAether2D(args.backupToRestorePath, args.path);
							} else {	
								try {		
									model = new Aether2D(args.backupToRestorePath);			
								} catch (Exception ex) {
									try {
										model = new BigIntAether2D(args.backupToRestorePath);							
									} catch (Exception ex3) {
										model = new IntAether2DRandomConfiguration(args.backupToRestorePath);								
									}						
								}	
							}
						}
					} else {
						System.out.printf(gridNotSupportedMessageFormat, args.model);
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
										System.out.printf(singleSourceOutOfRangeMessageFormat, FileBackedAether3D.MIN_INITIAL_VALUE, FileBackedAether3D.MAX_INITIAL_VALUE);
									}
								} else {
									if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether3D.MAX_INITIAL_VALUE)) <= 0
											&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether3D.MIN_INITIAL_VALUE)) >= 0) {
										model = new IntAether3D(args.initialConfiguration.singleSource.intValue());
									} else if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Aether3D.MAX_INITIAL_VALUE)) <= 0
											&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Aether3D.MIN_INITIAL_VALUE)) >= 0) {
										model = new Aether3D(args.initialConfiguration.singleSource.longValue());
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
									System.out.printf(minAndMaxOutOfRangeMessageFormat, Integer.MIN_VALUE, Integer.MAX_VALUE);
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
										model = new Aether3D(args.backupToRestorePath);							
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
								if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Aether3DCubicGrid.MAX_INITIAL_VALUE)) <= 0
										&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Aether3DCubicGrid.MIN_INITIAL_VALUE)) >= 0) {
									model = new Aether3DCubicGrid(args.grid.side, args.initialConfiguration.singleSource.longValue());
								} else {
									model = new BigIntAether3DCubicGrid(args.grid.side, args.initialConfiguration.singleSource);
								}
							} else {
								System.out.printf(initialConfigNotSupportedMessageFormat, args.model);
							}
						} else {
							try {
								model = new Aether3DCubicGrid(args.backupToRestorePath);							
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
								//TODO use swap implementations depending on asymmetric and single source options and available heap space?
								//long heapFreeSize = Runtime.getRuntime().freeMemory();
								if (args.memorySafe) {
									if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedAether4D.MAX_INITIAL_VALUE)) <= 0
											&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedAether4D.MIN_INITIAL_VALUE)) >= 0) {
										model = new FileBackedAether4D(args.initialConfiguration.singleSource.longValue(), args.path);
									} else {
										System.out.printf(singleSourceOutOfRangeMessageFormat, FileBackedAether4D.MIN_INITIAL_VALUE, FileBackedAether4D.MAX_INITIAL_VALUE);
									}
								} else {
									if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether4D.MAX_INITIAL_VALUE)) <= 0
											&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether4D.MIN_INITIAL_VALUE)) >= 0) {
										model = new IntAether4D(args.initialConfiguration.singleSource.intValue());
									} else if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Aether4D.MAX_INITIAL_VALUE)) <= 0
											&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Aether4D.MIN_INITIAL_VALUE)) >= 0) {
										model = new Aether4D(args.initialConfiguration.singleSource.longValue());
									} else {
										model = new BigIntAether4D(args.initialConfiguration.singleSource);
									}		
								}						
							} else {
								System.out.printf(initialConfigNotSupportedMessageFormat, args.model);
							}
						} else {
							if (args.memorySafe) {
								model = new FileBackedAether4D(args.backupToRestorePath, args.path);
							} else {	
								try {
									model = new Aether4D(args.backupToRestorePath);							
								} catch (Exception ex1) {
									model = new BigIntAether4D(args.backupToRestorePath);			
								}
							}
						}
					} else {
						System.out.printf(gridNotSupportedMessageFormat, args.model);
					}
					break;
				case 5:
					if (args.grid.side == null) {
						if (args.backupToRestorePath == null) {
							if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
								//TODO use swap implementations depending on asymmetric and single source options and available heap space?
								//long heapFreeSize = Runtime.getRuntime().freeMemory();
								if (args.memorySafe) {
									if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedAether5D.MAX_INITIAL_VALUE)) <= 0
											&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedAether5D.MIN_INITIAL_VALUE)) >= 0) {
										model = new FileBackedAether5D(args.initialConfiguration.singleSource.longValue(), args.path);
									} else {
										System.out.printf(singleSourceOutOfRangeMessageFormat, FileBackedAether5D.MIN_INITIAL_VALUE, FileBackedAether5D.MAX_INITIAL_VALUE);
									}
								} else {
									if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether5D.MAX_INITIAL_VALUE)) <= 0
											&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether5D.MIN_INITIAL_VALUE)) >= 0) {
										model = new IntAether5D(args.initialConfiguration.singleSource.intValue());
									} else if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Aether5D.MAX_INITIAL_VALUE)) <= 0
											&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Aether5D.MIN_INITIAL_VALUE)) >= 0) {
										model = new Aether5D(args.initialConfiguration.singleSource.longValue());
									} else {
										System.out.printf(singleSourceOutOfRangeMessageFormat, Aether5D.MIN_INITIAL_VALUE, Aether5D.MAX_INITIAL_VALUE);
									}
								}
							} else {
								System.out.printf(initialConfigNotSupportedMessageFormat, args.model);
							}
						} else {
							if (args.memorySafe) {
								model = new FileBackedAether5D(args.backupToRestorePath, args.path);
							} else {	
								try {
									model = new IntAether5D(args.backupToRestorePath);							
								} catch (Exception ex1) {
									model = new Aether5D(args.backupToRestorePath);			
								}
							}
						}
					} else {
						System.out.printf(gridNotSupportedMessageFormat, args.model);
					}
					break;
				default:
					System.out.printf(gridNotSupportedMessageFormat, args.model);
			}
		}
		return model;
	}

	private static Model getNearAether1Model(Args args) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.memorySafe) {
			System.out.printf(memorySafeNotSupportedForThisModelMessageFormat, args.model);
		} else if (args.initialConfiguration == null && args.backupToRestorePath == null) {
			System.out.printf(initialConfigNeededMessageFormat, args.model);
//		} else if (args.backupToRestorePath != null && args.grid == null) { //uncomment if more grid types become supported
//			System.out.printf(gridTypeNeededToRestoreMessageFormat);
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
									System.out.printf(singleSourceOutOfRangeMessageFormat, IntNearAether1_3D.MIN_INITIAL_VALUE, IntNearAether1_3D.MAX_INITIAL_VALUE);
								}
							} else {
								System.out.printf(initialConfigNotSupportedMessageFormat, args.model);
							}
						} else {
							model = new IntNearAether1_3D(args.backupToRestorePath);
						}
					} else {
						System.out.printf(gridNotSupportedMessageFormat, args.model);
					}
					break;
				default:
					System.out.printf(gridNotSupportedMessageFormat, args.model);
			}
		}
		return model;
	}
	
	private static Model getNearAether2Model(Args args) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.memorySafe) {
			System.out.printf(memorySafeNotSupportedForThisModelMessageFormat, args.model);
		} else if (args.memorySafe) {
			System.out.printf(memorySafeNotSupportedForThisModelMessageFormat, args.model);
		} else if (args.initialConfiguration == null && args.backupToRestorePath == null) {
			System.out.printf(initialConfigNeededMessageFormat, args.model);
//		} else if (args.backupToRestorePath != null && args.grid == null) { //uncomment if more grid types become supported
//			System.out.printf(gridTypeNeededToRestoreMessageFormat);
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
									System.out.printf(singleSourceOutOfRangeMessageFormat, IntNearAether1_3D.MIN_INITIAL_VALUE, IntNearAether1_3D.MAX_INITIAL_VALUE);
								}
							} else {
								System.out.printf(initialConfigNotSupportedMessageFormat, args.model);
							}
						} else {
							model = new IntNearAether2_3D(args.backupToRestorePath);
						}
					} else {
						System.out.printf(gridNotSupportedMessageFormat, args.model);
					}
					break;
				default:
					System.out.printf(gridNotSupportedMessageFormat, args.model);
			}
		}
		return model;
	}
	
	private static Model getNearAether3Model(Args args) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.memorySafe) {
			System.out.printf(memorySafeNotSupportedForThisModelMessageFormat, args.model);
		} else if (args.initialConfiguration == null && args.backupToRestorePath == null) {
			System.out.printf(initialConfigNeededMessageFormat, args.model);
//		} else if (args.backupToRestorePath != null && args.grid == null) { //uncomment if more grid types become supported
//			System.out.printf(gridTypeNeededToRestoreMessageFormat);
		} else {
			if (args.grid == null) {
				args.grid = new GridOptionValue(3);//default
			}
			switch (args.grid.dimension) {
				case 3:
					if (args.grid.side == null) {
						if (args.backupToRestorePath == null) {
							if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
								model = new BigIntNearAether3Simple3D(args.initialConfiguration.singleSource);
							} else {
								System.out.printf(initialConfigNotSupportedMessageFormat, args.model);
							}
						} else {
							model = new BigIntNearAether3Simple3D(args.backupToRestorePath);
						}
					} else {
						System.out.printf(gridNotSupportedMessageFormat, args.model);
					}
					break;
				default:
					System.out.printf(gridNotSupportedMessageFormat, args.model);
			}
		}
		return model;
	}
	
}
