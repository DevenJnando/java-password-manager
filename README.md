# java-password-manager
Password manager app in Java

## Cloning and running

For running from source, add the following vm option to the java build configuration: `--add-opens=java.base/java.lang.reflect=com.jfoenix` 
Java 11 and JavaFX are required for this application to work.

## Brief breakdown of how it works

The frontend of this application is written in JavaFX. I wanted to make a password manager as a standalone application with the logic that having it hosted on a web server as a web application could introduce an additional point of vulnerability for hypothetical undesirables!

The backend storage is accomplished using CosmosDB. This was chosen because of the well-documented secuirty principles and protocols which are designed to keep CosmosDB containers secure and difficult to penetrate. It was also because I wanted a storage solution which could be easily scaled and have its schema modified without resulting in major rework of the business logic. Having potential geo-redundancy is also a big plus.

There is a master database which contains all usernames for the application itself as well as a one-way encrypted master password. The user's passwords are stored in a separate database for added security; if a breach did occur in the master database, very little usable information would be acquired since the passwords are one-way encrypted. Once a user logs in with a correct username and password, the key for the password database is retrieved using an azure function. This key is then used to unlock the password database and the user's passwords can then be viewed from their own personal container.

User's passwords and credentials are two-way encrypted using an implementation of the AES algorithm along with a salt. The use of a salt makes it far more difficult to brute force a password with something like a rainbow table (again, assuming a breach of any kind did occur).

For presentation and processing of business logic and MVC architecture has been implemented. The modularity of this architecture, as well as the use of other design patterns (builders, factories, reflection, etc.) make the code more readable and more cohesive. It also makes diagnosing and locating bugs far easier.

## Working features

*Register and login (Currently only allows username for login)
*Add/remove folder for passwords
*Random password generation
*Add/remove/view/edit website passwords
*Add/remove/view/edit database passwords
*Update a user's master password
*Change timescale before a password reminder is given

## Features to come

*Unit tests need written for utility classes and controller methods (Shame on me! Should have written those much sooner!) - dev benefit
*Code needs much better documentation (Again, shame on me!) - dev benefit
*Add/remove/view/edit credit/debit card entries
*Add/remove/view/edit passport entries
*Add/remove/view/edit documents and files
*Dynamically log in with either email or username
*Allow user to enter notes on their passwords/sensitive data
*Improvements to UI for ease of use, aesthetics + more user feedback
*Replace placeholder 1password logo with a logo of my own
*Billing system needs implemented (even if I never get this in a sellable state, it's still a cloud based app!)
*Vulnerability/password breach scans
*Implement geo-redundancy in the form of geography selection
