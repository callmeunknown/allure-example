## Test execution framework
BDD framework for automated tests in Java, utilizing:

- [Cucumber](https://cucumber.io) To write scenarios in the BDD style
- [REST assured](https://rest-assured.io) for testing REST API

## How to start writing API tests
### The principle of writing tests is similar to the approach of creating and sending requests in Postman
*Step 1. Configure the request using a step*
```gherkin
* create and send the request 
  | method | full_url | path | headers | body |
```
If any of the columns are not specified in this step, they are not considered in the request
Example:
```gherkin
* create and send the request
| method | path  | body            |
| POST   | /user | createUser.json |
OR
* create and send the request
| method | path  |      body        |
| POST   | /user | {<request body>} |
OR
* create and send the request
| method |                  full_url                      |
| GET    | https://petstore.swagger.io/v2/user/<username> |
```

* Request Body - You can pass the body as text or specify a file name ***json***, in the table, which will be located at the path ***autotest/resources/json***

*Step 2. Addition Query*
```gherkin
* add query parameters
| model_id | Tesla |
| year     | 2019  |
```
*Step 3. Response check*
```gherkin
* expected status code "200"
```
If you need to check the response body, you can extract data using JSONPath. The value will be saved in the variable specified in the column 1<br/>
```gherkin
* extract data from the response body into the context variable  
  | user_id | $.message |
```
You can verify the extracted data using a step:
```gherkin
* compare values
| %{user_id}% | not equals | null |
OR
| %{user_id}% | equals | 1234567890 |
OR
| %{user_id}% | > | 0 |
OR
| %{user_id}% | < | 100 |
OR
| %{user_id}% | contains | qwerty123 |
```
### Other information.
With the following step, you can generate variables for subsequent use in the test
```gherkin
* generate variable by mask
   | id         | 0                 |
   | username   | EEEEEEEE          |
   | firstName  | EEEEEEEE          |
   | lastName   | EEEEEEEE          |
   | email      | EEEEEEE@EEEDDD.EE |
   | password   | DDDEEEDDDEEE      |
```
**R** - a random Russian letter<br/>
**E** - a random English letter<br/>
**D** - a random number<br/>
Other characters in the string are ignored and remain unchanged.
The generated values are stored in the test context. They can be substituted into requests, request bodies. You can retrieve them using the syntax ***%{userName}%***<br/>

### Running tests via the console

```java 
mvn clean -am -pl "rest-plugin" test -DtagName="@api_examples_pet" -Ddataproviderthreadcount=1
 ``` 
* ***-Ddataproviderthreadcount=4*** - Number of execution threads, default value is 1 thread (Changing the default number of threads is done in the file src\test\resources\suite.xml)
* ***-DtagName="@authentication"***  - Selecting tests with a specific tag. The default value is absent, so if this parameter is not specified in the run, all feature files will be executed.

>You can also pass any parameter from the files to the command line *.properties
>To add a new parameter, you will need to define it in the corresponding .properties file and add the corresponding getter method in the Java class.    

#### To run with default parameters, use the command. 
```java 
mvn clean -am -pl "rest-plugin" test 
 ```



You can also run the tests via the Cucumber plugin (after installing it in IntelliJ IDEA). To do this, open any feature file, and click on the green arrow next to the line **Feature** or **Scenario**<br/>