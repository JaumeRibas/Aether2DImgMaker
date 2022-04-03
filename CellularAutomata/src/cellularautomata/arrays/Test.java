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
package cellularautomata.arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;

import cellularautomata.Coordinates;
import cellularautomata.Utils;

public class Test {	

	public static void main(String[] args) {
		testRectangularIntArray2();
	}
	
	public static void testArrayClonning() {
		int[] array1 = new int[] {1, 2, 3, 4};
		System.out.println("a:" + Arrays.toString(array1));
		int[] array2 = array1.clone();
		System.out.println("b:" + Arrays.toString(array2));
		array2[0] = 9;
		System.out.println("a:" + Arrays.toString(array1));
		System.out.println("b:" + Arrays.toString(array2));
	}
	
	public static void testVarargs() {
		int[] myArray = new int[] { 12 };
		testVarargs2(myArray);
		System.out.println(myArray[0]);
	}
	
	public static void testVarargs2(int... args) {
		if (args.length > 0) {
			args[0] = 75;
		}		
	}
	
	public static void testArrayCopy() {
		int[] array1 = new int[] {1, 2, 3, 4};
		System.out.println(Arrays.toString(array1));
		int[] array2 = new int[array1.length];
		System.arraycopy(array1, 0, array2, 0, array2.length);
		System.out.println(Arrays.toString(array2));
	}
	
	public static void testSquareIntArray() {
		int side = 3;
		int dimension = 5;
		HypercubicIntArray arr = new HypercubicIntArray(dimension, side);
		System.out.println("dimension: " + arr.getDimension());
		ArrayList<Integer> list = new ArrayList<Integer>();
		System.out.println("Setting values");
		arr.forEachPosition(new Consumer<Coordinates>() {
			
			@Override
			public void accept(Coordinates coordinates) {
				int value = Utils.getRandomInt(Integer.MIN_VALUE, Integer.MAX_VALUE - 1);
				list.add(value);
				arr.set(coordinates, value);		
			}
		});
		System.out.println("Getting values");
		Iterator<Integer> iterator = list.iterator();
		arr.forEachPosition(new Consumer<Coordinates>() {
			
			@Override
			public void accept(Coordinates coordinates) {
				int value = arr.get(coordinates);
				if (value != iterator.next()) {
					System.err.println("Wroong!");
				}	
			}
		});
		if (list.size() != Math.pow(side, dimension)) {
			System.err.println("Wroong!");
		}
	}
	
	public static void testRectangularIntArray2() {
		int dimension = 5;
		int[] sizes = new int[dimension];
		int positionCount = 1;
		for (int i = 0; i < dimension; i++) {
			sizes[i] = Utils.getRandomInt(2, 6);
			positionCount *= sizes[i];
		}
		System.out.println(Arrays.toString(sizes));
		HyperrectangularIntArray arr = new HyperrectangularIntArray(sizes);
		System.out.println("dimension: " + arr.getDimension());
		ArrayList<Integer> list = new ArrayList<Integer>();
		System.out.println("Setting values");
		arr.forEachPosition(new Consumer<Coordinates>() {
			
			@Override
			public void accept(Coordinates coordinates) {
				int value = Utils.getRandomInt(Integer.MIN_VALUE, Integer.MAX_VALUE - 1);
				list.add(value);
				arr.set(coordinates, value);		
			}
		});
		System.out.println("Getting values");
		Iterator<Integer> iterator = list.iterator();
		arr.forEachPosition(new Consumer<Coordinates>() {
			
			@Override
			public void accept(Coordinates coordinates) {
				int value = arr.get(coordinates);
				if (value != iterator.next()) {
					System.err.println("Wroong!");
				}	
			}
		});		
		if (list.size() != positionCount) {
			System.err.println("Wroong!");
		}
	}
	
	public static void testRectangularIntArray() {
		HyperrectangularIntArray arr = new HyperrectangularIntArray(new int[] { 50, 13, 12, 32 });
		System.out.println("dimension: " + arr.getDimension());
		int size0 = arr.getSize(0);
		int size1 = arr.getSize(1);
		int size2 = arr.getSize(2);
		int size3 = arr.getSize(3);
		ArrayList<Integer> list = new ArrayList<Integer>();
		System.out.println("Setting values");
		for (int i = 0; i < size0; i++) {
			for (int j = 0; j < size1; j++) {
				for (int k = 0; k < size2; k++) {
					for (int l = 0; l < size3; l++) {
						int value = Utils.getRandomInt(Integer.MIN_VALUE, Integer.MAX_VALUE - 1);
						list.add(value);
						arr.set(new Coordinates(new int[] {i, j, k, l}), value);
					}
				}
			}
		}
		System.out.println(list.size());
		int listIndex = 0;
		System.out.println("Getting values");
		for (int i = 0; i < size0; i++) {
			for (int j = 0; j < size1; j++) {
				for (int k = 0; k < size2; k++) {
					for (int l = 0; l < size3; l++) {
						int value = arr.get(new Coordinates(new int[] {i, j, k, l}));
						if (value != list.get(listIndex)) {
							System.err.println("Wroong!");
						}
						listIndex++;
					}
				}
			}
		}
	}
	
	public static void testRectangularIntArray3() {
		HyperrectangularIntArray arr = new HyperrectangularIntArray(new int[] { 3, 3, 3 });
		System.out.println("dimension: " + arr.getDimension());
		arr.forEachPosition(new Consumer<Coordinates>() {
			
			@Override
			public void accept(Coordinates coordinates) {
				System.out.println(coordinates);
			}
		});
	}

}









