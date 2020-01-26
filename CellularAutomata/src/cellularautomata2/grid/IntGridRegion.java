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

	public Bounds getValueBounds() throws Exception {
		GetValueBoundsCommand command = new GetValueBoundsCommand(this);
		forEachPosition(command);
		return new Bounds(command.lowerBound, command.upperBound);
	}
	
	public Bounds getEvenPositionsValueBounds() throws Exception {
		GetValueBoundsCommand command = new GetValueBoundsCommand(this);
		forEachEvenPosition(command);
		return new Bounds(command.lowerBound, command.upperBound);
	}
	
	public Bounds getOddPositionsValueBounds() throws Exception {
		GetValueBoundsCommand command = new GetValueBoundsCommand(this);
		forEachOddPosition(command);
		return new Bounds(command.lowerBound, command.upperBound);
	}
	
	public long getTotalValue() throws Exception {
		GetTotalValueCommand command = new GetTotalValueCommand(this);
		forEachPosition(command);
		return command.totalValue;
	}
	
	class GetValueBoundsCommand implements CoordinateCommand {
		public int lowerBound = Integer.MAX_VALUE;
		public int upperBound = Integer.MIN_VALUE;
		private IntGridRegion region;
		
		public GetValueBoundsCommand(IntGridRegion region) {
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
	
	class GetTotalValueCommand implements CoordinateCommand {
		public int totalValue = 0;
		private IntGridRegion region;
		
		public GetTotalValueCommand(IntGridRegion region) {
			this.region = region;
		}
		
		@Override
		public void execute(Coordinates coordinates) {
			totalValue += region.getValue(coordinates);
		}
	}
}
