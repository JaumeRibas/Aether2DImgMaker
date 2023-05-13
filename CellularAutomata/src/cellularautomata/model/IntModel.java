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
package cellularautomata.model;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import cellularautomata.Coordinates;
import cellularautomata.MinAndMaxIntConsumer;
import cellularautomata.PartialCoordinates;
import cellularautomata.TotalIntConsumer;

public interface IntModel extends Model, Iterable<Integer> {
	
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
	int getFromPosition(Coordinates coordinates) throws Exception;
	
	/**
	 * Feeds every value of the region, in a consistent order, to an {@link IntConsumer}.
	 * 
	 * @param consumer
	 */
	default void forEach(IntConsumer consumer) {
		forEachPosition(new Consumer<Coordinates>() {
			
			@Override
			public void accept(Coordinates coords) {
				try {
					consumer.accept(getFromPosition(coords));
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}
		});
	}
	
	/**
	 * Feeds every value at an even position of the region in a consistent order to a {@link IntConsumer}.
	 * 
	 * @param consumer
	 * @throws Exception 
	 */
	default void forEachAtEvenPosition(IntConsumer consumer) throws Exception {
		forEachEvenPosition(new Consumer<Coordinates>() {
			
			@Override
			public void accept(Coordinates coordinates) {
				try {
					int value = getFromPosition(coordinates);
					consumer.accept(value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Feeds every value at an odd position of the region in a consistent order to a {@link IntConsumer}.
	 * 
	 * @param consumer
	 * @throws Exception 
	 */
	default void forEachAtOddPosition(IntConsumer consumer) throws Exception {
		forEachOddPosition(new Consumer<Coordinates>() {
			
			@Override
			public void accept(Coordinates coordinates) {
				try {
					int value = getFromPosition(coordinates);
					consumer.accept(value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	default int[] getMinAndMax() throws Exception {
		MinAndMaxIntConsumer consumer = new MinAndMaxIntConsumer();
		forEach(consumer);
		return consumer.getMinAndMaxValue();
	}
	
	default int getTotal() throws Exception {
		TotalIntConsumer consumer = new TotalIntConsumer();
		forEach(consumer);
		return consumer.getTotal();
	}
	
	default int[] getEvenOddPositionsMinAndMax(boolean isEven) throws Exception {
		if (isEven) {
			return getEvenPositionsMinAndMax();
		} else {
			return getOddPositionsMinAndMax();
		}
	}
	
	default int[] getEvenPositionsMinAndMax() throws Exception {
		MinAndMaxIntConsumer consumer = new MinAndMaxIntConsumer();
		forEachAtEvenPosition(consumer);
		return consumer.getMinAndMaxValue();
	}
	
	default int[] getOddPositionsMinAndMax() throws Exception {
		MinAndMaxIntConsumer consumer = new MinAndMaxIntConsumer();
		forEachAtOddPosition(consumer);
		return consumer.getMinAndMaxValue();
	}

	@Override
	default Iterator<Integer> iterator() {
		return new IntModelIterator(this);
	}
	
	@Override
	default IntModel subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return new IntSubModel(this, minCoordinates, maxCoordinates);
	}
	
	@Override
	default IntModel crossSection(int axis, int coordinate) {
		return new IntModelCrossSection(this, axis, coordinate);
	}
	
	@Override
	default IntModel crossSection(PartialCoordinates coordinates) {
		return (IntModel) Model.super.crossSection(coordinates);
	}
	
	@Override
	default IntModel diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return new IntModelDiagonalCrossSection(this, firstAxis, secondAxis, positiveSlope, offset);
	}
	
}
