# DAT076 Project

A web app project for the [DAT076 course](http://www.cse.chalmers.se/edu/course/DAT076/) at Chalmers University of Technology built with the Java [Play framework](http://www.playframework.com/).

For the Web Applications course we have created an application, “Invoicer”, for managing invoices and their clients. Users can add, edit, and remove invoices and clients, as well as connecting different bank accounts to an invoice. The user can get notified in the UI about paid and overdue invoices, as well as sending out invoices and reminders to the clients with e-mail.

A separate component based JSF project which acts as payment app for clients has been created, which communicates with the main application over the built-in REST API using JSON. Notifications about paid invoices are instantly appearing in real-time in the main application using Javascript and Server Sent Events (SSE). 

## JSF Client App

A separate JSF app is located at [invoicer_client](https://github.com/henke101/invoicer_client). Imaginary clients can fetch invoices from the main JSON API and choose to pay them from the client app. Notifications are instantly shown in the main app interface.

## Run

In the `invoicer` directory, run

	play run

and visit `http://localhost:9000`. No database setup is needed – an in memory H2 database is used.

## API

A JSON REST API is available at the following endpoints:

	# Invoices

	GET			/invoices						# Get all invoices
	POST		/invoices						# Create new invoice

	GET			/invoices/:id				# Get invoice by ID
	PUT			/invoices/:id				# Update invoice by ID
	DELETE	/invoices/:id				# Delete invoice by ID
	PUT			/invoices/:id/star	# Toggle star on invoice by ID

	GET			/invoices/starred		# Get all starred invoices

	POST		/invoices/:id/send	# Send the invoice specified by ID by email to its client
	POST		/invoices/:id/reminder	# Send a reminder of invoice ID by email to its client

	# Bank Accounts

	GET			/accounts					# Get all accounts
	POST		/accounts					# Create new account

	GET			/accounts/:id			# Get account by ID
	PUT			/accounts/:id			# Update account by ID

	# Clients

	GET			/clients					# Get all clients
	POST		/clients					# Create new client

	GET			/clients/:id			# Get client by ID
	PUT			/clients/:id			# Update client by ID
	DELETE	/clients/:id			# Delete client by ID

	POST		/clients/:id/send	# Send all client's (specified by ID) invoices to its email

	GET			/clients/:name/invoices	# Get all invoices by client name

	# Users

	GET		/me								# Get currently logged in user
	POST	/user							# Create new user

	# Paid invoices stream

	GET		/invoices/paid		# Listen for incoming paid invoices, data payload as JSON string

Some endpoints may need session authentication.

## Develop

Always use the `develop` branch as trunk, and use feature/bug branches as needed. `develop` is thus considered unstable and `master` is stable.

Sample workflow when starting a new coding session:

1. Do a `git pull` on `develop`.
2. `checkout` your dev branch and rebase with `develop`.
3. Code.

Before pushing to `develop`, do the following:

1. Do a `git pull` on `develop`.
2. `checkout` your dev branch and rebase with `develop`. Fix any merge conflicts *locally in your local dev branch* (not `develop`).
3. `git checkout develop` and merge with your dev branch.
4. Push to `develop`.

**Only push to `master` when having stable working builds** (they *should* pass all tests).

## Tests

### Play tests

Tests reside in the `tests` package. Run with:

	play tests

### Javascript tests

The JS tests are built with [Mocha](http://visionmedia.github.io/mocha/) and [Chai JS](http://chaijs.com/). Navigate to `http://localhost:9000/assets/javascripts/test/index.html` in your browser to boot up the test runner.

## Dependencies

- Java 7
- Play 2.2.0

A `build.sbt` file includes additional library dependencies, which will be automatically installed when building/running the application.

## Contributors

- Johan Brook
- Henrik Andersson
- Robin Andersson
- Andreas Rolén