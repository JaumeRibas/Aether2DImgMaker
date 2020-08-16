/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2020 Jaume Ribas

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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import cellularautomata.evolvinggrid.EvolvingIntGrid;
import cellularautomata.evolvinggrid.EvolvingIntGrid2D;
import cellularautomata.evolvinggrid.EvolvingIntGrid3D;
import cellularautomata.evolvinggrid.EvolvingIntGrid4D;
import cellularautomata.evolvinggrid.EvolvingLongGrid;
import cellularautomata.evolvinggrid.EvolvingLongGrid1D;
import cellularautomata.evolvinggrid.EvolvingLongGrid2D;
import cellularautomata.evolvinggrid.EvolvingLongGrid3D;
import cellularautomata.evolvinggrid.EvolvingLongGrid4D;
import cellularautomata.evolvinggrid.EvolvingModel;
import cellularautomata.evolvinggrid.EvolvingShortGrid;
import cellularautomata.evolvinggrid.EvolvingShortGrid3D;
import cellularautomata.evolvinggrid.ActionableEvolvingIntGrid3D;
import cellularautomata.evolvinggrid.ActionableEvolvingLongGrid3D;
import cellularautomata.evolvinggrid.ActionableEvolvingLongGrid4D;
import cellularautomata.evolvinggrid.SymmetricEvolvingShortGrid4D;
import cellularautomata.grid.GridProcessor;
import cellularautomata.grid1d.LongGrid1D;
import cellularautomata.grid2d.IntGrid2D;
import cellularautomata.grid2d.LongGrid2D;
import cellularautomata.grid2d.ShortGrid2D;
import cellularautomata.grid3d.IntGrid3D;
import cellularautomata.grid3d.LongGrid3D;
import cellularautomata.grid4d.LongGrid4D;
import cellularautomata.grid4d.ShortGrid4D;

public class CATests {
	
	public static void main(String[] args) throws Exception {
		long initialValue = 100000;
		Aether2D ae1 = new Aether2D(initialValue);
		AetherSimple2D ae2 = new AetherSimple2D(initialValue);
		compare(ae1, ae2);
	}
	
