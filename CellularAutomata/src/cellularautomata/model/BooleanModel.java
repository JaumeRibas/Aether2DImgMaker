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
package cellularautomata.model;

import java.util.Iterator;
import java.util.function.Consumer;
import cellularautomata.Coordinates;
import cellularautomata.PartialCoordinates;

public interface BooleanModel extends Model, Iterable<Boolean> {
	
	/**
	 * <p>Returns the value at the given coordinates.</p>
	 * <p>It is not defined to call this method with coordinates of a dimension different form the grid's dimension. This is obtained by calling the {@link #getGridDimension()} method.
	 * <p>It is also not defined to call this method passing coordinates outside the bounds of the region. 
	 * To get these bounds use the {@link #getMaxCoordinate(int)}, {@link #getMaxCoordinate(int, PartialCoordinates)}, 
	 * {@link #getMinCoordinate(int)} and {@link #getMinCoordinate(int, PartialCoordinates)} methods.</p>
	 * 
	 * @param coordinates a {@link Coordinates} object
	 * @return the value at the given position.
	 * @throws Exception 
	 */
	boolean getFromPosition(Coordinates coordinates) throws Exception;
	
	/**
	 * Feeds every value at an even position of the region in a consistent order to a {@link Consumer}.
	 * 
	 * @param consumer
	 * @throws Exception 
	 */
	default void forEachAtEvenPosition(Consumer<? super Boolean> consumer) throws Exception {
		forEachEvenPosition(new Consumer<Coordinates>() {
			
			@Override
			public void accept(Coordinates coordinates) {
				try {
					boolean value = getFromPosition(coordinates);
					consumer.accept(value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Feeds every value at an odd position of the region in a consistent order to a {@link Consumer}.
	 * 
	 * @param consumer
	 * @throws Exception 
	 */
	default void forEachAtOddPosition(Consumer<? super Boolean> consumer) throws Exception {
		forEachOddPosition(new Consumer<Coordinates>() {
			
			@Override
			public void accept(Coordinates coordinates) {
				try {
					boolean value = getFromPosition(coordinates);
					consumer.accept(value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	default Iterator<Boolean> iterator() {
		return new BooleanModelIterator(this);
	}
	
	@Override
	default BooleanModel subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return new BooleanSubModel(this, minCoordinates, maxCoordinates);
	}
	
	@Override
	default BooleanModel crossSection(int axis, int coordinate) {
		return new BooleanModelCrossSection(this, axis, coordinate);
	}
	
	@Override
	default BooleanModel crossSection(PartialCoordinates coordinates) {
		return (BooleanModel) Model.super.crossSection(coordinates);
	}
	
	@Override
	default BooleanModel diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return new BooleanModelDiagonalCrossSection(this, firstAxis, secondAxis, positiveSlope, offset);
	}
	
}
