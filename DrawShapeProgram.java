import swiftbot.SwiftBotAPI;
import swiftbot.Button;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DrawShapeProgram {
	static SwiftBotAPI bot;
	static Scanner scanner = new Scanner(System.in);

	static List<String> ShapeData = new ArrayList<>();
	static Map<String, Integer> NoofShape = new HashMap<>();
	static List<Long> TimeTaken = new ArrayList<>();
	static double LargestArea = 0;
	static String LargestShape = "";
	private static final String LogFilePath = "shape_summary_log.txt";

	static class Shape {
		String type;
		int[] sides;

		Shape(String type, int[] dimensions) {
			this.type = type;
			this.sides = dimensions;
		}

		void displayShape() {
			if (type.equals("S")) {
				System.out.println("Square: " + sides[0] + " cm");
			} else if (type.equals("T")) {
				System.out.println("Triangle: " + sides[0] + ", " + sides[1] + ", " + sides[2] + " cm");
			}
		}
	}

	public static void main(String[] args) {

		try {
			bot = new SwiftBotAPI();
			bot.enableButton(Button.X, () -> {
				System.out.println("Exit button (X) pressed. Exiting...");
				GenerateSummary();
				System.exit(2);
			});

			while (true) {
				System.out.println("--------------------------------------------------");
				System.out.println("          SWIFTBOT DRAW SHAPE PROGRAM        ");
				System.out.println("--------------------------------------------------");
				System.out.println("  Please select one of the methods to enter data  ");
				System.out.println("  Press [1] if you want to scan a QR  ");
				System.out.println("  Press [2] if you want to type in the data  ");
				System.out.println("  Press [X] at any time to terminate the program ");
				System.out.println("==================================================");
				String Method = scanner.nextLine();
				String InputData = "";

				if (Method.equals("1")) {
					InputData = ScanQRCode();
				} else if (Method.equals("2")) {
					System.out.println("PLEASE ENTER THE DATA ");
					System.out.println("Follow this format");
					System.out.println(" S-XX or T-XX-YY-ZZ ~~ If multiple shapes separate it by ' & '");
					InputData = scanner.nextLine();
				} else if (Method.equalsIgnoreCase("X")) {
					SafeExit();
					return;
				} else {
					System.out.println(" -- unrecognized input --");
					System.out.println("----Please ensure it contains valid shape data (e.g., S-16 or T-16-30-24)----");
					System.out.println("--- TRY AGAIN ---");
					continue;
				}

				List<Shape> shapes = ProcessShapes(InputData);
				if (shapes.isEmpty()) {
					System.out.println("Could not recognize a valid shape");
					continue;
				}

				DrawMultipleShapes(shapes);
				boolean InSubMenu = true;
				while (InSubMenu) {
					System.out.println("---------- DONE ----------");
					System.out.println("Now press [1] to same Redraw shapes");
					System.out.println("To draw a new shape press [2]");
					System.out.println("To terminate press [X]");

					String choice = scanner.nextLine();
					if (choice.equals("1")) {
						DrawMultipleShapes(shapes);
					} else if (choice.equals("2")) {
						InSubMenu = false; // Go back to shape input
					} else if (choice.equals("X")) {
						SafeExit();
						return;
					} else {
						System.out.println("Could not recognize a valid input. Please enter 1, 2, or 3.");
					}
				}
			}
		} catch (Exception e) {
			System.out.println(" ----------UNEXPECTED ERROR---------- ");
		}
	}

	public static void SafeExit() {
		System.out.println("----- PROGRAM TERMINATED ----------");
		scanner.close();
		GenerateSummary();
		System.exit(0);
		// Runtime.getRuntime().halt(0);
	}

	public static String ScanQRCode() {
		long startTime = System.currentTimeMillis();
		long endTime = startTime + 10000; // 10 seconds timeout

		System.out.println(" Place QR in front of camera");

		try {
			while (System.currentTimeMillis() < endTime) {
				BufferedImage img = bot.getQRImage();
				String qrData = bot.decodeQRImage(img);

				if (!qrData.isEmpty()) {
					System.out.println(" QR DETECTED ");
					System.out.println("Decoded message: " + qrData);
					return qrData;
				} else {
					System.out.println("----- NO QR DETECTED -----");
				}

				Thread.sleep(1000); // break fo 10 sec
			}

			System.out.println("----- NOT ABLE TO DETACT A QR -----");
			System.out.println(" ------- Use other methods -------    ");
			return "";

		} catch (Exception e) {
			System.out.println("----- NOT ABLE TO DETACT A QR -----");
			System.out.println("------- Use other methods -------  ");
			e.printStackTrace();
			return "";
		}
	}

	public static List<Shape> ProcessShapes(String data) {
		List<Shape> Shapes = new ArrayList<>();
		String[] ShapeDataArray = data.split("&"); // it seperate data by &

		for (int i = 0; i < ShapeDataArray.length; i++) { // loop for over ever shape
			String shapeData = ShapeDataArray[i].trim();
			try {
				if (shapeData.startsWith("S-")) {
					int SL = Integer.parseInt(shapeData.substring(2).trim());
					if (SL >= 15 && SL <= 85) {
						Shapes.add(new Shape("S", new int[] { SL }));
					} else {
						System.out.println("Lenght out of range (must be 15-85 cm)");
					}
				} else if (shapeData.startsWith("T-")) {
					String[] parts = shapeData.substring(2).split("-"); // it seperate data by -
					if (parts.length == 3) {
						int T1 = Integer.parseInt(parts[0].trim());
						int T2 = Integer.parseInt(parts[1].trim());
						int T3 = Integer.parseInt(parts[2].trim());

						if (T1 + T2 > T3 && T1 + T3 > T2 && T2 + T3 > T1) {
							Shapes.add(new Shape("T", new int[] { T1, T2, T3 }));
						} else {
							System.out.println("Can not form a triangle with these lenght");
						}
					}
				} else {
					System.out.println("Invalid format: " + shapeData);
				}
			} catch (NumberFormatException e) {
				System.out.println("INPUT ERROR: " + shapeData);
			}
		}
		return Shapes;
	}

	public static void DrawMultipleShapes(List<Shape> shapes) {
		for (int i = 0; i < shapes.size(); i++) {
			Shape shape = shapes.get(i);
			long StartTime = System.currentTimeMillis();
			shape.displayShape();

			if (shape.type.equals("S")) {
				DrawSquare(shape.sides[0]);
				UpdateStats("Square", shape.sides[0] * shape.sides[0], StartTime);// update data for summary
			} else if (shape.type.equals("T")) {
				DrawTriangle(shape.sides[0], shape.sides[1], shape.sides[2]);
				double area = CalculateTriangleArea(shape.sides[0], shape.sides[1], shape.sides[2]);
				UpdateStats("Triangle", area, StartTime); // update data for summary
			}

			if (i < shapes.size() - 1) {
				System.out.println("== MOVING BACK ==");

				bot.move(-45, -50, 940); // MOVE 15CM BACK
				bot.stopMove();
			}
		}
	}

	public static void FlashRedLight(int times) {
		for (int i = 0; i < times; i++) {
			bot.fillUnderlights(new int[] { 255, 0, 0 });
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}

			bot.fillUnderlights(new int[] { 0, 0, 0 });
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
	}

	public static void FlashgreenLight(int times) {
		for (int i = 0; i < times; i++) {
			bot.fillUnderlights(new int[] { 0, 255, 0 });
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			bot.fillUnderlights(new int[] { 0, 0, 0 });
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
	}

	public static int[] SelectSpeed(int TotalDistance) {
		System.out.println(" WHAT SPEED YOU WANT SWIFTBOT TO MOVE ");
		System.out.println("Press [1] for High Speed (Speed +5)");
		System.out.println("Press [2] for Low Speed (Speed -5)");
		System.out.println("And if normal Speed press [3]");

		int Speed = 0;
		int Calibration = 0;
		String speedChoice = scanner.nextLine().trim();
		if (TotalDistance <= 200) {
			Speed = 50;
			Calibration = 50;
		} else {
			Speed = 60;
			Calibration = 40;
		}
		if (speedChoice.equals("1")) {
			Speed += 5;
			Calibration += 5;
			System.out.println("===== HIGH SPEED SUCCESSFULLY SELECTED =====");
		} else if (speedChoice.equals("2")) {
			Speed -= 5;
			Calibration -= 5;
			System.out.println("===== LOW SPEED SUCCESSFULLY SELECTED =====");
		} else {
			System.out.println("===== SPEED SUCCESSFULY SELECTED =====");
		}
		return new int[] { Speed, Calibration };

	}

	public static void DrawSquare(int sideLength) {

		int TotalDistance = sideLength * 4;

		int[] speedConfig = SelectSpeed(TotalDistance);
		int Speed = speedConfig[0];
		int Calibration = speedConfig[1];
		System.out.println("Drawing square of side: " + sideLength + " cm at speed: " + Speed);
		FlashRedLight(3);
		DisplaySquare(sideLength);

		for (int i = 0; i < 4; i++) {
			bot.fillUnderlights(new int[] { 255, 255, 0 });
			bot.move(Speed - 6, Speed, sideLength * Calibration);
			bot.stopMove();
			System.out.println("TURNING RIGHT");
			bot.move(-70, 70, 450);

		}

		FlashgreenLight(3);
		System.out.println("===== SQUARE DRAWING HAS BEEN COMPLETED ====");
	}

	public static void DisplaySquare(int sideLength) {
		System.out.println("~~~~ DRAWING IN PROCESS ~~~~");
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				System.out.print("* ");
			}
			System.out.println();
		}
	}

	public static void DrawTriangle(int t1, int t2, int t3) {

		int TotalDistance = t1 + t2 + t3;

		int[] speedConfig = SelectSpeed(TotalDistance);
		int Speed = speedConfig[0];
		int Calibration = speedConfig[1];

		int a1 = 180 - CalculateAngle(t2, t3, t1);
		int a2 = 180 - CalculateAngle(t1, t3, t2);

		System.out.println("Drawing Triangle of sides " + t1 + ", " + t2 + ", " + t3 + " at speed " + Speed);

		FlashRedLight(3);

		DisplayTriangle(t1, t2, t3);
		bot.fillUnderlights(new int[] { 255, 255, 0 });
		bot.move(Speed - 5, Speed, t1 * Calibration);
		bot.stopMove();

		bot.move(50, -50, a1 * 5);
		bot.stopMove();

		bot.move(Speed - 5, Speed, t2 * Calibration);
		bot.stopMove();

		bot.move(50, -50, a2 * 5);
		bot.stopMove();

		bot.move(Speed - 5, Speed, t3 * Calibration);
		bot.stopMove();

		FlashgreenLight(3);
		System.out.println("===== TRIANGLE DRAWING HAS BEEN COMPLETED ====");

	}

	public static void DisplayTriangle(int a, int b, int c) {
		int size = 10;
		for (int i = 1; i <= size; i++) {
			for (int j = 1; j <= i; j++) {
				System.out.print("* ");
			}
			System.out.println();
		}
	}

	private static int CalculateAngle(int a, int b, int c) {
		double CosValue = (Math.pow(a, 2) + Math.pow(b, 2) - Math.pow(c, 2)) / (2.0 * a * b);
		return (int) Math.round(Math.toDegrees(Math.acos(CosValue)));
	}

	private static double CalculateTriangleArea(int a, int b, int c) {
		double s = (a + b + c) / 2.0;
		return Math.sqrt(s * (s - a) * (s - b) * (s - c));
	}

	private static void UpdateStats(String shapeName, double area, long startTime) {
		long endTime = System.currentTimeMillis();
		long timeTaken = endTime - startTime;

		ShapeData.add(shapeName + " Size: " + area + " cm² - Time: " + timeTaken + "ms");// adding Shape details
		TimeTaken.add(timeTaken);

		if (NoofShape.containsKey(shapeName)) {
			NoofShape.put(shapeName, NoofShape.get(shapeName) + 1);
		} else {
			NoofShape.put(shapeName, 1);
		}

		if (area > LargestArea) {
			LargestArea = area;
			LargestShape = shapeName + " (Size: " + area + " cm²)";
		}
	}

	public static void GenerateSummary() {
		StringBuilder Summary = new StringBuilder("\n===== SUMMARY REPORT =====\n");
		Summary.append("Shapes drawn in order:\n");

		for (int i = 0; i < ShapeData.size(); i++) {
			Summary.append(ShapeData.get(i)).append("\n");
		}
		String mostFrequentShape = "None";
		int maxCount = 0;
		Object[] keys = NoofShape.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			String shape = (String) keys[i];
			int count = NoofShape.get(shape);
			if (count > maxCount) {
				mostFrequentShape = shape;
				maxCount = count;
			}
		}

		long totalTime = 0;
		for (int i = 0; i < TimeTaken.size(); i++) {
			totalTime += TimeTaken.get(i);
		}

		double AverageTime = 0;
		if (TimeTaken.size() > 0) {
			AverageTime = (double) totalTime / TimeTaken.size();
		}

		Summary.append("\nLargest Shape: ");
		if (LargestShape.isEmpty()) {
			Summary.append("None\n");
		} else {
			Summary.append(LargestShape).append("\n");
		}

		Summary.append("Most Frequent Shape: ").append(mostFrequentShape).append(" (Count: ");
		if (NoofShape.containsKey(mostFrequentShape)) {
			Summary.append(NoofShape.get(mostFrequentShape));
		} else {
			Summary.append("0");
		}
		Summary.append(")\n");

		Summary.append("Average Time Per Shape: ").append(String.format("%.2f", AverageTime)).append("ms\n");
		Summary.append("========================================\n");

		SaveLogToFile(Summary.toString());
	}

	private static void SaveLogToFile(String content) {
		try {
			File file = new File(LogFilePath);
			if (!file.exists()) {
				file.createNewFile(); // Ensure the log file exists
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true)); // Append mode enabled
			writer.write(content);
			writer.newLine();
			writer.close();
			System.out.println("Summary file is saved at: " + LogFilePath); // Confirmation message

		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

}
