/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2018 Jaume Ribas

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
package cellularautomata.automata;

import cellularautomata.grid.ShortGrid3D;
import cellularautomata.grid.SymmetricShortGrid4D;

public abstract class SymmetricShortCellularAutomaton4D extends SymmetricShortGrid4D implements CellularAutomaton {

	/**
	 * Returns the background value
	 * 
	 * @return the value padding the most part of the grid
	 */
	public abstract short getBackgroundValue();
		
	public ShortGrid3D projected3DEdge() {
		return this.projected3DEdge(getBackgroundValue());
	}
}
