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
package cellularautomata.grid2d;

public class EvenOddYSubGrid2D<G extends Grid2D> implements Grid2D {
	
	protected G source;
	protected boolean isEven;
	protected int minX;
	protected int maxX;
	protected int minY;
	protected int maxY;
	
	public EvenOddYSubGrid2D(G source, boolean isEven) {
		int minY = source.getMinY(), maxY = source.getMaxY();
		if (minY == maxY && minY%2 == 0 != isEven) {
			throw new IllegalArgumentException("Y range has no " + (isEven? "even" : "odd") + " coordinate.");
		}
		this.source = source;
		this.isEven = isEven;
		updateBounds();
	}
	
	protected void updateBounds() {
		int sourceMinY = source.getMinY();
		if (sourceMinY%2 == 0 != isEven) {
			sourceMinY++;
		}
		int sourceMaxY = source.getMaxX();
		if (sourceMaxY%2 == 0 != isEven) {
			sourceMaxY--;
		}
		minX = source.getMinX(sourceMinY);
		maxX = source.getMaxX(sourceMinY);
		for (int sourceY = sourceMinY + 2; sourceY <= sourceMaxY; sourceY+=2) {
			int localMinX = source.getMinX(sourceY);
			if (localMinX < minX) {
				minX = localMinX;
			}
			int localMaxX = source.getMaxX(sourceY);
			if (localMaxX > maxX) {
				maxX = localMaxX;
			}
		}
		minY = getY(sourceMinY);
		maxY = getY(sourceMaxY);
	}
	
	public int getSourceY(int y) {
		if (isEven) {
			return 2*y;
		} else {
			return 2*y + 1;
		}
	}
	
	public int getY(int sourceY) {
		if (isEven) {
			return sourceY/2;
		} else {
			return (sourceY - 1)/2;
		}
	}

	@Override
	public int getMinX() {
		return minX;
	}
	
	@Override
	public int getMinX(int y) {
		return source.getMinX(getSourceY(y));
	}

	@Override
	public int getMaxX() {
		return maxX;
	}
	
	@Override
	public int getMaxX(int y) {
		return source.getMaxX(getSourceY(y));
	}

	@Override
	public int getMinY() {
		return minY;
	}
	
	@Override
	public int getMinY(int x) {
		return getY(source.getMinY(x));
	}

	@Override
	public int getMaxY() {
		return maxY;
	}
	
	@Override
	public int getMaxY(int x) {
		return getY(source.getMaxY(x));
	}
	
}
