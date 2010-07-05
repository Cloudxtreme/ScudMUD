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

/**
 * A room is just what you think it is.  It contains: players, items and NPCS.
 * It will eventually be able to serve as a command parser as well, to allow
 * for custom commands in rooms to be used as well as for directions as I want
 * non-standard directions to be easily usable.
 */
class Room extends Messageable {
  /** 
   * The list of players in the room.
   */
  var players = List[Player]()

  /**
   * Initially empty command map.
   */
  var commands = Map[String, Command]()  

  /**
   * Add the given command to the room's command map.
   * @param c the command to add
   */
  def addCommand(c: Command) { 
    commands += (c.getPrefix() -> c)
  }

  /**
   * Receive the following message and don't exclude anyone from getting it.
   * @param message the message to be received by this object
   */
  def sendMessage(message: String) {
    players.foreach { player =>
      player.sendMessage(message)
    } 
  }

  /**
   * Receive the following message, but exclude the messagable object m from
   * receiving it.
   * @param m       the messagable object to exclude from receiving the message
   * @param message the message for this object to receive
   */
  def sendMessage(m: Messageable, message: String) {
    players.foreach { player =>
      player.sendMessage(m, message)
    } 
  }
}
