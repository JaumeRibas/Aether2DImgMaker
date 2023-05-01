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
package cellularautomata.model;

import cellularautomata.Coordinates;
import cellularautomata.Utils;

public class ParitySignumBondTestIntModel implements IntModel {
	
	private int step = 0;
	private boolean isEvenStep = true;

	@Override
	public int getGridDimension() {
		return 1;
	}

	@Override
	public int getMaxCoordinate(int axis) {
		return 1;
	}

	@Override
	public int getMinCoordinate(int axis) {
		return -1;
	}

	@Override
	public Boolean nextStep() throws Exception {
		isEvenStep = !isEvenStep;
		step++;
		return step == 1;
	}

	@Override
	public Boolean isChanged() {
		return step == 0 ? null : step == 1;
	}

	@Override
	public long getStep() {
		return step;
	}

	@Override
	public String getName() {
		return "OpenPatternTestIntModel";
	}

	@Override
	public String getSubfolderPath() {
		return getName();
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
		Utils.serializeToFile(this, backupPath, backupName);		
	}

	@Override
	public int getFromPosition(Coordinates coordinates) throws Exception {
		return Utils.isEvenPosition(coordinates) == isEvenStep? -1 : 1;
	}	

}
