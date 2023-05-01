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
package cellularautomata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.math3.FieldElement;

import cellularautomata.automata.aether.Aether2D;
import cellularautomata.automata.aether.Aether4D;
import cellularautomata.automata.aether.SimpleAether2D;
import cellularautomata.automata.aether.SimpleAether5D;
import cellularautomata.automata.aether.IntAether4D;
import cellularautomata.automata.siv.SpreadIntegerValue2D;
import cellularautomata.automata.siv.SimpleSpreadIntegerValue2D;
import cellularautomata.model.Model;
import cellularautomata.model.IntModel;
import cellularautomata.model.LongModel;
import cellularautomata.model.NumericModel;
import cellularautomata.model.ObjectModel;
import cellularautomata.model.HypercubicPyramidGrid;
import cellularautomata.model1d.IntModel1D;
import cellularautomata.model1d.IntModelAs1D;
import cellularautomata.model1d.LongModel1D;
import cellularautomata.model1d.LongModelAs1D;
import cellularautomata.model1d.NumericModel1D;
import cellularautomata.model1d.NumericModelAs1D;
import cellularautomata.model1d.ObjectModelAs1D;
import cellularautomata.model2d.ArrayIntGrid2D;
import cellularautomata.model2d.ArrayLongGrid2D;
import cellularautomata.model2d.ArrayNumberGrid2D;
import cellularautomata.model2d.Model2D;
import cellularautomata.model2d.IntModel2D;
import cellularautomata.model2d.IntModelAs2D;
import cellularautomata.model2d.LongModel2D;
import cellularautomata.model2d.LongModelAs2D;
import cellularautomata.model2d.NumericModel2D;
import cellularautomata.model2d.NumericModelAs2D;
import cellularautomata.model2d.ObjectModelAs2D;
import cellularautomata.model3d.Model3D;
import cellularautomata.model3d.ModelAs3D;
import cellularautomata.model3d.IntModel3D;
import cellularautomata.model3d.LongModel3D;
import cellularautomata.model3d.RegularIntGrid3D;
import cellularautomata.model4d.IntModel4D;
import cellularautomata.model4d.LongModel4D;
import cellularautomata.model4d.Model4D;
import cellularautomata.model4d.ModelAs4D;
import cellularautomata.model4d.RegularIntGrid4D;
import cellularautomata.model5d.LongModel5D;
import cellularautomata.model5d.Model5D;
import cellularautomata.model5d.ModelAs5D;
import cellularautomata.model5d.RegularIntGrid5D;
import cellularautomata.numbers.BigInt;

public class Test {
	
	public static void main(String[] args) throws Exception {
		long initialValue = -3000;
		Aether2D ae1 = new Aether2D(initialValue);
		SimpleAether2D ae2 = new SimpleAether2D(initialValue);
		compareAllSteps(ae1, ae2);
	}
	
	public static void test2DModelMinAndMaxValues() throws Exception {
		int side = 20;
		ThreadLocalRandom random = ThreadLocalRandom.current();
		String[] names = new String[] { "Global", "Even", "Odd", "Even X", "Odd X", "Even Y", "Odd Y" };
		//int 
		System.out.println("Testing IntModel2D");
		int[][] intValues = new int[side][side];
		ArrayIntGrid2D intGrid = new ArrayIntGrid2D(0, new int[side], intValues);
		int[][] intMinAndMaxToCompare = new int[7][2];
		for (int i = 0; i < intMinAndMaxToCompare.length; i++) {
			intMinAndMaxToCompare[i][0] = Integer.MAX_VALUE;
			intMinAndMaxToCompare[i][1] = Integer.MIN_VALUE;
		}
		for (int x = 0; x < side; x++) {
			for (int y = 0; y < side; y++) {
				boolean isEvenPosition = (x+y)%2 == 0;
				boolean isEvenX = x%2 == 0;
				boolean isEvenY = y%2 == 0;
				int value = random.nextInt();
				intValues[x][y] = value;
				int i = 0; //all positions
				if (value < intMinAndMaxToCompare[i][0])
					intMinAndMaxToCompare[i][0] = value;
				if (value > intMinAndMaxToCompare[i][1])
					intMinAndMaxToCompare[i][1] = value;
				if (isEvenPosition) {
					i = 1;//even
					if (value < intMinAndMaxToCompare[i][0])
						intMinAndMaxToCompare[i][0] = value;
					if (value > intMinAndMaxToCompare[i][1])
						intMinAndMaxToCompare[i][1] = value;
				} else {
					i = 2;//odd
					if (value < intMinAndMaxToCompare[i][0])
						intMinAndMaxToCompare[i][0] = value;
					if (value > intMinAndMaxToCompare[i][1])
						intMinAndMaxToCompare[i][1] = value;
				}
				if (isEvenX) {
					i = 3;//even x
					if (value < intMinAndMaxToCompare[i][0])
						intMinAndMaxToCompare[i][0] = value;
					if (value > intMinAndMaxToCompare[i][1])
						intMinAndMaxToCompare[i][1] = value;
				} else {
					i = 4;//odd x
					if (value < intMinAndMaxToCompare[i][0])
						intMinAndMaxToCompare[i][0] = value;
					if (value > intMinAndMaxToCompare[i][1])
						intMinAndMaxToCompare[i][1] = value;
				}
				if (isEvenY) {
					i = 5;//even y
					if (value < intMinAndMaxToCompare[i][0])
						intMinAndMaxToCompare[i][0] = value;
					if (value > intMinAndMaxToCompare[i][1])
						intMinAndMaxToCompare[i][1] = value;
				} else {
					i = 6;//odd y
					if (value < intMinAndMaxToCompare[i][0])
						intMinAndMaxToCompare[i][0] = value;
					if (value > intMinAndMaxToCompare[i][1])
						intMinAndMaxToCompare[i][1] = value;
				}
			}
		}
		int[][] intMinAndMax = new int[7][2];
		intMinAndMax[0] = intGrid.getMinAndMax();
		intMinAndMax[1] = intGrid.getEvenOddPositionsMinAndMax(true);
		intMinAndMax[2] = intGrid.getEvenOddPositionsMinAndMax(false);
		intMinAndMax[3] = intGrid.getMinAndMaxAtEvenOddX(true);
		intMinAndMax[4] = intGrid.getMinAndMaxAtEvenOddX(false);
		intMinAndMax[5] = intGrid.getMinAndMaxAtEvenOddY(true);
		intMinAndMax[6] = intGrid.getMinAndMaxAtEvenOddY(false);
		for (int i = 0; i < intMinAndMaxToCompare.length; i++) {
			if (intMinAndMax[i][0] != intMinAndMaxToCompare[i][0] || intMinAndMax[i][1] != intMinAndMaxToCompare[i][1]) {
				System.err.println(names[i] + " min and max don't match [" + intMinAndMax[i][0] + ", " + intMinAndMax[i][1] + "] != [" + intMinAndMaxToCompare[i][0] + ", " + intMinAndMaxToCompare[i][1] + "]");
				return;
			}
		}
		
		//long
		System.out.println("Testing LongModel2D");
		long[][] longValues = new long[side][side];
		ArrayLongGrid2D longGrid = new ArrayLongGrid2D(0, new int[side], longValues);
		long[][] longMinAndMaxToCompare = new long[7][2];
		for (int i = 0; i < longMinAndMaxToCompare.length; i++) {
			longMinAndMaxToCompare[i][0] = Long.MAX_VALUE;
			longMinAndMaxToCompare[i][1] = Long.MIN_VALUE;
		}
		for (int x = 0; x < side; x++) {
			for (int y = 0; y < side; y++) {
				boolean isEvenPosition = (x+y)%2 == 0;
				boolean isEvenX = x%2 == 0;
				boolean isEvenY = y%2 == 0;
				long value = random.nextLong();
				longValues[x][y] = value;
				int i = 0; //all positions
				if (value < longMinAndMaxToCompare[i][0])
					longMinAndMaxToCompare[i][0] = value;
				if (value > longMinAndMaxToCompare[i][1])
					longMinAndMaxToCompare[i][1] = value;
				if (isEvenPosition) {
					i = 1;//even
					if (value < longMinAndMaxToCompare[i][0])
						longMinAndMaxToCompare[i][0] = value;
					if (value > longMinAndMaxToCompare[i][1])
						longMinAndMaxToCompare[i][1] = value;
				} else {
					i = 2;//odd
					if (value < longMinAndMaxToCompare[i][0])
						longMinAndMaxToCompare[i][0] = value;
					if (value > longMinAndMaxToCompare[i][1])
						longMinAndMaxToCompare[i][1] = value;
				}
				if (isEvenX) {
					i = 3;//even x
					if (value < longMinAndMaxToCompare[i][0])
						longMinAndMaxToCompare[i][0] = value;
					if (value > longMinAndMaxToCompare[i][1])
						longMinAndMaxToCompare[i][1] = value;
				} else {
					i = 4;//odd x
					if (value < longMinAndMaxToCompare[i][0])
						longMinAndMaxToCompare[i][0] = value;
					if (value > longMinAndMaxToCompare[i][1])
						longMinAndMaxToCompare[i][1] = value;
				}
				if (isEvenY) {
					i = 5;//even y
					if (value < longMinAndMaxToCompare[i][0])
						longMinAndMaxToCompare[i][0] = value;
					if (value > longMinAndMaxToCompare[i][1])
						longMinAndMaxToCompare[i][1] = value;
				} else {
					i = 6;//odd y
					if (value < longMinAndMaxToCompare[i][0])
						longMinAndMaxToCompare[i][0] = value;
					if (value > longMinAndMaxToCompare[i][1])
						longMinAndMaxToCompare[i][1] = value;
				}
			}
		}
		long[][] longMinAndMax = new long[7][2];
		longMinAndMax[0] = longGrid.getMinAndMax();
		longMinAndMax[1] = longGrid.getEvenOddPositionsMinAndMax(true);
		longMinAndMax[2] = longGrid.getEvenOddPositionsMinAndMax(false);
		longMinAndMax[3] = longGrid.getMinAndMaxAtEvenOddX(true);
		longMinAndMax[4] = longGrid.getMinAndMaxAtEvenOddX(false);
		longMinAndMax[5] = longGrid.getMinAndMaxAtEvenOddY(true);
		longMinAndMax[6] = longGrid.getMinAndMaxAtEvenOddY(false);
		for (int i = 0; i < longMinAndMaxToCompare.length; i++) {
			if (longMinAndMax[i][0] != longMinAndMaxToCompare[i][0] || longMinAndMax[i][1] != longMinAndMaxToCompare[i][1]) {
				System.err.println(names[i] + " min and max don't match [" + longMinAndMax[i][0] + ", " + longMinAndMax[i][1] + "] != [" + longMinAndMaxToCompare[i][0] + ", " + longMinAndMaxToCompare[i][1] + "]");
				return;
			}
		}

		//Numeric
		System.out.println("Testing NumericModel2D");
		BigInt[][] bigIntValues = new BigInt[side][side];
		ArrayNumberGrid2D<BigInt> bigIntGrid = new ArrayNumberGrid2D<BigInt>(0, new int[side], bigIntValues);
		BigInt[][] bigIntMinAndMaxToCompare = new BigInt[7][2];
		for (int i = 0; i < bigIntMinAndMaxToCompare.length; i++) {
			bigIntMinAndMaxToCompare[i][0] = BigInt.valueOf(Long.MAX_VALUE);
			bigIntMinAndMaxToCompare[i][1] = BigInt.valueOf(Long.MIN_VALUE);
		}
		for (int x = 0; x < side; x++) {
			for (int y = 0; y < side; y++) {
				boolean isEvenPosition = (x+y)%2 == 0;
				boolean isEvenX = x%2 == 0;
				boolean isEvenY = y%2 == 0;
				BigInt value = BigInt.valueOf(random.nextLong());
				bigIntValues[x][y] = value;
				int i = 0; //all positions
				if (value.compareTo(bigIntMinAndMaxToCompare[i][0]) < 0)
					bigIntMinAndMaxToCompare[i][0] = value;
				if (value.compareTo(bigIntMinAndMaxToCompare[i][1]) > 0)
					bigIntMinAndMaxToCompare[i][1] = value;
				if (isEvenPosition) {
					i = 1;//even
					if (value.compareTo(bigIntMinAndMaxToCompare[i][0]) < 0)
						bigIntMinAndMaxToCompare[i][0] = value;
					if (value.compareTo(bigIntMinAndMaxToCompare[i][1]) > 0)
						bigIntMinAndMaxToCompare[i][1] = value;
				} else {
					i = 2;//odd
					if (value.compareTo(bigIntMinAndMaxToCompare[i][0]) < 0)
						bigIntMinAndMaxToCompare[i][0] = value;
					if (value.compareTo(bigIntMinAndMaxToCompare[i][1]) > 0)
						bigIntMinAndMaxToCompare[i][1] = value;
				}
				if (isEvenX) {
					i = 3;//even x
					if (value.compareTo(bigIntMinAndMaxToCompare[i][0]) < 0)
						bigIntMinAndMaxToCompare[i][0] = value;
					if (value.compareTo(bigIntMinAndMaxToCompare[i][1]) > 0)
						bigIntMinAndMaxToCompare[i][1] = value;
				} else {
					i = 4;//odd x
					if (value.compareTo(bigIntMinAndMaxToCompare[i][0]) < 0)
						bigIntMinAndMaxToCompare[i][0] = value;
					if (value.compareTo(bigIntMinAndMaxToCompare[i][1]) > 0)
						bigIntMinAndMaxToCompare[i][1] = value;
				}
				if (isEvenY) {
					i = 5;//even y
					if (value.compareTo(bigIntMinAndMaxToCompare[i][0]) < 0)
						bigIntMinAndMaxToCompare[i][0] = value;
					if (value.compareTo(bigIntMinAndMaxToCompare[i][1]) > 0)
						bigIntMinAndMaxToCompare[i][1] = value;
				} else {
					i = 6;//odd y
					if (value.compareTo(bigIntMinAndMaxToCompare[i][0]) < 0)
						bigIntMinAndMaxToCompare[i][0] = value;
					if (value.compareTo(bigIntMinAndMaxToCompare[i][1]) > 0)
						bigIntMinAndMaxToCompare[i][1] = value;
				}
			}
		}
		@SuppressWarnings("unchecked")
		MinAndMax<BigInt>[] bigIntMinAndMax = (MinAndMax<BigInt>[]) Array.newInstance(new MinAndMax<BigInt>(BigInt.ZERO, BigInt.ONE).getClass(), 7);
		bigIntMinAndMax[0] = bigIntGrid.getMinAndMax();
		bigIntMinAndMax[1] = bigIntGrid.getEvenOddPositionsMinAndMax(true);
		bigIntMinAndMax[2] = bigIntGrid.getEvenOddPositionsMinAndMax(false);
		bigIntMinAndMax[3] = bigIntGrid.getMinAndMaxAtEvenOddX(true);
		bigIntMinAndMax[4] = bigIntGrid.getMinAndMaxAtEvenOddX(false);
		bigIntMinAndMax[5] = bigIntGrid.getMinAndMaxAtEvenOddY(true);
		bigIntMinAndMax[6] = bigIntGrid.getMinAndMaxAtEvenOddY(false);
		for (int i = 0; i < bigIntMinAndMaxToCompare.length; i++) {
			if (!bigIntMinAndMax[i].getMin().equals(bigIntMinAndMaxToCompare[i][0]) || !bigIntMinAndMax[i].getMax().equals(bigIntMinAndMaxToCompare[i][1])) {
				System.err.println(names[i] + " min and max don't match [" + bigIntMinAndMax[i].getMin() + ", " + bigIntMinAndMax[i].getMax() + "] != [" + bigIntMinAndMaxToCompare[i][0] + ", " + bigIntMinAndMaxToCompare[i][1] + "]");
				return;
			}
		}
		System.out.println("Min and max values match!");
	}	

	private static final String BOUNDS_CONSISTENCY_ERROR_FORMAT = "Inconsistency found in bounds in %s traversal%n";
	
