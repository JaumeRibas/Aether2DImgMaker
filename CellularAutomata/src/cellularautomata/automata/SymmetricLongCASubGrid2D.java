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

import java.io.FileNotFoundException;
import java.io.IOException;
import cellularautomata.grid2D.LongGrid2D;

public class SymmetricLongCASubGrid2D implements LongCellularAutomaton2D {
	
	private SymmetricLongCellularAutomaton2D ca;
	private LongGrid2D subGrid;
	
	public SymmetricLongCASubGrid2D(SymmetricLongCellularAutomaton2D ca, int minX, int maxX, int minY, int maxY) {
		this.ca = ca;
		this.subGrid = ca.subGrid(minX, maxX, minY, maxY, true);
	}

	@Override
	public long getValueAtPosition(int x, int y) throws Exception {
		return ca.getValueAtPosition(x, y);
	}

	@Override
	public int getMinX() {
		return subGrid.getMinX();
	}

	@Override
	public int getMaxX() {
		return subGrid.getMaxX();
	}

	@Override
	public int getMinY() {
		return subGrid.getMinY();
	}

	@Override
	public int getMaxY() {
		return subGrid.getMaxY();
	}

	@Override
	public boolean nextStep() throws Exception {
		return ca.nextStep();
	}

	@Override
	public long getStep() {
		return ca.getStep();
	}

	@Override
	public String getName() {
		return ca.getName();
	}

	@Override
	public String getSubFolderPath() {
		return ca.getSubFolderPath() + "/minX=" + getMinX() + "_maxX=" + getMaxX() + "_minY=" + getMinY() + "_maxY=" + getMaxY();
	}

	@Override
	public long getBackgroundValue() {
		return ca.getBackgroundValue();
	}

	@Override
	public LongCellularAutomaton2D caSubGrid(int minX, int maxX, int minY, int maxY) {
		return new LongCASubGrid2D(this, minX, maxX, minY, maxY);
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}

}
