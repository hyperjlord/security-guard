#include <math.h>
#include <opencv2/opencv.hpp>

float TestOpencv(float* buf, int len)
{
    cv::Mat mat = cv::Mat(len, 1, CV_32FC1, buf);
    auto sum = cv::sum(mat);
    return sum.val[0];
}

float TestMath()
{
    return sqrt(2.0f);
}