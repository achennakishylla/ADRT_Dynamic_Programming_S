#include <opencv2/highgui.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/core/utility.hpp>
#include <iostream>
#include <time.h>
#include <dirent.h>
#include <fstream>

using namespace cv;
using namespace std;

int main(int argc, char** argv)
{
	clock_t prog_st, prog_ed;
	prog_st = clock();

	// Date: 06/10/2020 switch to branch subchanges
	//Initialize directory
	std::string inputDirectory = "C:\\Users\\achen\\Desktop\\Spring 2020\\Master's Thesis\\Code\\Hough Transform Results\\Dataset";
	std::string outputDirectory = "C:\\Users\\achen\\Desktop\\Spring 2020\\Master's Thesis\\Code\\Hough Transform Results\\Results\\5_line_detection";
	std::string dir_GB = "C:\\Users\\achen\\Desktop\\Spring 2020\\Master's Thesis\\Code\\Hough Transform Results\\Results\\2_Gaussian Blur";
	std::string dir_Sobel = "C:\\Users\\achen\\Desktop\\Spring 2020\\Master's Thesis\\Code\\Hough Transform Results\\Results\\3_Sobel";
	std::string dir_resize = "C:\\Users\\achen\\Desktop\\Spring 2020\\Master's Thesis\\Code\\Hough Transform Results\\Results\\1_Resize";
	std::string dir_cvtColor = "C:\\Users\\achen\\Desktop\\Spring 2020\\Master's Thesis\\Code\\Hough Transform Results\\Results\\4_CvtColor";
	std::string inputDirectory_2 = "C:\\Users\\achen\\Desktop\\Spring 2020\\Master's Thesis\\Code\\Hough Transform Results\\Dataset\\sobel";
	std::string outputDirectory_2 = "C:\\Users\\achen\\Desktop\\Spring 2020\\Master's Thesis\\Code\\Hough Transform Results\\Results\\6_line_detection_PH";
	std::string resDirectory = "C:\\Users\\achen\\Desktop\\Spring 2020\\Master's Thesis\\Code\\Hough Transform Results\\EX4\\Quantitative Results\\res.txt";

	// For Large dataset
	std::string dir_cvtColor_3 = "C:\\Users\\achen\\Desktop\\Spring 2020\\Master's Thesis\\Code\\Hough Transform Results\\EX4\\4_CvtColor";
	std::string inputDirectory_3 = "C:\\Users\\achen\\Desktop\\Spring 2020\\Master's Thesis\\Code\\Hough Transform Results\\EX4\\sobel";
	std::string outputDirectory_3 = "C:\\Users\\achen\\Desktop\\Spring 2020\\Master's Thesis\\Code\\Hough Transform Results\\EX4\\6_line_detection_PH";

	// Fine Tuning for PHT
	std::string dir_cvtColor_4 = "C:\\Users\\achen\\Desktop\\Spring 2020\\Master's Thesis\\Code\\Hough Transform Results\\EX3\\Fine Tuning\\4_CvtColor";
	std::string inputDirectory_4 = "C:\\Users\\achen\\Desktop\\Spring 2020\\Master's Thesis\\Code\\Hough Transform Results\\EX3\\Fine Tuning\\test";
	std::string outputDirectory_4 = "C:\\Users\\achen\\Desktop\\Spring 2020\\Master's Thesis\\Code\\Hough Transform Results\\EX3\\Fine Tuning\\6_line_detection_PH";
	std::string resDirectory_4 = "C:\\Users\\achen\\Desktop\\Spring 2020\\Master's Thesis\\Code\\Hough Transform Results\\EX3\\Fine Tuning\\res.txt";

	//Open directory
	//DIR *directory = opendir(inputDirectory.c_str());
	DIR *directory = opendir(inputDirectory_4.c_str());
	struct dirent *_dirent = NULL;

	//Check if directory exists
	if (directory == NULL)
	{
		printf("Cannot open Input Folder\n");
		return 1;
	}

	// create file for quantitative results
	std::ofstream out(resDirectory_4);
	out << "Time Taken in seconds\n";

	//load images from input directory
	while ((_dirent = readdir(directory)) != NULL)
	{
		//std::string fileName = inputDirectory + "\\" + std::string(_dirent->d_name);
		std::string fileName = inputDirectory_4 + "\\" + std::string(_dirent->d_name);
		cv::Mat rawImage = cv::imread(fileName.c_str(),0);
	
		if (rawImage.data == NULL)
		{
			printf("Cannot Open Image\n");
			continue;
		}

		// Add any image filter here
		Mat src, dst, cdst, dst1;

		//resize image
		//resize(rawImage, src, Size(960, 540));
		//fileName = dir_resize + "\\" + std::string(_dirent->d_name);
		//cv::imwrite(fileName.c_str(), src);

/*
		//perform gaussian blur on the source image after resize
		GaussianBlur(rawImage, dst1, Size(3, 3), 0);
		fileName = dir_GB + "\\" + std::string(_dirent->d_name);
		cv::imwrite(fileName.c_str(), dst1);

		//perform sobel on image after gaussian blur
		Sobel(dst1, dst, CV_8U, 1, 1, 3);
		//Canny(dst1, dst, 50, 50, 3);
		fileName = dir_Sobel + "\\" + std::string(_dirent->d_name);
		cv::imwrite(fileName.c_str(), dst);
*/

		//perform cvtColor on the image after Sobel
		//cvtColor(dst, cdst, CV_GRAY2BGR);
		cvtColor(rawImage, cdst, CV_GRAY2BGR);
		fileName = dir_cvtColor_4 + "\\" + std::string(_dirent->d_name);
		cv::imwrite(fileName.c_str(), cdst);

		//declare a vector for storing line coordinates for Hough Tranform
		//vector<Vec2f> lines;

		//declare a vector for storing line coordinates for HoughLinesP 
		vector<Vec4i> lines;

		//Run Houghlines() and check the runtime
		clock_t start, end;
		start = clock();
		//HoughLines(dst, lines, 1, CV_PI / 180, 150, 0, 0);
		//HoughLines(dst, lines, 1, CV_PI / 8192, 150, 0, 0);
		//HoughLines(rawImage, lines, 1, CV_PI / 180, 150, 0, 0);
		HoughLinesP(rawImage, lines, 1, CV_PI / 8192, 400, 0, 0);
		end = clock();
		double time_taken = double(end - start) / double(CLOCKS_PER_SEC);
		printf("Time taken: %.2fs\n", time_taken);

		// write PHT time to file
		out << time_taken<<"\n";

		// draw lines for HoughLines()
/*
		for (size_t i = 0; i < lines.size(); i++)
		{
			float rho = lines[i][0], theta = lines[i][1];
			Point pt1, pt2;
			double a = cos(theta), b = sin(theta);
			double x0 = a * rho, y0 = b * rho;
			pt1.x = cvRound(x0 + 1000 * (-b));
			pt1.y = cvRound(y0 + 1000 * (a));
			pt2.x = cvRound(x0 - 1000 * (-b));
			pt2.y = cvRound(y0 - 1000 * (a));
			line(cdst, pt1, pt2, Scalar(0, 0, 255), 3, CV_AA);
			//line(rawImage, pt1, pt2, Scalar(0, 0, 255), 3, CV_AA);
		}
*/

		// draw lines HoughLinesP()
		for (size_t i = 0; i < lines.size(); i++)
		{
			line(cdst, Point(lines[i][0], lines[i][1]),Point(lines[i][2], lines[i][3]), Scalar(0, 0, 255), 3, 8);
		}

		// end image filter

		//fileName = outputDirectory + "\\" + std::string(_dirent->d_name);
		// Save to probabilistic HT output directory
		fileName = outputDirectory_4 + "\\" + std::string(_dirent->d_name);
		cv::imwrite(fileName.c_str(), cdst);
		//cv::imwrite(fileName.c_str(), rawImage);
	} closedir(directory);


/*
	Mat src1 = imread(argv[1], 0);
	Mat src, dst, cdst;rawImage
	Mat dst1;
	//resize(src1, src, Size(960, 540)); // for NAL.jpg
	//Add a gaussian blur here
	GaussianBlur(src1, dst1, Size(15, 15), 0);
	//Canny(dst1, dst, 50, 50, 3); //for input.png , radon_input.png
	Sobel(dst1, dst, CV_8U, 1, 1, 3);
	//Canny(src, dst, 50, 50, 3); // for desk.jpg
	//Canny(src, dst, 50, 50, 3); //for NAL.jpg
	//Canny(src, dst, 50, 400, 3); // for TDP.jpg
	//Canny(src, dst, 50, 300, 3); // for BT.jpg
	//Canny(src, dst, 50, 20, 3); // for GA.jpg
	//Canny(src, dst, 50, 300, 3); // for TM.jpg
	namedWindow("canny", WINDOW_AUTOSIZE); // Create a window for display.
	imshow("canny", dst * 10);
	cvtColor(dst, cdst, CV_GRAY2BGR);

	vector<Vec2f> lines;
	// detect lines
	//HoughLines(dst, lines, 1, CV_PI / 180, 50, 0, 0); // for input.png
	//Check how fast the call to houghlines
	clock_t tStart = clock();
	HoughLines(dst, lines, 1, CV_PI / 8192, 100, 0, 0);
	//HoughLines(dst, lines, 1, CV_PI / 180, 300, 0, 0); // for radon_input.png, NAL.jpg, TDP.jpg
	printf("Time taken: %.2fs\n", (double)(clock() - tStart) / CLOCKS_PER_SEC);
	//HoughLines(dst, lines, 1, CV_PI / 180, 100, 0, 0); // for BT.jpg
	//HoughLines(dst, lines, 1, CV_PI / 180, 300, 0, 0); // for GA.jpg
	//HoughLines(dst, lines, 1, CV_PI / 180, 170, 0, 0); // for TM.jpg

	// draw lines
	for (size_t i = 0; i < lines.size(); i++)
	{
		float rho = lines[i][0], theta = lines[i][1];
		Point pt1, pt2;
		double a = cos(theta), b = sin(theta);
		double x0 = a * rho, y0 = b * rho;
		pt1.x = cvRound(x0 + 1000 * (-b));
		pt1.y = cvRound(y0 + 1000 * (a));
		pt2.x = cvRound(x0 - 1000 * (-b));
		pt2.y = cvRound(y0 - 1000 * (a));
		line(cdst, pt1, pt2, Scalar(0, 0, 255), 3, CV_AA);
	}

	namedWindow("source", WINDOW_NORMAL); // Create a window for display.
	imshow("source", src1);
	namedWindow("detected lines", WINDOW_NORMAL);
	imshow("detected lines", cdst);
	imwrite("input.png", cdst);
	//imwrite("National_Architecture_Library.jpg", cdst);
*/
	waitKey();

	prog_ed = clock();
	double total_time_taken = double(prog_ed - prog_st) / double(CLOCKS_PER_SEC);
	printf("Total time taken: %.2fs\n", total_time_taken);

	// close file for quantitative results
	out << total_time_taken<<"\n";
	out.close();

	return 0;


}