# Sensor_data_app

An app to read and store realtime accelerator and gyroscope sensor values corresponding to a gesture into a text file.

100 sets of 6 values (Accelerator and gyroscope readings along X, Y, Z axes) + 1 string representing the gesture.

**Using the app**

The labels show the real time values. 

Enter the file name (without .txt) to write to and click ' choose file '. Then enter the gesture name. 

It will record the gesture till ' cut! ' pops up on the screen. It'll show when it's done writing to the file. It'll write as one line with  (100*6) + 1 values (Recording 6 inputs, 100 times and 1 for gesture name). 
We can convert it to CSV using pandas. 
The first lineof any files contains column headings for conversion to csv.

You can use a test file to try out before actual recording. 
If the file is not specified ( empty text box → click ' choose file ' ), it wont record. You can simply click record in this case to see how long it'll record the gesture....for a general idea.
