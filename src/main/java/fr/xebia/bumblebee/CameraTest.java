package fr.xebia.bumblebee;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

public class CameraTest {
    static int minHeight = 20;
    static int minWidth = 10;

    public static void main(String[] args) throws Exception {
        URL videoUrl = CameraTest.class.getResource("/output.mov");
        File video = new File(videoUrl.toURI());
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(video);//"resources/main/cut.mov");
        grabber.start();


        System.out.println("framerate = " + grabber.getFrameRate());
        int width = grabber.getImageWidth(), height = grabber.getImageHeight();
        grabber.setFrameRate(grabber.getFrameRate());

        // source init
        CanvasFrame sourceCanvas = new CanvasFrame("Source");
        sourceCanvas.setCanvasSize(width, height);

        // post-treatment init
        CanvasFrame outputcanvas = new CanvasFrame("Post-treatment");
        sourceCanvas.setCanvasSize(width, height);

        IplImage outputImg = null;
        IplImage hsvImg = null;
        IplImage redImg = null;
        IplImage greenImg = null;
        IplImage blueImg = null;

        // background subtraction
        //opencv_video.BackgroundSubtractorMOG2 pMOG2 = new opencv_video.BackgroundSubtractorMOG2(30, 16, true); //MOG2 approach

        boolean canGrab = true;
        try {
            while (outputcanvas.isVisible() && sourceCanvas.isVisible() && canGrab) {
                final IplImage frame = grabber.grab();
                canGrab = frame != null;
                if (outputImg == null) {
                    outputImg = IplImage.create(cvGetSize(frame), 8, 1);
                }
                //cvCvtColor(frame, outputImg, CV_BGR2GRAY);
                //pMOG2.apply(outputImg, outputImg, -1);
                //GaussianBlur(outputImg, outputImg, new opencv_core.CvSize(3, 3), 0, 0, BORDER_DEFAULT);
                //cvAdaptiveThreshold(outputImg, outputImg, 255, CV_ADAPTIVE_THRESH_GAUSSIAN_C, CV_THRESH_BINARY_INV, 5, 4);
            /*CvMemStorage mem = CvMemStorage.create();
            double distanceResolutionInPixels = 1;
            double angleResolutionInRadians = Math.PI / 180;
            int minimumVotes = 200;
            double unused = 0;
            opencv_core.CvSeq lines = cvHoughLines2(
                    outputImg,
                    mem,
                    CV_HOUGH_STANDARD,
                    distanceResolutionInPixels,
                    angleResolutionInRadians,
                    minimumVotes,
                    unused, unused);

            List<List<CvPoint>> vectors = new ArrayList<List<CvPoint>>();
            for (int i = 0; i < lines.total(); i++) {
                opencv_core.CvPoint2D32f point = new opencv_core.CvPoint2D32f(cvGetSeqElem(lines, i));
                float rho = point.x();
                float theta = point.y();
                double a = Math.cos(theta);
                double b = Math.sin(theta);
                double x0 = a * rho;
                double y0 = b * rho;
                opencv_core.CvPoint pt1 = new opencv_core.CvPoint(
                        new Long(Math.round(x0 + 1000 * (-b))).intValue(),
                        new Long(Math.round(y0 + 1000 * (a))).intValue());
                opencv_core.CvPoint pt2 = new opencv_core.CvPoint(
                        new Long(Math.round(x0 - 1000 * (-b))).intValue(),
                        new Long(Math.round(y0 - 1000 * (a))).intValue());
                List<CvPoint> vector = new ArrayList<CvPoint>();
                vector.add(pt1);
                vector.add(pt2);
                vectors.add(vector);
                *//*System.out.println("line" +
                        " P1(" + point.position(0).x() + ", " + point.position(0).y() + ")" +
                        " P2(" + point.position(1).x() + ", " + point.position(1).y() + ")" +
                        " in " + grabber.getImageWidth() + "x" + grabber.getImageHeight());*//*

                cvLine(frame, pt1, pt2, CvScalar.GREEN, 1, 8, 0);
            }*/

            /*for (int i = 0; i < vectors.size(); i++) {
                List<CvPoint> vectorA = vectors.get(i);
                for (int j = i + 1; j < vectors.size(); j++) {
                    List<CvPoint> vectorB = vectors.get(j);
                    computeIntersect(
                            vectorA.get(0), vectorA.get(1),
                            vectorB.get(0), vectorB.get(1));
                    cvLine(frame, vectorA.get(0), vectorA.get(1), CvScalar.BLUE, 1, 8, 0);
                    cvLine(frame, vectorB.get(0), vectorB.get(1), CvScalar.BLUE, 1, 8, 0);
                }
            }*/

                if (hsvImg == null) {
                    hsvImg = cvCreateImage(cvGetSize(frame), 8, 3);
                    redImg = cvCreateImage(cvGetSize(frame), 8, 1);
                    greenImg = cvCreateImage(cvGetSize(frame), 8, 1);
                    blueImg = cvCreateImage(cvGetSize(frame), 8, 1);
                }
                cvSplit(frame, blueImg, greenImg, redImg, null);
                cvAdd(blueImg, greenImg, greenImg, null);
                cvSub(redImg, greenImg, redImg, null);
                cvThreshold(redImg, redImg, 20, 255, CV_THRESH_BINARY);

                CvMemStorage mem = CvMemStorage.create();
                CvSeq contours = new CvSeq();
                CvSeq ptr;
                cvFindContours(redImg, mem, contours, Loader.sizeof(CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);

                List<CvRect> rectangles = new ArrayList<>();
                for (ptr = contours; ptr != null; ptr = ptr.h_next()) {
                    rectangles.add(cvBoundingRect(ptr, 0));
                }
                Optional<CvRect> optPhoneRect = rectangles.stream()
                        .filter(rect -> rect.height() > minHeight && rect.width() > minWidth)
                        .findFirst();

                if (optPhoneRect.isPresent()) {
                    CvRect phoneRect = optPhoneRect.get();
                    IplImage cropped = cvCreateImage(cvSize(phoneRect.width(), phoneRect.height()), IPL_DEPTH_8U, 3);
                    IplImage croppedGray = cvCreateImage(cvGetSize(cropped), IPL_DEPTH_8U, 1);
                    cvCvtColor(cropped, croppedGray, CV_BGR2GRAY);
                    IplImage croppedThresholded = croppedGray.clone();
                    cvSetImageROI(frame, phoneRect);
                    cvCopy(frame, cropped);
                    cvResetImageROI(frame);

                    //cvThreshold(cropped, cropped, 255, 255, CV_THRESH_BINARY_INV);
                    cvAdaptiveThreshold(croppedGray, croppedThresholded, 255, CV_ADAPTIVE_THRESH_MEAN_C, CV_THRESH_BINARY_INV, 5, 4);
                    outputImg = croppedGray;//croppedThresholded;

                    cvRectangle(frame, cvPoint(phoneRect.x(), phoneRect.y()),
                            cvPoint(phoneRect.x() + phoneRect.width(), phoneRect.y() + phoneRect.height()),
                            CvScalar.BLUE, 1, 0, 0);

                    sourceCanvas.showImage(frame);
                    outputcanvas.setCanvasSize(outputImg.width() * 5, outputImg.height() * 5);
                    outputcanvas.showImage(outputImg);
                }
            }
        } finally {
            grabber.stop();
            outputcanvas.dispose();
            sourceCanvas.dispose();
        }
    }

    static CvPoint computeIntersect(CvPoint a1, CvPoint a2, CvPoint b1, CvPoint b2) {
        int x1 = a1.x(), y1 = a1.y(), x2 = a2.x(), y2 = a2.y();
        int x3 = b1.x(), y3 = b1.y(), x4 = b2.x(), y4 = b2.y();
        float d = 0;
        if ((d = ((float) (x1 - x2) * (y3 - y4)) - ((y1 - y2) * (x3 - x4))) > 0) {
            CvPoint pt = new CvPoint();
            pt.x((int) (((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d));
            pt.y((int) (((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d));
            return pt;
        } else
            return new CvPoint(-1, -1);
    }

}