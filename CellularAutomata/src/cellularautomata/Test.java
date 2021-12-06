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
package cellularautomata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.fraction.BigFraction;

import cellularautomata.automata.SingleSourceLongSandpile1D;
import cellularautomata.automata.aether.Aether2D;
import cellularautomata.automata.aether.Aether3DEnclosed;
import cellularautomata.automata.aether.Aether3DEnclosed2;
import cellularautomata.automata.aether.IntAether3DRandomConfiguration;
import cellularautomata.automata.aether.Aether4D;
import cellularautomata.automata.aether.AetherSimple2D;
import cellularautomata.automata.aether.AetherSimple5D;
import cellularautomata.automata.aether.IntAether3D;
import cellularautomata.automata.aether.IntAether4D;
import cellularautomata.automata.siv.SpreadIntegerValue1D;
import cellularautomata.automata.siv.SpreadIntegerValue2D;
import cellularautomata.automata.siv.SpreadIntegerValueRules;
import cellularautomata.automata.siv.SpreadIntegerValueSimple2D;
import cellularautomata.grid.GridProcessor;
import cellularautomata.grid1d.LongGrid1D;
import cellularautomata.grid1d.ObjectGrid1D;
import cellularautomata.grid2d.ObjectGrid2D;
import cellularautomata.grid2d.ArrayIntGrid2D;
import cellularautomata.grid2d.ArrayNumberGrid2D;
import cellularautomata.grid2d.IntGrid2D;
import cellularautomata.grid2d.LongGrid2D;
import cellularautomata.grid3d.BigFractionGrid3DPattern;
import cellularautomata.grid3d.BigIntGrid3DPattern;
import cellularautomata.grid3d.RegularIntGrid3D;
import cellularautomata.grid3d.Grid3DZCrossSectionCopierProcessor;
import cellularautomata.grid3d.IntGrid3D;
import cellularautomata.grid3d.IntGrid3DXCrossSectionCopierProcessor;
import cellularautomata.grid3d.IntGrid3DZCrossSectionCopierProcessor;
import cellularautomata.grid3d.LongGrid3D;
import cellularautomata.grid3d.LongGrid3DZCrossSectionCopierProcessor;
import cellularautomata.grid3d.NumberGrid3D;
import cellularautomata.grid3d.ObjectGrid3D;
import cellularautomata.grid4d.IntGrid4D;
import cellularautomata.grid4d.LongGrid4D;
import cellularautomata.grid4d.NumberGrid4D;
import cellularautomata.grid5d.IntGrid5D;
import cellularautomata.grid5d.LongGrid5D;
import cellularautomata.model.IntModel;
import cellularautomata.model.LongModel;
import cellularautomata.model.NumericModel;
import cellularautomata.model.Model;
import cellularautomata.model1d.LongModel1D;
import cellularautomata.model1d.NumericModel1D;
import cellularautomata.model1d.SequentialLongModel1D;
import cellularautomata.model2d.IntModel2D;
import cellularautomata.model2d.LongModel2D;
import cellularautomata.model2d.NumericModel2D;
import cellularautomata.model3d.ActionableModel3D;
import cellularautomata.model3d.IntModel3D;
import cellularautomata.model3d.LongModel3D;
import cellularautomata.model3d.NumericModel3D;
import cellularautomata.model4d.ActionableModel4D;
import cellularautomata.model4d.IntModel4D;
import cellularautomata.model4d.LongModel4D;
import cellularautomata.model4d.NumericModel4D;
import cellularautomata.model5d.ActionableModel5D;
import cellularautomata.model5d.IntModel5D;
import cellularautomata.model5d.LongModel5D;
import cellularautomata.numbers.BigInt;

public class Test {
	
	public static void main(String[] args) throws Exception {
		long initialValue = -3000;
		Aether2D ae1 = new Aether2D(initialValue);
		AetherSimple2D ae2 = new AetherSimple2D(initialValue);
		compare(ae1, ae2);
	}
	
