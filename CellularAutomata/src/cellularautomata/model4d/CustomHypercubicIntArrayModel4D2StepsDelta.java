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
package cellularautomata.model4d;

public class CustomHypercubicIntArrayModel4D2StepsDelta implements SymmetricLongModel4D, IsotropicHypercubicModel4DA {	

	private int[][][][] previousStepGrid;
	private int[][][][] stepBeforePreviousGrid;
	private IsotropicHypercubicIntArrayModel4DA model;
	private int sizeDifference;

	public CustomHypercubicIntArrayModel4D2StepsDelta(IsotropicHypercubicIntArrayModel4DA model) throws Exception {
		this.model = model;
		stepBeforePreviousGrid = model.grid.clone();
		model.nextStep();
		previousStepGrid = model.grid.clone();
		model.nextStep();
		sizeDifference = model.grid.length - stepBeforePreviousGrid.length;
	}
	
	@Override
	public Boolean nextStep() throws Exception {
		stepBeforePreviousGrid = null;
		stepBeforePreviousGrid = previousStepGrid;
		previousStepGrid = model.grid.clone();//Clone it because it gets progressively cleared
		Boolean result = model.nextStep();
		sizeDifference = model.grid.length - stepBeforePreviousGrid.length;
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
	public int getAsymmetricMaxW() {
		return model.getAsymmetricMaxW() - sizeDifference;
	}
	
	@Override
	public long getFromPosition(int w, int x, int y, int z) {
		if (w < 0) w = -w;
		if (x < 0) x = -x;
		if (y < 0) y = -y;
		if (z < 0) z = -z;
		//sort coordinates
		//TODO faster sorting?
		boolean sorted;
		do {
			sorted = true;
			if (z > y) {
				sorted = false;
				int swp = z;
				z = y;
				y = swp;
			}
			if (y > x) {
				sorted = false;
				int swp = y;
				y = x;
				x = swp;
			}
			if (x > w) {
				sorted = false;
				int swp = x;
				x = w;
				w = swp;
			}
		} while (!sorted);
		return getFromAsymmetricPosition(w, x, y, z);
	}

	@Override
	public long getFromAsymmetricPosition(int w, int x, int y, int z) {
		return (long)model.grid[w][x][y][z] - stepBeforePreviousGrid[w][x][y][z];
	}

	@Override
	public String getName() {
		return model.getName();
	}
	
	@Override
	public String getSubfolderPath() {
		return model.getSubfolderPath() + "/two_steps_delta";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
		model.backUp(backupPath, backupName);
	}
	
}
