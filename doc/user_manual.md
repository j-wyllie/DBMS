# User Manual
## Compiling
This project uses maven to compile the project.

From a terminal window, change directory to the project root directory and execute the following:

    mvn -B clean package

This will download any required dependencies and compile the program to the `target` folder.

## Running
To run the application once compiled, change terminal directory to the target folder and execute 
the following:

    java -jar odms-0.1.jar

This will bring you into the application which can be terminated at any time by executing the 
`quit` command.

## Usage
Upon executing ODMS (Organ Donor Management System), ODMS will begin to expect commands. A list of 
commands can be accessed at any time by executing `help`

## Data
### Importing Data
When ODMS executed the database will be blank, a JSON file can be imported to the database by 
executing

    import path\file.json

**The database is not automatically saved.** Please see below about exporting/saving the database. 

### Exporting Data
The current database can be saved and exported by executing

    export-changes path\file.json
    

### Example
An example database can be imported by executing 
    
    import example
    
This example database is a database containing 10 donors

## Donor Management
