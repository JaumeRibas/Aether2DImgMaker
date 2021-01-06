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
package cellularautomata2.grid;

import cellularautomata2.arrays.Coordinates;
import cellularautomata2.arrays.IntValueCommand;

public abstract class SymmetricIntGridRegion extends IntGridRegion implements SymmetricGridRegion {

	/**
	 * <p>Returns the value at the given coordinates from an asymmetric region.</p>
	 * <p>It is not defined to call this method with coordinates of a dimension different form the grid's dimension. This is obtained by calling the {@link #getGridDimension()} method.
	 * <p>It is also not defined to call this method passing coordinates outside the bounds of the region. 
	 * To get these bounds use the {@link #getBounds(int)} and {@link #getBounds(int, Integer[])} methods.</p>
	 * 
	 * @param coordinates a {@link int} array
	 * @return the value at the given position.
	 */
	public abstract int getValueFromAsymmetricRegion(Coordinates coordinates);

	/**
	 * Executes a {@link IntValueCommand} for every value of an asymmetric region.
	 * @param command
	 */
	public abstract void forEachValueInAsymmetricRegion(IntValueCommand command);

	@Override
	public Bounds getValueBounds() throws Exception {
		GetValueBoundsValueCommand command = new GetValueBoundsValueCommand();
		forEachValueInAsymmetricRegion(command);
		return new Bounds(command.lowerBound, command.upperBound);
	}
	
	@Override
	public Bounds getEvenPositionsValueBounds() throws Exception {
		GetValueBoundsCoordinateCommand command = new GetValueBoundsCoordinateCommand(this);
		forEachEvenPositionInAsymmetricRegion(command);
		return new Bounds(command.lowerBound, command.upperBound);
	}
	
	@Override
	public Bounds getOddPositionsValueBounds() throws Exception {
		GetValueBoundsCoordinateCommand command = new GetValueBoundsCoordinateCommand(this);
		forEachOddPositionInAsymmetricRegion(command);
		return new Bounds(command.lowerBound, command.upperBound);
	}
	
	
}
