// New Branch
// Dated: 04/06/2020
package testPackage;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
//importing required sharpening libraries
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

@SuppressWarnings("serial")
public class FastPanel extends JPanel {

	// fields
	private double maxAveFinal;
	private BufferedImage image;
	private BufferedImage newImage;
	private BufferedImage finalImage;
	private Graphics2D graphic;
	private Graphics2D g2;
	private Graphics2D finalGraphic;
	private String path = "";
	private static final GraphicsConfiguration configu = GraphicsEnvironment.getLocalGraphicsEnvironment()
			.getDefaultScreenDevice().getDefaultConfiguration();

	// Settings
	private static int blur_strength = 1500;   //Number of times to "blur" the radon image
	private static int blur = 50;              //
	private static double sensitivity = 0; // Initial 0.4
	private static int image_blur_kernel = 3;
	//int count1, count2 = 0;
	
	// Run for dataset
	//private static String outdir = "...dir\\Test Results\\";
	//private static String indir  = "...dir\\images\\";
	//private static String img_dir  = "...dir\\Dataset\\";
	
	//private static String result_dir  = "...dir\\line_detection\\";
	//private static String radon_dir  = "...dir\\sinogram\\";
	//private static String gray_dir  = "...dir\\gray\\";
	//private static String blur_dir  = "...dir\\blur\\";
	//private static String blurx_dir  = "...dir\\blurx\\";
	//private static String sobel_dir  = "...dir\\sobel\\";
	
	// EX - Specify directories for experiments here
	private static String result_dir  = "...dir\\Radon Transform Results\\EX5_test\\6_line_detection\\";
	private static String radon_dir  = "...dir\\Radon Transform Results\\EX5_test\\5_sinogram\\";
	private static String gray_dir  = "...dir\\Radon Transform Results\\EX5_test\\1_gray\\";
	private static String blur_dir  = "...dir\\Radon Transform Results\\EX5_test\\2_blur\\";
	private static String blurx_dir  = "...dir\\Radon Transform Results\\EX5_test\\3_blurx\\";
	private static String sobel_dir  = "...dir\\Radon Transform Results\\EX5_test\\4_sobel\\";
	
	// File representing the image dataset directory folder
	static final File img_dir = new File("...dir\\Radon Transform Results\\EX5_test\\0_EX4_Test 100\\");
	
	//static final File img_dir = new File("...dir\\Dataset\\");
	//static final File result_dir = new File("...dir\\Results\\line_detection\\");
	//static final File radon_dir = new File("...dir\\Results\\sinogram\\");
	//static final File gray_dir = new File("...dir\\Results\\gray\\");
	//static final File blur_dir = new File("...dir\\Results\\blur\\");
	//static final File blurx_dir = new File("...dir\\Results\\blurx\\");
	
	// array of supported extensions
	static final String[] EXTENSIONS = new String[] {
		"gif", "png", "bmp", "jpg"
	};
	
	// filter to identify images based on their extensions
	static final FilenameFilter img_filter = new FilenameFilter() {
		
		@Override
		public boolean accept(final File dir, final String name) {
			for(final String ext : EXTENSIONS) {
				if (name.endsWith("." + ext)) {
					return (true);					
				}
			}
			return false;
		}
	};
	

	/**
	 * a constructor for the FastPanel class the constructor pulls the image from
	 * the file path, finds its derivative, erodes it, and pads it to a 2^n x 2^n
	 * square
	 */

