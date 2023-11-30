package org.example;

public class Coordinates {
    final int[] leftTop;
    final int[] leftBottom;
    final int[] rightBottom;
    final int[] rightTop;

    Coordinates(int[] leftTop, int[] leftBottom, int[] rightBottom, int[] rightTop) {
        this.leftTop = leftTop;
        this.leftBottom = leftBottom;
        this.rightBottom = rightBottom;
        this.rightTop = rightTop;
    }
}
