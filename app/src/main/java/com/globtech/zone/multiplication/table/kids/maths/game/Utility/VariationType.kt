package com.globtech.zone.multiplication.table.kids.maths.game.Utility

enum class VariationType(val title: String, val digit1: Int, val digit2: Int, val preKey: String) {

    Digit11("1 Digit + 1 Digit".uppercase(), 1, 1, "d11"), //Easy, Medium, Hard
    Digit22("2 Digit + 2 Digit".uppercase(), 2, 2, "d22"), //Easy, Medium, Hard
    Digit32("3 Digit + 2 Digit".uppercase(), 3, 2, "d32"), //Easy, Medium, Hard
    Digit33("3 Digit + 3 Digit".uppercase(), 3, 3, "d33"), //Easy, Medium, Hard
    Digit44("4 Digit + 4 Digit".uppercase(), 4, 4, "d44"), //Easy, Medium, Hard
    Digit3("Digit 3".uppercase(), 3, 3, "d3"), //Easy, Medium, Hard
    Digit4("Digit 4".uppercase(), 4, 4, "d4"), //Easy, Medium, Hard
    RandoDigit("Random Digit".uppercase(), 0, 0, "rd"), //Easy, Medium, Hard
    DecimalAddition("Addition".uppercase(), 0, 0, "da"), //Easy, Medium, Hard
    DecimalSubtraction("Subtraction".uppercase(), 0, 0, "ds"), //Easy, Medium, Hard
    DecimalMultiplication("Multiplication".uppercase(), 0, 0, "dm"), //Easy, Medium, Hard

    TimeChallenge(
        "Time Challenge".uppercase(),
        0,
        0, "tc"
    ), //Difficulty(Easy,Medium,Hard), Time(0,10,30,60), Digit(1,2,3,4)

    MissingNumber(
        "Missing Number".uppercase(),
        0,
        0,
        "mn"
    ), //Difficulty(Easy,Medium,Hard), Digit(1,2,3,4)
    Balanced("Balanced".uppercase(), 0, 0, "bl"), //Difficulty(Easy,Medium,Hard), Digit(1,2,3,4)
    Negative("Negative".uppercase(), 0, 0, "ng"), //Difficulty(Easy,Medium,Hard), Digit(1,2,3,4)

    TableLearn("Learn".uppercase(), 0, 0, "tl"), //set ViewPager table of (1 to 30)

    TablePractice("Table Practice".uppercase(), 0, 0, "tp"), //Sequential and random number

    SquareRootLearn("Learn".uppercase(), 0, 0, "sr"),
    SquareRootTimeChallenge("Time Challenge".uppercase(), 0, 0, "srtc"),;

}