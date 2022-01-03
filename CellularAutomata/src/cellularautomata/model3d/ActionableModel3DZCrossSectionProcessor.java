/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2021 Jaume Ribas

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
package cellularautomata.model3d;

import cellularautomata.model.ActionableModelTransformerProcessor;
import cellularautomata.model2d.Model2D;

public class ActionableModel3DZCrossSectionProcessor<G1 extends Model3D, G2 extends Model2D> 
	extends ActionableModelTransformerProcessor<G1, G2> {

	private int z;

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public ActionableModel3DZCrossSectionProcessor(int z) {
		this.z = z;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected G2 transformModelBlock(G1 gridBlock) {
		if (z >= gridBlock.getMinZ() && z <= gridBlock.getMaxZ()) {
			return (G2) gridBlock.crossSectionAtZ(z);
		} else {
			return null;
		}
	}
	
}
