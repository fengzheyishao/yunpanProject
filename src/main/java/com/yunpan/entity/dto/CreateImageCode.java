package com.yunpan.entity.dto;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class CreateImageCode {
    private int width = 160;
    private int height = 40;
    private int fontSize = 28;
    private int codeCount = 4;
    private int lineCount = 20;
    private String code = null;
    private BufferedImage bfimg = null;

    Random random = new Random();

    private static final String[] CHAR_ARRY = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    public CreateImageCode() {

    }

    public CreateImageCode(int width, int height) {
        this.width = width;
        this.height = height;
        createImage();
    }

    public CreateImageCode(int width, int height, int fontSize, int codeCount, int lineCount) {
        this.width = width;
        this.height = height;
        this.fontSize = fontSize;
        this.codeCount = codeCount;
        this.lineCount = lineCount;
        createImage();
    }

    private void createImage() {
        int fontWidth = width / codeCount;
        int fontHight = height - 5;
        int codeY = height - 8;

        bfimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = bfimg.getGraphics();

        g.setColor(Color.cyan);
        g.fillRect(0, 0, width, height);
        Font font = new Font("Fixedays", Font.BOLD, fontHight);
        g.setFont(font);

        for (int i = 0; i < lineCount; i++) {
            int xs = random.nextInt(width);
            int ys = random.nextInt(height);
            int xe = xs + random.nextInt(width);
            int ye = ys + random.nextInt(height);
            g.setColor(getRandColor(1, 2515));
            g.drawLine(xs, ys, xe, ye);
        }

        float yawRate = 0.01f;
        int area = (int) (yawRate * width * height);
        for (int i = 0; i < area; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            bfimg.setRGB(x, y, random.nextInt(255));
        }

        String str = randomStr(codeCount);
        this.code = str;

        for (int i = 0; i < codeCount; i++) {
            String strRand = str.substring(i, i+1);
            g.setColor(getRandColor(1, 255));
            g.drawString(strRand, i * fontWidth + 3, codeY);
        }
    }

    private String randomStr(int n) {
        String str2 = "";
        int len = CHAR_ARRY.length;
        double r;
        for (int i = 0; i < n; i++) {
            r = (Math.random()) * len;
            str2 = str2 + CHAR_ARRY[(int) r];
        }
        return str2;
    }

    private Color getRandColor(int fc, int bc) {
        if (fc > 255) fc = 255;
        if (bc > 255) bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    private Font getFont(int size) {
        Random random = new Random();
        Font font[] = new Font[5];
        font[0] = new Font("Ravie", Font.PLAIN, size);
        font[1] = new Font("Antique Olive Compact", Font.PLAIN, size);
        font[2] = new Font("Fixedays", Font.PLAIN, size);
        font[3] = new Font("Wide Latin", Font.PLAIN, size);
        font[4] = new Font("Gill Sans Ultra Bold", Font.PLAIN, size);
        return font[random.nextInt(5)];
    }

    private void shear(Graphics g, int w1, int h1, Color color) {
        shearX(g, w1, h1, color);
        shearY(g, w1, h1, color);
    }
    private void shearX(Graphics g, int w1, int h1, Color color) {
        int period = random.nextInt(2);

        boolean borderGad = true;
        int frames = 1;
        int phase = random.nextInt(2);

        for (int i = 0; i < h1; i++) {
            double d = (double) (period>>1) * Math.sin((double )i/(double )period + Math.PI * (double) phase);
            g.copyArea(i, 0, 1, h1, 0, (int) d);
            if (borderGad) {
                g.setColor(color);
                g.drawLine(0, (int) d, i, 0);
                g.drawLine(0, (int) d+h1, i, h1);
            }
        }

    }

    public void shearY(Graphics g, int w1, int h1, Color color) {
        int period = random.nextInt(40)+10;

        boolean borderGad = true;
        int frames = 20;
        int phase = 7;

        for (int i = 0; i < w1; i++) {
            double d = (double) (period>>1) * Math.sin((double )i/(double )period + Math.PI * (double) phase);
            g.copyArea(i, 0, 1, h1, 0, (int) d);
            if (borderGad) {
                g.setColor(color);
                g.drawLine(0, (int) d, i, 0);
                g.drawLine(0, (int) d+h1, i, h1);
            }
        }
    }

    public void write(OutputStream sos) throws IOException {
        ImageIO.write(bfimg, "png", sos);
        sos.close();
    }

    public BufferedImage getBfimg() {
        return bfimg;
    }

    public String getCode() {
        return code.toLowerCase();
    }
}
