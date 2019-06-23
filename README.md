  #Abstract
  Grassroots Reporting, GrR, is an Android app to report non-urgent problems that government services can fix: roads, schools, utilities, etc.  The aim breadth: to cover most government services across different cities and states.

  #Setup
1. Update "server_url_and_port" in res.values.strings.xml to point to your server.
   Run "python server.py" on your server with the right port.
1. Either install GrR on an Android device or start a Virtual Device with Android Studio.
1. Create a "Posted_Reports" folder in the directory in which server.py is running.
   Reports you submit from GrR will go into this folder.

  #Future Work
  Below are some to-do's to make this application work on a large scale:
* Create a load-balanced backend server to receive and process the HTTP POST'd reports.
* Reserve a DNS domain update "server_url_and_port" in res.values.strings.xml to use this domain.
  Point the domain at your backend server or load balancer IP address.
* Create a retry mechanism that re-submits a report if there are connection issues.
  Currently the app only tries to submit once.
* Boost security and protect against fake reports: e.g. check device type, multiple submissions from the same device, add captcha, use an encrypted connection (currently just cleartext HTTP), etc.
* Support multiple picture types (currently only tested with Bitmaps from a Galaxy Nexus camera.
  Maybe add support for videos.
* Forward reports to the right government officials.
* Make a website to publicize report statistics and responsible government officials.

  #Environment Used for Testing
* Windows 10, 64-bit OS
* Python 3.6.3
* Galaxy Nexus API 29 Virtual Device: Android 9.+, x86
