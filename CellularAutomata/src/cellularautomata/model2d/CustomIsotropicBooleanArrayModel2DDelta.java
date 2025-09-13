/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2025 Jaume Ribas

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

public class CustomIsotropicBooleanArrayModel2DDelta implements IsotropicSquareBooleanModelAsymmetricSection {	

	private final IsotropicSquareBooleanArrayModelAsymmetricSection model;
	private boolean[][] oldGrid;
	private int size;

	public CustomIsotropicBooleanArrayModel2DDelta(IsotropicSquareBooleanArrayModelAsymmetricSection model) throws Exception {
		this.model = model;
		nextStep();
	}
	
	@Override
	public Boolean nextStep() throws Exception {
		oldGrid = null;
		oldGrid = model.grid/*.clone()*/;//No need to clone it because it's used with the toppling alternation compliance class
		int oldSize = model.getSize();
		Boolean result = model.nextStep();
		int newSize = model.getSize();
		size = Math.min(oldSize, newSize);
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
	public int getSize() {
		return size;
	}

	@Override
	public boolean getFromPosition(int x, int y) {
		return model.grid[x][y] != oldGrid[x][y];
	}

	@Override
	public String getName() {
		return model.getName();
	}
	
	@Override
	public String getWholeGridSubfolderPath() {
		return model.getWholeGridSubfolderPath() + "/delta";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
		model.backUp(backupPath, backupName);
	}
	
}