	public static void testBoundsConsistency(Model2D grid) {
		boolean consistentBounds = true;
		HashMap<Integer, int[]> positions = new HashMap<Integer, int[]>();
		int[] minMaxY;
		//xy
		int firstPositionCount = 0;
		int maxX = grid.getMaxX();
		for (int x = grid.getMinX(); x <= maxX; x++) {
			minMaxY = new int[2];
			positions.put(x, minMaxY);
			int localMinY = grid.getMinY(x);
			int localMaxY = grid.getMaxY(x);
			minMaxY[0] = localMinY;
			minMaxY[1] = localMaxY;
			firstPositionCount += localMaxY - localMinY + 1;
		}
		//yx
		String traversal = "yx";
		int positionCount = 0;
		int maxY = grid.getMaxY();
		for (int y = grid.getMinY(); y <= maxY; y++) {
			int localMaxX = grid.getMaxX(y);
			for (int x = grid.getMinX(y); x <= localMaxX; x++) {
				positionCount++;
				if ((minMaxY = positions.get(x)) == null || y < minMaxY[0] || y > minMaxY[1]) {
					System.err.printf(BOUNDS_CONSISTENCY_ERROR_FORMAT, traversal);
					consistentBounds = false;
				}
			}
		}
		if (firstPositionCount != positionCount) {
			System.err.printf(BOUNDS_CONSISTENCY_ERROR_FORMAT, traversal);
			consistentBounds = false;
		}
		if (consistentBounds) {
			System.out.println("Bounds are consistent!");
		}
	}

	public static void testBoundsConsistency(Model3D grid) {
		boolean consistentBounds = true;
		HashMap<Integer, HashMap<Integer, int[]>> positions = new HashMap<Integer, HashMap<Integer, int[]>>();
		HashMap<Integer, int[]> yzs;
		int[] minMaxZ;
		//xyz
		int firstPositionCount = 0;
		int maxX = grid.getMaxX();
		for (int x = grid.getMinX(); x <= maxX; x++) {
			yzs = new HashMap<Integer, int[]>();
			positions.put(x, yzs);
			int localMaxY = grid.getMaxYAtX(x);
			for (int y = grid.getMinYAtX(x); y <= localMaxY; y++) {
				minMaxZ = new int[2];
				yzs.put(y, minMaxZ);
				int localMinZ = grid.getMinZ(x, y);
				int localMaxZ = grid.getMaxZ(x, y);
				minMaxZ[0] = localMinZ;
				minMaxZ[1] = localMaxZ;
				firstPositionCount += localMaxZ - localMinZ + 1;
			}
		}
		//xzy
		String traversal = "xzy";
		int positionCount = 0;
		maxX = grid.getMaxX();
		for (int x = grid.getMinX(); x <= maxX; x++) {
			int localMaxZ = grid.getMaxZAtX(x);
			for (int z = grid.getMinZAtX(x); z <= localMaxZ; z++) {
				int localMaxY = grid.getMaxY(x, z);
				for (int y = grid.getMinY(x, z); y <= localMaxY; y++) {
					positionCount++;
					if ((yzs = positions.get(x)) == null || (minMaxZ = yzs.get(y)) == null || z < minMaxZ[0] || z > minMaxZ[1]) {
						System.err.printf(BOUNDS_CONSISTENCY_ERROR_FORMAT, traversal);
						consistentBounds = false;
					}
				}
			}
		}
		if (firstPositionCount != positionCount) {
			System.err.printf(BOUNDS_CONSISTENCY_ERROR_FORMAT, traversal);
			consistentBounds = false;
		}
		//yxz
		traversal = "yxz";
		positionCount = 0;
		int maxY = grid.getMaxY();
		for (int y = grid.getMinY(); y <= maxY; y++) {
			int localMaxX = grid.getMaxXAtY(y);
			for (int x = grid.getMinXAtY(y); x <= localMaxX; x++) {
				int localMaxZ = grid.getMaxZ(x, y);
				for (int z = grid.getMinZ(x, y); z <= localMaxZ; z++) {
					positionCount++;
					if ((yzs = positions.get(x)) == null || (minMaxZ = yzs.get(y)) == null || z < minMaxZ[0] || z > minMaxZ[1]) {
						System.err.printf(BOUNDS_CONSISTENCY_ERROR_FORMAT, traversal);
						consistentBounds = false;
					}
				}
			}
		}
		if (firstPositionCount != positionCount) {
			System.err.printf(BOUNDS_CONSISTENCY_ERROR_FORMAT, traversal);
			consistentBounds = false;
		}
		//yzx
		traversal = "yzx";
		positionCount = 0;
		maxY = grid.getMaxY();
		for (int y = grid.getMinY(); y <= maxY; y++) {
			int localMaxZ = grid.getMaxZAtY(y);
			for (int z = grid.getMinZAtY(y); z <= localMaxZ; z++) {
				int localMaxX = grid.getMaxX(y, z);
				for (int x = grid.getMinX(y, z); x <= localMaxX; x++) {
					positionCount++;
					if ((yzs = positions.get(x)) == null || (minMaxZ = yzs.get(y)) == null || z < minMaxZ[0] || z > minMaxZ[1]) {
						System.err.printf(BOUNDS_CONSISTENCY_ERROR_FORMAT, traversal);
						consistentBounds = false;
					}
				}
			}
		}
		if (firstPositionCount != positionCount) {
			System.err.printf(BOUNDS_CONSISTENCY_ERROR_FORMAT, traversal);
			consistentBounds = false;
		}
		//zxy
		traversal = "zxy";
		positionCount = 0;
		int maxZ = grid.getMaxZ();
		for (int z = grid.getMinZ(); z <= maxZ; z++) {
			int localMaxX = grid.getMaxXAtZ(z);
			for (int x = grid.getMinXAtZ(z); x <= localMaxX; x++) {
				int localMaxY = grid.getMaxY(x, z);
				for (int y = grid.getMinY(x, z); y <= localMaxY; y++) {
					positionCount++;
					if ((yzs = positions.get(x)) == null || (minMaxZ = yzs.get(y)) == null || z < minMaxZ[0] || z > minMaxZ[1]) {
						System.err.printf(BOUNDS_CONSISTENCY_ERROR_FORMAT, traversal);
						consistentBounds = false;
					}
				}
			}
		}
		if (firstPositionCount != positionCount) {
			System.err.printf(BOUNDS_CONSISTENCY_ERROR_FORMAT, traversal);
			consistentBounds = false;
		}
		//zyx
		traversal = "zyx";
		positionCount = 0;
		maxZ = grid.getMaxZ();
		for (int z = grid.getMinZ(); z <= maxZ; z++) {
			int localMaxY = grid.getMaxYAtZ(z);
			for (int y = grid.getMinYAtZ(z); y <= localMaxY; y++) {
				int localMaxX = grid.getMaxX(y, z);
				for (int x = grid.getMinX(y, z); x <= localMaxX; x++) {
					positionCount++;
					if ((yzs = positions.get(x)) == null || (minMaxZ = yzs.get(y)) == null || z < minMaxZ[0] || z > minMaxZ[1]) {
						System.err.printf(BOUNDS_CONSISTENCY_ERROR_FORMAT, traversal);
						consistentBounds = false;
					}
				}
			}
		}
		if (firstPositionCount != positionCount) {
			System.err.printf(BOUNDS_CONSISTENCY_ERROR_FORMAT, traversal);
			consistentBounds = false;
		}
		if (consistentBounds) {
			System.out.println("Bounds are consistent!");
		}
	}

