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
import java.util.function.Consumer;

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
import cellularautomata.model.Coordinates;
import cellularautomata.model.ModelProcessor;
import cellularautomata.model.Model;
import cellularautomata.model.IntModel;
import cellularautomata.model.LongModel;
import cellularautomata.model.NumericModel;
import cellularautomata.model1d.LongModel1D;
import cellularautomata.model1d.NumericModel1D;
import cellularautomata.model1d.ObjectModel1D;
import cellularautomata.model1d.SequentialLongModel1D;
import cellularautomata.model2d.ArrayIntGrid2D;
import cellularautomata.model2d.ArrayNumberGrid2D;
import cellularautomata.model2d.Model2D;
import cellularautomata.model2d.IntModel2D;
import cellularautomata.model2d.LongModel2D;
import cellularautomata.model2d.NumericModel2D;
import cellularautomata.model2d.ObjectModel2D;
import cellularautomata.model3d.ActionableModel3D;
import cellularautomata.model3d.BigFractionModel3DPattern;
import cellularautomata.model3d.BigIntModel3DPattern;
import cellularautomata.model3d.Model3D;
import cellularautomata.model3d.Model3DZCrossSectionCopierProcessor;
import cellularautomata.model3d.IntModel3D;
import cellularautomata.model3d.IntModel3DXCrossSectionCopierProcessor;
import cellularautomata.model3d.IntModel3DZCrossSectionCopierProcessor;
import cellularautomata.model3d.LongModel3D;
import cellularautomata.model3d.LongModel3DZCrossSectionCopierProcessor;
import cellularautomata.model3d.NumericModel3D;
import cellularautomata.model3d.ObjectModel3D;
import cellularautomata.model3d.RegularIntGrid3D;
import cellularautomata.model4d.ActionableModel4D;
import cellularautomata.model4d.IntModel4D;
import cellularautomata.model4d.LongModel4D;
import cellularautomata.model4d.NumericModel4D;
import cellularautomata.model4d.RegularIntGrid4D;
import cellularautomata.model5d.ActionableModel5D;
import cellularautomata.model5d.IntModel5D;
import cellularautomata.model5d.LongModel5D;
import cellularautomata.model5d.RegularIntGrid5D;
import cellularautomata.numbers.BigInt;

public class Test {
	
	public static void main(String[] args) throws Exception {
		long initialValue = -3000;
		Aether2D ae1 = new Aether2D(initialValue);
		AetherSimple2D ae2 = new AetherSimple2D(initialValue);
		compareAllSteps(ae1, ae2);
	}
	
	public static void checkBoundsConsistency(Model2D grid) {
		List<String> edgePositions = new ArrayList<String>();
		//xy
		int x = grid.getMinX();
		int maxX = grid.getMaxX();
		int localMaxY = grid.getMaxY(x);
		for (int y = grid.getMinY(x); y <= localMaxY; y++) {
			edgePositions.add(x + "," + y);
		}
		for (x++; x < maxX; x++) {
			edgePositions.add(x + "," + grid.getMinY(x));
			edgePositions.add(x + "," + grid.getMaxY(x));
		}
		if (x == maxX) {
			localMaxY = grid.getMaxY(x);
			for (int y = grid.getMinY(x); y <= localMaxY; y++) {
				edgePositions.add(x + "," + y);
			}
		}
		//yx
		String errorMessage = "Inconsistency found in bounds in yx traversal";
		int y = grid.getMinY();
		int maxY = grid.getMaxY();
		int localMaxX = grid.getMaxX(y);
		for (x = grid.getMinX(y); x <= localMaxX; x++) {
			if (!edgePositions.contains(x + "," + y)) {
				System.out.println(errorMessage);
			}
		}
		for (y++; y < maxY; y++) {
			if (!edgePositions.contains(grid.getMinX(y) + "," + y) 
					|| !edgePositions.contains(grid.getMaxX(y) + "," + y)) {
				System.out.println(errorMessage);
			}
		}
		if (y == maxY) {
			localMaxX = grid.getMaxX(y);
			for (x = grid.getMinX(y); x <= localMaxX; x++) {
				if (!edgePositions.contains(x + "," + y)) {
					System.out.println(errorMessage);
				}
			}
		}
	}
	
