/*
 * Copyright (c) 2010, Preston Skupinski <skupinsk@cse.msu.edu>
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */ 
package com.scudmud

import java.nio.channels._

/**
 * A player is the game's representation of a user connected to the game.
 * @param s the user's selection key
 */
class Player(val s: SelectionKey) extends Messageable {
  /**
   * Have the player receive the given message.
   * @param message the message for the player to receive
   */
  def sendMessage(message: String) = NetworkManager.write(s, message)
  
  /**
   * Have the player receive the given message, but not if they are meant to be
   * excluded from receiving it.
   * @param m       the messagable object to exclude
   * @param message the message to be received
   */
  def sendMessage(m: Messageable, message: String) {
    if(this ne m) sendMessage(message)  
  }
}
