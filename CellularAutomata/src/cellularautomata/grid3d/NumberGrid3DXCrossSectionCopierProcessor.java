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

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.FieldElement;

import cellularautomata.grid.GridProcessor;
import cellularautomata.grid2d.ArrayNumberGrid2D;
import cellularautomata.grid2d.NumberGrid2D;

public class NumberGrid3DXCrossSectionCopierProcessor<T extends FieldElement<T> & Comparable<T>> implements GridProcessor<NumberGrid3D<T>> {

	private boolean isProcessing = false; 
	private Set<Integer> copyRequests = new HashSet<Integer>();
	private Map<Integer, NumberGrid2D<T>> copies = new HashMap<Integer, NumberGrid2D<T>>();
	private Class<T> numberClass;
	private Class<T[]> numberArrayClass;
	
	public void requestCopy(int crossSectionX) {
		if (isProcessing) {
			throw new IllegalStateException("Copies cannot be requested while the copier is processing.");
		}
		copyRequests.add(crossSectionX);
	}
	
	public NumberGrid2D<T> getCopy(int crossSectionX) {
		return copies.get(crossSectionX);
	}	

	@Override
	public void beforeProcessing() throws Exception {
		isProcessing = true;
		copies.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void processGridBlock(NumberGrid3D<T> gridBlock) throws Exception {
		if (!copyRequests.isEmpty() && this.numberClass == null) {
			int x = gridBlock.getMinX();
			int y = gridBlock.getMinYAtX(x);
			T sampleNumber = gridBlock.getFromPosition(x, y, gridBlock.getMinZ(x, y));
			this.numberClass = (Class<T>) sampleNumber.getClass();
			this.numberArrayClass = (Class<T[]>) Array.newInstance(numberClass, 0).getClass();
		}
		for (Integer copyX : copyRequests) {
			if (copyX >= gridBlock.getMinX() && copyX <= gridBlock.getMaxX()) {
				int minZ = gridBlock.getMinZAtX(copyX);
				int maxZ = gridBlock.getMaxZAtX(copyX);
				int length = maxZ - minZ + 1;
				int[] localYMinima = new int[length];
				T[][] values = (T[][]) Array.newInstance(numberArrayClass, length);
				for (int z = minZ, i = 0; z <= maxZ; z++, i++) {
					int localMinY = gridBlock.getMinY(copyX, z);
					int localMaxY = gridBlock.getMaxY(copyX, z);
					localYMinima[i] = localMinY;
					T[] slice = (T[]) Array.newInstance(numberClass, localMaxY - localMinY + 1);
					for (int y = localMinY, j = 0; y <= localMaxY; y++, j++) {
						slice[j] = gridBlock.getFromPosition(copyX, y, z);
					}
					values[i] = slice;
				}
				copies.put(copyX, new ArrayNumberGrid2D<T>(minZ, localYMinima, values));
			}
		}
	}

	@Override
	public void afterProcessing() throws Exception {
		copyRequests.clear();
		isProcessing = false;
	}
	
}
