/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2021 Jaume Ribas

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
package cellularautomata.automata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;

import cellularautomata.evolvinggrid.ActionableEvolvingGrid4D;
import cellularautomata.grid4d.AnisotropicIntGrid4DSlice;
import cellularautomata.grid4d.IntGrid4D;
import cellularautomata.grid4d.IntSubGrid4DWithWBounds;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 4D with a single source initial configuration
 * 
 * @author Jaume
 *
 */
public class IntAether4DAsymmetricSectionSwap extends ActionableEvolvingGrid4D<IntGrid4D> {
	
	public static final int MAX_INITIAL_VALUE = Integer.MAX_VALUE;
	public static final int MIN_INITIAL_VALUE = -613566757;

	private static final String PROPERTIES_BACKUP_FILE_NAME = "properties.ser";
	private static final String GRID_FOLDER_NAME = "grid";

	private static final byte W_POSITIVE = 0;
	private static final byte W_NEGATIVE = 1;
	private static final byte X_POSITIVE = 2;
	private static final byte X_NEGATIVE = 3;
	private static final byte Y_POSITIVE = 4;
	private static final byte Y_NEGATIVE = 5;
	private static final byte Z_POSITIVE = 6;
	private static final byte Z_NEGATIVE = 7;
	
	private SizeLimitedAnisotropicIntGrid4DBlock gridBlockA;
	private SizeLimitedAnisotropicIntGrid4DBlock gridBlockB;
	private int initialValue;
	private int currentStep;
	private int maxW, maxX, maxY, maxZ;
	private File gridFolder;
	private long maxGridBlockSize;
	
	/**
	 * 
	 * @param initialValue
	 * @param maxGridHeapSize the maximum amount of heap space the grid can take up in bytes. This won't limit the total size of the grid.
	 * @param folderPath
	 * @throws Exception
	 */
	public IntAether4DAsymmetricSectionSwap(int initialValue, long maxGridHeapSize, String folderPath) throws Exception {
		if (initialValue < MIN_INITIAL_VALUE) {//to prevent overflow of int type
			throw new IllegalArgumentException("Initial value cannot be smaller than -613,566,757. Use a greater initial value or a different implementation.");
		}
		this.initialValue = initialValue;
		maxGridBlockSize = maxGridHeapSize/2;
		gridBlockA = new SizeLimitedAnisotropicIntGrid4DBlock(0, maxGridBlockSize);
		gridBlockA.setValueAtPosition(0, 0, 0, 0, initialValue);
		maxW = 1;//we leave a buffer of one position to account for 'negative growth'
		maxX = 0;
		maxY = 0;
		maxZ = 0;
		currentStep = 0;
		gridFolder = new File(folderPath + File.separator + getSubFolderPath() + File.separator + GRID_FOLDER_NAME);
		if (!gridFolder.exists()) {
			gridFolder.mkdirs();
		} else {
			FileUtils.cleanDirectory(gridFolder);
		}
	}
	
	/**
	 * Creates an instance restoring a backup
	 * 
	 * @param backupPath the path to the backup file to restore.
	 * @param folderPath
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 */
	public IntAether4DAsymmetricSectionSwap(String backupPath, String folderPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		gridFolder = new File(backupPath + File.separator + GRID_FOLDER_NAME);
		if (!gridFolder.exists()) {
			throw new FileNotFoundException("Missing grid folder at '" + gridFolder.getAbsolutePath() + "'");
		}
		gridBlockA = loadGridBlock(0);
		@SuppressWarnings("unchecked")
		HashMap<String, Object> properties = 
				(HashMap<String, Object>) Utils.deserializeFromFile(backupPath + File.separator + PROPERTIES_BACKUP_FILE_NAME);
		setPropertiesFromMap(properties);
		File gridBackupFolder = gridFolder;
		gridFolder = new File(folderPath + File.separator + getSubFolderPath() + File.separator + GRID_FOLDER_NAME);
		if (gridFolder.exists()) {
			FileUtils.cleanDirectory(gridFolder);
		}
		FileUtils.copyDirectory(gridBackupFolder, gridFolder);
		if (maxW > gridBlockA.maxW) {
			gridBlockB = loadGridBlock(gridBlockA.maxW + 1);
		}
	}

