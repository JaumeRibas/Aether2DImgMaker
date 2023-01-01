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
package cellularautomata.model2d;

import java.io.Serializable;

public class ArrayObjectGrid2D<Object_Type> extends ArrayGrid2D implements ObjectModel2D<Object_Type>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6271083205749084067L;
	protected Object_Type[][] values;
	
	/**
	 * Constructs an {@code ArrayObjectGrid2D} with the specified bounds
	 * 
	 * @param minX the smallest x-coordinate within the region
	 * @param localYMinima an array of the smallest y-coordinates at each x-coordinate of the region. Beginning at {@code minX}.
	 * @param values a 2D array containing the values of the region
	 */
	public ArrayObjectGrid2D(int minX, int[] localYMinima, Object_Type[][] values) {
		super(minX, localYMinima);
		if (localYMinima.length != values.length) {
			throw new IllegalArgumentException("Local y minima's length must be equal to values' length.");
		}
		this.values = values;
		int[] localYMaxima = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			long longLocalMaxY = (long)values[i].length + localYMinima[i] - 1;
			if (longLocalMaxY > Integer.MAX_VALUE) {
				throw new IllegalArgumentException(
						"Resulting max y at index " + i + " (" + longLocalMaxY + ") is greater than supported max (" + Integer.MAX_VALUE + ").");
			}
			localYMaxima[i] = (int)longLocalMaxY;
		}
		getYBounds(localYMaxima);
	}
	
	@Override
	public int getMinX(int y) {
		if (y < minY || y > maxY) throw new IllegalArgumentException("The coordinate is out of bounds.");
		int localMinX = minX;
		int edgeMinY = localYMinima[0];
		int edgeMaxY = values[0].length + edgeMinY - 1;
		if (y > edgeMaxY) {
			int i = 1;
			localMinX++;
			while (y > values[i].length + localYMinima[i] - 1) {
				localMinX++;
				i++;
			}
		} else if (y < edgeMinY) {
			int i = 1;
			localMinX++;
			while (y < localYMinima[i]) {
				localMinX++;
				i++;
			}
		}
		return localMinX;
	}
	
	@Override
	public int getMaxX(int y) {
		if (y < minY || y > maxY) throw new IllegalArgumentException("The coordinate is out of bounds.");
		int localMaxX = maxX;
		int i = values.length - 1;
		int edgeMinY = localYMinima[i];
		int edgeMaxY = values[i].length + edgeMinY - 1;
		if (y > edgeMaxY) {
			i--;
			localMaxX--;
			while (y > values[i].length + localYMinima[i] - 1) {
				localMaxX--;
				i--;
			}
		} else if (y < edgeMinY) {
			i--;
			localMaxX--;
			while (y < localYMinima[i]) {
				localMaxX--;
				i--;
			}
		}
		return localMaxX;
	}
	
	@Override
	public int getMaxY(int x) {
		int index = x - minX;
		return values[index].length + localYMinima[index] - 1;
	}

	@Override
	public Object_Type getFromPosition(int x, int y) {
		int i = x - minX;
		int j = y - localYMinima[i];
		return values[i][j];
	}
}
