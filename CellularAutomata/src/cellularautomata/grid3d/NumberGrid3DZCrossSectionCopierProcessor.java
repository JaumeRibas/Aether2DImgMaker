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

import org.apache.commons.math3.FieldElement;

import cellularautomata.grid.GridProcessor;
import cellularautomata.grid2d.ArrayNumberGrid2D;
import cellularautomata.grid2d.NumberGrid2D;

public class NumberGrid3DZCrossSectionCopierProcessor<T extends FieldElement<T> & Comparable<T>> implements GridProcessor<NumberGrid3D<T>> {

	private boolean isProcessing = false; 
	private Map<Integer, CopyData> copyRequests = new HashMap<Integer, CopyData>();
	private Map<Integer, CopyData> copies = new HashMap<Integer, CopyData>();
	
	public void requestCopy(int crossSectionZ) {
		if (isProcessing) {
			throw new UnsupportedOperationException("Copies cannot be requested while the copier is processing.");
		}
		copyRequests.put(crossSectionZ, new CopyData(crossSectionZ));
	}
	
	public NumberGrid2D<T> getCopy(int crossSectionZ) {
		if (copies.containsKey(crossSectionZ)) {
			CopyData copyData = copies.get(crossSectionZ);
			int valuesSize = copyData.values.size();
			if (valuesSize > 0) {
				Object[][] values = new Object[valuesSize][];
				int i = 0;
				for (Object[] slice : copyData.values) {
					values[i] = slice;
					i++;
				}
				int[] localYMinima = new int[valuesSize];
				i = 0;
				for (Integer localMinY : copyData.localYMinima) {
					localYMinima[i] = localMinY;
					i++;
				}
				return new ArrayNumberGrid2D<T>(copyData.minX, localYMinima, values);
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
	public void processGridBlock(NumberGrid3D<T> gridBlock) throws Exception {
		for (CopyData copyData : copyRequests.values()) {
			int copyZ = copyData.z;
			if (copyZ >= gridBlock.getMinZ() && copyZ <= gridBlock.getMaxZ()) {
				if (copyData.values.size() == 0) {
					copyData.minX = gridBlock.getMinXAtZ(copyZ);
				}
				int maxX = gridBlock.getMaxXAtZ(copyZ);
				for (int x = gridBlock.getMinXAtZ(copyZ); x <= maxX; x++) {
					int localMinY = gridBlock.getMinY(x, copyZ);
					int localMaxY = gridBlock.getMaxY(x, copyZ);
					copyData.localYMinima.add(localMinY);
					Object[] slice = new Object[localMaxY - localMinY + 1];
					for (int y = localMinY, i = 0; y <= localMaxY; y++, i++) {
						slice[i] = gridBlock.getFromPosition(x, y, copyZ);
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
		CopyData(int z) {
			this.z = z;
			values = new ArrayList<Object[]>();
			localYMinima = new ArrayList<Integer>();
		}
		int z;
		int minX;
		List<Object[]> values;
		List<Integer> localYMinima;
	}
	
}
