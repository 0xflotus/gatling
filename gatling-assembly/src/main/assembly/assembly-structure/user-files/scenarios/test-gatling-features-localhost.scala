val iterations = 10
val pause1 = 1
val pause2 = 2
val pause3 = 3

val baseUrl = "http://localhost:3000"

val usersCredentials = new TSVFeeder("user_credential", List("login", "password"))
val usersInformation = new TSVFeeder("user_information", List("firstname", "lastname"))

include("lambda-user-test-gatling")
include("_admin-user-test-gatling")

val lambdaUserConfig = configureScenario(lambdaUser) withUsersNumber 5 withRampOf 10
val adminConfig = configureScenario(adminUser) withUsersNumber 5 withRampOf 10 startsAt 60

runSimulations(lambdaUserConfig, adminConfig)