  Grassroots Reporting, GrR, is an Android app to report non-urgent problems that government services can fix: roads, schools, utilities, etc.  The aim breadth: to cover most government services across different cities and states.

  Setup:
1. Update "server_url_and_port" in res.values.strings.xml to point to your server.
   Run "python server.py" on your server with the right port.
2. Either install GrR on an Android device or start a Virtual Device with Android Studio.
3. Create a "Posted_Reports" folder in the directory in which server.py is running.
   Reports you submit from GrR will go into this folder.

  Environment I used for testing:
* Windows 10, 64-bit OS
* Python 3.6.3
* Galaxy Nexus API 29 Virtual Device: Android 9.+, x86
