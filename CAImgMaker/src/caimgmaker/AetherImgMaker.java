/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2024 Jaume Ribas

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
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.SortedMap;
import org.apache.commons.io.FileUtils;

import com.beust.jcommander.JCommander;

import caimgmaker.args.Args;
import caimgmaker.args.CoordinateFilters;
import caimgmaker.args.CustomUsageFormatter;
import caimgmaker.colormap.ColorMapper;
import caimgmaker.colormap.GrayscaleMapper;
import caimgmaker.colormap.HueMapper;
import cellularautomata.PartialCoordinates;
import cellularautomata.Utils;
import cellularautomata.model.IntModel;
import cellularautomata.model.Model;
import cellularautomata.model.SymmetricModel;
import cellularautomata.model2d.BooleanModel2D;
import cellularautomata.model2d.IntModel2D;
import cellularautomata.model2d.IntModelAs2D;
import cellularautomata.model2d.LongModel2D;
import cellularautomata.model2d.NumericModel2D;
import cellularautomata.model3d.BooleanModel3D;
import cellularautomata.model3d.IntModel3D;
import cellularautomata.model3d.IntModelAs3D;
import cellularautomata.model3d.LongModel3D;
import cellularautomata.model3d.Model3D;
import cellularautomata.model3d.ModelAs3D;
import cellularautomata.model3d.NumericModel3D;
import cellularautomata.numbers.BigInt;

public class AetherImgMaker {
	
	public static ResourceBundle messages;
			
	public static void main(String[] rawArgs) throws Exception {
//		String debugArgs = "92233720368547758079999 -path D:/data/test";//debug
//		debugArgs = "-help";//debug
//		rawArgs = debugArgs.split(" ");//debug
		InputReaderTask inputReader = null;
		Thread inputThread = null;
		try {
			messages = ResourceBundle.getBundle("MessagesBundle", Locale.getDefault());
			inputReader = new InputReaderTask(messages);
			inputThread = new Thread(inputReader);
			inputThread.start();
			Args args = new Args();
			JCommander jcommander = JCommander.newBuilder()
					.programName(Args.PROGRAM_INVOCATION)
					.addObject(args)
					.build();
			jcommander.setUsageFormatter(new CustomUsageFormatter(jcommander));
			jcommander.setCaseSensitiveOptions(false);
			jcommander.parse(rawArgs);
			if (args.help) {
				jcommander.usage();
				return;
			}
			if (!mergeInitialConfigParameters(args)) {
				System.out.printf(messages.getString("use-help-format"), Args.HELP);
				return;
			}
			if (args.outputVersion) {
				System.out.println("0.9.0");
				return;
			}
			Model model = getModel(args);
			if (model == null) {
				System.out.printf(messages.getString("use-help-format"), Args.HELP);
				return;
			}
			int lastCharacterInPathIndex = args.path.length() - 1;
			char lastCharacterInPath = args.path.charAt(lastCharacterInPathIndex);
			if (lastCharacterInPath == '/' || lastCharacterInPath == '\\') {
				args.path = args.path.substring(0, lastCharacterInPathIndex);
			}
			String path = args.path;
			if (!args.noFolders) {
				path += "/" + model.getSubfolderPath();
			}
			String backupsPath = path + "/backups";
			evolveModelToFirstStep(model, args, backupsPath, inputReader);
			Model modelSection = getModelSection(model, args);
			if (modelSection == null) {
				System.out.printf(messages.getString("use-help-format"), Args.HELP);
				return;
			}
			if (args.backupToRestorePath == null)
				FileUtils.writeStringToFile(
						new File(path + "/parameters.txt"), 
						new Timestamp(System.currentTimeMillis()).toString() + "\t" + String.join(" ", rawArgs) + System.lineSeparator(), 
						Charset.forName("UTF8"), 
						true);
			boolean success = generateImages(modelSection, args, backupsPath, inputReader);
			if (success) {
				System.out.println(messages.getString("finished"));
			} else {
				System.out.printf(messages.getString("use-help-format"), Args.HELP);
			}
		} catch (Exception ex) {
			String message = ex.getLocalizedMessage();
			if (message == null) {
				System.out.println(messages.getString("unexpected-error"));
				ex.printStackTrace();
			} else {
				System.out.println(message);
			}
			System.out.printf(messages.getString("use-help-format"), Args.HELP);
//			throw ex;//debug
		} finally {
			if (inputReader != null) {
				inputReader.stop();
			}
			if (inputThread != null) {
				inputThread.join();
			}
		}
	}
	
