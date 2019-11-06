# keycloak-test-demo
Demo project to clarify testing bug

All test classes should pass testing but the two tests expecting a response of 401 - Unauthorized fail as the Keycloak security constraints seem to not get applied in test context.
