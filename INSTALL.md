# 1. Prerequisites
# 1.1 System Requirements
You need Linux  OS (We have not tested Oreo on any Operating System other than Linux- CentOs), and at least 12 GB of RAM to run Oreo. Also, you need to have Java 8, and Python 3.6 installed. After installing these two, follow the following instructions to download all the prerequisites. 
# 1.2	Required Dependencies
Oreo needs that the TensorFlow library for Python is installed; this is needed for running the deep learning part of Oreo. The best way to install TensorFlow and other required dependencies is by creating a virtual environment (venv) for Python. 
- Create a virtual environment using the following command:
`python3 -m venv /path/to/new/virtual/environment`

- Start the virtual environment:
`source /path/to/new/virtual/environment/bin/activate`

- Inside the submission package, there is a folder named `oreo/`. Copy `oreo/` (with all its contents) to the root of virtual environment, which is `/path/to/new/virtual/environment`. 
- Change to `/path/to/new/virtual/environment`, and there, change to `oreo/python_scripts/dependencies/` directory and run the following command to install all the required dependencies:
`pip install -r dependencies.txt`
# 2 Generate Input for Oreo
Oreo uses a combination of metrics, machine learning, and information retrieval to produce its pairs. Hence, as a preprocessing step, we need to have metrics for the methods in its input ready. Metrics calculation is a one-time process, and when you prepare the metrics for an input dataset ready, you can run the clone detection part on these metrics for as many times as you want.
In order to make the execution of this artifact simple, we have generated the needed metrics file for the BigCloneBench  (https://github.com/clonebench/BigCloneBench), and put it at https://drive.google.com/open?id=1AUC2uA7ik7ZWrQ4x9ZcKGyyX6YNStG_y (The metrics file is for the reduced version that is used by BigCloneEval (https://jeffsvajlenko.weebly.com/bigcloneeval.html) in recall studies, and we also used it for evaluating Oreo’s recall). The name for this file is `blocks.file`. You can skip to **Section 3** if you want to run Oreo with this file. Also, since executing Oreo on the whole dataset of BigCloneBench can take time, we are providing a quick way of running Oreo using a small sample of BigCloneBench dataset (10,000 methods); to run Oreo using this sample, skip to **Section 5**. Otherwise, follow the steps below to generate the metrics for your input dataset.
To generate the input metrics file, the Metric Calculator tool, which we provide with Oreo, is used. The tool needs to know the absolute path of the dataset for which this input file needs to be created.  The path should point to the directory under which the Java files are included. Java files need to be located at the second level of hierarchy in this directory (it means that the path should point to the directory containing some folders where each folder has the Java files). Metrics calculator calculates metrics for the methods found in these Java files, and it creates an output file where each line corresponds to the metrics for one method. This file is then used as the input to Oreo.
Follow the following steps to generate the input file:
- In a terminal, go to the root folder of Oreo (`/path/to/new/virtual/environment/oreo`), and then change directory to `java-parser`. Then, run `ant` command to create the needed jar file:
`ant metric`
- Then, again change the directory to the root folder of Oreo (`/path/to/new/virtual/environment/oreo`), and then change to `python_scripts` directory; there, you need to run the `metricCalculationWorkManager.py` script that launches the jar file for metric calculation. This script should be run as follows:
`python3 metricCalculationWorkManager.py 1 d <absolute path to input dataset>`
You need to replace `<absolute path to input dataset>` with the absolute path to the directory containing the input Java source files. As mentioned earlier, Java files need to be located at the second level of hierarchy of the directory whose address is provided. Please note that we are providing the BigCloneBench dataset source files with our submission. It is located at https://drive.google.com/open?id=1AUC2uA7ik7ZWrQ4x9ZcKGyyX6YNStG_y, under the name `bcb_dataset.zip`. You can download this file, unzip it, and provide the path to the unzipped directory to the above command.
After issuing the above two commands, look for two files in the current directory: `metric.out` and `metric.err`. Any possible error will be printed in `metric.err` file, and `metric.out` shows the progress of metric calculation process. Once the process is ended, there will be a `done!` printed in the last line of `metric.out` file.
It will take about 8 minutes on a system with 4 cores and 32GB of RAM to generate the metrics. When the process ends, you will have a folder named `1_metric_output` inside the current directory (`python_scripts`). Inside this folder, there will be a file named `mlcc_input.file`. This file will be used as the input for Oreo.
# 3. Setting Up Oreo
In order to proceed to clone detection step, we need to place metrics file in the appropriate place for Oreo to use it. To this end, follow these steps:
- If you are using the metrics file provided by us (which is at https://drive.google.com/open?id=1AUC2uA7ik7ZWrQ4x9ZcKGyyX6YNStG_y): Download `blocks.file` from this link and place it inside  `/path/to/new/virtual/environment/oreo/clone-detector/input/dataset/`
- If you are using the metrics file that you generated in **Section 2**: First, copy the generated `mlcc_input.file` file to this path:  `/path/to/new/virtual/environment/oreo/clone-detector/input/dataset/`, and then, rename it to `blocks.file`. Please make sure that there is no other file present at this location. 
# 4. Running Oreo
After completing the setup, follow the following steps to run Oreo clone detection. Note that we have run the clone detection process on a Linux system , with Intel(R) Core(TM) i5-4670 CPU @ 3.40GHz having 4 cores, using 12 GB of RAM, and it took 65 minutes to complete the detection process. 
- Change the directory to the root of Oreo (`/path/to/new/virtual/environment/oreo`), and then to `clone-detector`. There, run the following command:
`python controller.py 1`
- Now, open another terminal and change directory to `/path/to/new/virtual/environment/oreo/python_scripts`. Then, run the following command:
`./runPredictor.sh`
Once the clone detection process is finished, the clone pairs will be reported in several `.txt` files located at `/path/to/new/virtual/environment/oreo/results/predictions/`; you need to concatenate them in one file to have them all in one place.
Please note that each time you want to run Oreo after one round of execution, you need to run `./cleanup.sh` before `python controller.py 1`.
# 5 Quick Execution of Oreo
In order to make the execution of Oreo simpler and quicker, we are providing a smaller version of input metrics file so that readers can go through the execution of Oreo faster. This file is located at the `input/` folder of submission package, and it is named `blocks_small.file`. In order to do the fast execution, copy this file to `/path/to/new/virtual/environment/oreo/clone-detector/input/dataset/`, and rename the file to `blocks.file`. Then, follow the instructions in **Section 4** to run Oreo on this input. Please note that no other file is placed in this location. It would normally take about 2 minutes for this input to complete the clone detection.
