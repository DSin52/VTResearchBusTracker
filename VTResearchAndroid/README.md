VTResearchAndroid
=================

Test Android framework on Node Server


IP Address Instructions
=================
-Change the settings in your virtualbox so that the network adapter is a Bridged connection. (Look up on google if needed).
-In this way, Ubuntu will share the ip address of your computer (win7) and you can use the app without having to hook it up to ubuntu etc.
-Then write down the ip address your linux machine is using (connection information).
-Replace the ip addresses in the MainActivity file (getFromServer() and postToServer(...))
-Compile, send app to phone, and run Node.js server. Everything should work.
