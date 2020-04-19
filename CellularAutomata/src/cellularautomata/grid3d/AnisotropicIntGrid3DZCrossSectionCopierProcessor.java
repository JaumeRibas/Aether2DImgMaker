/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2020 Jaume Ribas

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

import cellularautomata.grid.GridProcessor;

public class AnisotropicIntGrid3DZCrossSectionCopierProcessor implements GridProcessor<IntGrid3D> {

	private Map<Integer, List<AnisotropicIntGrid3DZCrossSectionBlock>> copyRequests = new HashMap<Integer, List<AnisotropicIntGrid3DZCrossSectionBlock>>();
	private Map<Integer, List<AnisotropicIntGrid3DZCrossSectionBlock>> copies = new HashMap<Integer, List<AnisotropicIntGrid3DZCrossSectionBlock>>();
	
	public void requestCopy(int crossSectionZ) {
		copyRequests.put(crossSectionZ, new ArrayList<AnisotropicIntGrid3DZCrossSectionBlock>());
	}
	
	public ActionableAnisotropicIntGrid3DZCrossSectionCopy getCopy(int crossSectionZ) {
		if (copies.containsKey(crossSectionZ)) {
			List<AnisotropicIntGrid3DZCrossSectionBlock> copyBlocks = copies.get(crossSectionZ);
			if (copyBlocks.size() > 0) {
				return new ActionableAnisotropicIntGrid3DZCrossSectionCopy(
						(AnisotropicIntGrid3DZCrossSectionBlock[])copyBlocks.toArray(new AnisotropicIntGrid3DZCrossSectionBlock[0]), 
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
				copyRequests.get(copyZ).add(new AnisotropicIntGrid3DZCrossSectionBlock(gridBlock, copyZ));
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
