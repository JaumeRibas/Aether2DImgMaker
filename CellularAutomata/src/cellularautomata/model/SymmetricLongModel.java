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
import java.util.function.LongConsumer;

import cellularautomata.Coordinates;

public interface SymmetricLongModel extends LongModel, SymmetricModel {
	
	@Override
	default LongModel asymmetricSection() {
		return new AsymmetricLongModelSection<SymmetricLongModel>(this);
	}

	/**
	 * <p>Returns the value at the given coordinates from an asymmetric section.</p>
	 * <p>It is not defined to call this method with coordinates of a dimension different form the grid's dimension. This is obtained by calling the {@link #getGridDimension()} method.
	 * <p>It is also not defined to call this method passing coordinates outside the bounds of the asymmetric section. 
	 * To get these bounds use the {@link #getAsymmetricMaxCoordinate(int)}, {@link #getAsymmetricMaxCoordinate(int, PartialCoordinates)}, 
	 * {@link #getAsymmetricMinCoordinate(int)} and {@link #getAsymmetricMinCoordinate(int, PartialCoordinates)} methods.</p>
	 * 
	 * @param coordinates a {@link Coordinates} object
	 * @return the value at the given position.
	 * @throws Exception 
	 */
	long getFromAsymmetricPosition(Coordinates coordinates) throws Exception;
	
	/**
	 * Feeds every value of an asymmetric section, in a consistent order, to a {@link LongConsumer}.
	 * @param consumer
	 */
	default void forEachInAsymmetricSection(LongConsumer consumer) {
		forEachPositionInAsymmetricSection(new Consumer<Coordinates>() {
			
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
	
}
