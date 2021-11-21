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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cellularautomata.grid.GridProcessor;
import cellularautomata.grid2d.ArrayLongGrid2D;
import cellularautomata.grid2d.LongGrid2D;

public class LongGrid3DYCrossSectionCopierProcessor implements GridProcessor<LongGrid3D> {

	private boolean isProcessing = false; 
	private Map<Integer, CopyData> copyRequests = new HashMap<Integer, CopyData>();
	private Map<Integer, CopyData> copies = new HashMap<Integer, CopyData>();
	
	public void requestCopy(int crossSectionY) {
		if (isProcessing) {
			throw new IllegalStateException("Copies cannot be requested while the copier is processing.");
		}
		copyRequests.put(crossSectionY, new CopyData(crossSectionY));
	}
	
	public LongGrid2D getCopy(int crossSectionY) {
		if (copies.containsKey(crossSectionY)) {
			CopyData copyData = copies.get(crossSectionY);
			int valuesSize = copyData.values.size();
			if (valuesSize > 0) {
				long[][] values = new long[valuesSize][];
				int i = 0;
				for (long[] slice : copyData.values) {
					values[i] = slice;
					i++;
				}
				int[] localZMinima = new int[valuesSize];
				i = 0;
				for (Integer localMinZ : copyData.localZMinima) {
					localZMinima[i] = localMinZ;
					i++;
				}
				return new ArrayLongGrid2D(copyData.minX, localZMinima, values);
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
	public void processGridBlock(LongGrid3D gridBlock) throws Exception {
		for (CopyData copyData : copyRequests.values()) {
			int copyY = copyData.y;
			if (copyY >= gridBlock.getMinY() && copyY <= gridBlock.getMaxY()) {
				if (copyData.values.size() == 0) {
					copyData.minX = gridBlock.getMinXAtY(copyY);
				}
				int maxX = gridBlock.getMaxXAtY(copyY);
				for (int x = gridBlock.getMinXAtY(copyY); x <= maxX; x++) {
					int localMinZ = gridBlock.getMinZ(x, copyY);
					int localMaxZ = gridBlock.getMaxZ(x, copyY);
					copyData.localZMinima.add(localMinZ);
					long[] slice = new long[localMaxZ - localMinZ + 1];
					for (int z = localMinZ, i = 0; z <= localMaxZ; z++, i++) {
						slice[i] = gridBlock.getFromPosition(x, copyY, z);
					}
					copyData.values.add(slice);
				}
			}
		}
	}

	@Override
	public void afterProcessing() throws Exception {
		for (Integer copyData : copyRequests.keySet()) {
			copies.put(copyData, copyRequests.get(copyData));
		}
		copyRequests.clear();
		isProcessing = false;
	}
	
	private class CopyData {
		CopyData(int y) {
			this.y = y;
			values = new ArrayList<long[]>();
			localZMinima = new ArrayList<Integer>();
		}
		int y;
		int minX;
		List<long[]> values;
		List<Integer> localZMinima;
	}
	
}
