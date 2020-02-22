/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2020 Jaume Ribas

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
import cellularautomata2.arrays.PositionCommand;

public abstract class IntGridRegion extends GridRegion {
	
	/**
	 * <p>Returns the value at the given coordinates.</p>
	 * <p>It is not defined to call this method with coordinates of a dimension different form the grid's dimension. This is obtained by calling the {@link #getGridDimension()} method.
	 * <p>It is also not defined to call this method passing coordinates outside the bounds of the region. 
	 * To get these bounds use the {@link #getBounds(int)} and {@link #getBounds(int, Integer[])} methods.</p>
	 * 
	 * @param coordinates a {@link int} array
	 * @return the value at the given position.
	 */
	public abstract int getValue(Coordinates coordinates);
	
	/**
	 * Executes a {@link IntValueCommand} for every value of the region.
	 * @param command
	 */
	public abstract void forEachValue(IntValueCommand command);

	public Bounds getValueBounds() throws Exception {
		GetValueBoundsValueCommand command = new GetValueBoundsValueCommand();
		forEachValue(command);
		return new Bounds(command.lowerBound, command.upperBound);
	}
	
	public Bounds getEvenPositionsValueBounds() throws Exception {
		GetValueBoundsCoordinateCommand command = new GetValueBoundsCoordinateCommand(this);
		forEachEvenPosition(command);
		return new Bounds(command.lowerBound, command.upperBound);
	}
	
	public Bounds getOddPositionsValueBounds() throws Exception {
		GetValueBoundsCoordinateCommand command = new GetValueBoundsCoordinateCommand(this);
		forEachOddPosition(command);
		return new Bounds(command.lowerBound, command.upperBound);
	}
	
	public long getTotalValue() throws Exception {
		GetTotalValueCommand command = new GetTotalValueCommand();
		forEachValue(command);
		return command.totalValue;
	}
	
	class GetValueBoundsValueCommand implements IntValueCommand {
		public int lowerBound = Integer.MAX_VALUE;
		public int upperBound = Integer.MIN_VALUE;
		
		@Override
		public void execute(int value) {
			if (value > upperBound)
				upperBound = value;
			if (value < lowerBound)
				lowerBound = value;
		}
	}
	
	class GetValueBoundsCoordinateCommand implements PositionCommand {
		public int lowerBound = Integer.MAX_VALUE;
		public int upperBound = Integer.MIN_VALUE;
		private IntGridRegion region;
		
		public GetValueBoundsCoordinateCommand(IntGridRegion region) {
			this.region = region;
		}
		
		@Override
		public void execute(Coordinates coordinates) {
			int value = region.getValue(coordinates);
			if (value > upperBound)
				upperBound = value;
			if (value < lowerBound)
				lowerBound = value;
		}
	}
	
	class GetTotalValueCommand implements IntValueCommand {
		public int totalValue = 0;
		
		@Override
		public void execute(int value) {
			totalValue += value;
		}
	}
}
