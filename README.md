# Pi-Hole-Compose
Android companion app for Pi-hole server written with Jetpack Compose


<img src="https://raw.githubusercontent.com/Kennyc1012/Pi-Hole-Compose/master/screenshots/device-2021-03-14-081139.png" width="480"/>

# Building
To build the app, you will be required to provide a `PIHOLE_PASSWORD` in your `gradle.properties` in order for the project to build.
```groovy
PIHOLE_PASSWORD="YOUR PASSWORD"
```
This value can be obtained from the conf file on the Pi-hole server with the following command</br>
`> cat /etc/pihole/setupVars.conf | grep WEBPASSWORD`</br>
`> WEBPASSWORD=YOURHASHEDWEBPASSWORD`</br>
This step is not required, but any value needs to be set so the project can compile. If the incorrect password is set, the Enable and Disable functionality will not work. 
