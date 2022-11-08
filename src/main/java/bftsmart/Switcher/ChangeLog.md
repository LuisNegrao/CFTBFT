Created a class for each type of message in the protocol. Trigger Switch and Confirmation.
Added to the config file the variables regarding the timers for the algorithm changes.
Added to the TOMConfiguration file methods to allow the program to know if we are allowed to change and to retrieve the new variables.
Added to the Controller a method to retrieve the CFT and BFT quorum size
Added a method to the static controller in order to allow the isBFT var to change.
Added to the TOMLayer the initialization of the switch protocol.
Created the switch file with all its methods
Added trigger to the acceptor class.
Added separate queue for the switch protocol messagesa