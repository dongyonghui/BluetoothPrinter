package cn.vip.dw.bluetoothprinterlib.parser;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.io.UnsupportedEncodingException;

public class GpUtils {
    private static int[] p0;
    private static int[] p1;
    private static int[] p2;
    private static int[] p3;
    private static int[] p4;
    private static int[] p5;
    private static int[] p6;
    private static int[][] Floyd16x16;
    private static int[][] Floyd8x8;
    public static final int ALGORITHM_DITHER_16x16 = 16;
    public static final int ALGORITHM_DITHER_8x8 = 8;
    public static final int ALGORITHM_TEXTMODE = 2;
    public static final int ALGORITHM_GRAYTEXTMODE = 1;

    static {
        int[] arrayOfInt1 = new int[]{0, 128};
        p0 = arrayOfInt1;
        int[] arrayOfInt2 = new int[]{0, 64};
        p1 = arrayOfInt2;
        int[] arrayOfInt3 = new int[]{0, 32};
        p2 = arrayOfInt3;
        int[] arrayOfInt4 = new int[]{0, 16};
        p3 = arrayOfInt4;
        int[] arrayOfInt5 = new int[]{0, 8};
        p4 = arrayOfInt5;
        int[] arrayOfInt6 = new int[]{0, 4};
        p5 = arrayOfInt6;
        int[] arrayOfInt7 = new int[]{0, 2};
        p6 = arrayOfInt7;
        int[][] arrayOfInt8 = new int[16][];
        int[] arrayOfInt9 = new int[]{0, 128, 32, 160, 8, 136, 40, 168, 2, 130, 34, 162, 10, 138, 42, 170};
        arrayOfInt8[0] = arrayOfInt9;
        int[] arrayOfInt10 = new int[]{192, 64, 224, 96, 200, 72, 232, 104, 194, 66, 226, 98, 202, 74, 234, 106};
        arrayOfInt8[1] = arrayOfInt10;
        int[] arrayOfInt11 = new int[]{48, 176, 16, 144, 56, 184, 24, 152, 50, 178, 18, 146, 58, 186, 26, 154};
        arrayOfInt8[2] = arrayOfInt11;
        int[] arrayOfInt12 = new int[]{240, 112, 208, 80, 248, 120, 216, 88, 242, 114, 210, 82, 250, 122, 218, 90};
        arrayOfInt8[3] = arrayOfInt12;
        int[] arrayOfInt13 = new int[]{12, 140, 44, 172, 4, 132, 36, 164, 14, 142, 46, 174, 6, 134, 38, 166};
        arrayOfInt8[4] = arrayOfInt13;
        int[] arrayOfInt14 = new int[]{204, 76, 236, 108, 196, 68, 228, 100, 206, 78, 238, 110, 198, 70, 230, 102};
        arrayOfInt8[5] = arrayOfInt14;
        int[] arrayOfInt15 = new int[]{60, 188, 28, 156, 52, 180, 20, 148, 62, 190, 30, 158, 54, 182, 22, 150};
        arrayOfInt8[6] = arrayOfInt15;
        int[] arrayOfInt16 = new int[]{252, 124, 220, 92, 244, 116, 212, 84, 254, 126, 222, 94, 246, 118, 214, 86};
        arrayOfInt8[7] = arrayOfInt16;
        int[] arrayOfInt17 = new int[]{3, 131, 35, 163, 11, 139, 43, 171, 1, 129, 33, 161, 9, 137, 41, 169};
        arrayOfInt8[8] = arrayOfInt17;
        int[] arrayOfInt18 = new int[]{195, 67, 227, 99, 203, 75, 235, 107, 193, 65, 225, 97, 201, 73, 233, 105};
        arrayOfInt8[9] = arrayOfInt18;
        int[] arrayOfInt19 = new int[]{51, 179, 19, 147, 59, 187, 27, 155, 49, 177, 17, 145, 57, 185, 25, 153};
        arrayOfInt8[10] = arrayOfInt19;
        int[] arrayOfInt20 = new int[]{243, 115, 211, 83, 251, 123, 219, 91, 241, 113, 209, 81, 249, 121, 217, 89};
        arrayOfInt8[11] = arrayOfInt20;
        int[] arrayOfInt21 = new int[]{15, 143, 47, 175, 7, 135, 39, 167, 13, 141, 45, 173, 5, 133, 37, 165};
        arrayOfInt8[12] = arrayOfInt21;
        int[] arrayOfInt22 = new int[]{207, 79, 239, 111, 199, 71, 231, 103, 205, 77, 237, 109, 197, 69, 229, 101};
        arrayOfInt8[13] = arrayOfInt22;
        int[] arrayOfInt23 = new int[]{63, 191, 31, 159, 55, 183, 23, 151, 61, 189, 29, 157, 53, 181, 21, 149};
        arrayOfInt8[14] = arrayOfInt23;
        int[] arrayOfInt24 = new int[]{254, 127, 223, 95, 247, 119, 215, 87, 253, 125, 221, 93, 245, 117, 213, 85};
        arrayOfInt8[15] = arrayOfInt24;
        int[][] arrayOfInt25 = new int[8][];
        int[] arrayOfInt26 = new int[]{0, 32, 8, 40, 2, 34, 10, 42};
        arrayOfInt25[0] = arrayOfInt26;
        int[] arrayOfInt27 = new int[]{48, 16, 56, 24, 50, 18, 58, 26};
        arrayOfInt25[1] = arrayOfInt27;
        int[] arrayOfInt28 = new int[]{12, 44, 4, 36, 14, 46, 6, 38};
        arrayOfInt25[2] = arrayOfInt28;
        int[] arrayOfInt29 = new int[]{60, 28, 52, 20, 62, 30, 54, 22};
        arrayOfInt25[3] = arrayOfInt29;
        int[] arrayOfInt30 = new int[]{3, 35, 11, 43, 1, 33, 9, 41};
        arrayOfInt25[4] = arrayOfInt30;
        int[] arrayOfInt31 = new int[]{51, 19, 59, 27, 49, 17, 57, 25};
        arrayOfInt25[5] = arrayOfInt31;
        int[] arrayOfInt32 = new int[]{15, 47, 7, 39, 13, 45, 5, 37};
        arrayOfInt25[6] = arrayOfInt32;
        int[] arrayOfInt33 = new int[]{63, 31, 55, 23, 61, 29, 53, 21};
        arrayOfInt25[7] = arrayOfInt33;
        int[][] arrayOfInt34 = new int[4][];
        int[] arrayOfInt35 = new int[]{0, 8, 2, 10};
        arrayOfInt34[0] = arrayOfInt35;
        int[] arrayOfInt36 = new int[]{12, 4, 14, 6};
        arrayOfInt34[1] = arrayOfInt36;
        int[] arrayOfInt37 = new int[]{3, 11, 1, 9};
        arrayOfInt34[2] = arrayOfInt37;
        int[] arrayOfInt38 = new int[]{15, 7, 13, 5};
        arrayOfInt34[3] = arrayOfInt38;
        Floyd16x16 = new int[][]{{0, 128, 32, 160, 8, 136, 40, 168, 2, 130, 34, 162, 10, 138, 42, 170}, {192, 64, 224, 96, 200, 72, 232, 104, 194, 66, 226, 98, 202, 74, 234, 106}, {48, 176, 16, 144, 56, 184, 24, 152, 50, 178, 18, 146, 58, 186, 26, 154}, {240, 112, 208, 80, 248, 120, 216, 88, 242, 114, 210, 82, 250, 122, 218, 90}, {12, 140, 44, 172, 4, 132, 36, 164, 14, 142, 46, 174, 6, 134, 38, 166}, {204, 76, 236, 108, 196, 68, 228, 100, 206, 78, 238, 110, 198, 70, 230, 102}, {60, 188, 28, 156, 52, 180, 20, 148, 62, 190, 30, 158, 54, 182, 22, 150}, {252, 124, 220, 92, 244, 116, 212, 84, 254, 126, 222, 94, 246, 118, 214, 86}, {3, 131, 35, 163, 11, 139, 43, 171, 1, 129, 33, 161, 9, 137, 41, 169}, {195, 67, 227, 99, 203, 75, 235, 107, 193, 65, 225, 97, 201, 73, 233, 105}, {51, 179, 19, 147, 59, 187, 27, 155, 49, 177, 17, 145, 57, 185, 25, 153}, {243, 115, 211, 83, 251, 123, 219, 91, 241, 113, 209, 81, 249, 121, 217, 89}, {15, 143, 47, 175, 7, 135, 39, 167, 13, 141, 45, 173, 5, 133, 37, 165}, {207, 79, 239, 111, 199, 71, 231, 103, 205, 77, 237, 109, 197, 69, 229, 101}, {63, 191, 31, 159, 55, 183, 23, 151, 61, 189, 29, 157, 53, 181, 21, 149}, {254, 127, 223, 95, 247, 119, 215, 87, 253, 125, 221, 93, 245, 117, 213, 85}};
        Floyd8x8 = new int[][]{{0, 32, 8, 40, 2, 34, 10, 42}, {48, 16, 56, 24, 50, 18, 58, 26}, {12, 44, 4, 36, 14, 46, 6, 38}, {60, 28, 52, 20, 62, 30, 54, 22}, {3, 35, 11, 43, 1, 33, 9, 41}, {51, 19, 59, 27, 49, 17, 57, 25}, {15, 47, 7, 39, 13, 45, 5, 37}, {63, 31, 55, 23, 61, 29, 53, 21}};
    }

