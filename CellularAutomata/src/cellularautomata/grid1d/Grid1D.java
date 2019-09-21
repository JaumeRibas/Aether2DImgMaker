/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2019 Jaume Ribas

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
package cellularautomata.grid1d;

import cellularautomata.grid.Grid;

public interface Grid1D extends Grid {
	
	/**
	 * Returns the smallest x-coordinate
	 * 
	 * @return the smallest x
	 */
	int getMinX();
	
	/**
	 * Returns the largest x-coordinate
	 * 
	 * @return the largest x
	 */
	int getMaxX();
	
	/**
	 * Returns a decorated {@link Grid1D} with the passed bounds.
	 * 
	 * @param minX
	 * @param maxX
	 * @return a {@link Grid1D} decorating the current grid 
	 */
	Grid1D subGrid(int minX, int maxX);
}
