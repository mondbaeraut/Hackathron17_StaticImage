package com.globe_spinners.main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class ImageController {
    private long startTime;
    private long endTime;
    private double lastTime;
    private double frameRate;
    private int globeW;
    private int globeH;
    private boolean imageSent = false;

    BufferedImage processedImage = null;
    UdpConnection connection = null;

    public void init() {
        frameRate = 1.0 / 24.0;
        lastTime = 0;
        globeW = 86;
        globeH = 43;

        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("resources/img_fancy.PNG"));
        } catch (IOException e) {
        }
//			image = doWinklerProjection(image, globeH / 2, globeH / 2);
        image = scaleImageToGlobeSize(image, globeW, globeH);
        processedImage = image;
        connection = UdpConnection.getInstance();
    }

    public void run() {
        if(!imageSent) {
            startTime = System.currentTimeMillis();
            int[] output = new int[processedImage.getWidth() * processedImage.getHeight()];
            for (int x = 0; x < processedImage.getWidth(); x++) {
                for (int y = 0; y < processedImage.getHeight(); y++) {
                    output[x * y] = processedImage.getRGB(x, y);
                }
            }
            ByteBuffer byteBuffer = ByteBuffer.allocate(output.length * 4);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            IntBuffer intBuffer = byteBuffer.asIntBuffer();
            intBuffer.put(output);
            byte[] buffer = byteBuffer.array();
            connection.writeData(buffer);

            endTime = System.currentTimeMillis();
            lastTime += endTime - startTime;
            imageSent = true;
        }
    }

    public BufferedImage doWinklerProjection(BufferedImage image, double inNorth, double inSouth) {
        BufferedImage retImage = null;
        double lMaxLat = inNorth * Math.PI / 180;
        double lMinLat = inSouth * Math.PI / 180;

        double lLatDiff = lMaxLat - lMinLat;

        //Get the inverse of the Mercator projection
        double lNewMaxLat = Math.atan(Math.sinh(lMaxLat));
        double lNewMinLat = Math.atan(Math.sinh(lMinLat));
        double lNewDiff = lNewMaxLat - lNewMinLat;

        // Allocate the new image.
        int lImageWidth = image.getWidth(); //Image width will remain the same
        int lImageHeight = image.getHeight();
        int lNewHeight = (int) (lImageHeight * lNewDiff / lLatDiff);
        retImage = new BufferedImage(lImageWidth, lNewHeight, image.getType());
        double lLatPerRow = lLatDiff / lImageHeight;
        double lNewLatPerRow = lNewDiff / lNewHeight;

        //Move each row of the original to its row in the new image
        for (int lRow = 0; lRow < lImageHeight; lRow++) {
            //Get the latitude of the current row in original image
            double lLatOfRow = lMaxLat - (lRow * lLatPerRow);
            double lNewLat = Math.atan(Math.sinh(lLatOfRow)); //Reproject lat
            //Determine where the reprojected latitude goes in new image
            int lNewLatRow = (int) ((lNewMaxLat - lNewLat) / lNewLatPerRow);
            //Copy the original row to the reprojected row.
            int[] lRgb = image.getRGB(0, lRow, lImageWidth, 1, null, 0, 4 * lImageWidth);
            retImage.setRGB(0, lNewLatRow, lImageWidth, 1, lRgb, 0, 4 * lImageWidth);
        }
        return retImage;
    }

    public BufferedImage scaleImageToGlobeSize(BufferedImage image, int globeW, int globeH) {
        BufferedImage newImage = new BufferedImage(globeW, globeH, BufferedImage.TYPE_INT_RGB);

        Graphics g = newImage.createGraphics();
        g.drawImage(image, 0, 0, globeW, globeH, null);
        g.dispose();

        return newImage;
    }

    private double getLongitude(int x) {
        return 0;
    }

    private double getLatitude(int y) {
        return 0;
    }
}