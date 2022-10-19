LiPTool is an automated tool aiming at learning the optimal index placement for databases. The tool consists of three parts: the Pytorch-RL that finds the optimal response server for every query in the workload, BPlusTreeBackEnd that constructs the index for servers, and the Bplustree_frontend that provides a user interface and shows the results.

**DEPENDENCIES**

1. Python3 (version 3.9+), 

   Pytorch (version 1.12.1), 

   Matplotlib, 

   numpy, pandas, pylab, itertools, time, sys, tkinter, flask, flask_cors, random, datatime.

2. Java (version 8+), 

   springboot (version 2.7.3), 

   vue (version 3.2.39), 

   element-plus (version 2.2.17), axios (version 0.27.2), core-js (version 3.25.1), vue-router (version 4.1.5), vuex (version 4.0.2).

**DIRECTORY TREE**

By default, the tool assumes the following folder structure exists for its operation:

1. Pytorch-RL

|-- undefined
    |-- Pytorch-RL
    |   |-- MyExperiment
    |       |-- .DS_Store
    |       |-- exp1_CG-index
    |       |   |-- cluster_env.py						-->Initialize its environment
    |       |   |-- local.py									-->Execute the CG-index method
    |       |   |-- __init__.py
    |       |   |-- result									   -->Folder to store the results
    |       |   |-- __pycache__
    |       |       |-- cluster_env.cpython-39.pyc
    |       |-- exp1_LiPCore
    |           |-- cluster_env.py						-->Initialize its environment
    |           |-- comparison.py					   -->Generate comparison results
    |           |-- RL_brain.py							 -->Train the DRL model
    |           |-- __init__.py
    |           |-- comparison							-->Folder to store the comparison results
    |           |   |-- figure_estimated_1.jpg
    |           |-- data										 -->Folder to store training data
    |           |-- indexFile								 -->Folder to store data of index nodes
    |           |-- trainingResult						-->Folder to store training results
    |           |-- __pycache__
    |               |-- cluster_env.cpython-39.pyc



2. BPlusTreeBackEnd

|-- undefined
    |-- src
    |   |-- main
    |   |   |-- java
    |   |   |   |-- com
    |   |   |       |-- example
    |   |   |           |-- bplustreebackend
    |   |   |               |-- BPlusTreeBackEndApplication.java		-->Start the whole project
    |   |   |               |-- control
    |   |   |               |   |-- Controller.java									  -->Connect with front end
    |   |   |               |-- data															 -->Store servers' data
    |   |   |               |-- entity
    |   |   |               |   |-- BPlusTree.java									  -->Build the data structure of B+ tree
    |   |   |               |-- service
    |   |   |                   |-- BTreeRepository.java 						  -->Include interfaces of B+ tree service
    |   |   |                   |-- BTreeService.java								  -->Provide B+ tree service

**COMPILE & RUN**

1. Run the MyExperiment/exp1_LiPCore/RL_brain.py

2. Run the src/main/java/com/example/bplustreebackend/BPlusTreeBackEndApplication.java

3. Bplustree_frontend Terminal: 

   (1) npm install; 

   (2)npm run serve

**USAGE**

1. Access the address corresponding to the frontend in the browser.
2. Select the training data and click the "Start training" button to start the training.
3. Jump to the "Index placement" page to view the results. You can select to view the results of different servers.
4.  Jump to the "Comparison" page to view the comparison experiment results.