	private static boolean mergeInitialConfigParameters(Args args) {
		boolean succeeded = true;
		if (args.initialConfiguration2 != null) {
			if (args.initialConfiguration != null) {
				System.out.println(messages.getString("only-one-initial-config-allowed"));
				succeeded = false;
			} else {
				args.initialConfiguration = args.initialConfiguration2;
			}
		}
		return succeeded;
	}
	
	private static boolean generateImages(Model model, Args args, String backupsPath, InputReaderTask inputReader) throws Exception {
		ColorMapper colorMapper = getColorMapper(args);
		if (colorMapper == null)
			return false;
		String imagesName = args.imgName;
		if (imagesName == null) {
			imagesName = model.getName() + "_";
		}
		String imagesPath = args.path;
		if (!args.noFolders) {
			imagesPath += "/" + model.getSubfolderPath();
			if (args.steapLeap > 1) {
				imagesPath += "/stepleap=" + args.steapLeap;
			}
			imagesPath += "/img/" + colorMapper.getColormapName();
		}
		ImgMaker imgMaker = null;
		if (args.millisBetweenBackups == null) {
			imgMaker = new ImgMaker(messages, inputReader);
		} else {
			imgMaker = new ImgMaker(messages, inputReader, args.millisBetweenBackups);
		}
		boolean error = false;
		int dimension = model.getGridDimension();
		switch (dimension) {
			case 2:
				if (model instanceof BooleanModel2D) {
					BooleanModel2D castedModel = (BooleanModel2D)model;
					switch (args.imgGenerationMode) {
						case TOPPLING_ALTERNATION_COMPLIANCE:	
						case NORMAL: imgMaker.createImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap);
							break;
						case SPLIT_COORDINATE_PARITY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, false, false);
							break;
						case EVEN_COORDINATES_ONLY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, false, true);
							break;
						case ODD_COORDINATES_ONLY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, true, false);
							break;
						default: 
							System.out.println(messages.getString("unknown-img-gen-mode"));
							error = true;
					}				
				} else if (model instanceof IntModel2D) {
					IntModel2D castedModel = (IntModel2D)model;
					switch (args.imgGenerationMode) {
						case TOPPLING_ALTERNATION_COMPLIANCE:	
						case NORMAL: imgMaker.createImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap);
							break;
						case SPLIT_COORDINATE_PARITY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, false, false);
							break;
						case EVEN_COORDINATES_ONLY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, false, true);
							break;
						case ODD_COORDINATES_ONLY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, true, false);
							break;
						default: 
							System.out.println(messages.getString("unknown-img-gen-mode"));
							error = true;
					}				
				} else if (model instanceof LongModel2D) {
					LongModel2D castedModel = (LongModel2D)model;
					switch (args.imgGenerationMode) {
						case TOPPLING_ALTERNATION_COMPLIANCE:	
						case NORMAL: imgMaker.createImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap);
							break;
						case SPLIT_COORDINATE_PARITY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, false, false);
							break;
						case EVEN_COORDINATES_ONLY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, false, true);
							break;
						case ODD_COORDINATES_ONLY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, true, false);
							break;
						default: 
							System.out.println(messages.getString("unknown-img-gen-mode"));
							error = true;
					}	
				} else if (model instanceof NumericModel2D) {
					@SuppressWarnings("unchecked")
					NumericModel2D<BigInt> castedModel = (NumericModel2D<BigInt>)model;
					switch (args.imgGenerationMode) {
						case TOPPLING_ALTERNATION_COMPLIANCE:	
						case NORMAL: imgMaker.createImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap);
							break;
						case SPLIT_COORDINATE_PARITY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, false, false);
							break;
						case EVEN_COORDINATES_ONLY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, false, true);
							break;
						case ODD_COORDINATES_ONLY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, true, false);
							break;
						default: 
							System.out.println(messages.getString("unknown-img-gen-mode"));
							error = true;
					}
				} else if (model instanceof IntModel) {
					IntModel2D castedModel = new IntModelAs2D((IntModel)model);
					switch (args.imgGenerationMode) {
						case TOPPLING_ALTERNATION_COMPLIANCE:	
						case NORMAL: imgMaker.createImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap);
							break;
						case SPLIT_COORDINATE_PARITY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, false, false);
							break;
						case EVEN_COORDINATES_ONLY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, false, true);
							break;
						case ODD_COORDINATES_ONLY: imgMaker.createEvenOddImages(castedModel, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, true, false);
							break;
						default: 
							System.out.println(messages.getString("unknown-img-gen-mode"));
							error = true;
					}
				} else {
					System.out.printf(messages.getString("unsupported-model-section-format"), model.getClass().getName());
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
				if (model instanceof BooleanModel3D) {
					BooleanModel3D castedModel = (BooleanModel3D)model;
					switch (args.imgGenerationMode) {
					case TOPPLING_ALTERNATION_COMPLIANCE:	
					case NORMAL: imgMaker.createScanningAndZCrossSectionImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap);
						break;
					case SPLIT_COORDINATE_PARITY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, false, false);
						break;
					case EVEN_COORDINATES_ONLY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, false, true);
						break;
					case ODD_COORDINATES_ONLY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, true, false);
						break;
					default: 
						System.out.println(messages.getString("unknown-img-gen-mode"));
						error = true;
					}				
				} else if (model instanceof IntModel3D) {
					IntModel3D castedModel = (IntModel3D)model;
					switch (args.imgGenerationMode) {
						case TOPPLING_ALTERNATION_COMPLIANCE:	
						case NORMAL: imgMaker.createScanningAndZCrossSectionImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap);
							break;
						case SPLIT_COORDINATE_PARITY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, false, false);
							break;
						case EVEN_COORDINATES_ONLY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, false, true);
							break;
						case ODD_COORDINATES_ONLY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, true, false);
							break;
						default: 
							System.out.println(messages.getString("unknown-img-gen-mode"));
							error = true;
					}			
				} else if (model instanceof LongModel3D) {
					LongModel3D castedModel = (LongModel3D)model;
					switch (args.imgGenerationMode) {
						case TOPPLING_ALTERNATION_COMPLIANCE:	
						case NORMAL: imgMaker.createScanningAndZCrossSectionImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap);
							break;
						case SPLIT_COORDINATE_PARITY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, false, false);
							break;
						case EVEN_COORDINATES_ONLY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, false, true);
							break;
						case ODD_COORDINATES_ONLY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, true, false);
							break;
						default: 
							System.out.println(messages.getString("unknown-img-gen-mode"));
							error = true;
					}
				} else if (model instanceof NumericModel3D) {
					@SuppressWarnings("unchecked")
					NumericModel3D<BigInt> castedModel = (NumericModel3D<BigInt>)model;
					switch (args.imgGenerationMode) {
						case TOPPLING_ALTERNATION_COMPLIANCE:	
						case NORMAL: imgMaker.createScanningAndZCrossSectionImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap);
							break;
						case SPLIT_COORDINATE_PARITY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, false, false);
							break;
						case EVEN_COORDINATES_ONLY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, false, true);
							break;
						case ODD_COORDINATES_ONLY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, true, false);
							break;
						default: 
							System.out.println(messages.getString("unknown-img-gen-mode"));
							error = true;
					}
				} else if (model instanceof IntModel) {
					IntModel3D castedModel = new IntModelAs3D((IntModel)model);
					switch (args.imgGenerationMode) {
						case TOPPLING_ALTERNATION_COMPLIANCE:	
						case NORMAL: imgMaker.createScanningAndZCrossSectionImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap);
							break;
						case SPLIT_COORDINATE_PARITY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, false, false);
							break;
						case EVEN_COORDINATES_ONLY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, false, true);
							break;
						case ODD_COORDINATES_ONLY: imgMaker.createScanningAndZCrossSectionEvenOddImages(castedModel, scanCoords, crossSectionZ, colorMapper, args.minimumImageSize.width, args.minimumImageSize.height, imagesPath, imagesName, backupsPath, args.steapLeap, true, false);
							break;
						default: 
							System.out.println(messages.getString("unknown-img-gen-mode"));
							error = true;
					}
				} else {
					System.out.printf(messages.getString("unsupported-model-section-format"), model.getClass().getName());
					error = true;
				}
				break;
			default:
				System.out.printf(messages.getString("unsupported-dimension-format"), dimension, Args.COORDIANTE_FILTERS, Args.GRID);
				error = true;						
		}
		return !error;
	}
	
	private static void backUp(Model model, long step, String backupsPath) throws Exception {
		String backupName = model.getName() + "_" + step + "_" + Utils.getFileNameSafeTimeStamp();
		System.out.printf(messages.getString("backing-up-instance-format"), backupsPath + "/" + backupName);
		model.backUp(backupsPath, backupName);		
		System.out.println(messages.getString("backing-up-finished"));
	}

	private static void evolveModelToFirstStep(Model model, Args args, String backupsPath, InputReaderTask inputReader) throws Exception {
		long step = model.getStep();
		if (args.firstStep > step) {
			System.out.printf(messages.getString("evolving-model-to-step-format"), args.firstStep);
			Boolean changed;
			String stepNameAndEquals = messages.getString("step") + " = ";
			if (args.millisBetweenBackups != null) {
				long nextBckTime = System.currentTimeMillis() + args.millisBetweenBackups;
				do {
					System.out.println(stepNameAndEquals + step);
					changed = model.nextStep();
					step++;
					boolean backUp = false;
					if (System.currentTimeMillis() >= nextBckTime) {
						backUp = true;
						nextBckTime += args.millisBetweenBackups;
					}
					if (inputReader.backupRequested) {
						backUp = true;
						inputReader.backupRequested = false;
					}
					if (backUp) {
						backUp(model, step, backupsPath);
					}
				} while ((changed == null || changed) && step < args.firstStep);
			} else {
				do {
					System.out.println(stepNameAndEquals + step);
					changed = model.nextStep();
					step++;
					if (inputReader.backupRequested) {
						inputReader.backupRequested = false;
						backUp(model, step, backupsPath);
					}
				} while ((changed == null || changed) && step < args.firstStep);
			}
		}		
	}

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
				System.out.printf(messages.getString("invalid-coord-index-format"), dimension);
				return null;
			}
			int relativeFilterGroupCount = filters.relativeFilterGroups.size();
			List<List<Integer>> relativeFilterGroupsCoords = new ArrayList<List<Integer>>(relativeFilterGroupCount);
			for (int i = 0; i != relativeFilterGroupCount; i++) {
				SortedMap<Integer, int[]> group = filters.relativeFilterGroups.get(i);
				List<Integer> groupCoords = new ArrayList<Integer>(group.keySet());
				if (groupCoords.get(group.size() - 1) >= dimension) {
					System.out.printf(messages.getString("invalid-coord-index-format"), dimension);
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
				System.out.printf(messages.getString("colormap-not-recognized-format"), args.colormap);
		}
		return colorMapper;
	}
	
	private static Model getModel(Args args) throws Exception {
		Model model = null;
		String lowerCaseModelName = args.model.toLowerCase();
		switch (lowerCaseModelName) {
			case "ae":
			case "aether":
				model = AetherFactory.create(args, messages);
				break;
			case "sunflower":
				model = SunflowerFactory.create(args, messages);
				break;
			case "as":
			case "abelian_sandpile":
				model = AbelianSandpileFactory.create(args, messages);
				break;
			case "nearae1":
			case "nearaether1":
				model = NearAether1Factory.create(args, messages);
				break;
			case "nearae2":
			case "nearaether2":
				model = NearAether2Factory.create(args, messages);
				break;
			case "nearae3":
			case "nearaether3":
				model = NearAether3Factory.create(args, messages);
				break;
			default:
				System.out.printf(messages.getString("model-not-recognized-format"), args.model);
		}
		return model;
	}
	
}
