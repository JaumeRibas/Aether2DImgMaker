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

public class IsotropicBooleanArrayModel1DDelta implements SymmetricBooleanModel1D, IsotropicModel1DA {	

	private boolean[] oldGrid;
	private IsotropicBooleanArrayModel1DA model;
	private int sizeDifference;

	public IsotropicBooleanArrayModel1DDelta(IsotropicBooleanArrayModel1DA model) throws Exception {
		this.model = model;
		nextStep();
	}
	
	@Override
	public Boolean nextStep() throws Exception {
		oldGrid = model.grid;
		Boolean result = model.nextStep();
		sizeDifference = model.grid.length - oldGrid.length;
		return result;
	}

	@Override
	public Boolean isChanged() {
		return model.isChanged();
	}

	@Override
	public long getStep() {
		return model.getStep();
	}

	@Override
	public int getAsymmetricMaxX() {
		return model.getAsymmetricMaxX() - sizeDifference;
	}
	
	@Override
	public boolean getFromPosition(int x) {	
		if (x < 0) x = -x;
		return getFromAsymmetricPosition(x);
	}

	@Override
	public boolean getFromAsymmetricPosition(int x) {
		return model.grid[x] != oldGrid[x];
	}

	@Override
	public String getName() {
		return model.getName();
	}
	
	@Override
	public String getSubfolderPath() {
		return model.getSubfolderPath() + "/delta";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
		model.backUp(backupPath, backupName);
	}
	
}