    public GpUtils() {
    }

    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = (float)(w / width);
        float scaleHeight = (float)(h / height);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return resizedBitmap;
    }

    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int height = bmpOriginal.getHeight();
        int width = bmpOriginal.getWidth();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0.0F);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0.0F, 0.0F, paint);
        return bmpGrayscale;
    }

    private static void format_K_dither16x16(int[] orgpixels, int xsize, int ysize, byte[] despixels) {
        int k = 0;

        for(int y = 0; y < ysize; ++y) {
            for(int x = 0; x < xsize; ++x) {
                if ((orgpixels[k] & 255) > Floyd16x16[x & 15][y & 15]) {
                    despixels[k] = 0;
                } else {
                    despixels[k] = 1;
                }

                ++k;
            }
        }

    }

    public static byte[] bitmapToBWPix(Bitmap mBitmap) {
        int[] pixels = new int[mBitmap.getWidth() * mBitmap.getHeight()];
        byte[] data = new byte[mBitmap.getWidth() * mBitmap.getHeight()];
        Bitmap grayBitmap = toGrayscale(mBitmap);
        grayBitmap.getPixels(pixels, 0, mBitmap.getWidth(), 0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        format_K_dither16x16(pixels, grayBitmap.getWidth(), grayBitmap.getHeight(), data);
        return data;
    }

    private static void format_K_dither16x16_int(int[] orgpixels, int xsize, int ysize, int[] despixels) {
        int k = 0;

        for(int y = 0; y < ysize; ++y) {
            for(int x = 0; x < xsize; ++x) {
                if ((orgpixels[k] & 255) > Floyd16x16[x & 15][y & 15]) {
                    despixels[k] = -1;
                } else {
                    despixels[k] = -16777216;
                }

                ++k;
            }
        }

    }

    private static void format_K_dither8x8_int(int[] orgpixels, int xsize, int ysize, int[] despixels) {
        int k = 0;

        for(int y = 0; y < ysize; ++y) {
            for(int x = 0; x < xsize; ++x) {
                if ((orgpixels[k] & 255) >> 2 > Floyd8x8[x & 7][y & 7]) {
                    despixels[k] = -1;
                } else {
                    despixels[k] = -16777216;
                }

                ++k;
            }
        }

    }

    public static int[] bitmapToBWPix_int(Bitmap mBitmap, int algorithm) {
        int[] pixels = new int[0];
        switch(algorithm) {
        case 2:
            break;
        case 8:
            Bitmap grayBitmap = toGrayscale(mBitmap);
            pixels = new int[grayBitmap.getWidth() * grayBitmap.getHeight()];
            grayBitmap.getPixels(pixels, 0, grayBitmap.getWidth(), 0, 0, grayBitmap.getWidth(), grayBitmap.getHeight());
            format_K_dither8x8_int(pixels, grayBitmap.getWidth(), grayBitmap.getHeight(), pixels);
            break;
        case 16:
        default:
            Bitmap grayBitmap1 = toGrayscale(mBitmap);
            pixels = new int[grayBitmap1.getWidth() * grayBitmap1.getHeight()];
            grayBitmap1.getPixels(pixels, 0, grayBitmap1.getWidth(), 0, 0, grayBitmap1.getWidth(), grayBitmap1.getHeight());
            format_K_dither16x16_int(pixels, grayBitmap1.getWidth(), grayBitmap1.getHeight(), pixels);
        }

        return pixels;
    }

    public static Bitmap toBinaryImage(Bitmap mBitmap, int nWidth, int algorithm) {
        int width = (nWidth + 7) / 8 * 8;
        int height = mBitmap.getHeight() * width / mBitmap.getWidth();
        Bitmap rszBitmap = resizeImage(mBitmap, width, height);
        int[] pixels = bitmapToBWPix_int(rszBitmap, algorithm);
        rszBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return rszBitmap;
    }

    public static byte[] pixToTSCCmd(int x, int y, int mode, byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
        int height = paramArrayOfByte.length / paramInt1;
        int width = paramInt1 / 8;
        String str = "BITMAP " + x + "," + y + "," + width + "," + height + "," + mode + ",";
        byte[] bitmap = null;

        try {
            bitmap = str.getBytes("GB2312");
        } catch (UnsupportedEncodingException var14) {
            var14.printStackTrace();
        }

        byte[] arrayOfByte = new byte[paramArrayOfByte.length / 8];
        int j = 0;

        for(int k = 0; k < arrayOfByte.length; ++k) {
            byte temp = (byte)(p0[paramArrayOfByte[j]] + p1[paramArrayOfByte[j + 1]] + p2[paramArrayOfByte[j + 2]] + p3[paramArrayOfByte[j + 3]] + p4[paramArrayOfByte[j + 4]] + p5[paramArrayOfByte[j + 5]] + p6[paramArrayOfByte[j + 6]] + paramArrayOfByte[j + 7]);
            arrayOfByte[k] = (byte)(~temp);
            j += 8;
        }

        byte[] data = new byte[bitmap.length + arrayOfByte.length];
        System.arraycopy(bitmap, 0, data, 0, bitmap.length);
        System.arraycopy(arrayOfByte, 0, data, bitmap.length, arrayOfByte.length);
        return data;
    }

    public static byte[] pixToESCCmd(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
        int i = paramArrayOfByte.length / paramInt1;
        byte[] arrayOfByte = new byte[paramArrayOfByte.length / 8];
        arrayOfByte[0] = 29;
        arrayOfByte[1] = 118;
        arrayOfByte[2] = 48;
        arrayOfByte[3] = 48;
        arrayOfByte[4] = (byte)(paramInt1 / 8 % 256);
        arrayOfByte[5] = (byte)(paramInt1 / 8 / 256);
        arrayOfByte[6] = (byte)(i % 256);
        arrayOfByte[7] = (byte)(i / 256);
        int j = 0;

        for(int k = 8; k < arrayOfByte.length; ++k) {
            arrayOfByte[k] = (byte)(p0[paramArrayOfByte[j]] + p1[paramArrayOfByte[j + 1]] + p2[paramArrayOfByte[j + 2]] + p3[paramArrayOfByte[j + 3]] + p4[paramArrayOfByte[j + 4]] + p5[paramArrayOfByte[j + 5]] + p6[paramArrayOfByte[j + 6]] + paramArrayOfByte[j + 7]);
            j += 8;
        }

        return arrayOfByte;
    }
}
