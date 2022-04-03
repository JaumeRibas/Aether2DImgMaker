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
package cellularautomata.model2d;

public class SubareaModel<Source_Type extends Model2D> implements Model2D { 

	private Source_Type source;
	private int subareaWidth;
	private int subareaHeight;
	
	public SubareaModel(Source_Type baseModel, int subareaWidth, int subareaHeight) {
		if (subareaWidth < 1) {
			throw new IllegalArgumentException("Subarea width cannot be smaller than one.");
		}
		if (subareaHeight < 1) {
			throw new IllegalArgumentException("Subarea height cannot be smaller than one.");
		}
		this.source = baseModel;
		this.subareaWidth = subareaWidth;
		this.subareaHeight = subareaHeight;
	}
	
	@SuppressWarnings("unchecked")
	public Source_Type getSubareaAtPosition(int x, int y) {
		int minX = x * subareaWidth;
		int maxX = minX + subareaWidth - 1;
		int minY = y * subareaHeight;
		int maxY = minY + subareaHeight - 1;
		return (Source_Type) source.subsection(minX, maxX, minY, maxY);
	}

	@Override
	public int getMinX() {
		return source.getMinX() / subareaWidth;
	}

	@Override
	public int getMaxX() {
		return source.getMaxX() / subareaWidth;
	}

	@Override
	public int getMinY() {
		return source.getMinY() / subareaHeight;
	}

	@Override
	public int getMaxY() {
		return source.getMaxY() / subareaHeight;
	}
	
	@Override
	public int getMinX(int y) {
		int subareaMinY = y * subareaHeight;
		int subareaMaxY = subareaMinY + subareaHeight - 1;
		int subareaMinX = source.getMinX(subareaMinY);
		for (int baseY = subareaMinY + 1; baseY <= subareaMaxY; baseY++) {
			int minX = source.getMinX(baseY);
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
		int subareaMaxX = source.getMaxX(subareaMinY);
		for (int baseY = subareaMinY + 1; baseY <= subareaMaxY; baseY++) {
			int maxX = source.getMaxX(baseY);
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
		int subareaMinY = source.getMinY(subareaMinX);
		for (int baseX = subareaMinX + 1; baseX <= subareaMaxX; baseX++) {
			int minY = source.getMinY(baseX);
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
		int subareaMaxY = source.getMaxY(subareaMinX);
		for (int baseX = subareaMinX + 1; baseX <= subareaMaxX; baseX++) {
			int maxY = source.getMaxY(baseX);
			if (maxY > subareaMaxY) {
				subareaMaxY = maxY;
			}
		}
		return subareaMaxY / subareaHeight;
	}

	@Override
	public Source_Type subsection(int minX, int maxX, int minY, int maxY) {
		throw new UnsupportedOperationException();
	}

	public int getRegionWidth() {
		return subareaWidth;
	}

	public int getRegionHeight() {
		return subareaHeight;
	}
}
