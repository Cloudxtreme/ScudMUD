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
 * For notifying the main thread that one more command is done executing.
 */
case class Done()

/**
 * An actor for handling the execution of commands.
 */
object CommandHandler extends Actor {
  /**
   * The global command map.
   */
  val commands = Map[String, Command]()
  
  /**
   * The actor to send done messages to.
   */
  var mainActor: Actor = _

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
   * Respond to messages sent to this actor.
   */
  def act() {
    loop {
      react {
        case ExecuteCommand(c: Command, args: String, p: Player) => 
          c.execute(args, p)
          mainActor ! Done()
      }
    }
  }
}