	/*
	public FastPanel() {
		
		// Start loop
		if ((img_dir.isDirectory()) && (result_dir.isDirectory()) && (gray_dir.isDirectory()) && (blur_dir.isDirectory()) && (blurx_dir.isDirectory())) { // make sure it's a directory
            for (final File f : img_dir.listFiles(img_filter)) {
                //BufferedImage img = null;
				try {
					// Run for dataset :: image input directory
					
					//path = indir + "floor2.jpg";
					//System.out.println(path);
					//image = ImageIO.read(new File(path));
					System.out.println("img_dir->> " + img_dir);
					System.out.println("image_name: " + f.getName());
					image = ImageIO.read(f);
					graphic = (Graphics2D) image.getGraphics();
					FastWindow.WIDTH = image.getWidth();
					FastWindow.HEIGHT = image.getHeight();
					int width = image.getWidth();
					int height = image.getHeight();
		
					//
					// Convert to grayscale
					//
					double [][]gray = new double[width][height];
					for (int x=0; x<width; x++) {
						for (int y=0; y<height; y++) {
							Pixel myPixel = new Pixel(x,y);
							gray[x][y] = myPixel.giveBrightness(image);
						}
					}
					
					// Run for dataset
					//saveGray(gray, outdir+"gray.png", width, height);
					saveGray(gray, gray_dir + "gray_"+count1+".png", width, height);
					
					//
					// Blur the image
					//
					double []kernel   = triangularKernel(image_blur_kernel);
					double [][]blurx  = convolveX(gray, kernel, width, height);
					double [][]blurimg  = convolveY(blurx, kernel, width, height);
					
					// Run for dataset
					//saveGray(blurx,  outdir+"blurx.png", width, height);
					//saveGray(blurimg,   outdir+"blur.png", width, height);
					saveGray(blurx, blurx_dir +"blurx_" + count1 + ".png", width, height);
					saveGray(blurimg, blur_dir + "blur_" + count1 + ".png", width, height);
					count1++;
					
					//
					// Take the derivative
					//
					//Morph derivative = new Morph(image);
					//double[][] derivArray = derivative.findDerivative();
					double[][] derivArray = sobel(blurimg, width, height);
					for (int x = 0; x < width; x++) {
						for(int y =0;y<height;y++) {
							derivArray[x][y] *=10;
						}
					}
					
					//
					// Draw the derivative
					//
					for (int x = 0; x < width - 2; x++) {
						for (int y = 0; y < height - 2; y++) {
							double arrayValue = derivArray[x][y];
							int rgbNum = (int) Math.floor(arrayValue);
							try {
								Color color = new Color(rgbNum, rgbNum, rgbNum);
								graphic.setColor(color);
								graphic.fillRect(x, y, 1, 1);
							} catch (IllegalArgumentException a) {
								try {
									int newRGB = (int) Math.floor(rgbNum * 0.25);
									Color color = new Color(newRGB, newRGB, newRGB);
									graphic.setColor(color);
									graphic.fillRect(x, y, 1, 1);
								} catch (IllegalArgumentException b) {
									// some values fall outside of 0 to 255 range, doesn't affect the image
								}
							}
						}
					}
		
					//
					// Widen the lines 3x3 kernel
					//
					Morph erosion = new Morph(image);
					double[][] array = erosion.erode();
					for (int x = 1; x < width - 2; x++) {
						for (int y = 1; y < height - 2; y++) {
							double arrayValue = array[x][y];
							int rgbNum = (int) Math.floor(arrayValue);
							try {
								Color color = new Color(rgbNum, rgbNum, rgbNum);
								graphic.setColor(color);
								graphic.fillRect(x, y, 1, 1);
							} catch (IllegalArgumentException e) {
								System.out.println(rgbNum + " is not a valid value");
							}
						}
					}
		
					//
					// padding out the image and fixing the white line left by the derivative
					//
					int control = Math.max(width, height);
					int newN = powerOfTwo(control);
					newImage = configu.createCompatibleImage(newN, newN);
					g2 = (Graphics2D) newImage.getGraphics();
					g2.drawImage(image, 0, 0, null);
					g2.setColor(Color.BLACK);
					g2.fillRect(0, 0, image.getWidth() - 1, 5);
					g2.setColor(Color.BLACK);
					g2.fillRect(0, 0, 10, image.getHeight() - 1);
					g2.setColor(Color.BLACK);
					g2.fillRect(0, image.getHeight() - 2, image.getWidth() - 1, 5);
					g2.setColor(Color.BLACK);
					g2.fillRect(image.getWidth() - 10, 0, 10, image.getHeight() - 1);
		
				} catch (IOException e) {
				  System.out.println("IOException");
				} catch (NullPointerException n) {
				  System.out.println("File name " + f.getName() + " does not exist.");
				}
		
		//end for loop and if
            }
		}

	}

	*/
	/**
	 * a method which calls the radon on the image, and calls makeLine to draw on
	 * the edges. Can also construct the sinogram map
	 */
	public void start() {

		// Switch fast panel code to start() method

		//Write radon processing time to file
		try {
			File file_1 = new File("...dir\\Radon Transform Results\\EX5_test\\quantitative_results\\res.txt");
			if(file_1.createNewFile()) {
				System.out.println("Successfully created");
			}
			else {
				System.out.println("Failed to create or file already exist");
			}
			FileWriter fileWriter_1 = new FileWriter(file_1);
			//System.out.println("Writing fields to file... ");
			fileWriter_1.write(String.format("%-14s\n", "Time Taken by Radon"));
			//System.out.println("Writing data to file... ");
		
		// Start loop
		//if ((img_dir.isDirectory()) && (result_dir.isDirectory()) && (radon_dir.isDirectory()) && (gray_dir.isDirectory()) && (blur_dir.isDirectory()) && (blurx_dir.isDirectory())) { // make sure it's a directory
		if (img_dir.isDirectory()) {    
			for (final File f : img_dir.listFiles(img_filter)) {
                //BufferedImage img = null;
				
				try {
					// Run for dataset :: image input directory
					
					//path = indir + "floor2.jpg";
					//System.out.println(path);
					//image = ImageIO.read(new File(path));
					System.out.println("img_dir->> " + img_dir);
					System.out.println("image_name: " + f.getName());
					image = ImageIO.read(f);
					graphic = (Graphics2D) image.getGraphics();
					FastWindow.WIDTH = image.getWidth();
					FastWindow.HEIGHT = image.getHeight();
					int width = image.getWidth();
					int height = image.getHeight();
		
					//
					// Convert to grayscale
					//
					double [][]gray = new double[width][height];
					for (int x=0; x<width; x++) {
						for (int y=0; y<height; y++) {
							Pixel myPixel = new Pixel(x,y);
							gray[x][y] = myPixel.giveBrightness(image);
						}
					}
					
					// Run for dataset
					//saveGray(gray, outdir+"gray.png", width, height);
					System.out.println("gray_dir->> " + gray_dir);
					saveGray(gray, gray_dir + f.getName(), width, height);
					
					//
					// Blur the image
					//
					double []kernel   = triangularKernel(image_blur_kernel);
					double [][]blurx  = convolveX(gray, kernel, width, height);
					double [][]blurimg  = convolveY(blurx, kernel, width, height);
					
					// Run for dataset
					//saveGray(blurx,  outdir+"blurx.png", width, height);
					//saveGray(blurimg,   outdir+"blur.png", width, height);
					System.out.println("blurx_dir->> " + blurx_dir);
					System.out.println("blur_dir->> " + blur_dir);
					saveGray(blurx, blurx_dir + f.getName(), width, height);
					saveGray(blurimg, blur_dir + f.getName(), width, height);
					//count1++;
					
					//
					// Take the derivative
					//
					//Morph derivative = new Morph(image);
					//double[][] derivArray = derivative.findDerivative();
					double[][] derivArray = sobel(blurimg, width, height);
					for (int x = 0; x < width; x++) {
						for(int y =0;y<height;y++) {
							derivArray[x][y] *=10;
						}
					}
					//save image after applying sobel
					System.out.println("sobel_dir->> " + sobel_dir);
					saveGray(derivArray, sobel_dir + f.getName(), width, height);
					
					//
					// Draw the derivative
					//
					for (int x = 0; x < width - 2; x++) {
						for (int y = 0; y < height - 2; y++) {
							double arrayValue = derivArray[x][y];
							int rgbNum = (int) Math.floor(arrayValue);
							try {
								Color color = new Color(rgbNum, rgbNum, rgbNum);
								graphic.setColor(color);
								graphic.fillRect(x, y, 1, 1);
							} catch (IllegalArgumentException a) {
								try {
									int newRGB = (int) Math.floor(rgbNum * 0.25);
									Color color = new Color(newRGB, newRGB, newRGB);
									graphic.setColor(color);
									graphic.fillRect(x, y, 1, 1);
								} catch (IllegalArgumentException b) {
									// some values fall outside of 0 to 255 range, doesn't affect the image
								}
							}
						}
					}
		
					//
					// Widen the lines 3x3 kernel
					//
					Morph erosion = new Morph(image);
					double[][] array = erosion.erode();
					for (int x = 1; x < width - 2; x++) {
						for (int y = 1; y < height - 2; y++) {
							double arrayValue = array[x][y];
							int rgbNum = (int) Math.floor(arrayValue);
							try {
								Color color = new Color(rgbNum, rgbNum, rgbNum);
								graphic.setColor(color);
								graphic.fillRect(x, y, 1, 1);
							} catch (IllegalArgumentException e) {
								System.out.println(rgbNum + " is not a valid value");
							}
						}
					}
		
					//
					// padding out the image and fixing the white line left by the derivative
					//
					int control = Math.max(width, height);
					int newN = powerOfTwo(control);
					newImage = configu.createCompatibleImage(newN, newN);
					g2 = (Graphics2D) newImage.getGraphics();
					g2.drawImage(image, 0, 0, null);
					g2.setColor(Color.BLACK);
					g2.fillRect(0, 0, image.getWidth() - 1, 5);
					g2.setColor(Color.BLACK);
					g2.fillRect(0, 0, 10, image.getHeight() - 1);
					g2.setColor(Color.BLACK);
					g2.fillRect(0, image.getHeight() - 2, image.getWidth() - 1, 5);
					g2.setColor(Color.BLACK);
					g2.fillRect(image.getWidth() - 10, 0, 10, image.getHeight() - 1);
		
	
		// End: Switch fast panel code to start() method
		
				// Calculate the radon transform using O N^2 lg N with ADRT algorithm
				int n = (newImage.getWidth()) / 2;
				int maxRho = (int) Math.floor(Math.sqrt((n * n) + (n * n)));
				
				//write to file radon time
				long start = System.currentTimeMillis();;
				double[][] table = radon(newImage, maxRho, 360);
				long end = System.currentTimeMillis();;
				//System.out.println(((end-start)/1000)+" s  radon time");
				//fileWriter_1.write(String.format("%-14d\n", ((end-start)/1000)));
				
				//For EX1
				System.out.println(((end-start))+" ms  radon time");
				fileWriter_1.write(String.format("%-14d\n", ((end-start))));
				fileWriter_1.flush();
				
				
				// make a backup of the table
				double[][] origtable = new double[maxRho][360];
				for (int r = 0; r < maxRho; r++)
					for (int t = 0; t < 360; t++)
						origtable[r][t] = table[r][t];
		
				//:: blur image changes
				System.out.println("Blurring table\n");
				double newtable[][] = new double[maxRho][360];
		
				//default: 250    for tall buildings: 1000
				for (int i = 0; i < blur_strength; i++) {
					for (int rho1 = 1; rho1 < maxRho - 1; rho1++) {
						for (int theta = 1; theta < 360 - 1; theta++) {
							double blurred = (4 * table[rho1][theta] + table[rho1 + 1][theta] + table[rho1][theta + 1]
									+ table[rho1 - 1][theta] + table[rho1][theta - 1]) / 8;
							newtable[rho1][theta] = Math.max(blurred, table[rho1][theta]);
						}
					}
		
					for (int rho1 = 1; rho1 < maxRho; rho1++) {
						for (int theta = 1; theta < 360; theta++) {
							table[rho1][theta] = newtable[rho1][theta];
						}
					}
		
				}
				//default: 350	  for tall buildings: 100
				for (int i = 0; i < blur; i++) {
					for (int rho1 = 1; rho1 < maxRho - 1; rho1++) {
						for (int theta = 1; theta < 360 - 1; theta++) {
							double blurred = (4 * table[rho1][theta] + table[rho1 + 1][theta] + table[rho1][theta + 1]
									+ table[rho1 - 1][theta] + table[rho1][theta - 1]) / 8;
							newtable[rho1][theta] = blurred;
						}
					}
		
					for (int rho1 = 1; rho1 < maxRho; rho1++) {
						for (int theta = 1; theta < 360; theta++) {
							table[rho1][theta] = newtable[rho1][theta];
						}
					}
				}
				// end:: blur image changes
		
				/*
				 * /:: alternate smooth image changes
				 * System.out.println("Smoothening table\n"); double newtable1[][] = new
				 * double[maxRho][360]; //int maxNeigh = max value of 9 neighbours; int
				 * smoothen_strength = 60; for (int i=0;i<smoothen_strength;i++) { for (int rho1
				 * = 1; rho1 < maxRho-1; rho1++) { for (int theta = 1; theta < 360-1; theta++) {
				 * double maxNeigh =
				 * Math.max(Math.max(Math.max(Math.max(Math.max(Math.max(table[rho1+1][theta+1],
				 * table[rho1][theta]),table[rho1+1][theta]),table[rho1][theta+1]),table[rho1-1]
				 * [theta]),table[rho1][theta-1]),table[rho1-1][theta-1]);
				 * newtable1[rho1][theta] = Math.max(maxNeigh-50, table[rho1][theta]); } }
				 * //newtable1[rho][theta] = max(0.97 * maxNeigh, table[rho][theta]; for (int
				 * rho1 = 1; rho1 < maxRho; rho1++) { for (int theta = 1; theta < 360; theta++)
				 * { table[rho1][theta] = newtable1[rho1][theta]; } } } //end :: smoothen
				 * pixel changes
				 */
		
				// makeLine(table);
				// ArrayList<Pixel> pixelList = makeLine(newtable);
				ArrayList<Pixel> pixelList = new ArrayList<Pixel>();
				int maxRho1 = table.length;
				int maxDegs = table[0].length;
				double percent = maxAveFinal * sensitivity;
		
				// Original version using sensitivity
				/*
				 * // takes out pixels within the threshold, and then removes those which are
				 * too // close together for (int outerRho = 0; outerRho < maxRho; outerRho++) {
				 * for (int outerDegs = 0; outerDegs < maxDegs; outerDegs++) { double currentVal
				 * = table[outerRho][outerDegs]; if (currentVal >= (maxAveFinal - percent)) {
				 * Pixel bigPixel = new Pixel(outerRho, outerDegs);
				 * 
				 * if (suppress(pixelList, bigPixel)) {
				 * 
				 * } else { pixelList.add(bigPixel); } } } }
				 */
		
				// Pure Non-max suppress
				for (int rho = 2; rho < maxRho1 - 2; rho++) {
					for (int deg = 2; deg < maxDegs - 2; deg++) {
						if (table[rho][deg] > table[rho - 1][deg - 1] && table[rho][deg] > table[rho - 1][deg]
								&& table[rho][deg] > table[rho - 1][deg + 1] && table[rho][deg] > table[rho][deg - 1]
								&& table[rho][deg] > table[rho][deg + 1] && table[rho][deg] > table[rho + 1][deg - 1]
								&& table[rho][deg] > table[rho + 1][deg] && table[rho][deg] > table[rho + 1][deg + 1]) {
							Pixel bigPixel = new Pixel(rho, deg);
							pixelList.add(bigPixel);
						}
					}
				}
				
				// create backup of pixelList
				ArrayList<Pixel> origPixelList = new ArrayList<Pixel>();
				for ( int i= 0;i<pixelList.size();i++) {
					origPixelList.add(pixelList.get(i));
				}
		
				// : Create Map of Pixel
				int clustermap[][] = new int[maxRho][360];
				int kmax = 1;
				for (int i = 0; i < kmax; i++) {
					for (int rho2 = 1; rho2 < maxRho - 1; rho2++) {
						for (int theta2 = 1; theta2 < 360 - 1; theta2++) {
							for (int c = 0; c < pixelList.size(); c++) {
								double a = rho2 - pixelList.get(c).x;
								double b = theta2 - pixelList.get(c).y;
								double dist = (a * a) + (b * b);
								dist = Math.sqrt(dist);
								double d = rho2 - pixelList.get(clustermap[rho2][theta2]).x;
								double e = theta2 - pixelList.get(clustermap[rho2][theta2]).y;
								// System.out.println("d = "+d);
								// System.out.println("e = "+e);
								double prevDist = (d * d) + (e * e);
								prevDist = Math.sqrt(prevDist);
								if (dist < prevDist) {
									clustermap[rho2][theta2] = c;
									// System.out.printf("clustermap = " + clustermap[rho2][theta2]);
								}
							}
						}
					}
		
					for (int rho2 = 1; rho2 < maxRho; rho2++) {
						for (int theta2 = 1; theta2 < 360; theta2++) {
							int c = clustermap[rho2][theta2];
							// System.out.println("c = " + c);
							double a = origtable[rho2][theta2];
							double b = origtable[pixelList.get(c).x][pixelList.get(c).y];
		
							if (a > b) {
								pixelList.get(c).x = rho2;
								pixelList.get(c).y = theta2;
							}
						}
					}
				}
				// :: end map of pixel changes
		
				// makeLine
				makeLine(newtable, pixelList, f);
		
				// the below code draws the sinogram
				//double percent = maxAveFinal * sensitivity;
				finalImage = configu.createCompatibleImage(maxRho, 360);
				finalGraphic = finalImage.createGraphics();
		
				for (int degs = 0; degs < 360; degs++) {
					for (int rho = 0; rho < maxRho; rho++) {
						// double val = table[rho][degs];
						//double val = newtable[rho][degs];
						double val = origtable[rho][degs];
						// :: print table values with (rho, degs) coordinates
						//System.out.println(val);
						double part1 = val / maxAveFinal;
						double part2 = part1 * 255;
						int colorVal = (int) Math.floor(part2);
						Color color = new Color(colorVal, colorVal, colorVal);
						/*
						 * if (val >= (maxAveFinal - percent)) { System.out.println("Rho = " + rho +
						 * ", Deg = " + degs); finalGraphic.setColor(Color.RED);
						 * //finalGraphic.fillRect(rho, degs, 5, 5) finalGraphic.fillRect(rho - 1, degs
						 * - 1, 1, 1); }
						 */
		
						finalGraphic.setColor(color);
						finalGraphic.fillRect(rho, degs, 1, 1);
					}
				}
		
				// :: create file and write rho and theta values
				try {
					File file = new File("...dir\\Radon Transform Code\\test.txt");
					if(file.createNewFile()) {
						System.out.println("Successfully created");
					}
					else {
						System.out.println("Failed to create or file already exist");
					}
					FileWriter fileWriter = new FileWriter(file);
					//System.out.println("Writing fields to file... ");
					fileWriter.write(String.format("%-14s%-14s%s\n", "rho", "theta", "pixel"));
					//System.out.println("Writing data to file... ");
					
				for (int i = 0; i < origPixelList.size(); i++) {
					Pixel maxRhoTheta = origPixelList.get(i);
					int rh = maxRhoTheta.x;
					int degrees = maxRhoTheta.y;
					double val = origtable[rh][degrees];
		
					//System.out.println("rho = " + rh);
					//System.out.println("theta = " + degrees);
					//System.out.println("Pixel Value = " + val);
					// :: write rho and theta to file
					fileWriter.write(String.format("%-14d%-14d%f\n", rh, degrees, val));
					fileWriter.flush();
					finalGraphic.setColor(Color.BLUE);
					finalGraphic.fillRect(rh - 1, degrees - 1, 4, 4);
				}
				fileWriter.close();
				
				}
				catch(IOException e){
					e.printStackTrace();				
				}
				// :: end write to file
				
				
				 for (int i = 0; i < pixelList.size(); i++) 
				 { 
					 Pixel maxRhoTheta =  pixelList.get(i); int rh = maxRhoTheta.x; int degrees = maxRhoTheta.y;
					 //System.out.println("rho = " + rh); 
					 //System.out.println("degrees = " + degrees); 
					 finalGraphic.setColor(Color.RED); 
					 finalGraphic.fillRect(rh - 1, degrees - 1, 4, 4); 
					 }
				 
		
				System.out.println("Height of image:: " + finalImage.getHeight());
				System.out.println("Width of image:: " + finalImage.getWidth());
		
				// Run for dataset
				//File outputFile = new File(
				//		outdir + "floor2_radon.png");
				System.out.println("radon_dir->> " + radon_dir);
				File outputFile = new File(
						radon_dir + f.getName());
				try {
					ImageIO.write(finalImage, "png", outputFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				} catch (IOException e) {
					  System.out.println("IOException");
					} catch (NullPointerException n) {
					  System.out.println("File name " + img_dir + f.getName() + " does not exist.");
					}
			// close try block above and end of for loop and if from FastPanel()
	            }
			}
		
		//catch block for filewriter radon time
		fileWriter_1.close();
		}
		catch(IOException e){
			e.printStackTrace();				
		}

	}

	/**
	 * a private method which returns the nearest power of two to the inputted (called inside constructor FastPanel())
	 * value, used to find the nearest size to pad the image
	 * 
	 * @param side - a int of the inputted value
	 * @return an int which is the closest power of two greater than side
	 */
	
	// End for loop
	
	private int powerOfTwo(int side) {
		int power = 0;
		while (side > (Math.pow(2, power))) {
			power += 1;
		}
		return ((int) Math.pow(2, power));
	}
	
	/**
	 * a private method which converts polar coordinates into cartesian coordinates (called within function makeLine())
	 * 
	 * @param rho   - the coordinate's rho value, as an int
	 * @param theta - a double representing the coordinate's theta value
	 * @return a Pixel representing the corresponding cartesian coordinate
	 */
	private Pixel polarToCart(int rho, double theta) {
		int x = (int) Math.floor((double) rho * Math.cos(theta));
		int y = (int) Math.floor((double) rho * Math.sin(theta));
		Pixel point = new Pixel(x, y);
		return point;
	}

	/**
	 * a private method which converts a cartesian coordinate into a pixel on an
	 * image (called within function makeLine())
	 * 
	 * @param x     - the x-value of the inputted coordinate
	 * @param y     - the y-value of the inputted coordinate
	 * @param edgeX - the width of the image
	 * @param edgeY - the height of the image
	 * @return a Pixel representing the coordinate's pixel on the image
	 */
	private Pixel cartToPixel(int x, int y, int edgeX, int edgeY) {
		int xPrime = 0;
		int yPrime = 0;
		if ((edgeX % 2) == 0) {
			if ((edgeY % 2) == 0) {
				int w = (edgeX / 2) - 1;
				int h = (edgeY / 2);
				xPrime = x + w;
				yPrime = (-y) + h;
			} else {
				int w = (edgeX / 2) - 1;
				int h = (edgeY + 1) / 2;
				xPrime = x + w;
				yPrime = (-y) + h;
			}
		} else if ((edgeY % 2) == 0) {
			int w = (edgeX - 1) / 2;
			int h = (edgeY / 2);
			xPrime = x + w;
			yPrime = (-y) + h;
		} else {
			int w = (edgeX - 1) / 2;
			int h = (edgeY + 1) / 2;
			xPrime = x + w;
			yPrime = (-y) + h;
		}
		Pixel point = new Pixel(xPrime, yPrime);
		return point;
	}


	/**
	 * a private method that draws the edge line over the image (called within function start())
	 * 
	 * @param table - a double array representing all brightness values to each
	 *              corresponding rho, theta
	 * @return
	 */
	// need a better way to find multiple lines
	// maybe put all of the rho-theta combos and put them into a sorted list
	// then take the top however many
	// problem; how do you decide which edges to take?
	// need something that's not arbitrary
	private void makeLine(double[][] table, ArrayList<Pixel> pixelList, File f) {

		/*
		 * ArrayList<Pixel> pixelList = new ArrayList<Pixel>();
		 * 
		 * int maxRho = table.length; int maxDegs = table[0].length; double percent =
		 * maxAveFinal * sensitivity;
		 * 
		 * // Original version using sensitivity
		 * 
		 * // takes out pixels within the threshold, and then removes those which are
		 * too // close together for (int outerRho = 0; outerRho < maxRho; outerRho++) {
		 * for (int outerDegs = 0; outerDegs < maxDegs; outerDegs++) { double currentVal
		 * = table[outerRho][outerDegs]; if (currentVal >= (maxAveFinal - percent)) {
		 * Pixel bigPixel = new Pixel(outerRho, outerDegs);
		 * 
		 * if (suppress(pixelList, bigPixel)) {
		 * 
		 * } else { pixelList.add(bigPixel); } } } }
		 * 
		 * 
		 * // Pure Non-max suppress for (int rho=2; rho<maxRho-2; rho++) { for (int
		 * deg=2; deg<maxDegs-2; deg++) { if ( table[rho][deg] > table[rho-1][deg-1] &&
		 * table[rho][deg] > table[rho-1][deg] && table[rho][deg] > table[rho-1][deg+1]
		 * && table[rho][deg] > table[rho][deg-1] && table[rho][deg] > table[rho][deg+1]
		 * && table[rho][deg] > table[rho+1][deg-1] && table[rho][deg] >
		 * table[rho+1][deg] && table[rho][deg] > table[rho+1][deg+1]) { Pixel bigPixel
		 * = new Pixel(rho, deg); pixelList.add(bigPixel); } } }
		 */

		// making the line from each point
		System.out.println("Making a line...");
		System.out.println("pixelList::  "+ pixelList.get(0).x);
		System.out.println("pixelList::  "+ pixelList.get(0).y);
		for (int j = 0; j < pixelList.size(); j++) {
			Pixel maxRhoTheta = pixelList.get(j);
			
			int rho = maxRhoTheta.x;
			int degrees = maxRhoTheta.y;

			int width = newImage.getWidth();
			int height = newImage.getHeight();

			double theta = (double) Math.toRadians(degrees);

			int n = (newImage.getWidth()) / 2;
			int r = (int) Math.floor(Math.sqrt((n * n) + (n * n)));

			Pixel cartPoint = polarToCart(rho, theta);
			Pixel pixelPoint = cartToPixel(cartPoint.x, cartPoint.y, width, height);

			Pixel pixelA = new Pixel(0, 0);
			Pixel pixelB = new Pixel(0, 0);

			int x = pixelPoint.x;
			int y = pixelPoint.y;

			// the way the point is projected out changes depending on which quadrant the
			// line sits in, so four different methods are required

			if ((theta >= 0) && (theta < Math.PI / 2)) {

				double theta1 = (double) Math.PI - (theta + Math.PI / 2);
				double thetaHat = (double) Math.PI / 2 - theta;
				double theta2 = (double) Math.PI - (thetaHat + Math.PI / 2);

				int a_x = x - (int) Math.floor((double) r * Math.cos(theta1));
				int a_y = y - (int) Math.floor((double) r * Math.sin(theta1));

				int b_x = x + (int) Math.floor((double) r * Math.sin(theta2));
				int b_y = y + (int) Math.floor((double) r * Math.cos(theta2));

				Pixel tempA = new Pixel(a_x, a_y);
				Pixel tempB = new Pixel(b_x, b_y);

				pixelA = tempA;
				pixelB = tempB;

			} else if ((theta >= Math.PI / 2) && (theta < Math.PI)) {

				double thetaT = (double) theta - Math.PI / 2;
				double theta1 = (double) Math.PI - (thetaT + Math.PI / 2);
				double thetaHat = (double) (Math.PI / 2) - thetaT;
				double theta2 = (double) Math.PI - (thetaHat + Math.PI / 2);

				int a_x = x - (int) Math.floor((double) r * Math.sin(theta1));
				int a_y = y + (int) Math.floor((double) r * Math.cos(theta1));

				int b_x = x + (int) Math.floor((double) r * Math.cos(theta2));
				int b_y = y - (int) Math.floor((double) r * Math.sin(theta2));

				Pixel tempA = new Pixel(a_x, a_y);
				Pixel tempB = new Pixel(b_x, b_y);

				pixelA = tempA;
				pixelB = tempB;

			} else if ((theta >= Math.PI) && (theta < 3 * Math.PI / 2)) {

				double thetaT = (double) theta - Math.PI;
				double thetaHat = (double) (Math.PI / 2) - thetaT;
				double theta1 = (double) Math.PI - (thetaHat + Math.PI / 2);
				double theta2 = (double) Math.PI - (thetaT + Math.PI / 2);

				int a_x = x - (int) Math.floor((double) r * Math.sin(theta1));
				int a_y = y - (int) Math.floor((double) r * Math.cos(theta1));

				int b_x = x + (int) Math.floor((double) r * Math.cos(theta2));
				int b_y = y + (int) Math.floor((double) r * Math.sin(theta2));

				Pixel tempA = new Pixel(a_x, a_y);
				Pixel tempB = new Pixel(b_x, b_y);

				pixelA = tempA;
				pixelB = tempB;

			} else {

				double thetaT = (double) theta - 3 * (Math.PI / 2);
				double thetaHat = (double) (Math.PI / 2) - thetaT;
				double theta1 = (double) Math.PI - (thetaHat + Math.PI / 2);
				double theta2 = (double) Math.PI - (thetaT + Math.PI / 2);

				int a_x = x - (int) Math.floor((double) r * Math.cos(theta1));
				int a_y = y + (int) Math.floor((double) r * Math.sin(theta1));

				int b_x = x + (int) Math.floor((double) r * Math.sin(theta2));
				int b_y = y - (int) Math.floor((double) r * Math.cos(theta2));

				Pixel tempA = new Pixel(a_x, a_y);
				Pixel tempB = new Pixel(b_x, b_y);

				pixelA = tempA;
				pixelB = tempB;

			}

			PerpLine perpLine = new PerpLine();
			ArrayList<Pixel> pixelListFinal = perpLine.makePoints(pixelA, pixelB);

			for (int i = 0; i < pixelListFinal.size(); i++) {
				Pixel currentPixel = pixelListFinal.get(i);
				graphic.setColor(Color.GREEN);
				graphic.fillRect(currentPixel.x, currentPixel.y, 1, 1);
			}

			graphic.setColor(Color.RED);
			graphic.fillRect(x, y, 5, 5);
		}

		// this saves the completed image to a file, taking it out doesn't affect
		// anything
		// Run for dataset
		//File outputFile = new File(
		//		indir + "floor2_saved.jpg");
		System.out.println("result_dir->> " + result_dir);
		File outputFile = new File(
				result_dir + f.getName());
		//count2++;
		try {
			ImageIO.write(image, "png", outputFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// return pixellist
		//return pixelList;

	}


	/**
	 * a private method that finds the radon transform of an image (called in function start())
	 * 
	 * @param image      - a BufferedImage to be examined
	 * @param maxRho     - the maximum rho value of the image
	 * @param maxDegrees - the maximum degrees to check
	 * @return a 2D array of doubles which corresponds each brightness value to a
	 *         rho, theta
	 */
	private double[][] radon(BufferedImage image, int maxRho, int maxDegrees) {
		int N = image.getWidth();

		int numLevels = (int) ((double) Math.log(N) / Math.log(2)) + 1;
		int shiftMax = (int) Math.pow(2, numLevels - 1);
		int offsetMax = N + shiftMax;

		double[][] table_sum = new double[maxRho][maxDegrees];
		double[][] table_count = new double[maxRho][maxDegrees];

		// Arrays for p and q (right and up of the original image
		// in the coordinate system of the rotated / flipped image)
		double P_x[] = { 1, 0, -1, 0, 0, 1, 0, -1 };
		double P_y[] = { 0, -1, 0, 1, 1, 0, -1, 0 };

		double Q_x[] = { 0, 1, 0, -1, 1, 0, -1, 0 };
		double Q_y[] = { 1, 0, -1, 0, 0, -1, 0, 1 };

		BufferedImage img2 = image;

		// performs the radon transform 8 times over the flipped and rotated images
		for (int i = 0; i < 8; i++) {

			// perform radon transform
			FastRadon radon = new FastRadon(img2);
			HashMap<Integer, int[][][]> radon1 = radon.findLine();
			int[][][] table1 = radon1.get(numLevels - 1);

			// bin shift and offset values into rho and theta values
			for (int S = 0; S < shiftMax; S++) {
				for (int F = 0; F < offsetMax; F++) {
					// calculated the normalized u and v vectors
					double u_x = -S;
					double u_y = (N - 1.0);
					double inv_uLen = 1.0 / Math.sqrt((u_x * u_x) + (u_y * u_y));
					u_x *= inv_uLen;
					u_y *= inv_uLen;
					double v_x = u_y;
					double v_y = -u_x;

					double p_x = F;
					double p_y = 0;
					double c_x = (N - 1.0) / 2.0;
					double c_y = (N - 1.0) / 2.0;

					// calculate rho
					double rhoTop = (double) ((c_x * u_y) - (c_y * u_x) - (p_x * u_y) + (p_y * u_x));
					double rho = (double) rhoTop / ((v_x * u_y) - (v_y * u_x));

					// Invert v, and rotate / flip using p and q
					v_x = -v_x; // v points away from the center now
					v_y = -v_y;
					double w_x = (v_x * P_x[i]) + (v_y * P_y[i]);
					double w_y = (v_x * Q_x[i]) + (v_y * Q_y[i]);

					// calculate theta
					double theta = Math.atan2(w_y, w_x);
					if (theta < 0) {
						theta += ((double) 2.0 * Math.PI);
					}

					// if rho is less than one, turn theta around 180 degrees and make rho positive
					if (rho < 0) {
						rho = Math.abs(rho);
						theta += Math.PI;
					}

					// bin by rho and theta
					int iRho = (int) Math.floor(rho);
					int iTheta = (int) Math.floor(Math.toDegrees(theta));

					if (iRho < 0) {
						iRho = 0;
					}

					if (iRho > maxRho - 1) {
						iRho = maxRho - 1;
					}

					if (iTheta < 0) {
						iTheta += maxDegrees;
					}

					if (iTheta > maxDegrees - 1) {
						iTheta = iTheta - maxDegrees;
					}

					table_sum[iRho][iTheta] += table1[0][S][F];
					table_count[iRho][iTheta]++;
				}
			}

			// rotate or mirror the image
			if (i == 3) {
				Morph r = new Morph(img2);
				Color[][] colorTable = r.mirror();
				img2 = configu.createCompatibleImage(N, N);
				Graphics2D graphicR4 = img2.createGraphics();
				for (int x = 0; x < N; x++) {
					for (int y = 0; y < N; y++) {
						Color color = colorTable[x][y];
						graphicR4.setColor(color);
						graphicR4.fillRect(x, y, 1, 1);
					}
				}
			} else if (i < 7) {
				Morph r = new Morph(img2);
				Color[][] colorTable = r.rotate(Math.PI / 2);
				img2 = configu.createCompatibleImage(N, N);
				Graphics2D graphicR4 = img2.createGraphics();
				for (int x = 0; x < N; x++) {
					for (int y = 0; y < N; y++) {
						Color color = colorTable[x][y];
						graphicR4.setColor(color);
						graphicR4.fillRect(x, y, 1, 1);
					}

				}
			}
		}

		double maxAve = 0;

		// averages out every value between count and total, and finds the max average
		// value
		double table[][] = new double[maxRho][maxDegrees];
		for (int rho = 0; rho < maxRho; rho++) {
			for (int theta = 0; theta < maxDegrees; theta++) {
				if (table_count[rho][theta] > 0) {
					table[rho][theta] = table_sum[rho][theta] / table_count[rho][theta];
					if (table[rho][theta] >= maxAve) {
						maxAve = table[rho][theta];
					}
				} else
					table[rho][theta] = 0;
			}

		}
		maxAveFinal = maxAve;
		return table;
	}
	
	/**
	 * a method that finds the blur along x direction (called within constructor FastPanel())
	 */
	double [][] convolveX(double [][] in, double []kernel, int width, int height)
	{
		int y,x;
		
		// blur x
		int nKernel = kernel.length;
		double [][]blurx = new double[width][height];
				
		// Convolution in x dimension
		for (y=0; y<height; y++) {
			for (x=0; x<width; x++) {
			
				// Start and end to blur
				int minx = x-nKernel/2;      // k_x/2 left of pixel
				int maxx = minx + nKernel;   // k_x/2 right of pixel
				minx = Math.max(minx, 0);     // keep in bounds
				maxx = Math.min(maxx, width);
				
				// average blur it
				int x2,k=minx-(x-nKernel/2);   // How much was cropped
				double total = 0.0;
				for (x2=minx; x2<maxx; x2++) {
					total += kernel[k] * in[x2][y];    // use "red" as input
					k++;
				}
				//System.out.println("total =" + total);
				//System.out.println("x =" + x);
				//System.out.println("y =" + y);
				//System.out.println("blurx[y][x] = "+ blurx[x][y]);
				blurx[x][y] = total; // blurx is output
			}
		}

		return blurx;
	}

	/**
	 * a method that finds the blur along y direction (called within constructor FastPanel())
	 */
	double [][] convolveY(double [][] in, double []kernel, int width, int height)
	{	
		int y,x;
		
		// blur x
		int nKernel = kernel.length;
		double [][]blury = new double[width][height];
		
		// Convolution in y dimension
		for (y=0; y<height; y++) {
			for (x=0; x<width; x++) {
			
				// Start and end to blur
				int miny = y-nKernel/2;      // k_x/2 left of pixel
				int maxy = miny + nKernel;   // k_x/2 right of pixel
				miny = Math.max(miny, 0);     // keep in bounds
				maxy = Math.min(maxy, height);
				
				// average blur it
				int y2,k=miny-(y-nKernel/2);   // How much was cropped
				double total = 0.0;
				for (y2=miny; y2<maxy; y2++) {
					total += (double)kernel[k] * in[x][y2];    // use "red" as input
					k++;
				}
				blury[x][y] = total; // blury is output
			}
		}

		return blury;
	}

	/**
	 * a method that returns 3*3 Kernel for blur (called within constructor FastPanel())
	 */
	double []triangularKernel(int len) {
		double []kernel = new double[len];
		
		for (int i=0; i<(len+1)/2; i++)
			kernel[i] = i+1;
		for (int i=0; i<(len+1)/2; i++)
			kernel[len-i-1] = i+1;
		
		double sum=0;
		for (int i=0; i<len; i++)
			sum+=kernel[i];
		double inv_sum = 1.0 / sum;
		for (int i=0; i<len; i++) {
			kernel[i] *= inv_sum;
			System.out.println("kernel[" + i + "] " + kernel[i]);
		}
		
		return kernel;
	}
	
	/**
	 * a method for finding derivative of image (called within constructor FastPanel())
	 */
	double[][] sobel(double[][] in, int width, int height) {
		double[][] out = new double[width][height];

		for (int y=1; y<height-1; y++) {
			for (int x=1; x<width-1; x++) {
				double dy1 = (in[x][y+1] - in[x][y]);
				//double dy1 = (in[x][y+1] - in[x][y-1]);
				double dy2 = (in[x][y-1] - in[x][y]);
				double dx1 = (in[x+1][y] - in[x][y]);
				//double dx1 = (in[x+1][y] - in[x-1][y]);
				double dx2 = (in[x-1][y] - in[x][y]);
				out[x][y] = Math.sqrt(dy1*dy1 + dy2*dy2 + dx1*dx1 + dx2*dx2)/2;
				//out[x][y] = Math.sqrt(dy1*dy1 + dx1*dx1);
			}
		}
		return out;
	}

	/**
	 * a method for smoothening output image (called within constructor FastPanel())
	 */
	void saveGray(double [][]in, String fname, int width, int height)
	{
		BufferedImage img2 = configu.createCompatibleImage(width, height);
		Graphics2D graphicR4 = img2.createGraphics();
		
		double min = in[0][0];
		double max = in[0][0];
		for (int x=0; x<width; x++) {
			for (int y=0; y<height; y++) {
				min = Math.min(in[x][y], min);
				max = Math.max(in[x][y], max);
			}
		}
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				double val = 255.0 * (in[x][y]-min) / (max-min);
				Color color = new Color((int)val, (int)val, (int)val);
				graphicR4.setColor(color);
				graphicR4.fillRect(x, y, 1, 1);
			}
		}

		File outputFile = new File(
				fname);
		try {
			ImageIO.write(img2, "png", outputFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * a method which hands in the image on the window
	 */
	public void paintComponent(Graphics g) {
		// switch out the first variable for whichever image you want to display
//		g.drawImage(finalImage, 0, 0, getWidth(), getHeight(), null);
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
	}
	
	/**
	 * a private helper method for makeLine which checks if any rho-theta values in
	 * the pixel list are too close to the inputted pixel
	 * 
	 * @param pixelList - the list of pixels to check
	 * @param pixel     - the inputted pixel
	 * @return false if no pixels are too close, true otherwise
	 */
	private boolean suppress(ArrayList<Pixel> pixelList, Pixel pixel) {
		boolean control = false;
		int rho = pixel.x;
		int theta = pixel.y;
		for (int i = 0; i < pixelList.size(); i++) {
			Pixel currentPixel = pixelList.get(i);
			int currRho = currentPixel.x;
			int currTheta = currentPixel.y;
			if (Math.abs(currRho - rho) <= 10) {
				if (Math.abs(currTheta - theta) <= 10) {
					control = true;
				} else {
					// leave control as false
				}
			} else {
				// leave control as false
			}
		}
		return control;
	}
	
}
