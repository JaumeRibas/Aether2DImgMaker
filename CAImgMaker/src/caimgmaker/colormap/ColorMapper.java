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
package caimgmaker.colormap;

import java.math.BigInteger;

import cellularautomata.grid2d.BigIntGrid2D;
import cellularautomata.grid2d.IntGrid2D;
import cellularautomata.grid2d.LongGrid2D;
import cellularautomata.grid2d.ShortGrid2D;

public interface ColorMapper {
	
	public abstract ColorGrid2D getMappedGrid(BigIntGrid2D grid, BigInteger minValue, BigInteger maxValue);
	
	public abstract ColorGrid2D getMappedGrid(LongGrid2D grid, long minValue, long maxValue);
	
	public abstract ColorGrid2D getMappedGrid(IntGrid2D grid, int minValue, int maxValue);
	
	public abstract ColorGrid2D getMappedGrid(ShortGrid2D grid, short minValue, short maxValue);
	
//	/**
//	 * Return the color mapper's name in a format that can be used in file names
//	 * 
//	 * @return the name
//	 */
//	public abstract String getName();
//	
//	/**
//	 * Return the color mapper's name and configuration as a folder and sub-folder(s) path.
//	 * For example: "<Name>/<backgroundValue>/<backgroundColor>"
//	 * 
//	 * @return the path
//	 */
//	public abstract String getSubFolderPath();
}