	private SizeLimitedAnisotropicIntGrid4DBlock loadGridBlockSafe(int minX) throws IOException, ClassNotFoundException {
		File[] files = gridFolder.listFiles();
		boolean found = false;
		SizeLimitedAnisotropicIntGrid4DBlock gridBlock = null;
		File gridBlockFile = null;
		for (int i = 0; i < files.length && !found; i++) {
			File currentFile = files[i];
			String fileName = currentFile.getName();
			int fileMinW;
			try {
				//"minW=".length() == 5
				fileMinW = Integer.parseInt(fileName.substring(5, fileName.indexOf("_")));
				if (fileMinW == minX) {
					found = true;
					gridBlockFile = currentFile;
				}
			} catch (NumberFormatException ex) {
				
			}
		}
		if (found) {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(gridBlockFile));
			gridBlock = (SizeLimitedAnisotropicIntGrid4DBlock) in.readObject();
			in.close();
		}
		return gridBlock;
	}
	
	private SizeLimitedAnisotropicIntGrid4DBlock loadGridBlock(int minW) throws IOException, ClassNotFoundException {
		SizeLimitedAnisotropicIntGrid4DBlock gridBlock = loadGridBlockSafe(minW);
		if (gridBlock == null) {
			throw new FileNotFoundException("No grid block with minW=" + minW + " could be found at folder path \"" + gridFolder.getAbsolutePath() + "\".");
		} else {
			return gridBlock;
		}
	}
	
	private void saveGridBlock(SizeLimitedAnisotropicIntGrid4DBlock gridBlock) throws FileNotFoundException, IOException {
		String name = "minW=" + gridBlock.minW + "_maxW=" + gridBlock.maxW + ".ser";
		String pathName = this.gridFolder.getPath() + File.separator + name;
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pathName));
		out.writeObject(gridBlock);
		out.flush();
		out.close();
	}
	
	private SizeLimitedAnisotropicIntGrid4DBlock loadOrBuildGridBlock(int minW) throws ClassNotFoundException, IOException {		
		SizeLimitedAnisotropicIntGrid4DBlock gridBlock = loadGridBlockSafe(minW);
		if (gridBlock != null) {
			return gridBlock;
		} else {
			return new SizeLimitedAnisotropicIntGrid4DBlock(minW, maxGridBlockSize);
		}
	}

	@Override
	public boolean nextStep() throws Exception {
		triggerBeforeProcessing();
		boolean gridChanged = false;		
		AnisotropicIntGrid4DSlice[] newGridSlices = 
				new AnisotropicIntGrid4DSlice[] {
						null, 
						new AnisotropicIntGrid4DSlice(0), 
						new AnisotropicIntGrid4DSlice(1)};
		if (gridBlockA.minW > 0) {
			if (gridBlockB != null && gridBlockB.minW == 0) {
				SizeLimitedAnisotropicIntGrid4DBlock swp = gridBlockA;
				gridBlockA = gridBlockB;
				gridBlockB = swp;
			} else {
				saveGridBlock(gridBlockA);
				gridBlockA.free();
				gridBlockA = loadGridBlock(0);
			}
		}
		int currentMaxW = maxW;//it can change during computing
		int w = 0;
		boolean anySlicePositionToppled = computeGridSlice(gridBlockA, 0, newGridSlices);
		gridChanged = gridChanged || anySlicePositionToppled;
		w++;
		while (w <= currentMaxW) {
			while (w <= currentMaxW && w < gridBlockA.maxW) {
				slideGridSlices(newGridSlices, new AnisotropicIntGrid4DSlice(w + 1));
				anySlicePositionToppled = computeGridSlice(gridBlockA, w, newGridSlices);
				gridChanged = gridChanged || anySlicePositionToppled;
				gridBlockA.setSlice(w - 1, newGridSlices[0]);
				w++;
			}
			if (w <= currentMaxW) {
				if (gridBlockB != null) {
					if (gridBlockB.minW != w + 1) {
						saveGridBlock(gridBlockB);
						gridBlockB.free();
						gridBlockB = loadOrBuildGridBlock(w + 1);
					}
				} else {
					gridBlockB = loadOrBuildGridBlock(w + 1);
				}
				slideGridSlices(newGridSlices, new AnisotropicIntGrid4DSlice(w + 1));
				anySlicePositionToppled = computeLastGridSlice(gridBlockA, gridBlockB, w, newGridSlices);
				gridChanged = gridChanged || anySlicePositionToppled;
				gridBlockA.setSlice(w - 1, newGridSlices[0]);
				w++;
				if (w <= currentMaxW) {
					slideGridSlices(newGridSlices, new AnisotropicIntGrid4DSlice(w + 1));
					anySlicePositionToppled = computeFirstGridSlice(gridBlockA, gridBlockB, w, newGridSlices);
					gridChanged = gridChanged || anySlicePositionToppled;
					gridBlockA.setSlice(w - 1, newGridSlices[0]);
					processGridBlock(gridBlockA);
					SizeLimitedAnisotropicIntGrid4DBlock swp = gridBlockA;
					gridBlockA = gridBlockB;
					gridBlockB = swp;
					w++;
				}
			}
		}
		gridBlockA.setSlice(currentMaxW, newGridSlices[1]);
		if (currentMaxW == gridBlockA.maxW) {
			processGridBlock(gridBlockA);
			gridBlockB.setSlice(currentMaxW + 1, newGridSlices[2]);
			processGridBlock(gridBlockB);
		} else {
			gridBlockA.setSlice(currentMaxW + 1, newGridSlices[2]);
			processGridBlock(gridBlockA);
		}
		currentStep++;
		triggerAfterProcessing();
		return gridChanged;
	}
	
	private void processGridBlock(SizeLimitedAnisotropicIntGrid4DBlock block) throws Exception {
		if (block.minW <= maxW) {
			IntGrid4D subBlock = null;
			if (block.maxW > maxW) {
				subBlock = new IntSubGrid4DWithWBounds(block, block.minW, maxW);
			} else {
				subBlock = block;
			}
			triggerProcessGridBlock(subBlock);
		}
	}
	
	@Override
	public void processGrid() throws Exception {
		triggerBeforeProcessing();
		if (gridBlockB == null) { //one block
			processGridBlock(gridBlockA);
		} else {
			if (gridBlockA.minW < gridBlockB.minW) {
				SizeLimitedAnisotropicIntGrid4DBlock swp = gridBlockA;
				gridBlockA = gridBlockB;
				gridBlockB = swp;
			}
			if (gridBlockB.minW == 0 && gridBlockA.maxW >= maxW
					&& gridBlockB.maxW == gridBlockA.minW - 1) {//two blocks
				processGridBlock(gridBlockB);
				processGridBlock(gridBlockA);
			} else { //more than two blocks
				//I keep the one closest to zero (in case it can be reused when computing the next step)
				//and save the other
				saveGridBlock(gridBlockA);
				if (gridBlockB.minW > 0) {
					gridBlockA.free();
					gridBlockA = loadGridBlock(0);
					processGridBlock(gridBlockA);
					while (gridBlockA.maxW < gridBlockB.minW - 1) {
						int nextW = gridBlockA.maxW + 1;
						gridBlockA.free();
						gridBlockA = loadGridBlock(nextW);
						processGridBlock(gridBlockA);
					}
				}
				processGridBlock(gridBlockB);
				int previousMaxW = gridBlockB.maxW;			
				while (previousMaxW < maxW) {
					int nextW = previousMaxW + 1;
					gridBlockA.free();
					gridBlockA = loadGridBlock(nextW);
					processGridBlock(gridBlockA);
					previousMaxW = gridBlockA.maxW;
				}
			}			
		}
		triggerAfterProcessing();
	}

	private void slideGridSlices(AnisotropicIntGrid4DSlice[] newGridSlices, AnisotropicIntGrid4DSlice newSlice) {
		newGridSlices[0] = newGridSlices[1];
		newGridSlices[1] = newGridSlices[2];
		newGridSlices[2] = newSlice;
	}
	
	private boolean computeGridSlice(SizeLimitedAnisotropicIntGrid4DBlock gridBlock, int w, AnisotropicIntGrid4DSlice[] newGridSlices) {
		return computeGridSlice(gridBlock, gridBlock, gridBlock, w, newGridSlices);
	}
	
	private boolean computeLastGridSlice(SizeLimitedAnisotropicIntGrid4DBlock leftGridBlock, SizeLimitedAnisotropicIntGrid4DBlock rightGridBlock,
			int w, AnisotropicIntGrid4DSlice[] newGridSlices) {
		return computeGridSlice(leftGridBlock, leftGridBlock, rightGridBlock, w, newGridSlices);
	}
	
	private boolean computeFirstGridSlice(SizeLimitedAnisotropicIntGrid4DBlock leftGridBlock, SizeLimitedAnisotropicIntGrid4DBlock rightGridBlock,
			int w, AnisotropicIntGrid4DSlice[] newGridSlices) {
		return computeGridSlice(leftGridBlock, rightGridBlock, rightGridBlock, w, newGridSlices);
	}

	private boolean computeGridSlice(SizeLimitedAnisotropicIntGrid4DBlock leftGridBlock, SizeLimitedAnisotropicIntGrid4DBlock centerGridBlock, 
			SizeLimitedAnisotropicIntGrid4DBlock rightGridBlock, int w, AnisotropicIntGrid4DSlice[] newGridSlices) {
		boolean anyPositionToppled = false;
		for (int x = 0; x <= w; x++) {
			for (int y = 0; y <= x; y++) {
				for (int z = 0; z <= y; z++) {				
					int value = centerGridBlock.getFromPosition(w, x, y, z);
					int upperWNeighborValue = getFromPosition(w, leftGridBlock, centerGridBlock, rightGridBlock, w + 1, x, y, z);
					int lowerWNeighborValue = getFromPosition(w, leftGridBlock, centerGridBlock, rightGridBlock, w - 1, x, y, z);
					int upperXNeighborValue = getFromPosition(w, leftGridBlock, centerGridBlock, rightGridBlock, w, x + 1, y, z);
					int lowerXNeighborValue = getFromPosition(w, leftGridBlock, centerGridBlock, rightGridBlock, w, x - 1, y, z);
					int upperYNeighborValue = getFromPosition(w, leftGridBlock, centerGridBlock, rightGridBlock, w, x, y + 1, z);
					int lowerYNeighborValue = getFromPosition(w, leftGridBlock, centerGridBlock, rightGridBlock, w, x, y - 1, z);
					int upperZNeighborValue = getFromPosition(w, leftGridBlock, centerGridBlock, rightGridBlock, w, x, y, z + 1);
					int lowerZNeighborValue = getFromPosition(w, leftGridBlock, centerGridBlock, rightGridBlock, w, x, y, z - 1);				
					boolean positionToppled = computePosition(value, 
							upperWNeighborValue, lowerWNeighborValue, 
							upperXNeighborValue, lowerXNeighborValue, 
							upperYNeighborValue, lowerYNeighborValue, 
							upperZNeighborValue, lowerZNeighborValue, 
							w, x, y, z, newGridSlices);
					anyPositionToppled = anyPositionToppled || positionToppled;
				}
			}
		}
		return anyPositionToppled;
	}
	
	private int getFromPosition(int centerW, SizeLimitedAnisotropicIntGrid4DBlock lowerWGridBlock, 
			SizeLimitedAnisotropicIntGrid4DBlock centerGridBlock, SizeLimitedAnisotropicIntGrid4DBlock upperWGridBlock, int w, int x, int y, int z) {
		int[] asymmetricCoords = getAsymmetricCoords(w, x, y, z);
		w = asymmetricCoords[0];
		x = asymmetricCoords[1];
		y = asymmetricCoords[2];
		z = asymmetricCoords[3];
		int value;
		if (w == centerW) {
			value = centerGridBlock.getFromPosition(w, x, y, z);
		} else if (w < centerW) {
			value = lowerWGridBlock.getFromPosition(w, x, y, z);
		} else {
			value = upperWGridBlock.getFromPosition(w, x, y, z);
		}
		return value;
	}
	
	private boolean computePosition(int value, 
			int upperWNeighborValue, int lowerWNeighborValue, 
			int upperXNeighborValue, int lowerXNeighborValue, 
			int upperYNeighborValue, int lowerYNeighborValue, 
			int upperZNeighborValue, int lowerZNeighborValue, 
			int w, int x, int y, int z, AnisotropicIntGrid4DSlice[] newGridSlices) {
		int[] neighborValues = new int[8];
		byte[] neighborDirections = new byte[8];
		int relevantNeighborCount = 0;
		if (upperWNeighborValue < value) {
			neighborValues[relevantNeighborCount] = upperWNeighborValue;
			neighborDirections[relevantNeighborCount] = W_POSITIVE;
			relevantNeighborCount++;
		}
		if (lowerWNeighborValue < value) {
			neighborValues[relevantNeighborCount] = lowerWNeighborValue;
			neighborDirections[relevantNeighborCount] = W_NEGATIVE;
			relevantNeighborCount++;
		}
		if (upperXNeighborValue < value) {
			neighborValues[relevantNeighborCount] = upperXNeighborValue;
			neighborDirections[relevantNeighborCount] = X_POSITIVE;
			relevantNeighborCount++;
		}
		if (lowerXNeighborValue < value) {
			neighborValues[relevantNeighborCount] = lowerXNeighborValue;
			neighborDirections[relevantNeighborCount] = X_NEGATIVE;
			relevantNeighborCount++;
		}
		if (upperYNeighborValue < value) {
			neighborValues[relevantNeighborCount] = upperYNeighborValue;
			neighborDirections[relevantNeighborCount] = Y_POSITIVE;
			relevantNeighborCount++;
		}
		if (lowerYNeighborValue < value) {
			neighborValues[relevantNeighborCount] = lowerYNeighborValue;
			neighborDirections[relevantNeighborCount] = Y_NEGATIVE;
			relevantNeighborCount++;
		}
		if (upperZNeighborValue < value) {
			neighborValues[relevantNeighborCount] = upperZNeighborValue;
			neighborDirections[relevantNeighborCount] = Z_POSITIVE;
			relevantNeighborCount++;
		}
		if (lowerZNeighborValue < value) {
			neighborValues[relevantNeighborCount] = lowerZNeighborValue;
			neighborDirections[relevantNeighborCount] = Z_NEGATIVE;
			relevantNeighborCount++;
		}
		boolean toppled = false;
		if (relevantNeighborCount > 0) {
			//sort					
			Utils.sortNeighborsByValueDesc(relevantNeighborCount, neighborValues, neighborDirections);
			//divide
			boolean isFirstNeighbor = true;
			int previousNeighborValue = 0;
			for (int i = 0; i < relevantNeighborCount; i++,isFirstNeighbor = false) {
				int neighborValue = neighborValues[i];
				if (neighborValue != previousNeighborValue || isFirstNeighbor) {
					int shareCount = relevantNeighborCount - i + 1;
					int toShare = value - neighborValue;
					int share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
						value = value - toShare + toShare%shareCount + share;
						for (int j = i; j < relevantNeighborCount; j++) {
							addToNeighbor(newGridSlices, w, x, y, z, neighborDirections[j], share);
						}
					}
					previousNeighborValue = neighborValue;
				}
			}	
		}					
		newGridSlices[1].addToPosition(x, y, z, value);
		return toppled;
	}

	private void addToNeighbor(AnisotropicIntGrid4DSlice[] newGridSlices, int w, int x, int y, int z, byte direction, int value) {
		switch(direction) {
		case W_POSITIVE:
			addToWPositive(newGridSlices, w, x, y, z, value);
			break;
		case W_NEGATIVE:
			addToWNegative(newGridSlices, w, x, y, z, value);
			break;
		case X_POSITIVE:
			addToXPositive(newGridSlices, w, x, y, z, value);
			break;
		case X_NEGATIVE:
			addToXNegative(newGridSlices, w, x, y, z, value);
			break;
		case Y_POSITIVE:
			addToYPositive(newGridSlices, w, x, y, z, value);
			break;
		case Y_NEGATIVE:
			addToYNegative(newGridSlices, w, x, y, z, value);
			break;
		case Z_POSITIVE:
			addToZPositive(newGridSlices, w, x, y, z, value);
			break;
		case Z_NEGATIVE:
			addToZNegative(newGridSlices, w, x, y, z, value);
			break;
		}
	}
	private void addToWPositive(AnisotropicIntGrid4DSlice[] newGridSlices, int w, int x, int y, int z, int value) {
		newGridSlices[2].addToPosition(x, y, z, value);
		if (w == maxW - 1) {
			maxW++;
		}
	}
				
	private void addToWNegative(AnisotropicIntGrid4DSlice[] newGridSlices, int w, int x, int y, int z, int value) {
		if (w > x) {
			int valueToAdd = value;
			if (w == x + 1) {
				valueToAdd += value;
				if (x == y) {
					valueToAdd += value;
					if (y == z) {
						valueToAdd += value;
						if (w == 1) {
							valueToAdd += 4*value;
						}
					}
				}
			}
			newGridSlices[0].addToPosition(x, y, z, valueToAdd);
		}
		if (w == maxW) {
			maxW++;
		}
	}

	private void addToXPositive(AnisotropicIntGrid4DSlice[] newGridSlices, int w, int x, int y, int z, int value) {
		if (x < w) {
			int valueToAdd = value;
			if (x == w - 1) {
				valueToAdd += value;
			}
			int xx = x+1;
			newGridSlices[1].addToPosition(xx, y, z, valueToAdd);
			if (xx > maxX)
				maxX = xx;
		}
	}

	private void addToXNegative(AnisotropicIntGrid4DSlice[] newGridSlices, int w, int x, int y, int z, int value) {
		if (x > y) {
			int valueToAdd = value;									
			if (y == x - 1) {
				valueToAdd += value;
				if (y == z) {
					valueToAdd += value;
					if (y == 0) {
						valueToAdd += 3*value;
					}
				}
			}
			newGridSlices[1].addToPosition(x-1, y, z, valueToAdd);
		}
	}

	private void addToYPositive(AnisotropicIntGrid4DSlice[] newGridSlices, int w, int x, int y, int z, int value) {
		if (y < x) {
			int valueToAdd = value;									
			if (y == x - 1) {
				valueToAdd += value;
				if (w == x) {
					valueToAdd += value;
				}
			}
			int yy = y+1;
			newGridSlices[1].addToPosition(x, yy, z, valueToAdd);
			if (yy > maxY)
				maxY = yy;
		}
	}

	private void addToYNegative(AnisotropicIntGrid4DSlice[] newGridSlices, int w, int x, int y, int z, int value) {
		if (y > z) {	
			int valueToAdd = value;
			if (z == y - 1) {
				valueToAdd += value;
				if (y == 1) {
					valueToAdd += 2*value;
				}
			}
			newGridSlices[1].addToPosition(x, y-1, z, valueToAdd);
		}
	}

	private void addToZPositive(AnisotropicIntGrid4DSlice[] newGridSlices, int w, int x, int y, int z, int value) {
		if (z < y) {
			int valueToAdd = value;
			if (z == y - 1) {
				valueToAdd += value;
				if (x == y) {
					valueToAdd += value;
					if (w == x) {
						valueToAdd += value;
					}
				}
			}
			int zz = z+1;
			newGridSlices[1].addToPosition(x, y, zz, valueToAdd);
			if (zz > maxZ)
				maxZ = zz;
		}
	}

	private void addToZNegative(AnisotropicIntGrid4DSlice[] newGridSlices, int w, int x, int y, int z, int value) {
		if (z > 0) {
			int valueToAdd = value;
			if (z == 1) {
				valueToAdd += value;
			}
			newGridSlices[1].addToPosition(x, y, z-1, valueToAdd);
		}
	}

	@Override
	public String getName() {
		return "Aether4D";
	}
	
	@Override
	public String getSubFolderPath() {
		return getName() + "/" + initialValue;
	}
	
	@Override
	public long getStep() {
		return currentStep;
	}
	
	/**
	 * Returns the initial value
	 * 
	 * @return the value at the origin at step 0
	 */
	public int getInitialValue() {
		return initialValue;
	}
	
	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		String backupFolderPath = backupPath + File.separator + backupName;
		File backupFolder = new File(backupFolderPath);
		if (backupFolder.exists()) {
			FileUtils.cleanDirectory(backupFolder);
		} else {
			backupFolder.mkdirs();
		}		
		if (gridBlockA != null) {
			saveGridBlock(gridBlockA);
		}
		if (gridBlockB != null) {
			saveGridBlock(gridBlockB);
		}
		File gridBackupFolder = new File(backupFolderPath + File.separator + GRID_FOLDER_NAME);
	    FileUtils.copyDirectory(gridFolder, gridBackupFolder);
		HashMap<String, Object> properties = getPropertiesMap();
		Utils.serializeToFile(properties, backupFolderPath, PROPERTIES_BACKUP_FILE_NAME);
	}
	
	private HashMap<String, Object> getPropertiesMap() {
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("initialValue", initialValue);
		properties.put("currentStep", currentStep);
		properties.put("maxW", maxW);
		properties.put("maxX", maxX);
		properties.put("maxY", maxY);
		properties.put("maxZ", maxZ);
		properties.put("maxGridBlockSize", maxGridBlockSize);
		return properties;
	}
	
	private void setPropertiesFromMap(HashMap<String, Object> properties) {
		initialValue = (int) properties.get("initialValue");
		currentStep = (int) properties.get("currentStep");
		maxW = (int) properties.get("maxW"); 
		maxX = (int) properties.get("maxX"); 
		maxY = (int) properties.get("maxY"); 
		maxZ = (int) properties.get("maxZ");
		maxGridBlockSize = (long) properties.get("maxGridBlockSize");
	}
	
	private int[] getAsymmetricCoords(int w, int x, int y, int z) {
		if (w < 0) w = -w;
		if (x < 0) x = -x;
		if (y < 0) y = -y;
		if (z < 0) z = -z;
		//sort coordinates
		boolean sorted;
		do {
			sorted = true;
			if (z > y) {
				sorted = false;
				int swp = z;
				z = y;
				y = swp;
			}
			if (y > x) {
				sorted = false;
				int swp = y;
				y = x;
				x = swp;
			}
			if (x > w) {
				sorted = false;
				int swp = x;
				x = w;
				w = swp;
			}
		} while (!sorted);
		return new int[] {w, x, y, z};
	}
	
	@Override
	public int getMinW() {
		return 0;
	}
	
	@Override
	public int getMinW(int x, int y, int z) {
		return Math.max(Math.max(x, y), z);
	}

	@Override
	public int getMaxW(int x, int y, int z) {
		return getMaxW();
	}
	
	@Override
	public int getMaxW() {
		return maxW;
	}
	
	@Override
	public int getMinX() {
		return 0;
	}

	@Override
	public int getMaxX() {
		return maxX;
	}
	
	@Override
	public int getMinY() {
		return 0;
	}
	
	@Override
	public int getMaxY() {
		return maxY;
	}
	
	@Override
	public int getMinZ() {
		return 0;
	}
	
	@Override
	public int getMaxZ() {
		return maxZ;
	}

	@Override
	public int getMinWAtZ(int z) {
		return z;
	}

	@Override
	public int getMinWAtXZ(int x, int z) {
		return x;
	}

	@Override
	public int getMinWAtYZ(int y, int z) {
		return y;
	}

	@Override
	public int getMaxWAtZ(int z) {
		return getMaxW(); //TODO: check actual value? store all max values?
	}

	@Override
	public int getMaxWAtXZ(int x, int z) {
		return getMaxW();
	}

	@Override
	public int getMaxWAtYZ(int y, int z) {
		return getMaxW();
	}

	@Override
	public int getMinXAtZ(int z) {
		return z;
	}

	@Override
	public int getMinXAtWZ(int w, int z) {
		return z;
	}

	@Override
	public int getMinXAtYZ(int y, int z) {
		return y;
	}

	@Override
	public int getMinX(int w, int y, int z) {
		return y;
	}

	@Override
	public int getMaxXAtZ(int z) {
		return getMaxX();
	}

	@Override
	public int getMaxXAtWZ(int w, int z) {
		return Math.min(getMaxX(), w);
	}

	@Override
	public int getMaxXAtYZ(int y, int z) {
		return getMaxX();
	}

	@Override
	public int getMaxX(int w, int y, int z) {
		return Math.min(getMaxX(), w);
	}
	
	@Override
	public int getMinXAtW(int w) {
		return 0;
	}
	
	@Override
	public int getMaxXAtW(int w) {
		return Math.min(getMaxX(), w);
	}

	@Override
	public int getMinYAtZ(int z) {
		return z;
	}

	@Override
	public int getMaxYAtWZ(int w, int z) {
		return Math.min(getMaxY(), w);
	}

	@Override
	public int getMinYAtXZ(int x, int z) {
		return z;
	}

	@Override
	public int getMinY(int w, int x, int z) {
		return z;
	}

	@Override
	public int getMaxYAtZ(int z) {
		return getMaxY();
	}

	@Override
	public int getMinYAtWZ(int w, int z) {
		return z;
	}

	@Override
	public int getMaxYAtXZ(int x, int z) {
		return Math.min(getMaxY(), x);
	}

	@Override
	public int getMaxY(int w, int x, int z) {
		return Math.min(getMaxY(), x);
	}

	@Override
	public int getMinYAtWX(int w, int x) {
		return 0;
	}

	@Override
	public int getMaxYAtWX(int w, int x) {
		return Math.min(getMaxY(), x);
	}

	@Override
	public int getMinZ(int w, int x, int y) {
		return 0;
	}

	@Override
	public int getMaxZ(int w, int x, int y) {
		return Math.min(getMaxZ(), y);
	}
	
	@Override
	public int getMinYAtW(int w) {
		return 0;
	}

	@Override
	public int getMaxYAtW(int w) {
		return Math.min(getMaxY(), w);
	}

	@Override
	public int getMinZAtW(int w) {
		return 0;
	}

	@Override
	public int getMaxZAtW(int w) {
		return Math.min(getMaxZ(), w);
	}

}