	public static void checkBoundsConsistency(Model3D grid) {
		List<String> edgePositions = new ArrayList<String>();
		//xyz
		int x = grid.getMinX();
		int maxX = grid.getMaxX();
		int localMaxY = grid.getMaxYAtX(x);
		for (int y = grid.getMinYAtX(x); y <= localMaxY; y++) {
			int localMaxZ = grid.getMaxZ(x, y);
			for (int z = grid.getMinZ(x, y); z <= localMaxZ; z++) {
				edgePositions.add(x + "," + y + "," + z);
			}
		}
		for (x++; x < maxX; x++) {
			int y = grid.getMinYAtX(x);
			int maxY = grid.getMaxYAtX(x);
			int localMaxZ = grid.getMaxZ(x, y);
			for (int z = grid.getMinZ(x, y); z <= localMaxZ; z++) {
				edgePositions.add(x + "," + y + "," + z);
			}
			for (y++; y < maxY; y++) {
				edgePositions.add(x + "," + y + "," + grid.getMinZ(x, y));
				edgePositions.add(x + "," + y + "," + grid.getMaxZ(x, y));
			}
			if (y == maxY) {
				localMaxZ = grid.getMaxZ(x, y);
				for (int z = grid.getMinZ(x, y); z <= localMaxZ; z++) {
					edgePositions.add(x + "," + y + "," + z);
				}
			}
		}
		if (x == maxX) {
			localMaxY = grid.getMaxYAtX(x);
			for (int y = grid.getMinYAtX(x); y <= localMaxY; y++) {
				int localMaxZ = grid.getMaxZ(x, y);
				for (int z = grid.getMinZ(x, y); z <= localMaxZ; z++) {
					edgePositions.add(x + "," + y + "," + z);
				}
			}
		}
		//xzy
		String errorMessage = "Inconsistency found in bounds in xzy traversal";
		x = grid.getMinX();
		maxX = grid.getMaxX();
		int localMaxZ = grid.getMaxZAtX(x);
		for (int z = grid.getMinYAtX(x); z <= localMaxZ; z++) {
			localMaxY = grid.getMaxY(x, z);
			for (int y = grid.getMinY(x, z); y <= localMaxY; y++) {
				if (!edgePositions.contains(x + "," + y + "," + z)) {
					System.out.println(errorMessage);
				}
			}
		}
		for (x++; x < maxX; x++) {
			int z = grid.getMinZAtX(x);
			int maxZ = grid.getMaxZAtX(x);
			localMaxY = grid.getMaxY(x, z);
			for (int y = grid.getMinY(x, z); y <= localMaxY; y++) {
				if (!edgePositions.contains(x + "," + y + "," + z)) {
					System.out.println(errorMessage);
				}
			}
			for (z++; z < maxZ; z++) {
				if (!edgePositions.contains(x + "," + grid.getMinY(x, z) + "," + z) 
						|| !edgePositions.contains(x + "," + grid.getMaxY(x, z) + "," + z)) {
					System.out.println(errorMessage);
				}
			}
			if (z == maxZ) {
				localMaxY = grid.getMaxY(x, z);
				for (int y = grid.getMinY(x, z); y <= localMaxY; y++) {
					if (!edgePositions.contains(x + "," + y + "," + z)) {
						System.out.println(errorMessage);
					}
				}
			}
		}
		if (x == maxX) {
			localMaxZ = grid.getMaxZAtX(x);
			for (int z = grid.getMinZAtX(x); z <= localMaxZ; z++) {
				localMaxY = grid.getMaxY(x, z);
				for (int y = grid.getMinY(x, z); y <= localMaxY; y++) {
					if (!edgePositions.contains(x + "," + y + "," + z)) {
						System.out.println(errorMessage);
					}
				}
			}
		}
		//yxz
		errorMessage = "Inconsistency found in bounds in yxz traversal";
		int y = grid.getMinY();
		int maxY = grid.getMaxY();
		int localMaxX = grid.getMaxXAtY(y);
		for (x = grid.getMinXAtY(y); x <= localMaxX; x++) {
			localMaxZ = grid.getMaxZ(x, y);
			for (int z = grid.getMinZ(x, y); z <= localMaxZ; z++) {
				if (!edgePositions.contains(x + "," + y + "," + z)) {
					System.out.println(errorMessage);
				}
			}
		}
		for (y++; y < maxY; y++) {
			x = grid.getMinXAtY(y);
			maxX = grid.getMaxXAtY(y);
			localMaxZ = grid.getMaxZ(x, y);
			for (int z = grid.getMinZ(x, y); z <= localMaxZ; z++) {
				if (!edgePositions.contains(x + "," + y + "," + z)) {
					System.out.println(errorMessage);
				}
			}
			for (x++; x < maxX; x++) {
				if (!edgePositions.contains(x + "," + y + "," + grid.getMinZ(x, y)) 
						|| !edgePositions.contains(x + "," + y + "," + grid.getMaxZ(x, y))) {
					System.out.println(errorMessage);
				}
			}
			if (x == maxX) {
				localMaxZ = grid.getMaxZ(x, y);
				for (int z = grid.getMinZ(x, y); z <= localMaxZ; z++) {
					if (!edgePositions.contains(x + "," + y + "," + z)) {
						System.out.println(errorMessage);
					}
				}
			}
		}
		if (y == maxY) {
			localMaxX = grid.getMaxXAtY(y);
			for (x = grid.getMinXAtY(y); x <= localMaxX; x++) {
				localMaxZ = grid.getMaxZ(x, y);
				for (int z = grid.getMinZ(x, y); z <= localMaxZ; z++) {
					if (!edgePositions.contains(x + "," + y + "," + z)) {
						System.out.println(errorMessage);
					}
				}
			}
		}
		//yzx
		errorMessage = "Inconsistency found in bounds in yzx traversal";
		y = grid.getMinY();
		maxY = grid.getMaxY();
		localMaxZ = grid.getMaxZAtY(y);
		for (int z = grid.getMinZAtY(y); z <= localMaxZ; z++) {
			localMaxX = grid.getMaxX(y, z);
			for (x = grid.getMinX(y, z); x <= localMaxX; x++) {
				if (!edgePositions.contains(x + "," + y + "," + z)) {
					System.out.println(errorMessage);
				}
			}
		}
		for (y++; y < maxY; y++) {
			int z = grid.getMinZAtY(y);
			int maxZ = grid.getMaxZAtY(y);
			localMaxX = grid.getMaxX(y, z);
			for (x = grid.getMinX(y, z); x <= localMaxX; x++) {
				if (!edgePositions.contains(x + "," + y + "," + z)) {
					System.out.println(errorMessage);
				}
			}
			for (z++; z < maxZ; z++) {
				if (!edgePositions.contains(grid.getMinX(y, z) + "," + y + "," + z) 
						|| !edgePositions.contains(grid.getMaxX(y, z) + "," + y + "," + z)) {
					System.out.println(errorMessage);
				}
			}
			if (z == maxZ) {
				localMaxX = grid.getMaxX(y, z);
				for (x = grid.getMinX(y, z); x <= localMaxX; x++) {
					if (!edgePositions.contains(x + "," + y + "," + z)) {
						System.out.println(errorMessage);
					}
				}
			}
		}
		if (y == maxY) {
			localMaxZ = grid.getMaxZAtY(y);
			for (int z = grid.getMinZAtY(y); z <= localMaxZ; z++) {
				localMaxX = grid.getMaxX(y, z);
				for (x = grid.getMinX(y, z); x <= localMaxX; x++) {
					if (!edgePositions.contains(x + "," + y + "," + z)) {
						System.out.println(errorMessage);
					}
				}
			}
		}
		//zxy
		errorMessage = "Inconsistency found in bounds in zxy traversal";
		int z = grid.getMinZ();
		int maxZ = grid.getMaxZ();
		localMaxX = grid.getMaxXAtZ(z);
		for (x = grid.getMinXAtZ(z); x <= localMaxX; x++) {
			localMaxY = grid.getMaxY(x, z);
			for (y = grid.getMinY(x, z); y <= localMaxY; y++) {
				if (!edgePositions.contains(x + "," + y + "," + z)) {
					System.out.println(errorMessage);
				}
			}
		}
		for (z++; z < maxZ; z++) {
			x = grid.getMinXAtZ(z);
			maxX = grid.getMaxXAtZ(z);
			localMaxY = grid.getMaxY(x, z);
			for (y = grid.getMinY(x, z); y <= localMaxY; y++) {
				if (!edgePositions.contains(x + "," + y + "," + z)) {
					System.out.println(errorMessage);
				}
			}
			for (x++; x < maxX; x++) {
				if (!edgePositions.contains(x + "," + grid.getMinY(x, z) + "," + z) 
						|| !edgePositions.contains(x + "," + grid.getMaxY(x, z) + "," + z)) {
					System.out.println(errorMessage);
				}
			}
			if (x == maxX) {
				localMaxY = grid.getMaxY(x, z);
				for (y = grid.getMinY(x, z); y <= localMaxY; y++) {
					if (!edgePositions.contains(x + "," + y + "," + z)) {
						System.out.println(errorMessage);
					}
				}
			}
		}
		if (z == maxZ) {
			localMaxX = grid.getMaxXAtZ(z);
			for (x = grid.getMinXAtZ(z); x <= localMaxX; x++) {
				localMaxY = grid.getMaxY(x, z);
				for (y = grid.getMinY(x, z); y <= localMaxY; y++) {
					if (!edgePositions.contains(x + "," + y + "," + z)) {
						System.out.println(errorMessage);
					}
				}
			}
		}
		//zyx
		errorMessage = "Inconsistency found in bounds in zyx traversal";
		z = grid.getMinZ();
		maxZ = grid.getMaxZ();
		localMaxY = grid.getMaxYAtZ(z);
		for (y = grid.getMinYAtZ(z); y <= localMaxY; y++) {
			localMaxX = grid.getMaxX(y, z);
			for (x = grid.getMinX(y, z); x <= localMaxX; x++) {
				if (!edgePositions.contains(x + "," + y + "," + z)) {
					System.out.println(errorMessage);
				}
			}
		}
		for (z++; z < maxZ; z++) {
			y = grid.getMinYAtZ(z);
			maxY = grid.getMaxYAtZ(z);
			localMaxX = grid.getMaxX(y, z);
			for (x = grid.getMinX(y, z); x <= localMaxX; x++) {
				if (!edgePositions.contains(x + "," + y + "," + z)) {
					System.out.println(errorMessage);
				}
			}
			for (y++; y < maxY; y++) {
				if (!edgePositions.contains(grid.getMinX(y, z) + "," + y + "," + z) 
						|| !edgePositions.contains(grid.getMaxX(y, z) + "," + y + "," + z)) {
					System.out.println(errorMessage);
				}
			}
			if (y == maxY) {
				localMaxX = grid.getMaxX(y, z);
				for (x = grid.getMinX(y, z); x <= localMaxX; x++) {
					if (!edgePositions.contains(x + "," + y + "," + z)) {
						System.out.println(errorMessage);
					}
				}
			}
		}
		if (z == maxZ) {
			localMaxY = grid.getMaxYAtZ(z);
			for (y = grid.getMinYAtZ(z); y <= localMaxY; y++) {
				localMaxX = grid.getMaxX(y, z);
				for (x = grid.getMinX(y, z); x <= localMaxX; x++) {
					if (!edgePositions.contains(x + "," + y + "," + z)) {
						System.out.println(errorMessage);
					}
				}
			}
		}
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
		return arr;
	}
	
