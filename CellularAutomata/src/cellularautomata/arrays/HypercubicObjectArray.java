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
import java.util.Iterator;
import java.util.function.Consumer;
import cellularautomata.Coordinates;

public abstract class HypercubicObjectArray<Object_Type> extends HypercubicArray implements MultidimensionalObjectArray<Object_Type> {

	protected Object_Type[] underlyingArray;
	
	public HypercubicObjectArray(int dimension, int side) {
		super(dimension, side);
	}
	
	@Override
	public Object_Type get(Coordinates indexes) {
		return underlyingArray[getInternalArrayIndex(indexes)];
	}
	
	@Override
	public void set(Coordinates indexes, Object_Type element) {
		underlyingArray[getInternalArrayIndex(indexes)] = element;
	}

	@Override
	public void fill(Object_Type element) {
		Arrays.fill(underlyingArray, element);
	}
	
	public void fillEdges(int edgeWidth, Object_Type element) {
		forEachEdgeIndex(edgeWidth, new Consumer<Coordinates>() {		
			@Override
			public void accept(Coordinates indexes) {
				set(indexes, element);
			}
		});
	}
	
	@Override
	public void forEach(Consumer<? super Object_Type> consumer) {
		for (int i = 0; i < underlyingArray.length; i++) {
			consumer.accept(underlyingArray[i]);
		}
	}
	
	@Override
	public Iterator<Object_Type> iterator() {
		return Arrays.asList(underlyingArray).iterator();
	}
	
}
