package cellularautomata;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class LongInputStreamIterator implements Iterator<Long> {

	private DataInputStream inputStream;
	private long nextValue;
	private boolean hasNext;
	private boolean invalidated = false;
	private List<LongInputStreamIterator> iteratorList;
	private static final String invalidatedExceptionMessage = "This iterator has been invalidated.";
	
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
			throw new IllegalStateException(invalidatedExceptionMessage);
		}
		return hasNext;
	}

	@Override
	public Long next() {
		if (invalidated) {
			throw new IllegalStateException(invalidatedExceptionMessage);
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
