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

import cellularautomata.evolvinggrid.ActionableEvolvingLongGrid4D;
import cellularautomata.grid4d.AnisotropicLongGrid4DSlice;
import cellularautomata.grid4d.LongGrid4D;
import cellularautomata.grid4d.LongSubGrid4DWithWBounds;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 4D with a single source initial configuration
 * 
 * @author Jaume
 *
 */
public class Aether4DAsymmetricSectionSwap extends ActionableEvolvingLongGrid4D {

	public static final long PRIMITIVE_MAX_VALUE = Long.MAX_VALUE;
	
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
	
	private SizeLimitedAnisotropicLongGrid4DBlock gridBlockA;
	private SizeLimitedAnisotropicLongGrid4DBlock gridBlockB;
	private long initialValue;
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
	public Aether4DAsymmetricSectionSwap(long initialValue, long maxGridHeapSize, String folderPath) throws Exception {
		if (initialValue < Long.valueOf("-2635249153387078803")) {//to prevent overflow of long type
			throw new IllegalArgumentException("Initial value cannot be smaller than -2,635,249,153,387,078,803. Use a greater initial value or a different implementation.");
		}
		this.initialValue = initialValue;
		maxGridBlockSize = maxGridHeapSize/2;
		gridBlockA = new SizeLimitedAnisotropicLongGrid4DBlock(0, maxGridBlockSize);
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
	public Aether4DAsymmetricSectionSwap(String backupPath, String folderPath) throws FileNotFoundException, ClassNotFoundException, IOException {
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

	private SizeLimitedAnisotropicLongGrid4DBlock loadGridBlockSafe(int minX) throws IOException, ClassNotFoundException {
		File[] files = gridFolder.listFiles();
		boolean found = false;
		SizeLimitedAnisotropicLongGrid4DBlock gridBlock = null;
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
			gridBlock = (SizeLimitedAnisotropicLongGrid4DBlock) in.readObject();
			in.close();
		}
		return gridBlock;
	}
	
	private SizeLimitedAnisotropicLongGrid4DBlock loadGridBlock(int minW) throws IOException, ClassNotFoundException {
		SizeLimitedAnisotropicLongGrid4DBlock gridBlock = loadGridBlockSafe(minW);
		if (gridBlock == null) {
			throw new FileNotFoundException("No grid block with minW=" + minW + " could be found at folder path \"" + gridFolder.getAbsolutePath() + "\".");
		} else {
			return gridBlock;
		}
	}
	
	private void saveGridBlock(SizeLimitedAnisotropicLongGrid4DBlock gridBlock) throws FileNotFoundException, IOException {
		String name = "minW=" + gridBlock.minW + "_maxW=" + gridBlock.maxW + ".ser";
		String pathName = this.gridFolder.getPath() + File.separator + name;
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pathName));
		out.writeObject(gridBlock);
		out.flush();
		out.close();
	}
	
	private SizeLimitedAnisotropicLongGrid4DBlock loadOrBuildGridBlock(int minW) throws ClassNotFoundException, IOException {		
		SizeLimitedAnisotropicLongGrid4DBlock gridBlock = loadGridBlockSafe(minW);
		if (gridBlock != null) {
			return gridBlock;
		} else {
			return new SizeLimitedAnisotropicLongGrid4DBlock(minW, maxGridBlockSize);
		}
	}

	@Override
	public boolean nextStep() throws Exception {
		triggerBeforeProcessing();
		boolean gridChanged = false;		
		AnisotropicLongGrid4DSlice[] newGridSlices = 
				new AnisotropicLongGrid4DSlice[] {
						null, 
						new AnisotropicLongGrid4DSlice(0), 
						new AnisotropicLongGrid4DSlice(1)};
		if (gridBlockA.minW > 0) {
			if (gridBlockB != null && gridBlockB.minW == 0) {
				SizeLimitedAnisotropicLongGrid4DBlock swp = gridBlockA;
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
				slideGridSlices(newGridSlices, new AnisotropicLongGrid4DSlice(w + 1));
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
				slideGridSlices(newGridSlices, new AnisotropicLongGrid4DSlice(w + 1));
				anySlicePositionToppled = computeLastGridSlice(gridBlockA, gridBlockB, w, newGridSlices);
				gridChanged = gridChanged || anySlicePositionToppled;
				gridBlockA.setSlice(w - 1, newGridSlices[0]);
				w++;
				if (w <= currentMaxW) {
					slideGridSlices(newGridSlices, new AnisotropicLongGrid4DSlice(w + 1));
					anySlicePositionToppled = computeFirstGridSlice(gridBlockA, gridBlockB, w, newGridSlices);
					gridChanged = gridChanged || anySlicePositionToppled;
					gridBlockA.setSlice(w - 1, newGridSlices[0]);
					processGridBlock(gridBlockA);
					SizeLimitedAnisotropicLongGrid4DBlock swp = gridBlockA;
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
	
	private void processGridBlock(SizeLimitedAnisotropicLongGrid4DBlock block) throws Exception {
		if (block.minW <= maxW) {
			LongGrid4D subBlock = null;
			if (block.maxW > maxW) {
				subBlock = new LongSubGrid4DWithWBounds(block, block.minW, maxW);
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
				SizeLimitedAnisotropicLongGrid4DBlock swp = gridBlockA;
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

	private void slideGridSlices(AnisotropicLongGrid4DSlice[] newGridSlices, AnisotropicLongGrid4DSlice newSlice) {
		newGridSlices[0] = newGridSlices[1];
		newGridSlices[1] = newGridSlices[2];
		newGridSlices[2] = newSlice;
	}
	
	private boolean computeGridSlice(SizeLimitedAnisotropicLongGrid4DBlock gridBlock, int w, AnisotropicLongGrid4DSlice[] newGridSlices) {
		return computeGridSlice(gridBlock, gridBlock, gridBlock, w, newGridSlices);
	}
	
	private boolean computeLastGridSlice(SizeLimitedAnisotropicLongGrid4DBlock leftGridBlock, SizeLimitedAnisotropicLongGrid4DBlock rightGridBlock,
			int w, AnisotropicLongGrid4DSlice[] newGridSlices) {
		return computeGridSlice(leftGridBlock, leftGridBlock, rightGridBlock, w, newGridSlices);
	}
	
	private boolean computeFirstGridSlice(SizeLimitedAnisotropicLongGrid4DBlock leftGridBlock, SizeLimitedAnisotropicLongGrid4DBlock rightGridBlock,
			int w, AnisotropicLongGrid4DSlice[] newGridSlices) {
		return computeGridSlice(leftGridBlock, rightGridBlock, rightGridBlock, w, newGridSlices);
	}

	private boolean computeGridSlice(SizeLimitedAnisotropicLongGrid4DBlock leftGridBlock, SizeLimitedAnisotropicLongGrid4DBlock centerGridBlock, 
			SizeLimitedAnisotropicLongGrid4DBlock rightGridBlock, int w, AnisotropicLongGrid4DSlice[] newGridSlices) {
		boolean anyPositionToppled = false;
		for (int x = 0; x <= w; x++) {
			for (int y = 0; y <= x; y++) {
				for (int z = 0; z <= y; z++) {				
					long value = centerGridBlock.getFromPosition(w, x, y, z);
					long upperWNeighborValue = getFromPosition(w, leftGridBlock, centerGridBlock, rightGridBlock, w + 1, x, y, z);
					long lowerWNeighborValue = getFromPosition(w, leftGridBlock, centerGridBlock, rightGridBlock, w - 1, x, y, z);
					long upperXNeighborValue = getFromPosition(w, leftGridBlock, centerGridBlock, rightGridBlock, w, x + 1, y, z);
					long lowerXNeighborValue = getFromPosition(w, leftGridBlock, centerGridBlock, rightGridBlock, w, x - 1, y, z);
					long upperYNeighborValue = getFromPosition(w, leftGridBlock, centerGridBlock, rightGridBlock, w, x, y + 1, z);
					long lowerYNeighborValue = getFromPosition(w, leftGridBlock, centerGridBlock, rightGridBlock, w, x, y - 1, z);
					long upperZNeighborValue = getFromPosition(w, leftGridBlock, centerGridBlock, rightGridBlock, w, x, y, z + 1);
					long lowerZNeighborValue = getFromPosition(w, leftGridBlock, centerGridBlock, rightGridBlock, w, x, y, z - 1);				
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
	
	private long getFromPosition(int centerW, SizeLimitedAnisotropicLongGrid4DBlock lowerWGridBlock, 
			SizeLimitedAnisotropicLongGrid4DBlock centerGridBlock, SizeLimitedAnisotropicLongGrid4DBlock upperWGridBlock, int w, int x, int y, int z) {
		int[] asymmetricCoords = getAsymmetricCoords(w, x, y, z);
		w = asymmetricCoords[0];
		x = asymmetricCoords[1];
		y = asymmetricCoords[2];
		z = asymmetricCoords[3];
		long value;
		if (w == centerW) {
			value = centerGridBlock.getFromPosition(w, x, y, z);
		} else if (w < centerW) {
			value = lowerWGridBlock.getFromPosition(w, x, y, z);
		} else {
			value = upperWGridBlock.getFromPosition(w, x, y, z);
		}
		return value;
	}
	
	private boolean computePosition(long value, 
			long upperWNeighborValue, long lowerWNeighborValue, 
			long upperXNeighborValue, long lowerXNeighborValue, 
			long upperYNeighborValue, long lowerYNeighborValue, 
			long upperZNeighborValue, long lowerZNeighborValue, 
			int w, int x, int y, int z, AnisotropicLongGrid4DSlice[] newGridSlices) {
		long[] neighborValues = new long[8];
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
			boolean sorted = false;
			while (!sorted) {
				sorted = true;
				for (int i = relevantNeighborCount - 2; i >= 0; i--) {
					if (neighborValues[i] < neighborValues[i+1]) {
						sorted = false;
						long valSwap = neighborValues[i];
						neighborValues[i] = neighborValues[i+1];
						neighborValues[i+1] = valSwap;
						byte dirSwap = neighborDirections[i];
						neighborDirections[i] = neighborDirections[i+1];
						neighborDirections[i+1] = dirSwap;
					}
				}
			}
			//divide
			boolean isFirstNeighbor = true;
			long previousNeighborValue = 0;
			for (int i = 0; i < relevantNeighborCount; i++,isFirstNeighbor = false) {
				long neighborValue = neighborValues[i];
				if (neighborValue != previousNeighborValue || isFirstNeighbor) {
					int shareCount = relevantNeighborCount - i + 1;
					long toShare = value - neighborValue;
					long share = toShare/shareCount;
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

	private void addToNeighbor(AnisotropicLongGrid4DSlice[] newGridSlices, int w, int x, int y, int z, byte direction, long value) {
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
	private void addToWPositive(AnisotropicLongGrid4DSlice[] newGridSlices, int w, int x, int y, int z, long value) {
		newGridSlices[2].addToPosition(x, y, z, value);
		if (w == maxW - 1) {
			maxW++;
		}
	}
				
	private void addToWNegative(AnisotropicLongGrid4DSlice[] newGridSlices, int w, int x, int y, int z, long value) {
		if (w > x) {
			long valueToAdd = value;
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

	private void addToXPositive(AnisotropicLongGrid4DSlice[] newGridSlices, int w, int x, int y, int z, long value) {
		if (x < w) {
			long valueToAdd = value;
			if (x == w - 1) {
				valueToAdd += value;
			}
			int xx = x+1;
			newGridSlices[1].addToPosition(xx, y, z, valueToAdd);
			if (xx > maxX)
				maxX = xx;
		}
	}

	private void addToXNegative(AnisotropicLongGrid4DSlice[] newGridSlices, int w, int x, int y, int z, long value) {
		if (x > y) {
			long valueToAdd = value;									
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

	private void addToYPositive(AnisotropicLongGrid4DSlice[] newGridSlices, int w, int x, int y, int z, long value) {
		if (y < x) {
			long valueToAdd = value;									
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

	private void addToYNegative(AnisotropicLongGrid4DSlice[] newGridSlices, int w, int x, int y, int z, long value) {
		if (y > z) {	
			long valueToAdd = value;
			if (z == y - 1) {
				valueToAdd += value;
				if (y == 1) {
					valueToAdd += 2*value;
				}
			}
			newGridSlices[1].addToPosition(x, y-1, z, valueToAdd);
		}
	}

	private void addToZPositive(AnisotropicLongGrid4DSlice[] newGridSlices, int w, int x, int y, int z, long value) {
		if (z < y) {
			long valueToAdd = value;
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

	private void addToZNegative(AnisotropicLongGrid4DSlice[] newGridSlices, int w, int x, int y, int z, long value) {
		if (z > 0) {
			long valueToAdd = value;
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
	public long getInitialValue() {
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
		initialValue = (long) properties.get("initialValue");
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

}