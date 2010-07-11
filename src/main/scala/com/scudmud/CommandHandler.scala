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

import scala.collection.mutable.Map

/**
 * Let a command executor know to execute the given command using the given
 * paramaters.
 * @param c    the command to execute
 * @param args the arguments to send to the command
 * @param p    the player that sent the command request
 */
case class ExecuteCommand(c: Command, args: String, p: Player)

/**
 * For handling the parsing and execution of commands.  
 */
object CommandHandler {
  val commands = Map[String, Command]()

  /**
   * Add a command to the command handler.
   * @param c the command to add
   */
  def addCommand(c: Command) {
    commands.put(c.getPrefix(), c)
  }

  /**
   * Get the given command for the given command name.
   * @param p   the player that sent this command request
   * @param str the command name to look for
   * @return    the command based on the given name
   */
  def getCommand(p: Player, str: String): Command = {
    p.room.commands.get(str) getOrElse {
      commands.get(str) getOrElse { InvalidCommand }
    }
  }

  /**
   * Execute the given requests if it is possible to.
   * @param message the messages to be processed
   */
  def execute(messages: List[(Player, String)]) {
    // Split up each message to extract the command name and have it executed
    // if it can be executed.
    messages.foreach { messageTuple =>
      val p = messageTuple._1
      var s = messageTuple._2

      // Give the user a new prompt.
      if(s=="\r") {
        p.sendMessage("> ")
      } else {
        s = s.trim 
    
        // Extract the command name as that will always be the first token in 
        // the request. 
        var split = s.split(" ", 2) 
        if(split.length != 2) {
          val newSplit = new Array[String](2)
          newSplit(0) = split(0)
          newSplit(1) = ""
          split = newSplit
        }

        // Get the command for the given command name.  If the command doesn't
        // exist in the user's room then look in the global scope and if it  
        // doesn't exist there return an invalid command.
        val command = getCommand(p, split(0))
  
        // TODO: Switch to some logging framework. 
        println(s)
        command.execute(split(1), p)
      }
    }
  }
}
