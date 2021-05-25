#include <iostream>
#include <time.h>
#include <dirent.h>
#include <fstream>

#include <opencv2/imgproc.hpp>
#include <opencv2/ximgproc.hpp>
#include <opencv2/imgcodecs.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/core/utility.hpp>


using namespace std;
using namespace cv;
using namespace cv::ximgproc;

int main(int argc, char** argv)
{
	clock_t prog_st, prog_ed;
	prog_st = clock();

	// Fine Tuning for LSD
	std::string inputDirectory_4 = "C:\\Users\\achen\\Dell 5000 gaming backup\\SDD backup\\Users\\achen\\Desktop\\Spring 2020\\Master's Thesis\\Code\\LSD Results\\EX0\\Fine Tuning\\test";
	std::string outputDirectory_4 = "C:\\Users\\achen\\Dell 5000 gaming backup\\SDD backup\\Users\\achen\\Desktop\\Spring 2020\\Master's Thesis\\Code\\LSD Results\\EX0\\Fine Tuning\\6_line_detection_PH";
	std::string resDirectory_4 = "C:\\Users\\achen\\Dell 5000 gaming backup\\SDD backup\\Users\\achen\\Desktop\\Spring 2020\\Master's Thesis\\Code\\LSD Results\\EX0\\Fine Tuning\\res.txt";


	//Open directory
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
		std::string fileName = inputDirectory_4 + "\\" + std::string(_dirent->d_name);
		cv::Mat rawImage = cv::imread(fileName.c_str(), 0);

		if (rawImage.empty())
		{
			return -1;
		}

		namedWindow("rawImage", WINDOW_NORMAL);
		imshow("rawImage", rawImage);

		if (rawImage.data == NULL)
		{
			printf("Cannot Open Image\n");
			continue;
		}


		// Add any image filter here
		//Mat src, dst, cdst, dst1;

		// LSD image read
		//Mat rawImage = imread(in, IMREAD_GRAYSCALE);

		// Create LSD detector
		Ptr<LineSegmentDetector> lsd = createLineSegmentDetector();
		vector<Vec4f> lines_lsd;

		// Because of some CPU's power strategy, it seems that the first running of
		// an algorithm takes much longer. So here we run both of the algorithmes 10
		// times to see each algorithm's processing time with sufficiently warmed-up
		// CPU performance.
		for (int run_count = 0; run_count < 10; run_count++) {
			lines_lsd.clear();
			int64 start_lsd = getTickCount();
			lsd->detect(rawImage, lines_lsd);

			// Detect the lines with LSD
			double freq = getTickFrequency();
			double duration_ms_lsd = double(getTickCount() - start_lsd) * 1000 / freq;
			std::cout << "Elapsed time for LSD: " << duration_ms_lsd << " ms." << std::endl;

			// write LSD processing time to file
			out << duration_ms_lsd << "\n";

		}

		// Show found lines with LSD
		Mat line_image_lsd(rawImage);
		lsd->drawSegments(line_image_lsd, lines_lsd);
		imshow("LSD result", line_image_lsd);

		fileName = outputDirectory_4 + "\\" + std::string(_dirent->d_name);
		cv::imwrite(fileName.c_str(), line_image_lsd);

	} closedir(directory);

	waitKey();

	prog_ed = clock();
	double total_time_taken = double(prog_ed - prog_st) / double(CLOCKS_PER_SEC);
	printf("Total time taken: %.2fs\n", total_time_taken);

	// close file for quantitative results
	out << total_time_taken << "\n";
	out.close();

	return 0;
}