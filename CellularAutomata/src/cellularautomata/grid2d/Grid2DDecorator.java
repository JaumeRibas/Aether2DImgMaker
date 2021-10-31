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

public class Grid2DDecorator<G extends Grid2D> implements Grid2D {
	
	protected G source;
	
	public Grid2DDecorator(G source) {
		this.source = source;
	}

	@Override
	public int getMinX() { return source.getMinX(); }

	@Override
	public int getMaxX() { return source.getMaxX(); }

	@Override
	public int getMinX(int y) { return source.getMinX(y); }

	@Override
	public int getMaxX(int y) { return source.getMaxX(y); }

	@Override
	public int getMinY() { return source.getMinY(); }

	@Override
	public int getMaxY() { return source.getMaxY(); }

	@Override
	public int getMinY(int x) { return source.getMinY(x); }

	@Override
	public int getMaxY(int x) { return source.getMaxY(x); }

}