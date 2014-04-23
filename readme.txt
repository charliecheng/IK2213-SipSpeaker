Compile all the source files and the jlibrtp source files.
Note that we changed some source file in jlibrtp, so you need to compile with the jlibrtp package from our zip file instead of the one downloaded from the Internet.

The default sip user name is charlie.
The default sip port is 5060, which you need to change to other port if you want to test using local LinPhone, but SJphone is OK. The SJ phone will use port 1024 if you run our application first.
The default http server port is 8080/
The default interface of sip and http server is eth0.

USAGE: java SIPSpeaker [-c config_file_name] [-user sip_uri] [-http http_bind_address]