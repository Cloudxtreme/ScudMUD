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

import scala.actors._
import Actor._

/**
 * An actor that parses commands.
 */
object CommandParser extends Actor {
  /**
   * The actor to send done messages to.
   */
  var mainActor: Actor = _

  /**
   * Respond to messages sent to this actor.
   */
  def act() {
    loop {
      react {
        case (p: Player, str: String) => 
          // Give the user a new prompt.
          if(str=="\r") {
            p.sendMessage("> ")
            mainActor ! Done()
          } else {
            val trimmedStr = str.trim

            // Extract the command name(will always be the first token in the
            // request).
            var split = trimmedStr.split(" ", 2)
            if(split.length != 2) {
              val newSplit = new Array[String](2)
              newSplit(0) = split(0)
              newSplit(1) = ""
              split = newSplit
            }

            // Get the command for the given command name.  If the command
            // doesn't exist in the user's room then look in the global scope 
            // and if it doesn't exist there return an invalid command.
            val command = CommandHandler.getCommand(p, split(0))
            CommandHandler ! ExecuteCommand(command, split(1), p)
          }
      }
    }
  }
}
