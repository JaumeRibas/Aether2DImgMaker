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
package cellularautomata;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class LongInputStreamIterator implements Iterator<Long> {

	private final DataInputStream inputStream;
	private long nextValue;
	private boolean hasNext;
	private boolean invalidated = false;
	private final List<LongInputStreamIterator> iteratorList;
	private static final String INVALIDATED_EXCEPTION_MESSAGE = "This iterator has been invalidated.";
	
	public LongInputStreamIterator(DataInputStream inputStream, List<LongInputStreamIterator> iteratorList) {
		this.inputStream = inputStream;
		this.iteratorList = iteratorList;
		readNext();
	}
	
	private void readNext() {
		try {
			nextValue = inputStream.readLong();
			hasNext = true;
		} catch (IOException e) {
			hasNext = false;
			try {
				inputStream.close();
			} catch (IOException e1) {}
			iteratorList.remove(this);
		}
	}

	@Override
	public boolean hasNext() {
		if (invalidated) {
			throw new IllegalStateException(INVALIDATED_EXCEPTION_MESSAGE);
		}
		return hasNext;
	}

	@Override
	public Long next() {
		if (invalidated) {
			throw new IllegalStateException(INVALIDATED_EXCEPTION_MESSAGE);
		}
		if (hasNext) {
			long valueToReturn = nextValue;
			readNext();
			return valueToReturn;
		} else {
			throw new NoSuchElementException();
		}
	}	
	
	public void invalidate() {
		try {
			inputStream.close();
		} catch (IOException e) {}
		invalidated = true;
	}
}
