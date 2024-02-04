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
package cellularautomata.arrays;

import java.util.Arrays;
import java.util.function.Consumer;
import cellularautomata.Coordinates;

public class HypercubicBooleanArray extends HypercubicArray implements MultidimensionalBooleanArray {

	private boolean[] values;
	
	public HypercubicBooleanArray(int dimension, int side) {
		super(dimension, side);
		values = new boolean[(int) getPositionCount()];
	}
	
	@Override
	public boolean get(Coordinates indexes) {
		return values[getInternalArrayIndex(indexes)];
	}
	
	@Override
	public void set(Coordinates indexes, boolean value) {
		values[getInternalArrayIndex(indexes)] = value;
	}

	@Override
	public void fill(boolean value) {
		Arrays.fill(values, value);
	}
	
	public void fillEdges(int edgeWidth, boolean value) {
		forEachEdgeIndex(edgeWidth, new Consumer<Coordinates>() {		
			@Override
			public void accept(Coordinates indexes) {
				set(indexes, value);
			}
		});
	}
	
	@Override
	public void forEach(Consumer<? super Boolean> consumer) {
		for (int i = 0; i < values.length; i++) {
			consumer.accept(values[i]);
		}
	}
	
}