	public static void testArrayGrid() {
		System.out.println("Test ArrayGrid2D");
		ArrayIntGrid2D grid;
		System.out.println("nulls");
		try {
			grid = new ArrayIntGrid2D(0, null, null);
		} catch(Exception ex) { 
			ex.printStackTrace(System.out);}
		try {
			grid = new ArrayIntGrid2D(0, new int[] {0, 0}, null);
		} catch(Exception ex) { 
			ex.printStackTrace(System.out);}
		try {
			grid = new ArrayIntGrid2D(0, null, new int[][] {{0, 0}, {0, 0}});
		} catch(Exception ex) { 
			ex.printStackTrace(System.out);}
		System.out.println("empty array");
		try {
			grid = new ArrayIntGrid2D(0, new int[0], new int[0][0]);
		} catch(Exception ex) { 
			ex.printStackTrace(System.out);}		
		System.out.println("different length");
		try {
			grid = new ArrayIntGrid2D(0, new int[] {0, 0, 0}, new int[][] {{0, 0}, {0, 0}});
		} catch(Exception ex) { 
			ex.printStackTrace(System.out);}
		System.out.println("minX = Ineger.MAX_VALUE, values.length = large");
		try {
			grid = new ArrayIntGrid2D(Integer.MAX_VALUE, new int[99999], new int[99999][1]);
		} catch(Exception ex) { 
			ex.printStackTrace(System.out);}
		System.out.println("not convex");
		try {
			grid = new ArrayIntGrid2D(0, new int[] {0, 0, -2, 0, 0}, new int[][] { {1}, {1}, {1, 1, 1, 1, 1}, {1}, {1} });
		} catch(Exception ex) { 
			ex.printStackTrace(System.out);}
		try {
			grid = new ArrayIntGrid2D(0, new int[] {0, 0, 0, 0, 0}, new int[][] { {1}, {1}, {1, 1, 1, 1, 1}, {1}, {1} });
		} catch(Exception ex) { 
			ex.printStackTrace(System.out);}
		try {
			grid = new ArrayIntGrid2D(0, new int[] {-3, -3, -2, -3, -3 }, 
					new int[][] { {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1} });
		} catch(Exception ex) { 
			ex.printStackTrace(System.out);}
		try {
			System.out.println("single dot");
			grid = new ArrayIntGrid2D(Integer.MIN_VALUE, new int[1], new int[][] {{1}});
			printAsGrid(grid, 0);
			System.out.println("single line, horiz, vert, diagonal");
			grid = new ArrayIntGrid2D(Integer.MIN_VALUE, new int[5], new int[][] { {1}, {1}, {1}, {1}, {1} });
			printAsGrid(grid, 0);
			grid = new ArrayIntGrid2D(Integer.MIN_VALUE, new int[1], new int[][] { {1, 1, 1, 1, 1 } });
			printAsGrid(grid, 0);
			grid = new ArrayIntGrid2D(Integer.MIN_VALUE, new int[] {-2, -1, 0, 1, 2 }, new int[][] { {1}, {1}, {1}, {1}, {1} });
			printAsGrid(grid, 0);
			grid = new ArrayIntGrid2D(Integer.MIN_VALUE, new int[] {2, 1, 0, -1, -2 }, new int[][] { {1}, {1}, {1}, {1}, {1} });
			printAsGrid(grid, 0);
			System.out.println("tilted square");
			grid = new ArrayIntGrid2D(Integer.MIN_VALUE, new int[] {0, -1, -2, -3, -2, -1, 0 }, 
					new int[][] { {1}, {1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1}, {1} });
			printAsGrid(grid, 0);
			System.out.println("aligned square");
			grid = new ArrayIntGrid2D(Integer.MIN_VALUE, new int[] {-3, -3, -3, -3, -3 }, 
					new int[][] { {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1} });
			printAsGrid(grid, 0);
			System.out.println("object grid");
			BigInt[][] array = testGenericArrayBySample(BigInt.ZERO, 1, 1);
			array[0][0] = BigInt.ONE;
			ArrayNumberGrid2D<BigInt> nubergrid = new ArrayNumberGrid2D<BigInt>(0, new int[1], array);
			printAsGrid(nubergrid, BigInt.ZERO);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T[][] testGenericArrayBySample(T sample, int width, int height) {
		Class<T> clazz = (Class<T>) sample.getClass();
		Class<T[]> arrayClass = (Class<T[]>) Array.newInstance(clazz, 0).getClass();
		T[][] arr = (T[][]) Array.newInstance(arrayClass, width);
		for (int i = 0; i < arr.length; i++) {
			arr[i] = (T[]) Array.newInstance(clazz, height);
		}
		return (T[][]) arr;
	}
	
	public static void test2DDiagonals() throws Exception {
		int[][][] sourceValues;
		int[][] resultValues;
		RegularIntGrid3D grid;
		IntGrid2D diagonal;
		int side = 20;
		int offset = 5;
		ThreadLocalRandom random = ThreadLocalRandom.current();
		//xy 
		for (int off = 0; off <= offset; off += offset) {
			sourceValues = new int[side][side][side];
			resultValues = new int[side][side];
			for (int x = 0; x < side; x++) {
				for (int z = 0; z < side; z++) {
					int value = random.nextInt();
					int y = x+off;
					if (y < side) {
						sourceValues[x][x+off][z] = value;
						resultValues[z][x] = value;	
					}
				}
			}			
			grid = new RegularIntGrid3D(sourceValues, 0, 0, 0);
			diagonal = grid.diagonalCrossSectionOnXY(off);
			compare(diagonal, resultValues, 0, 0);
		}
		//xz
		for (int off = 0; off <= offset; off += offset) {
			sourceValues = new int[side][side][side];
			resultValues = new int[side][side];
			for (int x = 0; x < side; x++) {
				for (int y = 0; y < side; y++) {
					int value = random.nextInt();
					int z = x+off;
					if (z < side) {
						sourceValues[x][y][z] = value;
						resultValues[x][y] = value;
					}
				}
			}			
			grid = new RegularIntGrid3D(sourceValues, 0, 0, 0);
			diagonal = grid.diagonalCrossSectionOnXZ(off);
			compare(diagonal, resultValues, 0, 0);
		}
		//yz
		for (int off = 0; off <= offset; off += offset) {
			sourceValues = new int[side][side][side];
			resultValues = new int[side][side];
			for (int x = 0; x < side; x++) {
				for (int y = 0; y < side; y++) {
					int value = random.nextInt();
					int z = y+off;
					if (z < side) {
						sourceValues[x][y][z] = value;
						resultValues[x][y] = value;
					}
				}
			}			
			grid = new RegularIntGrid3D(sourceValues, 0, 0, 0);
			diagonal = grid.diagonalCrossSectionOnYZ(off);
			compare(diagonal, resultValues, 0, 0);
		}
	}
	
	public static void compare(IntGrid2D grid, int[][] array, int xOffset, int yOffset) throws Exception {
		int maxX = grid.getMaxX();
		for (int x = grid.getMinX(); x <= maxX; x++) {
			int localMaxY = grid.getMaxY(x);
			for (int y = grid.getMinY(x); y <= localMaxY; y++) {
				if (grid.getFromPosition(x, y) != array[x + xOffset][y + yOffset]) {
					System.out.println("Different value at (" + x + ", " + y + "): " + grid.getFromPosition(x, y) + " != " + array[x + xOffset][y + yOffset]);
					return;
				}
			}
		}
		System.out.println("Equal!");
	}
	
	public static void testBigIntValueOfPerformance() {
		int count = 100000000;
		BigInt total, bigIntIncrement;
		int increment;
		long millis;
		millis = System.currentTimeMillis();
		total = BigInt.ZERO;
		increment = 0;
		for (int i = 0; i < count; i++) {
			increment++;
			total = total.add(BigInt.valueOf(increment));
		}
		System.out.println(System.currentTimeMillis() - millis);
		System.out.println(total);
		millis = System.currentTimeMillis();
		total = BigInt.ZERO;
		bigIntIncrement = BigInt.ZERO;
		for (int i = 0; i < count; i++) {
			bigIntIncrement = bigIntIncrement.add(BigInt.ONE);
			total = total.add(bigIntIncrement);
		}
		System.out.println(System.currentTimeMillis() - millis);
		System.out.println(total);
	}
	
	public static void queryAether5DNeighborhood(String[] args) {
		long initialValue = Long.parseLong(args[0]);
		int step = Integer.parseInt(args[1]);
		int v = Integer.parseInt(args[2]);
		int w = Integer.parseInt(args[3]);
		int x = Integer.parseInt(args[4]);
		int y = Integer.parseInt(args[5]);
		int z = Integer.parseInt(args[6]);
		AetherSimple5D ae = new AetherSimple5D(initialValue);
		System.out.println("Initial value: " + initialValue);
		for (int i = 0; i < step; i++) {
			System.out.print("Step: " + i + "\r");
			ae.nextStep();
		}
		System.out.println("Step: " + step);
		printVonNeumannNeighborhood(ae, v, w, x, y, z);
	}
	
	public static void queryAether4DNeighborhood(String[] args) {
		long initialValue = Long.parseLong(args[0]);
		int step = Integer.parseInt(args[1]);
		int w = Integer.parseInt(args[2]);
		int x = Integer.parseInt(args[3]);
		int y = Integer.parseInt(args[4]);
		int z = Integer.parseInt(args[5]);
		Aether4D ae = new Aether4D(initialValue);
		System.out.println("Initial value: " + initialValue);
		for (int i = 0; i < step; i++) {
			System.out.print("Step: " + i + "\r");
			ae.nextStep();
		}
		System.out.println("Step: " + step);
		printVonNeumannNeighborhood(ae, w, x, y, z);
	}
	
	public static void testSort() {
		int testCount = 1000000;
		int arrayLength = 6;
		
		int lengthMinusOne = arrayLength - 1;
		ThreadLocalRandom random = ThreadLocalRandom.current();
		boolean error = false;
		int[][] array2 = new int[arrayLength][];
		for (int i = 0; i < testCount; i++) {
			int[] array = new int[arrayLength];
			for (int j = 0; j < arrayLength; j++) {
				array[j] = random.nextInt();
			}
			Utils.sortNeighborsByValueDesc(arrayLength, array, array2);
			error = false;
//			System.out.println(Arrays.toString(array));
			for (int j = 0; j < lengthMinusOne; j++) {
				if (array[j] < array[j + 1]) {
					System.out.println("Error!");
					error = true;
					break;
				}
			}
			if (error) {
				break;
			}
		}
		if (!error) {
			System.out.println("Success!");
		}
	}
	
	public static void timeIntAether3D(int singleSource) {
		IntAether3D ae1 = new IntAether3D(singleSource);
		long millis = System.currentTimeMillis();
		while(ae1.nextStep());
		System.out.println(System.currentTimeMillis() - millis);
	}
	
	public static void printVonNeumannNeighborhood(LongGrid3D grid, int x, int y, int z) {
		try {
			//center
			System.out.println("(" + x + ", " + y + ", " + z + "): " + grid.getFromPosition(x, y, z));
			//gx
			System.out.println("("+ (x + 1) + ", " + y + ", " + z + "): " + grid.getFromPosition(x + 1, y, z));
			//sx
			System.out.println("("+ (x - 1) + ", " + y + ", " + z + "): " + grid.getFromPosition(x - 1, y, z));
			//gy
			System.out.println("("+ x + ", " + (y + 1) + ", " + z + "): " + grid.getFromPosition(x, y + 1, z));
			//sy
			System.out.println("("+ x + ", " + (y - 1) + ", " + z + "): " + grid.getFromPosition(x, y - 1, z));
			//gz
			System.out.println("("+ x + ", " + y + ", " + (z + 1) + "): " + grid.getFromPosition(x, y, z + 1));
			//sz
			System.out.println("("+ x + ", " + y + ", " + (z - 1) + "): " + grid.getFromPosition(x, y, z - 1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public static void printVonNeumannNeighborhood(LongGrid4D grid, int w, int x, int y, int z) {
		try {
			//center
			System.out.println("(" + w + ", " + x + ", " + y + ", " + z + "): " + grid.getFromPosition(w, x, y, z));
			//gw
			System.out.println("(" + (w + 1) + ", " + x + ", " + y + ", " + z + "): " + grid.getFromPosition(w + 1, x, y, z));
			//sw
			System.out.println("(" + (w - 1) + ", " + x + ", " + y + ", " + z + "): " + grid.getFromPosition(w - 1, x, y, z));
			//gx
			System.out.println("(" + w + ", " + (x + 1) + ", " + y + ", " + z + "): " + grid.getFromPosition(w, x + 1, y, z));
			//sx
			System.out.println("(" + w + ", " + (x - 1) + ", " + y + ", " + z + "): " + grid.getFromPosition(w, x - 1, y, z));
			//gy
			System.out.println("(" + w + ", " + x + ", " + (y + 1) + ", " + z + "): " + grid.getFromPosition(w, x, y + 1, z));
			//sy
			System.out.println("(" + w + ", " + x + ", " + (y - 1) + ", " + z + "): " + grid.getFromPosition(w, x, y - 1, z));
			//gz
			System.out.println("(" + w + ", " + x + ", " + y + ", " + (z + 1) + "): " + grid.getFromPosition(w, x, y, z + 1));
			//sz
			System.out.println("(" + w + ", " + x + ", " + y + ", " + (z - 1) + "): " + grid.getFromPosition(w, x, y, z - 1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void printVonNeumannNeighborhood(LongGrid5D grid, int v, int w, int x, int y, int z) {
		try {
			//center
			System.out.println("(" + v + ", " + w + ", " + x + ", " + y + ", " + z + "): " + grid.getFromPosition(v, w, x, y, z));
			//gv
			System.out.println("(" + (v + 1) + ", " + w + ", " + x + ", " + y + ", " + z + "): " + grid.getFromPosition(v + 1, w, x, y, z));
			//sv
			System.out.println("(" + (v - 1) + ", " + w + ", " + x + ", " + y + ", " + z + "): " + grid.getFromPosition(v - 1, w, x, y, z));
			//gw
			System.out.println("(" + v + ", " + (w + 1) + ", " + x + ", " + y + ", " + z + "): " + grid.getFromPosition(v, w + 1, x, y, z));
			//sw
			System.out.println("(" + v + ", " + (w - 1) + ", " + x + ", " + y + ", " + z + "): " + grid.getFromPosition(v, w - 1, x, y, z));
			//gx
			System.out.println("(" + v + ", " + w + ", " + (x + 1) + ", " + y + ", " + z + "): " + grid.getFromPosition(v, w, x + 1, y, z));
			//sx
			System.out.println("(" + v + ", " + w + ", " + (x - 1) + ", " + y + ", " + z + "): " + grid.getFromPosition(v, w, x - 1, y, z));
			//gy
			System.out.println("(" + v + ", " + w + ", " + x + ", " + (y + 1) + ", " + z + "): " + grid.getFromPosition(v, w, x, y + 1, z));
			//sy
			System.out.println("(" + v + ", " + w + ", " + x + ", " + (y - 1) + ", " + z + "): " + grid.getFromPosition(v, w, x, y - 1, z));
			//gz
			System.out.println("(" + v + ", " + w + ", " + x + ", " + y + ", " + (z + 1) + "): " + grid.getFromPosition(v, w, x, y, z + 1));
			//sz
			System.out.println("(" + v + ", " + w + ", " + x + ", " + y + ", " + (z - 1) + "): " + grid.getFromPosition(v, w, x, y, z - 1));
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	public static void compareWithOffset(LongModel3D ca1, LongModel3D ca2, int xOffset, int yOffset, int zOffset) {
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
							if (ca1.getFromPosition(x, y, z) != ca2.getFromPosition(x2, y2, z2)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(x, y, z) 
										+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x2, y2, z2));
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
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testAsymmetricSection() {
		IntAether4D ae = new IntAether4D((short) -10000);
		IntGrid4D ns = ae.asymmetricSection();
		try {
			do {
				System.out.println("Comparing step " + ae.getStep());
				int maxW = ae.getAsymmetricMaxW();
				for (int w = 0; w <= maxW; w++) {
					for (int x = 0; x <= w; x++) {
						for (int y = 0; y <= x; y++) {
							for (int z = 0; z <= y; z++) {
//								System.out.println("Comparing value at (" + w + ", " + x + ", " + y + ", " + z + ")");
								if (ae.getFromAsymmetricPosition(w, x, y, z) != ns.getFromPosition(w, x, y, z)) {
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
							if (ae.getFromAsymmetricPosition(w, x, y, z) != cs.getFromPosition(w, x, y)) {
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
		IntAether3DRandomConfiguration ae1 = new IntAether3DRandomConfiguration(3, -10, 10);
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
		race(new Model[] { siv1, siv2 });
	}
	
	public static void compare(LongModel1D ca1, LongModel1D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				//System.out.println("step " + ca1.getStep());
				for (int x = ca2.getMinX(); x <= ca2.getMaxX(); x++) {
					System.out.println(x);
					long a = ca1.getFromPosition(x);
					long b = ca2.getFromPosition(x);
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
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(IntModel2D ca1, LongModel2D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				for (int y = ca1.getMinY(); y <= ca1.getMaxY(); y++) {
					for (int x = ca2.getMinX(y); x <= ca2.getMaxX(y); x++) {
						if (ca1.getFromPosition(x, y) != ca2.getFromPosition(x, y)) {
							equal = false;
							System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + "): " 
									+ ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(x, y) 
									+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x, y));
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
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(IntModel2D ca1, IntModel2D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				for (int y = ca1.getMinY(); y <= ca1.getMaxY(); y++) {
					for (int x = ca2.getMinX(y); x <= ca2.getMaxX(y); x++) {
						if (ca1.getFromPosition(x, y) != ca2.getFromPosition(x, y)) {
							equal = false;
							System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + "): " 
									+ ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(x, y) 
									+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x, y));
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
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(LongModel2D ca1, LongModel2D ca2) {
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
						if (ca1.getFromPosition(x, y) != ca2.getFromPosition(x, y)) {
							equal = false;
							System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + "): " 
									+ ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(x, y) 
									+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x, y));
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
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(IntModel3D ca1, IntModel3D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinX(y,z); x <= ca2.getMaxX(y,z); x++) {
							if (ca1.getFromPosition(x, y, z) != ca2.getFromPosition(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(x, y, z) 
										+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x, y, z));
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
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(LongModel3D ca1, ActionableModel3D<IntGrid3D> ca2) {
		compare(ca2 , ca1);
	}
	
	public static void compare(ActionableModel3D<IntGrid3D> ca1, LongModel3D ca2) {
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
								if (gridBlock.getFromPosition(x, y, z) != ca2.getFromPosition(x, y, z)) {
									System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
											+ ca1.getClass().getSimpleName() + ":" + gridBlock.getFromPosition(x, y, z) 
											+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x, y, z));
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
	
	public static void compare2(ActionableModel3D<LongGrid3D> ca1, LongModel3D ca2) {
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
//								System.out.println("Comparing position (" + x + ", " + y + ", " + z + ")");
								if (gridBlock.getFromPosition(x, y, z) != ca2.getFromPosition(x, y, z)) {
									System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
											+ ca1.getClass().getSimpleName() + ":" + gridBlock.getFromPosition(x, y, z) 
											+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x, y, z));
									//return;
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
	
	public static void compare(LongModel4D ca1, ActionableModel4D<LongGrid4D> ca2) {
		compare(ca2, ca1);
	}
	
	public static void compare(ActionableModel4D<LongGrid4D> ca1, LongModel4D ca2) {
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
									if (gridBlock.getFromPosition(w, x, y, z) != ca2.getFromPosition(w, x, y, z)) {
										System.out.println("Different value at step " + ca1.getStep() + " (" + w + ", " + x + ", " + y + ", " + z + "): " 
												+ ca1.getClass().getSimpleName() + ":" + gridBlock.getFromPosition(w, x, y, z) 
												+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(w, x, y, z));
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
	
	public static void compare(IntModel4D ca1, ActionableModel4D<IntGrid4D> ca2) {
		compare(ca2, ca1);
	}
	
	public static void compare(ActionableModel4D<IntGrid4D> ca1, IntModel4D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			GridProcessor<IntGrid4D> comparator = new GridProcessor<IntGrid4D>() {
				
				@Override
				public void processGridBlock(IntGrid4D gridBlock) throws Exception {
					for (int z = gridBlock.getMinZ(); z <= gridBlock.getMaxZ(); z++) {
						for (int y = gridBlock.getMinYAtZ(z); y <= gridBlock.getMaxYAtZ(z); y++) {
							for (int x = gridBlock.getMinXAtYZ(y,z); x <= gridBlock.getMaxXAtYZ(y,z); x++) {
								for (int w = gridBlock.getMinW(x,y,z); w <= gridBlock.getMaxW(x,y,z); w++) {
									if (gridBlock.getFromPosition(w, x, y, z) != ca2.getFromPosition(w, x, y, z)) {
										System.out.println("Different value at step " + ca1.getStep() + " (" + w + ", " + x + ", " + y + ", " + z + "): " 
												+ ca1.getClass().getSimpleName() + ":" + gridBlock.getFromPosition(w, x, y, z) 
												+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(w, x, y, z));
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
	
	public static void compare(LongModel5D ca1, ActionableModel5D<LongGrid5D> ca2) {
		compare(ca2, ca1);
	}
	
	public static void compare(ActionableModel5D<LongGrid5D> ca1, LongModel5D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			GridProcessor<LongGrid5D> comparator = new GridProcessor<LongGrid5D>() {
				
				@Override
				public void processGridBlock(LongGrid5D gridBlock) throws Exception {
					for (int z = gridBlock.getMinZ(); z <= gridBlock.getMaxZ(); z++) {
						for (int y = gridBlock.getMinYAtZ(z); y <= gridBlock.getMaxYAtZ(z); y++) {
							for (int x = gridBlock.getMinXAtYZ(y,z); x <= gridBlock.getMaxXAtYZ(y,z); x++) {
								for (int w = gridBlock.getMinWAtXYZ(x,y,z); w <= gridBlock.getMaxWAtXYZ(x,y,z); w++) {
									for (int v = gridBlock.getMinV(w,x,y,z); v <= gridBlock.getMaxV(w,x,y,z); v++) {
										if (gridBlock.getFromPosition(v, w, x, y, z) != ca2.getFromPosition(v, w, x, y, z)) {
											System.out.println("Different value at step " + ca1.getStep() + " (" + v + ", " + w + ", " + x + ", " + y + ", " + z + "): " 
													+ ca1.getClass().getSimpleName() + ":" + gridBlock.getFromPosition(v, w, x, y, z) 
													+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(v, w, x, y, z));
										}
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
	
	public static void compare(IntModel5D ca1, ActionableModel5D<IntGrid5D> ca2) {
		compare(ca2, ca1);
	}
	
	public static void compare(ActionableModel5D<IntGrid5D> ca1, IntModel5D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			GridProcessor<IntGrid5D> comparator = new GridProcessor<IntGrid5D>() {
				
				@Override
				public void processGridBlock(IntGrid5D gridBlock) throws Exception {
					for (int z = gridBlock.getMinZ(); z <= gridBlock.getMaxZ(); z++) {
						for (int y = gridBlock.getMinYAtZ(z); y <= gridBlock.getMaxYAtZ(z); y++) {
							for (int x = gridBlock.getMinXAtYZ(y,z); x <= gridBlock.getMaxXAtYZ(y,z); x++) {
								for (int w = gridBlock.getMinWAtXYZ(x,y,z); w <= gridBlock.getMaxWAtXYZ(x,y,z); w++) {
									for (int v = gridBlock.getMinV(w,x,y,z); v <= gridBlock.getMaxV(w,x,y,z); v++) {
										if (gridBlock.getFromPosition(v, w, x, y, z) != ca2.getFromPosition(v, w, x, y, z)) {
											System.out.println("Different value at step " + ca1.getStep() + " (" + v + ", " + w + ", " + x + ", " + y + ", " + z + "): " 
													+ ca1.getClass().getSimpleName() + ":" + gridBlock.getFromPosition(v, w, x, y, z) 
													+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(v, w, x, y, z));
										}
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
	
	public static <T extends Number & FieldElement<T> & Comparable<T>> void 
		compare(ActionableModel3D<NumberGrid3D<T>> ca1, NumericModel3D<T> ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			GridProcessor<NumberGrid3D<T>> comparator = new GridProcessor<NumberGrid3D<T>>() {
				
				@Override
				public void processGridBlock(NumberGrid3D<T> gridBlock) throws Exception {
					for (int x = gridBlock.getMinX(); x <= gridBlock.getMaxX(); x++) {
						for (int z = gridBlock.getMinZAtX(x); z <= gridBlock.getMaxZAtX(x); z++) {
							for (int y = gridBlock.getMinY(x, z); y <= gridBlock.getMaxY(x, z); y++) {
								if (!gridBlock.getFromPosition(x, y, z).equals(ca2.getFromPosition(x, y, z))) {
									System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
											+ ca1.getClass().getSimpleName() + ":" + gridBlock.getFromPosition(x, y, z) 
											+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x, y, z));
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
	
	public static <T extends Number & FieldElement<T> & Comparable<T>> void 
		compare(ActionableModel4D<NumberGrid4D<T>> ca1, NumericModel4D<T> ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			GridProcessor<NumberGrid4D<T>> comparator = new GridProcessor<NumberGrid4D<T>>() {
				
				@Override
				public void processGridBlock(NumberGrid4D<T> gridBlock) throws Exception {
					int minW = gridBlock.getMinW(), maxW = gridBlock.getMaxW();
					for (int w = minW; w <= maxW; w++) {
						int minX = gridBlock.getMinXAtW(w), maxX = gridBlock.getMaxXAtW(w);
						for (int x = minX; x <= maxX; x++) {
							int minY = gridBlock.getMinYAtWX(w, x), maxY = gridBlock.getMaxYAtWX(w, x);
							for (int y = minY; y <= maxY; y++) {
								int minZ = gridBlock.getMinZ(w, x, y), maxZ = gridBlock.getMaxZ(w, x, y);
								for (int z = minZ; z <= maxZ; z++) {
									if (!gridBlock.getFromPosition(w, x, y, z).equals(ca2.getFromPosition(w, x, y, z))) {
										System.out.println("Different value at step " + ca1.getStep() 
											+ " (" + w + ", " + x + ", " + y + ", " + z + "): " 
												+ ca1.getClass().getSimpleName() + ":" + gridBlock.getFromPosition(w, x, y, z) 
												+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(w, x, y, z));
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
	
	public static void compare(LongModel4D ca1, LongModel4D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				System.out.println("step " + ca1.getStep());
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinXAtYZ(y,z); x <= ca2.getMaxXAtYZ(y,z); x++) {
							for (int w = ca2.getMinW(x,y,z); w <= ca2.getMaxW(x,y,z); w++) {
								if (ca1.getFromPosition(w, x, y, z) != ca2.getFromPosition(w, x, y, z)) {
									equal = false;
									System.out.println("Different value at step " + ca1.getStep() + " (" + w + ", " + x + ", " + y + ", " + z + "): " 
											+ ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(w, x, y, z) 
											+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(w, x, y, z));
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
				if (!equal)
					break;
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(LongModel4D ca1, IntModel4D ca2) {
		compare(ca2, ca1);
	}
	
	public static void compare(IntModel4D ca1, LongModel4D ca2) {
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
								if (ca1.getFromPosition(w, x, y, z) != ca2.getFromPosition(w, x, y, z)) {
									equal = false;
									System.out.println("Different value at step " + ca1.getStep() + " (" + w + ", " + x + ", " + y + ", " + z + "): " 
											+ ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(w, x, y, z) 
											+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(w, x, y, z));
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
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(IntModel4D ca1, IntModel4D ca2) {
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
								if (ca1.getFromPosition(w, x, y, z) != ca2.getFromPosition(w, x, y, z)) {
									equal = false;
									System.out.println("Different value at step " + ca1.getStep() + " (" + w + ", " + x + ", " + y + ", " + z + "): " 
											+ ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(w, x, y, z) 
											+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(w, x, y, z));
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
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(LongModel3D ca1, IntModel3D ca2) {
		compare(ca2, ca1);
	}
	
	public static void compare(IntModel3D ca1, LongModel3D ca2) {
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
							if (ca1.getFromPosition(x, y, z) != ca2.getFromPosition(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x, y, z) 
										+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(x, y, z));
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
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(LongModel3D ca1, LongModel3D ca2) {
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
							if (ca1.getFromPosition(x, y, z) != ca2.getFromPosition(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x, y, z) 
										+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(x, y, z));
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
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compareInverted(LongModel3D ca1, LongModel3D ca2) {
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
							if (ca1.getFromPosition(x, y, z) != -ca2.getFromPosition(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x, y, z) 
										+ " != - " + ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(x, y, z));
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
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static <T extends Number & FieldElement<T> & Comparable<T>> void compare(NumericModel3D<T> ca1, LongModel3D ca2) {
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
							if (ca1.getFromPosition(x, y, z).longValue() != ca2.getFromPosition(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x, y, z) 
										+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(x, y, z));
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
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static <T extends Number & FieldElement<T> & Comparable<T>> void compare(NumericModel4D<T> ca1, LongModel4D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
//				System.out.println("Comparing step " + ca1.getStep());
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinXAtYZ(y,z); x <= ca2.getMaxXAtYZ(y,z); x++) {
							for (int w = ca2.getMinW(x,y,z); w <= ca2.getMaxW(x,y,z); w++) {
								if (ca1.getFromPosition(w, x, y, z).longValue() != ca2.getFromPosition(w, x, y, z)) {
									equal = false;
									System.out.println("Different value at step " + ca1.getStep() + " (" + w + ", " + x + ", " + y + ", " + z + "): " 
											+ ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(w, x, y, z) 
											+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(w, x, y, z));
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
				if (!equal)
					return;
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void comparePatterns(NumericModel3D<BigInt> ca1, NumericModel3D<BigInt> ca2, double maxDifference) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				BigIntGrid3DPattern<NumericModel3D<BigInt>> p1 = new BigIntGrid3DPattern<NumericModel3D<BigInt>>(ca1);
				BigIntGrid3DPattern<NumericModel3D<BigInt>> p2 = new BigIntGrid3DPattern<NumericModel3D<BigInt>>(ca2);
				System.out.println("Comparing step " + ca1.getStep());
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinX(y,z); x <= ca2.getMaxX(y,z); x++) {
							double val1 = p1.getFromPosition(x, y, z).doubleValue(),
									val2 = p2.getFromPosition(x, y, z).doubleValue();
							if (Math.abs(val1 - val2) > maxDifference) {
								equal = false;
								System.out.println("Difference in pattern at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca2.getClass().getSimpleName() + ":" + val2 
										+ " != " + ca1.getClass().getSimpleName() + ":" + val1);
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
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void comparePatterns2(NumericModel3D<BigFraction> ca1, NumericModel3D<BigInt> ca2, double maxDifference) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				BigFractionGrid3DPattern p1 = new BigFractionGrid3DPattern(ca1);
				BigIntGrid3DPattern<NumericModel3D<BigInt>> p2 = new BigIntGrid3DPattern<NumericModel3D<BigInt>>(ca2);
				System.out.println("Comparing step " + ca1.getStep());
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinX(y,z); x <= ca2.getMaxX(y,z); x++) {
							double val1 = p1.getFromPosition(x, y, z).doubleValue(),
									val2 = p2.getFromPosition(x, y, z).doubleValue();
							if (Math.abs(val1 - val2) > maxDifference) {
								equal = false;
								System.out.println("Difference in pattern at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca2.getClass().getSimpleName() + ":" + val2 
										+ " != " + ca1.getClass().getSimpleName() + ":" + val1);
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
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void printAveragePatternDifferenceAtEachStep(NumericModel3D<BigFraction> ca1, NumericModel3D<BigInt> ca2) {
		try {
			boolean finished1 = false;
			boolean finished2 = false;
			while (!finished1 && !finished2) {
				System.out.println("Average difference in pattern at step " + ca1.getStep() + ": " + getAveragePatternDifference(ca1, ca2));
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static double getAveragePatternDifference(NumericModel3D<BigFraction> ca1, 
			NumericModel3D<BigInt> ca2) throws Exception {
		BigFractionGrid3DPattern p1 = new BigFractionGrid3DPattern(ca1);
		BigIntGrid3DPattern<NumericModel3D<BigInt>> p2 = new BigIntGrid3DPattern<NumericModel3D<BigInt>>(ca2);
		double totalDifference = 0;
		long positionCount = 0;
		int maxZ = ca1.getMaxZ();
		for (int z = ca1.getMinZ(); z <= maxZ; z++) {
			int maxY = ca1.getMaxYAtZ(z);
			for (int y = ca1.getMinYAtZ(z); y <= maxY; y++) {
				int minX = ca1.getMinX(y,z);
				int maxX = ca1.getMaxX(y,z);
				for (int x = minX; x <= maxX; x++) {
					double val1 = p1.getFromPosition(x, y, z).doubleValue(),
							val2 = p2.getFromPosition(x, y, z).doubleValue();
					double difference = val1 - val2;
					if (difference < 0) {
						difference = -difference;
					}
					totalDifference += difference;
					positionCount++;
				}	
			}	
		}
		return totalDifference/positionCount;
	}
	
	public static <T extends Number & FieldElement<T> & Comparable<T>> void compare(NumericModel1D<T> ca1, LongModel1D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
//				System.out.println("Comparing step " + ca1.getStep());
				for (int x = ca2.getMinX(); x <= ca2.getMaxX(); x++) {
					if (ca1.getFromPosition(x).longValue() != ca2.getFromPosition(x)) {
						equal = false;
						System.out.println("Different value at step " + ca1.getStep() + " (" + x + "): " 
								+ ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x) 
								+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(x));
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
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static <T extends Number & FieldElement<T> & Comparable<T>> void compare(NumericModel3D<T> ca1, IntModel3D ca2) {
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
							if (ca1.getFromPosition(x, y, z).intValue() != ca2.getFromPosition(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x, y, z) 
										+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(x, y, z));
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
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static <T extends FieldElement<T> & Comparable<T>> void compare(NumericModel3D<T> ca1, NumericModel3D<T> ca2) {
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
							if (ca1.getFromPosition(x, y, z).compareTo(ca2.getFromPosition(x, y, z)) != 0) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x, y, z) 
										+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(x, y, z));
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
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static <T extends FieldElement<T> & Comparable<T>> void compare(NumericModel4D<T> ca1, NumericModel4D<T> ca2) {
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
							for (int w = ca2.getMinW(x,y,z); w <= ca2.getMaxW(w,y,z); w++) {
								if (ca1.getFromPosition(w, x, y, z).compareTo(ca2.getFromPosition(w, x, y, z)) != 0) {
									equal = false;
									System.out.println("Different value at step " + ca1.getStep() + " (" + w + ", " + x + ", " + y + ", " + z + "): " 
											+ ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(w, x, y, z) 
											+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(w, x, y, z));
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
				if (!equal)
					return;
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(LongModel5D ca1, LongModel5D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				System.out.println("step " + ca1.getStep());
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca1.getMinXAtYZ(y,z); x <= ca1.getMaxXAtYZ(y,z); x++) {
							for (int w = ca1.getMinWAtXYZ(x,y,z); w <= ca1.getMaxWAtXYZ(x,y,z); w++) {
								for (int v = ca1.getMinV(w,x,y,z); v <= ca1.getMaxV(w,x,y,z); v++) {
									if (ca1.getFromPosition(v, w, x, y, z) != ca2.getFromPosition(v, w, x, y, z)) {
										equal = false;
										System.out.println("Different value at step " + ca1.getStep() + " (" + v + ", " + w + ", " + x + ", " + y + ", " + z + "): " 
												+ ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(v, w, x, y, z) 
												+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(v, w, x, y, z));
									}
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
				if (!equal)
					break;
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(LongModel5D ca1, IntModel5D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				System.out.println("step " + ca1.getStep());
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca1.getMinXAtYZ(y,z); x <= ca1.getMaxXAtYZ(y,z); x++) {
							for (int w = ca1.getMinWAtXYZ(x,y,z); w <= ca1.getMaxWAtXYZ(x,y,z); w++) {
								for (int v = ca1.getMinV(w,x,y,z); v <= ca1.getMaxV(w,x,y,z); v++) {
									if (ca1.getFromPosition(v, w, x, y, z) != ca2.getFromPosition(v, w, x, y, z)) {
										equal = false;
										System.out.println("Different value at step " + ca1.getStep() + " (" + v + ", " + w + ", " + x + ", " + y + ", " + z + "): " 
												+ ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(v, w, x, y, z) 
												+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(v, w, x, y, z));
									}
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
				if (!equal)
					break;
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(SequentialLongModel1D ca1, LongModel1D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				Iterator<Long> iterator1 = ca1.iterator();
				Iterator<Long> iterator2 = ca2.iterator();
//				System.out.println("Comparing step " + ca1.getStep());
				while (iterator1.hasNext() && iterator2.hasNext()) {
					long value1 = iterator1.next();
					long value2 = iterator2.next();
					if (value1 != value2) {
						equal = false;
						System.out.println("Different value at step " + ca1.getStep() + ": " 
								+ ca1.getClass().getSimpleName() + ":" + value1 
								+ " != " + ca2.getClass().getSimpleName() + ":" + value2);
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
				if (!equal) {
					return;
				}
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void printMinAndMaxValues(LongModel ca) {
		try {
			do {
				long[] minAndMax = ca.getMinAndMax();
				System.out.println("min: " + minAndMax[0] + "\t\tmax: " + minAndMax[1]);
			} while(ca.nextStep());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void checkTotalValueConservation(LongModel ca) {
		System.out.println("Checking total value conservation...");
		try {
			long value = ca.getTotal(), newValue = value;
			boolean finished = false;
			while (value == newValue && !finished) {
				finished = !ca.nextStep();
				newValue = ca.getTotal();
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
	
	public static <T extends FieldElement<T> & Comparable<T>> void checkTotalValueConservation(NumericModel<T> ca) {
		System.out.println("Checking total value conservation...");
		try {
			T value = ca.getTotal(), newValue = value;
			boolean finished = false;
			while (value.equals(newValue) && !finished) {
				finished = !ca.nextStep();
				newValue = ca.getTotal();
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
	
	public static void printTotalValueEvolution(LongModel ca) {
		try {
			do {
				System.out.println("step " + ca.getStep() + ": " + ca.getTotal());
			}
			while (ca.nextStep());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void checkTotalValueConservation(IntModel ca) {
		System.out.println("Checking total value conservation...");
		try {
			int value = ca.getTotal(), newValue = value;
			boolean finished = false;
			while (value == newValue && !finished) {
				finished = !ca.nextStep();
				newValue = ca.getTotal();
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
	
	public static void stepByStep(IntModel2D ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getStep());
				printAsGrid(ca, 0);
				System.out.println("total value " + ca.getTotal());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep(LongModel1D ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getStep());
				printAsGrid(ca, 0);
				System.out.println("total value " + ca.getTotal());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep(LongModel2D ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getStep());
				printAsGrid(ca, 0);
				System.out.println("total value " + ca.getTotal());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static <T extends FieldElement<T> & Comparable<T>> void stepByStep(NumericModel1D<T> ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getStep());
				printAsGrid(ca, null);
				System.out.println("total value " + ca.getTotal());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static <T extends Number & FieldElement<T> & Comparable<T>> void stepByStep(NumericModel2D<T> ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getStep());
				printAsGrid(ca, null);
				System.out.println("total value " + ca.getTotal());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static <T> void stepByStepZCrossSection(ActionableModel3D<ObjectGrid3D<T>> ca, T backgroundValue, int z) {
		try {
			Scanner s = new Scanner(System.in);
			Grid3DZCrossSectionCopierProcessor<T> copier = new Grid3DZCrossSectionCopierProcessor<T>();
			ca.addProcessor(copier);
			copier.requestCopy(z);
			ca.processGrid();
			do {
				System.out.println("step " + ca.getStep());
				ObjectGrid2D<T> crossSectionCopy = copier.getCopy(z);
				if (crossSectionCopy != null) {
					printAsGrid(crossSectionCopy, backgroundValue);
				}
				s.nextLine();
				copier.requestCopy(z);
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public static void stepByStepZCrossSectionOfLongGrid(ActionableModel3D<LongGrid3D> ca, int z) {
		try {
			Scanner s = new Scanner(System.in);
			LongGrid3DZCrossSectionCopierProcessor copier = new LongGrid3DZCrossSectionCopierProcessor();
			ca.addProcessor(copier);
			copier.requestCopy(z);
			ca.processGrid();
			do {
				System.out.println("step " + ca.getStep());
				LongGrid2D crossSectionCopy = copier.getCopy(z);
				if (crossSectionCopy != null) {
					printAsGrid(crossSectionCopy, 0);
					System.out.println("total value " + crossSectionCopy.getTotal());
				}
				s.nextLine();
				copier.requestCopy(z);
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public static void stepByStepZCrossSectionOfIntGrid(ActionableModel3D<IntGrid3D> ca, int z) {
		try {
			Scanner s = new Scanner(System.in);
			IntGrid3DZCrossSectionCopierProcessor copier = new IntGrid3DZCrossSectionCopierProcessor();
			ca.addProcessor(copier);
			copier.requestCopy(z);
			ca.processGrid();
			do {
				System.out.println("step " + ca.getStep());
				IntGrid2D crossSectionCopy = copier.getCopy(z);
				if (crossSectionCopy != null) {
					printAsGrid(crossSectionCopy, 0);
					System.out.println("total value " + crossSectionCopy.getTotal());
				}
				s.nextLine();
				copier.requestCopy(z);
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public static void stepByStepXCrossSection(ActionableModel3D<IntGrid3D> ca, int x) {
		try {
			Scanner s = new Scanner(System.in);
			IntGrid3DXCrossSectionCopierProcessor copier = new IntGrid3DXCrossSectionCopierProcessor();
			ca.addProcessor(copier);
			copier.requestCopy(x);
			ca.processGrid();
			do {
				System.out.println("step " + ca.getStep());
				IntGrid2D crossSectionCopy = copier.getCopy(x);
				if (crossSectionCopy != null) {
					printAsGrid(crossSectionCopy, 0);
					System.out.println("total value " + crossSectionCopy.getTotal());
				}
				s.nextLine();
				copier.requestCopy(x);
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void race(Model... cas) {
		try {
			long millis;
			for (Model ca : cas) {
				millis = System.currentTimeMillis();
				while (ca.nextStep());
				System.out.println(ca.getClass().getSimpleName() + ": " + (System.currentTimeMillis() - millis));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static <T> void printAsGrid(ObjectGrid2D<T> grid, T backgroundValue) throws Exception {
		
		int maxDigits = 3;
		int maxY = grid.getMaxY();
		int minY = grid.getMinY();
		int maxX = grid.getMaxX();
		int minX = grid.getMinX();
		for (int y = maxY; y >= minY; y--) {
			int localMaxX = grid.getMaxX(y);
			for (int x = grid.getMinX(y); x <= localMaxX; x++) {
				int digits = grid.getFromPosition(x, y).toString().length();
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
			int localMaxX = grid.getMaxX(y);
			int localMinX = grid.getMinX(y);
			int x = minX;
			for (; x < localMinX; x++) {
				System.out.print("|" + padLeft(" ", ' ', maxDigits));
			}
			for (; x <= localMaxX; x++) {
				String strVal = " ";
				T val = grid.getFromPosition(x, y);
				if (!val.equals(backgroundValue)) {
					strVal = val.toString();
				}
				System.out.print("|" + padLeft(strVal, ' ', maxDigits));
			}
			for (; x <= maxX; x++) {
				System.out.print("|" + padLeft(" ", ' ', maxDigits));
			}
			System.out.println("|");
		}
		System.out.println(headFoot);
	}
	
	public static <T> void printAsGrid(ObjectGrid1D<T> grid, T backgroundValue) {
		int maxDigits = 3;
		int maxX = grid.getMaxX();
		int minX = grid.getMinX();
		for (int x = minX; x <= maxX; x++) {
			int digits = grid.getFromPosition(x).toString().length();
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
			T val = grid.getFromPosition(x);
			if (!val.equals(backgroundValue)) {
				strVal = val + "";
			}
			System.out.print("|" + padLeft(strVal, ' ', maxDigits));
		}
		System.out.println("|");
		System.out.println(headFoot);
	}
	
	public static void printAsGrid(LongGrid2D grid, long backgroundValue) throws Exception {
		int maxDigits = 3;
		int maxY = grid.getMaxY();
		int minY = grid.getMinY();
		int maxX = grid.getMaxX();
		int minX = grid.getMinX();
		for (int y = maxY; y >= minY; y--) {
			int localMaxX = grid.getMaxX(y);
			for (int x = grid.getMinX(y); x <= localMaxX; x++) {
				int digits = Long.toString(grid.getFromPosition(x, y)).length();
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
			int localMaxX = grid.getMaxX(y);
			int localMinX = grid.getMinX(y);
			int x = minX;
			for (; x < localMinX; x++) {
				System.out.print("|" + padLeft(" ", ' ', maxDigits));
			}
			for (; x <= localMaxX; x++) {
				String strVal = " ";
				long val = grid.getFromPosition(x, y);
				if (val != backgroundValue) {
					strVal = val + "";
				}
				System.out.print("|" + padLeft(strVal, ' ', maxDigits));
			}
			for (; x <= maxX; x++) {
				System.out.print("|" + padLeft(" ", ' ', maxDigits));
			}
			System.out.println("|");
		}
		System.out.println(headFoot);
	}
	
	public static void printAsGrid(LongGrid1D grid, long backgroundValue) {
		int maxDigits = 3;
		int maxX = grid.getMaxX();
		int minX = grid.getMinX();
		for (int x = minX; x <= maxX; x++) {
			int digits = Long.toString(grid.getFromPosition(x)).length();
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
			long val = grid.getFromPosition(x);
			if (val != backgroundValue) {
				strVal = val + "";
			}
			System.out.print("|" + padLeft(strVal, ' ', maxDigits));
		}
		System.out.println("|");
		System.out.println(headFoot);
	}
	
	public static void printAsGrid(IntGrid2D grid, int backgroundValue) throws Exception {
		int maxDigits = 3;
		int maxY = grid.getMaxY();
		int minY = grid.getMinY();
		int maxX = grid.getMaxX();
		int minX = grid.getMinX();
		for (int y = maxY; y >= minY; y--) {
			int localMaxX = grid.getMaxX(y);
			for (int x = grid.getMinX(y); x <= localMaxX; x++) {
				int digits = Long.toString(grid.getFromPosition(x, y)).length();
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
			int localMaxX = grid.getMaxX(y);
			int localMinX = grid.getMinX(y);
			int x = minX;
			for (; x < localMinX; x++) {
				System.out.print("|" + padLeft(" ", ' ', maxDigits));
			}
			for (; x <= localMaxX; x++) {
				String strVal = " ";
				long val = grid.getFromPosition(x, y);
				if (val != backgroundValue) {
					strVal = val + "";
				}
				System.out.print("|" + padLeft(strVal, ' ', maxDigits));
			}
			for (; x <= maxX; x++) {
				System.out.print("|" + padLeft(" ", ' ', maxDigits));
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
