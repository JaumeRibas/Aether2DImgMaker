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
package cellularautomata.grid3d;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cellularautomata.grid.GridProcessor;
import cellularautomata.grid2d.ArrayIntGrid2D;
import cellularautomata.grid2d.IntGrid2D;

public class IntGrid3DXCrossSectionCopierProcessor implements GridProcessor<IntGrid3D> {

	private boolean isProcessing = false; 
	private Set<Integer> copyRequests = new HashSet<Integer>();
	private Map<Integer, IntGrid2D> copies = new HashMap<Integer, IntGrid2D>();
	
	public void requestCopy(int crossSectionX) {
		if (isProcessing) {
			throw new IllegalStateException("Copies cannot be requested while the copier is processing.");
		}
		copyRequests.add(crossSectionX);
	}
	
	public IntGrid2D getCopy(int crossSectionX) {
		return copies.get(crossSectionX);
	}	

	@Override
	public void beforeProcessing() throws Exception {
		isProcessing = true;
		copies.clear();
	}

	@Override
	public void processGridBlock(IntGrid3D gridBlock) throws Exception {
		for (Integer copyX : copyRequests) {
			if (copyX >= gridBlock.getMinX() && copyX <= gridBlock.getMaxX()) {
				int minZ = gridBlock.getMinZAtX(copyX);
				int maxZ = gridBlock.getMaxZAtX(copyX);
				int length = maxZ - minZ + 1;
				int[] localYMinima = new int[length];
				int[][] values = new int [length][];
				for (int z = minZ, i = 0; z <= maxZ; z++, i++) {
					int localMinY = gridBlock.getMinY(copyX, z);
					int localMaxY = gridBlock.getMaxY(copyX, z);
					localYMinima[i] = localMinY;
					int[] slice = new int[localMaxY - localMinY + 1];
					for (int y = localMinY, j = 0; y <= localMaxY; y++, j++) {
						slice[j] = gridBlock.getFromPosition(copyX, y, z);
					}
					values[i] = slice;
				}
				copies.put(copyX, new ArrayIntGrid2D(minZ, localYMinima, values));
			}
		}
	}

	@Override
	public void afterProcessing() throws Exception {
		copyRequests.clear();
		isProcessing = false;
	}
	
}
