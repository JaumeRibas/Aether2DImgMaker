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
package cellularautomata.automata.aether;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;

import cellularautomata.Constants;
import cellularautomata.Utils;
import cellularautomata.grid4d.AnisotropicBigIntGrid4DSlice;
import cellularautomata.grid4d.AnisotropicGrid4DA;
import cellularautomata.grid4d.ImmutableNumberGrid4D;
import cellularautomata.grid4d.NumberGrid4D;
import cellularautomata.grid4d.NumberSubGrid4D;
import cellularautomata.grid4d.NumberSubGrid4DWithWBounds;
import cellularautomata.grid4d.SizeLimitedAnisotropicBigIntGrid4DBlock;
import cellularautomata.model4d.ActionableModel4D;
import cellularautomata.numbers.BigInt;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 4D with a single source initial configuration
 * 
 * @author Jaume
 *
 */
public class BigIntAether4DAsymmetricSectionSwap extends ActionableModel4D<NumberGrid4D<BigInt>> implements AnisotropicGrid4DA {

	private static final String PROPERTIES_BACKUP_FILE_NAME = "properties.ser";
	private static final String GRID_FOLDER_NAME = "grid";
	
	private static final BigInt TWO = BigInt.valueOf(2);
	private static final BigInt THREE = BigInt.valueOf(3);
	private static final BigInt FOUR = BigInt.valueOf(4);

	private static final byte W_POSITIVE = 0;
	private static final byte W_NEGATIVE = 1;
	private static final byte X_POSITIVE = 2;
	private static final byte X_NEGATIVE = 3;
	private static final byte Y_POSITIVE = 4;
	private static final byte Y_NEGATIVE = 5;
	private static final byte Z_POSITIVE = 6;
	private static final byte Z_NEGATIVE = 7;
	
	private SizeLimitedAnisotropicBigIntGrid4DBlock gridBlockA;
	private SizeLimitedAnisotropicBigIntGrid4DBlock gridBlockB;
	private BigInt initialValue;
	private int step;
	private int maxW, maxX, maxY, maxZ;
	private File gridFolder;
	private int gridBlockSide;
	/**
	 * Used in {@link #getSubfolderPath()} in case the initial value is too big.
	 */
	private String creationTimestamp;
	
	/**
	 * 
	 * @param initialValue
	 * @param maxGridSideInHeap a parameter to limit the amount of heap space occupied by the grid. This won't limit the total size of the grid.
	 * @param folderPath
	 * @throws Exception
	 */
	public BigIntAether4DAsymmetricSectionSwap(BigInt initialValue, int maxGridSideInHeap, String folderPath) throws Exception {
		this.initialValue = initialValue;
		gridBlockSide = maxGridSideInHeap/2;
		gridBlockA = new SizeLimitedAnisotropicBigIntGrid4DBlock(0, gridBlockSide);
		gridBlockA.setValueAtPosition(0, 0, 0, 0, initialValue);
		maxW = 1;//we leave a buffer of one position to account for 'negative growth'
		maxX = 0;
		maxY = 0;
		maxZ = 0;
		step = 0;
		gridFolder = new File(folderPath + File.separator + GRID_FOLDER_NAME);
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
	public BigIntAether4DAsymmetricSectionSwap(String backupPath, String folderPath) throws FileNotFoundException, ClassNotFoundException, IOException {
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
		gridFolder = new File(folderPath + File.separator + GRID_FOLDER_NAME);
		if (gridFolder.exists()) {
			FileUtils.cleanDirectory(gridFolder);
		}
		FileUtils.copyDirectory(gridBackupFolder, gridFolder);
		if (maxW > gridBlockA.maxW) {
			gridBlockB = loadGridBlock(gridBlockA.maxW + 1);
		}
	}

	private SizeLimitedAnisotropicBigIntGrid4DBlock loadGridBlockSafe(int minX) throws IOException, ClassNotFoundException {
		File[] files = gridFolder.listFiles();
		boolean found = false;
		SizeLimitedAnisotropicBigIntGrid4DBlock gridBlock = null;
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
			gridBlock = (SizeLimitedAnisotropicBigIntGrid4DBlock) in.readObject();
			in.close();
		}
		return gridBlock;
	}
	
