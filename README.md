Draw Shape Program
📌 Overview

The Draw Shape Program is a Year 1 software project developed to control a Swift Bot and draw geometric shapes (squares and triangles) based on QR code input.
It provides both a visual output on the Command-Line Interface (CLI) and real movement of the Swift Bot in the specified shape.
The program validates user input, performs shape analysis (including triangle angle calculation), and generates a log file with details of the drawn shapes.

🚀 Features
QR Code Integration: Reads shape definitions (e.g., S-16 or T-16-30-24).

Shape Validation:
Squares: Checks side length range (15–85 cm).
Triangles: Ensures sides meet the Triangle Inequality Theorem and are within range.

Drawing Mechanism:
Swift Bot moves forward and turns according to shape sides and angles.
Supports drawing multiple shapes from a single QR code.

Error Handling:
Invalid QR codes
Out-of-range side lengths
Invalid triangle sides
Drawing or scanning errors

Logging:
Stores names, sizes, and angles of drawn shapes
Records largest shape (by area)
Tracks most frequent shape
Calculates average drawing time

🛠️ Technologies Used
Java (main programming language)
Swift Bot SDK
Command-Line Interface (CLI) for interaction

📖 Example QR Codes
S-16 → Square with side 16 cm
T-16-30-24 → Triangle with sides 16 cm, 30 cm, 24 cm
S-16 & T-16-30-24 → Multiple shapes

📂 Project Structure
DrawShapeProgram.java   # Main source code
README.md               # Documentation
Software Design.docx    # Design document (SRS, flowcharts, UI mockups)

Usage
Run the program on your Swift Bot setup.
Scan a QR code with shape definitions.
Watch the Swift Bot draw the shape(s).
View logs in the generated output file after termination.

📊 Skills Demonstrated
Problem-solving and algorithm design
Input validation and error handling
Implementation of mathematical rules (e.g., triangle inequality, cosine rule)
Software design and documentation
Hardware and software integration with Swift Bot

📜 License

This project was created as part of a Year 1 coursework assignment and is for educational use.

