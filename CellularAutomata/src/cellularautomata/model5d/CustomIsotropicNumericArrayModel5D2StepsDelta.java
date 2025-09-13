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
package cellularautomata.model5d;

import org.apache.commons.math3.FieldElement;

public class CustomIsotropicNumericArrayModel5D2StepsDelta<Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> implements IsotropicHypercubicNumericModelAsymmetricSection5D<Number_Type> {	

	private final IsotropicHypercubicNumericArrayModelAsymmetricSection5D<Number_Type> model;
	private Number_Type[][][][][] previousStepGrid;
	private Number_Type[][][][][] stepBeforePreviousGrid;
	private int previousStepSize;
	private int stepBeforePreviousSize;

	public CustomIsotropicNumericArrayModel5D2StepsDelta(IsotropicHypercubicNumericArrayModelAsymmetricSection5D<Number_Type> model) throws Exception {
		this.model = model;
		stepBeforePreviousGrid = model.grid.clone();//Clone it because it gets progressively cleared
		stepBeforePreviousSize = model.getSize();
		model.nextStep();
		previousStepGrid = model.grid.clone();
		previousStepSize = model.getSize();
		model.nextStep();
	}
	
	@Override
	public Boolean nextStep() throws Exception {
		stepBeforePreviousGrid = previousStepGrid;
		stepBeforePreviousSize = previousStepSize;
		previousStepGrid = model.grid.clone();
		previousStepSize = model.getSize();
		return model.nextStep();
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
		return Math.min(model.getSize(), stepBeforePreviousSize);
	}

	@Override
	public Number_Type getFromPosition(int v, int w, int x, int y, int z) {
		return model.grid[v][w][x][y][z].subtract(stepBeforePreviousGrid[v][w][x][y][z]);
	}

	@Override
	public String getName() {
		return model.getName();
	}
	
	@Override
	public String getWholeGridSubfolderPath() {
		return model.getWholeGridSubfolderPath() + "/two_steps_delta";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
		model.backUp(backupPath, backupName);
	}
	
}