	private SizeLimitedAnisotropicBigIntGrid4DBlock loadGridBlock(int minW) throws IOException, ClassNotFoundException {
		SizeLimitedAnisotropicBigIntGrid4DBlock gridBlock = loadGridBlockSafe(minW);
		if (gridBlock == null) {
			throw new FileNotFoundException("No grid block with minW=" + minW + " could be found at folder path \"" + gridFolder.getAbsolutePath() + "\".");
		} else {
			return gridBlock;
		}
	}
	
	private void saveGridBlock(SizeLimitedAnisotropicBigIntGrid4DBlock gridBlock) throws FileNotFoundException, IOException {
		String name = "minW=" + gridBlock.minW + "_maxW=" + gridBlock.maxW + ".ser";
		String pathName = this.gridFolder.getPath() + File.separator + name;
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pathName));
		out.writeObject(gridBlock);
		out.flush();
		out.close();
	}
	
	private SizeLimitedAnisotropicBigIntGrid4DBlock loadOrBuildGridBlock(int minW) throws ClassNotFoundException, IOException {		
		SizeLimitedAnisotropicBigIntGrid4DBlock gridBlock = loadGridBlockSafe(minW);
		if (gridBlock != null) {
			return gridBlock;
		} else {
			return new SizeLimitedAnisotropicBigIntGrid4DBlock(minW, gridBlockSide);
		}
	}

	@Override
	public boolean nextStep() throws Exception {
		step++;
		triggerBeforeProcessing();
		boolean gridChanged = false;		
		AnisotropicBigIntGrid4DSlice[] newGridSlices = 
				new AnisotropicBigIntGrid4DSlice[] {
						null, 
						new AnisotropicBigIntGrid4DSlice(0), 
						new AnisotropicBigIntGrid4DSlice(1)};
		if (gridBlockA.minW > 0) {
			if (gridBlockB != null && gridBlockB.minW == 0) {
				SizeLimitedAnisotropicBigIntGrid4DBlock swp = gridBlockA;
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
				slideGridSlices(newGridSlices, new AnisotropicBigIntGrid4DSlice(w + 1));
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
				slideGridSlices(newGridSlices, new AnisotropicBigIntGrid4DSlice(w + 1));
				anySlicePositionToppled = computeLastGridSlice(gridBlockA, gridBlockB, w, newGridSlices);
				gridChanged = gridChanged || anySlicePositionToppled;
				gridBlockA.setSlice(w - 1, newGridSlices[0]);
				w++;
				if (w <= currentMaxW) {
					slideGridSlices(newGridSlices, new AnisotropicBigIntGrid4DSlice(w + 1));
					anySlicePositionToppled = computeFirstGridSlice(gridBlockA, gridBlockB, w, newGridSlices);
					gridChanged = gridChanged || anySlicePositionToppled;
					gridBlockA.setSlice(w - 1, newGridSlices[0]);
					processGridBlock(gridBlockA);
					SizeLimitedAnisotropicBigIntGrid4DBlock swp = gridBlockA;
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
		triggerAfterProcessing();
		return gridChanged;
	}
	
	private void processGridBlock(SizeLimitedAnisotropicBigIntGrid4DBlock block) throws Exception {
		if (block.minW <= maxW) {
			NumberGrid4D<BigInt> subBlock = null;
			int maxX = getMaxX();
			int maxY = getMaxY();
			int maxZ = getMaxZ();
			if (block.maxW > maxW) {
				if (block.maxW > maxX || block.maxW > maxY || block.maxW > maxZ) {
					subBlock = new NumberSubGrid4D<BigInt, NumberGrid4D<BigInt>>(block, block.minW, maxW, 0, maxX, 0, maxY, 0, maxZ);
				} else {
					subBlock = new NumberSubGrid4DWithWBounds<BigInt>(block, block.minW, maxW);
				}
			} else {
				if (block.maxW > maxX || block.maxW > maxY || block.maxW > maxZ) {
					subBlock = new NumberSubGrid4D<BigInt, NumberGrid4D<BigInt>>(block, block.minW, block.maxW, 0, maxX, 0, maxY, 0, maxZ);
				} else {
					subBlock = new ImmutableNumberGrid4D<BigInt>(block);
				}
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
				SizeLimitedAnisotropicBigIntGrid4DBlock swp = gridBlockA;
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

	private void slideGridSlices(AnisotropicBigIntGrid4DSlice[] newGridSlices, AnisotropicBigIntGrid4DSlice newSlice) {
		newGridSlices[0] = newGridSlices[1];
		newGridSlices[1] = newGridSlices[2];
		newGridSlices[2] = newSlice;
	}
	
	private boolean computeGridSlice(SizeLimitedAnisotropicBigIntGrid4DBlock gridBlock, int w, AnisotropicBigIntGrid4DSlice[] newGridSlices) {
		return computeGridSlice(gridBlock, gridBlock, gridBlock, w, newGridSlices);
	}
	
	private boolean computeLastGridSlice(SizeLimitedAnisotropicBigIntGrid4DBlock leftGridBlock, SizeLimitedAnisotropicBigIntGrid4DBlock rightGridBlock,
			int w, AnisotropicBigIntGrid4DSlice[] newGridSlices) {
		return computeGridSlice(leftGridBlock, leftGridBlock, rightGridBlock, w, newGridSlices);
	}
	
	private boolean computeFirstGridSlice(SizeLimitedAnisotropicBigIntGrid4DBlock leftGridBlock, SizeLimitedAnisotropicBigIntGrid4DBlock rightGridBlock,
			int w, AnisotropicBigIntGrid4DSlice[] newGridSlices) {
		return computeGridSlice(leftGridBlock, rightGridBlock, rightGridBlock, w, newGridSlices);
	}

	private boolean computeGridSlice(SizeLimitedAnisotropicBigIntGrid4DBlock leftGridBlock, SizeLimitedAnisotropicBigIntGrid4DBlock centerGridBlock, 
			SizeLimitedAnisotropicBigIntGrid4DBlock rightGridBlock, int w, AnisotropicBigIntGrid4DSlice[] newGridSlices) {
		boolean anyPositionToppled = false;
		for (int x = 0; x <= w; x++) {
			for (int y = 0; y <= x; y++) {
				for (int z = 0; z <= y; z++) {				
					BigInt value = centerGridBlock.getFromPosition(w, x, y, z);
					BigInt upperWNeighborValue = getFromPosition(w, leftGridBlock, centerGridBlock, rightGridBlock, w + 1, x, y, z);
					BigInt lowerWNeighborValue = getFromPosition(w, leftGridBlock, centerGridBlock, rightGridBlock, w - 1, x, y, z);
					BigInt upperXNeighborValue = getFromPosition(w, leftGridBlock, centerGridBlock, rightGridBlock, w, x + 1, y, z);
					BigInt lowerXNeighborValue = getFromPosition(w, leftGridBlock, centerGridBlock, rightGridBlock, w, x - 1, y, z);
					BigInt upperYNeighborValue = getFromPosition(w, leftGridBlock, centerGridBlock, rightGridBlock, w, x, y + 1, z);
					BigInt lowerYNeighborValue = getFromPosition(w, leftGridBlock, centerGridBlock, rightGridBlock, w, x, y - 1, z);
					BigInt upperZNeighborValue = getFromPosition(w, leftGridBlock, centerGridBlock, rightGridBlock, w, x, y, z + 1);
					BigInt lowerZNeighborValue = getFromPosition(w, leftGridBlock, centerGridBlock, rightGridBlock, w, x, y, z - 1);				
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
	
	private BigInt getFromPosition(int centerW, SizeLimitedAnisotropicBigIntGrid4DBlock lowerWGridBlock, 
			SizeLimitedAnisotropicBigIntGrid4DBlock centerGridBlock, SizeLimitedAnisotropicBigIntGrid4DBlock upperWGridBlock, int w, int x, int y, int z) {
		int[] asymmetricCoords = getAsymmetricCoords(w, x, y, z);
		w = asymmetricCoords[0];
		x = asymmetricCoords[1];
		y = asymmetricCoords[2];
		z = asymmetricCoords[3];
		BigInt value;
		if (w == centerW) {
			value = centerGridBlock.getFromPosition(w, x, y, z);
		} else if (w < centerW) {
			value = lowerWGridBlock.getFromPosition(w, x, y, z);
		} else {
			value = upperWGridBlock.getFromPosition(w, x, y, z);
		}
		return value;
	}
	
	private boolean computePosition(BigInt value, 
			BigInt upperWNeighborValue, BigInt lowerWNeighborValue, 
			BigInt upperXNeighborValue, BigInt lowerXNeighborValue, 
			BigInt upperYNeighborValue, BigInt lowerYNeighborValue, 
			BigInt upperZNeighborValue, BigInt lowerZNeighborValue, 
			int w, int x, int y, int z, AnisotropicBigIntGrid4DSlice[] newGridSlices) {
		BigInt[] neighborValues = new BigInt[8];
		byte[] neighborDirections = new byte[8];
		int relevantNeighborCount = 0;
		if (upperWNeighborValue.compareTo(value) < 0) {
			neighborValues[relevantNeighborCount] = upperWNeighborValue;
			neighborDirections[relevantNeighborCount] = W_POSITIVE;
			relevantNeighborCount++;
		}
		if (lowerWNeighborValue.compareTo(value) < 0) {
			neighborValues[relevantNeighborCount] = lowerWNeighborValue;
			neighborDirections[relevantNeighborCount] = W_NEGATIVE;
			relevantNeighborCount++;
		}
		if (upperXNeighborValue.compareTo(value) < 0) {
			neighborValues[relevantNeighborCount] = upperXNeighborValue;
			neighborDirections[relevantNeighborCount] = X_POSITIVE;
			relevantNeighborCount++;
		}
		if (lowerXNeighborValue.compareTo(value) < 0) {
			neighborValues[relevantNeighborCount] = lowerXNeighborValue;
			neighborDirections[relevantNeighborCount] = X_NEGATIVE;
			relevantNeighborCount++;
		}
		if (upperYNeighborValue.compareTo(value) < 0) {
			neighborValues[relevantNeighborCount] = upperYNeighborValue;
			neighborDirections[relevantNeighborCount] = Y_POSITIVE;
			relevantNeighborCount++;
		}
		if (lowerYNeighborValue.compareTo(value) < 0) {
			neighborValues[relevantNeighborCount] = lowerYNeighborValue;
			neighborDirections[relevantNeighborCount] = Y_NEGATIVE;
			relevantNeighborCount++;
		}
		if (upperZNeighborValue.compareTo(value) < 0) {
			neighborValues[relevantNeighborCount] = upperZNeighborValue;
			neighborDirections[relevantNeighborCount] = Z_POSITIVE;
			relevantNeighborCount++;
		}
		if (lowerZNeighborValue.compareTo(value) < 0) {
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
			BigInt previousNeighborValue = null;
			for (int i = 0; i < relevantNeighborCount; i++,isFirstNeighbor = false) {
				BigInt neighborValue = neighborValues[i];
				if (!neighborValue.equals(previousNeighborValue) || isFirstNeighbor) {
					int shareCount = relevantNeighborCount - i + 1;
					BigInt toShare = value.subtract(neighborValue);
					BigInt[] shareAndReminder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					BigInt share = shareAndReminder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
						value = value.subtract(toShare).add(shareAndReminder[1]).add(share);
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

	private void addToNeighbor(AnisotropicBigIntGrid4DSlice[] newGridSlices, int w, int x, int y, int z, byte direction, BigInt value) {
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
	private void addToWPositive(AnisotropicBigIntGrid4DSlice[] newGridSlices, int w, int x, int y, int z, BigInt value) {
		newGridSlices[2].addToPosition(x, y, z, value);
		if (w == maxW - 1) {
			maxW++;
		}
	}
				
	private void addToWNegative(AnisotropicBigIntGrid4DSlice[] newGridSlices, int w, int x, int y, int z, BigInt value) {
		if (w > x) {
			BigInt valueToAdd = value;
			if (w == x + 1) {
				valueToAdd = valueToAdd.add(value);
				if (x == y) {
					valueToAdd = valueToAdd.add(value);
					if (y == z) {
						valueToAdd = valueToAdd.add(value);
						if (w == 1) {
							valueToAdd = valueToAdd.add(value.multiply(FOUR));
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

	private void addToXPositive(AnisotropicBigIntGrid4DSlice[] newGridSlices, int w, int x, int y, int z, BigInt value) {
		if (x < w) {
			BigInt valueToAdd = value;
			if (x == w - 1) {
				valueToAdd = valueToAdd.add(value);
			}
			int xx = x+1;
			newGridSlices[1].addToPosition(xx, y, z, valueToAdd);
			if (xx > maxX)
				maxX = xx;
		}
	}

	private void addToXNegative(AnisotropicBigIntGrid4DSlice[] newGridSlices, int w, int x, int y, int z, BigInt value) {
		if (x > y) {
			BigInt valueToAdd = value;									
			if (y == x - 1) {
				valueToAdd = valueToAdd.add(value);
				if (y == z) {
					valueToAdd = valueToAdd.add(value);
					if (y == 0) {
						valueToAdd = valueToAdd.add(value.multiply(THREE));
					}
				}
			}
			newGridSlices[1].addToPosition(x-1, y, z, valueToAdd);
			if (x > maxX)
				maxX = x;
		}
	}

	private void addToYPositive(AnisotropicBigIntGrid4DSlice[] newGridSlices, int w, int x, int y, int z, BigInt value) {
		if (y < x) {
			BigInt valueToAdd = value;									
			if (y == x - 1) {
				valueToAdd = valueToAdd.add(value);
				if (w == x) {
					valueToAdd = valueToAdd.add(value);
				}
			}
			int yy = y+1;
			newGridSlices[1].addToPosition(x, yy, z, valueToAdd);
			if (yy > maxY)
				maxY = yy;
		}
	}

	private void addToYNegative(AnisotropicBigIntGrid4DSlice[] newGridSlices, int w, int x, int y, int z, BigInt value) {
		if (y > z) {	
			BigInt valueToAdd = value;
			if (z == y - 1) {
				valueToAdd = valueToAdd.add(value);
				if (y == 1) {
					valueToAdd = valueToAdd.add(value.multiply(TWO));
				}
			}
			newGridSlices[1].addToPosition(x, y-1, z, valueToAdd);
			if (y > maxY)
				maxY = y;
		}
	}

	private void addToZPositive(AnisotropicBigIntGrid4DSlice[] newGridSlices, int w, int x, int y, int z, BigInt value) {
		if (z < y) {
			BigInt valueToAdd = value;
			if (z == y - 1) {
				valueToAdd = valueToAdd.add(value);
				if (x == y) {
					valueToAdd = valueToAdd.add(value);
					if (w == x) {
						valueToAdd = valueToAdd.add(value);
					}
				}
			}
			int zz = z+1;
			newGridSlices[1].addToPosition(x, y, zz, valueToAdd);
			if (zz > maxZ)
				maxZ = zz;
		}
	}

	private void addToZNegative(AnisotropicBigIntGrid4DSlice[] newGridSlices, int w, int x, int y, int z, BigInt value) {
		if (z > 0) {
			BigInt valueToAdd = value;
			if (z == 1) {
				valueToAdd = valueToAdd.add(value);
			}
			newGridSlices[1].addToPosition(x, y, z-1, valueToAdd);
			if (z > maxZ)
				maxZ = z;
		}
	}

	@Override
	public String getName() {
		return "Aether";
	}
	
	@Override
	public String getSubfolderPath() {
		String strInitialValue = initialValue.toString();
		if (strInitialValue.length() > Constants.MAX_INITIAL_VALUE_LENGTH_IN_PATH)
			strInitialValue = creationTimestamp;
		return getName() + "/4D/" + strInitialValue + "/asymmetric_section";
	}
	
	@Override
	public long getStep() {
		return step;
	}
	
	/**
	 * Returns the initial value
	 * 
	 * @return the value at the origin at step 0
	 */
	public BigInt getInitialValue() {
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
		properties.put("step", step);
		properties.put("maxW", maxW);
		properties.put("maxX", maxX);
		properties.put("maxY", maxY);
		properties.put("maxZ", maxZ);
		properties.put("gridBlockSide", gridBlockSide);
		properties.put("creationTimestamp", creationTimestamp);
		return properties;
	}
	
	private void setPropertiesFromMap(HashMap<String, Object> properties) {
		initialValue = (BigInt) properties.get("initialValue");
		step = (int) properties.get("step");
		maxW = (int) properties.get("maxW"); 
		maxX = (int) properties.get("maxX"); 
		maxY = (int) properties.get("maxY"); 
		maxZ = (int) properties.get("maxZ");
		gridBlockSide = (int) properties.get("gridBlockSide");
		creationTimestamp = (String) properties.get("creationTimestamp");
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
	public int getMaxW() {
		return maxW;
	}
	
	@Override
	public int getMaxX() {
		return (int) Math.min((step + 2)/2 - 1, maxW);
	}
	
	@Override
	public int getMaxY() {
		return getMaxX();
	}
	
	@Override
	public int getMaxZ() {
		return getMaxX();
	}

}