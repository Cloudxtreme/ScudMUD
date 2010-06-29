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
import scala.actors._
import Actor._

/**
 * For when a command executor is done processing a command, but generated no 
 * deltas from the command's execution.
 */
case class Done

/**
 * For when a command executor is done processing a command and generated a
 * single delta from the command's execution.
 * @param d the delta generated
 */
case class DoneWithDelta(d: Delta) 

/**
 * For when a command executor is done processing a command and generated 
 * multiple deltas from the command's execution.
 * @param list the list of deltas generated
 */
case class DoneWithDeltas(list: List[Delta]) 

/**
 * Let a command executor know to execute the given command using the given
 * paramaters.
 * @param c    the command to execute
 * @param args the arguments to send to the command
 * @param p    the player that sent the command request
 * @param a    the actor to send the deltas and done message to.
 */
case class ExecuteCommand(c: Command, args: String, p: Player, a: Actor)

/**
 * For handling the parsing and execution of commands.  
 */
object CommandHandler {
  val commands = Map[String, Command]()
  var executors = Array[CommandExecutor]()
  private var executorNum = 0

  /**
   * Create a given number of command executors to use.
   * @param num the number of command executors to create
   */
  def initExecutors(num: Int) {
    executors = new Array[CommandExecutor](num)
    executorNum = 0
    for(i <- 0 until num) {
      val exec = new CommandExecutor
      exec.start
      executors(i) = exec  
    }
  }

  /**
   * Add a command to the command handler.
   * @param c the command to add
   */
  def addCommand(c: Command) {
    commands.put(c.toString(), c)
  }

  /**
   * Execute the given requests if it is possible to.
   */
  def execute(messages: List[(Player, String)], players: List[Player]) = {
    // The number of done messages that are needed to be considered finished.
    var donesNeeded = 0

    // The player list thing is temporary, in due time it will be replaced
    // and done in a much nicer fashion once I can get things working at a
    // basic level.
 
    messages.foreach { messageTuple =>
      val p = messageTuple._1
      val s = messageTuple._2.trim
     
      var split = s.split(" ", 2) 
      if(split.length != 2) {
        val newSplit = new Array[String](2)
        newSplit(0) = split(0)
        newSplit(1) = ""
        split = newSplit
      }

      var validCommand = true
      val command = commands.get(split(0)) getOrElse { 
        validCommand = false
        p.sendMessage("Wha?!\n")
        null
      }
   
      println(s)

      if(validCommand) {
        donesNeeded += 1
        executors(executorNum) ! ExecuteCommand(command, split(1), p, self)
        executorNum += 1
        if(executorNum == executors.size)
          executorNum = 0 
      }
    }

    var deltas = List[Delta]()
    while(donesNeeded!=0) {
      receive {
        case Done() => donesNeeded -= 1
        case DoneWithDelta(d: Delta) => donesNeeded -= 1
          deltas += d
        case DoneWithDeltas(ds: List[Delta]) => donesNeeded -= 1
          deltas ++= ds
      }
    }
    deltas
  }
}