	public static void test2DDiagonals() throws Exception {
		int[][][] sourceValues;
		int[][] resultValues;
		RegularIntGrid3D grid;
		IntModel2D diagonal;
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
						sourceValues[x][y][z] = value;
						resultValues[z][x] = value;	
					}
				}
			}			
			grid = new RegularIntGrid3D(sourceValues, 0, 0, 0);
			diagonal = grid.diagonalCrossSectionOnXY(off);
			compare(diagonal, resultValues, 0, 0);
			checkBoundsConsistency(diagonal);
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
			checkBoundsConsistency(diagonal);
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
			checkBoundsConsistency(diagonal);
		}
	}
	
	public static void printRegionBounds(Model region) {
		int dimension = region.getGridDimension();
		for (int axis = 0; axis < dimension; axis++) {
			char axisLetter = Utils.getAxisLetterFromIndex(dimension, axis);
			System.out.printf("%d <= %c <= %d %n", region.getMinCoordinate(axis), axisLetter, region.getMaxCoordinate(axis));
		}
	}
	
	public static void test3DDiagonals() throws Exception {
		int[][][][] sourceValues;
		int[][][] resultValues;
		RegularIntGrid4D grid;
		IntModel3D diagonal;
		int side = 20;
		int offset = 5;
		ThreadLocalRandom random = ThreadLocalRandom.current();
		//wx 
		for (int off = 0; off <= offset; off += offset) {
			sourceValues = new int[side][side][side][side];
			resultValues = new int[side][side][side];
			for (int w = 0; w < side; w++) {
				for (int y = 0; y < side; y++) {
					for (int z = 0; z < side; z++) {
						int value = random.nextInt();
						int x = w+off;
						if (x < side) {
							sourceValues[w][x][y][z] = value;
							resultValues[w][y][z] = value;	
						}
					}
				}
			}			
			grid = new RegularIntGrid4D(sourceValues, 0, 0, 0, 0);
			diagonal = grid.diagonalCrossSectionOnWX(off);
			checkBoundsConsistency(diagonal);
			compare(diagonal, new RegularIntGrid3D(resultValues, 0, 0, 0));
		}
		//yz 
		for (int off = 0; off <= offset; off += offset) {
			sourceValues = new int[side][side][side][side];
			resultValues = new int[side][side][side];
			for (int w = 0; w < side; w++) {
				for (int y = 0; y < side; y++) {
					for (int x = 0; x < side; x++) {
						int value = random.nextInt();
						int z = y+off;
						if (z < side) {
							sourceValues[w][x][y][z] = value;
							resultValues[w][x][y] = value;	
						}
					}
				}
			}			
			grid = new RegularIntGrid4D(sourceValues, 0, 0, 0, 0);
			diagonal = grid.diagonalCrossSectionOnYZ(off);
			checkBoundsConsistency(diagonal);
			compare(diagonal, new RegularIntGrid3D(resultValues, 0, 0, 0));
		}
	}
	
	public static void compare(IntModel2D grid, int[][] array, int xOffset, int yOffset) throws Exception {
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
	
	public static void compare(IntModel3D grid1, IntModel3D grid2) {
		try {
			boolean equal = true;
			for (int z = grid1.getMinZ(); z <= grid1.getMaxZ(); z++) {
				for (int y = grid1.getMinYAtZ(z); y <= grid1.getMaxYAtZ(z); y++) {
					for (int x = grid1.getMinX(y,z); x <= grid1.getMaxX(y,z); x++) {
						if (grid1.getFromPosition(x, y, z) != grid2.getFromPosition(x, y, z)) {
							equal = false;
							System.out.println("Different value at (" + x + ", " + y + ", " + z + "): " 
									+ grid1.getClass().getSimpleName() + ":" + grid1.getFromPosition(x, y, z) 
									+ " != " + grid2.getClass().getSimpleName() + ":" + grid2.getFromPosition(x, y, z));
						}
					}	
				}	
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void test4DDiagonals() throws Exception {
		int[][][][][] sourceValues;
		int[][][][] resultValues;
		RegularIntGrid5D grid;
		IntModel4D diagonal;
		int side = 20;
		int offset = 5;
		ThreadLocalRandom random = ThreadLocalRandom.current();
		//vw
		for (int off = 0; off <= offset; off += offset) {
			sourceValues = new int[side][side][side][side][side];
			resultValues = new int[side][side][side][side];
			for (int v = 0; v < side; v++) {
				for (int x = 0; x < side; x++) {
					for (int y = 0; y < side; y++) {
						for (int z = 0; z < side; z++) {
							int value = random.nextInt();
							int w = v+off;
							if (w < side) {
								sourceValues[v][w][x][y][z] = value;
								resultValues[v][x][y][z] = value;	
							}
						}
					}
				}
			}			
			grid = new RegularIntGrid5D(sourceValues, 0, 0, 0, 0, 0);
			diagonal = grid.diagonalCrossSectionOnVW(off);
			compare(diagonal, new RegularIntGrid4D(resultValues, 0, 0, 0, 0));
		}
	}
	
	public static void compare(IntModel4D grid1, IntModel4D grid2) {
		try {
			boolean equal = true;
			for (int z = grid1.getMinZ(); z <= grid1.getMaxZ(); z++) {
				for (int y = grid1.getMinYAtZ(z); y <= grid1.getMaxYAtZ(z); y++) {
					for (int x = grid1.getMinXAtYZ(y,z); x <= grid1.getMaxXAtYZ(y,z); x++) {
						for (int w = grid1.getMinW(x,y,z); w <= grid1.getMaxW(x,y,z); w++) {
							if (grid1.getFromPosition(w, x, y, z) != grid2.getFromPosition(w, x, y, z)) {
								equal = false;
								System.out.println("Different value at (" + w + ", " + x + ", " + y + ", " + z + "): " 
										+ grid1.getClass().getSimpleName() + ":" + grid1.getFromPosition(w, x, y, z) 
										+ " != " + grid2.getClass().getSimpleName() + ":" + grid2.getFromPosition(w, x, y, z));
							}
						}
					}	
				}	
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	public static void timeIntAether3D(int singleSource) {
		IntAether3D ae1 = new IntAether3D(singleSource);
		long millis = System.currentTimeMillis();
		while(ae1.nextStep());
		System.out.println(System.currentTimeMillis() - millis);
	}
	
	public static void printVonNeumannNeighborhood(LongModel3D grid, int x, int y, int z) {
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
	
	public static void printVonNeumannNeighborhood(LongModel4D grid, int w, int x, int y, int z) {
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
	
	public static void printVonNeumannNeighborhood(LongModel5D grid, int v, int w, int x, int y, int z) {
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
		IntAether4D ae = new IntAether4D(-10000);
		IntModel4D ns = ae.asymmetricSection();
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
		IntModel3D cs = ae.asymmetricSection().crossSectionAtZ(z);
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
		compareAllSteps(ae1, ae2);
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
	
	public static void compareAllSteps(LongModel1D ca1, LongModel1D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				//System.out.println("step " + ca1.getStep());
				for (int x = ca2.getMinX(); x <= ca2.getMaxX(); x++) {
//					System.out.println(x);
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
	
	public static void compareAllSteps(IntModel2D ca1, LongModel2D ca2) {
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
	
	public static void compareAllSteps(IntModel2D ca1, IntModel2D ca2) {
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
	
	public static void compareAllSteps(LongModel2D ca1, LongModel2D ca2) {
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
	
	public static void compareAllSteps(IntModel3D ca1, IntModel3D ca2) {
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
	
	public static void compareAllStepsFromIntModel(LongModel3D ca1, ActionableModel3D<IntModel3D> ca2) {
		compareAllStepsFromIntModel(ca2 , ca1);
	}
	
	public static void compareAllStepsFromIntModel(ActionableModel3D<IntModel3D> ca1, LongModel3D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			ModelProcessor<IntModel3D> comparator = new ModelProcessor<IntModel3D>() {
				
				@Override
				public void processModelBlock(IntModel3D gridBlock) throws Exception {
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
				ca1.processModel();
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
	
	public static void compareAllStepsFromLongModel(ActionableModel3D<LongModel3D> ca1, LongModel3D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			ModelProcessor<LongModel3D> comparator = new ModelProcessor<LongModel3D>() {
				
				@Override
				public void processModelBlock(LongModel3D gridBlock) throws Exception {
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
				ca1.processModel();
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
	
	public static void compareAllStepsFromLongModel(LongModel4D ca1, ActionableModel4D<LongModel4D> ca2) {
		compareAllStepsFromLongModel(ca2, ca1);
	}
	
	public static void compareAllStepsFromLongModel(ActionableModel4D<LongModel4D> ca1, LongModel4D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			ModelProcessor<LongModel4D> comparator = new ModelProcessor<LongModel4D>() {
				
				@Override
				public void processModelBlock(LongModel4D gridBlock) throws Exception {
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
				ca1.processModel();
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
	
	public static void compareAllStepsFromIntModel(LongModel4D ca1, ActionableModel4D<IntModel4D> ca2) {
		compareAllStepsFromIntModel(ca2, ca1);
	}
	
	public static void compareAllStepsFromIntModel(ActionableModel4D<IntModel4D> ca1, LongModel4D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			ModelProcessor<IntModel4D> comparator = new ModelProcessor<IntModel4D>() {
				
				@Override
				public void processModelBlock(IntModel4D gridBlock) throws Exception {
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
				ca1.processModel();
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
	
	public static void compareAllStepsFromLongModel(LongModel5D ca1, ActionableModel5D<LongModel5D> ca2) {
		compareAllStepsFromLongModel(ca2, ca1);
	}
	
	public static void compareAllStepsFromLongModel(ActionableModel5D<LongModel5D> ca1, LongModel5D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			ModelProcessor<LongModel5D> comparator = new ModelProcessor<LongModel5D>() {
				
				@Override
				public void processModelBlock(LongModel5D gridBlock) throws Exception {
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
				ca1.processModel();
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
	
	public static void compareAllStepsFromIntModel(IntModel5D ca1, ActionableModel5D<IntModel5D> ca2) {
		compareAllStepsFromIntModel(ca2, ca1);
	}
	
	public static void compareAllStepsFromIntModel(ActionableModel5D<IntModel5D> ca1, IntModel5D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			ModelProcessor<IntModel5D> comparator = new ModelProcessor<IntModel5D>() {
				
				@Override
				public void processModelBlock(IntModel5D gridBlock) throws Exception {
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
				ca1.processModel();
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
	
	public static void compareAllStepsFromIntModel(LongModel5D ca1, ActionableModel5D<IntModel5D> ca2) {
		compareAllStepsFromIntModel(ca2, ca1);
	}
	
	public static void compareAllStepsFromIntModel(ActionableModel5D<IntModel5D> ca1, LongModel5D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			ModelProcessor<IntModel5D> comparator = new ModelProcessor<IntModel5D>() {
				
				@Override
				public void processModelBlock(IntModel5D gridBlock) throws Exception {
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
				ca1.processModel();
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
		compareAllStepsFromNumericModel(ActionableModel3D<NumericModel3D<T>> ca1, NumericModel3D<T> ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			ModelProcessor<NumericModel3D<T>> comparator = new ModelProcessor<NumericModel3D<T>>() {
				
				@Override
				public void processModelBlock(NumericModel3D<T> gridBlock) throws Exception {
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
				ca1.processModel();
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
		compareAllStepsFromNumericModel(ActionableModel4D<NumericModel4D<T>> ca1, NumericModel4D<T> ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			ModelProcessor<NumericModel4D<T>> comparator = new ModelProcessor<NumericModel4D<T>>() {
				
				@Override
				public void processModelBlock(NumericModel4D<T> gridBlock) throws Exception {
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
				ca1.processModel();
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
	
	public static void compareAllSteps(LongModel4D ca1, LongModel4D ca2) {
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
	
	public static void compareAllSteps(LongModel4D ca1, IntModel4D ca2) {
		compareAllSteps(ca2, ca1);
	}
	
	public static void compareAllSteps(IntModel4D ca1, LongModel4D ca2) {
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
	
	public static void compareAllSteps(IntModel4D ca1, IntModel4D ca2) {
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
	
	public static void compareAllSteps(LongModel3D ca1, IntModel3D ca2) {
		compareAllSteps(ca2, ca1);
	}
	
	public static void compareAllSteps(IntModel3D ca1, LongModel3D ca2) {
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
	
	public static void compareAllSteps(LongModel3D ca1, LongModel3D ca2) {
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
	
	public static <T extends Number & FieldElement<T> & Comparable<T>> void compareAllSteps(NumericModel3D<T> ca1, LongModel3D ca2) {
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
	
	public static <T extends Number & FieldElement<T> & Comparable<T>> void compareAllSteps(NumericModel4D<T> ca1, LongModel4D ca2) {
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
				BigIntModel3DPattern<NumericModel3D<BigInt>> p1 = new BigIntModel3DPattern<NumericModel3D<BigInt>>(ca1);
				BigIntModel3DPattern<NumericModel3D<BigInt>> p2 = new BigIntModel3DPattern<NumericModel3D<BigInt>>(ca2);
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
				BigFractionModel3DPattern p1 = new BigFractionModel3DPattern(ca1);
				BigIntModel3DPattern<NumericModel3D<BigInt>> p2 = new BigIntModel3DPattern<NumericModel3D<BigInt>>(ca2);
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
		BigFractionModel3DPattern p1 = new BigFractionModel3DPattern(ca1);
		BigIntModel3DPattern<NumericModel3D<BigInt>> p2 = new BigIntModel3DPattern<NumericModel3D<BigInt>>(ca2);
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
	
	public static <T extends Number & FieldElement<T> & Comparable<T>> void compareAllSteps(NumericModel1D<T> ca1, LongModel1D ca2) {
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
	
	public static <T extends Number & FieldElement<T> & Comparable<T>> void compareAllSteps(NumericModel3D<T> ca1, IntModel3D ca2) {
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
	
	public static <T extends FieldElement<T> & Comparable<T>> void compareAllSteps(NumericModel3D<T> ca1, NumericModel3D<T> ca2) {
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
	
	public static <T extends FieldElement<T> & Comparable<T>> void compareAllSteps(NumericModel4D<T> ca1, NumericModel4D<T> ca2) {
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
	
	public static void compareAllSteps(LongModel5D ca1, LongModel5D ca2) {
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
	
	public static void compareAllSteps(IntModel5D ca1, LongModel5D ca2) {
		compareAllSteps(ca2, ca1);
	}
	
	public static void compareAllSteps(LongModel5D ca1, IntModel5D ca2) {
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
	
	public static void compareAllSteps(SequentialLongModel1D ca1, LongModel1D ca2) {
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
	
	public static <T> void stepByStepZCrossSection(ActionableModel3D<ObjectModel3D<T>> ca, T backgroundValue, int z) {
		try {
			Scanner s = new Scanner(System.in);
			Model3DZCrossSectionCopierProcessor<T> copier = new Model3DZCrossSectionCopierProcessor<T>();
			ca.addProcessor(copier);
			copier.requestCopy(z);
			ca.processModel();
			do {
				System.out.println("step " + ca.getStep());
				ObjectModel2D<T> crossSectionCopy = copier.getCopy(z);
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
	
	public static void stepByStepZCrossSectionOfLongGrid(ActionableModel3D<LongModel3D> ca, int z) {
		try {
			Scanner s = new Scanner(System.in);
			LongModel3DZCrossSectionCopierProcessor copier = new LongModel3DZCrossSectionCopierProcessor();
			ca.addProcessor(copier);
			copier.requestCopy(z);
			ca.processModel();
			do {
				System.out.println("step " + ca.getStep());
				LongModel2D crossSectionCopy = copier.getCopy(z);
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
	
	public static void stepByStepZCrossSectionOfIntGrid(ActionableModel3D<IntModel3D> ca, int z) {
		try {
			Scanner s = new Scanner(System.in);
			IntModel3DZCrossSectionCopierProcessor copier = new IntModel3DZCrossSectionCopierProcessor();
			ca.addProcessor(copier);
			copier.requestCopy(z);
			ca.processModel();
			do {
				System.out.println("step " + ca.getStep());
				IntModel2D crossSectionCopy = copier.getCopy(z);
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
	
	public static void stepByStepXCrossSection(ActionableModel3D<IntModel3D> ca, int x) {
		try {
			Scanner s = new Scanner(System.in);
			IntModel3DXCrossSectionCopierProcessor copier = new IntModel3DXCrossSectionCopierProcessor();
			ca.addProcessor(copier);
			copier.requestCopy(x);
			ca.processModel();
			do {
				System.out.println("step " + ca.getStep());
				IntModel2D crossSectionCopy = copier.getCopy(x);
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
	
	public static <T> void printAsGrid(ObjectModel2D<T> grid, T backgroundValue) throws Exception {
		
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
	
	public static <T> void printAsGrid(ObjectModel1D<T> grid, T backgroundValue) throws Exception {
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
	
	public static void printAsGrid(LongModel2D grid, long backgroundValue) throws Exception {
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
	
	public static void printAsGrid(LongModel1D grid, long backgroundValue) throws Exception {
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
	
	public static void printAsGrid(IntModel2D grid, int backgroundValue) throws Exception {
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
	
	public static void compareAllSteps(IntModel ca1, IntModel ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
//			boolean equal = true;
			while (!finished1 && !finished2) {
				System.out.println("Comparing step " + ca1.getStep());//debug
				ca1.forEachPosition(new Consumer<Coordinates>() {
					
					@Override
					public void accept(Coordinates coordinates) {
						try {
							if (ca1.getFromPosition(coordinates) != ca2.getFromPosition(coordinates)) {
//							equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " " + coordinates + ": " 
										+ ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(coordinates) 
										+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(coordinates));
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
//					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
//			if (equal)
//				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compareAllSteps(LongModel ca1, IntModel ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
//			boolean equal = true;
			while (!finished1 && !finished2) {
				System.out.println("Comparing step " + ca1.getStep());//debug
				ca1.forEachPosition(new Consumer<Coordinates>() {
					
					@Override
					public void accept(Coordinates coordinates) {
						try {
							if (ca1.getFromPosition(coordinates) != ca2.getFromPosition(coordinates)) {
//							equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " " + coordinates + ": " 
										+ ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(coordinates) 
										+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(coordinates));
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
//					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
//			if (equal)
//				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep(IntModel ca) {
		try {
			Scanner s = new Scanner(System.in);
			Coordinates crossSectionCoordinates = new Coordinates(new int[ca.getGridDimension()]);
			do {
				System.out.println("step " + ca.getStep());
				System.out.println(ca.toString2DCrossSection(0, 1, crossSectionCoordinates));
				System.out.println("total value " + ca.getTotal());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
