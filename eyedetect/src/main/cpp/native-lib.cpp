#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>
#include <opencv2/imgproc/types_c.h>
#include <vector>
#include <android/log.h>

#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, "EyeDetectHelper", __VA_ARGS__)


using namespace std;
using namespace cv;

class CascadeDetectorAdapter : public DetectionBasedTracker::IDetector {
public:
    CascadeDetectorAdapter(cv::Ptr<cv::CascadeClassifier> detector) :
            IDetector(),
            Detector(detector) {
        CV_Assert(detector);
    }

    void detect(const cv::Mat &Image, std::vector<cv::Rect> &objects) {
        Detector->detectMultiScale(Image, objects, scaleFactor, minNeighbours, 0, minObjSize,
                                   maxObjSize);
    }

    virtual ~CascadeDetectorAdapter() {
    }

private:
    CascadeDetectorAdapter();

    cv::Ptr<cv::CascadeClassifier> Detector;
};

extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_face1_FaceTracker_nativeCreateObject(JNIEnv *env, jobject thiz, jstring model_) {
    const char *model = env->GetStringUTFChars(model_, 0);

    jlong result = 0;
    Ptr<CascadeClassifier> cascade = makePtr<CascadeClassifier>(model);
    result = (jlong) new CascadeDetectorAdapter(cascade);
    return result;

}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_face1_FaceTracker_nativeStart(JNIEnv *env, jobject clazz, jlong thiz) {

    if (thiz != 0) {
//        opencv 4 需要run
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_face1_FaceTracker_nativeDestoryObject(JNIEnv *env, jobject clazz,
                                                       jlong thiz) {
    if (thiz != 0) {
        delete ((CascadeDetectorAdapter *) thiz);
    }
}


extern "C"
JNIEXPORT jfloat JNICALL
Java_com_example_face1_FaceTracker_detectEyesDistance(JNIEnv *env, jobject clazz, jlong thiz,
                                                      jbyteArray bytes,
                                                      jint width, jint height, jint camera_type,
                                                      jfloat focal_length, jboolean needRoation) {

    if (thiz == 0)
        return -1;

    jbyte *imputImage = env->GetByteArrayElements(bytes, 0);

    Mat img(height + height / 2, width, CV_8UC1, (unsigned char *) imputImage);
    Mat imgRgb;
    cvtColor(img, imgRgb, CV_YUV2BGR_NV21, 4);
    int compressHeight = 1;
    if (needRoation) {//前置
        rotate(imgRgb, imgRgb, ROTATE_90_COUNTERCLOCKWISE);
        compressHeight = (int) (width * 400 / height); // 旋转之后，高宽互调
    } else {
        compressHeight = (int) (400 * height / width);
    }
    Mat source;

    resize(imgRgb, source, Size(400, compressHeight));

    vector<Rect> faces;
    Mat faceGray;

    //灰度处理（彩色图像变为黑白）
    cvtColor(source, faceGray, CV_RGB2GRAY);
    //灰度图象直方图均衡化（归一化图像亮度和增强对比度）
    equalizeHist(faceGray, faceGray);
//    Detector->detectMultiScale(Image, objects, scaleFactor, minNeighbours, 0, minObjSize,maxObjSize);
//    faceCascade.detectMultiScale(faceGray, faces, 1.1, 3, 0 | CV_HAAR_SCALE_IMAGE, Size(30, 30), Size(300, 300));
    ((CascadeDetectorAdapter *) thiz)->setScaleFactor(1.1);
    ((CascadeDetectorAdapter *) thiz)->setMinObjectSize(Size(30, 30));
    ((CascadeDetectorAdapter *) thiz)->setMaxObjectSize(Size(300, 300));
    ((CascadeDetectorAdapter *) thiz)->detect(faceGray, faces);


//    ((DetectorAgregator *) thiz)->tracker->process(faceGray);
//    ((DetectorAgregator *) thiz)->tracker->getObjects(faces);

    vector<Rect> eyes(2);

    bool flag = false;
    int iCount = faces.size();
    if (iCount < 2) return -1;


    Rect one, other;
    int minDiff = 10000, tempDiff = 0;
    for (vector<Rect>::const_iterator r = faces.begin(); r != faces.end(); r++) {
        for (vector<Rect>::const_iterator t = faces.begin(); t != faces.end(); t++) {
            if (r == t) continue;
            tempDiff = abs(r->y - t->y);
            if (tempDiff < minDiff && tempDiff <= 50 && abs(r->x - t->x) >= 50) {
                flag = true;
                one = *r;
                other = *t;
                minDiff = tempDiff;
            }
        }
    }

    if (flag) {
        eyes.clear();
        eyes.push_back(one);
        eyes.push_back(other);

    } else {
        return -1;
    }
    int cameraDistance = abs(eyes[0].x - eyes[1].x);
    float m = 3500 * focal_length / 4;
    int distance = cvRound(m / cameraDistance);
    env->ReleaseByteArrayElements(bytes, imputImage, 0);
    return needRoation ? distance : distance * 0.8;
}