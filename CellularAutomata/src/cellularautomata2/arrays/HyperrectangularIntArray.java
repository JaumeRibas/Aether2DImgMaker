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
package cellularautomata2.arrays;

public class HyperrectangularIntArray extends HyperrectangularArray implements MultidimensionalIntArray {

	private int[] values;
	
	public HyperrectangularIntArray(int[] sizes) {
		super(sizes);
		values = new int[(int) getVolume()];
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
	public void setAll(int value) {
		for (int i = 0; i < values.length; i++) {
			values[i] = value;
		}
	}

	@Override
	public int addAndGet(Coordinates indexes, int value) {
		int index = getInternalArrayIndex(indexes);
		values[index] += value;
		return values[index];
	}
	
	public void padEdges(int edgeWidth, int value) {
		forEachEdgeIndex(edgeWidth, new PositionCommand() {		
			@Override
			public void execute(Coordinates coordinates) {
				set(coordinates, value);
			}
		});
	}

	@Override
	public void forEachValue(IntValueCommand command) {
		for (int i = 0; i < values.length; i++) {
			command.execute(values[i]);
		}
	}
	
}
