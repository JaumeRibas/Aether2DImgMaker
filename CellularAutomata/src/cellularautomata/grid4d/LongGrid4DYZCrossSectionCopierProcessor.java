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
package cellularautomata.grid4d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cellularautomata.grid.GridProcessor;
import cellularautomata.grid2d.ArrayLongGrid2D;
import cellularautomata.grid2d.LongGrid2D;

public class LongGrid4DYZCrossSectionCopierProcessor implements GridProcessor<LongGrid4D> {

	private boolean isProcessing = false; 
	private Map<String, CopyData> copyRequests = new HashMap<String, CopyData>();
	private Map<String, CopyData> copies = new HashMap<String, CopyData>();
	
	public void requestCopy(int crossSectionY, int crossSectionZ) {
		if (isProcessing) {
			throw new UnsupportedOperationException("Copies cannot be requested while the copier is processing.");
		}
		copyRequests.put(crossSectionY + "," + crossSectionZ, new CopyData(crossSectionY, crossSectionZ));
	}
	
	public LongGrid2D getCopy(int crossSectionY, int crossSectionZ) {
		String key = crossSectionY + "," + crossSectionZ;
		if (copies.containsKey(key)) {
			CopyData copyData = copies.get(key);
			int valuesSize = copyData.values.size();
			if (valuesSize > 0) {
				long[][] values = new long[valuesSize][];
				int i = 0;
				for (long[] slice : copyData.values) {
					values[i] = slice;
					i++;
				}
				int[] localYMinima = new int[valuesSize];
				i = 0;
				for (Integer localMinY : copyData.localYMinima) {
					localYMinima[i] = localMinY;
					i++;
				}
				return new ArrayLongGrid2D(copyData.minX, localYMinima, values);
			}
		}
		return null;
	}	

	@Override
	public void beforeProcessing() throws Exception {
		isProcessing = true;
		copies.clear();
	}

	@Override
	public void processGridBlock(LongGrid4D gridBlock) throws Exception {
		for (CopyData copyData : copyRequests.values()) {
			int copyY = copyData.y;
			int copyZ = copyData.z;
			if (copyZ >= gridBlock.getMinZ() && copyZ <= gridBlock.getMaxZ()
					&& copyY >= gridBlock.getMinYAtZ(copyZ) && copyY <= gridBlock.getMaxYAtZ(copyZ)) {
				if (copyData.values.size() == 0) {
					copyData.minX = gridBlock.getMinWAtYZ(copyY, copyZ);
				}
				int maxW = gridBlock.getMaxWAtYZ(copyY, copyZ);
				for (int w = gridBlock.getMinWAtYZ(copyY, copyZ); w <= maxW; w++) {
					int localMinX = gridBlock.getMinX(w, copyY, copyZ);
					int localMaxX = gridBlock.getMaxX(w, copyY, copyZ);
					copyData.localYMinima.add(localMinX);
					long[] slice = new long[localMaxX - localMinX + 1];
					for (int x = localMinX, i = 0; x <= localMaxX; x++, i++) {
						slice[i] = gridBlock.getFromPosition(w, x, copyY, copyZ);
					}
					copyData.values.add(slice);
				}
			}
		}
	}

	@Override
	public void afterProcessing() throws Exception {
		for (String copyData : copyRequests.keySet()) {
			copies.put(copyData, copyRequests.get(copyData));
		}
		copyRequests.clear();
		isProcessing = false;
	}
	
	private class CopyData {
		CopyData(int y, int z) {
			this.y = z;
			this.z = z;
			values = new ArrayList<long[]>();
			localYMinima = new ArrayList<Integer>();
		}
		int y;
		int z;
		int minX;
		List<long[]> values;
		List<Integer> localYMinima;
	}
	
}
