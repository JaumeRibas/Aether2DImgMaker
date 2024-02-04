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
package cellularautomata.model;

import java.util.function.Consumer;

import cellularautomata.Coordinates;
import cellularautomata.Utils;

public class Test {
	public static void main(String[] args) {
		testGridRegionForEachOddPosition();
	}

	public static void testGridRegionForEachPosition() {
		int dimension = 4;
		int[] vertex1 = new int[dimension];
		int[] vertex2 = new int[dimension];
		for (int i = 0; i < dimension; i++) {
			vertex1[i] = 0;
			vertex2[i] = 2;
		}
		AlignedHyperrectangle testRectangle = new AlignedHyperrectangle(new Coordinates(vertex1), new Coordinates(vertex2));
		testRectangle.forEachPosition(new Consumer<Coordinates>() {
			
			@Override
			public void accept(Coordinates coordinates) {
				System.out.println(coordinates);
			}
		});
	}
	
	public static void testGridRegionForEachEvenPosition() {
		int dimension = 4;
		int[] vertex1 = new int[dimension];
		int[] vertex2 = new int[dimension];
		for (int i = 0; i < dimension; i++) {
			vertex1[i] = 0;
			vertex2[i] = 2;
		}
		AlignedHyperrectangle testRectangle = new AlignedHyperrectangle(new Coordinates(vertex1), new Coordinates(vertex2));
		testRectangle.forEachEvenPosition(new Consumer<Coordinates>() {
			
			@Override
			public void accept(Coordinates coordinates) {
				if (!Utils.isEvenPosition(coordinates.getCopyAsArray())) {
					System.out.println("Wroong!");
				}
				System.out.println(coordinates);
			}
		});
	}
	
	public static void testGridRegionForEachOddPosition() {
		int dimension = 4;
		int[] vertex1 = new int[dimension];
		int[] vertex2 = new int[dimension];
		for (int i = 0; i < dimension; i++) {
			vertex1[i] = 0;
			vertex2[i] = 2;
		}
		AlignedHyperrectangle testRectangle = new AlignedHyperrectangle(new Coordinates(vertex1), new Coordinates(vertex2));
		testRectangle.forEachOddPosition(new Consumer<Coordinates>() {
			
			@Override
			public void accept(Coordinates coordinates) {
				if (Utils.isEvenPosition(coordinates.getCopyAsArray())) {
					System.out.println("Wroong!");
				}
				System.out.println(coordinates);
			}
		});
	}
}
