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

import cellularautomata.model.IsotropicHypercubicModel;

public class IsotropicSquareModel<AsymmetricSection_Type extends IsotropicSquareModelAsymmetricSection> extends IsotropicHypercubicModel<AsymmetricSection_Type> implements Model2D {

	public IsotropicSquareModel(AsymmetricSection_Type asymmetricSection) {
		super(asymmetricSection);
	}
	
	@Override
	public String getXLabel() {
		return asymmetricSection.getXLabel();
	}
	
	@Override
	public String getYLabel() {
		return asymmetricSection.getYLabel();
	}

	@Override
	public int getMinX() {
		return -asymmetricSection.getSize();
	}

	@Override
	public int getMaxX() {
		return asymmetricSection.getSize();
	}

	@Override
	public int getMinY() {
		return -asymmetricSection.getSize();
	}

	@Override
	public int getMaxY() {
		return asymmetricSection.getSize();
	}

}
