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
package cellularautomata.automata;

import cellularautomata.grid3d.IntGrid3D;
import cellularautomata.grid4d.SymmetricIntGrid4D;

public interface SymmetricIntCellularAutomaton4D extends SymmetricIntGrid4D, CellularAutomaton {

	/**
	 * Returns the background value
	 * 
	 * @return the value padding the most part of the grid
	 */
	int getBackgroundValue();
	
	default IntGrid3D projected3DEdge() {
		return this.projected3DEdge(getBackgroundValue());
	}
}
