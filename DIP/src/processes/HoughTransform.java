/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processes;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Orkun
 */
public class HoughTransform extends Process {

    double d_alfa = 1 * (Math.PI / 360);

    void processHoughTransform() {
        try {
            HoughTransform(this.img);
        } catch (Exception ex) {
            Logger.getLogger(HoughTransform.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    boolean ench = false;

    public HoughTransform(boolean ench) {
        this.ench = ench;
    }
    int houghSpace_widht;
    int houghSpace_height;
    int[][] HoughSpace;

    void HoughTransform(BufferedImage img) throws Exception {
        if (img.getType() != BufferedImage.TYPE_BYTE_BINARY) {
            throw new Exception("Input Image Type Must be TYPE_BYTE_BINARY");
        }

        int imgWidht = img.getWidth();
        int imgHeight = img.getHeight();
        // hough uzayının boyutlarını hesaplıyoruz
        houghSpace_widht = (int) (Math.PI / d_alfa);
        double houghSpace_height_d = Math.sqrt(Math.pow(imgWidht, 2) + Math.pow(imgHeight, 2));
        houghSpace_height = (int) houghSpace_height_d;
        HoughSpace = new int[houghSpace_widht + 1][houghSpace_height + 1];

        for (int i = 0; i < houghSpace_widht; i++) {
            for (int j = 0; j < houghSpace_height; j++) {
                HoughSpace[i][j] = 0;
            }
        }

        Raster raster = img.getData();
        for (int i = 0; i < imgWidht; i++) {
            for (int j = 0; j < imgHeight; j++) {
                int pixel = raster.getSample(i, j, 0);
                if (pixel == 0) {
                    for (int alfa = 0; alfa < houghSpace_widht; alfa++) {
                        int x = i - (imgWidht / 2);
                        int y = j - (imgHeight / 2);

                        int r = (int) (x * Math.cos(alfa * d_alfa) + (y * Math.sin(alfa * d_alfa)));
                        try {
                            HoughSpace[alfa][(int) (r + (houghSpace_height / 2))]++;
                            //assert (HoughSpace[alfa][(int) (r)]) <= 6000;
                            //assert (HoughSpace[alfa][(int) (r)]) >= 0;
                        } catch (ArrayIndexOutOfBoundsException hata) {
                            System.err.println(houghSpace_widht + "-" + houghSpace_height + " " + alfa + "-" + String.valueOf(r / 2) + (houghSpace_height));
                        }
                    }
                }
            }
        }

        CalcMaxMin(HoughSpace);
        Enchenhancement(5, 5, 100);
        if (ench) {
            
            outputImg = new BufferedImage(houghSpace_widht, houghSpace_height, BufferedImage.TYPE_BYTE_GRAY);
            Raster data = outputImg.getData();
            for (int i = 0; i < houghSpace_widht; i++) {
                for (int j = 0; j < houghSpace_height; j++) {
                    int[] beyaz = new int[] { 255, 255,255 };
                    if(hough_enc[i][j])
                        outputImg.getRaster().setPixel(i, j, beyaz);
                    
                    
                }
            }
        } else {
            outputImg = new BufferedImage(houghSpace_widht, houghSpace_height, BufferedImage.TYPE_BYTE_GRAY);
            Raster data = outputImg.getData();
            for (int i = 0; i < houghSpace_widht; i++) {
                for (int j = 0; j < houghSpace_height; j++) {
                    int stretch = HoughSpace[i][j];//(int) (((HoughSpace[i][j]-Min)*256)/(double)Max);
                    outputImg.getRaster().setSample(i, j, 0, stretch);
                }
            }

        }
        
    }

    boolean[][] hough_enc;

    private void Enchenhancement(int w, int h, int Tc) {
        hough_enc = new boolean[houghSpace_widht][houghSpace_height];
        for (int i = w / 2; i < houghSpace_widht - w / 2; i++) {
            for (int j = h / 2; j < houghSpace_height - h / 2; j++) {
                double bolen = 0;
                for (int x = (-1 * w) / 2; x <= w / 2; x++) {
                    for (int y = (-1 * h) / 2; y <= h / 2; y++) {
                        bolen += HoughSpace[i + x][j + y];
                    }
                }
                double sabit = (h * w * Math.pow(HoughSpace[i][j], 2));
                hough_enc[i][j] = (sabit / bolen) > Tc;
            }
        }

    }

    int Max = 0;
    int Min = 0;

    private void CalcMaxMin(int[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                int pixel = array[i][j];
                if (pixel > Max) {
                    Max = pixel;
                } else if (pixel < Min) {
                    Min = pixel;
                }
            }
        }
    }

    /*
     private void stretch() {
     BufferedImage tempImg = copyImage(outputImg);

     int imgWidht = tempImg.getWidth();
     int imgHeight = tempImg.getHeight();
     for (int i = 0; i < imgWidht; i++) {
     for (int j = 0; j < imgHeight; j++) {
     int pixel = tempImg.getRaster().getSample(i, j, 0);
     if (pixel > Max) {
     Max = pixel;
     } else if (pixel < Min) {
     Min = pixel;
     }
     }
     }

     for (int i = 0; i < imgWidht; i++) {
     for (int j = 0; j < imgHeight; j++) {
     int newPixel = ((tempImg.getRaster().getSample(i, j, 0) - Min) / (Max - Min)) * 65536;
     outputImg.getRaster().setSample(i, j, 0, newPixel);
     }
     }

     }*/
    @Override
    public void run() {
        processHoughTransform();
    }

}
