/*
 *  Copyright (c) 2014-2017 Kumuluz and/or its affiliates
 *  and other contributors as indicated by the @author tags and
 *  the contributor list.
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://opensource.org/licenses/MIT
 *
 *  The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or
 *  implied, including but not limited to the warranties of merchantability,
 *  fitness for a particular purpose and noninfringement. in no event shall the
 *  authors or copyright holders be liable for any claim, damages or other
 *  liability, whether in an action of contract, tort or otherwise, arising from,
 *  out of or in connection with the software or the use or other dealings in the
 *  software. See the License for the specific language governing permissions and
 *  limitations under the License.
 */
var wsUri = "ws://" + document.location.hostname + ":" + document.location.port + document.location.pathname + "customer";
var websocket = new WebSocket(wsUri);

websocket.onopen = function(evt) { onOpen(evt) };
websocket.onmessage = function(evt) { onMessage(evt) };
websocket.onerror = function(evt) { onError(evt) };
var output = document.getElementById("output");

function sendMessage() {
    websocket.send(textField.value);
}

function onOpen() {
    writeToScreen("Connected to " + wsUri);
}

function onMessage(event) {
    console.log("onMessage: " + event.data);
    responseField.innerHTML += event.data + "\n";
}

function onError(event) {
    writeToScreen('<span style="color: red;">ERROR:</span> ' + event.data);
}

function writeToScreen(message) {
    output.innerHTML += message + "<br>";
}