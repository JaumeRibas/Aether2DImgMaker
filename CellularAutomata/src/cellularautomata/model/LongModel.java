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
package cellularautomata.model;

import java.io.IOException;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

public interface LongModel extends SequentialLongModel {

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
	long getFromPosition(Coordinates coordinates) throws Exception;
	
	/**
	 * Feeds every value of the region in a consistent order to an {@link LongConsumer}.
	 * @param consumer
	 * @throws IOException 
	 */
	default void forEach(LongConsumer consumer) throws IOException {
		forEachPosition(new Consumer<Coordinates>() {
			
			@Override
			public void accept(Coordinates coords) {
				try {
					consumer.accept(getFromPosition(coords));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		});
	}
	
	default long[] getEvenPositionsMinAndMax() throws Exception {
		LongModelMinAndMaxCoordinateConsumer consumer = new LongModelMinAndMaxCoordinateConsumer(this);
		forEachEvenPosition(consumer);
		return new long[] {consumer.min, consumer.max};
	}
	
	default long[] getOddPositionsMinAndMax() throws Exception {
		LongModelMinAndMaxCoordinateConsumer consumer = new LongModelMinAndMaxCoordinateConsumer(this);
		forEachOddPosition(consumer);
		return new long[] {consumer.min, consumer.max};
	}

	@Override
	default Iterator<Long> iterator() {
		return new LongModelIterator(this);
	}
}
