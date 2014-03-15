package fr.xebia.bumblebee;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.avcodec;
import com.googlecode.javacv.cpp.avformat;
import com.googlecode.javacv.cpp.opencv_core;

public class BgSub {
    public static void main(String[] args) throws Exception {

        CanvasFrame canvas = new CanvasFrame("VideoCanvas");

        /*canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(args[0]);

        grabber.start();
        canvas.setCanvasSize(grabber.getImageWidth(), grabber.getImageHeight());

        while (true)
        {
            System.out.println("Reading...");
            canvas.showImage(grabber.grab());
        }*/
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.start();
        opencv_core.IplImage grabbedImage = grabber.grab();

        System.out.println("framerate = " + grabber.getFrameRate());
        grabber.setFrameRate(grabber.getFrameRate());

        avcodec.avcodec_register_all();
        avformat.av_register_all();

        while (canvas.isVisible() && (grabbedImage = grabber.grab()) != null) {
            canvas.showImage(grabbedImage);
        }
        grabber.stop();

    }

}
