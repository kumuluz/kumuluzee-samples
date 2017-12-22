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
package com.kumuluz.ee.samples.reactive.vertx;

import com.kumuluz.ee.reactive.common.annotations.ReactiveEventListener;
import io.vertx.core.eventbus.Message;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
*
* @author Žan Ožbot
*/
@ApplicationScoped
public class VertxEventListener {
	
	private static final Logger log = Logger.getLogger(VertxEventListener.class.getName());
	
	private List<String> messages = new ArrayList<>();
	
	@ReactiveEventListener(address = "tacos")
	public void onMessage(Message<Object> event) {
		if(event.body() != null) {
			messages.add((String) event.body());
			log.info("New message received: " + event.body());
		} else {
			log.warning("Error when receiving messages.");
		}
	}
	
	public List<String> getFiveLastMessages() {
		if(messages.size() <= 5) {
			return messages;
		}
		return messages.subList(messages.size() - 5, messages.size());
	}

}
