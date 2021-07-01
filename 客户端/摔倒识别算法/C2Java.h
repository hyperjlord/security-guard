#pragma once
#include<iostream>  
#include<opencv2\core\core.hpp>  
#include<opencv2\highgui\highgui.hpp>  
#include<opencv2\imgproc\imgproc.hpp>

inline void danger()
{
	std::cout << "I'm in danger" << std::endl;
}
inline void result(const char *address)
{
	const unsigned char FORE_GROUD = 255;
	int thresh = 8;
	cv::VideoCapture video = cv::VideoCapture(address);
	if (!video.isOpened())
	{
		std::cout << "Read video Failed !" << std::endl;
		return;
	}

	std::vector<cv::Point> c2;
	int count = 0;
	cv::Mat currentBGRFrame;

	cv::Mat previousSecondGrayFrame;
	cv::Mat previousFirstGrayFrame;
	cv::Mat currentGaryFrame;

	cv::Mat previousFrameDifference;//previousFrameFirst - previousFrameSecond�Ĳ��  
	cv::Mat currentFrameDifference;//currentFrame - previousFrameFirst;  

	cv::Mat absFrameDifferece;

	cv::Mat previousSegmentation;
	cv::Mat currentSegmentation;
	cv::Mat segmentation;

	cv::namedWindow("segmentation", 1);
	cv::createTrackbar("��ֵ:", "segmentation", &thresh, FORE_GROUD, NULL);
	int numberFrame = 0;

	int flag = 0;
	int flagSum = 0;
	cv::Mat morphologyKernel = cv::getStructuringElement(cv::MORPH_RECT, cv::Size(3, 3), cv::Point(-1, -1));
	for (;;)
	{
		video >> currentBGRFrame;

		if (!currentBGRFrame.data)
			break;

		numberFrame++;
		cvtColor(currentBGRFrame, currentGaryFrame, cv::COLOR_BGR2GRAY);

		if (numberFrame == 1)
		{
			previousSecondGrayFrame = currentGaryFrame.clone();
			imshow("video", currentBGRFrame);
			continue;
		}
		else if (numberFrame == 2)
		{
			previousFirstGrayFrame = currentGaryFrame.clone();

			subtract(previousFirstGrayFrame, previousSecondGrayFrame, previousFrameDifference, cv::Mat(), CV_16SC1);
			absFrameDifferece = abs(previousFrameDifference);
			absFrameDifferece.convertTo(absFrameDifferece, CV_8UC1, 1, 0);
			threshold(absFrameDifferece, previousSegmentation, double(thresh), double(FORE_GROUD), cv::THRESH_BINARY);
			imshow("video", currentBGRFrame);
			continue;
		}

		else
		{
			subtract(currentGaryFrame, previousFirstGrayFrame, currentFrameDifference, cv::Mat(), CV_16SC1);
			absFrameDifferece = abs(currentFrameDifference);

			absFrameDifferece.convertTo(absFrameDifferece, CV_8UC1, 1, 0);

			threshold(absFrameDifferece, currentSegmentation, double(thresh), double(FORE_GROUD), cv::THRESH_BINARY);
 
			bitwise_and(previousSegmentation, currentSegmentation, segmentation);


			medianBlur(segmentation, segmentation, 3);

			separateGaussianFilter(segmentation, 3, 2);

			Guass_high_pass(segmentation);
			morphologyEx(segmentation, segmentation, cv::MORPH_CLOSE, morphologyKernel, cv::Point(-1, -1), 2, cv::BORDER_REPLICATE);

			cv::Ptr<cv::CLAHE> clahe =cv:: createCLAHE(3.0, cv::Size(8, 8));
			cv::Mat O;
			clahe->apply(segmentation, O);
			Sobel(segmentation);
			std::vector<std::vector<cv::Point> > contours;
			std::vector<cv::Vec4i> hierarchy;

			cv::Mat tempSegmentation = segmentation.clone();
			findContours(segmentation, contours, hierarchy, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE, cv::Point(0, 0));//CV_RETR_TREE  
			std::vector< std::vector<cv::Point> > contours_poly(contours.size());

			std::vector<cv::Rect> boundRect;
			boundRect.clear();

			for (int index = 0; index < contours.size(); index++)
			{
				approxPolyDP(cv::Mat(contours[index]), contours_poly[index], 3, true);
				cv::Rect rect = cv::boundingRect(cv::Mat(contours_poly[index]));
				double  a, b, c;
				a = rect.width;
				b = rect.height;
				c = a / b;

				cv::Point cpt;
				cpt.x = rect.x + cvRound(rect.width / 2.0);							//����
				cpt.y = rect.y + cvRound(rect.height / 2.0);
				c2.push_back(cpt);
				count++;



				if ((a * b > 700) && a < b)
				{
					rectangle(currentBGRFrame, rect, cv::Scalar(0, 255, 0), 2);//���˵�һЩ����,��ʾ��ɫ
					flag = 0;
				}
				else if ((a > 1.5 * b) && (a * b > 400)) {
					rectangle(currentBGRFrame, rect, cv::Scalar(0, 0, 255), 2);//���˵�һЩ������ʾ��ɫ
					double a2 = c2[count - 2].x - c2[count - 1].x, b2 = c2[count - 2].y - c2[count - 1].y;
					a2 = fabs(a2), b2 = fabs(b2);																//ȡ����ֵ
					if (a2 > 2 || b2 > 2)																	//���ı仯������ֵ
					{
						flag = flag + 1;
						flagSum = flagSum + 1;
					}
					//������֡ �����ܹ�5֡���λ���ˤ��
					if (flag > 3 || flagSum > 5)
					{
						danger();
					}

				}
			}

			imshow("video", currentBGRFrame);


			imshow("segmentation", segmentation);


			previousFirstGrayFrame = currentGaryFrame.clone();


			previousSegmentation = currentSegmentation.clone();
		}
		cv::waitKey(50);
	}
}
