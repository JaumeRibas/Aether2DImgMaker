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

public class SubareaGrid<G extends Grid2D> implements Grid2D { 

	private G baseGrid;
	private int subareaWidth;
	private int subareaHeight;
	
	public SubareaGrid(G baseGrid, int subareaWidth, int subareaHeight) {
		if (subareaWidth < 1) {
			throw new IllegalArgumentException("Subarea width cannot be smaller than one.");
		}
		if (subareaHeight < 1) {
			throw new IllegalArgumentException("Subarea height cannot be smaller than one.");
		}
		this.baseGrid = baseGrid;
		this.subareaWidth = subareaWidth;
		this.subareaHeight = subareaHeight;
	}
	
	@SuppressWarnings("unchecked")
	public G getSubareaAtPosition(int x, int y) {
		int minX = x * subareaWidth;
		int maxX = minX + subareaWidth - 1;
		int minY = y * subareaHeight;
		int maxY = minY + subareaHeight - 1;
		return (G) baseGrid.subsection(minX, maxX, minY, maxY);
	}

	@Override
	public int getMinX() {
		return baseGrid.getMinX() / subareaWidth;
	}

	@Override
	public int getMaxX() {
		return baseGrid.getMaxX() / subareaWidth;
	}

	@Override
	public int getMinY() {
		return baseGrid.getMinY() / subareaHeight;
	}

	@Override
	public int getMaxY() {
		return baseGrid.getMaxY() / subareaHeight;
	}
	
	@Override
	public int getMinX(int y) {
		int subareaMinY = y * subareaHeight;
		int subareaMaxY = subareaMinY + subareaHeight - 1;
		int subareaMinX = baseGrid.getMinX(subareaMinY);
		for (int baseY = subareaMinY + 1; baseY <= subareaMaxY; baseY++) {
			int minX = baseGrid.getMinX(baseY);
			if (minX < subareaMinX) {
				subareaMinX = minX;
			}
		}
		return subareaMinX / subareaWidth;
	}

	@Override
	public int getMaxX(int y) {
		int subareaMinY = y * subareaHeight;
		int subareaMaxY = subareaMinY + subareaHeight - 1;
		int subareaMaxX = baseGrid.getMaxX(subareaMinY);
		for (int baseY = subareaMinY + 1; baseY <= subareaMaxY; baseY++) {
			int maxX = baseGrid.getMaxX(baseY);
			if (maxX > subareaMaxX) {
				subareaMaxX = maxX;
			}
		}
		return subareaMaxX / subareaWidth;
	}

	@Override
	public int getMinY(int x) {
		int subareaMinX = x * subareaWidth;
		int subareaMaxX = subareaMinX + subareaWidth - 1;
		int subareaMinY = baseGrid.getMinY(subareaMinX);
		for (int baseX = subareaMinX + 1; baseX <= subareaMaxX; baseX++) {
			int minY = baseGrid.getMinY(baseX);
			if (minY < subareaMinY) {
				subareaMinY = minY;
			}
		}
		return subareaMinY / subareaHeight;
	}

	@Override
	public int getMaxY(int x) {
		int subareaMinX = x * subareaWidth;
		int subareaMaxX = subareaMinX + subareaWidth - 1;
		int subareaMaxY = baseGrid.getMaxY(subareaMinX);
		for (int baseX = subareaMinX + 1; baseX <= subareaMaxX; baseX++) {
			int maxY = baseGrid.getMaxY(baseX);
			if (maxY > subareaMaxY) {
				subareaMaxY = maxY;
			}
		}
		return subareaMaxY / subareaHeight;
	}

	@Override
	public G subsection(int minX, int maxX, int minY, int maxY) {
		throw new UnsupportedOperationException();
	}

	public int getRegionWidth() {
		return subareaWidth;
	}

	public int getRegionHeight() {
		return subareaHeight;
	}
}
