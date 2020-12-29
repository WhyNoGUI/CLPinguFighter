package com.whynogui.clpf;

import com.googlecode.lanterna.TextColor;
import com.whynogui.clpf.lanterna.Ansi8PixelScreenBuffer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Sprite {
    int width, height;
    private boolean visible;

    private int[][] colorValues;
    private Ansi8PixelScreenBuffer preloadedPixelBuffer;

    public Sprite (int width, int height, int[][] colorValues) {
        this.width = width;
        this.height = height;
        this.colorValues = colorValues;
        visible = true;
        preloadedPixelBuffer = Ansi8PixelScreenBuffer.convertColorArrayToBuffer(colorValues);
    }

    private static int rgbToAnsi8(int red, int blue, int green) {
        int rescaledRed = (int)(((double)red / 255.0) * 5.0);
        int rescaledGreen = (int)(((double)green / 255.0) * 5.0);
        int rescaledBlue = (int)(((double)blue / 255.0) * 5.0);

        int colorIndex = rescaledBlue + (6 * rescaledGreen) + (36 * rescaledRed) + 16;
        TextColor.Indexed fromColorCube = new TextColor.Indexed(colorIndex);
        int greyIndex = (int)(((double)((red + green + blue) / 3) / 255.0) * 23.0) + 232;
        TextColor.Indexed fromGreyRamp = new TextColor.Indexed(greyIndex);

        int coloredDistance = ((red - fromColorCube.getRed()) * (red - fromColorCube.getRed())) +
                ((green - fromColorCube.getGreen()) * (green - fromColorCube.getGreen())) +
                ((blue - fromColorCube.getBlue()) * (blue - fromColorCube.getBlue()));
        int greyDistance = ((red - fromGreyRamp.getRed()) * (red - fromGreyRamp.getRed())) +
                ((green - fromGreyRamp.getGreen()) * (green - fromGreyRamp.getGreen())) +
                ((blue - fromGreyRamp.getBlue()) * (blue - fromGreyRamp.getBlue()));
        if(coloredDistance < greyDistance) {
            return colorIndex;
        }
        else {
            return greyIndex;
        }
    }

    public static int[][] ansi8ColorsFromBitmap(String path) {
        try {
            BufferedImage img = ImageIO.read(Sprite.class.getResourceAsStream(path));
            int numColumns = img.getWidth();
            int numRows = img.getHeight();
            int[][] colorValues = new int[numColumns][numRows];
            for (int column = 0; column < colorValues.length; column++) {
                for (int row = 0; row < colorValues.length; row++) {
                    int rgb = img.getRGB(column, row);
                    colorValues[column][row] = rgbToAnsi8(
                            (rgb >> 16 ) & 0x000000FF,
                            (rgb >> 8 ) & 0x000000FF,
                            (rgb) & 0x000000FF);
                }
            }
            return colorValues;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sprite: " + path, e);
        }
    }

    public int[][] getColorValues() {
        return colorValues;
    }

    public Ansi8PixelScreenBuffer getPreloadedPixelBuffer() {
        return preloadedPixelBuffer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isVisible() {
        return visible;
    }
}
