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

import org.apache.commons.math3.FieldElement;

public class IsotropicNumericArrayModel1D2StepsDelta<Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> implements SymmetricNumericModel1D<Number_Type>, IsotropicModel1DA {	

	private Number_Type[] previousStepGrid;
	private Number_Type[] stepBeforePreviousGrid;
	private IsotropicNumericArrayModel1DA<Number_Type> model;
	private int sizeDifference;

	public IsotropicNumericArrayModel1D2StepsDelta(IsotropicNumericArrayModel1DA<Number_Type> model) throws Exception {
		this.model = model;
		stepBeforePreviousGrid = model.grid;
		model.nextStep();
		previousStepGrid = model.grid;
		model.nextStep();
		sizeDifference = model.grid.length - stepBeforePreviousGrid.length;
	}
	
	@Override
	public Boolean nextStep() throws Exception {
		stepBeforePreviousGrid = previousStepGrid;
		previousStepGrid = model.grid;
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
	public int getAsymmetricMaxX() {
		return model.getAsymmetricMaxX() - sizeDifference;
	}
	
	@Override
	public Number_Type getFromPosition(int x) {	
		if (x < 0) x = -x;
		return getFromAsymmetricPosition(x);
	}

	@Override
	public Number_Type getFromAsymmetricPosition(int x) {
		return model.grid[x].subtract(stepBeforePreviousGrid[x]);
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