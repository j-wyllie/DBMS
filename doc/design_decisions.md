# Design Decisions

## Donor Data Storage
### Memory
During runtime the 'database' will be kept in memory, with supporting functions.

### File Storage
For the sake of readability JSON has been chosen for storing the donor data upon saving. CSV usage 
was explored but found to be less readable to a user.
* GSON library chosen for helping handle JSON related functions. 

## Code Style
We agreed to conform to the Google Style Guide for Java with the exception of 4 spaces instead of 2 
for indentation and 8 spaces instead of 4 for continuation.

## Testing
###Setters & Getters
* If a setter/getter performs no additional functionality above simply adjusting or retrieving a 
class property then it does not require testing as this provides no real benefits.

## Constants
### Enums
Enums have been used for any constants throughout the program such as the organs, blood-types 
and the attributes of a donor. This will prevent any errors that could occur from repeating 
definitions across multiple files.
