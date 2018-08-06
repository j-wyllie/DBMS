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

    export path\file.json
    
### Example Database
An example database can be found in the example directory.
    
This example database is a database containing 10 profiles of varying organ donation status and other 
mixed attributes.

## Profile Management
ODMS refers to the specifics of a profile as attributes, such as names, date of birth, organs they opt
 to donate, etc.

### Profiles
#### Creating a profile
A profile profile can be created from the base command `create-profile` followed by the desired 
attributes you wish to set at time of creation.

**Note: **Profiles must be created with `given-names`, `last-names`, `dob`, and `nhi`

The following attributes are available within ODMS:

| Attribute     | Value                   | Example                                |
| ------------- | ----------------------- | -------------------------------------- |
| `given-names` | All given (first) names | `given-names="Marion Mitchell"`        |
| `last-names`  | All last (family) names | `last-names="Morrison"`                |
| `dob`         | Date of birth           | `dob="dd-mm-yyyy"`                     |
| `nhi`         | NHI number              | `nhi="123546789"`                      |
| `dod`         | Date of death           | `dod="dd-mm-yyyy"`                     |
| `gender`      | Gender                  | `gender="Male"`                        |
| `height`      | Height in centimetres   | `height="180"`                         |
| `weight`      | Weight in kilograms     | `weight="104"`                         |
| `blood-type`  | Blood type              | `blood-type="O-"`                      |
| `address`     | Address                 | `address="123 Fake St, Faketon, Chch"` |
| `region`      | Region                  | `region="Canterbury"`                  |

When using attributes the format is always `attribute="value"`

##### Examples
Basic profile profile creation

    create-profile given-names="Marion Mitchell" last-names="Morrison" dob="1-08-1989" nhi="123456789"
    
More detailed profile profile creation

    create-profile given-names="Marion Mitchell" last-names="Morrison" dob="1-08-1989" nhi="123456789" gender="male" height="180" weight="104"

#### Viewing or updating a profile
A profile profile can be selected by searching via the `profile` command and providing attributes 
`given-names`, `last-names`, or `nhi`. After selecting these attributes follow them with ` > ` and 
either `view` or the attributes you wish to modify.

##### Examples
Viewing a profile:
    
    profile given-names"michell marion" > view
    
This will result in a summary of all profiles that match the criteria:

    NHI: 123456789
    Given Names: Marion Mitchell
    Last Names: Morrison
    Date Of Birth: 01-08-1989
    NHI: 123456789
    Last updated at: 03:10 PM 08-03-2018
    
Updating a profile:

    profile given-names="Marion Mitchell" > age="84" blood-type="O+"

##### Viewing a profile's creation time
`date-created` will display the profile's creation time.

    profile given-names="Marion Mitchell" > date-created

##### View a profile's previous donations
`donations` will display the previous donations made by a profile.

    profile given-names="Marion Mitchell" > donations

#### Managing a profile's organ donation status
The organs that are available to be added to a profile are:

`liver`
`kidney`
`pancreas`
`heart`
`lung`
`intestine`
`cornea`
`middle-ear`
`skin`
`bone`
`bone-marrow`
`connective-tissue`

Please be explicit when adding organs that are available to be donated to a profile, explicitly use an
 NHI number for best accuracy.

    profile nhi="123456789" > add-organ="heart"

Multiple organs can be added by either space or comma delimitation

    profile nhi="123456789" > add-organ="liver,kidney"
    
To remove an organ from the opted donation list use the `remove-organ` command

    profile nhi="123456789" > remove-organ="liver"
    
#### Displaying all profile's
You can display all profile profiles at any time with the following command:

    print all
    
This can be limited to only those profile profiles that currently are registered to donate an organ 
with the following command:
    
    print profiles
    
#### UNDO/REDO Actions
Undo undoes previously done actions in the command line and GUI.

    undo
    
Redo redoes previously undone actions in the command line and GUI.

    redo
