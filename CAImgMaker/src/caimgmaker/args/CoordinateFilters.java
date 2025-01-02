/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2025 Jaume Ribas

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
package caimgmaker.args;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class CoordinateFilters {	

	/** The keys are the axes */
	public SortedMap<Integer, Integer> absoluteFilters = new TreeMap<Integer, Integer>();
	
	/** The keys are the axes and the values are arrays of length 2 where the first value is the signum (+1 or -1) and the second is the offset of the key axis from the smallest axis in the map */
	public List<SortedMap<Integer, int[]>> relativeFilterGroups = new ArrayList<SortedMap<Integer, int[]>>();
	
	/** The keys are the axes and the values are arrays of length 2 where the first value is the min and the second is the max. At least one of the two is not null */
	public SortedMap<Integer, Integer[]> minMaxFilters = new TreeMap<Integer, Integer[]>();
	
}