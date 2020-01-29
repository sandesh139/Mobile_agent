# Mobile_Agents
Author - Raisul Ahsan and Sandesh Timilsina

# Introduction
The project is to show the simulation of the fire spreading from nodes to their neighbors. Here
any two nodes are neighbor of each other if there is an edge in between them. This
repository consists source code for simulation inside the src directory, MobileAgents.jar file,
docs folder containing the design of the project and .gitignore file.

 
# How to run the Mobile_Agents.jar
  - Console.jar can be run from the command line with command "java -jar MobileAgents.jar".
But make sure to include the different .txt files from resources in the same directory as jar
file is in. Once you run the jar file, you have to type in the file name to have its
simulation. After the java console is displayed, Click on Start Simulation to start the simulation.

# GUI Description
 There is a Base Station with orchid colored and other nodes colored with red, yellow and aqua.
Red nodes are the nodes that are on Fire. Yellow nodes are the neighbors of the fire nodes.
And all the other aqua colored nodes are normal nodes. Agent in the simualtion is The right 
side of the canvas displayes the information of the simulation once it starts. Information 
contains the unique ID and location where the node is created. Whenever the fire spreads, 
message of communication lost is printed.


# Simulation Logic
Simulation begins with one or more node on fire and agent starts its random walk from the Base
Station. Fire keeps spreading while agent keeps moving until it finds the node having its
neighbor on fire. Then the agent clone itself to its neighbors with new unique ID. So, the
fire keeps growing throughout the simulation. The agent in the fire node gets destroyed.

# Extra Features
 - Background image is added.
 - Background music is added for throughout the simulation and an extra audio whenever fire spreads.

# Bugs in the program
 - In few running, there was an error "CheckforCommodification". We are not sure for the reason.
 - In big Graphs audio does not work properly and also in all graphs, audio does not give the blast
 sound when fire spreads to the last node.