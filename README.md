# PolyVox

## Client

Make sure you have install **typings** and **underscore**
 
``` bash
sudo npm install -g typings
typings install dt~underscore --global --save
``` 

When you clone Polyvox, write this line in the client folder in a terminal

``` bash
npm install
```

To client, write this line in the client folder in a terminal

``` bash
npm start
```

When you pull the project, don't forget to do : 
``` bash
npm install
```

## Streaming Server

Polyvox streaming app is a standalone server. Go to server folder.

``` bash
make
./PolyvoxServer
```

Polyvox streaming server use 8082 and 8084 internet port.
Streaming now allow audio and speaker change every 30 sec.

Streaming speaker is currently an external app only for Linux. Run streamerApp.sh to stream.

``` bash
./streamerApp.sh <name> [ip addresse]
```
