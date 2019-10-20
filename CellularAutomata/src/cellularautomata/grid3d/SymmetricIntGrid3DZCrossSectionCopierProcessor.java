/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2019 Jaume Ribas

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
package cellularautomata.grid3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cellularautomata.grid.SymmetricGridProcessor;

public class SymmetricIntGrid3DZCrossSectionCopierProcessor implements SymmetricGridProcessor<IntGrid3D> {

	private Map<Integer, List<NonsymmetricIntGrid3DZCrossSectionBlock>> copyRequests = new HashMap<Integer, List<NonsymmetricIntGrid3DZCrossSectionBlock>>();
	private Map<Integer, List<NonsymmetricIntGrid3DZCrossSectionBlock>> copies = new HashMap<Integer, List<NonsymmetricIntGrid3DZCrossSectionBlock>>();
	
	public void requestCopy(int crossSectionZ) {
		copyRequests.put(crossSectionZ, new ArrayList<NonsymmetricIntGrid3DZCrossSectionBlock>());
	}
	
	public ActionableSymmetricIntGrid3DZCrossSectionCopy getCopy(int crossSectionZ) {
		if (copies.containsKey(crossSectionZ)) {
			List<NonsymmetricIntGrid3DZCrossSectionBlock> copyBlocks = copies.get(crossSectionZ);
			if (copyBlocks.size() > 0) {
				return new ActionableSymmetricIntGrid3DZCrossSectionCopy(
						(NonsymmetricIntGrid3DZCrossSectionBlock[])copyBlocks.toArray(new NonsymmetricIntGrid3DZCrossSectionBlock[0]), 
						crossSectionZ);
			}
		}
		return null;
	}	

	@Override
	public void beforeProcessing() throws Exception {
		copies.clear();
	}

	@Override
	public void processGridBlock(IntGrid3D gridBlock) throws Exception {
		for (Integer copyZ : copyRequests.keySet()) {
			if (copyZ >= gridBlock.getMinZ() && copyZ <= gridBlock.getMaxZ()) {
				copyRequests.get(copyZ).add(new NonsymmetricIntGrid3DZCrossSectionBlock(gridBlock, copyZ));
			}
		}
	}

	@Override
	public void afterProcessing() throws Exception {
		for (Integer copyZ : copyRequests.keySet()) {
			copies.put(copyZ, copyRequests.get(copyZ));
		}
		copyRequests.clear();
	}
	
}
