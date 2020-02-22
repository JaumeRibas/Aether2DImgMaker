package cellularautomata2.automata;

import cellularautomata2.arrays.Coordinates;
import cellularautomata2.arrays.PositionCommand;

public class Test {

	public static void main(String[] args) {
		testSpreadIntegerValue();
	}
	
	public static void testSpreadIntegerValue() {
		int counter = 0;
		try {
			SpreadIntegerValue siv = new SpreadIntegerValue(1, 0, 9);
			do {
				counter++;
				siv.forEachPosition(new PositionCommand() {
					
					@Override
					public void execute(Coordinates coordinates) {
						System.out.print(siv.getValue(coordinates) + " ");
					}
				});
				System.out.println();
				System.out.println("total: " + siv.getTotalValue());
				if (counter == 50) break;
			} while (siv.nextStep());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
