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
package cellularautomata.automata.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import cellularautomata.model2d.IntModel2D;

public class IntValueSlope2D implements IntModel2D {
	
	private static final int maxX = 19;
	private static final int maxY = 11;
	private long step = 0;
	private Boolean changed = null;

	@Override
	public int getFromPosition(int x, int y) {
		return x;
	}
	
	@Override
	public Boolean nextStep() {
		step++;
		changed = false;
		return changed;
	}

	@Override
	public Boolean isChanged() {
		return changed;
	}
	
	@Override
	public int getMaxX() {
		return maxX;
	}
	
	@Override
	public int getMinX() {
		return 0;
	}
	
	@Override
	public int getMaxY() {
		return maxY;
	}
	
	@Override
	public int getMinY() {
		return 0;
	}

	@Override
	public long getStep() {
		return step;
	}

	@Override
	public String getName() {
		return "ValueSlope";
	}

	@Override
	public String getSubfolderPath() {
		return getName() + "/2D";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}
}