	public static void testBoundsConsistency(Model grid) {
		boolean consistentBounds = true;
		int dimension = grid.getGridDimension();
		if (dimension > 0) {
			CounterConsumer<Coordinates> counter;
			WithinBoundsCoordinatesConsumer withinBounds;
			int[] axes = new int[dimension];
			for (int axis = 0; axis != dimension; axis++) {
				axes[axis] = axis;
			}
			List<int[]> axesPermutations = getPermutations(axes);
			int[] axesOrder = axesPermutations.get(0);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i != dimension; i++) {
				sb.append(Utils.getAxisLabel(dimension, axesOrder[i]));
			}
			String traversal = sb.toString();
			grid.forEachPosition((counter = new CounterConsumer<Coordinates>())
					.andThen((withinBounds = new WithinBoundsCoordinatesConsumer(grid))), axesOrder);
			if (!withinBounds.isWithinBounds()) {
				System.err.printf(BOUNDS_CONSISTENCY_ERROR_FORMAT, traversal);
				consistentBounds = false;
			}
			BigInt firstPositionCount = counter.getCount();
			for (int i = 1; i < axesPermutations.size(); i++) {
				axesOrder = axesPermutations.get(i);
				sb = new StringBuilder();
				for (int j = 0; j != dimension; j++) {
					sb.append(Utils.getAxisLabel(dimension, axesOrder[j]));
				}
				traversal = sb.toString();
				grid.forEachPosition((counter = new CounterConsumer<Coordinates>())
						.andThen((withinBounds = new WithinBoundsCoordinatesConsumer(grid))), axesOrder);
				if (!withinBounds.isWithinBounds()) {
					System.err.printf(BOUNDS_CONSISTENCY_ERROR_FORMAT, traversal);
					consistentBounds = false;
				}
				if (!firstPositionCount.equals(counter.getCount())) {
					System.err.printf(BOUNDS_CONSISTENCY_ERROR_FORMAT, traversal);
					consistentBounds = false;
				}
			}
		}
		if (consistentBounds) {
			System.out.println("Bounds are consistent!");
		}
	}
	
	private static List<int[]> getPermutations(int[] array) {
		List<int[]> permutations = new ArrayList<int[]>();
		heapPermutations(array, array.length, permutations);
		return permutations;
	}
	
	// Get permutations using Heap's Algorithm
    private static void heapPermutations(int array[], int size, List<int[]> permutations)
    {
        if (size == 1) {
        	permutations.add(array.clone());
        }
 
        for (int i = 0; i < size; i++) {
            heapPermutations(array, size - 1, permutations);
 
            // if size is odd, swap 0th i.e (first) and
            // (size-1)th i.e (last) element
            if (size % 2 == 1) {
                int temp = array[0];
                array[0] = array[size - 1];
                array[size - 1] = temp;
            }
 
            // If size is even, swap ith
            // and (size-1)th i.e last element
            else {
                int temp = array[i];
                array[i] = array[size - 1];
                array[size - 1] = temp;
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
		System.out.println("unsupported shapes");
		try {
			grid = new ArrayIntGrid2D(0, new int[] {0, 0, -2, 0, 0}, new int[][] { {1}, {1}, {1, 1, 1, 1, 1}, {1}, {1} });
		} catch(Exception ex) { 
			ex.printStackTrace(System.out);}
		try {
			grid = new ArrayIntGrid2D(0, new int[] {0, 0, 0, 0, 0}, new int[][] { {1}, {1}, {1, 1, 1, 1, 1}, {1}, {1} });
		} catch(Exception ex) { 
			ex.printStackTrace(System.out);}
		try {
			grid = new ArrayIntGrid2D(0, new int[] {0, 0, 0, 0, 0}, new int[][] { {1}, {1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1}, {1} });
		} catch(Exception ex) { 
			ex.printStackTrace(System.out);}
		try {
			grid = new ArrayIntGrid2D(0, new int[] {0, 0, 0, 0, 0, 0}, new int[][] { {1}, {1}, {1, 1}, {1, 1}, {1}, {1} });
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
			Utils.printAsGrid(grid);
			System.out.println("single line horiz");
			grid = new ArrayIntGrid2D(Integer.MIN_VALUE, new int[5], new int[][] { {1}, {1}, {1}, {1}, {1} });
			Utils.printAsGrid(grid);
			System.out.println("single line vert");
			grid = new ArrayIntGrid2D(Integer.MIN_VALUE, new int[1], new int[][] { {1, 1, 1, 1, 1 } });
			Utils.printAsGrid(grid);
			System.out.println("single line diagonal");
			grid = new ArrayIntGrid2D(Integer.MIN_VALUE, new int[] {-2, -1, 0, 1, 2 }, new int[][] { {1}, {1}, {1}, {1}, {1} });
			Utils.printAsGrid(grid);
			System.out.println("single line diagonal inverted");
			grid = new ArrayIntGrid2D(Integer.MIN_VALUE, new int[] {2, 1, 0, -1, -2 }, new int[][] { {1}, {1}, {1}, {1}, {1} });
			Utils.printAsGrid(grid);
			System.out.println("tilted square");
			grid = new ArrayIntGrid2D(Integer.MIN_VALUE, new int[] {0, -1, -2, -3, -2, -1, 0 }, 
					new int[][] { {1}, {1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1}, {1} });
			Utils.printAsGrid(grid);
			System.out.println("aligned square");
			grid = new ArrayIntGrid2D(Integer.MIN_VALUE, new int[] {-3, -3, -3, -3, -3 }, 
					new int[][] { {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1} });
			Utils.printAsGrid(grid);
			System.out.println("triangle 1");
			grid = new ArrayIntGrid2D(Integer.MIN_VALUE, new int[] {0, 0, 0, 0, 0, 0, 0 }, 
					new int[][] { {1}, {1, 1}, {1, 1, 1}, {1, 1, 1, 1}, {1, 1, 1}, {1, 1}, {1} });
			Utils.printAsGrid(grid);
			System.out.println("triangle 2");
			grid = new ArrayIntGrid2D(Integer.MIN_VALUE, new int[] {0, -1, -2, -3, -2, -1, 0 }, 
					new int[][] { {1}, {1, 1}, {1, 1, 1}, {1, 1, 1, 1}, {1, 1, 1}, {1, 1}, {1} });
			Utils.printAsGrid(grid);
			System.out.println("triangle 3");
			grid = new ArrayIntGrid2D(Integer.MIN_VALUE, new int[] {-3, -2, -1, 0}, 
					new int[][] { {1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1}, {1} });
			Utils.printAsGrid(grid);
			System.out.println("triangle 4");
			grid = new ArrayIntGrid2D(Integer.MIN_VALUE, new int[] {0, -1, -2, -3}, 
					new int[][] { {1}, {1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1} });
			Utils.printAsGrid(grid);
			System.out.println("beveled square");
			grid = new ArrayIntGrid2D(Integer.MIN_VALUE, new int[] {1, 0, 0, 0, 0, 0, 1}, 
					new int[][] { {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1} });
			Utils.printAsGrid(grid);
			System.out.println("object grid");
			BigInt[][] array = testGenericArrayBySample(BigInt.ZERO, 1, 1);
			array[0][0] = BigInt.ONE;
			ArrayNumberGrid2D<BigInt> nubergrid = new ArrayNumberGrid2D<BigInt>(0, new int[1], array);
			Utils.printAsGrid(nubergrid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <Object_Type> Object_Type[][] testGenericArrayBySample(Object_Type sample, int width, int height) {
		Class<Object_Type> clazz = (Class<Object_Type>) sample.getClass();
		Class<Object_Type[]> arrayClass = (Class<Object_Type[]>) Array.newInstance(clazz, 0).getClass();
		Object_Type[][] arr = (Object_Type[][]) Array.newInstance(arrayClass, width);
		for (int i = 0; i < arr.length; i++) {
			arr[i] = (Object_Type[]) Array.newInstance(clazz, height);
		}
		return arr;
	}
	
	public static void test2DDiagonals() throws Exception {
		int[][][] sourceValues;
		int[][] resultValues;
		RegularIntGrid3D grid;
		IntModel2D diagonal;
		int side = 21;
		int originIndex = side/2;
		int offset = 5;
		ThreadLocalRandom random = ThreadLocalRandom.current();
		Model3D pyramid1 = new ModelAs3D<Model>(new HypercubicPyramidGrid(new Coordinates(0,0,0), 13, 0));
		Model3D pyramid2 = new ModelAs3D<Model>(new HypercubicPyramidGrid(new Coordinates(0,0,0), 13, 1));
		Model3D pyramid3 = new ModelAs3D<Model>(new HypercubicPyramidGrid(new Coordinates(0,0,0), 13, 2));
		//xy 
		for (int slope = 1; slope != -3; slope -= 2) {
			for (int off = -offset; off <= offset; off += offset) {
				sourceValues = new int[side][side][side];
				resultValues = new int[side][side];
				for (int x = -originIndex; x < side-originIndex; x++) {
					for (int z = -originIndex; z < side-originIndex; z++) {
						int value = random.nextInt();
						int y = slope*x+off;
						if (y < side-originIndex && y >= -originIndex) {
							sourceValues[x+originIndex][y+originIndex][z+originIndex] = value;
							resultValues[x+originIndex][z+originIndex] = value;	
						}
					}
				}			
				grid = new RegularIntGrid3D(sourceValues, -originIndex, -originIndex, -originIndex);
				diagonal = grid.diagonalCrossSectionOnXY(slope == 1, off);
				compare(diagonal, resultValues, originIndex, originIndex);
				testBoundsConsistency(diagonal);
				testBoundsConsistency(pyramid1.diagonalCrossSectionOnXY(slope == 1, off));
				testBoundsConsistency(pyramid2.diagonalCrossSectionOnXY(slope == 1, off));
				testBoundsConsistency(pyramid3.diagonalCrossSectionOnXY(slope == 1, off));
			}
		}
		//xz
		for (int slope = 1; slope != -3; slope -= 2) {
			for (int off = -offset; off <= offset; off += offset) {
				sourceValues = new int[side][side][side];
				resultValues = new int[side][side];
				for (int x = -originIndex; x < side-originIndex; x++) {
					for (int y = -originIndex; y < side-originIndex; y++) {
						int value = random.nextInt();
						int z = slope*x+off;
						if (z < side-originIndex && z >= -originIndex) {
							sourceValues[x+originIndex][y+originIndex][z+originIndex] = value;
							resultValues[x+originIndex][y+originIndex] = value;
						}
					}
				}			
				grid = new RegularIntGrid3D(sourceValues, -originIndex, -originIndex, -originIndex);
				diagonal = grid.diagonalCrossSectionOnXZ(slope == 1, off);
				compare(diagonal, resultValues, originIndex, originIndex);
				testBoundsConsistency(diagonal);
				testBoundsConsistency(pyramid1.diagonalCrossSectionOnXZ(slope == 1, off));
				testBoundsConsistency(pyramid2.diagonalCrossSectionOnXZ(slope == 1, off));
				testBoundsConsistency(pyramid3.diagonalCrossSectionOnXZ(slope == 1, off));
			}
		}
		//yz
		for (int slope = 1; slope != -3; slope -= 2) {
			for (int off = -offset; off <= offset; off += offset) {
				sourceValues = new int[side][side][side];
				resultValues = new int[side][side];
				for (int x = -originIndex; x < side-originIndex; x++) {
					for (int y = -originIndex; y < side-originIndex; y++) {
						int value = random.nextInt();
						int z = slope*y+off;
						if (z < side-originIndex && z >= -originIndex) {
							sourceValues[x+originIndex][y+originIndex][z+originIndex] = value;
							resultValues[x+originIndex][y+originIndex] = value;
						}
					}
				}			
				grid = new RegularIntGrid3D(sourceValues, -originIndex, -originIndex, -originIndex);
				diagonal = grid.diagonalCrossSectionOnYZ(slope == 1, off);
				compare(diagonal, resultValues, originIndex, originIndex);
				testBoundsConsistency(diagonal);
				testBoundsConsistency(pyramid1.diagonalCrossSectionOnYZ(slope == 1, off));
				testBoundsConsistency(pyramid2.diagonalCrossSectionOnYZ(slope == 1, off));
				testBoundsConsistency(pyramid3.diagonalCrossSectionOnYZ(slope == 1, off));
			}
		}
	}
	
	public static void test2DCrossSections() throws Exception {
		int side = 21;
		int originIndex = side/2;
		int crossSectionCoord = 2;
		Model3D pyramid1 = new ModelAs3D<Model>(new HypercubicPyramidGrid(new Coordinates(-crossSectionCoord,0,0), 15, 0));
		Model3D pyramid2 = new ModelAs3D<Model>(new HypercubicPyramidGrid(new Coordinates(0,-crossSectionCoord,0), 15, 1));
		Model3D pyramid3 = new ModelAs3D<Model>(new HypercubicPyramidGrid(new Coordinates(0,0,-crossSectionCoord), 15, 2));
		int[][][] sourceValues = new int[side][side][side];		
		int[][] resultValues = new int[side][side];
		Utils.fillWithRandomValues(sourceValues, ThreadLocalRandom.current());
		RegularIntGrid3D grid = new RegularIntGrid3D(sourceValues, -originIndex, -originIndex, -originIndex);
		//x
		for (int coord = -crossSectionCoord; coord <= crossSectionCoord; coord += crossSectionCoord) {
			for (int y = -originIndex; y < side-originIndex; y++) {
				for (int z = -originIndex; z < side-originIndex; z++) {
					resultValues[y+originIndex][z+originIndex] = sourceValues[coord+originIndex][y+originIndex][z+originIndex];	
				}
			}			
			IntModel2D crossSection = grid.crossSectionAtX(coord);
			compare(crossSection, resultValues, originIndex, originIndex);
			testBoundsConsistency(crossSection);
			testBoundsConsistency(pyramid1.crossSectionAtX(coord));
			testBoundsConsistency(pyramid2.crossSectionAtX(coord));
			testBoundsConsistency(pyramid3.crossSectionAtX(coord));
		}
		//y
		for (int coord = -crossSectionCoord; coord <= crossSectionCoord; coord += crossSectionCoord) {
			for (int x = -originIndex; x < side-originIndex; x++) {
				for (int z = -originIndex; z < side-originIndex; z++) {
					resultValues[x+originIndex][z+originIndex] = sourceValues[x+originIndex][coord+originIndex][z+originIndex];	
				}
			}			
			IntModel2D crossSection = grid.crossSectionAtY(coord);
			compare(crossSection, resultValues, originIndex, originIndex);
			testBoundsConsistency(crossSection);
			testBoundsConsistency(pyramid1.crossSectionAtY(coord));
			testBoundsConsistency(pyramid2.crossSectionAtY(coord));
			testBoundsConsistency(pyramid3.crossSectionAtY(coord));
		}
		//z
		for (int coord = -crossSectionCoord; coord <= crossSectionCoord; coord += crossSectionCoord) {
			for (int x = -originIndex; x < side-originIndex; x++) {
				for (int y = -originIndex; y < side-originIndex; y++) {
					resultValues[x+originIndex][y+originIndex] = sourceValues[x+originIndex][y+originIndex][coord+originIndex];	
				}
			}			
			IntModel2D crossSection = grid.crossSectionAtZ(coord);
			compare(crossSection, resultValues, originIndex, originIndex);
			testBoundsConsistency(crossSection);
			testBoundsConsistency(pyramid1.crossSectionAtZ(coord));
			testBoundsConsistency(pyramid2.crossSectionAtZ(coord));
			testBoundsConsistency(pyramid3.crossSectionAtZ(coord));
		}
	}
	
	public static void test3DCrossSections() throws Exception {
		int side = 21;
		int originIndex = side/2;
		int crossSectionCoord = 2;
		Model4D pyramid1 = new ModelAs4D<Model>(new HypercubicPyramidGrid(new Coordinates(-crossSectionCoord,0,0,0), 15, 0));
		Model4D pyramid2 = new ModelAs4D<Model>(new HypercubicPyramidGrid(new Coordinates(0,-crossSectionCoord,0,0), 15, 1));
		Model4D pyramid3 = new ModelAs4D<Model>(new HypercubicPyramidGrid(new Coordinates(0,0,-crossSectionCoord,0), 15, 2));
		Model4D pyramid4 = new ModelAs4D<Model>(new HypercubicPyramidGrid(new Coordinates(0,0,0,-crossSectionCoord), 15, 3));
		int[][][][] sourceValues = new int[side][side][side][side];		
		int[][][] resultValues = new int[side][side][side];
		Utils.fillWithRandomValues(sourceValues, ThreadLocalRandom.current());
		RegularIntGrid4D grid = new RegularIntGrid4D(sourceValues, -originIndex, -originIndex, -originIndex, -originIndex);
		//w
		for (int coord = -crossSectionCoord; coord <= crossSectionCoord; coord += crossSectionCoord) {
			for (int x = -originIndex; x < side-originIndex; x++) {
				for (int y = -originIndex; y < side-originIndex; y++) {
					for (int z = -originIndex; z < side-originIndex; z++) {
						resultValues[x+originIndex][y+originIndex][z+originIndex] = sourceValues[coord+originIndex][x+originIndex][y+originIndex][z+originIndex];	
					}
				}
			}			
			IntModel3D crossSection = grid.crossSectionAtW(coord);
			compare(crossSection, new RegularIntGrid3D(resultValues, -originIndex, -originIndex, -originIndex));
			testBoundsConsistency(crossSection);
			testBoundsConsistency(pyramid1.crossSectionAtW(coord));
			testBoundsConsistency(pyramid2.crossSectionAtW(coord));
			testBoundsConsistency(pyramid3.crossSectionAtW(coord));
			testBoundsConsistency(pyramid4.crossSectionAtW(coord));
		}
		//x
		for (int coord = -crossSectionCoord; coord <= crossSectionCoord; coord += crossSectionCoord) {
			for (int w = -originIndex; w < side-originIndex; w++) {
				for (int y = -originIndex; y < side-originIndex; y++) {
					for (int z = -originIndex; z < side-originIndex; z++) {
						resultValues[w+originIndex][y+originIndex][z+originIndex] = sourceValues[w+originIndex][coord+originIndex][y+originIndex][z+originIndex];	
					}
				}
			}			
			IntModel3D crossSection = grid.crossSectionAtX(coord);
			compare(crossSection, new RegularIntGrid3D(resultValues, -originIndex, -originIndex, -originIndex));
			testBoundsConsistency(crossSection);
			testBoundsConsistency(pyramid1.crossSectionAtX(coord));
			testBoundsConsistency(pyramid2.crossSectionAtX(coord));
			testBoundsConsistency(pyramid3.crossSectionAtX(coord));
			testBoundsConsistency(pyramid4.crossSectionAtX(coord));
		}
		//y
		for (int coord = -crossSectionCoord; coord <= crossSectionCoord; coord += crossSectionCoord) {
			for (int w = -originIndex; w < side-originIndex; w++) {
				for (int x = -originIndex; x < side-originIndex; x++) {
					for (int z = -originIndex; z < side-originIndex; z++) {
						resultValues[w+originIndex][x+originIndex][z+originIndex] = sourceValues[w+originIndex][x+originIndex][coord+originIndex][z+originIndex];	
					}
				}
			}			
			IntModel3D crossSection = grid.crossSectionAtY(coord);
			compare(crossSection, new RegularIntGrid3D(resultValues, -originIndex, -originIndex, -originIndex));
			testBoundsConsistency(crossSection);
			testBoundsConsistency(pyramid1.crossSectionAtY(coord));
			testBoundsConsistency(pyramid2.crossSectionAtY(coord));
			testBoundsConsistency(pyramid3.crossSectionAtY(coord));
			testBoundsConsistency(pyramid4.crossSectionAtY(coord));
		}
		//z
		for (int coord = -crossSectionCoord; coord <= crossSectionCoord; coord += crossSectionCoord) {
			for (int w = -originIndex; w < side-originIndex; w++) {
				for (int x = -originIndex; x < side-originIndex; x++) {
					for (int y = -originIndex; y < side-originIndex; y++) {
						resultValues[w+originIndex][x+originIndex][y+originIndex] = sourceValues[w+originIndex][x+originIndex][y+originIndex][coord+originIndex];	
					}
				}
			}			
			IntModel3D crossSection = grid.crossSectionAtZ(coord);
			compare(crossSection, new RegularIntGrid3D(resultValues, -originIndex, -originIndex, -originIndex));
			testBoundsConsistency(crossSection);
			testBoundsConsistency(pyramid1.crossSectionAtZ(coord));
			testBoundsConsistency(pyramid2.crossSectionAtZ(coord));
			testBoundsConsistency(pyramid3.crossSectionAtZ(coord));
			testBoundsConsistency(pyramid4.crossSectionAtZ(coord));
		}
	}
	
	public static void test4DCrossSections() throws Exception {
		int side = 21;
		int originIndex = side/2;
		int crossSectionCoord = 2;
		Model5D pyramid1 = new ModelAs5D<Model>(new HypercubicPyramidGrid(new Coordinates(-crossSectionCoord,0,0,0,0), 15, 0));
		Model5D pyramid2 = new ModelAs5D<Model>(new HypercubicPyramidGrid(new Coordinates(0,-crossSectionCoord,0,0,0), 15, 1));
		Model5D pyramid3 = new ModelAs5D<Model>(new HypercubicPyramidGrid(new Coordinates(0,0,-crossSectionCoord,0,0), 15, 2));
		Model5D pyramid4 = new ModelAs5D<Model>(new HypercubicPyramidGrid(new Coordinates(0,0,0,-crossSectionCoord,0), 15, 3));
		Model5D pyramid5 = new ModelAs5D<Model>(new HypercubicPyramidGrid(new Coordinates(0,0,0,0,-crossSectionCoord), 15, 4));
		int[][][][][] sourceValues = new int[side][side][side][side][side];		
		int[][][][] resultValues = new int[side][side][side][side];
		Utils.fillWithRandomValues(sourceValues, ThreadLocalRandom.current());
		RegularIntGrid5D grid = new RegularIntGrid5D(sourceValues, -originIndex, -originIndex, -originIndex, -originIndex, -originIndex);
		//v
		for (int coord = -crossSectionCoord; coord <= crossSectionCoord; coord += crossSectionCoord) {
			for (int w = -originIndex; w < side-originIndex; w++) {
				for (int x = -originIndex; x < side-originIndex; x++) {
					for (int y = -originIndex; y < side-originIndex; y++) {
						for (int z = -originIndex; z < side-originIndex; z++) {
							resultValues[w+originIndex][x+originIndex][y+originIndex][z+originIndex] = sourceValues[coord+originIndex][w+originIndex][x+originIndex][y+originIndex][z+originIndex];	
						}
					}
				}
			}			
			IntModel4D crossSection = grid.crossSectionAtV(coord);
			compare(crossSection, new RegularIntGrid4D(resultValues, -originIndex, -originIndex, -originIndex, -originIndex));
			testBoundsConsistency(crossSection);
			testBoundsConsistency(pyramid1.crossSectionAtV(coord));
			testBoundsConsistency(pyramid2.crossSectionAtV(coord));
			testBoundsConsistency(pyramid3.crossSectionAtV(coord));
			testBoundsConsistency(pyramid4.crossSectionAtV(coord));
			testBoundsConsistency(pyramid5.crossSectionAtV(coord));
		}
		//w
		for (int coord = -crossSectionCoord; coord <= crossSectionCoord; coord += crossSectionCoord) {
			for (int v = -originIndex; v < side-originIndex; v++) {
				for (int x = -originIndex; x < side-originIndex; x++) {
					for (int y = -originIndex; y < side-originIndex; y++) {
						for (int z = -originIndex; z < side-originIndex; z++) {
							resultValues[v+originIndex][x+originIndex][y+originIndex][z+originIndex] = sourceValues[v+originIndex][coord+originIndex][x+originIndex][y+originIndex][z+originIndex];	
						}
					}
				}
			}			
			IntModel4D crossSection = grid.crossSectionAtW(coord);
			compare(crossSection, new RegularIntGrid4D(resultValues, -originIndex, -originIndex, -originIndex, -originIndex));
			testBoundsConsistency(crossSection);
			testBoundsConsistency(pyramid1.crossSectionAtW(coord));
			testBoundsConsistency(pyramid2.crossSectionAtW(coord));
			testBoundsConsistency(pyramid3.crossSectionAtW(coord));
			testBoundsConsistency(pyramid4.crossSectionAtW(coord));
			testBoundsConsistency(pyramid5.crossSectionAtW(coord));
		}
		//x
		for (int coord = -crossSectionCoord; coord <= crossSectionCoord; coord += crossSectionCoord) {
			for (int v = -originIndex; v < side-originIndex; v++) {
				for (int w = -originIndex; w < side-originIndex; w++) {
					for (int y = -originIndex; y < side-originIndex; y++) {
						for (int z = -originIndex; z < side-originIndex; z++) {
							resultValues[v+originIndex][w+originIndex][y+originIndex][z+originIndex] = sourceValues[v+originIndex][w+originIndex][coord+originIndex][y+originIndex][z+originIndex];	
						}
					}
				}
			}			
			IntModel4D crossSection = grid.crossSectionAtX(coord);
			compare(crossSection, new RegularIntGrid4D(resultValues, -originIndex, -originIndex, -originIndex, -originIndex));
			testBoundsConsistency(crossSection);
			testBoundsConsistency(pyramid1.crossSectionAtX(coord));
			testBoundsConsistency(pyramid2.crossSectionAtX(coord));
			testBoundsConsistency(pyramid3.crossSectionAtX(coord));
			testBoundsConsistency(pyramid4.crossSectionAtX(coord));
			testBoundsConsistency(pyramid5.crossSectionAtX(coord));
		}
		//y
		for (int coord = -crossSectionCoord; coord <= crossSectionCoord; coord += crossSectionCoord) {
			for (int v = -originIndex; v < side-originIndex; v++) {
				for (int w = -originIndex; w < side-originIndex; w++) {
					for (int x = -originIndex; x < side-originIndex; x++) {
						for (int z = -originIndex; z < side-originIndex; z++) {
							resultValues[v+originIndex][w+originIndex][x+originIndex][z+originIndex] = sourceValues[v+originIndex][w+originIndex][x+originIndex][coord+originIndex][z+originIndex];	
						}
					}
				}
			}			
			IntModel4D crossSection = grid.crossSectionAtY(coord);
			compare(crossSection, new RegularIntGrid4D(resultValues, -originIndex, -originIndex, -originIndex, -originIndex));
			testBoundsConsistency(crossSection);
			testBoundsConsistency(pyramid1.crossSectionAtY(coord));
			testBoundsConsistency(pyramid2.crossSectionAtY(coord));
			testBoundsConsistency(pyramid3.crossSectionAtY(coord));
			testBoundsConsistency(pyramid4.crossSectionAtY(coord));
			testBoundsConsistency(pyramid5.crossSectionAtY(coord));
		}
		//z
		for (int coord = -crossSectionCoord; coord <= crossSectionCoord; coord += crossSectionCoord) {
			for (int v = -originIndex; v < side-originIndex; v++) {
				for (int w = -originIndex; w < side-originIndex; w++) {
					for (int x = -originIndex; x < side-originIndex; x++) {
						for (int y = -originIndex; y < side-originIndex; y++) {
							resultValues[v+originIndex][w+originIndex][x+originIndex][y+originIndex] = sourceValues[v+originIndex][w+originIndex][x+originIndex][y+originIndex][coord+originIndex];	
						}
					}
				}
			}			
			IntModel4D crossSection = grid.crossSectionAtZ(coord);
			compare(crossSection, new RegularIntGrid4D(resultValues, -originIndex, -originIndex, -originIndex, -originIndex));
			testBoundsConsistency(crossSection);
			testBoundsConsistency(pyramid1.crossSectionAtZ(coord));
			testBoundsConsistency(pyramid2.crossSectionAtZ(coord));
			testBoundsConsistency(pyramid3.crossSectionAtZ(coord));
			testBoundsConsistency(pyramid4.crossSectionAtZ(coord));
			testBoundsConsistency(pyramid5.crossSectionAtZ(coord));
		}
	}
	
	public static void printRegionBounds(Model region) {
		int dimension = region.getGridDimension();
		for (int axis = 0; axis < dimension; axis++) {
			String axisLabel = Utils.getAxisLabel(dimension, axis);
			System.out.printf("%d <= %c <= %d %n", region.getMinCoordinate(axis), axisLabel, region.getMaxCoordinate(axis));
		}
	}
	
	public static void test3DDiagonals() throws Exception {
		int[][][][] sourceValues;
		int[][][] resultValues;
		RegularIntGrid4D grid;
		IntModel3D diagonal;
		int side = 21;
		int originIndex = side/2;
		int offset = 5;
		ThreadLocalRandom random = ThreadLocalRandom.current();
		Model4D pyramid1 = new ModelAs4D<Model>(new HypercubicPyramidGrid(new Coordinates(0,0,0,0), 13, 0));
		Model4D pyramid2 = new ModelAs4D<Model>(new HypercubicPyramidGrid(new Coordinates(0,0,0,0), 13, 1));
		Model4D pyramid3 = new ModelAs4D<Model>(new HypercubicPyramidGrid(new Coordinates(0,0,0,0), 13, 2));
		Model4D pyramid4 = new ModelAs4D<Model>(new HypercubicPyramidGrid(new Coordinates(0,0,0,0), 13, 3));
		//wx 
		for (int slope = 1; slope != -3; slope -= 2) {
			for (int off = -offset; off <= offset; off += offset) {
				sourceValues = new int[side][side][side][side];
				resultValues = new int[side][side][side];
				for (int w = -originIndex; w < side-originIndex; w++) {
					for (int y = -originIndex; y < side-originIndex; y++) {
						for (int z = -originIndex; z < side-originIndex; z++) {
							int value = random.nextInt();
							int x = slope*w+off;
							if (x < side-originIndex && x >= -originIndex) {
								sourceValues[w+originIndex][x+originIndex][y+originIndex][z+originIndex] = value;
								resultValues[w+originIndex][y+originIndex][z+originIndex] = value;	
							}
						}
					}
				}			
				grid = new RegularIntGrid4D(sourceValues, -originIndex, -originIndex, -originIndex, -originIndex);
				diagonal = grid.diagonalCrossSectionOnWX(slope == 1, off);
				compare(diagonal, new RegularIntGrid3D(resultValues, -originIndex, -originIndex, -originIndex));
				testBoundsConsistency(diagonal);
				testBoundsConsistency(pyramid1.diagonalCrossSectionOnWX(slope == 1, off));
				testBoundsConsistency(pyramid2.diagonalCrossSectionOnWX(slope == 1, off));
				testBoundsConsistency(pyramid3.diagonalCrossSectionOnWX(slope == 1, off));
				testBoundsConsistency(pyramid4.diagonalCrossSectionOnWX(slope == 1, off));
			}
		}
		//wy 
		for (int slope = 1; slope != -3; slope -= 2) {
			for (int off = -offset; off <= offset; off += offset) {
				sourceValues = new int[side][side][side][side];
				resultValues = new int[side][side][side];
				for (int w = -originIndex; w < side-originIndex; w++) {
					for (int x = -originIndex; x < side-originIndex; x++) {
						for (int z = -originIndex; z < side-originIndex; z++) {
							int value = random.nextInt();
							int y = slope*w+off;
							if (y < side-originIndex && y >= -originIndex) {
								sourceValues[w+originIndex][x+originIndex][y+originIndex][z+originIndex] = value;
								resultValues[w+originIndex][x+originIndex][z+originIndex] = value;	
							}
						}
					}
				}			
				grid = new RegularIntGrid4D(sourceValues, -originIndex, -originIndex, -originIndex, -originIndex);
				diagonal = grid.diagonalCrossSectionOnWY(slope == 1, off);
				compare(diagonal, new RegularIntGrid3D(resultValues, -originIndex, -originIndex, -originIndex));
				testBoundsConsistency(diagonal);
				testBoundsConsistency(pyramid1.diagonalCrossSectionOnWY(slope == 1, off));
				testBoundsConsistency(pyramid2.diagonalCrossSectionOnWY(slope == 1, off));
				testBoundsConsistency(pyramid3.diagonalCrossSectionOnWY(slope == 1, off));
				testBoundsConsistency(pyramid4.diagonalCrossSectionOnWY(slope == 1, off));
			}
		}
		//wz 
		for (int slope = 1; slope != -3; slope -= 2) {
			for (int off = -offset; off <= offset; off += offset) {
				sourceValues = new int[side][side][side][side];
				resultValues = new int[side][side][side];
				for (int w = -originIndex; w < side-originIndex; w++) {
					for (int y = -originIndex; y < side-originIndex; y++) {
						for (int x = -originIndex; x < side-originIndex; x++) {
							int value = random.nextInt();
							int z = slope*w+off;
							if (z < side-originIndex && z >= -originIndex) {
								sourceValues[w+originIndex][x+originIndex][y+originIndex][z+originIndex] = value;
								resultValues[w+originIndex][x+originIndex][y+originIndex] = value;	
							}
						}
					}
				}			
				grid = new RegularIntGrid4D(sourceValues, -originIndex, -originIndex, -originIndex, -originIndex);
				diagonal = grid.diagonalCrossSectionOnWZ(slope == 1, off);
				compare(diagonal, new RegularIntGrid3D(resultValues, -originIndex, -originIndex, -originIndex));
				testBoundsConsistency(diagonal);
				testBoundsConsistency(pyramid1.diagonalCrossSectionOnWZ(slope == 1, off));
				testBoundsConsistency(pyramid2.diagonalCrossSectionOnWZ(slope == 1, off));
				testBoundsConsistency(pyramid3.diagonalCrossSectionOnWZ(slope == 1, off));
				testBoundsConsistency(pyramid4.diagonalCrossSectionOnWZ(slope == 1, off));
			}
		}
		//xy 
		for (int slope = 1; slope != -3; slope -= 2) {
			for (int off = -offset; off <= offset; off += offset) {
				sourceValues = new int[side][side][side][side];
				resultValues = new int[side][side][side];
				for (int w = -originIndex; w < side-originIndex; w++) {
					for (int x = -originIndex; x < side-originIndex; x++) {
						for (int z = -originIndex; z < side-originIndex; z++) {
							int value = random.nextInt();
							int y = slope*x+off;
							if (y < side-originIndex && y >= -originIndex) {
								sourceValues[w+originIndex][x+originIndex][y+originIndex][z+originIndex] = value;
								resultValues[w+originIndex][x+originIndex][z+originIndex] = value;	
							}
						}
					}
				}			
				grid = new RegularIntGrid4D(sourceValues, -originIndex, -originIndex, -originIndex, -originIndex);
				diagonal = grid.diagonalCrossSectionOnXY(slope == 1, off);
				compare(diagonal, new RegularIntGrid3D(resultValues, -originIndex, -originIndex, -originIndex));
				testBoundsConsistency(diagonal);
				testBoundsConsistency(pyramid1.diagonalCrossSectionOnXY(slope == 1, off));
				testBoundsConsistency(pyramid2.diagonalCrossSectionOnXY(slope == 1, off));
				testBoundsConsistency(pyramid3.diagonalCrossSectionOnXY(slope == 1, off));
				testBoundsConsistency(pyramid4.diagonalCrossSectionOnXY(slope == 1, off));
			}
		}
		//xz 
		for (int slope = 1; slope != -3; slope -= 2) {
			for (int off = -offset; off <= offset; off += offset) {
				sourceValues = new int[side][side][side][side];
				resultValues = new int[side][side][side];
				for (int w = -originIndex; w < side-originIndex; w++) {
					for (int y = -originIndex; y < side-originIndex; y++) {
						for (int x = -originIndex; x < side-originIndex; x++) {
							int value = random.nextInt();
							int z = slope*x+off;
							if (z < side-originIndex && z >= -originIndex) {
								sourceValues[w+originIndex][x+originIndex][y+originIndex][z+originIndex] = value;
								resultValues[w+originIndex][x+originIndex][y+originIndex] = value;	
							}
						}
					}
				}			
				grid = new RegularIntGrid4D(sourceValues, -originIndex, -originIndex, -originIndex, -originIndex);
				diagonal = grid.diagonalCrossSectionOnXZ(slope == 1, off);
				compare(diagonal, new RegularIntGrid3D(resultValues, -originIndex, -originIndex, -originIndex));
				testBoundsConsistency(diagonal);
				testBoundsConsistency(pyramid1.diagonalCrossSectionOnXZ(slope == 1, off));
				testBoundsConsistency(pyramid2.diagonalCrossSectionOnXZ(slope == 1, off));
				testBoundsConsistency(pyramid3.diagonalCrossSectionOnXZ(slope == 1, off));
				testBoundsConsistency(pyramid4.diagonalCrossSectionOnXZ(slope == 1, off));
			}
		}
		//yz 
		for (int slope = 1; slope != -3; slope -= 2) {
			for (int off = -offset; off <= offset; off += offset) {
				sourceValues = new int[side][side][side][side];
				resultValues = new int[side][side][side];
				for (int w = -originIndex; w < side-originIndex; w++) {
					for (int y = -originIndex; y < side-originIndex; y++) {
						for (int x = -originIndex; x < side-originIndex; x++) {
							int value = random.nextInt();
							int z = slope*y+off;
							if (z < side-originIndex && z >= -originIndex) {
								sourceValues[w+originIndex][x+originIndex][y+originIndex][z+originIndex] = value;
								resultValues[w+originIndex][x+originIndex][y+originIndex] = value;	
							}
						}
					}
				}			
				grid = new RegularIntGrid4D(sourceValues, -originIndex, -originIndex, -originIndex, -originIndex);
				diagonal = grid.diagonalCrossSectionOnYZ(slope == 1, off);
				compare(diagonal, new RegularIntGrid3D(resultValues, -originIndex, -originIndex, -originIndex));
				testBoundsConsistency(diagonal);
				testBoundsConsistency(pyramid1.diagonalCrossSectionOnYZ(slope == 1, off));
				testBoundsConsistency(pyramid2.diagonalCrossSectionOnYZ(slope == 1, off));
				testBoundsConsistency(pyramid3.diagonalCrossSectionOnYZ(slope == 1, off));
				testBoundsConsistency(pyramid4.diagonalCrossSectionOnYZ(slope == 1, off));
			}
		}
	}
	
	public static void testDiagonals(int dimension) throws Exception {
		if (dimension > 1) {
			int offset = 5;
			Model[] pyramids = new Model[dimension];
			Coordinates originCoords = new Coordinates(new int[dimension]);
			for (int i = 0; i < dimension; i++) {
				pyramids[i] = new HypercubicPyramidGrid(originCoords, 13, i);
			}
			for (int firstAxis = 0; firstAxis < dimension; firstAxis++) {
				int secondAxis = 0;
				for (; secondAxis != firstAxis; secondAxis++) {
					for (int slope = 1; slope != -3; slope -= 2) {
						for (int off = -offset; off <= offset; off += offset) {
							for (int i = 0; i < dimension; i++) {
								testBoundsConsistency(pyramids[i].diagonalCrossSection(firstAxis, secondAxis, slope == 1, off));
							}
						}
					}
				}
				for (secondAxis++; secondAxis < dimension; secondAxis++) {
					for (int slope = 1; slope != -3; slope -= 2) {
						for (int off = -offset; off <= offset; off += offset) {
							for (int i = 0; i < dimension; i++) {
								testBoundsConsistency(pyramids[i].diagonalCrossSection(firstAxis, secondAxis, slope == 1, off));
							}
						}
					}
				}
			}
		}
	}
	
	public static void compare(IntModel2D grid, int[][] array, int xOffset, int yOffset) throws Exception {
		int maxX = grid.getMaxX();
		for (int x = grid.getMinX(); x <= maxX; x++) {
			int localMaxY = grid.getMaxY(x);
			for (int y = grid.getMinY(x); y <= localMaxY; y++) {
				if (grid.getFromPosition(x, y) != array[x + xOffset][y + yOffset]) {
					System.err.println("Different value at (" + x + ", " + y + "): " + grid.getFromPosition(x, y) + " != " + array[x + xOffset][y + yOffset]);
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
							System.err.println("Different value at (" + x + ", " + y + ", " + z + "): " 
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
		int side = 21;
		int originIndex = side/2;
		int offset = 5;
		ThreadLocalRandom random = ThreadLocalRandom.current();
		Model5D pyramid1 = new ModelAs5D<Model>(new HypercubicPyramidGrid(new Coordinates(0,0,0,0,0), 13, 0));
		Model5D pyramid2 = new ModelAs5D<Model>(new HypercubicPyramidGrid(new Coordinates(0,0,0,0,0), 13, 1));
		Model5D pyramid3 = new ModelAs5D<Model>(new HypercubicPyramidGrid(new Coordinates(0,0,0,0,0), 13, 2));
		Model5D pyramid4 = new ModelAs5D<Model>(new HypercubicPyramidGrid(new Coordinates(0,0,0,0,0), 13, 3));
		Model5D pyramid5 = new ModelAs5D<Model>(new HypercubicPyramidGrid(new Coordinates(0,0,0,0,0), 13, 4));
		//vw
		for (int slope = 1; slope != -3; slope -= 2) {
			for (int off = -offset; off <= offset; off += offset) {
				sourceValues = new int[side][side][side][side][side];
				resultValues = new int[side][side][side][side];
				for (int v = -originIndex; v < side-originIndex; v++) {
					for (int x = -originIndex; x < side-originIndex; x++) {
						for (int y = -originIndex; y < side-originIndex; y++) {
							for (int z = -originIndex; z < side-originIndex; z++) {
								int value = random.nextInt();
								int w = slope*v+off;
								if (w < side-originIndex && w >= -originIndex) {
									sourceValues[v+originIndex][w+originIndex][x+originIndex][y+originIndex][z+originIndex] = value;
									resultValues[v+originIndex][x+originIndex][y+originIndex][z+originIndex] = value;	
								}
							}
						}
					}
				}			
				grid = new RegularIntGrid5D(sourceValues, -originIndex, -originIndex, -originIndex, -originIndex, -originIndex);
				diagonal = grid.diagonalCrossSectionOnVW(slope == 1, off);
				compare(diagonal, new RegularIntGrid4D(resultValues, -originIndex, -originIndex, -originIndex, -originIndex));
				testBoundsConsistency(diagonal);
				testBoundsConsistency(pyramid1.diagonalCrossSectionOnVW(slope == 1, off));
				testBoundsConsistency(pyramid2.diagonalCrossSectionOnVW(slope == 1, off));
				testBoundsConsistency(pyramid3.diagonalCrossSectionOnVW(slope == 1, off));
				testBoundsConsistency(pyramid4.diagonalCrossSectionOnVW(slope == 1, off));
				testBoundsConsistency(pyramid5.diagonalCrossSectionOnVW(slope == 1, off));
			}
		}
		//vx
		for (int slope = 1; slope != -3; slope -= 2) {
			for (int off = -offset; off <= offset; off += offset) {
				sourceValues = new int[side][side][side][side][side];
				resultValues = new int[side][side][side][side];
				for (int v = -originIndex; v < side-originIndex; v++) {
					for (int w = -originIndex; w < side-originIndex; w++) {
						for (int y = -originIndex; y < side-originIndex; y++) {
							for (int z = -originIndex; z < side-originIndex; z++) {
								int value = random.nextInt();
								int x = slope*v+off;
								if (x < side-originIndex && x >= -originIndex) {
									sourceValues[v+originIndex][w+originIndex][x+originIndex][y+originIndex][z+originIndex] = value;
									resultValues[v+originIndex][w+originIndex][y+originIndex][z+originIndex] = value;	
								}
							}
						}
					}
				}			
				grid = new RegularIntGrid5D(sourceValues, -originIndex, -originIndex, -originIndex, -originIndex, -originIndex);
				diagonal = grid.diagonalCrossSectionOnVX(slope == 1, off);
				compare(diagonal, new RegularIntGrid4D(resultValues, -originIndex, -originIndex, -originIndex, -originIndex));
				testBoundsConsistency(diagonal);
				testBoundsConsistency(pyramid1.diagonalCrossSectionOnVX(slope == 1, off));
				testBoundsConsistency(pyramid2.diagonalCrossSectionOnVX(slope == 1, off));
				testBoundsConsistency(pyramid3.diagonalCrossSectionOnVX(slope == 1, off));
				testBoundsConsistency(pyramid4.diagonalCrossSectionOnVX(slope == 1, off));
				testBoundsConsistency(pyramid5.diagonalCrossSectionOnVX(slope == 1, off));
			}
		}
		//vy
		for (int slope = 1; slope != -3; slope -= 2) {
			for (int off = -offset; off <= offset; off += offset) {
				sourceValues = new int[side][side][side][side][side];
				resultValues = new int[side][side][side][side];
				for (int v = -originIndex; v < side-originIndex; v++) {
					for (int w = -originIndex; w < side-originIndex; w++) {
						for (int x = -originIndex; x < side-originIndex; x++) {
							for (int z = -originIndex; z < side-originIndex; z++) {
								int value = random.nextInt();
								int y = slope*v+off;
								if (y < side-originIndex && y >= -originIndex) {
									sourceValues[v+originIndex][w+originIndex][x+originIndex][y+originIndex][z+originIndex] = value;
									resultValues[v+originIndex][w+originIndex][x+originIndex][z+originIndex] = value;	
								}
							}
						}
					}
				}			
				grid = new RegularIntGrid5D(sourceValues, -originIndex, -originIndex, -originIndex, -originIndex, -originIndex);
				diagonal = grid.diagonalCrossSectionOnVY(slope == 1, off);
				compare(diagonal, new RegularIntGrid4D(resultValues, -originIndex, -originIndex, -originIndex, -originIndex));
				testBoundsConsistency(diagonal);
				testBoundsConsistency(pyramid1.diagonalCrossSectionOnVY(slope == 1, off));
				testBoundsConsistency(pyramid2.diagonalCrossSectionOnVY(slope == 1, off));
				testBoundsConsistency(pyramid3.diagonalCrossSectionOnVY(slope == 1, off));
				testBoundsConsistency(pyramid4.diagonalCrossSectionOnVY(slope == 1, off));
				testBoundsConsistency(pyramid5.diagonalCrossSectionOnVY(slope == 1, off));
			}
		}
		//vz
		for (int slope = 1; slope != -3; slope -= 2) {
			for (int off = -offset; off <= offset; off += offset) {
				sourceValues = new int[side][side][side][side][side];
				resultValues = new int[side][side][side][side];
				for (int v = -originIndex; v < side-originIndex; v++) {
					for (int w = -originIndex; w < side-originIndex; w++) {
						for (int x = -originIndex; x < side-originIndex; x++) {
							for (int y = -originIndex; y < side-originIndex; y++) {
								int value = random.nextInt();
								int z = slope*v+off;
								if (z < side-originIndex && z >= -originIndex) {
									sourceValues[v+originIndex][w+originIndex][x+originIndex][y+originIndex][z+originIndex] = value;
									resultValues[v+originIndex][w+originIndex][x+originIndex][y+originIndex] = value;	
								}
							}
						}
					}
				}			
				grid = new RegularIntGrid5D(sourceValues, -originIndex, -originIndex, -originIndex, -originIndex, -originIndex);
				diagonal = grid.diagonalCrossSectionOnVZ(slope == 1, off);
				compare(diagonal, new RegularIntGrid4D(resultValues, -originIndex, -originIndex, -originIndex, -originIndex));
				testBoundsConsistency(diagonal);
				testBoundsConsistency(pyramid1.diagonalCrossSectionOnVZ(slope == 1, off));
				testBoundsConsistency(pyramid2.diagonalCrossSectionOnVZ(slope == 1, off));
				testBoundsConsistency(pyramid3.diagonalCrossSectionOnVZ(slope == 1, off));
				testBoundsConsistency(pyramid4.diagonalCrossSectionOnVZ(slope == 1, off));
				testBoundsConsistency(pyramid5.diagonalCrossSectionOnVZ(slope == 1, off));
			}
		}
		//wx
		for (int slope = 1; slope != -3; slope -= 2) {
			for (int off = -offset; off <= offset; off += offset) {
				sourceValues = new int[side][side][side][side][side];
				resultValues = new int[side][side][side][side];
				for (int v = -originIndex; v < side-originIndex; v++) {
					for (int w = -originIndex; w < side-originIndex; w++) {
						for (int y = -originIndex; y < side-originIndex; y++) {
							for (int z = -originIndex; z < side-originIndex; z++) {
								int value = random.nextInt();
								int x = slope*w+off;
								if (x < side-originIndex && x >= -originIndex) {
									sourceValues[v+originIndex][w+originIndex][x+originIndex][y+originIndex][z+originIndex] = value;
									resultValues[v+originIndex][w+originIndex][y+originIndex][z+originIndex] = value;	
								}
							}
						}
					}
				}			
				grid = new RegularIntGrid5D(sourceValues, -originIndex, -originIndex, -originIndex, -originIndex, -originIndex);
				diagonal = grid.diagonalCrossSectionOnWX(slope == 1, off);
				compare(diagonal, new RegularIntGrid4D(resultValues, -originIndex, -originIndex, -originIndex, -originIndex));
				testBoundsConsistency(diagonal);
				testBoundsConsistency(pyramid1.diagonalCrossSectionOnWX(slope == 1, off));
				testBoundsConsistency(pyramid2.diagonalCrossSectionOnWX(slope == 1, off));
				testBoundsConsistency(pyramid3.diagonalCrossSectionOnWX(slope == 1, off));
				testBoundsConsistency(pyramid4.diagonalCrossSectionOnWX(slope == 1, off));
				testBoundsConsistency(pyramid5.diagonalCrossSectionOnWX(slope == 1, off));
			}
		}
		//wy
		for (int slope = 1; slope != -3; slope -= 2) {
			for (int off = -offset; off <= offset; off += offset) {
				sourceValues = new int[side][side][side][side][side];
				resultValues = new int[side][side][side][side];
				for (int v = -originIndex; v < side-originIndex; v++) {
					for (int w = -originIndex; w < side-originIndex; w++) {
						for (int x = -originIndex; x < side-originIndex; x++) {
							for (int z = -originIndex; z < side-originIndex; z++) {
								int value = random.nextInt();
								int y = slope*w+off;
								if (y < side-originIndex && y >= -originIndex) {
									sourceValues[v+originIndex][w+originIndex][x+originIndex][y+originIndex][z+originIndex] = value;
									resultValues[v+originIndex][w+originIndex][x+originIndex][z+originIndex] = value;	
								}
							}
						}
					}
				}			
				grid = new RegularIntGrid5D(sourceValues, -originIndex, -originIndex, -originIndex, -originIndex, -originIndex);
				diagonal = grid.diagonalCrossSectionOnWY(slope == 1, off);
				compare(diagonal, new RegularIntGrid4D(resultValues, -originIndex, -originIndex, -originIndex, -originIndex));
				testBoundsConsistency(diagonal);
				testBoundsConsistency(pyramid1.diagonalCrossSectionOnWY(slope == 1, off));
				testBoundsConsistency(pyramid2.diagonalCrossSectionOnWY(slope == 1, off));
				testBoundsConsistency(pyramid3.diagonalCrossSectionOnWY(slope == 1, off));
				testBoundsConsistency(pyramid4.diagonalCrossSectionOnWY(slope == 1, off));
				testBoundsConsistency(pyramid5.diagonalCrossSectionOnWY(slope == 1, off));
			}
		}
		//wz
		for (int slope = 1; slope != -3; slope -= 2) {
			for (int off = -offset; off <= offset; off += offset) {
				sourceValues = new int[side][side][side][side][side];
				resultValues = new int[side][side][side][side];
				for (int v = -originIndex; v < side-originIndex; v++) {
					for (int w = -originIndex; w < side-originIndex; w++) {
						for (int x = -originIndex; x < side-originIndex; x++) {
							for (int y = -originIndex; y < side-originIndex; y++) {
								int value = random.nextInt();
								int z = slope*w+off;
								if (z < side-originIndex && z >= -originIndex) {
									sourceValues[v+originIndex][w+originIndex][x+originIndex][y+originIndex][z+originIndex] = value;
									resultValues[v+originIndex][w+originIndex][x+originIndex][y+originIndex] = value;	
								}
							}
						}
					}
				}			
				grid = new RegularIntGrid5D(sourceValues, -originIndex, -originIndex, -originIndex, -originIndex, -originIndex);
				diagonal = grid.diagonalCrossSectionOnWZ(slope == 1, off);
				compare(diagonal, new RegularIntGrid4D(resultValues, -originIndex, -originIndex, -originIndex, -originIndex));
				testBoundsConsistency(diagonal);
				testBoundsConsistency(pyramid1.diagonalCrossSectionOnWZ(slope == 1, off));
				testBoundsConsistency(pyramid2.diagonalCrossSectionOnWZ(slope == 1, off));
				testBoundsConsistency(pyramid3.diagonalCrossSectionOnWZ(slope == 1, off));
				testBoundsConsistency(pyramid4.diagonalCrossSectionOnWZ(slope == 1, off));
				testBoundsConsistency(pyramid5.diagonalCrossSectionOnWZ(slope == 1, off));
			}
		}
		//xy
		for (int slope = 1; slope != -3; slope -= 2) {
			for (int off = -offset; off <= offset; off += offset) {
				sourceValues = new int[side][side][side][side][side];
				resultValues = new int[side][side][side][side];
				for (int v = -originIndex; v < side-originIndex; v++) {
					for (int w = -originIndex; w < side-originIndex; w++) {
						for (int x = -originIndex; x < side-originIndex; x++) {
							for (int z = -originIndex; z < side-originIndex; z++) {
								int value = random.nextInt();
								int y = slope*x+off;
								if (y < side-originIndex && y >= -originIndex) {
									sourceValues[v+originIndex][w+originIndex][x+originIndex][y+originIndex][z+originIndex] = value;
									resultValues[v+originIndex][w+originIndex][x+originIndex][z+originIndex] = value;	
								}
							}
						}
					}
				}			
				grid = new RegularIntGrid5D(sourceValues, -originIndex, -originIndex, -originIndex, -originIndex, -originIndex);
				diagonal = grid.diagonalCrossSectionOnXY(slope == 1, off);
				compare(diagonal, new RegularIntGrid4D(resultValues, -originIndex, -originIndex, -originIndex, -originIndex));
				testBoundsConsistency(diagonal);
				testBoundsConsistency(pyramid1.diagonalCrossSectionOnXY(slope == 1, off));
				testBoundsConsistency(pyramid2.diagonalCrossSectionOnXY(slope == 1, off));
				testBoundsConsistency(pyramid3.diagonalCrossSectionOnXY(slope == 1, off));
				testBoundsConsistency(pyramid4.diagonalCrossSectionOnXY(slope == 1, off));
				testBoundsConsistency(pyramid5.diagonalCrossSectionOnXY(slope == 1, off));
			}
		}
		//xz
		for (int slope = 1; slope != -3; slope -= 2) {
			for (int off = -offset; off <= offset; off += offset) {
				sourceValues = new int[side][side][side][side][side];
				resultValues = new int[side][side][side][side];
				for (int v = -originIndex; v < side-originIndex; v++) {
					for (int w = -originIndex; w < side-originIndex; w++) {
						for (int x = -originIndex; x < side-originIndex; x++) {
							for (int y = -originIndex; y < side-originIndex; y++) {
								int value = random.nextInt();
								int z = slope*x+off;
								if (z < side-originIndex && z >= -originIndex) {
									sourceValues[v+originIndex][w+originIndex][x+originIndex][y+originIndex][z+originIndex] = value;
									resultValues[v+originIndex][w+originIndex][x+originIndex][y+originIndex] = value;	
								}
							}
						}
					}
				}			
				grid = new RegularIntGrid5D(sourceValues, -originIndex, -originIndex, -originIndex, -originIndex, -originIndex);
				diagonal = grid.diagonalCrossSectionOnXZ(slope == 1, off);
				compare(diagonal, new RegularIntGrid4D(resultValues, -originIndex, -originIndex, -originIndex, -originIndex));
				testBoundsConsistency(diagonal);
				testBoundsConsistency(pyramid1.diagonalCrossSectionOnXZ(slope == 1, off));
				testBoundsConsistency(pyramid2.diagonalCrossSectionOnXZ(slope == 1, off));
				testBoundsConsistency(pyramid3.diagonalCrossSectionOnXZ(slope == 1, off));
				testBoundsConsistency(pyramid4.diagonalCrossSectionOnXZ(slope == 1, off));
				testBoundsConsistency(pyramid5.diagonalCrossSectionOnXZ(slope == 1, off));
			}
		}
		//yz
		for (int slope = 1; slope != -3; slope -= 2) {
			for (int off = -offset; off <= offset; off += offset) {
				sourceValues = new int[side][side][side][side][side];
				resultValues = new int[side][side][side][side];
				for (int v = -originIndex; v < side-originIndex; v++) {
					for (int w = -originIndex; w < side-originIndex; w++) {
						for (int x = -originIndex; x < side-originIndex; x++) {
							for (int y = -originIndex; y < side-originIndex; y++) {
								int value = random.nextInt();
								int z = slope*y+off;
								if (z < side-originIndex && z >= -originIndex) {
									sourceValues[v+originIndex][w+originIndex][x+originIndex][y+originIndex][z+originIndex] = value;
									resultValues[v+originIndex][w+originIndex][x+originIndex][y+originIndex] = value;	
								}
							}
						}
					}
				}			
				grid = new RegularIntGrid5D(sourceValues, -originIndex, -originIndex, -originIndex, -originIndex, -originIndex);
				diagonal = grid.diagonalCrossSectionOnYZ(slope == 1, off);
				compare(diagonal, new RegularIntGrid4D(resultValues, -originIndex, -originIndex, -originIndex, -originIndex));
				testBoundsConsistency(diagonal);
				testBoundsConsistency(pyramid1.diagonalCrossSectionOnYZ(slope == 1, off));
				testBoundsConsistency(pyramid2.diagonalCrossSectionOnYZ(slope == 1, off));
				testBoundsConsistency(pyramid3.diagonalCrossSectionOnYZ(slope == 1, off));
				testBoundsConsistency(pyramid4.diagonalCrossSectionOnYZ(slope == 1, off));
				testBoundsConsistency(pyramid5.diagonalCrossSectionOnYZ(slope == 1, off));
			}
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
								System.err.println("Different value at (" + w + ", " + x + ", " + y + ", " + z + "): " 
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
		SimpleAether5D ae = new SimpleAether5D(initialValue);
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
	
	public static void compareAllStepsWithOffset(LongModel3D ca1, LongModel3D ca2, int xOffset, int yOffset, int zOffset) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (equal && !finished1 && !finished2) {
//				System.out.println("Comparing step " + ca1.getStep());
				for (int z = ca1.getMinZ(), z2 = z + zOffset; z <= ca1.getMaxZ(); z++, z2++) {
					for (int y = ca1.getMinYAtZ(z), y2 = y + yOffset; y <= ca1.getMaxYAtZ(z); y++, y2++) {
						for (int x = ca1.getMinX(y,z), x2 = x + xOffset; x <= ca1.getMaxX(y,z); x++, x2++) {
							if (ca1.getFromPosition(x, y, z) != ca2.getFromPosition(x2, y2, z2)) {
								equal = false;
								System.err.println("Different value at step " + ca1.getStep() + ": " 
										+ ca1.getClass().getSimpleName() + "(" + x + ", " + y + ", " + z + "):" + ca1.getFromPosition(x, y, z) 
										+ " != " + ca2.getClass().getSimpleName() + "(" + x2 + ", " + y2 + ", " + z2 + "):" + ca2.getFromPosition(x2, y2, z2));
							}
						}	
					}	
				}
				Boolean changed;
				finished1 = (changed = ca1.nextStep()) != null && !changed;
				finished2 = (changed = ca2.nextStep()) != null && !changed;
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.err.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
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
			Boolean changed;
			do {
				System.out.println("Comparing step " + ae.getStep());
				int maxW = ae.getAsymmetricMaxW();
				for (int w = 0; w <= maxW; w++) {
					for (int x = 0; x <= w; x++) {
						for (int y = 0; y <= x; y++) {
							for (int z = 0; z <= y; z++) {
//								System.out.println("Comparing value at (" + w + ", " + x + ", " + y + ", " + z + ")");
								if (ae.getFromAsymmetricPosition(w, x, y, z) != ns.getFromPosition(w, x, y, z)) {
									System.err.println("Different value");
									return;
								}
							}
						}
					}
				}
			} while ((changed = ae.nextStep()) == null || changed);
			System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void sivTesting() {
		long initialValue = 100000;
		SpreadIntegerValue2D ae1 = new SpreadIntegerValue2D(initialValue, 0);
		SimpleSpreadIntegerValue2D ae2 = new SimpleSpreadIntegerValue2D(initialValue, 0);
		compareAllSteps(ae1, ae2);
	}
	
	public static void compareAllSteps(IntModel ca1, IntModel ca2) {
		System.out.println("Comparing...");
		int dimension = ca1.getGridDimension();
		int dimension2 = ca2.getGridDimension();
		if (dimension == dimension2) {
			try {
				boolean finished1 = false;
				boolean finished2 = false;
				boolean equal = true;
				int[] coordinates = new int[dimension];
				while (equal && !finished1 && !finished2) {
					if (dimension == 0) {
						Coordinates coordinatesObj = new Coordinates(coordinates);
						int val1 = ca1.getFromPosition(coordinatesObj);
						int val2 = ca2.getFromPosition(coordinatesObj);
						if (val1 != val2) {
							equal = false;
							System.err.println("Different value at step " + ca1.getStep() + " " + coordinatesObj + ": " 
									+ ca1.getClass().getSimpleName() + ":" + val1 
									+ " != " + ca2.getClass().getSimpleName() + ":" + val2);
						}
					} else {
						Integer[] partialCoordinates = new Integer[dimension];
						int[] maxCoords = new int[dimension];
						int currentAxis = dimension - 1;
						boolean isBeginningOfLoop = true;
						while (currentAxis < dimension) {
							if (currentAxis == 0) {
								PartialCoordinates partialCoordinatesObj = new PartialCoordinates(partialCoordinates);
								//ignore possible differences in bounds
								int minCoord = Math.max(ca1.getMinCoordinate(0, partialCoordinatesObj), ca2.getMinCoordinate(0, partialCoordinatesObj));
								int maxCoord = Math.min(ca1.getMaxCoordinate(0, partialCoordinatesObj), ca2.getMaxCoordinate(0, partialCoordinatesObj));
								for (int currentCoordinate = minCoord; currentCoordinate != maxCoord; currentCoordinate++) {
									coordinates[0] = currentCoordinate;
									Coordinates coordinatesObj = new Coordinates(coordinates);
									int val1 = ca1.getFromPosition(coordinatesObj);
									int val2 = ca2.getFromPosition(coordinatesObj);
									if (val1 != val2) {
										equal = false;
										System.err.println("Different value at step " + ca1.getStep() + " " + coordinatesObj + ": " 
												+ ca1.getClass().getSimpleName() + ":" + val1 
												+ " != " + ca2.getClass().getSimpleName() + ":" + val2);
									}
								}
								//to prevent infinite loop if maxCoord is equal to Integer.MAX_VALUE
								coordinates[0] = maxCoord;
								Coordinates coordinatesObj = new Coordinates(coordinates);
								int val1 = ca1.getFromPosition(coordinatesObj);
								int val2 = ca2.getFromPosition(coordinatesObj);
								if (val1 != val2) {
									equal = false;
									System.err.println("Different value at step " + ca1.getStep() + " " + coordinatesObj + ": " 
											+ ca1.getClass().getSimpleName() + ":" + val1 
											+ " != " + ca2.getClass().getSimpleName() + ":" + val2);
								}
								isBeginningOfLoop = false;
								currentAxis++;
							} else if (isBeginningOfLoop) {
								PartialCoordinates partialCoordinatesObj = new PartialCoordinates(partialCoordinates);
								int localMinCoord = Math.max(ca1.getMinCoordinate(0, partialCoordinatesObj), ca2.getMinCoordinate(0, partialCoordinatesObj));
								maxCoords[currentAxis] = Math.min(ca1.getMaxCoordinate(0, partialCoordinatesObj), ca2.getMaxCoordinate(0, partialCoordinatesObj));
								coordinates[currentAxis] = localMinCoord;
								partialCoordinates[currentAxis] = localMinCoord;
								currentAxis--;
							} else {
								int currentCoordinate = coordinates[currentAxis];
								if (currentCoordinate < maxCoords[currentAxis]) {
									isBeginningOfLoop = true;
									currentCoordinate++;
									coordinates[currentAxis] = currentCoordinate;
									partialCoordinates[currentAxis] = currentCoordinate;
									currentAxis--;
								} else {
									partialCoordinates[currentAxis] = null;
									currentAxis++;
								}
							}
						}
					}
					Boolean changed;
					finished1 = (changed = ca1.nextStep()) != null && !changed;
					finished2 = (changed = ca2.nextStep()) != null && !changed;
					if (finished1 != finished2) {
						equal = false;
						String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
						System.err.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
					}
				}
				if (equal)
					System.out.println("Equal!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("Different grid dimension: " 
					+ ca1.getClass().getSimpleName() + ":" + dimension 
					+ " != " + ca2.getClass().getSimpleName() + ":" + dimension2);
		}
	}
	
	public static void compareAllSteps(LongModel ca1, LongModel ca2) {
		System.out.println("Comparing...");
		int dimension = ca1.getGridDimension();
		int dimension2 = ca2.getGridDimension();
		if (dimension == dimension2) {
			try {
				boolean finished1 = false;
				boolean finished2 = false;
				boolean equal = true;
				int[] coordinates = new int[dimension];
				while (equal && !finished1 && !finished2) {
					if (dimension == 0) {
						Coordinates coordinatesObj = new Coordinates(coordinates);
						long val1 = ca1.getFromPosition(coordinatesObj);
						long val2 = ca2.getFromPosition(coordinatesObj);
						if (val1 != val2) {
							equal = false;
							System.err.println("Different value at step " + ca1.getStep() + " " + coordinatesObj + ": " 
									+ ca1.getClass().getSimpleName() + ":" + val1 
									+ " != " + ca2.getClass().getSimpleName() + ":" + val2);
						}
					} else {
						Integer[] partialCoordinates = new Integer[dimension];
						int[] maxCoords = new int[dimension];
						int currentAxis = dimension - 1;
						boolean isBeginningOfLoop = true;
						while (currentAxis < dimension) {
							if (currentAxis == 0) {
								PartialCoordinates partialCoordinatesObj = new PartialCoordinates(partialCoordinates);
								//ignore possible differences in bounds
								int minCoord = Math.max(ca1.getMinCoordinate(0, partialCoordinatesObj), ca2.getMinCoordinate(0, partialCoordinatesObj));
								int maxCoord = Math.min(ca1.getMaxCoordinate(0, partialCoordinatesObj), ca2.getMaxCoordinate(0, partialCoordinatesObj));
								for (int currentCoordinate = minCoord; currentCoordinate != maxCoord; currentCoordinate++) {
									coordinates[0] = currentCoordinate;
									Coordinates coordinatesObj = new Coordinates(coordinates);
									long val1 = ca1.getFromPosition(coordinatesObj);
									long val2 = ca2.getFromPosition(coordinatesObj);
									if (val1 != val2) {
										equal = false;
										System.err.println("Different value at step " + ca1.getStep() + " " + coordinatesObj + ": " 
												+ ca1.getClass().getSimpleName() + ":" + val1 
												+ " != " + ca2.getClass().getSimpleName() + ":" + val2);
									}
								}
								//to prevent infinite loop if maxCoord is equal to Integer.MAX_VALUE
								coordinates[0] = maxCoord;
								Coordinates coordinatesObj = new Coordinates(coordinates);
								long val1 = ca1.getFromPosition(coordinatesObj);
								long val2 = ca2.getFromPosition(coordinatesObj);
								if (val1 != val2) {
									equal = false;
									System.err.println("Different value at step " + ca1.getStep() + " " + coordinatesObj + ": " 
											+ ca1.getClass().getSimpleName() + ":" + val1 
											+ " != " + ca2.getClass().getSimpleName() + ":" + val2);
								}
								isBeginningOfLoop = false;
								currentAxis++;
							} else if (isBeginningOfLoop) {
								PartialCoordinates partialCoordinatesObj = new PartialCoordinates(partialCoordinates);
								int localMinCoord = Math.max(ca1.getMinCoordinate(0, partialCoordinatesObj), ca2.getMinCoordinate(0, partialCoordinatesObj));
								maxCoords[currentAxis] = Math.min(ca1.getMaxCoordinate(0, partialCoordinatesObj), ca2.getMaxCoordinate(0, partialCoordinatesObj));
								coordinates[currentAxis] = localMinCoord;
								partialCoordinates[currentAxis] = localMinCoord;
								currentAxis--;
							} else {
								int currentCoordinate = coordinates[currentAxis];
								if (currentCoordinate < maxCoords[currentAxis]) {
									isBeginningOfLoop = true;
									currentCoordinate++;
									coordinates[currentAxis] = currentCoordinate;
									partialCoordinates[currentAxis] = currentCoordinate;
									currentAxis--;
								} else {
									partialCoordinates[currentAxis] = null;
									currentAxis++;
								}
							}
						}
					}
					Boolean changed;
					finished1 = (changed = ca1.nextStep()) != null && !changed;
					finished2 = (changed = ca2.nextStep()) != null && !changed;
					if (finished1 != finished2) {
						equal = false;
						String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
						System.err.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
					}
				}
				if (equal)
					System.out.println("Equal!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("Different grid dimension: " 
					+ ca1.getClass().getSimpleName() + ":" + dimension 
					+ " != " + ca2.getClass().getSimpleName() + ":" + dimension2);
		}
	}
	
	public static <Object_Type> void compareAllSteps(ObjectModel<Object_Type> ca1, ObjectModel<Object_Type> ca2) {
		System.out.println("Comparing...");
		int dimension = ca1.getGridDimension();
		int dimension2 = ca2.getGridDimension();
		if (dimension == dimension2) {
			try {
				boolean finished1 = false;
				boolean finished2 = false;
				boolean equal = true;
				int[] coordinates = new int[dimension];
				while (equal && !finished1 && !finished2) {
					if (dimension == 0) {
						Coordinates coordinatesObj = new Coordinates(coordinates);
						Object_Type val1 = ca1.getFromPosition(coordinatesObj);
						Object_Type val2 = ca2.getFromPosition(coordinatesObj);
						if (!val1.equals(val2)) {
							equal = false;
							System.err.println("Different value at step " + ca1.getStep() + " " + coordinatesObj + ": " 
									+ ca1.getClass().getSimpleName() + ":" + val1 
									+ " != " + ca2.getClass().getSimpleName() + ":" + val2);
						}
					} else {
						Integer[] partialCoordinates = new Integer[dimension];
						int[] maxCoords = new int[dimension];
						int currentAxis = dimension - 1;
						boolean isBeginningOfLoop = true;
						while (currentAxis < dimension) {
							if (currentAxis == 0) {
								PartialCoordinates partialCoordinatesObj = new PartialCoordinates(partialCoordinates);
								//ignore possible differences in bounds
								int minCoord = Math.max(ca1.getMinCoordinate(0, partialCoordinatesObj), ca2.getMinCoordinate(0, partialCoordinatesObj));
								int maxCoord = Math.min(ca1.getMaxCoordinate(0, partialCoordinatesObj), ca2.getMaxCoordinate(0, partialCoordinatesObj));
								for (int currentCoordinate = minCoord; currentCoordinate != maxCoord; currentCoordinate++) {
									coordinates[0] = currentCoordinate;
									Coordinates coordinatesObj = new Coordinates(coordinates);
									Object_Type val1 = ca1.getFromPosition(coordinatesObj);
									Object_Type val2 = ca2.getFromPosition(coordinatesObj);
									if (!val1.equals(val2)) {
										equal = false;
										System.err.println("Different value at step " + ca1.getStep() + " " + coordinatesObj + ": " 
												+ ca1.getClass().getSimpleName() + ":" + val1 
												+ " != " + ca2.getClass().getSimpleName() + ":" + val2);
									}
								}
								//to prevent infinite loop if maxCoord is equal to Integer.MAX_VALUE
								coordinates[0] = maxCoord;
								Coordinates coordinatesObj = new Coordinates(coordinates);
								Object_Type val1 = ca1.getFromPosition(coordinatesObj);
								Object_Type val2 = ca2.getFromPosition(coordinatesObj);
								if (!val1.equals(val2)) {
									equal = false;
									System.err.println("Different value at step " + ca1.getStep() + " " + coordinatesObj + ": " 
											+ ca1.getClass().getSimpleName() + ":" + val1 
											+ " != " + ca2.getClass().getSimpleName() + ":" + val2);
								}
								isBeginningOfLoop = false;
								currentAxis++;
							} else if (isBeginningOfLoop) {
								PartialCoordinates partialCoordinatesObj = new PartialCoordinates(partialCoordinates);
								int localMinCoord = Math.max(ca1.getMinCoordinate(0, partialCoordinatesObj), ca2.getMinCoordinate(0, partialCoordinatesObj));
								maxCoords[currentAxis] = Math.min(ca1.getMaxCoordinate(0, partialCoordinatesObj), ca2.getMaxCoordinate(0, partialCoordinatesObj));
								coordinates[currentAxis] = localMinCoord;
								partialCoordinates[currentAxis] = localMinCoord;
								currentAxis--;
							} else {
								int currentCoordinate = coordinates[currentAxis];
								if (currentCoordinate < maxCoords[currentAxis]) {
									isBeginningOfLoop = true;
									currentCoordinate++;
									coordinates[currentAxis] = currentCoordinate;
									partialCoordinates[currentAxis] = currentCoordinate;
									currentAxis--;
								} else {
									partialCoordinates[currentAxis] = null;
									currentAxis++;
								}
							}
						}
					}
					Boolean changed;
					finished1 = (changed = ca1.nextStep()) != null && !changed;
					finished2 = (changed = ca2.nextStep()) != null && !changed;
					if (finished1 != finished2) {
						equal = false;
						String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
						System.err.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
					}
				}
				if (equal)
					System.out.println("Equal!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("Different grid dimension: " 
					+ ca1.getClass().getSimpleName() + ":" + dimension 
					+ " != " + ca2.getClass().getSimpleName() + ":" + dimension2);
		}
	}
	
	public static void compareAllSteps(LongModel ca1, IntModel ca2) {
		compareAllSteps(ca2, ca1);
	}
	
	public static void compareAllSteps(IntModel ca1, LongModel ca2) {
		System.out.println("Comparing...");
		int dimension = ca1.getGridDimension();
		int dimension2 = ca2.getGridDimension();
		if (dimension == dimension2) {
			try {
				boolean finished1 = false;
				boolean finished2 = false;
				boolean equal = true;
				int[] coordinates = new int[dimension];
				while (equal && !finished1 && !finished2) {
					if (dimension == 0) {
						Coordinates coordinatesObj = new Coordinates(coordinates);
						int val1 = ca1.getFromPosition(coordinatesObj);
						long val2 = ca2.getFromPosition(coordinatesObj);
						if (val1 != val2) {
							equal = false;
							System.err.println("Different value at step " + ca1.getStep() + " " + coordinatesObj + ": " 
									+ ca1.getClass().getSimpleName() + ":" + val1 
									+ " != " + ca2.getClass().getSimpleName() + ":" + val2);
						}
					} else {
						Integer[] partialCoordinates = new Integer[dimension];
						int[] maxCoords = new int[dimension];
						int currentAxis = dimension - 1;
						boolean isBeginningOfLoop = true;
						while (currentAxis < dimension) {
							if (currentAxis == 0) {
								PartialCoordinates partialCoordinatesObj = new PartialCoordinates(partialCoordinates);
								//ignore possible differences in bounds
								int minCoord = Math.max(ca1.getMinCoordinate(0, partialCoordinatesObj), ca2.getMinCoordinate(0, partialCoordinatesObj));
								int maxCoord = Math.min(ca1.getMaxCoordinate(0, partialCoordinatesObj), ca2.getMaxCoordinate(0, partialCoordinatesObj));
								for (int currentCoordinate = minCoord; currentCoordinate != maxCoord; currentCoordinate++) {
									coordinates[0] = currentCoordinate;
									Coordinates coordinatesObj = new Coordinates(coordinates);
									int val1 = ca1.getFromPosition(coordinatesObj);
									long val2 = ca2.getFromPosition(coordinatesObj);
									if (val1 != val2) {
										equal = false;
										System.err.println("Different value at step " + ca1.getStep() + " " + coordinatesObj + ": " 
												+ ca1.getClass().getSimpleName() + ":" + val1 
												+ " != " + ca2.getClass().getSimpleName() + ":" + val2);
									}
								}
								//to prevent infinite loop if maxCoord is equal to Integer.MAX_VALUE
								coordinates[0] = maxCoord;
								Coordinates coordinatesObj = new Coordinates(coordinates);
								int val1 = ca1.getFromPosition(coordinatesObj);
								long val2 = ca2.getFromPosition(coordinatesObj);
								if (val1 != val2) {
									equal = false;
									System.err.println("Different value at step " + ca1.getStep() + " " + coordinatesObj + ": " 
											+ ca1.getClass().getSimpleName() + ":" + val1 
											+ " != " + ca2.getClass().getSimpleName() + ":" + val2);
								}
								isBeginningOfLoop = false;
								currentAxis++;
							} else if (isBeginningOfLoop) {
								PartialCoordinates partialCoordinatesObj = new PartialCoordinates(partialCoordinates);
								int localMinCoord = Math.max(ca1.getMinCoordinate(0, partialCoordinatesObj), ca2.getMinCoordinate(0, partialCoordinatesObj));
								maxCoords[currentAxis] = Math.min(ca1.getMaxCoordinate(0, partialCoordinatesObj), ca2.getMaxCoordinate(0, partialCoordinatesObj));
								coordinates[currentAxis] = localMinCoord;
								partialCoordinates[currentAxis] = localMinCoord;
								currentAxis--;
							} else {
								int currentCoordinate = coordinates[currentAxis];
								if (currentCoordinate < maxCoords[currentAxis]) {
									isBeginningOfLoop = true;
									currentCoordinate++;
									coordinates[currentAxis] = currentCoordinate;
									partialCoordinates[currentAxis] = currentCoordinate;
									currentAxis--;
								} else {
									partialCoordinates[currentAxis] = null;
									currentAxis++;
								}
							}
						}
					}
					Boolean changed;
					finished1 = (changed = ca1.nextStep()) != null && !changed;
					finished2 = (changed = ca2.nextStep()) != null && !changed;
					if (finished1 != finished2) {
						equal = false;
						String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
						System.err.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
					}
				}
				if (equal)
					System.out.println("Equal!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("Different grid dimension: " 
					+ ca1.getClass().getSimpleName() + ":" + dimension 
					+ " != " + ca2.getClass().getSimpleName() + ":" + dimension2);
		}
	}
	
	public static <Number_Type extends Number & FieldElement<Number_Type> & Comparable<Number_Type>> void compareAllSteps(LongModel ca1, NumericModel<Number_Type> ca2) {
		compareAllSteps(ca2, ca1);
	}
	
	public static <Number_Type extends Number & FieldElement<Number_Type> & Comparable<Number_Type>> void compareAllSteps(NumericModel<Number_Type> ca1, LongModel ca2) {
		System.out.println("Comparing...");
		int dimension = ca1.getGridDimension();
		int dimension2 = ca2.getGridDimension();
		if (dimension == dimension2) {
			try {
				boolean finished1 = false;
				boolean finished2 = false;
				boolean equal = true;
				int[] coordinates = new int[dimension];
				while (equal && !finished1 && !finished2) {
					if (dimension == 0) {
						Coordinates coordinatesObj = new Coordinates(coordinates);
						long val1 = ca1.getFromPosition(coordinatesObj).longValue();
						long val2 = ca2.getFromPosition(coordinatesObj);
						if (val1 != val2) {
							equal = false;
							System.err.println("Different value at step " + ca1.getStep() + " " + coordinatesObj + ": " 
									+ ca1.getClass().getSimpleName() + ":" + val1 
									+ " != " + ca2.getClass().getSimpleName() + ":" + val2);
						}
					} else {
						Integer[] partialCoordinates = new Integer[dimension];
						int[] maxCoords = new int[dimension];
						int currentAxis = dimension - 1;
						boolean isBeginningOfLoop = true;
						while (currentAxis < dimension) {
							if (currentAxis == 0) {
								PartialCoordinates partialCoordinatesObj = new PartialCoordinates(partialCoordinates);
								//ignore possible differences in bounds
								int minCoord = Math.max(ca1.getMinCoordinate(0, partialCoordinatesObj), ca2.getMinCoordinate(0, partialCoordinatesObj));
								int maxCoord = Math.min(ca1.getMaxCoordinate(0, partialCoordinatesObj), ca2.getMaxCoordinate(0, partialCoordinatesObj));
								for (int currentCoordinate = minCoord; currentCoordinate != maxCoord; currentCoordinate++) {
									coordinates[0] = currentCoordinate;
									Coordinates coordinatesObj = new Coordinates(coordinates);
									long val1 = ca1.getFromPosition(coordinatesObj).longValue();
									long val2 = ca2.getFromPosition(coordinatesObj);
									if (val1 != val2) {
										equal = false;
										System.err.println("Different value at step " + ca1.getStep() + " " + coordinatesObj + ": " 
												+ ca1.getClass().getSimpleName() + ":" + val1 
												+ " != " + ca2.getClass().getSimpleName() + ":" + val2);
									}
								}
								//to prevent infinite loop if maxCoord is equal to Integer.MAX_VALUE
								coordinates[0] = maxCoord;
								Coordinates coordinatesObj = new Coordinates(coordinates);
								long val1 = ca1.getFromPosition(coordinatesObj).longValue();
								long val2 = ca2.getFromPosition(coordinatesObj);
								if (val1 != val2) {
									equal = false;
									System.err.println("Different value at step " + ca1.getStep() + " " + coordinatesObj + ": " 
											+ ca1.getClass().getSimpleName() + ":" + val1 
											+ " != " + ca2.getClass().getSimpleName() + ":" + val2);
								}
								isBeginningOfLoop = false;
								currentAxis++;
							} else if (isBeginningOfLoop) {
								PartialCoordinates partialCoordinatesObj = new PartialCoordinates(partialCoordinates);
								int localMinCoord = Math.max(ca1.getMinCoordinate(0, partialCoordinatesObj), ca2.getMinCoordinate(0, partialCoordinatesObj));
								maxCoords[currentAxis] = Math.min(ca1.getMaxCoordinate(0, partialCoordinatesObj), ca2.getMaxCoordinate(0, partialCoordinatesObj));
								coordinates[currentAxis] = localMinCoord;
								partialCoordinates[currentAxis] = localMinCoord;
								currentAxis--;
							} else {
								int currentCoordinate = coordinates[currentAxis];
								if (currentCoordinate < maxCoords[currentAxis]) {
									isBeginningOfLoop = true;
									currentCoordinate++;
									coordinates[currentAxis] = currentCoordinate;
									partialCoordinates[currentAxis] = currentCoordinate;
									currentAxis--;
								} else {
									partialCoordinates[currentAxis] = null;
									currentAxis++;
								}
							}
						}
					}
					Boolean changed;
					finished1 = (changed = ca1.nextStep()) != null && !changed;
					finished2 = (changed = ca2.nextStep()) != null && !changed;
					if (finished1 != finished2) {
						equal = false;
						String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
						System.err.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
					}
				}
				if (equal)
					System.out.println("Equal!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("Different grid dimension: " 
					+ ca1.getClass().getSimpleName() + ":" + dimension 
					+ " != " + ca2.getClass().getSimpleName() + ":" + dimension2);
		}
	}
	

	public static <Number_Type extends Number & FieldElement<Number_Type> & Comparable<Number_Type>> void compareAllSteps(IntModel ca1, NumericModel<Number_Type> ca2) {
		compareAllSteps(ca2, ca1);
	}
	
	public static <Number_Type extends Number & FieldElement<Number_Type> & Comparable<Number_Type>> void compareAllSteps(NumericModel<Number_Type> ca1, IntModel ca2) {
		System.out.println("Comparing...");
		int dimension = ca1.getGridDimension();
		int dimension2 = ca2.getGridDimension();
		if (dimension == dimension2) {
			try {
				boolean finished1 = false;
				boolean finished2 = false;
				boolean equal = true;
				int[] coordinates = new int[dimension];
				while (equal && !finished1 && !finished2) {
					if (dimension == 0) {
						Coordinates coordinatesObj = new Coordinates(coordinates);
						int val1 = ca1.getFromPosition(coordinatesObj).intValue();
						int val2 = ca2.getFromPosition(coordinatesObj);
						if (val1 != val2) {
							equal = false;
							System.err.println("Different value at step " + ca1.getStep() + " " + coordinatesObj + ": " 
									+ ca1.getClass().getSimpleName() + ":" + val1 
									+ " != " + ca2.getClass().getSimpleName() + ":" + val2);
						}
					} else {
						Integer[] partialCoordinates = new Integer[dimension];
						int[] maxCoords = new int[dimension];
						int currentAxis = dimension - 1;
						boolean isBeginningOfLoop = true;
						while (currentAxis < dimension) {
							if (currentAxis == 0) {
								PartialCoordinates partialCoordinatesObj = new PartialCoordinates(partialCoordinates);
								//ignore possible differences in bounds
								int minCoord = Math.max(ca1.getMinCoordinate(0, partialCoordinatesObj), ca2.getMinCoordinate(0, partialCoordinatesObj));
								int maxCoord = Math.min(ca1.getMaxCoordinate(0, partialCoordinatesObj), ca2.getMaxCoordinate(0, partialCoordinatesObj));
								for (int currentCoordinate = minCoord; currentCoordinate != maxCoord; currentCoordinate++) {
									coordinates[0] = currentCoordinate;
									Coordinates coordinatesObj = new Coordinates(coordinates);
									int val1 = ca1.getFromPosition(coordinatesObj).intValue();
									int val2 = ca2.getFromPosition(coordinatesObj);
									if (val1 != val2) {
										equal = false;
										System.err.println("Different value at step " + ca1.getStep() + " " + coordinatesObj + ": " 
												+ ca1.getClass().getSimpleName() + ":" + val1 
												+ " != " + ca2.getClass().getSimpleName() + ":" + val2);
									}
								}
								//to prevent infinite loop if maxCoord is equal to Integer.MAX_VALUE
								coordinates[0] = maxCoord;
								Coordinates coordinatesObj = new Coordinates(coordinates);
								int val1 = ca1.getFromPosition(coordinatesObj).intValue();
								int val2 = ca2.getFromPosition(coordinatesObj);
								if (val1 != val2) {
									equal = false;
									System.err.println("Different value at step " + ca1.getStep() + " " + coordinatesObj + ": " 
											+ ca1.getClass().getSimpleName() + ":" + val1 
											+ " != " + ca2.getClass().getSimpleName() + ":" + val2);
								}
								isBeginningOfLoop = false;
								currentAxis++;
							} else if (isBeginningOfLoop) {
								PartialCoordinates partialCoordinatesObj = new PartialCoordinates(partialCoordinates);
								int localMinCoord = Math.max(ca1.getMinCoordinate(0, partialCoordinatesObj), ca2.getMinCoordinate(0, partialCoordinatesObj));
								maxCoords[currentAxis] = Math.min(ca1.getMaxCoordinate(0, partialCoordinatesObj), ca2.getMaxCoordinate(0, partialCoordinatesObj));
								coordinates[currentAxis] = localMinCoord;
								partialCoordinates[currentAxis] = localMinCoord;
								currentAxis--;
							} else {
								int currentCoordinate = coordinates[currentAxis];
								if (currentCoordinate < maxCoords[currentAxis]) {
									isBeginningOfLoop = true;
									currentCoordinate++;
									coordinates[currentAxis] = currentCoordinate;
									partialCoordinates[currentAxis] = currentCoordinate;
									currentAxis--;
								} else {
									partialCoordinates[currentAxis] = null;
									currentAxis++;
								}
							}
						}
					}
					Boolean changed;
					finished1 = (changed = ca1.nextStep()) != null && !changed;
					finished2 = (changed = ca2.nextStep()) != null && !changed;
					if (finished1 != finished2) {
						equal = false;
						String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
						System.err.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
					}
				}
				if (equal)
					System.out.println("Equal!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("Different grid dimension: " 
					+ ca1.getClass().getSimpleName() + ":" + dimension 
					+ " != " + ca2.getClass().getSimpleName() + ":" + dimension2);
		}
	}
	
	
	public static void compareAllStepsWithIterators(LongModel ca1, LongModel ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (equal && !finished1 && !finished2) {
				Iterator<Long> iterator1 = ca1.iterator();
				Iterator<Long> iterator2 = ca2.iterator();
//				System.out.println("Comparing step " + ca1.getStep());
				while (iterator1.hasNext() && iterator2.hasNext()) {
					long value1 = iterator1.next();
					long value2 = iterator2.next();
					if (value1 != value2) {
						equal = false;
						System.err.println("Different value at step " + ca1.getStep() + ": " 
								+ ca1.getClass().getSimpleName() + ":" + value1 
								+ " != " + ca2.getClass().getSimpleName() + ":" + value2);
						//return;
					}
				}
				Boolean changed;
				finished1 = (changed = ca1.nextStep()) != null && !changed;
				finished2 = (changed = ca2.nextStep()) != null && !changed;
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.err.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
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
			Boolean changed;
			do {
				long[] minAndMax = ca.getMinAndMax();
				System.out.println("min: " + minAndMax[0] + "\t\tmax: " + minAndMax[1]);
			} while((changed = ca.nextStep()) == null || changed);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void testTotalValueConservation(LongModel ca) {
		System.out.println("Checking total value conservation...");
		try {
			long value = ca.getTotal(), newValue = value;
			boolean finished = false;
			while (value == newValue && !finished) {
				Boolean changed;
				finished = (changed = ca.nextStep()) != null && !changed;
				newValue = ca.getTotal();
			}
			if (!finished) {
				System.err.println("The total value changed at step " + ca.getStep() + ". Previous value " + value + ", new value " + newValue);
			} else {
				System.out.println("The total value remained constant!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static <Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> void testTotalValueConservation(NumericModel<Number_Type> ca) {
		System.out.println("Checking total value conservation...");
		try {
			Number_Type value = ca.getTotal(), newValue = value;
			boolean finished = false;
			while (value.equals(newValue) && !finished) {
				Boolean changed;
				finished = (changed = ca.nextStep()) != null && !changed;
				newValue = ca.getTotal();
			}
			if (!finished) {
				System.err.println("The total value changed at step " + ca.getStep() + ". Previous value " + value + ", new value " + newValue);
			} else {
				System.out.println("The total value remained constant!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void printTotalValueEvolution(LongModel ca) {
		try {
			Boolean changed;
			do {
				System.out.println("step " + ca.getStep() + ": " + ca.getTotal());
			} while((changed = ca.nextStep()) == null || changed);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void testTotalValueConservation(IntModel ca) {
		System.out.println("Checking total value conservation...");
		try {
			int value = ca.getTotal(), newValue = value;
			boolean finished = false;
			while (value == newValue && !finished) {
				Boolean changed;
				finished = (changed = ca.nextStep()) != null && !changed;
				newValue = ca.getTotal();
			}
			if (!finished) {
				System.err.println("The total value changed at step " + ca.getStep() + ". Previous value " + value + ", new value " + newValue);
			} else {
				System.out.println("The total value remained constant!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static boolean isBondBetweenCoordinateParityAndValueSignum(int minAtEvenCompareToZero, int maxAtEvenCompareToZero, int minAtOddCompareToZero, int maxAtOddCompareToZero) {
		return minAtEvenCompareToZero <= 0 && maxAtEvenCompareToZero <= 0 && minAtOddCompareToZero >= 0 && maxAtOddCompareToZero >= 0
				|| minAtEvenCompareToZero >= 0 && maxAtEvenCompareToZero >= 0 && minAtOddCompareToZero <= 0 && maxAtOddCompareToZero <= 0;
	}
	
	public static <Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> void testBondBetweenCoordinateParityAndValueSignum(ObjectModel<Number_Type> ca, Number_Type zero) {
		System.out.println("Checking bond between coordinate parity and value signum...");
		try {
			MinAndMaxConsumer<Number_Type> consumer = new MinAndMaxConsumer<Number_Type>();
			ca.forEachAtEvenPosition(consumer);
			MinAndMax<Number_Type> minAndMax = consumer.getMinAndMaxValue();
			int minAtEvenCompareToZero = minAndMax.getMin().compareTo(zero);
			int maxAtEvenCompareToZero = minAndMax.getMax().compareTo(zero);
			consumer = new MinAndMaxConsumer<Number_Type>();
			ca.forEachAtOddPosition(consumer);
			minAndMax = consumer.getMinAndMaxValue();
			int minAtOddCompareToZero = minAndMax.getMin().compareTo(zero);
			int maxAtOddCompareToZero = minAndMax.getMax().compareTo(zero);
			if (isBondBetweenCoordinateParityAndValueSignum(minAtEvenCompareToZero, maxAtEvenCompareToZero, minAtOddCompareToZero, maxAtOddCompareToZero)) {
				boolean isEvenPositive = maxAtEvenCompareToZero > 0;
				boolean finished = false;
				while (isBondBetweenCoordinateParityAndValueSignum(minAtEvenCompareToZero, maxAtEvenCompareToZero, minAtOddCompareToZero, maxAtOddCompareToZero) 
						&& isEvenPositive == maxAtEvenCompareToZero > 0 && !finished) {
					Boolean changed;
					finished = (changed = ca.nextStep()) != null && !changed;
					consumer = new MinAndMaxConsumer<Number_Type>();
					ca.forEachAtEvenPosition(consumer);
					minAndMax = consumer.getMinAndMaxValue();
					minAtEvenCompareToZero = minAndMax.getMin().compareTo(zero);
					maxAtEvenCompareToZero = minAndMax.getMax().compareTo(zero);
					consumer = new MinAndMaxConsumer<Number_Type>();
					ca.forEachAtOddPosition(consumer);
					minAndMax = consumer.getMinAndMaxValue();
					minAtOddCompareToZero = minAndMax.getMin().compareTo(zero);
					maxAtOddCompareToZero = minAndMax.getMax().compareTo(zero);
					isEvenPositive = !isEvenPositive;
				}
				if (!finished) {
					System.err.println("The bond broke at step " + ca.getStep());
				} else {
					System.out.println("A perfect bond was found!");
				}				
			} else {
				System.err.println("No bond was found.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testBondBetweenCoordinateParityAndValueSignum(IntModel ca) {
		System.out.println("Checking bond between coordinate parity and value signum...");
		try {
			MinAndMaxIntConsumer consumer = new MinAndMaxIntConsumer();
			ca.forEachAtEvenPosition(consumer);
			int[] minAndMax = consumer.getMinAndMaxValue();
			int minAtEvenCompareToZero = minAndMax[0];
			int maxAtEvenCompareToZero = minAndMax[1];
			consumer = new MinAndMaxIntConsumer();
			ca.forEachAtOddPosition(consumer);
			minAndMax = consumer.getMinAndMaxValue();
			int minAtOddCompareToZero = minAndMax[0];
			int maxAtOddCompareToZero = minAndMax[1];
			if (isBondBetweenCoordinateParityAndValueSignum(minAtEvenCompareToZero, maxAtEvenCompareToZero, minAtOddCompareToZero, maxAtOddCompareToZero)) {
				boolean isEvenPositive = maxAtEvenCompareToZero > 0;
				boolean finished = false;
				while (isBondBetweenCoordinateParityAndValueSignum(minAtEvenCompareToZero, maxAtEvenCompareToZero, minAtOddCompareToZero, maxAtOddCompareToZero) 
						&& isEvenPositive == maxAtEvenCompareToZero > 0 && !finished) {
					Boolean changed;
					finished = (changed = ca.nextStep()) != null && !changed;
					consumer = new MinAndMaxIntConsumer();
					ca.forEachAtEvenPosition(consumer);
					minAndMax = consumer.getMinAndMaxValue();
					minAtEvenCompareToZero = minAndMax[0];
					maxAtEvenCompareToZero = minAndMax[1];
					consumer = new MinAndMaxIntConsumer();
					ca.forEachAtOddPosition(consumer);
					minAndMax = consumer.getMinAndMaxValue();
					minAtOddCompareToZero = minAndMax[0];
					maxAtOddCompareToZero = minAndMax[1];
					isEvenPositive = !isEvenPositive;
				}
				if (!finished) {
					System.err.println("The bond broke at step " + ca.getStep());
				} else {
					System.out.println("A perfect bond was found!");
				}				
			} else {
				System.err.println("No bond was found.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testBondBetweenCoordinateParityAndValueSignum(LongModel ca) {
		System.out.println("Checking bond between coordinate parity and value signum...");
		try {
			MinAndMaxLongConsumer consumer = new MinAndMaxLongConsumer();
			ca.forEachAtEvenPosition(consumer);
			long[] minAndMax = consumer.getMinAndMaxValue();
			long tmp;
			int minAtEvenCompareToZero = (tmp = minAndMax[0]) == 0 ? 0 : tmp > 0 ? 1 : -1;
			int maxAtEvenCompareToZero = (tmp = minAndMax[1]) == 0 ? 0 : tmp > 0 ? 1 : -1;
			consumer = new MinAndMaxLongConsumer();
			ca.forEachAtOddPosition(consumer);
			minAndMax = consumer.getMinAndMaxValue();
			int minAtOddCompareToZero = (tmp = minAndMax[0]) == 0 ? 0 : tmp > 0 ? 1 : -1;
			int maxAtOddCompareToZero = (tmp = minAndMax[1]) == 0 ? 0 : tmp > 0 ? 1 : -1;
			if (isBondBetweenCoordinateParityAndValueSignum(minAtEvenCompareToZero, maxAtEvenCompareToZero, minAtOddCompareToZero, maxAtOddCompareToZero)) {
				boolean isEvenPositive = maxAtEvenCompareToZero > 0;
				boolean finished = false;
				while (isBondBetweenCoordinateParityAndValueSignum(minAtEvenCompareToZero, maxAtEvenCompareToZero, minAtOddCompareToZero, maxAtOddCompareToZero) 
						&& isEvenPositive == maxAtEvenCompareToZero > 0 && !finished) {
					Boolean changed;
					finished = (changed = ca.nextStep()) != null && !changed;
					consumer = new MinAndMaxLongConsumer();
					ca.forEachAtEvenPosition(consumer);
					minAndMax = consumer.getMinAndMaxValue();
					minAtEvenCompareToZero = (tmp = minAndMax[0]) == 0 ? 0 : tmp > 0 ? 1 : -1;
					maxAtEvenCompareToZero = (tmp = minAndMax[1]) == 0 ? 0 : tmp > 0 ? 1 : -1;
					consumer = new MinAndMaxLongConsumer();
					ca.forEachAtOddPosition(consumer);
					minAndMax = consumer.getMinAndMaxValue();
					minAtOddCompareToZero = (tmp = minAndMax[0]) == 0 ? 0 : tmp > 0 ? 1 : -1;
					maxAtOddCompareToZero = (tmp = minAndMax[1]) == 0 ? 0 : tmp > 0 ? 1 : -1;
					isEvenPositive = !isEvenPositive;
				}
				if (!finished) {
					System.err.println("The bond broke at step " + ca.getStep());
				} else {
					System.out.println("A perfect bond was found!");
				}				
			} else {
				System.err.println("No bond was found.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static <Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> void printEvenOddMinMaxValues(ObjectModel<Number_Type> ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step: " + ca.getStep());
				MinAndMaxConsumer<Number_Type> consumer = new MinAndMaxConsumer<Number_Type>();
				ca.forEachAtEvenPosition(consumer);
				MinAndMax<Number_Type> minAndMax = consumer.getMinAndMaxValue();
				System.out.println("even positions:\t{ min: " + minAndMax.getMin() + ",\tmax: " + minAndMax.getMax() + " }");
				consumer = new MinAndMaxConsumer<Number_Type>();
				ca.forEachAtOddPosition(consumer);
				minAndMax = consumer.getMinAndMaxValue();
				System.out.println("odd positions:\t{ min: " + minAndMax.getMin() + ",\tmax: " + minAndMax.getMax() + " }");
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void printEvenOddMinMaxValues(IntModel ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step: " + ca.getStep());
				MinAndMaxIntConsumer consumer = new MinAndMaxIntConsumer();
				ca.forEachAtEvenPosition(consumer);
				int[] minAndMax = consumer.getMinAndMaxValue();
				System.out.println("even positions:\t{ min: " + minAndMax[0] + ",\tmax: " + minAndMax[1] + " }");
				consumer = new MinAndMaxIntConsumer();
				ca.forEachAtOddPosition(consumer);
				minAndMax = consumer.getMinAndMaxValue();
				System.out.println("odd positions:\t{ min: " + minAndMax[0] + ",\tmax: " + minAndMax[1] + " }" + System.lineSeparator());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void printEvenOddMinMaxValues(LongModel ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step: " + ca.getStep());
				MinAndMaxLongConsumer consumer = new MinAndMaxLongConsumer();
				ca.forEachAtEvenPosition(consumer);
				long[] minAndMax = consumer.getMinAndMaxValue();
				System.out.println("even positions:\t{ min: " + minAndMax[0] + ",\tmax: " + minAndMax[1] + " }");
				consumer = new MinAndMaxLongConsumer();
				ca.forEachAtOddPosition(consumer);
				minAndMax = consumer.getMinAndMaxValue();
				System.out.println("odd positions:\t{ min: " + minAndMax[0] + ",\tmax: " + minAndMax[1] + " }" + System.lineSeparator());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep(IntModel1D ca) {
		try {
			Scanner s = new Scanner(System.in);
			Boolean changed;
			do {
				System.out.println("step " + ca.getStep());
				Utils.printAsGrid(ca);
				System.out.println("total value " + ca.getTotal());
				s.nextLine();
			} while ((changed = ca.nextStep()) == null || changed);
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep(IntModel2D ca) {
		try {
			Scanner s = new Scanner(System.in);
			Boolean changed;
			do {
				System.out.println("step " + ca.getStep());
				Utils.printAsGrid(ca);
				System.out.println("total value " + ca.getTotal());
				s.nextLine();
			} while ((changed = ca.nextStep()) == null || changed);
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void stepByStep(LongModel1D ca) {
		try {
			Scanner s = new Scanner(System.in);
			Boolean changed;
			do {
				System.out.println("step " + ca.getStep());
				Utils.printAsGrid(ca);
				System.out.println("total value " + ca.getTotal());
				s.nextLine();
			} while ((changed = ca.nextStep()) == null || changed);
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void stepByStep(LongModel2D ca) {
		try {
			Scanner s = new Scanner(System.in);
			Boolean changed;
			do {
				System.out.println("step " + ca.getStep());
				Utils.printAsGrid(ca);
				System.out.println("total value " + ca.getTotal());
				s.nextLine();
			} while ((changed = ca.nextStep()) == null || changed);
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static <Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> void stepByStep(NumericModel1D<Number_Type> ca) {
		try {
			Scanner s = new Scanner(System.in);
			Boolean changed;
			do {
				System.out.println("step " + ca.getStep());
				Utils.printAsGrid(ca);
				System.out.println("total value " + ca.getTotal());
				s.nextLine();
			} while ((changed = ca.nextStep()) == null || changed);
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static <Number_Type extends Number & FieldElement<Number_Type> & Comparable<Number_Type>> void stepByStep(NumericModel2D<Number_Type> ca) {
		try {
			Scanner s = new Scanner(System.in);
			Boolean changed;
			do {
				System.out.println("step " + ca.getStep());
				Utils.printAsGrid(ca);
				System.out.println("total value " + ca.getTotal());
				s.nextLine();
			} while ((changed = ca.nextStep()) == null || changed);
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep(IntModel ca) {
		int dimension = ca.getGridDimension();
		if (dimension != 1 && dimension != 2) {
			throw new IllegalArgumentException("Grid's dimension must be one or two.");
		}
		if (dimension == 1) {
			stepByStep(new IntModelAs1D(ca));
		} else {
			stepByStep(new IntModelAs2D(ca));
		}
	}
	
	public static void stepByStep(LongModel ca) {
		int dimension = ca.getGridDimension();
		if (dimension != 1 && dimension != 2) {
			throw new IllegalArgumentException("Grid's dimension must be one or two.");
		}
		if (dimension == 1) {
			stepByStep(new LongModelAs1D(ca));
		} else {
			stepByStep(new LongModelAs2D(ca));
		}
	}
	
	public static <Object_Type> void stepByStep(ObjectModel<Object_Type> ca) {
		int dimension = ca.getGridDimension();
		if (dimension != 1 && dimension != 2) {
			throw new IllegalArgumentException("Grid's dimension must be one or two.");
		}
		if (dimension == 1) {
			stepByStep(new ObjectModelAs1D<Object_Type>(ca));
		} else {
			stepByStep(new ObjectModelAs2D<Object_Type>(ca));
		}
	}
	
	public static <Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> void stepByStep(NumericModel<Number_Type> ca) {
		int dimension = ca.getGridDimension();
		if (dimension != 1 && dimension != 2) {
			throw new IllegalArgumentException("Grid's dimension must be one or two.");
		}
		if (dimension == 1) {
			stepByStep(new NumericModelAs1D<Number_Type>(ca));
		} else {
			stepByStep(new NumericModelAs2D<Number_Type>(ca));
		}
	}
	
	public static void race(Model... cas) {
		try {
			long millis;
			for (Model ca : cas) {
				Boolean changed;
				millis = System.currentTimeMillis();
				while ((changed = ca.nextStep()) != null && changed);
				System.out.println(ca.getClass().getSimpleName() + ": " + (System.currentTimeMillis() - millis));
				if (changed == null) {
					System.out.println("Only one step computed.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static long[][] parseCsvLong2DArray(String pathName) throws IOException {
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
