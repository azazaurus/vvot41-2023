package org.example;

public class Coordinates {
    public final int[] leftTop;
    public final int[] leftBottom;
    public final int[] rightTop;
    public final int[] rightBottom;

    Coordinates(int[] leftTop, int[] leftBottom, int[] rightTop, int[] rightBottom) {
        this.leftTop = leftTop;
        this.leftBottom = leftBottom;
        this.rightTop = rightTop;
        this.rightBottom = rightBottom;
    }
}
