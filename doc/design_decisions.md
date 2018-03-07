# Design Decisions

## Donor Data Storage
### Memory
During runtime the 'database' will be kept in memory, with supporting functions.

### File Storage
For the sake of readability JSON has been chosen for storing the donor data upon saving. CSV usage 
was explored but found to be less readable to a user.
* GSON library chosen for helping handle JSON related functions. 

## Testing
###Setters & Getters
* If a setter/getter performs no additional functionality above simply adjusting or retrieving a 
class property then it does not require testing as this provides no real benefits.
