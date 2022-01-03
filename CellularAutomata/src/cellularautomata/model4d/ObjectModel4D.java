/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2022 Jaume Ribas

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
package cellularautomata.model4d;

import java.util.Iterator;

import cellularautomata.model.ObjectModel;
import cellularautomata.model3d.ObjectModel3D;

public interface ObjectModel4D<T> extends Model4D, ObjectModel<T> {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param w the position on the w-axis 
	 * @param x the position on the x-axis
	 * @param y the position on the y-axis
	 * @param z the position on the z-axis
	 * @return the value at (w,x,y,z)
	 * @throws Exception 
	 */
	T getFromPosition(int w, int x, int y, int z) throws Exception;
	
	@Override
	default ObjectModel4D<T> subsection(int minW, int maxW, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new ObjectSubModel4D<T, ObjectModel4D<T>>(this, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default ObjectModel3D<T> crossSectionAtW(int w) {
		return new ObjectModel4DWCrossSection<T, ObjectModel4D<T>>(this, w);
	}
	
	@Override
	default ObjectModel3D<T> crossSectionAtX(int x) {
		return new ObjectModel4DXCrossSection<T, ObjectModel4D<T>>(this, x);
	}
	
	@Override
	default ObjectModel3D<T> crossSectionAtY(int y) {
		return new ObjectModel4DYCrossSection<T, ObjectModel4D<T>>(this, y);
	}
	
	@Override
	default ObjectModel3D<T> crossSectionAtZ(int z) {
		return new ObjectModel4DZCrossSection<T, ObjectModel4D<T>>(this, z);
	}

	@Override
	default Iterator<T> iterator() {
		return new ObjectModel4DIterator<T>(this);
	}

}