	public static void printVonNeumannNeighborhood(LongGrid3D grid, int x, int y, int z) {
		try {
			System.out.println("center: " + grid.getValueAtPosition(x, y, z) 
				+ ", gx: " + grid.getValueAtPosition(x + 1, y, z) + ", sx: " + grid.getValueAtPosition(x - 1, y, z) 
				+ ", gy: " + grid.getValueAtPosition(x, y + 1, z) + ", sy: " + grid.getValueAtPosition(x, y - 1, z)
				+ ", gz: " + grid.getValueAtPosition(x, y, z + 1) + ", sz: " + grid.getValueAtPosition(x, y, z - 1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void asymmetricPositionsNeighbors() {
		int size = 15;
		System.out.println(" x | y | z | Neighborhood");
		System.out.println("-------------------------");
		for (int x = 0; x < size; x++) {
			for (int y = 0; y <= x; y++) {
				for (int z = 0; z <= y; z++) {
					int[] coords = new int[] {x, y, z};
					int[] greaterXNeighborCoords = new int[] {x+1, y, z};
					int[] smallerXNeighborCoords = new int[] {x-1, y, z};
					int[] greaterYNeighborCoords = new int[] {x, y+1, z};
					int[] smallerYNeighborCoords = new int[] {x, y-1, z};
					int[] greaterZNeighborCoords = new int[] {x, y, z+1};
					int[] smallerZNeighborCoords = new int[] {x, y, z-1};
					int greaterXNeighborSymmetries = 1;
					int smallerXNeighborSymmetries = 1;
					int greaterYNeighborSymmetries = 1;
					int smallerYNeighborSymmetries = 1;
					int greaterZNeighborSymmetries = 1;
					int smallerZNeighborSymmetries = 1;
					int greaterXNeighborWeight = 1;
					int smallerXNeighborWeight = 1;
					int greaterYNeighborWeight = 1;
					int smallerYNeighborWeight = 1;
					int greaterZNeighborWeight = 1;
					int smallerZNeighborWeight = 1;
					List<String> neighbors = new ArrayList<String>();
					int[] nc = greaterXNeighborCoords;
					if (isOutsideAsymmetricSection(nc)) {
						greaterXNeighborSymmetries = 0;
					} else {
						greaterXNeighborWeight += getSymmetricNeighborsCount(nc, coords);
						greaterXNeighborSymmetries += getSymmetricNeighborsCount(coords, nc);
					}
					nc = smallerXNeighborCoords;
					if (isOutsideAsymmetricSection(nc)) {
						smallerXNeighborSymmetries = 0;
					} else {
						smallerXNeighborWeight += getSymmetricNeighborsCount(nc, coords);
						smallerXNeighborSymmetries += getSymmetricNeighborsCount(coords, nc);
					}
					nc = greaterYNeighborCoords;
					if (isOutsideAsymmetricSection(nc)) {
						greaterYNeighborSymmetries = 0;
					} else {
						greaterYNeighborWeight += getSymmetricNeighborsCount(nc, coords);
						greaterYNeighborSymmetries += getSymmetricNeighborsCount(coords, nc);
					}
					nc = smallerYNeighborCoords;
					if (isOutsideAsymmetricSection(nc)) {
						smallerYNeighborSymmetries = 0;
					} else {
						smallerYNeighborWeight += getSymmetricNeighborsCount(nc, coords);
						smallerYNeighborSymmetries += getSymmetricNeighborsCount(coords, nc);
					}
					nc = greaterZNeighborCoords;
					if (isOutsideAsymmetricSection(nc)) {
						greaterZNeighborSymmetries = 0;
					} else {
						greaterZNeighborWeight += getSymmetricNeighborsCount(nc, coords);
						greaterZNeighborSymmetries += getSymmetricNeighborsCount(coords, nc);
					}
					nc = smallerZNeighborCoords;
					if (isOutsideAsymmetricSection(nc)) {
						smallerZNeighborSymmetries = 0;
					} else {
						smallerZNeighborWeight += getSymmetricNeighborsCount(nc, coords);
						smallerZNeighborSymmetries += getSymmetricNeighborsCount(coords, nc);
					}
					
					if (greaterXNeighborSymmetries > 0) {
						String neighbor = "GX";
						if (greaterXNeighborWeight > 1) {
							neighbor = neighbor + "(" + greaterXNeighborWeight + ")";
						}
						if (greaterXNeighborSymmetries > 1) {
							neighbor = greaterXNeighborSymmetries + "*" + neighbor;
						}
						neighbors.add(neighbor);
					}
					if (smallerXNeighborSymmetries > 0) {
						String neighbor = "SX";
						if (smallerXNeighborWeight > 1) {
							neighbor = neighbor + "(" + smallerXNeighborWeight + ")";
						}
						if (smallerXNeighborSymmetries > 1) {
							neighbor = smallerXNeighborSymmetries + "*" + neighbor;
						}
						neighbors.add(neighbor);
					}
					if (greaterYNeighborSymmetries > 0) {
						String neighbor = "GY";
						if (greaterYNeighborWeight > 1) {
							neighbor = neighbor + "(" + greaterYNeighborWeight + ")";
						}
						if (greaterYNeighborSymmetries > 1) {
							neighbor = greaterYNeighborSymmetries + "*" + neighbor;
						}
						neighbors.add(neighbor);
					}
					if (smallerYNeighborSymmetries > 0) {
						String neighbor = "SY";
						if (smallerYNeighborWeight > 1) {
							neighbor = neighbor + "(" + smallerYNeighborWeight + ")";
						}
						if (smallerYNeighborSymmetries > 1) {
							neighbor = smallerYNeighborSymmetries + "*" + neighbor;
						}
						neighbors.add(neighbor);
					}
					if (greaterZNeighborSymmetries > 0) {
						String neighbor = "GZ";
						if (greaterZNeighborWeight > 1) {
							neighbor = neighbor + "(" + greaterZNeighborWeight + ")";
						}
						if (greaterZNeighborSymmetries > 1) {
							neighbor = greaterZNeighborSymmetries + "*" + neighbor;
						}
						neighbors.add(neighbor);
					}
					if (smallerZNeighborSymmetries > 0) {
						String neighbor = "SZ";
						if (smallerZNeighborWeight > 1) {
							neighbor = neighbor + "(" + smallerZNeighborWeight + ")";
						}
						if (smallerZNeighborSymmetries > 1) {
							neighbor = smallerZNeighborSymmetries + "*" + neighbor;
						}
						neighbors.add(neighbor);
					}					
					
					System.out.println(" " + x + " | " + y + " | " + z + " | " + String.join(", ", neighbors));
				}
				System.out.println();
			}
			System.out.println(System.lineSeparator() + System.lineSeparator());
		}
	}
	
	private static boolean isOutsideAsymmetricSection(int[] coords) {
		for (int i = 0; i < coords.length; i++) {
			if (coords[i] < 0) {
				return true;
			}
		}
		int lengthMinusOne = coords.length - 1;
		for (int i = 0; i < lengthMinusOne; i++) {
			if (coords[i] < coords[i + 1]) {
				return true;
			}
		}
		return false;
	}
	
	private static int getSymmetricNeighborsCount(int[] coords, int[] compareCoords) {
		int x = coords[0], y = coords[1], z = coords[2];
		int[] greaterXNeighborCoords = new int[] {x+1, y, z};
		int[] smallerXNeighborCoords = new int[] {x-1, y, z};
		int[] greaterYNeighborCoords = new int[] {x, y+1, z};
		int[] smallerYNeighborCoords = new int[] {x, y-1, z};
		int[] greaterZNeighborCoords = new int[] {x, y, z+1};
		int[] smallerZNeighborCoords = new int[] {x, y, z-1};
		int count = 0;
		if (!Arrays.equals(greaterXNeighborCoords, compareCoords)) {
			if (Arrays.equals(getAsymmetricCoords(greaterXNeighborCoords), compareCoords)) {
				count++;
			}
		}
		if (!Arrays.equals(smallerXNeighborCoords, compareCoords)) {
			if (Arrays.equals(getAsymmetricCoords(smallerXNeighborCoords), compareCoords)) {
				count++;
			}
		}
		if (!Arrays.equals(greaterYNeighborCoords, compareCoords)) {
			if (Arrays.equals(getAsymmetricCoords(greaterYNeighborCoords), compareCoords)) {
				count++;
			}
		}
		if (!Arrays.equals(smallerYNeighborCoords, compareCoords)) {
			if (Arrays.equals(getAsymmetricCoords(smallerYNeighborCoords), compareCoords)) {
				count++;
			}
		}
		if (!Arrays.equals(greaterZNeighborCoords, compareCoords)) {
			if (Arrays.equals(getAsymmetricCoords(greaterZNeighborCoords), compareCoords)) {
				count++;
			}
		}
		if (!Arrays.equals(smallerZNeighborCoords, compareCoords)) {
			if (Arrays.equals(getAsymmetricCoords(smallerZNeighborCoords), compareCoords)) {
				count++;
			}
		}
		return count;
	}
	
	private static int[] getAsymmetricCoords(int[] coords){	
		int x = coords[0], y = coords[1], z = coords[2];
		if (x < 0) x = -x;
		if (y < 0) y = -y;
		if (z < 0) z = -z;
		if (x >= y) {
			if (y >= z) {
				//x >= y >= z
				return new int[]{x, y, z};
			} else if (x >= z) { 
				//x >= z > y
				return new int[]{x, z, y};
			} else {
				//z > x >= y
				return new int[]{z, x, y};
			}
		} else if (y >= z) {
			if (x >= z) {
				//y > x >= z
				return new int[]{y, x, z};
			} else {
				//y >= z > x
				return new int[]{y, z, x};
			}
		} else {
			// z > y > x
			return new int[]{z, y, x};
		}
	}
	
	public static void findAEMinAllowedValues() {
		//System.out.println(Integer.BYTES);
		/*System.out.println(Long.MIN_VALUE);
		return;*/
		BigInteger num = BigInteger.valueOf(1);
		BigInteger singleSourceValue = BigInteger.valueOf(-9365);
		BigInteger maxNeighboringValuesDifference = Utils.getAetherMaxNeighboringValuesDifferenceFromSingleSource(4, singleSourceValue);
		while (maxNeighboringValuesDifference.compareTo(BigInteger.valueOf(Short.MAX_VALUE)) > 0) {
			singleSourceValue = singleSourceValue.add(num);
			maxNeighboringValuesDifference = Utils.getAetherMaxNeighboringValuesDifferenceFromSingleSource(4, singleSourceValue);
		}
		System.out.println(singleSourceValue);
//		System.out.println(Integer.MIN_VALUE);
//		System.out.println(Integer.MAX_VALUE);
	}
	
	public static void testAether3DEnclosed2() {
		int side = 31;
		int initialValue = -10000;
		Aether3DEnclosed2 ae1 = new Aether3DEnclosed2(side, side, side, initialValue, side/2, side/2, side/2);
		Aether3DEnclosed ae2 = new Aether3DEnclosed(initialValue, side);
//		checkTotalValueConservation(ae1);
//		stepByStep(ae2);
		compareWithOffset(ae1, ae2, side/2 + 1, side/2 + 1, side/2 + 1);
	}
	
	public static void compareWithOffset(EvolvingLongGrid3D ca1, EvolvingLongGrid3D ca2, int xOffset, int yOffset, int zOffset) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
//				System.out.println("Comparing step " + ca1.getStep());
				for (int z = ca1.getMinZ(), z2 = z + zOffset; z <= ca1.getMaxZ(); z++, z2++) {
					for (int y = ca1.getMinYAtZ(z), y2 = y + yOffset; y <= ca1.getMaxYAtZ(z); y++, y2++) {
						for (int x = ca1.getMinX(y,z), x2 = x + xOffset; x <= ca1.getMaxX(y,z); x++, x2++) {
							if (ca1.getValueAtPosition(x, y, z) != ca2.getValueAtPosition(x2, y2, z2)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y, z) 
										+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x2, y2, z2));
								return;
							}
						}	
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
			if (equal)
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testAsymmetricSection() {
		ShortAether4D ae = new ShortAether4D((short) -10000);
		ShortGrid4D ns = ae.asymmetricSection();
		try {
			do {
				System.out.println("Comparing step " + ae.getStep());
				int maxW = ae.getAsymmetricMaxW();
				for (int w = 0; w <= maxW; w++) {
					for (int x = 0; x <= w; x++) {
						for (int y = 0; y <= x; y++) {
							for (int z = 0; z <= y; z++) {
//								System.out.println("Comparing value at (" + w + ", " + x + ", " + y + ", " + z + ")");
								if (ae.getValueAtAsymmetricPosition(w, x, y, z) != ns.getValueAtPosition(w, x, y, z)) {
									System.out.println("Different value");
									return;
								}
							}
						}
					}
				}
			} while(ae.nextStep());
			System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void test4DZCrossSection() {
		IntAether4D ae = new IntAether4D(-200);
		
		int z = 0;
		IntGrid3D cs = ae.asymmetricSection().crossSectionAtZ(z);
		try {
			do {
				System.out.println("Comparing step " + ae.getStep());
				int maxW = ae.getAsymmetricMaxWAtZ(z);
				if (maxW != cs.getMaxX()) {
					System.out.println("Different max w!");
					return;
				}
				int minW = ae.getAsymmetricMinWAtZ(z);
				if (minW != cs.getMinX()) {
					System.out.println("Different min w!");
					return;
				}
				for (int w = minW; w <= maxW; w++) {
					int maxX = ae.getAsymmetricMaxXAtWZ(w, z);
					if (maxX != cs.getMaxYAtX(w)) {
						System.out.println("Different max x!");
						return;
					}
					int minX = ae.getAsymmetricMinXAtWZ(w, z);
					if (minX != cs.getMinYAtX(w)) {
						System.out.println("Different min x!");
						return;
					}
					for (int x = minX; x <= maxX; x++) {
						int maxY = ae.getAsymmetricMaxY(w, x, z);
						if (maxY != cs.getMaxZ(w, x)) {
							System.out.println("Different max y!");
							return;
						}
						int minY = ae.getAsymmetricMinY(w, x, z);
						if (minY != cs.getMinZ(w, x)) {
							System.out.println("Different min y!");
							return;
						}
						for (int y = minY; y <= maxY; y++) {
							if (ae.getValueAtAsymmetricPosition(w, x, y, z) != cs.getValueAtPosition(w, x, y)) {
								System.out.println("Different value");
								return;
							}
						}
					}
				}
			} while(ae.nextStep());
			System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void sivTesting() {
		long initialValue = 100000;
		SpreadIntegerValue2D ae1 = new SpreadIntegerValue2D(initialValue, 0);
		SpreadIntegerValueSimple2D ae2 = new SpreadIntegerValueSimple2D(initialValue, 0);
		compare(ae1, ae2);
	}
	
	public static void testAetherRandomConfig() {
		Aether3DRandomConfiguration ae1 = new Aether3DRandomConfiguration(3, -10, 10);
		stepByStep(ae1.crossSectionAtZ(0));
	}
	
	public static int findAetherMaxSafeIntValue(int dimension, int minValue) {	
		long maxValue = (long)Integer.MAX_VALUE + 1;
		long maxResultingValue;
		do {
			maxValue--;
			maxResultingValue = minValue + (((maxValue-minValue)/2)*dimension*2);
		} while(maxResultingValue > Integer.MAX_VALUE);
		return (int)maxValue;
	}
	
	public static int findAetherMinSafeIntValue(int dimension, int maxValue) {	
		long minValue = (long)Integer.MAX_VALUE -1;
		long maxResultingValue;
		do {
			minValue++;
			maxResultingValue = minValue + (((maxValue-minValue)/2)*dimension*2);
		} while(maxResultingValue > Integer.MAX_VALUE);
		return (int)minValue;
	}
	
	public static void raceSandpileImplementations() {
		long initialValue = 2000;
		SpreadIntegerValue1D siv1 = new SpreadIntegerValue1D(initialValue, 0);
		SingleSourceLongSandpile1D siv2 = new SingleSourceLongSandpile1D(new SpreadIntegerValueRules(), initialValue);
		race(new EvolvingModel[] { siv1, siv2 });
	}
	
	public static void generateBackup() {
		IntAether3DAsymmetricSectionSwap ca;
		String path = "D:/data/test";
		int initialValue = -1000;
		try {
			ca = new IntAether3DAsymmetricSectionSwap(initialValue, 1024 * 100, path);
			for (int i = 0; i < 100; i++) {
				ca.nextStep();
				System.out.println("step " + ca.getStep());
			}
			String backupPath = path + "/" + ca.getSubFolderPath() + "/backup";
			System.out.println("Backing up at " + backupPath);
			ca.backUp(backupPath, ca.getName() + "_" + ca.getStep());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testAdvance() {
		IntAether3DAsymmetricSectionSwap ca;
		String path = "D:/data/test";
		int initialValue = -1000;
		try {
			ca = new IntAether3DAsymmetricSectionSwap(initialValue, 1024 * 500, path);
			while (ca.nextStep()) {
				System.out.println("step " + ca.getStep());
			}
			System.out.println("Finished");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(EvolvingLongGrid1D ca1, EvolvingLongGrid1D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				//System.out.println("step " + ca1.getStep());
				for (int x = ca2.getMinX(); x <= ca2.getMaxX(); x++) {
					System.out.println(x);
					long a = ca1.getValueAtPosition(x);
					long b = ca2.getValueAtPosition(x);
					if (a != b) {
						equal = false;
						System.out.println("Different value at step " + ca1.getStep() + " (" + x + "): " 
								+ ca1.getClass().getSimpleName() + ":" + a 
								+ " != " + ca2.getClass().getSimpleName() + ":" + b);
						//return;
					}
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
			if (equal)
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(EvolvingIntGrid2D ca1, EvolvingLongGrid2D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				for (int y = ca1.getMinY(); y <= ca1.getMaxY(); y++) {
					for (int x = ca2.getMinX(y); x <= ca2.getMaxX(y); x++) {
						if (ca1.getValueAtPosition(x, y) != ca2.getValueAtPosition(x, y)) {
							equal = false;
							System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + "): " 
									+ ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y) 
									+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y));
						}
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
			if (equal)
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(EvolvingIntGrid2D ca1, EvolvingIntGrid2D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				for (int y = ca1.getMinY(); y <= ca1.getMaxY(); y++) {
					for (int x = ca2.getMinX(y); x <= ca2.getMaxX(y); x++) {
						if (ca1.getValueAtPosition(x, y) != ca2.getValueAtPosition(x, y)) {
							equal = false;
							System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + "): " 
									+ ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y) 
									+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y));
						}
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
			if (equal)
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(EvolvingLongGrid2D ca1, EvolvingLongGrid2D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
//				System.out.println("Comparing step " + ca1.getStep());
				int maxY = ca1.getMaxY();
				for (int y = ca1.getMinY(); y <= maxY; y++) {
					for (int x = ca2.getMinX(y); x <= ca2.getMaxX(y); x++) {
						if (ca1.getValueAtPosition(x, y) != ca2.getValueAtPosition(x, y)) {
							equal = false;
							System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + "): " 
									+ ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y) 
									+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y));
							//return;
						}
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
				if (!equal) {
					return;
				}
			}
			if (equal)
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(EvolvingIntGrid3D ca1, EvolvingIntGrid3D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinX(y,z); x <= ca2.getMaxX(y,z); x++) {
							if (ca1.getValueAtPosition(x, y, z) != ca2.getValueAtPosition(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y, z) 
										+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y, z));
							}
						}	
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
			if (equal)
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(ActionableEvolvingIntGrid3D ca1, EvolvingLongGrid3D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			GridProcessor<IntGrid3D> comparator = new GridProcessor<IntGrid3D>() {
				
				@Override
				public void processGridBlock(IntGrid3D gridBlock) throws Exception {
					for (int x = gridBlock.getMinX(); x <= gridBlock.getMaxX(); x++) {
						for (int z = gridBlock.getMinZAtX(x); z <= gridBlock.getMaxZAtX(x); z++) {
							for (int y = gridBlock.getMinY(x, z); y <= gridBlock.getMaxY(x, z); y++) {
								if (gridBlock.getValueAtPosition(x, y, z) != ca2.getValueAtPosition(x, y, z)) {
									System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
											+ ca1.getClass().getSimpleName() + ":" + gridBlock.getValueAtPosition(x, y, z) 
											+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y, z));
								}	
							}	
						}
					}						
				}
				
				@Override
				public void beforeProcessing() throws Exception {
					// do nothing					
				}
				
				@Override
				public void afterProcessing() throws Exception {
					// do nothing					
				}
			};
			
			while (!finished1 && !finished2) {
				System.out.println("Step " + ca1.getStep());
				ca1.addProcessor(comparator);
				ca1.processGrid();
				ca1.removeProcessor(comparator);
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(ActionableEvolvingLongGrid3D ca1, EvolvingLongGrid3D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			GridProcessor<LongGrid3D> comparator = new GridProcessor<LongGrid3D>() {
				
				@Override
				public void processGridBlock(LongGrid3D gridBlock) throws Exception {
					for (int x = gridBlock.getMinX(); x <= gridBlock.getMaxX(); x++) {
						for (int z = gridBlock.getMinZAtX(x); z <= gridBlock.getMaxZAtX(x); z++) {
							for (int y = gridBlock.getMinY(x, z); y <= gridBlock.getMaxY(x, z); y++) {
								if (gridBlock.getValueAtPosition(x, y, z) != ca2.getValueAtPosition(x, y, z)) {
									System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
											+ ca1.getClass().getSimpleName() + ":" + gridBlock.getValueAtPosition(x, y, z) 
											+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y, z));
								}	
							}	
						}
					}						
				}
				
				@Override
				public void beforeProcessing() throws Exception {
					// do nothing					
				}
				
				@Override
				public void afterProcessing() throws Exception {
					// do nothing			
				}
			};
			
			while (!finished1 && !finished2) {
				System.out.println("Step " + ca1.getStep());
				ca1.addProcessor(comparator);
				ca1.processGrid();
				ca1.removeProcessor(comparator);
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(ActionableEvolvingLongGrid4D ca1, EvolvingLongGrid4D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			GridProcessor<LongGrid4D> comparator = new GridProcessor<LongGrid4D>() {
				
				@Override
				public void processGridBlock(LongGrid4D gridBlock) throws Exception {
					for (int z = gridBlock.getMinZ(); z <= gridBlock.getMaxZ(); z++) {
						for (int y = gridBlock.getMinYAtZ(z); y <= gridBlock.getMaxYAtZ(z); y++) {
							for (int x = gridBlock.getMinXAtYZ(y,z); x <= gridBlock.getMaxXAtYZ(y,z); x++) {
								for (int w = gridBlock.getMinW(x,y,z); w <= gridBlock.getMaxW(x,y,z); w++) {
									if (gridBlock.getValueAtPosition(w, x, y, z) != ca2.getValueAtPosition(w, x, y, z)) {
										System.out.println("Different value at step " + ca1.getStep() + " (" + w + ", " + x + ", " + y + ", " + z + "): " 
												+ ca1.getClass().getSimpleName() + ":" + gridBlock.getValueAtPosition(w, x, y, z) 
												+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(w, x, y, z));
									}
								}
							}	
						}	
					}						
				}
				
				@Override
				public void beforeProcessing() throws Exception {
					// do nothing					
				}
				
				@Override
				public void afterProcessing() throws Exception {
					// do nothing			
				}
			};
			
			while (!finished1 && !finished2) {
				System.out.println("Step " + ca1.getStep());
				ca1.addProcessor(comparator);
				ca1.processGrid();
				ca1.removeProcessor(comparator);
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(EvolvingLongGrid4D ca1, EvolvingLongGrid4D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinXAtYZ(y,z); x <= ca2.getMaxXAtYZ(y,z); x++) {
							for (int w = ca2.getMinW(x,y,z); w <= ca2.getMaxW(x,y,z); w++) {
								if (ca1.getValueAtPosition(w, x, y, z) != ca2.getValueAtPosition(w, x, y, z)) {
									equal = false;
									System.out.println("Different value at step " + ca1.getStep() + " (" + w + ", " + x + ", " + y + ", " + z + "): " 
											+ ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(w, x, y, z) 
											+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(w, x, y, z));
									return;
								}
							}
						}	
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
			if (equal)
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(SymmetricEvolvingShortGrid4D ca1, SymmetricEvolvingShortGrid4D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				System.out.println("Comparing step " + ca1.getStep());
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinXAtYZ(y,z); x <= ca2.getMaxXAtYZ(y,z); x++) {
							for (int w = ca2.getMinW(x,y,z); w <= ca2.getMaxW(x,y,z); w++) {
								if (ca1.getValueAtPosition(w, x, y, z) != ca2.getValueAtPosition(w, x, y, z)) {
									equal = false;
									System.out.println("Different value at step " + ca1.getStep() + " (" + w + ", " + x + ", " + y + ", " + z + "): " 
											+ ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(w, x, y, z) 
											+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(w, x, y, z));
									return;
								}
							}
						}	
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
			if (equal)
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(EvolvingLongGrid4D ca1, EvolvingIntGrid4D ca2) {
		compare(ca2, ca1);
	}
	
	public static void compare(EvolvingIntGrid4D ca1, EvolvingLongGrid4D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				System.out.println("Comparing step " + ca1.getStep());
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinXAtYZ(y,z); x <= ca2.getMaxXAtYZ(y,z); x++) {
							for (int w = ca2.getMinW(x,y,z); w <= ca2.getMaxW(x,y,z); w++) {
								if (ca1.getValueAtPosition(w, x, y, z) != ca2.getValueAtPosition(w, x, y, z)) {
									equal = false;
									System.out.println("Different value at step " + ca1.getStep() + " (" + w + ", " + x + ", " + y + ", " + z + "): " 
											+ ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(w, x, y, z) 
											+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(w, x, y, z));
									return;
								}
							}
						}	
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
			if (equal)
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(EvolvingShortGrid3D ca1,  EvolvingIntGrid3D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				System.out.println("Comparing step " + ca1.getStep());
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinX(y,z); x <= ca2.getMaxX(y,z); x++) {
							if (ca1.getValueAtPosition(x, y, z) != ca2.getValueAtPosition(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y, z) 
										+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y, z));
								return;
							}
						}	
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
			if (equal)
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(EvolvingLongGrid3D ca1, EvolvingIntGrid3D ca2) {
		compare(ca2, ca1);
	}
	
	public static void compare(EvolvingIntGrid3D ca1, EvolvingLongGrid3D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				System.out.println("Comparing step " + ca1.getStep());
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinX(y,z); x <= ca2.getMaxX(y,z); x++) {
//							System.out.println("Comparing position (" + x + ", " + y + ", " + z + ")");
							if (ca1.getValueAtPosition(x, y, z) != ca2.getValueAtPosition(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y, z) 
										+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y, z));
								return;
							}
						}	
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
			if (equal)
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(EvolvingShortGrid3D ca1, EvolvingLongGrid3D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				System.out.println("Comparing step " + ca1.getStep());
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinX(y,z); x <= ca2.getMaxX(y,z); x++) {
							if (ca1.getValueAtPosition(x, y, z) != ca2.getValueAtPosition(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y, z) 
										+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y, z));
								return;
							}
						}	
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
			if (equal)
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(EvolvingLongGrid3D ca1, EvolvingLongGrid3D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
//				System.out.println("Comparing step " + ca1.getStep());
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinX(y,z); x <= ca2.getMaxX(y,z); x++) {
							if (ca1.getValueAtPosition(x, y, z) != ca2.getValueAtPosition(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y, z) 
										+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y, z));
							}
						}	
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
				if (!equal)
					return;
			}
			if (equal)
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void printMinAndMaxValues(EvolvingShortGrid ca) {
		try {
			do {
				short[] minAndMax = ca.getMinAndMaxValue();
				System.out.println("min: " + minAndMax[0] + "\t\tmax: " + minAndMax[1]);
			} while(ca.nextStep());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void printMinAndMaxValues(EvolvingLongGrid ca) {
		try {
			do {
				long[] minAndMax = ca.getMinAndMaxValue();
				System.out.println("min: " + minAndMax[0] + "\t\tmax: " + minAndMax[1]);
			} while(ca.nextStep());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void checkTotalValueConservation(EvolvingLongGrid ca) {
		System.out.println("Checking total value conservation...");
		try {
			long value = ca.getTotalValue(), newValue = value;
			boolean finished = false;
			while (value == newValue && !finished) {
				finished = !ca.nextStep();
				newValue = ca.getTotalValue();
			}
			if (!finished) {
				System.out.println("Total value changed at step " + ca.getStep() + ". Previous value " + value + ", new value " + newValue);
			} else {
				System.out.println("The total value remained constant!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void printTotalValueEvolution(EvolvingLongGrid ca) {
		try {
			do {
				System.out.println("step " + ca.getStep() + ": " + ca.getTotalValue());
			}
			while (ca.nextStep());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void checkTotalValueConservation(EvolvingIntGrid ca) {
		System.out.println("Checking total value conservation...");
		try {
			int value = ca.getTotalValue(), newValue = value;
			boolean finished = false;
			while (value == newValue && !finished) {
				finished = !ca.nextStep();
				newValue = ca.getTotalValue();
			}
			if (!finished) {
				System.out.println("Total value changed at step " + ca.getStep() + ". Previous value " + value + ", new value " + newValue);
			} else {
				System.out.println("The total value remained constant!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep(EvolvingIntGrid2D ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getStep());
				printAsGrid(ca, 0);
				System.out.println("total value " + ca.getTotalValue());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep(EvolvingLongGrid1D ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getStep());
				printAsGrid(ca, 0);
				System.out.println("total value " + ca.getTotalValue());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep(EvolvingLongGrid2D ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getStep());
				printAsGrid(ca, 0);
				System.out.println("total value " + ca.getTotalValue());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public static void race(EvolvingModel... cas) {
		try {
			long millis;
			for (EvolvingModel ca : cas) {
				millis = System.currentTimeMillis();
				while (ca.nextStep());
				System.out.println(ca.getClass().getSimpleName() + ": " + (System.currentTimeMillis() - millis));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void printAsGrid(LongGrid2D m, long backgroundValue) throws Exception {
		int maxX = m.getMaxX(), maxY = m.getMaxY(), minX = m.getMinX(), minY = m.getMinY();
		printAsGrid(m, minX, maxX, minY, maxY, backgroundValue);
	}
	
	public static void printAsGrid(LongGrid1D m, long backgroundValue) {
		int maxX = m.getMaxX(), minX = m.getMinX();
		printAsGrid(m, minX, maxX, backgroundValue);
	}
	
	public static void printAsGrid(IntGrid2D m, int backgroundValue) throws Exception {
		int maxX = m.getMaxX(), maxY = m.getMaxY(), minX = m.getMinX(), minY = m.getMinY();
		printAsGrid(m, minX, maxX, minY, maxY, backgroundValue);
	}
	
	public static void printAsGrid(ShortGrid2D m, short backgroundValue) throws Exception {
		int maxX = m.getMaxX(), maxY = m.getMaxY(), minX = m.getMinX(), minY = m.getMinY();
		printAsGrid(m, minX, maxX, minY, maxY, backgroundValue);
	}
	
	public static void printAsGrid(LongGrid2D m, int minX, int maxX, int minY, int maxY, long backgroundValue) throws Exception {
		int maxDigits = 3;
		for (int y = maxY; y >= minY; y--) {
			for (int x = minX; x <= maxX; x++) {
				int digits = Long.toString(m.getValueAtPosition(x, y)).length();
				if (digits > maxDigits)
					maxDigits = digits;
			}
		}
		String headFootGap = "";
		for (int i = 0; i < maxDigits; i++) {
			headFootGap += "-";
		}
		String headFoot = "+";
		for (int i = minX; i <= maxX; i++) {
			headFoot += headFootGap + "+";
		}
		for (int y = maxY; y >= minY; y--) {
			System.out.println(headFoot);
			for (int x = minX; x <= maxX; x++) {
				String strVal = " ";
				long val = m.getValueAtPosition(x, y);
				if (val != backgroundValue) {
					strVal = val + "";
				}
				System.out.print("|" + padLeft(strVal, ' ', maxDigits));
			}
			System.out.println("|");
		}
		System.out.println(headFoot);
	}
	
	public static void printAsGrid(LongGrid1D m, int minX, int maxX, long backgroundValue) {
		int maxDigits = 3;
		for (int x = minX; x <= maxX; x++) {
			int digits = Long.toString(m.getValueAtPosition(x)).length();
			if (digits > maxDigits)
				maxDigits = digits;
		}
		String headFootGap = "";
		for (int i = 0; i < maxDigits; i++) {
			headFootGap += "-";
		}
		String headFoot = "+";
		for (int i = minX; i <= maxX; i++) {
			headFoot += headFootGap + "+";
		}
		System.out.println(headFoot);
		for (int x = minX; x <= maxX; x++) {
			String strVal = " ";
			long val = m.getValueAtPosition(x);
			if (val != backgroundValue) {
				strVal = val + "";
			}
			System.out.print("|" + padLeft(strVal, ' ', maxDigits));
		}
		System.out.println("|");
		System.out.println(headFoot);
	}
	
	public static void printAsGrid(IntGrid2D m, int minX, int maxX, int minY, int maxY, int backgroundValue) throws Exception {
		int maxDigits = 3;
		for (int y = maxY; y >= minY; y--) {
			for (int x = minX; x <= maxX; x++) {
				int digits = Long.toString(m.getValueAtPosition(x, y)).length();
				if (digits > maxDigits)
					maxDigits = digits;
			}
		}
		String headFootGap = "";
		for (int i = 0; i < maxDigits; i++) {
			headFootGap += "-";
		}
		String headFoot = "+";
		for (int i = minX; i <= maxX; i++) {
			headFoot += headFootGap + "+";
		}
		for (int y = maxY; y >= minY; y--) {
			System.out.println(headFoot);
			for (int x = minX; x <= maxX; x++) {
				String strVal = " ";
				int val = m.getValueAtPosition(x, y);
				if (val != backgroundValue) {
					strVal = val + "";
				}
				System.out.print("|" + padLeft(strVal, ' ', maxDigits));
			}
			System.out.println("|");
		}
		System.out.println(headFoot);
	}
	
	public static void printAsGrid(ShortGrid2D m, int minX, int maxX, int minY, int maxY, short backgroundValue) throws Exception {
		int maxDigits = 3;
		for (int y = maxY; y >= minY; y--) {
			for (int x = minX; x <= maxX; x++) {
				int digits = Long.toString(m.getValueAtPosition(x, y)).length();
				if (digits > maxDigits)
					maxDigits = digits;
			}
		}
		String headFootGap = "";
		for (int i = 0; i < maxDigits; i++) {
			headFootGap += "-";
		}
		String headFoot = "+";
		for (int i = minX; i <= maxX; i++) {
			headFoot += headFootGap + "+";
		}
		for (int y = maxY; y >= minY; y--) {
			System.out.println(headFoot);
			for (int x = minX; x <= maxX; x++) {
				String strVal = " ";
				long val = m.getValueAtPosition(x, y);
				if (val != backgroundValue) {
					strVal = val + "";
				}
				System.out.print("|" + padLeft(strVal, ' ', maxDigits));
			}
			System.out.println("|");
		}
		System.out.println(headFoot);
	}
	
	public static String padLeft(String source, char c, int totalLength) {
		int margin = totalLength - source.length();
		for (int i = 0; i < margin; i++) {
			source = c + source;
		}
		return source;
	}
	
	public static long[][] parseCSVLong2DArray(String pathName) throws IOException {
		long[][] array = null;
		try(BufferedReader br = new BufferedReader(new FileReader(pathName))) {
		    String line = br.readLine();
		    if (line != null) {
		    	List<long[]> rows = new ArrayList<long[]>();
		    	int lineIndex = 1; 
		    	String[] elements = line.split(",");
		    	int colCount = elements.length;
		    	long[] row = new long[colCount];
		    	for (int i = 0; i < colCount; i++) {
		    		row[i] = Long.parseLong(elements[i]);
		    	}
		    	rows.add(row);
		    	line = br.readLine();
		    	while (line != null) {
		    		lineIndex++;
		    		elements = line.split(",");
		    		if (elements.length != colCount)
		    			throw new IllegalArgumentException("Wrong number of elements at line " + lineIndex);
		    		row = new long[colCount];
			    	for (int i = 0; i < colCount; i++) {
			    		row[i] = Long.parseLong(elements[i]);
			    	}
			    	rows.add(row);
			        line = br.readLine();
			    }
		    	int rowCount = rows.size();
		    	array = new long[colCount][rowCount];
		    	for (int y = 0; y < rowCount; y++) {
		    		row = rows.get(y);
	    			for (int x = 0; x < colCount; x++) {
	    				array[x][y] = row[x];
			    	}
		    	}
		    }
		}
		return array;
	}

}
