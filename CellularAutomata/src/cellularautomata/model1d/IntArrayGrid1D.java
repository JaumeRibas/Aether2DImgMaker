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
package cellularautomata.model1d;

public class IntArrayGrid1D extends ArrayGrid1D implements IntModel1D {
	
	private int[] values;
	
	/**
	 * Constructs an {@code IntArrayGrid1D} with the specified bounds
	 * 
	 * @param minX the smallest x-coordinate within the region
	 * @param values a 1D int array containing the values of the region
	 */
	public IntArrayGrid1D(int minX, int[] values) {
		super(minX);
		setMaxX(values.length);
		this.values = values;
	}

	@Override
	public int getFromPosition(int x) {
		int i = x - minX;
		return values[i];
	}
}
