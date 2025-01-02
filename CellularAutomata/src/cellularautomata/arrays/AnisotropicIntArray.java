/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2025 Jaume Ribas

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

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import cellularautomata.Coordinates;

public class AnisotropicIntArray extends AnisotropicArray implements MultidimensionalIntArray {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7155466160162596971L;
	
	private int[] values;
	
	public AnisotropicIntArray(int dimension, int side) {
		super(dimension, side);
		values = new int[(int) getPositionCount()];
	}
	
	@Override
	public int get(Coordinates indexes) {
		return values[getInternalArrayIndex(indexes)];
	}
	
	@Override
	public void set(Coordinates indexes, int value) {
		values[getInternalArrayIndex(indexes)] = value;
	}

	@Override
	public int addAndGet(Coordinates indexes, int value) {
		int index = getInternalArrayIndex(indexes);
		int newValue = values[index] += value;
		return newValue;
	}

	@Override
	public void fill(int value) {
		Arrays.fill(values, value);
	}
	
	public void fillEdges(int edgeWidth, int value) {
		forEachEdgeIndex(edgeWidth, new Consumer<Coordinates>() {		
			@Override
			public void accept(Coordinates indexes) {
				set(indexes, value);
			}
		});
	}
	
	@Override
	public void forEach(IntConsumer consumer) {
		for (int i = 0; i < values.length; i++) {
			consumer.accept(values[i]);
		}
	}
	
}
