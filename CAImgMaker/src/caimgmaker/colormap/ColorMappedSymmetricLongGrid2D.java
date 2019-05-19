/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2018 Jaume Ribas

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

import java.awt.Color;

import cellularautomata.grid2D.SymmetricLongGrid2D;

public class ColorMappedSymmetricLongGrid2D implements SymmetricColorGrid2D {

	protected SymmetricLongGrid2D source;
	protected LongColorMap colorMap;
	
	public ColorMappedSymmetricLongGrid2D(SymmetricLongGrid2D source, LongColorMap colorMap) {
		this.source = source;
		this.colorMap = colorMap;
	}
		
	@Override
	public int getMinX() {
		return source.getMinX();
	}
	
	@Override
	public int getMinX(int y) {
		return source.getMinX(y);
	}
	
	@Override
	public int getMaxX() {
		return source.getMaxX();
	}
	
	@Override
	public int getMaxX(int y) {
		return source.getMaxX(y);
	}
	
	@Override
	public int getMinY() {
		return source.getMinY();
	}
	
	@Override
	public int getMinY(int x) {
		return source.getMinY(x);
	}
	
	@Override
	public int getMaxY() {
		return source.getMaxY();
	}
	
	@Override
	public int getMaxY(int x) {
		return source.getMaxY(x);
	}

	@Override
	public int getNonSymmetricMinX() {
		return source.getNonSymmetricMinX();
	}

	@Override
	public int getNonSymmetricMaxX() {
		return source.getNonSymmetricMaxX();
	}

	@Override
	public int getNonSymmetricMinY() {
		return source.getNonSymmetricMinY();
	}

	@Override
	public int getNonSymmetricMaxY() {
		return source.getNonSymmetricMaxY();
	}
	
	@Override
	public int getNonSymmetricMinX(int y) {
		return source.getNonSymmetricMinX(y);
	}

	@Override
	public int getNonSymmetricMaxX(int y) {
		return source.getNonSymmetricMaxX(y);
	}

	@Override
	public int getNonSymmetricMinY(int x) {
		return source.getNonSymmetricMinY(x);
	}

	@Override
	public int getNonSymmetricMaxY(int x) {
		return source.getNonSymmetricMaxY(x);
	}

	@Override
	public Color getColorAtNonSymmetricPosition(int x, int y) throws Exception {
		return colorMap.getColor(source.getValueAtNonSymmetricPosition(x, y));
	}
	
	@Override
	public Color getColorAtPosition(int x, int y) throws Exception {
		return colorMap.getColor(source.getValueAtPosition(x, y));
	}

}
