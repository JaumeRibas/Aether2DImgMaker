package cellularautomata2.automata;

import cellularautomata2.arrays.Coordinates;
import cellularautomata2.arrays.PositionCommand;

public class Test {

	public static void main(String[] args) {
		testSpreadIntegerValue2AndUpD();
	}
	
	public static void testSpreadIntegerValue1D() {
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
	
	public static void testSpreadIntegerValue2AndUpD() {
		//int counter = 0;
		try {
			int dimension = 2;
			SpreadIntegerValue siv = new SpreadIntegerValue(dimension, 20000, 0);
			int[] crossSectionCoordinatesArray = new int[dimension];
			//crossSectionCoordinatesArray[2] = 1;
			Coordinates crossSectionCoordinates = new Coordinates(crossSectionCoordinatesArray);
			do {
				//counter++;
				System.out.println(siv.toString2DCrossSection(0, 1, crossSectionCoordinates));
				System.out.println("total: " + siv.getTotalValue());
				System.out.println(System.lineSeparator());
				//if (counter == 50) break;
			} while (siv.nextStep());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
